import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;


public class LoginHandler implements HttpHandler {
    public static boolean isAuthorized = false;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Main.addCorsHeaders(exchange);
        String path = exchange.getRequestURI().getPath();
        System.out.println("path in the login handler: " + path);
        if ("POST".equals(exchange.getRequestMethod()))  {
//&& ("/Login/".equalsIgnoreCase(path)))
                String userNameAndPass = path.replace("/login/", "");
                String result = null;
                String[] credentials = new String[2];
                // Here, you would handle the login logic (e.g., read the request body, validate credentials, etc.)
                // For now, we'll just send a successful response.
                //EmployeeHandler.queryToMap(exchange.getRequestURI().getQuery());
                String[] entry = userNameAndPass.split("&");
                credentials[0] = entry[0];
                credentials[1] = entry[1];

                System.out.println("username and password entered: " + userNameAndPass);
                System.out.println("credentials: " + credentials[0] + " " + credentials[1]);

            if (DataSource.userPasswordMap.containsKey(credentials[0]) &&
                    DataSource.userPasswordMap.get(credentials[0]).equals(credentials[1])) {
                isAuthorized = true;
                System.out.println("Valid credentials. Access granted.");
                String response = "Successfully logged in!";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();

                System.out.println("isAuth " + isAuthorized );
            }else{
                isAuthorized = false;
                System.out.println("Invalid credentials...naughty naughty!");
                String response = "Wrong username or password!";
                exchange.sendResponseHeaders(401, -1); // 401  Not Allowed
                exchange.close();
            }

        }
    }

}
