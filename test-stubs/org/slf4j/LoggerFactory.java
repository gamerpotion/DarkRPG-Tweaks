package org.slf4j;
public final class LoggerFactory {
 public static final class CaptureLogger implements Logger {
  public String level, message;
  public void info(String s){level="INFO";message=s;System.out.println("[INFO] " + s);}
  public void warn(String s){level="WARN";message=s;System.out.println("[WARN] " + s);}
  public void error(String s){level="ERROR";message=s;System.out.println("[ERROR] " + s);}
 }
 public static final CaptureLogger CAPTURE=new CaptureLogger();
 public static Logger getLogger(String name) {return CAPTURE;}
}
