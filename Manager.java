import java.util.List;
import java.util.ArrayList;

public class Manager extends User {
    protected String department;
    protected String managementLevel;
    protected String officePhoneNumber;

    public Manager(String userId, String user, String pass, String nick, String fName, String lName, String addr, String phone, String role, String dept, String level, String officePhone) {
        super(userId, user, pass, nick, fName, lName, addr, phone, role);
        this.department = dept;
        this.managementLevel = level;
        this.officePhoneNumber = officePhone;
    }

    public void addProduct(List<Product> db, Product p) {
        db.add(p);
        // ลบ System.out (ให้ Main จัดการ output)
    }

    public void editProduct(Product p, double newPrice) {
        p.setPrice(newPrice);
        // ลบ System.out (ให้ Main จัดการ output)
    }

    public void deleteProduct(List<Product> db, Product p) {
        db.remove(p);
        // ลบ System.out (ให้ Main จัดการ output)
    }

    public void manageDiscount(Discount d) {
        // ลบ System.out (ให้ Main จัดการ output)
    }

    public void setPromotion(Discount d) {
        // ลบ System.out (ให้ Main จัดการ output)
    }

    public void viewSales(String jsonFilePath) {
        System.out.println("\n=======================================================");
        System.out.println("                 SALES HISTORY REPORT                  ");
        System.out.println("=======================================================");
        
        try {
            java.util.List<String> lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get(jsonFilePath));
            boolean inItems = false;
            double grandTotalAllOrders = 0.0; 

            // ตัวแปรพักข้อมูล
            String tempTotal = "";
            String tempCash = "";
            String tempChange = "";
            String tempDiscount = ""; // [เพิ่ม] เก็บส่วนลด
            String tempVat = "";      // [เพิ่ม] เก็บภาษี

            for (String line : lines) {
                line = line.trim(); 
                
                if (line.startsWith("\"orderId\":")) {
                    System.out.println("\n[ ใบเสร็จรับเงิน: " + extractValue(line) + " ]");
                    // รีเซ็ตค่าทุกครั้งที่ขึ้นบิลใหม่
                    tempTotal = ""; tempCash = ""; tempChange = ""; tempDiscount = ""; tempVat = "";
                } 
                else if (line.startsWith("\"timestamp\":")) {
                    System.out.println("เวลา: " + extractValue(line));
                } 
                else if (line.startsWith("\"cashier\":")) {
                    System.out.println("พนักงานขาย: " + extractValue(line));
                } 
                
                // --- ดึงข้อมูลที่เพิ่งเพิ่มเข้ามาใหม่จาก JSON ---
                else if (line.startsWith("\"discount\":")) {
                    tempDiscount = extractValue(line);
                }
                else if (line.startsWith("\"vatAmount\":")) {
                    tempVat = extractValue(line);
                }
                else if (line.startsWith("\"totalAmount\":")) {
                    tempTotal = extractValue(line);
                    grandTotalAllOrders += Double.parseDouble(tempTotal);
                } 
                else if (line.startsWith("\"cashTendered\":")) {
                    tempCash = extractValue(line);
                } 
                else if (line.startsWith("\"change\":")) {
                    tempChange = extractValue(line);
                } 
                
                // --- ส่วนเปิดรายการสินค้า ---
                else if (line.startsWith("\"items\":")) {
                    System.out.println("รายการสินค้า:");
                    inItems = true; 
                } 
                else if (inItems) {
                    if (line.startsWith("\"name\":")) {
                        System.out.print("  - " + extractValue(line));
                    } else if (line.startsWith("\"pricePerUnit\":")) {
                        System.out.print(" (@" + extractValue(line) + " THB) ");
                    } else if (line.startsWith("\"quantity\":")) {
                        System.out.print("x" + extractValue(line));
                    } else if (line.startsWith("\"subTotal\":")) {
                        System.out.println(" = " + extractValue(line) + " THB");
                    } 
                    else if (line.startsWith("]")) { 
                        inItems = false; 
                        
                        System.out.println("-----------------------------------");
                        
                        // [แก้ไข] นำข้อมูล VAT และ Discount มา Print ก่อนยอดรวม
                        if (!tempDiscount.isEmpty() && Double.parseDouble(tempDiscount) > 0) {
                            System.out.println(String.format("ส่วนลด (Discount): %.2f THB", Double.parseDouble(tempDiscount)));
                        }
                        if (!tempVat.isEmpty()) {
                            System.out.println(String.format("ภาษี (VAT 7%%): %.2f THB", Double.parseDouble(tempVat)));
                        }
                        if (!tempTotal.isEmpty()) {
                            System.out.println(String.format("ยอดรวมสุทธิ (Grand Total): %.2f THB", Double.parseDouble(tempTotal)));
                        }
                        
                        System.out.println("-----------------------------------");
                        if (!tempCash.isEmpty())  System.out.println(String.format("รับเงินมา: %.2f THB", Double.parseDouble(tempCash)));
                        if (!tempChange.isEmpty()) System.out.println(String.format("เงินทอน: %.2f THB", Double.parseDouble(tempChange)));
                        System.out.println("=======================================================");
                    }
                } 
            }
            
            System.out.println("\n>>> ยอดขายรวมทั้งหมด (Total Revenue): " + String.format("%.2f", grandTotalAllOrders) + " THB <<<");
            
        } catch (Exception e) {
            System.out.println("[System] ยังไม่มีข้อมูลการขาย หรือไม่สามารถอ่านไฟล์ได้ (" + e.getMessage() + ")");
        }
    }

    // อัปเกรดฟังก์ชันแยกค่า เพื่อให้รองรับค่าที่มีจุดทศนิยมหรือค่าว่างได้ดีขึ้น
    private String extractValue(String line) {
        String[] parts = line.split(":", 2); // แบ่งแค่ 2 ส่วน ป้องกัน Error ถ้ารายละเอียดมีเครื่องหมาย :
        if (parts.length > 1) {
            return parts[1].replace("\"", "").replace(",", "").trim();
        }
        return "";
    }

    public String generateNextProductId(List<Product> db, String categoryPrefix) {
        int maxId = 0;
        String prefix = categoryPrefix.toUpperCase(); // บังคับเป็นตัวพิมพ์ใหญ่ เช่น p1 -> P1
        
        for (Product p : db) {
            // ค้นหาสินค้าที่รหัสขึ้นต้นด้วย P1, P2 หรือ P3
            if (p.getProductId().toUpperCase().startsWith(prefix)) {
                try {
                    // ตัดเอาเฉพาะตัวเลขด้านหลังมาเช็ค เช่น P105 -> ตัด P1 ออก เหลือ 05 -> แปลงเป็นเลข 5
                    int idNum = Integer.parseInt(p.getProductId().substring(prefix.length()));
                    if (idNum > maxId) {
                        maxId = idNum; // เก็บค่าที่มากที่สุดไว้
                    }
                } catch (Exception e) { 
                    /* ข้ามรายการที่ตัวเลขด้านหลังแปลงค่าไม่ได้ ป้องกันโปรแกรมพัง */ 
                }
            }
        }
        // นำค่าที่มากที่สุดมา +1 แล้วจัดฟอร์แมตให้เป็น 2 หลัก (เช่น 1 -> 01)
        return prefix + String.format("%02d", maxId + 1); 
    }

    public void manageStock(Product p, int amountToAdd) {
        if (amountToAdd < 0) {
            // Error: must be positive value - ให้ Main จัดการ output
            return;
        }
        p.updateStock(amountToAdd);
        // ลบ System.out (ให้ Main จัดการ output)
    }

    public void addCashier(List<User> userDb, Cashier c) {
        userDb.add(c);
        // ลบ System.out (ให้ Main จัดการ output)
    }

    public void approveVoid(Order order) {
        order.cancelOrder();
        // ลบ System.out (ให้ Main จัดการ output)
    }

    public List<Product> searchProduct(String keyword, List<Product> db) {
        List<Product> results = new ArrayList<>();
        for (Product p : db) {
            if (p.getName().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(p);
            }
        }
        return results;
    }

    public String generateInventoryReport(List<Product> db) {
        StringBuilder report = new StringBuilder();
        report.append("\n--- [ INVENTORY REPORT ] ---\n");
        for (Product p : db) {
            report.append(p.getProductId()).append(" | ").append(p.getName())
                   .append(" | Stock: ").append(p.getStockQuantity()).append(" | Active: ").append(p.isActive())
                   .append("\n");
        }
        return report.toString();
    }
}