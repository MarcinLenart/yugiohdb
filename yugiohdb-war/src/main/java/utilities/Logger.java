package utilities;

import java.io.BufferedWriter;
//import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Marcin
 */
public class Logger {

    //"/Users/marcin/data"
    private static String path = "logs",
            err = "err", warn = "warn", log = "log";

    public enum TYPE {

        ERR, WARN, LOG
    };

    public static String getErr() {
        return err;
    }

    public static void setErr(String err) {
        Logger.err = err;
    }

    public static String getLog() {
        return log;
    }

    public static void setLog(String log) {
        Logger.log = log;
    }

    public static String getPath() {
        return path;
    }

    public static void setPath(String path) {
        Logger.path = path;
    }

    public static String getWarn() {
        return warn;
    }

    public static void setWarn(String warn) {
        Logger.warn = warn;
    }

    public Logger() {
    }

    private static String getTime(){
        String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
        return sdf.format(cal.getTime());
    }
    
    public static void log(TYPE type, String message) {
        /*
        FileWriter file = null;
        try {
            switch (type) {
                case ERR:
                    file = new FileWriter(path + "/" + err, true);
                    break;
                case WARN:
                    file = new FileWriter(path + "/" + warn, true);
                    break;
                case LOG:
                    file = new FileWriter(path + "/" + log, true);
                    break;
            }
            BufferedWriter out = new BufferedWriter(file);
            out.write(getTime() + ": " + message);
            out.newLine();
            out.close();
        } catch (IOException e) {
        }*/
    }
}
