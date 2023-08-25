import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

public class LoggingHttpHandler implements HttpHandler {
    private final HttpHandler handler;
    private final MyLogger logger;

    public LoggingHttpHandler(HttpHandler handler, MyLogger logger) {
        this.handler = handler;
        this.logger = logger;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Log the request information
        logger.log("Request received for path: " + exchange.getRequestURI().getPath());

        // Delegate handling to the wrapped HttpHandler
        try {
            handler.handle(exchange);
        } catch (IOException e) {
            logger.log("Exception during request handling: " + e.getMessage());
            throw e;
        }

        // Log the response information
        logger.log("Response sent for path: " + exchange.getRequestURI().getPath());
    }
}
