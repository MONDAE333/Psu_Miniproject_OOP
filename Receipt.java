import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Receipt {
    private String receiptId;
    private Order order;
    private String paymentMethod;
    private LocalDateTime timestamp;
    
    // เพิ่มตัวแปรเก็บเงินที่รับและเงินทอน
    private double amountTendered;
    private double change;

    // อัปเดต Constructor ให้รับค่าเงินสดและเงินทอน
    public Receipt(String id, Order o, String paymentMethod, double amountTendered, double change) {
        this.receiptId = id;
        this.order = o;
        this.paymentMethod = paymentMethod;
        this.amountTendered = amountTendered;
        this.change = change;
    }

    public void generateReceipt() {
        this.timestamp = LocalDateTime.now();
    }

    public void printBill() {
        generateReceipt(); // เตรียมข้อมูลเวลา
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        
        System.out.println("\n-------------------------------------------");
        System.out.println("Moshi Moshi Japan Co.,Ltd. Br. 014");
        System.out.println("ใบเสร็จรับเงิน/ใบกำกับภาษีอย่างย่อ");
        System.out.println("เลขที่ : " + receiptId);
        System.out.println("วันที่ : " + timestamp.format(fmt));
        System.out.println("แคชเชียร์ : " + order.getCashier().getFirstName());
        System.out.println("-------------------------------------------");
        System.out.println("สินค้า                จำนวน             รวม");
        
        for (OrderItem item : order.getOrderItems()) {
            System.out.printf("%d %s\t\t%.2f\n", 
                item.getQuantity(), 
                item.getProduct().getName(), 
                item.getSubTotal());
        }
        System.out.println("-------------------------------------------");
        System.out.printf("ยอดรวมทั้งหมด:\t\t%.2f THB\n", order.getTotalAmount());
        
        // เพิ่มการแสดงผล เงินที่รับมา และ เงินทอน
        System.out.printf("เงินสด (Cash):\t\t%.2f THB\n", amountTendered);
        System.out.printf("เงินทอน (Change):\t%.2f THB\n", change);
        
        System.out.println("ชำระโดย: " + paymentMethod);
        System.out.println("**** ขอบคุณที่ใช้บริการ ****\n");
    }
}