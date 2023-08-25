import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingPrintStream extends PrintStream {

    private final Logger logger;
    private final PrintStream original;

    public LoggingPrintStream(PrintStream original, Logger logger) {
        super(original);
        this.original = original;
        this.logger = logger;
    }

    @Override
    public void println(String x) {
        logger.log(Level.INFO, x);
        original.println(x);
    }
}
