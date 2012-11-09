package fi.csc.microarray.manager.web.hbncontainer;

import java.util.Date;
import java.util.Random;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;
import org.hibernate.classic.Session;
import org.hibernate.dialect.H2Dialect;

import fi.csc.microarray.config.Configuration;
import fi.csc.microarray.config.DirectoryLayout;
import fi.csc.microarray.manager.web.data.JobLogEntry;
import fi.csc.microarray.manager.web.util.RandomUtil;

public class JobLogHibernateUtil {

    private static final SessionFactory sessionFactory;
    //private static Type defaultType;

    static {
    	AnnotationConfiguration cnf = new AnnotationConfiguration();

    	Configuration configuration = DirectoryLayout.getInstance().getConfiguration();

    	String dbDriver;
    	String dbUrl;
    	String dbUsername;
    	String dbPassword;

    	try {
    		dbDriver = configuration.getString("manager", "jdbc-driver");
    		dbUrl = configuration.getString("manager", "database-url");
    		dbUsername = configuration.getString("manager", "database-username");
    		dbPassword = configuration.getString("manager", "database-password");

    	} catch (Exception e) {

    		// Not a real server

    		//CREATE TABLE IF NOT EXISTS jobs (id VARCHAR(100) PRIMARY KEY, operation VARCHAR(200), status VARCHAR(200), starttime DATETIME DEFAULT NULL, endtime DATETIME DEFAULT NULL, 	wallclockTime INT DEFAULT NULL, errorMessage TEXT DEFAULT NULL, outputText TEXT DEFAULT NULL, username VARCHAR(200), compHost VARCHAR(500));
    		//insert into jobs values ('8f99df7a-5c38-439f-8f7b-15f8bed88eda', 'importseq.sadl', 'COMPLETED', '2010-07-08 13:40:39.0', '2010-07-08 13:40:39.0', 0, null, null, 'demo', 'hippu2.csc.fi');

    		dbDriver = "org.h2.Driver";
    		dbUrl = "jdbc:h2:tcp://localhost/test";
    		dbUsername = "sa";
    		dbPassword = "";

    		System.err.println("Chipster configuration unavailable, " +
    				"trying to use development database at " + dbUrl);
    	}

    	try {
    		cnf.setProperty(Environment.DRIVER, dbDriver);
    		cnf.setProperty(Environment.URL, dbUrl);
    		cnf.setProperty(Environment.USER, dbUsername);
    		cnf.setProperty(Environment.PASS, dbPassword);
    		cnf.setProperty(Environment.DIALECT, H2Dialect.class.getName());          
    		cnf.setProperty(Environment.SHOW_SQL, "true");
    		cnf.setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");

    		cnf.addAnnotatedClass(JobLogEntry.class);

    		sessionFactory = cnf.buildSessionFactory();

    	} catch (Throwable ex) {
    		// Make sure you log the exception, as it might be swallowed
    		System.err.println("Initial SessionFactory creation failed." + ex);
    		throw new ExceptionInInitializerError(ex);
    	}
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    


    public static void insertExampleData(int count) {
        Session sess = getSessionFactory().getCurrentSession();
        sess.beginTransaction();

    	String[] states = new String[] { "COMPLETED", "FAILED" };
    	Date startTime;
    	Date endTime;
    	int wallclockTime;
//    	String errorMessage;
//    	String outputText;
    	
        Random rnd = new Random();

        JobLogEntry job;

        for (int i = 0; i < count; i++) {
            job = new JobLogEntry();
            
            job.setId("8f99dsdf7a-5c38-439f-8f7b-15f8bed" + Math.abs(rnd.nextLong()));
            job.setOperation(RandomUtil.getRandomOperation(rnd));
            
            Date start = RandomUtil.getRandomDate(rnd, 2009);
            job.setStartTime(start);
            int duration = rnd.nextInt(rnd.nextInt(600) + 1);
            Date end = (Date) start.clone();
            end.setMinutes(start.getMinutes() + duration);
            job.setEndTime(end);
            job.setWallclockTime(duration);
            
            if (rnd.nextDouble() < 0.02) {
            	job.setStatus(states[1]);
            	job.setErrorMessage(RandomUtil.getRandomError(rnd));
            } else {
            	job.setStatus(states[0]);
            }
            
            job.setOutputText(RandomUtil.getRandomOutputtext(rnd));
            
            job.setUsername(RandomUtil.getRandomUserName(rnd));
            job.setCompHost(RandomUtil.getRandomComp(rnd));

            sess.save(job);
        }
    }
}
