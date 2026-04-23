/**
 * [แยก Responsibility] Receipt - เพียง data holder เท่านั้น
 * Logic ย้ายไป BillFormatter
 */
public class Receipt {
    private String receiptNo;
    private Order order;
    private String payMethod;
    private double cashTendered;
    private double change;

    public Receipt(String receiptNo, Order order, String payMethod, double cash, double change) {
        this.receiptNo = receiptNo;
        this.order = order;
        this.payMethod = payMethod;
        this.cashTendered = cash;
        this.change = change;
    }

    // --- Getters ---
    public String getReceiptNo() { return receiptNo; }
    public Order getOrder() { return order; }
    public String getPayMethod() { return payMethod; }
    public double getCashTendered() { return cashTendered; }
    public double getChange() { return change; }
}