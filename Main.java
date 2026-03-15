import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    static List<User> usersDb = new ArrayList<>();
    static List<Product> productsDb = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        setupMockDatabase();

        while (true) {
            System.out.println("\n=======================================");
            System.out.println("      SnapPay MOSHI - System Login");
            System.out.println("=======================================");
            System.out.print("> Username (exit to quit): ");
            String username = scanner.nextLine();
            if (username.equalsIgnoreCase("exit")) {
                System.out.println("[System] Shutting down...");
                break;
            }
            
            System.out.print("> Password: ");
            String password = scanner.nextLine();

            User loggedInUser = null;
            for (User u : usersDb) {
                if (u.login(username, password)) { 
                    loggedInUser = u; 
                    break; 
                }
            }

            if (loggedInUser != null) {
                if (loggedInUser instanceof Manager) {
                    showManagerMenu((Manager) loggedInUser);
                } else if (loggedInUser instanceof Cashier) {
                    showCashierMenu((Cashier) loggedInUser);
                }
            } else {
                System.out.println("[Error] Invalid Username or Password. Please try again.");
            }
        }
        scanner.close();
    }

    // ==========================================
    // เมนูของผู้จัดการ (MANAGER MENU) - แก้ไข Error แล้ว
    // ==========================================
    private static void showManagerMenu(Manager manager) {
        while (true) {
            System.out.println("\n=== MANAGER MENU (" + manager.getFirstName() + ") ===");
            System.out.println("1. Add Product (เพิ่มสินค้า)");
            System.out.println("2. Manage Stock (เพิ่มสต็อก)"); // เปลี่ยนคำอธิบายเพราะเราไม่ให้ลดแล้ว
            System.out.println("3. View Inventory Report (ดูสต็อกทั้งหมด)");
            System.out.println("4. Search Product (ค้นหาสินค้า)");
            System.out.println("5. View Sales Report (ดูยอดขายรวม)");
            System.out.println("0. Logout");
            System.out.print("> Choice: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                System.out.println("\n--- [ Add New Product ] ---");
                System.out.println("เลือกหมวดหมู่สินค้า:");
                System.out.println("  P1 = Doll (ตุ๊กตา)");
                System.out.println("  P2 = Pen (เครื่องเขียน)");
                System.out.println("  P3 = Lifestyle (ของใช้)");
                System.out.print("> ใส่รหัสหมวดหมู่ (P1/P2/P3) หรือพิมพ์ 'cancel' เพื่อยกเลิก: ");
                
                String prefix = scanner.nextLine().trim().toUpperCase();

                // 1. เช็คว่าต้องการยกเลิกหรือไม่
                if (prefix.equals("CANCEL")) {
                    System.out.println("[System] ยกเลิกการเพิ่มสินค้า กลับสู่เมนูหลัก");
                    continue; // ข้ามการทำงานที่เหลือแล้วกลับไปเริ่มลูป while ใหม่
                }

                // 2. ดักจับ Error ถ้าใส่รหัสหมวดหมู่ไม่ถูกต้อง
                if (!prefix.equals("P1") && !prefix.equals("P2") && !prefix.equals("P3")) {
                    System.out.println("[Error] รหัสหมวดหมู่ไม่ถูกต้อง กรุณาเลือก P1, P2 หรือ P3");
                    continue; // ให้กลับไปเลือกเมนูใหม่
                }

                // 3. เรียกใช้ฟังก์ชันรันรหัสอัตโนมัติ
                String id = manager.generateNextProductId(productsDb, prefix);
                System.out.println("[System] รหัสสินค้าของคุณคือ: " + id);

                System.out.print("Name: "); 
                String name = scanner.nextLine();
                double price = getDoubleInput("Price: ", 0.0); 
                int stock = getIntInput("Initial Stock: ", 0); 
                System.out.print("Unit (e.g., ชิ้น, ห่อ): "); 
                String unit = scanner.nextLine();
                
                Product p = null;
                
                // 4. สร้าง Object ตามหมวดหมู่ที่ถูกต้อง (Polymorphism)
                if (prefix.equals("P1")) {
                    p = new Doll(id, name, price, stock, unit, "25cm", "General");
                } else if (prefix.equals("P2")) {
                    p = new Stationery(id, name, price, stock, unit, "Gel", "Standard");
                } else if (prefix.equals("P3")) {
                    p = new Lifestyle(id, name, price, stock, unit, "Standard");
                }

                if (p != null) {
                    manager.addProduct(productsDb, p);
                    JsonDatabase.saveProductsToJson(productsDb, "products.json");
                    System.out.println("[System] เพิ่มสินค้า " + name + " สำเร็จ!");
                }
            }
            else if (choice.equals("2")) {
                System.out.print("Enter Product ID to manage: ");
                Product p = findProductById(scanner.nextLine());
                if (p != null) {
                    System.out.println("[System] Found: " + p.getDetails() + " | Current Stock: " + p.stockQuantity);
                    
                    // [FIX 3] เปลี่ยนข้อความและใส่ค่า min = 1 (บังคับให้เพิ่มอย่างเดียว ห้ามลดหรือใส่ 0)
                    int amount = getIntInput("Amount to ADD (ระบุจำนวนที่ต้องการเพิ่ม): ", 1); 
                    
                    manager.manageStock(p, amount);
                    JsonDatabase.saveProductsToJson(productsDb, "products.json");
                } else {
                    System.out.println("[Error] Product not found.");
                }
            }
            else if (choice.equals("3")) {
                System.out.println("\n--- [ Check Price & Stock ] ---");
                while(true) {
                    System.out.print("> Enter Product ID (หรือพิมพ์ 'exit' เพื่อกลับเมนูหลัก): ");
                    String input = scanner.nextLine().trim();
                    
                    if (input.equalsIgnoreCase("exit")) break;
                    
                    Product p = findProductById(input);
                    if (p != null) {
                        System.out.println("Details : " + p.getDetails()); 
                        
                        // [แก้ไขตรงนี้] เรียก p.getPrice() และ p.stockQuantity แทนการผ่าน cashier
                        System.out.println(String.format("Price   : %.2f THB / %s", p.getPrice(), p.unit));
                        System.out.println("Stock   : " + p.stockQuantity + " " + p.unit);
                        
                        System.out.println("-----------------------------------");
                    } else {
                        System.out.println("[Error] ไม่พบรหัสสินค้านี้ในระบบ");
                        System.out.println("-----------------------------------");
                    }
                }
            }
            else if (choice.equals("4")) {
                System.out.print("Enter Keyword to search: ");
                List<Product> res = manager.searchProduct(scanner.nextLine(), productsDb);
                System.out.println("\n--- [ Search Results ] ---");
                if (res.isEmpty()) System.out.println("No products found.");
                for (Product p : res) {
                    System.out.println("- ID: " + p.getProductId() + " | " + p.getDetails() + " | Price: " + p.getPrice());
                }
            }
            else if (choice.equals("5")) {
                // [FIX 4] ลบ Try-Catch อ่านไฟล์แบบเก่าทิ้ง เพราะเราย้าย Logic ไปไว้ใน Manager แล้ว
                // และส่งชื่อไฟล์เข้าไปให้ฟังก์ชัน viewSales() เพื่อให้มันทำงานได้ถูกต้อง
                manager.viewSales("order_database.json"); 
            }
            else if (choice.equals("0")) { 
                manager.logout(); 
                break; 
            }
            else {
                System.out.println("[Error] Invalid choice.");
            }
        }
    }

    // ==========================================
    // เมนูของแคชเชียร์ (CASHIER MENU)
    // ==========================================
    private static void showCashierMenu(Cashier cashier) {
        while (true) {
            System.out.println("\n=== CASHIER MENU (" + cashier.getFirstName() + " - Counter " + cashier.counterNumber + ") ===");
            System.out.println("1. Open Shift (เปิดกะ)");
            System.out.println("2. Create Order (ขายสินค้า)");
            System.out.println("3. Check Price & Stock (เช็คสินค้า)");
            System.out.println("4. Close Shift & X-Report (ปิดกะ)");
            System.out.println("0. Logout");
            System.out.print("> Choice: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) cashier.openShift();
            else if (choice.equals("2")) {
                if (cashier.isShiftOpen()) processNewOrder(cashier);
                else System.out.println("[Error] Please Open Shift first!");
            }
            else if (choice.equals("3")) {
                System.out.print("> Enter Product ID: ");
                Product p = findProductById(scanner.nextLine());
                if (p != null) {
                    System.out.println("Details : " + p.getDetails()); 
                    System.out.println("Price   : " + cashier.checkProductPrice(p) + " THB / " + p.unit);
                    System.out.println("Stock   : " + cashier.checkProductStock(p) + " " + p.unit);
                } else {
                    System.out.println("[Error] Product ID not found.");
                }
            }
            else if (choice.equals("4")) cashier.closeShift();
            else if (choice.equals("0")) { 
                if (cashier.isShiftOpen()) {
                    System.out.println("[Warning] Please close your shift before logging out!");
                } else {
                    cashier.logout(); 
                    break; 
                }
            }
            else {
                System.out.println("[Error] Invalid choice.");
            }
        }
    }

    // ==========================================
    // ระบบตะกร้าและการคิดเงิน (Order Processing)
    // ==========================================
    // --- ส่วนของ processNewOrder (ตะกร้าและการคิดเงิน) ---
    private static void processNewOrder(Cashier cashier) {
        Order order = cashier.createOrder("ORD-" + System.currentTimeMillis());
        System.out.println("\n--- [ NEW ORDER ] ---");
        
        while (true) {
            System.out.println("\nคำสั่ง: [รหัสสินค้า] = เพิ่มลงตะกร้า | [LIST] = ดูสินค้าทั้งหมด | [REMOVE รหัส] = ลบสินค้า | [PAY] = ชำระเงิน | [CANCEL] = ยกเลิกบิล");
            System.out.print("> ใส่คำสั่ง: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("PAY")) break;
            if (input.equalsIgnoreCase("CANCEL")) {
                order.cancelOrder();
                System.out.println("[System] ยกเลิกออเดอร์แล้ว");
                return;
            }
            if (input.equalsIgnoreCase("LIST")) {
                System.out.println("\n--- รายการสินค้าที่มี ---");
                for (Product p : productsDb) {
                    System.out.println(p.getProductId() + " : " + p.getDetails() + " | คงเหลือ: " + p.stockQuantity + " " + p.unit + " | ราคา: " + p.getPrice());
                }
                continue;
            }
            if (input.toUpperCase().startsWith("REMOVE ")) {
                String pidToRemove = input.substring(7).trim();
                Product pToRemove = findProductById(pidToRemove);
                if (pToRemove != null) {
                    order.removeItemFromCart(pToRemove);
                    System.out.println("[System] ลบ " + pToRemove.getName() + " ออกจากตะกร้าแล้ว");
                } else {
                    System.out.println("[Error] ไม่พบรหัสสินค้านี้ในระบบ");
                }
                continue;
            }

            Product p = findProductById(input);
            if (p != null) {
                System.out.println("พบสินค้า: " + p.getDetails() + " | " + p.getPrice() + " THB (คงเหลือ: " + p.stockQuantity + ")");
                int qty = getIntInput("ระบุจำนวนที่ต้องการซื้อ: ", 1);
                
                // เช็คสต็อกก่อนเพิ่มลงตะกร้า
                if (qty <= p.stockQuantity) {
                    order.addItemToCart(p, qty);
                    System.out.println("[System] เพิ่มลงตะกร้าสำเร็จ. ยอดรวมชั่วคราว: " + order.calculateTotal() + " THB");
                } else {
                    System.out.println("[Error] สต็อกไม่เพียงพอ! (เหลือเพียง " + p.stockQuantity + " " + p.unit + ")");
                }
            } else {
                System.out.println("[Error] คำสั่งไม่ถูกต้อง หรือไม่พบรหัสสินค้านี้");
            }
        }

        if (order.getOrderItems().size() > 0) {
            System.out.println("\n--- [ CHECKOUT ] ---");
            order.printOrderSummary(); // สร้าง Method นี้ใน Order.java เพื่อโชว์ลิสต์ตอนจบ
            
            double subTotal = order.calculateTotal();
            System.out.println("-----------------------------------");
            System.out.println(String.format("ยอดรวมชั่วคราว (Subtotal): %.2f THB", subTotal));
            System.out.println("-----------------------------------");
            
            System.out.print("มีโค้ดส่วนลดหรือไม่? (ปล่อยว่างถ้าไม่มี): ");
            String dCode = scanner.nextLine().trim();
            if (!dCode.isEmpty()) {
                order.applyDiscount(new Discount(dCode, 20.0)); // สมมติลด 20 บาท
            }

            // คำนวณยอดสุทธิ (Grand Total = (Subtotal - Discount) + VAT 7%)
            double grandTotal = order.calculateGrandTotal(); 
            System.out.println(String.format("ยอดรวมสุทธิ (รวม VAT 7%%): %.2f THB", grandTotal));
            
            // เลือกช่องทางการจ่ายเงิน
            System.out.println("ช่องทางการชำระเงิน: 1. Cash (เงินสด)  2. PromptPay (พร้อมเพย์)");
            System.out.print("> เลือก: ");
            String payMethod = scanner.nextLine().equals("2") ? "PromptPay" : "Cash";
            
            double cash = 0;
            double change = 0;
            boolean paymentSuccess = false;

            // วนลูปจนกว่าจะจ่ายเงินครบ หรือขอยกเลิก
            while (!paymentSuccess) {
                if (payMethod.equals("Cash")) {
                    cash = getDoubleInput("รับเงินสดมา (THB) [พิมพ์ -1 เพื่อยกเลิกบิล]: ", -1);
                    if (cash == -1) {
                        order.cancelOrder();
                        System.out.println("[System] ยกเลิกบิลแล้ว");
                        return;
                    }
                    
                    change = cashier.processPayment(cash, grandTotal);
                    
                    if (change >= 0) {
                        paymentSuccess = true;
                    } else {
                        // change ติดลบอยู่แล้ว ใช้ Math.abs() เพื่อทำให้เป็นบวกตอนแสดงผล
                        // และใช้ String.format เพื่อแสดงทศนิยม 2 หลัก
                        System.out.println(String.format("[Error] เงินไม่พอ! ขาดอีก %.2f THB", Math.abs(change)));
                    }
                } else {
                    System.out.println("[System] กำลังสร้าง QR Code... (สมมติว่าจ่ายสำเร็จ)");
                    cash = grandTotal;
                    change = 0;
                    paymentSuccess = true;
                }
            }

            if (order.processCheckout()) { 
                order.setPaymentDetails(cash, change);
                System.out.println(String.format("[System] ชำระเงินสำเร็จ เงินทอน: %.2f THB", change));
                Receipt receipt = new Receipt("REC-" + System.currentTimeMillis(), order, payMethod, cash, change);
                receipt.printBill();
                cashier.recordSoldItems(order.getOrderItems());
                JsonDatabase.saveOrderToJson(order, "order_database.json");
                JsonDatabase.saveProductsToJson(productsDb, "products.json");
            }
        } else {
            System.out.println("[System] ตะกร้าว่างเปล่า ยกเลิกออเดอร์");
        }
    }

    // ==========================================
    // ฟังก์ชันช่วยเหลือป้องกันโปรแกรมพัง (Error Handlers)
    // ==========================================
    private static Product findProductById(String id) {
        for (Product p : productsDb) {
            if (p.getProductId().equalsIgnoreCase(id)) return p;
        }
        return null;
    }

    // ฟังก์ชันช่วยเหลือป้องกันโปรแกรมพัง และป้องกันค่าติดลบ
    private static int getIntInput(String prompt, int min) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine());
                if (value < min) {
                    System.out.println("[Error] ค่าที่ใส่ต้องไม่น้อยกว่า " + min + " กรุณาใส่ใหม่");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("[Error] กรุณาใส่ตัวเลขจำนวนเต็มที่ถูกต้อง");
            }
        }
    }

    private static double getDoubleInput(String prompt, double min) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine());
                if (value < min) {
                    System.out.println("[Error] ค่าที่ใส่ต้องไม่น้อยกว่า " + min + " กรุณาใส่ใหม่");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("[Error] กรุณาใส่ตัวเลขทศนิยมที่ถูกต้อง");
            }
        }
    }

    private static void setupMockDatabase() {
        usersDb.add(new Manager("6810210428", "admin_kit", "1234", "Kit", "Kittapon", "W", "Songkhla", "081", "Manager", "IT", "StoreMgr", "0200"));
        usersDb.add(new Cashier("6810210533", "cashier_n", "5678", "Niphas", "Niphas", "P", "Songkhla", "082", "Cashier", 1));
        
        productsDb = JsonDatabase.loadProductsFromJson("products.json");
        if (productsDb.isEmpty()) {
            productsDb.add(new Doll("P101", "Care Bears", 399.0, 15, "ตัว", "25cm", "Cheer"));
            productsDb.add(new Stationery("P201", "Moshi Pen", 20.0, 50, "ด้าม", "Gel", "Blue"));
            productsDb.add(new Lifestyle("P301", "Sunscreen", 129.0, 20, "หลอด", "50ml"));
            JsonDatabase.saveProductsToJson(productsDb, "products.json");
        }
    }
}