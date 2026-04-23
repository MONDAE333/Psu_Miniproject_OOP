import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;

public class Cashier extends User {
    private int counterNumber;
    private LocalDateTime shiftStartTime;
    private double totalSalesInShift;
    private LocalDateTime shiftEndTime;
    private boolean isShiftOpen = false;
    
    private List<String> soldItemsLog;

    public Cashier(String userId, String user, String pass, String nick, String fName, String lName, String addr, String phone, String role, int counter) {
        super(userId, user, pass, nick, fName, lName, addr, phone, role);
        this.counterNumber = counter;
        this.totalSalesInShift = 0.0;
        this.soldItemsLog = new ArrayList<>(); 
    }

    // ==========================================
    // [แยก Responsibility] เปิดกะ - Single Responsibility
    // หน้าที่: เพียงจัดการสถานะของกะ (state management)
    // ==========================================
    public void openShift() {
        if (this.isShiftOpen) {
            // Error: Shift already open - ให้ Main จัดการ output
            return; 
        }
        
        this.isShiftOpen = true;
        this.shiftStartTime = LocalDateTime.now(); 
        this.totalSalesInShift = 0.0;
        this.soldItemsLog.clear();
    }

    // ==========================================
    // [เพิ่มใหม่] ได้ข้อมูลการเปิดกะ - เพื่อให้ Main จัดการการแสดงผล
    // ==========================================
    public String getOpenShiftMessage() {
        if (!this.isShiftOpen) {
            return "[Error] ยังไม่ได้เปิดกะ!";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "[Cashier] Shift OPENED at Counter " + counterNumber + 
               "\nเริ่มงานเวลา: " + this.shiftStartTime.format(formatter);
    }

    // ==========================================
    // [แยก Responsibility] ปิดกะ - Single Responsibility
    // หน้าที่: เพียงจัดการสถานะของกะ
    // ==========================================
    public void closeShift() {
        if (!this.isShiftOpen) {
            // Error: Shift not open - ให้ Main จัดการ output
            return; 
        }

        this.isShiftOpen = false;
        this.shiftEndTime = LocalDateTime.now(); 
    }

    // ==========================================
    // [เพิ่มใหม่] ได้ข้อมูลการปิดกะ - เพื่อให้ Main จัดการการแสดงผล
    // ==========================================
    public String getCloseShiftMessage() {
        if (this.isShiftOpen) {
            return "[Error] กะยังไม่ปิด!";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "[Cashier] Shift CLOSED at " + this.shiftEndTime.format(formatter);
    }

    // ==========================================
    // [แยก Responsibility] บันทึกรายการสินค้า - Single Responsibility
    // หน้าที่: ONLY บันทึก log ของสินค้าที่ขาย
    // ==========================================
    public void recordSoldItemsLog(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            String logText = item.getProduct().getName() + " x " + item.getQuantity();
            soldItemsLog.add(logText);
        }
    }

    // ==========================================
    // [แยก Responsibility] บวกยอดขาย - Single Responsibility
    // หน้าที่: ONLY บวกยอดขายเข้ากะ
    // ==========================================
    public void recordDailySales(Order order) {
        this.totalSalesInShift += order.calculateGrandTotal();
    }

    // ==========================================
    // [เพิ่มใหม่] ได้สรุปรายงาน X-Report - เพื่อให้ Main จัดการการแสดงผล
    // ==========================================
    public String generateXReportData() {
        StringBuilder report = new StringBuilder();
        report.append("\n===================================\n");
        report.append("      X-REPORT (END OF SHIFT)      \n");
        report.append("===================================\n");
        report.append("Cashier: ").append(this.getFirstName()).append("\n");
        report.append("-----------------------------------\n");
        report.append("Items Sold (รายการสินค้าที่ขายไป):\n");
        
        if (soldItemsLog.isEmpty()) {
            report.append("- No items sold.\n");
        } else {
            for (String log : soldItemsLog) {
                report.append("- ").append(log).append("\n");
            }
        }
        
        report.append("-----------------------------------\n");
        report.append("Total Sales: ").append(String.format("%.2f", this.totalSalesInShift)).append(" THB\n");
        report.append("===================================\n");
        
        return report.toString();
    }

    public void printXReport() {
        System.out.print(generateXReportData());
    }

    public Order createOrder(String orderId) {
        if (!isShiftOpen) {
            // Warning: Please open shift first - ให้ Main จัดการ output
            return null;
        }
        return new Order(orderId, this);
    }

    // ==========================================
    // [รักษาไว้] คำนวณเงินทอน - Single Responsibility
    // หน้าที่: ONLY คำนวณเงินทอน (ไม่ได้บวกยอดขาย)
    // ==========================================
    public double processPayment(double amountTendered, double totalAmount) {
        return amountTendered - totalAmount; 
    }

    // ==========================================
    // [เพิ่มใหม่] Getter/Setter สำหรับ Properties
    // ==========================================
    public double getTotalSalesInShift() { return totalSalesInShift; }
    public LocalDateTime getShiftStartTime() { return shiftStartTime; }
    public LocalDateTime getShiftEndTime() { return shiftEndTime; }
    public int getCounterNumber() { return counterNumber; }
    public List<String> getSoldItemsLog() { return new ArrayList<>(soldItemsLog); }
    public boolean isShiftOpenNow() { return isShiftOpen; }
}
