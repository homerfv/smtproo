/**
 * 
 */
package com.homerfv.smtproo;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;

import com.homerfv.smtproo.SimpleMessageListenerImpl;

/**
 * @author homerfv
 *
 */
@Service
public class SMTPServerService{
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SMTPServerService.class);
	
	public static final String MAILDIR = "smtpserver.maildir";
	public static final String HOST = "smtpserver.host";
	public static final String PORT = "smtpserver.port";
	
	@Value("${smtpserver.host}")
	String host="";
	
	@Value("${smtpserver.port}")
	String port="";
	
	@Value("${smtpserver.maildir}")
	String maildir="";
	
	SMTPServer smtpServer;
	
	@Autowired
	private Environment env;

	/**
	 * 
	 */
	public SMTPServerService() {
		// TODO Auto-generated constructor stub
	}
	
	@PostConstruct
	public void start() {
			
		this.maildir = System.getProperty(MAILDIR);
		if(StringUtils.isEmpty(this.maildir)) {
			this.maildir = env.getProperty(MAILDIR);
		}
		LOGGER.info("this.maildir: "+this.maildir);
		
		this.host = System.getProperty(HOST);
		if(StringUtils.isEmpty(this.host)) {
			this.host = env.getProperty(HOST);
		}
		LOGGER.info("this.host: "+this.host);
		
		this.port = System.getProperty(PORT);
		if(StringUtils.isEmpty(this.port)) {
			this.port = env.getProperty(PORT);
		}
		LOGGER.info("this.port: "+this.port);
		
		
		SimpleMessageListenerImpl l = new SimpleMessageListenerImpl();
		l.setDirPath(this.maildir);
		smtpServer = new SMTPServer(new SimpleMessageListenerAdapter(l));
		smtpServer.setHostName(this.host);
		smtpServer.setPort(Integer.valueOf(this.port));
		smtpServer.start();
		
		LOGGER.info("****** SMTP Server is running for domain "+smtpServer.getHostName()+" on port "+smtpServer.getPort());
	
	}

	@PreDestroy
	public void stop() {

		if(this.isRunning()) {
			LOGGER.info("****** Stopping SMTP Server for domain "+smtpServer.getHostName()+" on port "+smtpServer.getPort());
			smtpServer.stop();
		}
	}

	/**
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return smtpServer.isRunning();
	}

	/**
	 * 
	 * @return
	 */
	public String getMaildir() {
		return maildir;
	}
	
	
	

}
