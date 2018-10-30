/**
 * 
 */
package com.homerfv.smtproo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.mail.Header;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.homerfv.smtproo.SMTPServerService;

/**
 * @author homerfv
 *
 */
@Controller
public class InboxController {
		
	private static final Logger LOGGER = LoggerFactory
			.getLogger(InboxController.class);
	
	@Autowired
	private Environment env;
	
	private String dirPath = "";

	/**
	 * 
	 */
	public InboxController() {
		// TODO Auto-generated constructor stub
		
	}
	
	@GetMapping("/")
    public String index() {
        return "inbox";
	}
	
	@GetMapping("/inbox")
    public String inbox() {
        return "inbox";
	}
	
	@GetMapping("/inbox/get/mails")
	@ResponseBody
    public List<Email> mails(Model model, @RequestParam(value="dir") String dir) {
        model.addAttribute("dir", dir);
        
        List<Email> files = new ArrayList<Email>();
		try {
			
			LOGGER.info("this.mailDirPath: "+this.dirPath);
			if(StringUtils.isEmpty(this.dirPath)) {
				throw new Exception("mail dir path is empty");
			}
			File mailDir = new File(this.dirPath+"/"+dir);
			File[] listOfFolder = mailDir.listFiles();
			
			Arrays.sort(listOfFolder, new Comparator<File>() {
				public int compare(File f1, File f2) {
					return Long.compare(f2.lastModified(),f1.lastModified());
				}
			});
			LOGGER.info("found "+listOfFolder.length+" file items");
			for (File file : listOfFolder) {
				LOGGER.info("path: "+file.getPath());
				if(file.isFile()) {
					Email mail = this.mapToEmail(this.parseMailFile(file.getPath()));
					if(mail != null) {
						mail.setFile(file.getName());
						files.add(mail);
					}else {
						LOGGER.info("unable to parse mail "+file.getAbsolutePath());
					}
					
				}
			}
			
		}catch(Exception e) {
			LOGGER.error("",e.getMessage());
		}finally {
			
		}
		
		return files;
    }
	
	
	@GetMapping("/inbox/get/mail")
    public ResponseEntity<Resource> mail(
    		@RequestParam(name="dir", required=false, defaultValue="") String dir,
    		@RequestParam(name="file", required=false, defaultValue="") String file,
    		HttpServletRequest request) {
		
		// Load file as Resource
		Resource resource = new FileSystemResource(this.dirPath+"/"+dir+"/"+file);
        
        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(
            			resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            LOGGER.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + 
                				resource.getFilename() + "\"")
                .body(resource);
    }
	
	/**
	 * 
	 * @param map
	 * @return
	 */
	private Email mapToEmail(Map<String,String>  map) {
		
		if(map == null) {
			return null;
		}
		
		Email email = new Email();
		if(!StringUtils.isEmpty(map.get("Date"))){
			email.setDate(map.get("Date"));
		}
		if(!StringUtils.isEmpty(map.get("From"))){
			email.setFrom(map.get("From"));
		}
		if(!StringUtils.isEmpty(map.get("To"))){
			email.setTo(map.get("To"));
		}
		if(!StringUtils.isEmpty(map.get("Subject"))){
			email.setSubject(map.get("Subject"));
		}
		return email;
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	private Map<String,String> parseMailFile(String path){
		
		File mail = new File(path);
		try {
			LOGGER.info("parsing mail "+path);
			return this.parseMailContent(FileUtils.readFileToString(mail));
		} catch (IOException e) {
			LOGGER.info("",e.getMessage());
		}
		return null;
		
	}
	
	/**
	 * 
	 * @param content
	 * @return
	 */
	private Map<String,String> parseMailContent(String content){
		
		Map<String,String> out = new HashMap<String,String>();
		try {
		
			Session s = Session.getInstance(new Properties());
			InputStream is = new ByteArrayInputStream(content.getBytes());
			MimeMessage message = new MimeMessage(s, is);
			message.getAllHeaderLines();
			for (Enumeration<Header> e = message.getAllHeaders(); e.hasMoreElements();) {
			    Header h = e.nextElement();
			    LOGGER.info(h.getName()+" -> "+h.getValue());
			    out.put(h.getName(), h.getValue());
			}
			
		}catch(Exception e) {
			LOGGER.error("",e.getMessage());
			out = null;
		}
		return out;
	}
	
	@PostConstruct
	private void init() {
		LOGGER.info(this.getClass()+" initializing....");
		this.dirPath = System.getProperty(SMTPServerService.MAILDIR);
		if(StringUtils.isEmpty(this.dirPath)) {
			this.dirPath = env.getProperty(SMTPServerService.MAILDIR);
		}
		LOGGER.info("this.dirPath: "+this.dirPath);
	}

	private class Email{
		
		private String date;
		private String from;
		private String to;
		private String subject;
		private String file;
		
		public Email() {}
		
		public Email(String date, String from, String to, String subject, String file) {
			this.date = date;
			this.from = from;
			this.to = to;
			this.subject = subject;
			this.file = file;
		}

		public String getDate() {
			return date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getFrom() {
			return from;
		}

		public void setFrom(String from) {
			this.from = from;
		}

		public String getTo() {
			return to;
		}

		public void setTo(String to) {
			this.to = to;
		}

		public String getSubject() {
			return subject;
		}

		public void setSubject(String subject) {
			this.subject = subject;
		}

		public String getFile() {
			return file;
		}

		public void setFile(String file) {
			this.file = file;
		}
			
		
	}

}