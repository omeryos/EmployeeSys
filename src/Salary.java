public class Salary {
    private String date;
    private double gross;
    private double tax;
    private double total;

    public Salary(String date, double gross, double tax, double total) {
        this.date = date;
        this.gross = gross;
        this.tax = tax;
        this.total = total;
    }
    public double getTotal() {
        return total;
    }

    public String getDate(){
        return date;
    }

    public double getGrossSalary(){
        return gross;
    }

    public double getTax(){
        return tax;
    }


    public String toJson() {
        return String.format(
                "{\"date\": \"%s\", \"gross\": %.2f, \"tax\": %.2f, \"total\": %.2f}",
                date, gross, tax, total);
    }

    @Override
    public String toString() {
        return "Salary{" +
                "date='" + date + '\'' +
                ", gross=" + gross +
                ", tax=" + tax +
                ", total=" + total +
                '}';
    }
}
