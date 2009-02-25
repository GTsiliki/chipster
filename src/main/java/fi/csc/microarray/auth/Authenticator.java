package fi.csc.microarray.auth;

import javax.jms.Destination;
import javax.jms.JMSException;

import org.apache.log4j.Logger;

import fi.csc.microarray.ApplicationConstants;
import fi.csc.microarray.config.DirectoryLayout;
import fi.csc.microarray.messaging.MessagingEndpoint;
import fi.csc.microarray.messaging.MessagingListener;
import fi.csc.microarray.messaging.MessagingTopic;
import fi.csc.microarray.messaging.NodeBase;
import fi.csc.microarray.messaging.Topics;
import fi.csc.microarray.messaging.MessagingTopic.AccessMode;
import fi.csc.microarray.messaging.message.AuthenticationMessage;
import fi.csc.microarray.messaging.message.CommandMessage;
import fi.csc.microarray.messaging.message.NamiMessage;
import fi.csc.microarray.messaging.message.AuthenticationMessage.AuthenticationOperation;
import fi.csc.microarray.security.SecureSessionPool;
import fi.csc.microarray.security.SecureSessionPool.Session;
import fi.csc.microarray.util.MemUtil;

/**
 * @author Aleksi Kallio
 *
 */
public class Authenticator extends NodeBase {
    
	/**
	 * Logger for this class
	 */
	private static Logger logger = null;

	/**
	 * Logger for security issues
	 */
	private static Logger securityLogger = null;

	/**
	 *  Logger for message logging
	 */
	private static Logger messageLogger = null;
	
	private SecureSessionPool sessionPool = new SecureSessionPool();
	private MessagingEndpoint endpoint;
	private MessagingTopic authorisedTopic;
	private MessagingTopic authorisedUrlTopic;
	private MessagingTopic testTopic;

	private TestListener testListener;
	private AuthenticationProvider authenticationProvider; 
	
	public Authenticator() throws Exception {
		
		// initialise dir and logging
		DirectoryLayout.initialiseServerLayout();
		logger = Logger.getLogger(Authenticator.class);
		securityLogger = Logger.getLogger("security.frontend");
		messageLogger = Logger.getLogger("messages.frontend");
		
		// initialise communications
		this.endpoint = new MessagingEndpoint(this);

		// create authorised topics
		authorisedTopic = endpoint.createTopic(Topics.Name.AUTHORISED_REQUEST_TOPIC, AccessMode.WRITE);       
		authorisedUrlTopic = endpoint.createTopic(Topics.Name.AUTHORISED_URL_TOPIC, AccessMode.WRITE);       

		// create non-authorised topics
		RequestListener jobListener = new RequestListener(authorisedTopic);
		MessagingTopic requestTopic = endpoint.createTopic(Topics.Name.REQUEST_TOPIC, AccessMode.READ);		
		requestTopic.setListener(jobListener);		
		RequestListener urlListener = new RequestListener(authorisedUrlTopic);
		MessagingTopic urlTopic = endpoint.createTopic(Topics.Name.URL_TOPIC, AccessMode.READ);		
		urlTopic.setListener(urlListener);
		
		// create test-topic
		testTopic = endpoint.createTopic(Topics.Name.TEST_TOPIC, AccessMode.READ_WRITE);		
		this.testListener = new TestListener();
		testTopic.setListener(testListener);
		
		// initialise JAAS authentication
		authenticationProvider = new JaasAuthenticationProvider();
		
		logger.info("authenticator is up and running [" + ApplicationConstants.NAMI_VERSION + "]");
		logger.info("[mem: " + MemUtil.getMemInfo() + "]");
	}
	
	private class RequestListener implements MessagingListener {

		private static final String KEY_PENDING_MESSAGE = "pending-message";
		private static final String KEY_USERNAME = "username";
		
		private MessagingTopic routeTo;

		public RequestListener(MessagingTopic routeTo) {
			this.routeTo = routeTo;
		}

		/**
		 * Two step processing: authenticate, then route.
		 */
		public void onNamiMessage(NamiMessage msg) {
			try {

				logger.debug("starting to process " + msg);
				
				// variables
				NamiMessage messageToBeRouted = null;
				Session session = null;

				//
				// 1. authenticate
				//
				
				// 1.1. try to load existing session
				if (msg.getSessionID() != null) {
					String id = msg.getSessionID();
					if (sessionPool.getSession(id) != null) {
						session = sessionPool.getSession(id);
						logger.debug("message " + msg.getMessageID() + " had a proper session " + session.getID());
					}					
				}

				// 1.2. try to authenticate
				if (session == null) {
					logger.debug("message " + msg + " has no session, requires authentication");
					// 1.2.a. no existing session (possibly due to expiration)

					// create session and request authentication
					session = sessionPool.createSession();
					logger.debug("created new session, pool size is now " + sessionPool.size());
					session.putParameter(KEY_PENDING_MESSAGE, msg);
					
					requestAuthentication(msg, session.getID().toString());
					
				} else {					
					// 1.2.b. use existing session 
					
					if (msg instanceof AuthenticationMessage) {
						// 1.2.b.a. message is authentication message (login/logout)

						AuthenticationMessage authMsg = (AuthenticationMessage)msg;
						if (authMsg.isLogin()) {
							messageLogger.debug("message " + authMsg.getMessageID() + " is a login");

							// authenticate with username/password
							if (authenticationProvider.authenticate(authMsg.getUsername(), authMsg.getPassword().toCharArray())) {
								ackLogin(authMsg, session.getID().toString(), true);
								session.putParameter(KEY_USERNAME, authMsg.getUsername());
								authMsg.setSessionID(session.getID().toString());
								securityLogger.info("authenticated user " + authMsg.getUsername() + " (auth. message JMS id was " + authMsg.getJmsMessageID() + ")");
								
								if (session.getParameter(KEY_PENDING_MESSAGE) != null) {
									// route pending message
									messageToBeRouted = (NamiMessage)session.getParameter(KEY_PENDING_MESSAGE);
								}
							} else {
								securityLogger.info("illegal username/password (user " + authMsg.getUsername()  + ", auth. message JMS id was " + authMsg.getJmsMessageID() + ")");
								ackLogin(authMsg, session.getID().toString(), false);
								return;
							}
							
						} else if (authMsg.isLogout()) {
							logger.debug("message " + msg.getMessageID() + " is a logout");
							sessionPool.removeSession(session);
							return;
							
						} else {
							throw new IllegalArgumentException("unknown authentication message: " + authMsg);
						}
						
					 	
					} else {
						// 1.2.b.b. message is a regular message with a proper session
						messageToBeRouted = msg;						
					}
					
					// session succesfully used, so update timestamp
					session.touch();
				}
				
				//
				// 2. route
				//

				if (messageToBeRouted != null) {
					securityLogger.info("message " + messageToBeRouted.getMessageID() + " was allowed");
					messageLogger.info(messageToBeRouted);
					
					messageToBeRouted.setUsername((String)session.getParameter(KEY_USERNAME));
					routeTo.sendMessage(messageToBeRouted);
					securityLogger.info("routing message " + messageToBeRouted.getMessageID() + " to authorised topic");
				}
				
			} catch (JMSException e) {
				logger.error(e);
			} catch (AuthorisationException e) {
				securityLogger.info("authorisation failed: " +  e.getMessage());
				logger.info(e);
			} 
		}

		
		private void ackLogin(NamiMessage loginMessage, String sessionID, boolean succeeded) throws JMSException, AuthorisationException {
			AuthenticationOperation operation = succeeded ? AuthenticationMessage.AuthenticationOperation.LOGIN_SUCCEEDED : AuthenticationMessage.AuthenticationOperation.LOGIN_FAILED; 
			AuthenticationMessage request = new AuthenticationMessage(operation);
			request.setSessionID(sessionID);
			request.setReplyTo(loginMessage.getReplyTo());
			endpoint.replyToMessage(loginMessage, request, Topics.MultiplexName.AUTHORISE_TO.toString());
		}
		
		private void requestAuthentication(NamiMessage msg, String sessionID) throws JMSException, AuthorisationException {
			AuthenticationMessage request = new AuthenticationMessage(AuthenticationMessage.AuthenticationOperation.REQUEST);
			request.setSessionID(sessionID);
			request.setReplyTo(msg.getReplyTo());
			logger.debug("requesting authentication for " + msg.getMessageID() + " under the session " + sessionID);
			endpoint.replyToMessage(msg, request, Topics.MultiplexName.AUTHORISE_TO.toString());
		}
	}
		
	private class TestListener implements MessagingListener {
		public void onNamiMessage(NamiMessage msg) {
			logger.debug("got message on test-topic.");
			try {

				Destination dest = msg.getReplyTo();
				if (dest != null) {
					CommandMessage replyMessage = new CommandMessage("OK");
					endpoint.replyToMessage(msg, replyMessage);
				} else {
					logger.debug("ReplyTo is null, so no reply was sent.");
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}

	public String getName() {
		return "authenticator";
	}
}
