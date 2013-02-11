package fi.csc.microarray.client.session;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.zip.ZipException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.schlichtherle.truezip.zip.ZipFile;
import fi.csc.microarray.databeans.DataManager;
import fi.csc.microarray.exception.MicroarrayException;
import fi.csc.microarray.util.IOUtils;
import fi.csc.microarray.util.XmlUtil;

public class SessionLoader {
	
	private DataManager dataManager;
	private File sessionFile;
	private URL sessionURL;
	private boolean isDatalessSession;

	public SessionLoader(File sessionFile, boolean isDatalessSession, DataManager dataManager) throws MicroarrayException {
		this.sessionFile = sessionFile;
		this.sessionURL = null;
		this.dataManager = dataManager; 
		this.isDatalessSession = isDatalessSession;
	}

	public SessionLoader(URL sessionURL, DataManager dataManager) throws MicroarrayException {
		this.sessionFile = null;
		this.sessionURL = sessionURL;
		this.dataManager = dataManager; 
		this.isDatalessSession = true;
	}

	
	public void loadSession() throws ZipException, IOException, JAXBException, SAXException, ParserConfigurationException {
		
		ZipFile zipFile = null;
		InputStreamReader metadataReader = null;
		String version = Integer.toString(UserSession.SESSION_VERSION);
		try {
			// get the session.xml zip entry (only if a file, remote sessions are always latest version)
			if (sessionFile != null) {
				zipFile = new ZipFile(sessionFile);
				metadataReader = new InputStreamReader(zipFile.getInputStream(zipFile.getEntry(UserSession.SESSION_DATA_FILENAME)));
				Document doc = XmlUtil.parseReader(metadataReader);
				version = doc.getDocumentElement().getAttribute("format-version");
			}

		} finally {
			IOUtils.closeIfPossible(metadataReader);
			IOUtils.closeIfPossible(zipFile);
		}
		
		if (Integer.toString(UserSession.PREVIOUS_SESSION_VERSION).equals(version)) {
			// old format, use old loader
			SessionLoaderImpl1 impl = new SessionLoaderImpl1(sessionFile, dataManager, isDatalessSession);
			impl.loadSession();
			
		} else {
			// use new loader
			SessionLoaderImpl2 impl;
			if (sessionFile != null) {
				impl = new SessionLoaderImpl2(sessionFile, dataManager, isDatalessSession);
			} else {
				impl = new SessionLoaderImpl2(sessionURL, dataManager, isDatalessSession);
			}
			impl.loadSession();
		}
	}

}
