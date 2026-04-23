import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;

public class JsonDatabase {
    
    // ==========================================
    // 1. จัดการข้อมูลสต็อกสินค้า (Products)
    // ==========================================
    public static void saveProductsToJson(List<Product> products, String filename) {
        StringBuilder json = new StringBuilder();
        json.append("[\n");
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            json.append("  {\n");
            json.append("    \"id\": \"").append(p.getProductId()).append("\",\n");
            json.append("    \"name\": \"").append(p.getName()).append("\",\n");
            json.append("    \"price\": ").append(p.getPrice()).append(",\n");
            json.append("    \"stock\": ").append(p.getStockQuantity()).append(",\n");
            json.append("    \"unit\": \"").append(p.getUnit()).append("\"\n");
            json.append("  }");
            if (i < products.size() - 1) json.append(",");
            json.append("\n");
        }
        json.append("]\n");

        try (FileWriter file = new FileWriter(filename)) {
            file.write(json.toString());
        } catch (IOException e) {
            System.out.println("[Error] Save products failed.");
        }
    }

    public static List<Product> loadProductsFromJson(String filename) {
        List<Product> products = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filename));
            String id = "", name = "", unit = "";
            double price = 0;
            int stock = 0;

            for (String line : lines) {
                if (line.contains("\"id\":")) id = extractValue(line);
                else if (line.contains("\"name\":")) name = extractValue(line);
                else if (line.contains("\"price\":")) price = Double.parseDouble(extractValue(line));
                else if (line.contains("\"stock\":")) stock = Integer.parseInt(extractValue(line));
                else if (line.contains("\"unit\":")) unit = extractValue(line);
                else if (line.contains("}")) {
                    if (id.startsWith("P1")) products.add(new Doll(id, name, price, stock, unit, "25cm", "Collection"));
                    else if (id.startsWith("P3")) products.add(new Lifestyle(id, name, price, stock, unit, "50ml"));
                    else products.add(new Stationery(id, name, price, stock, unit, "Pen", "Blue"));
                }
            }
        } catch (Exception e) { 
            // ถ้าไม่เจอไฟล์ จะส่ง List ว่างกลับไปให้ Main สร้างข้อมูลจำลอง
        }
        return products;
    }

    private static String extractValue(String line) {
        String[] parts = line.split(":");
        if (parts.length > 1) return parts[1].replace("\"", "").replace(",", "").trim();
        return "";
    }

    // ==========================================
    // 2. จัดการข้อมูลการขายและยอดขาย (Orders)
    // ==========================================
    public static void saveOrderToJson(Order order, String filename) {
        // จัด Format เวลาให้อ่านง่าย เช่น 2023-10-25 14:30:00
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = order.getTimestamp().format(formatter);

        // --- 1. คำนวณยอดต่างๆ ก่อนนำไปเซฟ ---
        double grandTotal = order.calculateGrandTotal(); // ยอดสุทธิรวม VAT
        double subTotal = order.calculateTotal();        // ยอดรวมก่อนหักส่วนลด
        double discount = subTotal - grandTotal;         // หาส่วนต่างของส่วนลด
        if (discount < 0) discount = 0;                  // ป้องกันค่าติดลบ
        
        double valueBeforeVat = grandTotal / 1.07;       // มูลค่าก่อนภาษี
        double vatAmount = grandTotal - valueBeforeVat;  // ภาษีมูลค่าเพิ่ม 7%

        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"orderId\": \"").append(order.getOrderId()).append("\",\n");
        json.append("  \"timestamp\": \"").append(formattedTime).append("\",\n");
        json.append("  \"cashier\": \"").append(order.getCashier().getFirstName()).append("\",\n");
        
        // --- 2. เซฟตัวแปรใหม่ลงใน JSON (ใช้ Locale.US เพื่อป้องกันปัญหาจุดทศนิยมกลายเป็นลูกน้ำ) ---
        json.append("  \"subTotal\": \"").append(String.format(java.util.Locale.US, "%.2f", subTotal)).append("\",\n");
        json.append("  \"discount\": \"").append(String.format(java.util.Locale.US, "%.2f", discount)).append("\",\n");
        json.append("  \"vatAmount\": \"").append(String.format(java.util.Locale.US, "%.2f", vatAmount)).append("\",\n");
        
        // เปลี่ยนมาใช้ grandTotal เพื่อเก็บยอดสุทธิที่ลูกค้าจ่ายจริง
        json.append("  \"totalAmount\": \"").append(String.format(java.util.Locale.US, "%.2f", grandTotal)).append("\",\n"); 
        
        json.append("  \"cashTendered\": \"").append(String.format(java.util.Locale.US, "%.2f", order.getCashTendered())).append("\",\n");
        json.append("  \"change\": \"").append(String.format(java.util.Locale.US, "%.2f", order.getChange())).append("\",\n");
        
        json.append("  \"items\": [\n");
        
        for (int i = 0; i < order.getOrderItems().size(); i++) {
            OrderItem item = order.getOrderItems().get(i);
            json.append("    {\n");
            json.append("      \"productId\": \"").append(item.getProduct().getProductId()).append("\",\n");
            json.append("      \"name\": \"").append(item.getProduct().getName()).append("\",\n");
            json.append("      \"pricePerUnit\": ").append(item.getProduct().getPrice()).append(",\n");
            json.append("      \"quantity\": ").append(item.getQuantity()).append(",\n");
            json.append("      \"subTotal\": ").append(item.getSubTotal()).append("\n");
            json.append("    }");
            if (i < order.getOrderItems().size() - 1) json.append(","); // ใส่คอมม่าถ้าไม่ใช่ชิ้นสุดท้าย
            json.append("\n");
        }
        json.append("  ]\n}\n");

        try (java.io.FileWriter file = new java.io.FileWriter(filename, true)) {
            file.write(json.toString());
            System.out.println("[Database] Successfully saved detailed order to " + filename);
        } catch (java.io.IOException e) {
            System.out.println("[Error] Could not save order file: " + e.getMessage());
        }
    }

    // ===================================================
    // เพิ่ม 3 เมธอดนี้ลงในไฟล์ JsonDatabase.java
    // ===================================================

    public static void saveUsersToJson(List<User> users, String filePath) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            sb.append("  {\n");
            // ดึงข้อมูลพื้นฐานของ User ทุกคน
            sb.append("    \"role\": \"").append(u.getRole()).append("\",\n");
            sb.append("    \"userId\": \"").append(u.getUserId()).append("\",\n");
            sb.append("    \"username\": \"").append(u.getUsername()).append("\",\n");
            sb.append("    \"password\": \"").append(u.getPassword()).append("\",\n");
            sb.append("    \"nickname\": \"").append(u.getNickname()).append("\",\n");
            sb.append("    \"firstName\": \"").append(u.getFirstName()).append("\",\n");
            sb.append("    \"lastName\": \"").append(u.getLastName()).append("\",\n");
            sb.append("    \"address\": \"").append(u.getAddress()).append("\",\n");
            sb.append("    \"phone\": \"").append(u.getPhone()).append("\"");

            // ถ้าเป็น Manager ให้เซฟข้อมูลแผนกเพิ่ม
            if (u instanceof Manager) {
                Manager m = (Manager) u;
                // หาก error ตรง m.department ให้ใช้ Getter เช่น m.getDepartment() แทน
                sb.append(",\n    \"department\": \"").append(m.department).append("\",\n");
                sb.append("    \"managementLevel\": \"").append(m.managementLevel).append("\",\n");
                sb.append("    \"officePhone\": \"").append(m.officePhoneNumber).append("\"\n");
            } 
            // ถ้าเป็น Cashier ให้เซฟหมายเลขเคาน์เตอร์เพิ่ม
            else if (u instanceof Cashier) {
                Cashier c = (Cashier) u;
                sb.append(",\n    \"counterNumber\": ").append(c.getCounterNumber()).append("\n");
            } else {
                sb.append("\n");
            }

            sb.append("  }");
            if (i < users.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]\n");

        try {
            java.nio.file.Files.write(java.nio.file.Paths.get(filePath), sb.toString().getBytes());
        } catch (Exception e) {
            System.out.println("[Error] ไม่สามารถเซฟไฟล์ users.json ได้: " + e.getMessage());
        }
    }
    // ==========================================
    // ระบบ Save / Load ส่วนลดโปรโมชั่น
    // ==========================================
    public static void saveDiscountsToJson(java.util.List<Discount> discounts, String filePath) {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < discounts.size(); i++) {
            Discount d = discounts.get(i);
            sb.append("  {\n");
            sb.append("    \"code\": \"").append(d.getCode()).append("\",\n");
            sb.append("    \"description\": \"").append(d.getDescription()).append("\",\n");
            sb.append("    \"minAmount\": ").append(d.getMinAmount()).append(",\n");
            sb.append("    \"percentage\": ").append(d.getPercentage()).append(",\n");
            sb.append("    \"expirationDate\": \"").append(d.getExpirationDate().toString()).append("\"\n");
            sb.append("  }");
            if (i < discounts.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]\n");

        try (java.io.FileWriter file = new java.io.FileWriter(filePath)) {
            file.write(sb.toString());
        } catch (Exception e) {
            System.out.println("[Error] ไม่สามารถเซฟ discounts.json ได้: " + e.getMessage());
        }
    }

    public static java.util.List<Discount> loadDiscountsFromJson(String filePath) {
        java.util.List<Discount> list = new java.util.ArrayList<>();
        try {
            if (!java.nio.file.Files.exists(java.nio.file.Paths.get(filePath))) return list;
            java.util.List<String> lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get(filePath));
            
            String code = "", desc = "", expDateStr = "";
            double min = 0.0, percent = 0.0;

            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("\"code\":")) code = extractValue(line);
                else if (line.startsWith("\"description\":")) desc = extractValue(line);
                else if (line.startsWith("\"minAmount\":")) min = Double.parseDouble(extractValue(line).replace(",", ""));
                else if (line.startsWith("\"percentage\":")) percent = Double.parseDouble(extractValue(line).replace(",", ""));
                else if (line.startsWith("\"expirationDate\":")) expDateStr = extractValue(line);
                else if (line.equals("}") || line.equals("},")) {
                    java.time.LocalDate expDate = java.time.LocalDate.parse(expDateStr);
                    list.add(new Discount(code, desc, min, percent, expDate));
                }
            }
        } catch (Exception e) {
            System.out.println("[Error] โหลดข้อมูลส่วนลดไม่สำเร็จ: " + e.getMessage());
        }
        return list;
    }

    public static List<User> loadUsersFromJson(String filePath) {
        List<User> list = new java.util.ArrayList<>();
        try {
            // ถ้ายังไม่มีไฟล์ ให้คืนค่าลิสต์ว่างกลับไป
            if (!java.nio.file.Files.exists(java.nio.file.Paths.get(filePath))) return list;

            java.util.List<String> lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get(filePath));
            String role = "", id = "", user = "", pass = "", nick = "", fname = "", lname = "", addr = "", phone = "";
            String dept = "", level = "", office = "";
            int counter = 1;

            for (String line : lines) {
                line = line.trim();
                if (line.startsWith("\"role\":")) role = extractUserValue(line);
                else if (line.startsWith("\"userId\":")) id = extractUserValue(line);
                else if (line.startsWith("\"username\":")) user = extractUserValue(line);
                else if (line.startsWith("\"password\":")) pass = extractUserValue(line);
                else if (line.startsWith("\"nickname\":")) nick = extractUserValue(line);
                else if (line.startsWith("\"firstName\":")) fname = extractUserValue(line);
                else if (line.startsWith("\"lastName\":")) lname = extractUserValue(line);
                else if (line.startsWith("\"address\":")) addr = extractUserValue(line);
                else if (line.startsWith("\"phone\":")) phone = extractUserValue(line);
                else if (line.startsWith("\"department\":")) dept = extractUserValue(line);
                else if (line.startsWith("\"managementLevel\":")) level = extractUserValue(line);
                else if (line.startsWith("\"officePhone\":")) office = extractUserValue(line);
                else if (line.startsWith("\"counterNumber\":")) counter = Integer.parseInt(extractUserValue(line).replace(",", ""));
                else if (line.equals("}") || line.equals("},")) {
                    // ประกอบร่าง Object คืนชีพกลับมาตาม Role
                    if (role.equals("Manager")) {
                        list.add(new Manager(id, user, pass, nick, fname, lname, addr, phone, role, dept, level, office));
                    } else if (role.equals("Cashier")) {
                        list.add(new Cashier(id, user, pass, nick, fname, lname, addr, phone, role, counter));
                    }
                    // ล้างค่าตัวแปรเพื่อรออ่านคนถัดไป
                    role=""; id=""; user=""; pass=""; nick=""; fname=""; lname=""; addr=""; phone="";
                    dept=""; level=""; office=""; counter=1;
                }
            }
        } catch (Exception e) {
            System.out.println("[Error] โหลดข้อมูลพนักงานไม่สำเร็จ: " + e.getMessage());
        }
        return list;
    }

    // ฟังก์ชันช่วยแยกข้อความ JSON (ใส่ไว้ใน JsonDatabase.java)
    private static String extractUserValue(String line) {
        String[] parts = line.split(":", 2);
        if (parts.length > 1) {
            return parts[1].replace("\"", "").replace(",", "").trim();
        }
        return "";
    }
}

    