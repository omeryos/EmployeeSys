import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class DualOutputPrintStream extends PrintStream {
    private MyLogger logger;

    public DualOutputPrintStream(OutputStream out, MyLogger logger) {
        super(out, true);
        this.logger = logger;
    }

    @Override
    public void println(String x) {
        super.println(x);
        logger.log(x);
    }
}
