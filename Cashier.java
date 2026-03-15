import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class Cashier extends User {
    protected int counterNumber;
    protected LocalDateTime shiftStartTime;
    protected double totalSalesInShift;
    protected int ot;
    private LocalDateTime shiftEndTime;
    private boolean isShiftOpen = false;
    
    // เปลี่ยนมาใช้ ArrayList แบบ Basic ในการจดบันทึกสินค้า
    private List<String> soldItemsLog;

    public Cashier(String userId, String user, String pass, String nick, String fName, String lName, String addr, String phone, String role, int counter) {
        super(userId, user, pass, nick, fName, lName, addr, phone, role);
        this.counterNumber = counter;
        this.totalSalesInShift = 0.0;
        this.ot = 0;
        this.soldItemsLog = new ArrayList<>(); // สร้างลิสต์เปล่า
    }

    public void openShift() {
        this.isShiftOpen = true;
        this.shiftStartTime = LocalDateTime.now(); // เก็บเวลาเปิดกะ
        this.totalSalesInShift = 0.0;
        this.soldItemsLog.clear();
        System.out.println("[Cashier] Shift OPENED at Counter " + counterNumber);
        System.out.println("เริ่มงานเวลา: " + this.shiftStartTime);
    }

    public void closeShift() {
        this.isShiftOpen = false;
        this.shiftEndTime = LocalDateTime.now(); // เก็บเวลาตอนออกกะ
        System.out.println("[Cashier] Shift CLOSED at " + this.shiftEndTime);
        printXReport();
        // ทริคเพิ่มเติม: เราสามารถเขียน X-Report ลงไฟล์ .txt ได้ที่นี่เพื่อเก็บ Report
    }

    // เมธอดแบบ Basic: เอาชื่อสินค้ากับจำนวนมาต่อเป็นข้อความ (String) แล้วเก็บลง List
    public void recordSoldItems(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            String logText = item.getProduct().getName() + " x " + item.getQuantity();
            soldItemsLog.add(logText);
        }
    }

    public void printXReport() {
        System.out.println("\n===================================");
        System.out.println("      X-REPORT (END OF SHIFT)      ");
        System.out.println("===================================");
        System.out.println("Cashier: " + this.firstName);
        System.out.println("-----------------------------------");
        System.out.println("Items Sold (รายการสินค้าที่ขายไป):");
        
        if (soldItemsLog.isEmpty()) {
            System.out.println("- No items sold.");
        } else {
            // วนลูปปริ้นท์ String ออกมาแสดงผลทีละบรรทัด
            for (String log : soldItemsLog) {
                System.out.println("- " + log);
            }
        }
        
        System.out.println("-----------------------------------");
        System.out.println("Total Sales: " + this.totalSalesInShift + " THB");
        System.out.println("===================================\n");
    }

    public Order createOrder(String orderId) {
        if (!isShiftOpen) {
            System.out.println("[Warning] Please open shift first!");
            return null;
        }
        return new Order(orderId, this);
    }

    public double processPayment(double amountTendered, double totalAmount) {
        double difference = amountTendered - totalAmount; // หาค่าส่วนต่าง
        
        if (difference >= 0) {
            totalSalesInShift += totalAmount; // บันทึกยอดขายถ้ารับเงินมาพอ
        }
        
        // คืนค่าส่วนต่าง (ถ้าเป็นบวกคือเงินทอน ถ้าเป็นลบคือยอดเงินที่ยังขาด)
        return difference; 
    }

    public double checkProductPrice(Product p) { return p.getPrice(); }
    public int checkProductStock(Product p) { return p.stockQuantity; }
    public boolean isShiftOpen() { return isShiftOpen; }
}