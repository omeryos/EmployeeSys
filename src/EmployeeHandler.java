import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

public class EmployeeHandler implements HttpHandler {

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
                Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
                boolean toFile = params.containsKey("toFile") && params.get("toFile").equalsIgnoreCase("true");

                if (toFile) {
                    // Handle the case for generating and sending CSV file
                    handleGetEmployeeCSV(exchange, employeeCode);
                } else {
                    // Handle the case for returning JSON data
                    handleGetEmployee(exchange, employeeCode);
                }
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
        boolean toFile = params.containsKey("toFile") && params.get("toFile").equalsIgnoreCase("true");

        List<Employee> employees = DataSource.getEmployees(isActive, minCode, maxCode);

        if (sortField != null) {
            System.out.println("Sorting employees by: " + sortField);
            employees = sortEmployees(employees, sortField, order);
        }

        System.out.println("Employees returned from query: " + employees);
        String response;

        if (toFile) {
            response = convertToCSV(employees);
            exchange.getResponseHeaders().add("Content-Type", "text/csv");
            exchange.getResponseHeaders().add("Content-Disposition", "attachment; filename=employees.csv");
        } else {
            response = convertToJson(employees, includeSalaries);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
        }

        sendResponse(exchange, response);
    }

    public void handleGetEmployeeCSV(HttpExchange exchange, int employeeCode) throws IOException {
        Main.addCorsHeaders(exchange);
        Employee employee = DataSource.getEmployeeByCode(employeeCode);

        if (employee == null) {
            sendResponse(exchange, "Employee not found", 404);
            return;
        }

        String csv = convertToCSV(Collections.singletonList(employee));
        exchange.getResponseHeaders().add("Content-Type", "text/csv");
        exchange.getResponseHeaders().add("Content-Disposition", "attachment; filename=employee.csv");

        sendResponse(exchange, csv);
    }

    public static void handleGetEmployee(HttpExchange exchange, int employeeCode ) throws IOException {
        Main.addCorsHeaders(exchange);
        Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
        boolean includeSalaries = "true".equals(params.get("includeSalaries"));
        boolean includeAddress = "true".equals(params.get("address"));
        boolean toFile = params.containsKey("toFile") && params.get("toFile").equalsIgnoreCase("true");

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

    private static String convertToCSV(List<Employee> employees) {
        StringBuilder csv = new StringBuilder("Code,Name,IsActive,Street,Number,City,Country,SalaryDate,GrossSalary,Tax,NetSalary\n");

        for (Employee employee : employees) {
            List<Salary> salaries = employee.getSalaries();
            String employeeRow = String.format("%d,%s,%d,%s,%s,%s,%s", employee.getCode(), employee.getName(), employee.getIsActive(),
                    employee.getAddressStreet(), employee.getAddressNumber(), employee.getAddressCity(), employee.getAddressCountry());
            csv.append(employeeRow).append("\n");
            for (Salary salary : salaries) {
                String salaryRow = String.format(",%s,%.2f,%.2f,%.2f", salary.getDate(), salary.getGrossSalary(), salary.getTax(), salary.getTotal());
                csv.append(salaryRow).append("\n");
            }
        }

        return csv.toString();
    }
}
