import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        System.out.println("[Manager] Added Product: " + p.getName());
    }

    public void editProduct(Product p, double newPrice) {
        p.price = newPrice;
        System.out.println("[Manager] Edited Price for " + p.getName() + " to " + newPrice);
    }

    public void deleteProduct(List<Product> db, Product p) {
        db.remove(p);
        System.out.println("[Manager] Deleted Product: " + p.getName());
    }

    public void manageDiscount(Discount d) {
        System.out.println("[Manager] Discount " + d.getCode() + " managed successfully.");
    }

    public void setPromotion(Discount d) {
        System.out.println("[Manager] Promotion set for discount code: " + d.getCode());
    }

    public void viewSales(String jsonFilePath) {
        System.out.println("\n=======================================================");
        System.out.println("                 SALES HISTORY REPORT                  ");
        System.out.println("=======================================================");
        
        try {
            List<String> lines = Files.readAllLines(Paths.get(jsonFilePath));
            boolean inItems = false;
            double grandTotalAllOrders = 0.0; // เก็บยอดขายรวมทุกบิล

            for (String line : lines) {
                line = line.trim(); // ตัดช่องว่างหัวท้ายออก
                
                // --- ส่วนข้อมูลทั่วไปของบิล ---
                if (line.startsWith("\"orderId\":")) {
                    System.out.println("\n[ ใบเสร็จรับเงิน: " + extractValue(line) + " ]");
                } 
                else if (line.startsWith("\"timestamp\":")) {
                    System.out.println("เวลา: " + extractValue(line));
                } 
                else if (line.startsWith("\"cashier\":")) {
                    System.out.println("พนักงานขาย: " + extractValue(line));
                } 
                
                // --- ส่วนเปิดรายการสินค้า ---
                else if (line.startsWith("\"items\":")) {
                    System.out.println("รายการสินค้า:");
                    inItems = true; // เปิดโหมดอ่านสินค้า
                } 
                
                // --- ส่วนอ่านรายละเอียดสินค้าในลูป ---
                else if (inItems) {
                    if (line.startsWith("\"name\":")) {
                        System.out.print("  - " + extractValue(line));
                    } else if (line.startsWith("\"pricePerUnit\":")) {
                        System.out.print(" (@" + extractValue(line) + " THB) ");
                    } else if (line.startsWith("\"quantity\":")) {
                        System.out.print("x" + extractValue(line));
                    } else if (line.startsWith("\"subTotal\":")) {
                        System.out.println(" = " + extractValue(line) + " THB");
                    } else if (line.equals("]")) { // จบ Array ของ items
                        inItems = false; // ปิดโหมดอ่านสินค้า
                    }
                } 
                
                // --- ส่วนสรุปยอดเงิน (จะอยู่นอกโหมด inItems) ---
                else if (!inItems) {
                    if (line.startsWith("\"totalAmount\":")) {
                        double total = Double.parseDouble(extractValue(line));
                        grandTotalAllOrders += total; // บวกเข้ายอดรวมทั้งหมดของร้าน
                        System.out.println("-----------------------------------");
                        System.out.println(String.format("ยอดรวมบิลนี้: %.2f THB", total));
                    } else if (line.startsWith("\"cashTendered\":")) {
                        System.out.println(String.format("รับเงินมา: %.2f THB", Double.parseDouble(extractValue(line))));
                    } else if (line.startsWith("\"change\":")) {
                        System.out.println(String.format("เงินทอน: %.2f THB", Double.parseDouble(extractValue(line))));
                        System.out.println("=======================================================");
                    }
                }
            }
            
            // สรุปยอดขายรวมทั้งหมดตอนท้าย
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
        // แก้ไขให้บวกเพิ่มอย่างเดียวตาม Requirement ถ้าจะลดค่อยทำเมนูใหม่
        if (amountToAdd < 0) {
            System.out.println("[Error] การเพิ่มสต็อกต้องเป็นค่าบวกเท่านั้น");
            return;
        }
        p.updateStock(amountToAdd);
        System.out.println("[Manager] อัปเดตสต็อกเรียบร้อย ยอดคงเหลือของ " + p.getName() + " คือ " + p.stockQuantity);
    }

    public void addCashier(List<User> userDb, Cashier c) {
        userDb.add(c);
        System.out.println("[Manager] Added new Cashier: " + c.getFirstName());
    }

    public void approveVoid(Order order) {
        order.cancelOrder();
        System.out.println("[Manager] Approved VOID for Order ID: " + order.getOrderId());
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

    public void generateInventoryReport(List<Product> db) {
        System.out.println("\n--- [ INVENTORY REPORT ] ---");
        for (Product p : db) {
            System.out.println(p.getProductId() + " | " + p.getName() + " | Stock: " + p.stockQuantity + " | Active: " + p.isActive());
        }
    }
}