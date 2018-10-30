# SMTProo

SMTProo is a stand alone SMTP server for development testing with a very simple web interface.
Written purely in Java using SpringBoot 2.1.0, jQuery and Bootstrap framework.

  <img src="https://raw.githubusercontent.com/homerfv/smtproo/master/src/main/resources/static/images/web.png" />

SMTP server implementation is based on <a href="https://github.com/voodoodyne/subethasmtp">https://github.com/voodoodyne/subethasmtp</a>


Requirements
------------

Java 8


Usage
------------

You can run the SMTP server using the following command line. 

```
java -jar smtproo-VERSION-SNAPSHOT.jar
```

By default the SMTP server will listen on port 2525 and the web interface will listent on port 8080.

To view/download received emails, open up browser and then hit http://localhost:8080. 
By default you will be redirected to http://localhost:8080?dir=YYYYMMDD which dir value corresponds to current date.
Alternatively, you may change the value of dir paramter to view emails sent on other dates.

If downloaded email is not rendering properly, you may need to override default JVM line.separator. 

```
java -Dline.separator=$'\r\n' -jar smtproo-VERSION-SNAPSHOT.jar
```

If you wish to change the default setting, you may override the default values via command line arguments.

```
java -Dsmtpserver.host=127.0.0.1 -Dsmtpserver.port=2526 -Dsmtpserver.maildir=received -jar \
  -Dserver.port=8090 smtproo-VERSION-SNAPSHOT.jar	
```


How to build
------------

Use maven to compile and build

Execute the following maven command to build the jar file

```  
mvn -clean install
```

After successful compilation, look for target/smtproo-VERSION-SNAPSHOT.jar


Donate
-----------
If you find SMTProo useful, please consider supporting by making a donation. Any amount will greatly appreciated.

<a href="https://www.paypal.me/homerfv" rel="nofollow" target="_blank">
<img alt="Donate" src="https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif" style="max-width:100%;">
</a> 


  
  
