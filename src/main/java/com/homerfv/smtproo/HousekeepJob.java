/**
 * 
 */
package com.homerfv.smtproo;

import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author homerfv
 *
 */
public class HousekeepJob extends QuartzJobBean {
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(HousekeepJob.class);

	@Autowired
	SMTPServerService smtpService;
	
	@Value("${housekeep.retention}")
	String retention = "";
	
	/**
	 * 
	 */
	public HousekeepJob() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.springframework.scheduling.quartz.QuartzJobBean#executeInternal(org.quartz.JobExecutionContext)
	 */
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		
		String dirPath = smtpService.getMaildir();
		LOGGER.info("dirPath: "+dirPath);
		try {
			
			
			if(StringUtils.isEmpty(dirPath)) {
				throw new Exception("mail dir path is empty");
			}
			File mailDir = new File(dirPath);
			List<File> files = (List<File>) FileUtils.listFilesAndDirs(mailDir, 
						TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
			
			File[] listOfFolder = new File[files.size()];
			listOfFolder = files.toArray(listOfFolder);
			Arrays.sort(listOfFolder, new Comparator<File>() {
				public int compare(File f1, File f2) {
					return Long.compare(f2.lastModified(),f1.lastModified());
				}
			});
			
			LOGGER.info("days retention from config: "+retention);
			int days = Integer.parseInt(StringUtils.trim(retention)) * -1;
			
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_MONTH, days);
			Date daysAgo = calendar.getTime();
			LOGGER.info("Deleting files older than "+daysAgo);
			
			LOGGER.info("found "+listOfFolder.length+" file items");
			for (File file : listOfFolder) {
				if(file.lastModified() < daysAgo.getTime()) {
					LOGGER.info("Deleting "+file.getPath()+" => "+new Date(file.lastModified()));
					FileUtils.forceDelete(file);
				}
				if(file.isDirectory() && file.list().length == 0){
					LOGGER.info("Deleteing empty directory "+file.getPath());
					FileUtils.forceDelete(file);
				}
			}
			
		}catch(Exception e) {
			LOGGER.error("",e.getMessage());
		}
		

	}

}
