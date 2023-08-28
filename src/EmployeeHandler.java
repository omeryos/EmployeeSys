import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class EmployeeHandler implements HttpHandler {
    private static MyLogger logger = new MyLogger();

    @Override

    public void handle(HttpExchange exchange) throws IOException {
        if (!LoginHandler.isAuthorized) {
            sendResponse(exchange, "Unauthorized", 401);
            return;
        }
        String path = exchange.getRequestURI().getPath();
        System.out.println("Received request for path: " + path);

        if ("/employees".equals(path) && "GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            handleGetEmployees(exchange);
        } else if (path.startsWith("/employee/") && "GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            String employeeCodeStr = path.replace("/employee/", "");


            try {
                int employeeCode = Integer.parseInt(employeeCodeStr);
                handleGetEmployee(exchange, employeeCode);
            } catch (NumberFormatException e) {
                sendResponse(exchange, "Invalid Employee Code", 400);
            }
        } else {
            sendResponse(exchange, "Not Found", 404);
        }
    }

    public void handleGetEmployees(HttpExchange exchange) throws IOException {
        Main.addCorsHeaders(exchange);
        Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
        Integer minCode = parseInteger(params, "minCode");
        Integer maxCode = parseInteger(params, "maxCode");
        Integer isActive = parseInteger(params, "isActive");
        String sortField = params.get("sortField");
        String order = params.get("order");
        Boolean includeSalaries = Boolean.parseBoolean(params.get("includeSalaries"));

        List<Employee> employees = DataSource.getEmployees(isActive, minCode, maxCode);

        if (sortField != null) {
            System.out.println("Sorting employees by: " + sortField);
            employees = sortEmployees(employees, sortField, order);
        }

        System.out.println("Employees returned from query: " + employees );
        String jsonResponse = convertToJson(employees, includeSalaries);
        sendResponse(exchange, jsonResponse);
    }

    public static void handleGetEmployee(HttpExchange exchange, int employeeCode ) throws IOException {
        Main.addCorsHeaders(exchange);
        Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
        boolean includeSalaries = "true".equals(params.get("includeSalaries"));
        boolean includeAddress = "true".equals(params.get("address"));

        Employee employee = DataSource.getEmployeeByCode(employeeCode);
        if (employee == null) {
            sendResponse(exchange, "Employee not found", 404);
            return;
        }
        System.out.println("Employee returned from query: " + employee );
        String jsonResponse = employee.toJson(includeSalaries, includeAddress);
        sendResponse(exchange, jsonResponse);
    }


    private static Integer parseInteger(Map<String, String> params, String key) {
        if (params.containsKey(key)) {
            try {
                return Integer.parseInt(params.get(key));
            } catch (NumberFormatException e) {
                System.err.println("Failed to parse " + key + " value: " + params.get(key));
                return null;
            }
        }
        return null;
    }

    private List<Employee> sortEmployees(List<Employee> employees, String sortField, String order) {
        if (order == null) order = "asc";

        Comparator<Employee> comparator;

        switch (sortField) {
            case "code":
                comparator = Comparator.comparingInt(Employee::getCode);
                break;
            case "name":
                comparator = Comparator.comparing(Employee::getName);
                break;
            case "yearly":
                comparator = Comparator.comparingDouble(Employee::getYearlyTotal);
                break;
            default:
                return employees;
        }

        if ("desc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }

        employees.sort(comparator);

        return employees;
    }

    public static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        if (query != null) {
            for (String param : query.split("&")) {
                String[] entry = param.split("=");
                if (entry.length > 1) {
                    result.put(entry[0], entry[1]);
                }
            }
        }
        return result;
    }

    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        sendResponse(exchange, response, 200);
    }

    private static void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static String convertToJson(List<Employee> employees, boolean includeSalaries) {
        StringBuilder json = new StringBuilder("[");
        for (Employee employee : employees) {
            json.append(employee.toJson(includeSalaries)).append(", ");
        }
        if (json.length() > 1) {
            json.setLength(json.length() - 2); // Remove trailing comma and space
        }
        json.append("]");
        return json.toString();
    }
}
