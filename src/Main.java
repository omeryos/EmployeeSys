import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.io.PrintStream;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        DataSource.getUser();
        Scanner scanner = new Scanner(System.in);
        String[] credentials = new String[2];
        System.out.print("Enter username: ");
        credentials[0] = scanner.nextLine();
        System.out.print("Enter password: ");
        credentials[1] = scanner.nextLine();
        scanner.close();

        // Validate username and password
        if (DataSource.userPasswordMap.containsKey(credentials[0]) &&
                DataSource.userPasswordMap.get(credentials[0]).equals(credentials[1])) {
            System.out.println("Valid credentials. Access granted.");
        } else {
            System.out.println("Invalid credentials. Access denied, program shutdown.");
            System.exit(401);
        }

        // Set up the custom logger
        ServerLogger.setup();

        // Redirect System.out to  logger
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(new LoggingPrintStream(originalOut, ServerLogger.getLogger()), true));

        // Start the server
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/login", new LoginHandler());
        server.createContext("/employees", new EmployeeHandler());
        server.createContext("/employee/", new EmployeeHandler()); // This handles the /employee/{code} requests
        server.setExecutor(null); // Creates a default executor
        server.start();
        System.out.println("Server started on port 8000");
    }
}
