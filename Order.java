import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private String orderId;
    private Cashier cashier;
    private List<OrderItem> orderItems;
    private double totalAmount;
    private Discount discount; 
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
        // 1. วนลูปเช็คว่ามีสินค้านี้อยู่ในตะกร้าแล้วหรือยัง
        for (OrderItem item : orderItems) {
            if (item.getProduct().getProductId().equals(p.getProductId())) {
                // ถ้ามีอยู่แล้ว ให้นำจำนวนเดิมมาบวกกับจำนวนใหม่
                int newQty = item.getQuantity() + qty;
                item.setQuantity(newQty);
                return; // สำคัญมาก! เจอแล้วบวกเสร็จ สั่ง return เพื่อออกจากเมธอดทันที
            }
        }
        
        // 2. ถ้าหลุดลูปมาถึงตรงนี้ แปลว่ายังไม่เคยมีสินค้านี้ในตะกร้า ค่อยสร้างบรรทัดใหม่
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
        double discountAmount = 0.0;
        
        // ให้ Object Discount เป็นคนคำนวณยอดลดให้เอง (ตามหลัก OOP)
        if (this.discount != null) {
            discountAmount = this.discount.calculateDiscountAmount(subTotal);
        }
        
        double totalAfterDiscount = subTotal - discountAmount;
        if (totalAfterDiscount < 0) totalAfterDiscount = 0;
        
        // ถ้ามี VAT 7% ให้คิดจากยอดที่ลดแล้ว (ขึ้นอยู่กับระบบของคุณ)
        // สมมติว่าราคาสินค้ารวม VAT แล้ว ก็ return totalAfterDiscount ได้เลย
        return totalAfterDiscount; 
    }

    public void printOrderSummary() {
        if (orderItems.isEmpty()) {
            System.out.println("  (ตะกร้าสินค้าว่างเปล่า)");
            return;
        }
        for (OrderItem item : orderItems) {
            System.out.println(String.format("  - [%s] %-20s x %2d  = %8.2f THB", 
                item.getProduct().getProductId(), 
                item.getProduct().getName(), 
                item.getQuantity(), 
                item.getSubTotal()));
        }
    }

    public void applyDiscount(Discount discount) {
        this.discount = discount;
    }

    public boolean validateStock() {
        for (OrderItem item : orderItems) {
            if (item.getQuantity() > item.getProduct().getStockQuantity()) {
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
        this.status = "PAID";
        return true;
    }

    public void cancelOrder() {
        this.status = "CANCELLED";
    }

    // ==========================================
    // [เพิ่มใหม่] คืนสต๊อกสินค้าทั้งหมด (Restore Stock)
    // หน้าที่: คืนสต๊อกที่จองแล้วกลับเข้าระบบ เมื่อยกเลิกออเดอร์หรือลบสินค้า
    // ==========================================
    public void restoreStock() {
        for (OrderItem item : orderItems) {
            item.getProduct().updateStock(item.getQuantity());
        }
    }

    public String getOrderStatus() { return status; }
    public String getOrderId() { return orderId; }
    public Cashier getCashier() { return cashier; }
    public List<OrderItem> getOrderItems() { return orderItems; }
    public double getTotalAmount() { return totalAmount; }
    public Discount getDiscount() { return discount; }
}