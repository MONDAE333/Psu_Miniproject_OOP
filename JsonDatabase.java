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
            json.append("    \"stock\": ").append(p.stockQuantity).append(",\n");
            json.append("    \"unit\": \"").append(p.unit).append("\"\n");
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

        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"orderId\": \"").append(order.getOrderId()).append("\",\n");
        json.append("  \"timestamp\": \"").append(formattedTime).append("\",\n"); // เพิ่มเวลาการขาย
        json.append("  \"cashier\": \"").append(order.getCashier().getFirstName()).append("\",\n");
        // ถ้าต้องการเก็บยอดสุทธิหลังคิด VAT ให้เรียก order.calculateGrandTotal() ที่เราสร้างไว้รอบที่แล้ว
        json.append("  \"totalAmount\": ").append(order.calculateTotal()).append(",\n"); 
        json.append("  \"cashTendered\": ").append(order.getCashTendered()).append(",\n"); // เพิ่มเงินที่รับมา
        json.append("  \"change\": ").append(order.getChange()).append(",\n");         // เพิ่มเงินทอน
        json.append("  \"items\": [\n");
        
        for (int i = 0; i < order.getOrderItems().size(); i++) {
            OrderItem item = order.getOrderItems().get(i);
            json.append("    {\n");
            json.append("      \"productId\": \"").append(item.getProduct().getProductId()).append("\",\n");
            json.append("      \"name\": \"").append(item.getProduct().getName()).append("\",\n"); // เพิ่มชื่อสินค้า
            json.append("      \"pricePerUnit\": ").append(item.getProduct().getPrice()).append(",\n"); // ราคาต่อชิ้น ณ เวลานั้น
            json.append("      \"quantity\": ").append(item.getQuantity()).append(",\n");
            json.append("      \"subTotal\": ").append(item.getSubTotal()).append("\n"); // ราคารวมของชิ้นนี้
            json.append("    }");
            if (i < order.getOrderItems().size() - 1) json.append(","); // ใส่คอมม่าถ้าไม่ใช่ชิ้นสุดท้าย
            json.append("\n");
        }
        json.append("  ]\n}\n");

        try (FileWriter file = new FileWriter(filename, true)) {
            file.write(json.toString());
            System.out.println("[Database] Successfully saved detailed order to " + filename);
        } catch (IOException e) {
            System.out.println("[Error] Could not save order file: " + e.getMessage());
        }
    }
}