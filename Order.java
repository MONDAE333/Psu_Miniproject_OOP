import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private String orderId;
    private Cashier cashier;
    private List<OrderItem> orderItems;
    private double totalAmount;
    private Discount discountApplied;
    private LocalDateTime timestamp;
    private String status;
    private double cashTendered; // เงินที่รับมา
    private double change;       // เงินทอน

    public Order(String id, Cashier c) {
        this.orderId = id;
        this.cashier = c;
        this.orderItems = new ArrayList<>();
        this.totalAmount = 0.0;
        this.timestamp = LocalDateTime.now();
        this.status = "PENDING";
    }

    public void setPaymentDetails(double cash, double change) {
        this.cashTendered = cash;
        this.change = change;
    }

    public double getCashTendered() { return cashTendered; }
    public double getChange() { return change; }
    public LocalDateTime getTimestamp() { return timestamp; }
    
    public void addItemToCart(Product p, int qty) {
        orderItems.add(new OrderItem(p, qty));
    }

    public void removeItemFromCart(Product p) {
        orderItems.removeIf(item -> item.getProduct().getProductId().equals(p.getProductId()));
    }

    public void updateItemQuantity(Product p, int qty) {
        for (OrderItem item : orderItems) {
            if (item.getProduct().getProductId().equals(p.getProductId())) {
                item.setQuantity(qty);
                break;
            }
        }
    }

    public double calculateTotal() {
        totalAmount = 0;
        for (OrderItem item : orderItems) {
            totalAmount += item.getSubTotal();
        }
        return totalAmount; 
    }

    public double calculateGrandTotal() {
        double subTotal = calculateTotal();
        double afterDiscount = subTotal;
        
        if (discountApplied != null) {
            afterDiscount -= discountApplied.getAmount();
            if (afterDiscount < 0) afterDiscount = 0;
        }
        
        // คิด VAT 7% หลังหักส่วนลด
        double vat = afterDiscount * 0.07;
        return afterDiscount + vat;
    }

    public void printOrderSummary() {
        System.out.println("รายการสินค้าในตะกร้า:");
        for (OrderItem item : orderItems) {
            System.out.println("- [" + item.getProduct().getProductId() + "] " 
                + item.getProduct().getName() + " x " + item.getQuantity() 
                + " (" + item.getSubTotal() + " THB)");
        }
    }

    public void applyDiscount(Discount d) {
        this.discountApplied = d;
        System.out.println("[System] Discount " + d.getCode() + " applied.");
    }

    public boolean validateStock() {
        for (OrderItem item : orderItems) {
            if (item.getQuantity() > item.getProduct().stockQuantity) {
                System.out.println("[Error] Stock insufficient for " + item.getProduct().getName());
                return false;
            }
        }
        return true;
    }

    public void deductStock() {
        for (OrderItem item : orderItems) {
            item.getProduct().updateStock(-item.getQuantity());
        }
    }

    public boolean processCheckout() {
        if (validateStock()) {
            deductStock();
            this.status = "PAID";
            return true;
        }
        return false;
    }

    public void cancelOrder() {
        this.status = "CANCELLED";
    }

    public String getOrderStatus() { return status; }
    public String getOrderId() { return orderId; }
    public Cashier getCashier() { return cashier; }
    public List<OrderItem> getOrderItems() { return orderItems; }
    public double getTotalAmount() { return totalAmount; }
}