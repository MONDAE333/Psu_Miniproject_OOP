/**
 * [เพิ่มใหม่] Formatter class สำหรับจัดการการแสดงผลใบเสร็จ
 * Responsibility: เฉพาะการจัดฟอร์แมต และส่งข้อมูล (ไม่มี business logic)
 * ไม่มี System.out.println - return String ให้ Main print
 */
public class BillFormatter {
    
    /**
     * สร้าง bill text จากข้อมูล Order
     */
    public String formatBill(Receipt receipt) {
        Order order = receipt.getOrder();
        StringBuilder bill = new StringBuilder();
        
        bill.append("\n==============================================\n");
        
        // --- Header (จัดกึ่งกลาง) ---
        bill.append("     Moshi Moshi Hatyai Co.,Ltd. Br. 014\n");
        bill.append("          TEL. 065-517-7151 FAX:\n");
        bill.append("        ใบเสร็จรับเงิน/ใบกำกับภาษีอย่างย่อ\n");
        bill.append("          TAX ID. 0105543056078\n");
        bill.append("----------------------------------------------\n");

        // --- Transaction Info ---
        java.time.LocalDateTime now = order.getTimestamp();
        java.time.format.DateTimeFormatter dateFmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        java.time.format.DateTimeFormatter timeFmt = java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss");
        
        String runningNo = "14100" + order.getCashier().getCounterNumber() + now.format(java.time.format.DateTimeFormatter.ofPattern("MMdd"));

        bill.append(padRight("RID." + receipt.getReceiptNo(), 26)).append("เลขที่ : ").append(runningNo).append("\n");
        bill.append(padRight("วันที่ " + now.format(dateFmt), 26)).append("เวลา   : ").append(now.format(timeFmt)).append("\n");
        bill.append(padRight("เครื่อง " + order.getCashier().getCounterNumber(), 26)).append("แคชเชียร์ : ").append(order.getCashier().getFirstName().toUpperCase()).append("\n");
        bill.append("ลูกค้า\n");
        bill.append("พนักงานขาย\n");
        bill.append("----------------------------------------------\n");
        
        // --- Item List ---
        // เซ็ตความกว้าง: สินค้า(26) + จำนวน(7) + รวม(13) = 46 ช่องเป๊ะ
        bill.append(padRight("สินค้า", 26)).append(padLeft("จำนวน", 7)).append(padLeft("รวม", 13)).append("\n");
        bill.append("สินค้าโมชิ\n");

        int totalQty = 0;
        for (OrderItem item : order.getOrderItems()) {
            totalQty += item.getQuantity();
            String pId = item.getProduct().getProductId();
            String pName = truncateString(item.getProduct().getName(), 20); 
            double subTotal = item.getSubTotal();
            
            // จัดให้ตัวเลขตรงกับหัวข้อ "จำนวน" ที่กว้าง 7 ช่องพอดี
            String qtyStr = String.format("%5d  ", item.getQuantity());

            // รวม: รหัส(6) + ชื่อ(20) + จำนวน(7) + ราคา(13) = 46 ช่อง
            String line = padRight(pId, 6) + padRight(pName, 20) + qtyStr + String.format("%13.2f", subTotal);
            bill.append(line).append("\n");
        }
        bill.append("----------------------------------------------\n");

        // --- Summary & VAT Calculation ---
        double subTotal = order.calculateTotal(); 
        double grandTotal = order.calculateGrandTotal(); 
        
        double discountAmount = subTotal - grandTotal;
        if (discountAmount < 0) discountAmount = 0.0;
        
        double valueBeforeVat = grandTotal / 1.07;
        double vatAmount = grandTotal - valueBeforeVat;

        bill.append(padRight("รวมสินค้าโมชิ", 33)).append(String.format("%13.2f", subTotal)).append("\n");
        
        if (discountAmount > 0 && order.getDiscount() != null) {
            String dName = order.getDiscount().getDescription();
            bill.append("ส่วนลด: ").append(dName).append("\n");
            bill.append(padRight("  หักส่วนลด", 33)).append(String.format("-%12.2f", discountAmount)).append("\n");
        }

        bill.append(padRight("  " + totalQty + " ชิ้น", 15)).append(padRight("ยอดสุทธิ", 18)).append(String.format("%13.2f", grandTotal)).append("\n");
        
        bill.append(padRight("ภาษี 7%", 33)).append(String.format("%13.2f", vatAmount)).append("\n");
        bill.append(padRight("มูลค่าสินค้า", 33)).append(String.format("%13.2f", valueBeforeVat)).append("\n");
        bill.append(padRight("มูลค่าสินค้ารวมภาษี", 33)).append(String.format("%13.2f", grandTotal)).append("\n");
        bill.append("----------------------------------------------\n");
        
        // --- Payment ---
        if (receipt.getPayMethod().equals("Cash")) {
            bill.append(padRight("เงินสด :", 33)).append(String.format("%13.2f", receipt.getCashTendered())).append("\n");
            bill.append(padRight("เงินทอน :", 33)).append(String.format("%13.2f", receipt.getChange())).append("\n");
        } else {
            bill.append(padRight("โอนเงิน/PromptPay :", 33)).append(String.format("%13.2f", receipt.getCashTendered())).append("\n");
        }
        bill.append("----------------------------------------------\n");
        
        // --- Footer ---
        bill.append("                VAT INCLUDED                \n");
        bill.append("สินค้าขายขาด ไม่รับคืน\n");
        bill.append("สินค้าชำรุด เปลี่ยนได้ภายใน 7 วัน\n");
        bill.append("กรุณานำบิลมาด้วยทุกครั้ง\n");
        bill.append("\n          **** ขอบคุณที่ใช้บริการ **** \n");
        bill.append("==============================================\n");
        
        return bill.toString();
    }

    // เติมช่องว่างด้านขวา (ดันข้อความชิดซ้าย)
    private String padRight(String text, int width) {
        if (text == null) text = "";
        int visualLength = getVisualLength(text);
        int spacesNeeded = Math.max(0, width - visualLength);
        return text + " ".repeat(spacesNeeded);
    }

    // เติมช่องว่างด้านซ้าย (ดันข้อความชิดขวา)
    private String padLeft(String text, int width) {
        if (text == null) text = "";
        int visualLength = getVisualLength(text);
        int spacesNeeded = Math.max(0, width - visualLength);
        return " ".repeat(spacesNeeded) + text;
    }

    // เช็คสระลอย/วรรณยุกต์ (Thai zero-width characters)
    private boolean isThaiZeroWidth(char c) {
        // [แก้บั๊กสระอา] ปรับขอบเขตเพื่อไม่ให้นับรวม สระอา (า) และ สระอำ (ำ) เป็น Zero-width
        return c == '\u0E31' || 
               (c >= '\u0E34' && c <= '\u0E3A') || 
               (c >= '\u0E47' && c <= '\u0E4E');
    }
    
    // คำนวณความยาวของข้อความแบบมองเห็นจริง
    private int getVisualLength(String text) {
        if (text == null) return 0;
        int length = 0;
        for (char c : text.toCharArray()) {
            if (!isThaiZeroWidth(c)) {
                length++;
            }
        }
        return length;
    }

    // ตัดชื่อสินค้าที่ยาวเกินไป
    private String truncateString(String str, int maxVisualLength) {
        if (str == null) return "";
        if (maxVisualLength <= 0) return "";
        
        StringBuilder result = new StringBuilder();
        int visualLength = 0;
        
        for (char c : str.toCharArray()) {
            if (!isThaiZeroWidth(c)) {
                visualLength++;
            }
            if (visualLength > maxVisualLength) break;
            result.append(c);
        }
        
        return result.toString();
    }
}