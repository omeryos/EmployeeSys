import java.io.IOException;
import java.util.logging.*;

public class ServerLogger {

    private static final Logger logger = Logger.getLogger(ServerLogger.class.getName());
    private static FileHandler fileHandler;
    private static final Level logLevel = Level.INFO;

    public static void setup() {
        try {
            String logFileName = String.format("server_log_%s.log", java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")));

            // Initialize the FileHandler with the log file name
            fileHandler = new FileHandler(logFileName, true);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL); // Set the logging level to ALL for now
            fileHandler.setFilter(null); // No filter
            fileHandler.setErrorManager(new ErrorManager() { // Print any errors
                @Override
                public void error(String msg, Exception ex, int code) {
                    System.err.println("Logging error: " + msg);
                    if (ex != null) {
                        ex.printStackTrace();
                    }
                }
            });

            // Add the handler to our logger
            logger.addHandler(fileHandler);
            logger.setLevel(logLevel);

            // Remove default console handler
            for (Handler defaultHandler : logger.getParent().getHandlers()) {
                logger.getParent().removeHandler(defaultHandler);

            }

            // Add a new console handler
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(consoleHandler);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Logger getLogger() {
        return logger;
    }
}
