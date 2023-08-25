import java.util.ArrayList;
import java.util.List;

public class Employee {
    private int code;
    private String name;
    private int isActive;
    private String addressStreet;
    private String addressNumber;
    private String addressCity;
    private String addressCountry;
    private List<Salary> salaries = new ArrayList<>();
    private double yearlyTotal;

    public Employee(int code, String name, int isActive, String addressStreet, String addressNumber, String addressCity, String addressCountry) {
        this.code = code;
        this.name = name;
        this.isActive = isActive;
        this.addressStreet = addressStreet;
        this.addressNumber = addressNumber;
        this.addressCity = addressCity;
        this.addressCountry = addressCountry;
    }

    public void addSalary(Salary salary) {
        this.salaries.add(salary);
        this.yearlyTotal += salary.getTotal();
    }

    public List<Salary> getSalaries() {
        return salaries;
    }

    public double getYearlyTotal() {
        return yearlyTotal;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getIsActive() {
        return isActive;
    }

    public String getAddressStreet() {
        return addressStreet;
    }

    public String getAddressNumber() {
        return addressNumber;
    }

    public String getAddressCity() {
        return addressCity;
    }

    public String getAddressCountry() {
        return addressCountry;
    }

    public String toJson(boolean includeSalaries, boolean includeAddress) {
        StringBuilder json = new StringBuilder("{");

        // Basic employee details
        json.append("\"code\": ").append(code).append(", ");
        json.append("\"name\": \"").append(name.trim()).append("\", ");
        json.append("\"isActive\": ").append(isActive).append(", ");

        // Including address details if requested
        if (includeAddress) {
            json.append("\"addressStreet\": \"").append(addressStreet.trim()).append("\", ");
            json.append("\"addressNumber\": \"").append(addressNumber.trim()).append("\", ");
            json.append("\"addressCity\": \"").append(addressCity.trim()).append("\", ");
            json.append("\"addressCountry\": \"").append(addressCountry.trim()).append("\", ");
        }

        json.append("\"yearlyTotal\": ").append(String.format("%.2f", yearlyTotal));

        // Including salaries if requested
        if (includeSalaries && salaries != null) {
            json.append(", \"salaries\": [");
            for (Salary salary : salaries) {
                json.append(salary.toJson()).append(", ");
            }
            if (!salaries.isEmpty()) {
                json.setLength(json.length() - 2); // remove trailing comma and space
            }
            json.append("]");
        }

        json.append("}");
        return json.toString();
    }

    public String toJson() {
        return toJson(false, false); // Default behavior might not include either salaries or address details
    }

    public String toJson(boolean includeSalaries) {
        return toJson(includeSalaries, false); // Default behavior when only salaries inclusion is specified
    }

    @Override
    public String toString() {
        return "Employee{" +
                "code=" + code +
                ", name='" + name + '\'' +
                ", isActive=" + isActive +
                ", addressStreet='" + addressStreet + '\'' +
                ", addressNumber='" + addressNumber + '\'' +
                ", addressCity='" + addressCity + '\'' +
                ", addressCountry='" + addressCountry + '\'' +
                ", Salaries=" + salaries +
                ", YearlyTotal=" + yearlyTotal +
                '}';
    }
}
