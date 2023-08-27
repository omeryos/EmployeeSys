import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DataSource {

    private static Map<Integer, Employee> employeesCache;
    private static long cacheTimestamp;

    // Define a map to store username-password pairs
    public static Map<String, String> userPasswordMap = new HashMap<>();

    public static void getUser() {
        String authUsers = "Users.dat"; // path to the authorised users file

        // Read users
        try (BufferedReader br = new BufferedReader(new FileReader(authUsers))) {
            String line;
            boolean isFirstLine = true; // Skip header
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                try {
                    String[] values = line.split(",");
                    String username = values[0];
                    String password = values[1]; // Assuming password is at index 1 in the CSV
                    userPasswordMap.put(username, password);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static List<Employee> getEmployees(Integer isActive, Integer minCode, Integer maxCode) {
        long currentTime = System.currentTimeMillis();

        // Check if cache is older than 2 hours
        if (employeesCache == null || (currentTime - cacheTimestamp) > 2 * 60 * 60 * 1000) {
            employeesCache = new HashMap<>();
            cacheTimestamp = currentTime;

            String filePath = "Employees.eng.dat"; // Path to the employee file
            String salariesFilePath = "Salaries.dat"; // Path to the salaries file

//

            // Read Employees
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                boolean isFirstLine = true; // Skip header
                while ((line = br.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;

                    }
                    try {
                        String[] values = line.split(",");
                        int code = Integer.parseInt(values[0]);
                        String name = values[1];
                        int activeStatus = Integer.parseInt(values[2]);
                        String street = values[3];
                        String number = values[4];
                        String city = values[5];
                        String country = values[6];

                        Employee employee = new Employee(code, name, activeStatus, street, number, city, country);
                        employeesCache.put(code, employee);
                    } catch (NumberFormatException e) {
                        System.err.println("Could not parse line: " + line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Read Salaries
            try (BufferedReader br = new BufferedReader(new FileReader(salariesFilePath))) {
                String line;
                boolean isFirstLine = true; // Skip header
                while ((line = br.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        continue;
                    }
                    try {
                        String[] values = line.split(",");
                        int employeeCode = Integer.parseInt(values[0]);
                        Employee employee = employeesCache.get(employeeCode);
                        if (employee != null) {
                            // This assumes you have a Salary constructor like Salary(date, gross, tax, total)
                            Salary salary = new Salary(values[1], Double.parseDouble(values[2]), Double.parseDouble(values[3]), Double.parseDouble(values[4]));
                            employee.addSalary(salary);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Could not parse line: " + line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Return filtered list
        List<Employee> filteredEmployees = new ArrayList<>();
        for (Employee employee : employeesCache.values()) {
            int code = employee.getCode();
            int activeStatus = employee.getIsActive();
            if ((isActive == null || isActive == activeStatus) &&
                    (minCode == null || code >= minCode) &&
                    (maxCode == null || code <= maxCode)) {
                filteredEmployees.add(employee);
            }
        }

        return filteredEmployees;
    }
    public static Employee getEmployeeByCode(int employeeCode) {
        System.out.println("Employee code in the getEmployeeByCode " + employeeCode);



        if (employeesCache == null) {
            System.out.println("Cache is null. Populating...");
            getEmployees(null, null, null);  // empty params to populate the cache
        }

        System.out.println("Cache size: " + employeesCache.size());


        if (!employeesCache.containsKey(employeeCode)) {
            System.out.println("Cache does not contain key for employee code: " + employeeCode);
            return null;
        }

        return employeesCache.get(employeeCode);
    }



}
