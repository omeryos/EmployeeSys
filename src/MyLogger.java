import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MyLogger {
    private String logFileName;
    private BlockingQueue<String> logQueue;
    private Thread loggingThread;
    private boolean shouldRun;

    public MyLogger() {
        // Create a log file name based on the current date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy_HHmmss");
        String dateTime = dateFormat.format(new Date());
        this.logFileName = dateTime + ".log";

        this.logQueue = new LinkedBlockingQueue<>();
        this.shouldRun = true;

        this.loggingThread = new Thread(this::processLogQueue);
        this.loggingThread.start();
    }

    public void log(String message) {
        String formattedMessage = getFormattedLogMessage(message);
        logQueue.add(formattedMessage);
    }

    private String getFormattedLogMessage(String message) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = dateFormat.format(new Date());
        return "[" + dateTime + "] " + message;
    }

    private void processLogQueue() {
        //The BufferedWriter is created outside the loop within the processLogQueue method,
        // and the loop continues to write log messages using the same writer instance.
        // This means that the writer remains open as long as the logging thread is processing log messages.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFileName, true))) {
            while (shouldRun) {
                String logMessage = logQueue.take(); // Blocks until an entry is available
                writer.write(logMessage);
                writer.newLine();
                writer.flush(); // Ensure that the data is written immediately
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stopLogging() {
        shouldRun = false;
        loggingThread.interrupt();
    }
}
