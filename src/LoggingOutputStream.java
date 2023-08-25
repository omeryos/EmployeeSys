import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class LoggingOutputStream extends ByteArrayOutputStream {
    private final Logger logger;
    private final Level level;

    public LoggingOutputStream(Logger logger, Level level) {
        this.logger = logger;
        this.level = level;
    }

    @Override

    public synchronized void write(int b) {
        byte[] singleByte = { (byte) b };
        this.write(singleByte, 0, 1);
    }

}



