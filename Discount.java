public class Discount {
    private String code;
    private double amount;

    public Discount(String code, double amount) {
        this.code = code;
        this.amount = amount;
    }
    public String getCode() { return code; }
    public double getAmount() { return amount; }
}