import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyLogger {
    private String logFileName;

    public MyLogger() {
        // Create a log file name based on the current date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy_HHmmss");
        String dateTime = dateFormat.format(new Date());
        this.logFileName = dateTime + ".log";
    }

    public void log(String message) {
        String formattedMessage = getFormattedLogMessage(message);
        writeLogToFile(formattedMessage);
    }

    private String getFormattedLogMessage(String message) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = dateFormat.format(new Date());
        return "[" + dateTime + "] " + message;
    }

    private void writeLogToFile(String logMessage) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFileName, true))) {
            writer.write(logMessage);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void logSystemOut(String message) {
        System.out.println(message);
        log(message); // Append to log file as well
    }
}
