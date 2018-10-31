/**
 * 
 */
package com.homerfv.smtproo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.helper.SimpleMessageListener;


/**
 * @author homerfv
 *
 */
public class SimpleMessageListenerImpl implements SimpleMessageListener {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(SimpleMessageListenerImpl.class);

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");

	private final SimpleDateFormat dateFormatFile = new SimpleDateFormat("yyyyMMddhhmmssSSS");
	private final SimpleDateFormat dateFormatDir = new SimpleDateFormat("yyyyMMdd");
	
	private String dirPath = "";
	
	public String getDirPath() {
		return dirPath;
	}

	public void setDirPath(String dirPath) {
		this.dirPath = dirPath;
	}

	/**
	 * 
	 */
	public SimpleMessageListenerImpl() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.subethamail.smtp.helper.SimpleMessageListener#accept(java.lang.String, java.lang.String)
	 */
	@Override
	public boolean accept(String from, String recipient) {
		// TODO Auto-generated method stub
		LOGGER.info("accept email from "+from+" sent to "+recipient);
		return true;
	}

	/* (non-Javadoc)
	 * @see org.subethamail.smtp.helper.SimpleMessageListener#deliver(java.lang.String, java.lang.String, java.io.InputStream)
	 */
	@Override
	public void deliver(String from, String recipient, InputStream data) throws TooMuchDataException, IOException {
		// TODO Auto-generated method stub
		LOGGER.info("deliver email from "+from+" sent to "+recipient);
		this.saveEmailToFile(this.convertStreamToString(data));
		
	}
	
	/**
	 * 
	 * @param mailContent
	 * @return
	 */
	private String saveEmailToFile(String mailContent) {
		
		String yyyyMMdd = dateFormatDir.format(new Date());
		
		String dirPathDay = String.format("%s%s%s", this.getDirPath(), 
								File.separator,yyyyMMdd);
								
		File dir = new File(dirPathDay);
		if(!dir.exists()){
			if(!dir.mkdirs()) {
				LOGGER.error("Unable to create directory "+dirPathDay);
			}
		}else {
			LOGGER.info("Directory "+dirPathDay+" already exist..");
		}
		
		String filePath = String.format("%s%s%s", dirPathDay, 
											File.separator,
											dateFormatFile.format(new Date()));

		// Create file
		int i = 0;
		File file = null;
		while (file == null || file.exists()) {
			String iStr;
			if (i++ > 0) {
				iStr = Integer.toString(i);
			} else {
				iStr = "";
			}
			file = new File(filePath + iStr + ".eml");
		}

		// Copy String to file
		try {
			FileUtils.writeStringToFile(file, mailContent);
			LOGGER.info("Mail successfully dumped to "+file.getAbsolutePath());
		} catch (IOException e) {
			// If we can't save file, we display the error in the SMTP logs
			
			LOGGER.error("Error: Can't save email: {}", e.getMessage());
		}
		return file.getAbsolutePath();
	}
	
	/**
	 * 
	 * @param is
	 * @return
	 */
	private String convertStreamToString(InputStream is) {
		final long lineNbToStartCopy = 4; // Do not copy the first 4 lines (received part)
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		StringBuilder sb = new StringBuilder();

		String line;
		long lineNb = 0;
		try {
			while ((line = reader.readLine()) != null) {
				if (++lineNb > lineNbToStartCopy) {
					sb.append(line).append(LINE_SEPARATOR);
				}
			}
		} catch (IOException e) {
			LOGGER.error("", e);
		}
		return sb.toString();
	}

}
