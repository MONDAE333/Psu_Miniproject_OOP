import java.time.LocalDate;

public class Discount {
    private String code;
    private String description;
    private double minAmount;
    private double percentage;
    private LocalDate expirationDate; // [เพิ่มใหม่] วันที่หมดอายุ

    public Discount(String code, String description, double minAmount, double percentage, LocalDate expirationDate) {
        this.code = code.toUpperCase();
        this.description = description;
        this.minAmount = minAmount;
        this.percentage = percentage;
        this.expirationDate = expirationDate;
    }

    // --- Getters & Setters ---
    public String getCode() { return code; }
    public String getDescription() { return description; }
    public double getMinAmount() { return minAmount; }
    public double getPercentage() { return percentage; }
    public LocalDate getExpirationDate() { return expirationDate; }

    public void setDescription(String desc) { this.description = desc; }
    public void setMinAmount(double min) { this.minAmount = min; }
    public void setPercentage(double percent) { this.percentage = percent; }
    public void setExpirationDate(LocalDate expDate) { this.expirationDate = expDate; }

    // --- เมธอดเช็ควันหมดอายุ (OOP Behavior) ---
    public boolean isExpired() {
        // ถ้า "วันปัจจุบัน" (now) อยู่ "หลัง" (isAfter) วันหมดอายุ แปลว่าหมดอายุแล้ว
        return LocalDate.now().isAfter(this.expirationDate);
    }

    public double calculateDiscountAmount(double subTotal) {
        if (subTotal >= minAmount && !isExpired()) {
            return subTotal * (percentage / 100.0);
        }
        return 0.0;
    }
}