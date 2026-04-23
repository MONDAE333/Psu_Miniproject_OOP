import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    static List<User> usersDb = new ArrayList<>();
    static List<Product> productsDb = new ArrayList<>();
    static List<Discount> discountsDb = new ArrayList<>();
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
    // เมนูของผู้จัดการ (MANAGER MENU)
    // ==========================================
    private static void showManagerMenu(Manager manager) {
        while (true) {
            System.out.println("\n=== MANAGER MENU (" + manager.getFirstName() + ") ===");
            System.out.println("1. Product Management (จัดการข้อมูลสินค้า)");
            System.out.println("2. Stock Management (จัดการสต๊อกสินค้า)");
            System.out.println("3. View Inventory Report (ดูสต็อกทั้งหมดแบบแยกหมวดหมู่)");
            System.out.println("4. View Sales Report (ดูยอดขายรวม)");
            System.out.println("5. Add New Cashier (เพิ่มพนักงานขายใหม่)");
            System.out.println("6. Discount Management (เพิ่มส่วนลดใหม่)");
            System.out.println("0. Logout");
            System.out.print("> Choice: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                System.out.println("\n--- [ Product Management (จัดการข้อมูลสินค้า) ] ---");
                System.out.println("1. Add New Product (เพิ่มสินค้าใหม่)");
                System.out.println("2. Edit Product (แก้ไขข้อมูลสินค้า)");
                System.out.println("3. Delete Product (ลบสินค้าออกจากระบบ)");
                System.out.println("0. Back (กลับเมนูหลัก)");
                System.out.print("> เลือกคำสั่ง: ");
                String subChoice = scanner.nextLine().trim();

                if (subChoice.equals("1")) {
                    System.out.println("เลือกหมวดหมู่สินค้า: P1=Doll | P2=Pen | P3=Lifestyle");
                    System.out.print("> ใส่รหัสหมวดหมู่ หรือพิมพ์ 'cancel' เพื่อยกเลิก: ");
                    String prefix = scanner.nextLine().trim().toUpperCase();

                    if (prefix.equals("CANCEL")) {
                        System.out.println("[System] ยกเลิกการเพิ่มสินค้า...");
                        continue; 
                    }
                    if (!prefix.equals("P1") && !prefix.equals("P2") && !prefix.equals("P3")) {
                        System.out.println("[Error] รหัสหมวดหมู่ไม่ถูกต้อง\n");
                        continue; 
                    }

                    String id = manager.generateNextProductId(productsDb, prefix);
                    System.out.println("[System] รหัสสินค้าของคุณคือ: " + id);

                    // 1. รับชื่อสินค้า (ดัก cancel)
                    String name = getProductNameInput("Name (พิมพ์ 'cancel' เพื่อยกเลิก): ");
                    if (name.equals("CANCEL")) { System.out.println("[System] ยกเลิกการเพิ่มสินค้า..."); continue; }

                    // 2. รับราคาสินค้า (ดัก cancel ด้วยรหัสลับ -999.0)
                    double price = getDoubleInput("Price (พิมพ์ 'cancel' เพื่อยกเลิก): ", 0.0); 
                    if (price == -999.0) { System.out.println("[System] ยกเลิกการเพิ่มสินค้า..."); continue; }

                    // 3. รับจำนวนสต๊อก (ดัก cancel ด้วยรหัสลับ -999)
                    int stock = getIntInput("Initial Stock (พิมพ์ 'cancel' เพื่อยกเลิก): ", 0); 
                    if (stock == -999) { System.out.println("[System] ยกเลิกการเพิ่มสินค้า..."); continue; }

                    // 4. รับหน่วยนับ (ดักตัวเลข และดัก cancel)
                    String unit = getUnitInput("Unit (e.g., ชิ้น, ห่อ) (พิมพ์ 'cancel' เพื่อยกเลิก): ");
                    if (unit.equals("CANCEL")) { System.out.println("[System] ยกเลิกการเพิ่มสินค้า..."); continue; }
                    
                    Product p = null;
                    if (prefix.equals("P1")) p = new Doll(id, name, price, stock, unit, "25cm", "General");
                    else if (prefix.equals("P2")) p = new Stationery(id, name, price, stock, unit, "Gel", "Standard");
                    else if (prefix.equals("P3")) p = new Lifestyle(id, name, price, stock, unit, "Standard");

                    if (p != null) {
                        manager.addProduct(productsDb, p);
                        JsonDatabase.saveProductsToJson(productsDb, "products.json");
                    }
                } 
                else if (subChoice.equals("2")) {
                    System.out.println("\n--- [ Edit Product (แก้ไขข้อมูลสินค้า) ] ---");
                    displayProductCatalog(productsDb, false); // [แก้ไข] Manager ต้องเห็นทั้งหมด รวม stock 0 
                    System.out.println("----------------------------------------------");
                    
                    // [แก้ไข] เพิ่มลูปให้ค้นหาใหม่ได้ หรือพิมพ์ cancel เพื่อออก
                    while (true) {
                        System.out.print("> ใส่รหัสสินค้าที่ต้องการแก้ไข หรือพิมพ์ 'cancel' เพื่อยกเลิก: ");
                        String inputId = scanner.nextLine().trim();
                        
                        if (inputId.equalsIgnoreCase("cancel")) {
                            System.out.println("[System] ยกเลิกการแก้ไข กลับสู่เมนูก่อนหน้า");
                            break; // เตะออกจากลูป กลับไปเมนู Manager
                        }

                        Product p = findProductById(inputId);
                        if (p != null) {
                            // เข้าสู่โหมดแก้ไขข้อมูล
                            while (true) {
                                System.out.println("\n--- [ แก้ไขข้อมูลสินค้า: " + p.getProductId() + " ] ---");
                                System.out.println("1. แก้ไขชื่อ (ปัจจุบัน: " + p.getName() + ")");
                                System.out.println("2. แก้ไขราคา (ปัจจุบัน: " + p.getPrice() + " THB)");
                                System.out.println("3. แก้ไขหน่วยนับ (ปัจจุบัน: " + p.getUnit() + ")");
                                System.out.println("0. กลับเมนูก่อนหน้า (Done)");
                                System.out.print("> เลือกคำสั่ง: ");
                                String editChoice = scanner.nextLine().trim();

                                // --- ในโหมดแก้ไขสินค้า (ส่วนของเมนูย่อย editChoice) ---
                                
                                if (editChoice.equals("0")) {
                                    System.out.println("[System] ออกจากโหมดแก้ไขสินค้า");
                                    break; 
                                } 
                                else if (editChoice.equals("1")) {
                                    String newName = getProductNameInput("ใส่ชื่อใหม่ (พิมพ์ 'cancel' เพื่อยกเลิก): ");
                                    if (newName.equals("CANCEL")) {
                                        System.out.println("[System] ยกเลิกการแก้ไขชื่อ");
                                    } else {
                                        // [แก้ไข] เพิ่มการถามยืนยันก่อนเซฟ
                                        System.out.print("ยืนยันการเปลี่ยนชื่อเป็น '" + newName + "' (Y/Yes/N/No)?: ");
                                        String confirm = scanner.nextLine().trim();
                                        if (confirm.equalsIgnoreCase("Y") || confirm.equalsIgnoreCase("YES")) {
                                            p.setName(newName);
                                            JsonDatabase.saveProductsToJson(productsDb, "products.json");
                                            System.out.println("[System] อัปเดตชื่อสินค้าสำเร็จ!");
                                        } else {
                                            System.out.println("[System] ยกเลิกการบันทึกข้อมูล");
                                        }
                                    }
                                } 
                                else if (editChoice.equals("2")) {
                                    double newPrice = getDoubleInput("ใส่ราคาใหม่ (พิมพ์ 'cancel' เพื่อยกเลิก): ", 0.0);
                                    if (newPrice == -999.0) {
                                        System.out.println("[System] ยกเลิกการแก้ไขราคา");
                                    } else {
                                        // [แก้ไข] เพิ่มการถามยืนยันก่อนเซฟ
                                        System.out.print("ยืนยันการเปลี่ยนราคาเป็น '" + newPrice + "' THB (Y/Yes/N/No)?: ");
                                        String confirm = scanner.nextLine().trim();
                                        if (confirm.equalsIgnoreCase("Y") || confirm.equalsIgnoreCase("YES")) {
                                            p.setPrice(newPrice);
                                            JsonDatabase.saveProductsToJson(productsDb, "products.json");
                                            System.out.println("[System] อัปเดตราคาสำเร็จ!");
                                        } else {
                                            System.out.println("[System] ยกเลิกการบันทึกข้อมูล");
                                        }
                                    }
                                } 
                                else if (editChoice.equals("3")) {
                                    String newUnit = getUnitInput("ใส่หน่วยนับใหม่ (พิมพ์ 'cancel' เพื่อยกเลิก): ");
                                    if (newUnit.equals("CANCEL")) {
                                        System.out.println("[System] ยกเลิกการแก้ไขหน่วยนับ");
                                    } else {
                                        // [แก้ไข] เพิ่มการถามยืนยันก่อนเซฟ
                                        System.out.print("ยืนยันการเปลี่ยนหน่วยนับเป็น '" + newUnit + "' (Y/Yes/N/No)?: ");
                                        String confirm = scanner.nextLine().trim();
                                        if (confirm.equalsIgnoreCase("Y") || confirm.equalsIgnoreCase("YES")) {
                                            p.setUnit(newUnit);
                                            JsonDatabase.saveProductsToJson(productsDb, "products.json");
                                            System.out.println("[System] อัปเดตหน่วยนับสำเร็จ!");
                                        } else {
                                            System.out.println("[System] ยกเลิกการบันทึกข้อมูล");
                                        }
                                    }
                                } 
                                else {
                                    System.out.println("[Error] คำสั่งไม่ถูกต้อง");
                                    continue;
                                }
                                JsonDatabase.saveProductsToJson(productsDb, "products.json");
                                System.out.println("[System] อัปเดตข้อมูลสำเร็จ!");
                            }
                            break; // เมื่อแก้ไขเสร็จและกด 0 ออกมาแล้ว ให้ออกจากลูปค้นหาด้วย
                        } else {
                            System.out.println("[Error] ไม่พบสินค้ารหัส " + inputId + " กรุณาลองใหม่อีกครั้ง!\n");
                        }
                    }
                }
                else if (subChoice.equals("3")) {
                    System.out.println("\n--- [ Delete Product (ลบสินค้า) ] ---");
                    displayProductCatalog(productsDb, false); // [แก้ไข] Manager ต้องเห็นทั้งหมด รวม stock 0
                    System.out.println("----------------------------------------------");
                    
                    while (true) {
                        System.out.print("> ใส่รหัสสินค้าที่ต้องการลบ หรือพิมพ์ 'cancel' เพื่อยกเลิก: ");
                        String inputId = scanner.nextLine().trim();
                        
                        if (inputId.equalsIgnoreCase("cancel")) {
                            System.out.println("[System] ยกเลิกการลบสินค้า กลับสู่เมนูก่อนหน้า");
                            break; 
                        }

                        Product p = findProductById(inputId);
                        if (p != null) {
                            // [แก้ไข] เปลี่ยนคำถามให้รองรับ Yes/No
                            System.out.print("ยืนยันการลบ " + p.getName() + " (Y/Yes/N/No)?: ");
                            String confirm = scanner.nextLine().trim();
                            
                            // [แก้ไข] ดักจับทั้ง Y และ YES (พิมพ์เล็กพิมพ์ใหญ่ก็ได้)
                            if (confirm.equalsIgnoreCase("Y") || confirm.equalsIgnoreCase("YES")) {
                                manager.deleteProduct(productsDb, p); 
                                JsonDatabase.saveProductsToJson(productsDb, "products.json");
                            } else {
                                // ถ้าพิมพ์ N, No หรือคำอื่นๆ มั่วๆ จะถือว่ายกเลิกการลบทั้งหมดเพื่อความปลอดภัย
                                System.out.println("[System] ยกเลิกการลบสินค้า");
                            }
                            break; 
                        } else {
                            System.out.println("[Error] ไม่พบสินค้ารหัส " + inputId + " กรุณาลองใหม่อีกครั้ง!\n");
                        }
                    }
                }
            }
            else if (choice.equals("2")) {
                System.out.println("\n--- [ Manage Stock (เพิ่มสต๊อกสินค้า) ] ---");
                // โชว์รายการสินค้าทั้งหมดก่อนเริ่มลูปถามรหัส
                displayProductCatalog(productsDb, false); // [แก้ไข] Manager ต้องเห็นทั้งหมด รวม stock 0
                System.out.println("----------------------------------------------");
                while (true) {
                    System.out.print("> ใส่รหัสสินค้าที่ต้องการเพิ่มสต๊อก หรือพิมพ์ 'cancel' เพื่อยกเลิก: ");
                    String inputId = scanner.nextLine().trim();
                    
                    if (inputId.equalsIgnoreCase("cancel")) {
                        System.out.println("[System] ยกเลิกการเพิ่มสต๊อก กลับสู่เมนูหลัก");
                        break; 
                    }
                    
                    Product p = findProductById(inputId);
                    if (p != null) {
                        System.out.println("[System] คงเหลือปัจจุบัน: " + p.getStockQuantity() + " " + p.getUnit());
                        int amount = getIntInput("จำนวนที่ต้องการ 'เพิ่ม': ", 1); 
                        manager.manageStock(p, amount); 
                        JsonDatabase.saveProductsToJson(productsDb, "products.json");
                        break; 
                    } else {
                        System.out.println("[Error] ไม่พบสินค้ารหัส " + inputId + " กรุณาลองใหม่อีกครั้ง!\n");
                    }
                }
            }
            else if (choice.equals("3")) {
                System.out.println("\n=======================================================");
                System.out.println("              [ PRODUCT CATALOG & STOCK ]              ");
                System.out.println("=======================================================");
                displayProductCatalog(productsDb, false); // [แก้ไข] Manager ต้องเห็นทั้งหมด รวม stock 0
                System.out.println("\n=======================================================");
                System.out.print("กด Enter เพื่อกลับสู่เมนูหลัก...");
                scanner.nextLine();
            }
            else if (choice.equals("4")) {
                // [แก้ไข OOP] ใช้ SalesReportService แทน manager.viewSales()
                SalesReportService reportService = new SalesReportService();
                String report = reportService.generateSalesReport("order_database.json");
                System.out.println(report); 
            }
            else if (choice.equals("5")) {
                System.out.println("\n--- [ Add New Cashier (เพิ่มพนักงานขายใหม่) ] ---");
                System.out.println("(พิมพ์ 'cancel' ในขั้นตอนใดก็ได้เพื่อยกเลิก)");
                
                // 1. [แก้ไข] ตรวจสอบ Username ให้ขึ้นต้นด้วย A-Z หรือ a-z ด้วย Regex
                String cUser;
                while (true) {
                    cUser = getCredentialInput("Username สำหรับ Login (ขั้นต่ำ 4 ตัว และต้องขึ้นต้นด้วยตัวอักษร): ", 4);
                    if (cUser.equals("CANCEL")) { 
                        break; 
                    }
                    
                    // ลบช่องว่างหัวท้ายกันเหนียว และเช็คด้วย Regex (ตัวแรกต้องเป็น a-z หรือ A-Z)
                    cUser = cUser.trim();
                    if (!cUser.matches("^[a-zA-Z].*")) {
                        System.out.println("[Error] Username ต้องขึ้นต้นด้วยตัวอักษรภาษาอังกฤษ (A-Z, a-z) เท่านั้น กรุณาลองใหม่");
                        continue;
                    }
                    break;
                }
                if (cUser.equals("CANCEL")) { System.out.println("[System] ยกเลิก..."); continue; }
                
                String cPass = getCredentialInput("Password (ขั้นต่ำ 4 ตัว): ", 4);
                if (cPass.equals("CANCEL")) { System.out.println("[System] ยกเลิก..."); continue; }
                
                String cFname = getNameInput("ชื่อจริง (First Name): ");
                if (cFname.equals("CANCEL")) { System.out.println("[System] ยกเลิก..."); continue; }

                String cLname = getNameInput("นามสกุล (Last Name): ");
                if (cLname.equals("CANCEL")) { System.out.println("[System] ยกเลิก..."); continue; }

                String cNick = getNameInput("ชื่อเล่น (Nickname): ");
                if (cNick.equals("CANCEL")) { System.out.println("[System] ยกเลิก..."); continue; }

                String cPhone = getPhoneInput("เบอร์โทรศัพท์ (Phone): ");
                if (cPhone.equals("CANCEL")) { System.out.println("[System] ยกเลิก..."); continue; }
                
                System.out.print("> ที่อยู่ (Address): "); 
                String cAddr = scanner.nextLine().trim();
                if (cAddr.equalsIgnoreCase("cancel")) { System.out.println("[System] ยกเลิก..."); continue; }

                // 2. [แก้ไข] ตรวจสอบหมายเลขเคาน์เตอร์ 1-10
                int cCounter;
                while (true) {
                    cCounter = getIntInput("ประจำเคาน์เตอร์เบอร์ (Counter No. 1-10): ", 1);
                    if (cCounter == -999) { 
                        break; 
                    }
                    if (cCounter < 1 || cCounter > 10) {
                        System.out.println("[Error] หมายเลขเคาน์เตอร์ต้องอยู่ระหว่าง 1 ถึง 10 เท่านั้น กรุณาลองใหม่");
                        continue;
                    }
                    break;
                }
                if (cCounter == -999) { System.out.println("[System] ยกเลิก..."); continue; }

                // --- สร้างบัญชี ---
                String cId = "C" + System.currentTimeMillis();
                Cashier newCashier = new Cashier(cId, cUser, cPass, cNick, cFname, cLname, cAddr, cPhone, "Cashier", cCounter);
                manager.addCashier(usersDb, newCashier);
                JsonDatabase.saveUsersToJson(usersDb, "users.json");
                System.out.println("[System] สร้างบัญชีสำเร็จ! พนักงาน " + cFname + " สามารถ Login เข้าระบบได้ทันที");
            }
            else if (choice.equals("6")) {
                // [แก้ไข] ใส่ while (true) เพื่อวนลบเมนูย่อย จนกว่าจะกด 0
                while (true) {
                    System.out.println("\n--- [ Discount Management (จัดการโปรโมชั่น/ส่วนลด) ] ---");
                    System.out.println("1. View All Discounts (ดูส่วนลดทั้งหมด)");
                    System.out.println("2. Add New Discount (เพิ่มโค้ดส่วนลดใหม่)");
                    System.out.println("3. Edit Discount (แก้ไขส่วนลด)");
                    System.out.println("0. Back (กลับเมนูหลัก)");
                    System.out.print("> เลือกคำสั่ง: ");
                    String dChoice = scanner.nextLine().trim();

                    if (dChoice.equals("1")) {
                        System.out.println("\n>> รายการโค้ดส่วนลดทั้งหมด <<");
                        if (discountsDb.isEmpty()) System.out.println("  (ไม่มีโค้ดส่วนลดในระบบ)");
                        for (Discount d : discountsDb) {
                            System.out.println(String.format(" - โค้ด: [%s] | %s | ขั้นต่ำ: %.2f THB | ลด: %.1f%%", 
                                d.getCode(), d.getDescription(), d.getMinAmount(), d.getPercentage()));
                        }
                        // พอดูเสร็จ โค้ดจะวนกลับไปโชว์เมนูย่อยใหม่โดยอัตโนมัติ
                    } 
                    else if (dChoice.equals("2")) {
                        // 1. ตรวจสอบโค้ดส่วนลดให้ขึ้นต้นด้วยตัวอักษร
                        String code;
                        while (true) {
                            System.out.print("> ใส่โค้ดส่วนลดใหม่ (เช่น SUMMER20) หรือพิมพ์ 'cancel' เพื่อยกเลิก: ");
                            code = scanner.nextLine().trim();
                            if (code.equalsIgnoreCase("cancel")) break;
                            
                            if (code.isEmpty()) {
                                System.out.println("[Error] กรุณากรอกโค้ดส่วนลด");
                                continue;
                            }
                            if (!code.matches("^[a-zA-Z].*")) {
                                System.out.println("[Error] โค้ดส่วนลดต้องขึ้นต้นด้วยตัวอักษรภาษาอังกฤษ (A-Z, a-z) เท่านั้น");
                                continue;
                            }
                            break;
                        }
                        if (code.equalsIgnoreCase("cancel")) {
                            System.out.println("[System] ยกเลิกการเพิ่มส่วนลด...");
                            continue;
                        }

                        System.out.print("> คำอธิบายโปรโมชั่น: ");
                        String desc = scanner.nextLine().trim();
                        if (desc.equalsIgnoreCase("cancel")) {
                            System.out.println("[System] ยกเลิกการเพิ่มส่วนลด...");
                            continue;
                        }

                        // 2. ตรวจสอบยอดซื้อขั้นต่ำต้องมากกว่า 0
                        double min;
                        while (true) {
                            min = getDoubleInput("> ยอดซื้อขั้นต่ำ (THB): ", 0.0);
                            if (min == -999.0) break; // จับรหัสยกเลิก
                            
                            if (min <= 0) {
                                System.out.println("[Error] ยอดซื้อขั้นต่ำต้องมากกว่า 0 บาท กรุณาลองใหม่");
                                continue;
                            }
                            break;
                        }
                        if (min == -999.0) {
                            System.out.println("[System] ยกเลิกการเพิ่มส่วนลด...");
                            continue; 
                        }

                        // 3. ตรวจสอบเปอร์เซ็นต์ส่วนลด 1-100
                        double percent;
                        while (true) {
                            percent = getDoubleInput("> เปอร์เซ็นต์ส่วนลด (1-100%): ", 1.0);
                            if (percent == -999.0) break; 
                            
                            if (percent < 1.0 || percent > 100.0) {
                                System.out.println("[Error] เปอร์เซ็นต์ส่วนลดต้องอยู่ระหว่าง 1 ถึง 100 เท่านั้น กรุณาลองใหม่");
                                continue;
                            }
                            break;
                        }
                        if (percent == -999.0) {
                            System.out.println("[System] ยกเลิกการเพิ่มส่วนลด...");
                            continue; 
                        }

                        // 4. ตรวจสอบวันที่หมดอายุห้ามเป็นอดีต
                        java.time.LocalDate expDate;
                        while (true) {
                            expDate = getDateInput("> วันที่หมดอายุ (yyyy-MM-dd) เช่น 2026-12-31: ");
                            if (expDate.equals(java.time.LocalDate.MIN)) break; 
                            
                            // เช็คว่า วันที่กรอกมา "มาก่อน" วันปัจจุบันหรือไม่
                            if (expDate.isBefore(java.time.LocalDate.now())) {
                                System.out.println("[Error] วันที่หมดอายุต้องไม่เป็นวันในอดีต กรุณาระบุวันที่ตั้งแต่วันนี้เป็นต้นไป");
                                continue;
                            }
                            break;
                        }
                        if (expDate.equals(java.time.LocalDate.MIN)) {
                            System.out.println("[System] ยกเลิกการเพิ่มส่วนลด...");
                            continue; 
                        }
                        
                        // บันทึกข้อมูล
                        discountsDb.add(new Discount(code, desc, min, percent, expDate));
                        JsonDatabase.saveDiscountsToJson(discountsDb, "discounts.json");
                        System.out.println("[System] เพิ่มโค้ดส่วนลด " + code + " สำเร็จ!");
                    }
                    else if (dChoice.equals("3")) {
                        System.out.print("> ใส่โค้ดส่วนลดที่ต้องการแก้ไข (พิมพ์ 'cancel' เพื่อยกเลิก): ");
                        String code = scanner.nextLine().trim();
                        
                        if (code.equalsIgnoreCase("cancel")) {
                            System.out.println("[System] ยกเลิกการแก้ไขส่วนลด...");
                            continue;
                        }

                        Discount target = null;
                        for (Discount d : discountsDb) {
                            if (d.getCode().equalsIgnoreCase(code)) { target = d; break; }
                        }
                        
                        if (target != null) {
                            // 1. ดักจับ: ยอดซื้อขั้นต่ำต้องมากกว่า 0
                            double newMin;
                            while (true) {
                                newMin = getDoubleInput("ยอดซื้อขั้นต่ำใหม่ (ปัจจุบัน " + target.getMinAmount() + "): ", 0.0);
                                if (newMin == -999.0) break;
                                
                                if (newMin <= 0) {
                                    System.out.println("[Error] ยอดซื้อขั้นต่ำต้องมากกว่า 0 บาท กรุณาลองใหม่");
                                    continue;
                                }
                                break;
                            }
                            if (newMin == -999.0) {
                                System.out.println("[System] ยกเลิกการแก้ไขส่วนลด...");
                                continue;
                            }

                            // 2. ดักจับ: เปอร์เซ็นต์ส่วนลด 1-100
                            double newPercent;
                            while (true) {
                                newPercent = getDoubleInput("เปอร์เซ็นต์ส่วนลดใหม่ (ปัจจุบัน " + target.getPercentage() + "%): ", 1.0);
                                if (newPercent == -999.0) break;
                                
                                if (newPercent < 1.0 || newPercent > 100.0) {
                                    System.out.println("[Error] เปอร์เซ็นต์ส่วนลดต้องอยู่ระหว่าง 1 ถึง 100 เท่านั้น กรุณาลองใหม่");
                                    continue;
                                }
                                break;
                            }
                            if (newPercent == -999.0) {
                                System.out.println("[System] ยกเลิกการแก้ไขส่วนลด...");
                                continue;
                            }

                            // 3. ดักจับ: วันที่หมดอายุห้ามเป็นอดีต
                            java.time.LocalDate newExpDate;
                            while (true) {
                                newExpDate = getDateInput("วันที่หมดอายุใหม่ (ปัจจุบัน " + target.getExpirationDate() + "): ");
                                if (newExpDate.equals(java.time.LocalDate.MIN)) break;
                                
                                if (newExpDate.isBefore(java.time.LocalDate.now())) {
                                    System.out.println("[Error] วันที่หมดอายุต้องไม่เป็นวันในอดีต กรุณาระบุวันที่ตั้งแต่วันนี้เป็นต้นไป");
                                    continue;
                                }
                                break;
                            }
                            if (newExpDate.equals(java.time.LocalDate.MIN)) {
                                System.out.println("[System] ยกเลิกการแก้ไขส่วนลด...");
                                continue;
                            }

                            // ทำการบันทึกข้อมูลเมื่อผ่านทุกเงื่อนไข
                            target.setMinAmount(newMin);
                            target.setPercentage(newPercent);
                            target.setExpirationDate(newExpDate); 
                            JsonDatabase.saveDiscountsToJson(discountsDb, "discounts.json");
                            System.out.println("[System] อัปเดตโค้ดส่วนลดสำเร็จ!");
                        } else {
                            System.out.println("[Error] ไม่พบโค้ดส่วนลดนี้ในระบบ");
                        }
                    }
                    else if (dChoice.equals("0")) {
                        System.out.println("[System] กลับสู่เมนูหลัก...");
                        break; // ดีดตัวออกจากลูป while(true) ของเมนูย่อย เพื่อกลับไปเมนูหลักของ Manager
                    }
                    else {
                        System.out.println("[Error] คำสั่งไม่ถูกต้อง กรุณาเลือก 1, 2, 3 หรือ 0");
                    }
                } // สิ้นสุดลูป while ของเมนู Discount
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
            System.out.println("\n=== CASHIER MENU (" + cashier.getFirstName() + " - Counter " + cashier.getCounterNumber() + ") ===");
            System.out.println("1. Open Shift (เปิดกะ)");
            System.out.println("2. Create Order (ขายสินค้า)");
            System.out.println("3. Check Price & Stock (เช็คสินค้า)");
            System.out.println("4. Close Shift & X-Report (ปิดกะ)");
            System.out.println("0. Logout");
            System.out.print("> Choice: ");
            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                cashier.openShift();
                System.out.println(cashier.getOpenShiftMessage());
            }
            else if (choice.equals("2")) {
                if (cashier.isShiftOpenNow()) processNewOrder(cashier);
                else System.out.println("[Error] Please Open Shift first!");
            }
            else if (choice.equals("3")) {
                System.out.println("\n=======================================================");
                System.out.println("              [ PRODUCT CATALOG & STOCK ]              ");
                System.out.println("=======================================================");
                displayProductCatalog(productsDb, true);
                System.out.println("\n=======================================================");
                System.out.print("กด Enter เพื่อกลับสู่เมนูหลัก...");
                scanner.nextLine();
            }
            else if (choice.equals("4")) {
                // [แก้ไข] เช็คสถานะก่อนยอมให้ปิดกะ คล้ายๆ กับเมนู Create Order
                if (cashier.isShiftOpenNow()) {
                    cashier.closeShift();
                    System.out.println(cashier.getCloseShiftMessage());
                    System.out.println(cashier.generateXReportData());
                } else {
                    System.out.println("[Error] คุณยังไม่ได้เปิดกะ! ไม่สามารถปิดกะได้ครับ");
                }
            }
            else if (choice.equals("0")) { 
                if (cashier.isShiftOpenNow()) {
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
    private static void processNewOrder(Cashier cashier) {
        Order order = cashier.createOrder("ORD-" + System.currentTimeMillis());
        System.out.println("\n=======================================================");
        System.out.println("                 [ NEW ORDER (เปิดบิลใหม่) ]                 ");
        System.out.println("=======================================================");
        
        displayProductCatalog(productsDb, true);
        System.out.println("\n-------------------------------------------------------");

        while (true) {
            System.out.println("\n=================[ ORDER COMMANDS ]==================");
            System.out.println("  [รหัสสินค้า]       : เพิ่มสินค้าลงตะกร้า (เช่น P101)");
            System.out.println("  [1] หรือ CART   : ดูสินค้าที่เลือกไปแล้ว (Cart)");
            System.out.println("  [2] หรือ REMOVE : ลบสินค้าออกจากตะกร้า");
            System.out.println("  [3] หรือ PAY    : ไปหน้าชำระเงิน");
            System.out.println("  [4] หรือ LIST   : ดูแคตตาล็อกสินค้าทั้งหมด");
            System.out.println("  [0] หรือ CANCEL : ยกเลิกบิล (คืนสต๊อก)");
            System.out.println("=====================================================");
            System.out.print("> ใส่คำสั่ง: ");
            String input = scanner.nextLine().trim();

            // --- [0] CANCEL ---
            if (input.equalsIgnoreCase("cancel") || input.equals("0")) {
                order.restoreStock(); // คืนสต๊อกทั้งหมดก่อนยกเลิก
                order.cancelOrder();
                System.out.println("[System] ยกเลิกออเดอร์และคืนสต๊อกเรียบร้อยแล้ว");
                return;
            }
            
            // --- [1] CART ---
            else if (input.equalsIgnoreCase("cart") || input.equals("1")) {
                System.out.println("\n--- [ สินค้าที่เลือกไปแล้ว (Cart) ] ---");
                if (order.getOrderItems().isEmpty()) {
                    System.out.println("ยังไม่มีสินค้าในตะกร้า");
                } else {
                    order.printOrderSummary(); 
                    System.out.println("-----------------------------------");
                    System.out.println(String.format("ยอดรวมชั่วคราว (Subtotal): %.2f THB", order.calculateTotal()));
                }
            }
            
            // --- [4] LIST (ข้อ 3) ---
            else if (input.equalsIgnoreCase("list") || input.equals("4")) {
                displayProductCatalog(productsDb);
            }
            
            // --- [2] REMOVE (ข้อ 2) ---
            else if (input.equalsIgnoreCase("remove") || input.equals("2")) {
                // [แก้ไข] ใส่ while (true) เพื่อให้วนลบซ้ำได้จนกว่าจะพิมพ์ cancel หรือตะกร้าว่าง
                while (true) {
                    if (order.getOrderItems().isEmpty()) {
                        System.out.println("[System] ตะกร้าสินค้าว่างเปล่าแล้วครับ!");
                        break; // ตะกร้าว่างแล้ว ให้ออกจากโหมดลบทันที
                    }
                    
                    System.out.println("\n--- [ สินค้าที่เลือกไปแล้ว (Cart) ] ---");
                    order.printOrderSummary();
                    
                    System.out.print("\n> ใส่รหัสสินค้าที่ต้องการเอาออก (พิมพ์ 'cancel' เพื่อกลับเมนูหลัก): ");
                    String rmId = scanner.nextLine().trim();
                    if (rmId.equalsIgnoreCase("cancel")) {
                        System.out.println("[System] ออกจากโหมดลบสินค้า...");
                        break; // พิมพ์ cancel ให้ออกจากโหมดลบ
                    }

                    OrderItem targetItem = null;
                    for (int i = 0; i < order.getOrderItems().size(); i++) {
                        OrderItem item = order.getOrderItems().get(i);
                        if (item.getProduct().getProductId().equalsIgnoreCase(rmId)) {
                            targetItem = item; break;
                        }
                    }

                    if (targetItem == null) {
                        System.out.println("[Error] ไม่พบสินค้ารหัส " + rmId + " ในตะกร้าครับ!");
                    } else {
                        int maxRm = targetItem.getQuantity();
                        int rmQty = getIntInput("ต้องการเอาออกกี่ชิ้น? (สูงสุด " + maxRm + ", พิมพ์ 'cancel' เพื่อยกเลิก): ", 1);
                        
                        if (rmQty == -999) continue; // ถ้ากด cancel ตรงจำนวน ให้วนลูปกลับไปถามรหัสสินค้าใหม่

                        if (rmQty > maxRm) {
                            System.out.println("[Error] ระบุจำนวนเกินกว่าที่มีในตะกร้าครับ!");
                        } else {
                            System.out.print("ยืนยันการเอา " + targetItem.getProduct().getName() + " ออก " + rmQty + " ชิ้น (Y/N)?: ");
                            String confirm = scanner.nextLine().trim();
                            
                            if (confirm.equalsIgnoreCase("Y") || confirm.equalsIgnoreCase("YES")) {
                                Product pTarget = targetItem.getProduct();
                                
                                // ลบออกทั้งหมดก่อน
                                order.removeItemFromCart(pTarget);
                                
                                // ถ้าลบออกไม่หมด (เอาออกแค่บางส่วน) ให้ใส่ส่วนที่เหลือกลับเข้าไปใหม่
                                if (rmQty < maxRm) {
                                    order.addItemToCart(pTarget, maxRm - rmQty);
                                }
                                
                                // คืนสต๊อกกลับเข้าระบบทันที
                                pTarget.updateStock(rmQty);
                                System.out.println("[System] เอาสินค้าออกจากตะกร้าและคืนสต๊อกเรียบร้อยครับ!");
                            } else {
                                System.out.println("[System] ยกเลิกการเอาสินค้าออก");
                            }
                        }
                    }
                } // สิ้นสุดลูป while ของการ remove
            }
            
            // --- [3] PAY ---
            else if (input.equalsIgnoreCase("pay") || input.equals("3")) {
                if (order.getOrderItems().isEmpty()) {
                    System.out.println("\n[Error] ไม่สามารถชำระเงินได้เนื่องจากตะกร้าสินค้าว่างเปล่าครับ!");
                    System.out.println("1. ยกเลิกออเดอร์นี้ (Cancel Order)");
                    System.out.println("2. เลือกสินค้าต่อ (Continue Shopping)");
                    System.out.print("> เลือกคำสั่ง (1 หรือ 2): ");
                    String emptyChoice = scanner.nextLine().trim();
                    
                    if (emptyChoice.equals("1")) {
                        order.restoreStock();
                        order.cancelOrder();
                        System.out.println("[System] ยกเลิกออเดอร์และกลับสู่เมนูหลัก");
                        return;
                    } else {
                        continue; // กลับไปลูปซื้อของต่อ
                    }
                }
                break; // มีของในตะกร้า ออกจากลูปเพื่อไปหน้าชำระเงิน
            }
            
            // --- พิมพ์รหัสสินค้าเพื่อเพิ่มลงตะกร้า (ข้อ 4) ---
            else {
                Product p = findProductById(input);
                if (p != null) {
                    int availableStock = p.getStockQuantity(); // ดึงสต๊อกจริง ณ ปัจจุบัน
                    if (availableStock <= 0) {
                        System.out.println("[Error] สินค้านี้สต๊อกหมดแล้วครับ!");
                        continue;
                    }
                    
                    System.out.println("พบสินค้า: " + p.getDetails() + " | " + p.getPrice() + " THB (มีสต๊อก " + availableStock + " ชิ้น)");
                    int addQty = getIntInput("ระบุจำนวนที่ต้องการซื้อ (พิมพ์ 'cancel' ยกเลิก): ", 1);
                    if (addQty == -999) continue;
                    
                    if (addQty > availableStock) {
                        System.out.println("[Error] สต็อกไม่เพียงพอ! (คุณสั่ง " + addQty + " แต่มีของแค่ " + availableStock + ")");
                    } else {
                        order.addItemToCart(p, addQty);
                        p.updateStock(-addQty); // [แก้ไข] ใช้ method แทน direct access
                        System.out.println("[System] เพิ่มลงตะกร้าสำเร็จ. ยอดรวมชั่วคราว: " + order.calculateTotal() + " THB");
                    }
                } else {
                    System.out.println("[Error] คำสั่งไม่ถูกต้อง หรือไม่พบรหัสสินค้านี้ครับ!");
                }
            }
        }

        // --- เข้าสู่หมวด CHECKOUT (จ่ายเงิน) ---
        System.out.println("\n--- [ CHECKOUT ] ---");
        order.printOrderSummary(); 

        double subTotal = order.calculateTotal();
        System.out.println("-------------------------------------------------------");
        
        // ==========================================
        // โชว์โปรโมชั่นที่กำลังจัดรายการให้แคชเชียร์เห็นเพื่อช่วยเชียร์ขาย
        // ==========================================
        System.out.println(">> โปรโมชั่นที่กำลังจัดรายการ (Active Promotions) <<");
        boolean hasActivePromo = false;
        for (Discount d : discountsDb) {
            if (!d.isExpired()) {
                // ดึง Description มาโชว์ (เช่น "ซื้อครบ 1000 บาท ลด 10%")
                System.out.println("   * " + d.getDescription() + " (ยอดขั้นต่ำ: " + d.getMinAmount() + " THB)");
                hasActivePromo = true;
            }
        }
        if (!hasActivePromo) {
            System.out.println("   (ไม่มีโปรโมชั่นในขณะนี้)");
        }
        System.out.println("-------------------------------------------------------");
        
        System.out.println(String.format("ยอดรวมชั่วคราว (Subtotal): %.2f THB", subTotal));
        
        // ==========================================
        // ระบบกรอกโค้ดส่วนลดด้วยตัวเอง (Manual Discount Code)
        // ==========================================
        double discountAmount = 0.0;

        while (true) {
            System.out.print("> ใส่โค้ดส่วนลด (หากไม่มีให้กด Enter เพื่อข้าม): ");
            String codeInput = scanner.nextLine().trim();

            if (codeInput.isEmpty()) {
                break; // ข้ามการใส่ส่วนลด ไปคิดราคาเต็ม
            }

            // ค้นหาโค้ดส่วนลดในระบบ
            Discount foundDiscount = null;
            for (Discount d : discountsDb) {
                if (d.getCode().equalsIgnoreCase(codeInput)) {
                    foundDiscount = d;
                    break;
                }
            }

            // ตรวจสอบเงื่อนไขต่างๆ
            if (foundDiscount == null) {
                System.out.println("[Error] ไม่พบโค้ดส่วนลดนี้ในระบบ กรุณาลองใหม่");
                continue;
            }
            if (foundDiscount.isExpired()) {
                System.out.println("[Error] โค้ดส่วนลดนี้หมดอายุไปแล้ว");
                continue;
            }
            if (subTotal < foundDiscount.getMinAmount()) {
                System.out.println("[Error] ยอดซื้อไม่ถึงเงื่อนไข (ต้องซื้อขั้นต่ำ " + foundDiscount.getMinAmount() + " THB)");
                continue;
            }

            // หากผ่านทุกเงื่อนไข ให้คำนวณส่วนลดและบันทึกลง Order
            order.applyDiscount(foundDiscount); // ใช้ตัวแปร order ให้ตรงกับโค้ดของคุณ
            discountAmount = foundDiscount.calculateDiscountAmount(subTotal);
            
            System.out.println(String.format("[System] ใช้โค้ดส่วนลด [%s] สำเร็จ! (ลดไป %.2f THB)", foundDiscount.getCode(), discountAmount));
            break;
        }

        // 1. โชว์ยอดหลังหักส่วนลด
        double afterDiscount = subTotal - discountAmount;
        System.out.println(String.format("ยอดหลังหักส่วนลด (After Discount): %.2f THB", afterDiscount));
        System.out.println("-------------------------------------------------------");

        // ดึงยอดสุทธิจาก Order
        double grandTotal = order.calculateGrandTotal();
        
        // 2. คำนวณและโชว์ ยอดก่อน VAT และ ยอด VAT 7%
        double valueBeforeVat = grandTotal / 1.07;
        double vatAmount = grandTotal - valueBeforeVat;
        
        System.out.println(String.format("มูลค่าสินค้าก่อนภาษี (Value Before VAT): %.2f THB", valueBeforeVat));
        System.out.println(String.format("ภาษีมูลค่าเพิ่ม (VAT 7%%): %.2f THB", vatAmount));
        System.out.println(String.format(">> ยอดรวมสุทธิ (Grand Total): %.2f THB <<", grandTotal));
        System.out.println("=======================================================");
        
        double cash = 0;
        double change = 0;
        boolean paymentSuccess = false;
        String payMethod = "";

        while (!paymentSuccess) {
            while (true) {
                System.out.println("\nช่องทางการชำระเงิน: 1. Cash (เงินสด)  2. PromptPay (พร้อมเพย์)");
                System.out.print("> เลือก (1 หรือ 2) หรือพิมพ์ 0 เพื่อยกเลิกบิล: ");
                String payChoice = scanner.nextLine().trim();
                
                if (payChoice.equals("1")) { 
                    payMethod = "Cash"; break; 
                }
                else if (payChoice.equals("2")) { 
                    payMethod = "PromptPay"; break; 
                }
                else if (payChoice.equals("0")) {
                    order.restoreStock(); // คืนสต๊อก
                    order.cancelOrder();
                    System.out.println("[System] ยกเลิกบิลและคืนสต๊อกเรียบร้อยแล้ว");
                    return;
                }
                else { 
                    System.out.println("[Error] กรุณาพิมพ์ 1, 2 หรือ 0 เท่านั้น\n"); 
                }
            }

            boolean backToMethodSelect = false;

            while (!paymentSuccess && !backToMethodSelect) {
                if (payMethod.equals("Cash")) {
                    cash = getDoubleInput("รับเงินสดมา (THB) [พิมพ์ 0 เปลี่ยนวิธีจ่าย | พิมพ์ -1 ยกเลิกบิล]: ", -1);
                    
                    if (cash == -1) {
                        order.restoreStock(); // คืนสต๊อก
                        order.cancelOrder();
                        System.out.println("[System] ยกเลิกบิลและคืนสต๊อกเรียบร้อยแล้ว");
                        return;
                    } else if (cash == 0) {
                        backToMethodSelect = true;
                        System.out.println("[System] ย้อนกลับไปเลือกช่องทางการชำระเงิน...");
                    } else {
                        change = cashier.processPayment(cash, grandTotal);
                        if (change >= 0) {
                            paymentSuccess = true;
                        } else {
                            System.out.println(String.format("[Error] เงินไม่พอ! ขาดอีก %.2f THB", Math.abs(change)));
                        }
                    }
                } 
                else if (payMethod.equals("PromptPay")) {
                    System.out.println("\n[System] กำลังสร้าง QR Code ยอด " + grandTotal + " THB...");
                    System.out.print("> ลูกค้าสแกนสำเร็จหรือไม่? (Y = สำเร็จ | N = เปลี่ยนวิธีจ่าย | C = ยกเลิกบิล): ");
                    String confirm = scanner.nextLine().trim().toUpperCase();

                    if (confirm.equals("Y")) {
                        cash = grandTotal;
                        change = cashier.processPayment(cash, grandTotal);
                        paymentSuccess = true;
                    } else if (confirm.equals("N")) {
                        backToMethodSelect = true; 
                        System.out.println("[System] ย้อนกลับไปเลือกช่องทางการชำระเงิน...");
                    } else if (confirm.equals("C")) {
                        order.restoreStock(); // คืนสต๊อก
                        order.cancelOrder();
                        System.out.println("[System] ยกเลิกบิลและคืนสต๊อกเรียบร้อยแล้ว");
                        return;
                    } else {
                        System.out.println("[Error] กรุณาพิมพ์ Y, N หรือ C เท่านั้น");
                    }
                }
            }
        } 

        if (order.processCheckout()) { 
            order.setPaymentDetails(cash, change);
            System.out.println(String.format("[System] ชำระเงินสำเร็จ เงินทอน: %.2f THB", change));
            Receipt receipt = new Receipt("REC-" + System.currentTimeMillis(), order, payMethod, cash, change);
            // [แก้ไข OOP] ใช้ BillFormatter แทน receipt.printBill()
            BillFormatter formatter = new BillFormatter();
            System.out.println(formatter.formatBill(receipt));
            // [แก้ไข OOP] แยก recordSoldItems เป็น 2 method
            cashier.recordSoldItemsLog(order);   // บันทึก log
            cashier.recordDailySales(order);      // บวกยอดขาย
            JsonDatabase.saveOrderToJson(order, "order_database.json");
            
            // เซฟสินค้าลงไฟล์ทันที เพื่ออัปเดตสต๊อกล่าสุด
            JsonDatabase.saveProductsToJson(productsDb, "products.json"); 
        }
    }
    
    // ==========================================
    // เมธอดสำหรับแสดงแคตตาล็อกสินค้า (เพิ่มระบบซ่อนสินค้าหมด)
    // ==========================================
    // ==========================================
    // เมธอดแสดงสินค้า 1: สำหรับ Manager (เห็นทุกอย่าง)
    // ==========================================
    private static void displayProductCatalog(List<Product> db) {
        displayProductCatalog(db, true); // สั่ง true = ซ่อนของหมด
    }

    // ==========================================
    // เมธอดแสดงสินค้า 2: ทำงานหลัก และรองรับการซ่อนสต๊อก 0
    // ==========================================
    private static void displayProductCatalog(List<Product> db, boolean hideOutOfStock) {
        if (db.isEmpty()) {
            System.out.println("  (ไม่มีสินค้าในระบบ)");
            return; 
        }

        System.out.println("\n>> หมวดหมู่: Doll (ตุ๊กตา) <<");
        boolean hasDoll = false;
        for (Product p : db) {
            if (p instanceof Doll) { 
                if (hideOutOfStock && p.getStockQuantity() <= 0) continue; // ซ่อนถ้าแคชเชียร์ดูและของหมด
                System.out.println(String.format("  - [%s] %-20s | Price: %6.2f THB | Stock: %3d %s", 
                    p.getProductId(), p.getDetails(), p.getPrice(), p.getStockQuantity(), p.getUnit()));
                hasDoll = true;
            }
        }
        if (!hasDoll) System.out.println("  (ไม่มีสินค้าในหมวดหมู่นี้ หรือสินค้าหมดชั่วคราว)");

        System.out.println("\n>> หมวดหมู่: Pen & Stationery (เครื่องเขียน) <<");
        boolean hasStationery = false;
        for (Product p : db) {
            if (p instanceof Stationery) { 
                if (hideOutOfStock && p.getStockQuantity() <= 0) continue; // ซ่อนถ้าแคชเชียร์ดูและของหมด
                System.out.println(String.format("  - [%s] %-20s | Price: %6.2f THB | Stock: %3d %s", 
                    p.getProductId(), p.getDetails(), p.getPrice(), p.getStockQuantity(), p.getUnit()));
                hasStationery = true;
            }
        }
        if (!hasStationery) System.out.println("  (ไม่มีสินค้าในหมวดหมู่นี้ หรือสินค้าหมดชั่วคราว)");

        System.out.println("\n>> หมวดหมู่: Lifestyle (ของใช้ทั่วไป) <<");
        boolean hasLifestyle = false;
        for (Product p : db) {
            if (p instanceof Lifestyle) { 
                if (hideOutOfStock && p.getStockQuantity() <= 0) continue; // ซ่อนถ้าแคชเชียร์ดูและของหมด
                System.out.println(String.format("  - [%s] %-20s | Price: %6.2f THB | Stock: %3d %s", 
                    p.getProductId(), p.getDetails(), p.getPrice(), p.getStockQuantity(), p.getUnit()));
                hasLifestyle = true;
            }
        }
        if (!hasLifestyle) System.out.println("  (ไม่มีสินค้าในหมวดหมู่นี้ หรือสินค้าหมดชั่วคราว)");
    }

    // ==========================================
    // ฟังก์ชันช่วยเหลือป้องกันโปรแกรมพัง (Error Handlers & Validation)
    // ==========================================
    
    private static Product findProductById(String id) {
        for (Product p : productsDb) {
            if (p.getProductId().equalsIgnoreCase(id)) return p;
        }
        return null;
    }

    // ==========================================
    // แก้ไข getIntInput ให้ดักจับ -0 ด้วยเช่นกัน
    // ==========================================
    private static int getIntInput(String prompt, int min) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("cancel")) return -999; // ส่งรหัสลับกลับไปเมื่อกดยกเลิก

            try {
                int value = Integer.parseInt(input);
                
                // [แก้ไข] ดักจับค่าน้อยกว่า min และดักจับ -0
                if (value < min || (value == 0 && input.startsWith("-"))) {
                    System.out.println("[Error] ค่าที่ใส่ต้องไม่ติดลบ (ห้ามใส่ -0) และไม่น้อยกว่า " + min + " กรุณาใส่ใหม่");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("[Error] กรุณาใส่ตัวเลขจำนวนเต็มที่ถูกต้อง (หรือพิมพ์ 'cancel' เพื่อยกเลิก)");
            }
        }
    }

    private static String getUnitInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("cancel")) return "CANCEL";
            
            if (input.isEmpty()) {
                System.out.println("[Error] หน่วยนับห้ามเป็นค่าว่างครับ");
                continue;
            }
            
            // Regex: ^[^0-9]+$ แปลว่า "ตั้งแต่ต้นจนจบ ห้ามมีตัวเลข 0-9 โผล่มาแม้แต่ตัวเดียว"
            if (input.matches("^[^0-9]+$")) {
                return input;
            } else {
                System.out.println("[Error] หน่วยนับต้องไม่มีตัวเลขผสมอยู่ครับ (เช่น ใส่ 'ชิ้น', 'ห่อ', หรือ 'kg')");
            }
        }
    }

    // ==========================================
    // แก้ไข getDoubleInput ให้รองรับคำว่า cancel และดักจับ -0
    // ==========================================
    private static double getDoubleInput(String prompt, double min) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("cancel")) return -999.0; // ส่งรหัสลับกลับไปเมื่อกดยกเลิก

            try {
                double value = Double.parseDouble(input);
                
                // [แก้ไข] ดักจับค่าน้อยกว่า min และดักจับ -0 ด้วยการเช็คว่ามีเครื่องหมายลบนำหน้าหรือไม่
                if (value < min || (value == 0.0 && input.startsWith("-"))) {
                    System.out.println("[Error] ค่าที่ใส่ต้องไม่ติดลบ (ห้ามใส่ -0) และไม่น้อยกว่า " + min + " กรุณาใส่ใหม่");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("[Error] กรุณาใส่ตัวเลขทศนิยมที่ถูกต้อง (หรือพิมพ์ 'cancel' เพื่อยกเลิก)");
            }
        }
    }

    // ==========================================
    // ตรวจสอบการรับค่าวันที่ (รูปแบบ ปี-เดือน-วัน)
    // ==========================================
    private static java.time.LocalDate getDateInput(String prompt) {
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("cancel")) return java.time.LocalDate.MIN; // ใช้ LocalDate.MIN เป็นรหัสลับกดยกเลิก
            
            try {
                return java.time.LocalDate.parse(input, formatter);
            } catch (Exception e) {
                System.out.println("[Error] รูปแบบวันที่ไม่ถูกต้อง! กรุณาพิมพ์ ปี-เดือน-วัน (เช่น 2026-12-31) หรือ 'cancel'");
            }
        }
    }
    
    // ==========================================
    // ฟังก์ชันช่วยเหลือสำหรับตรวจสอบความถูกต้องของข้อความ (Validation)
    // ==========================================

    // ตรวจสอบชื่อ: รับเฉพาะภาษาอังกฤษ ภาษาไทย และช่องว่าง (ห้ามมีตัวเลขหรือสัญลักษณ์)
        private static String getNameInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("cancel")) return "CANCEL"; // ทางหนีพิเศษ
            
            if (input.matches("^[a-zA-Zก-ฮะ-์\\s]+$")) {
                return input;
            } else {
                System.out.println("[Error] กรุณาใส่เฉพาะตัวอักษร (หรือพิมพ์ 'cancel' เพื่อยกเลิก)");
            }
        }
    }

    // ==========================================
    // ตรวจสอบเบอร์โทร: รับเฉพาะตัวเลข 10 หลักเป๊ะๆ
    // ==========================================
    private static String getPhoneInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("cancel")) return "CANCEL"; // ทางหนีพิเศษ
            
            // Regex: ^[0-9]{10}$ แปลว่า ต้องเป็นตัวเลข (0-9) จำนวน 10 ตัวเท่านั้น
            if (input.matches("^[0-9]{10}$")) {
                return input;
            } else {
                System.out.println("[Error] กรุณาใส่เบอร์โทรศัพท์เป็นตัวเลข 10 หลักเป๊ะๆ (เช่น 0812345678)");
            }
        }
    }
    // ==========================================
    // ตรวจสอบชื่อสินค้า: ห้ามขึ้นต้นด้วยเลข, ห้ามมีอักษรพิเศษ
    // ==========================================   
    private static String getProductNameInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("cancel")) return "CANCEL"; // ทางหนีพิเศษ
            
            if (input.isEmpty()) {
                System.out.println("[Error] ชื่อสินค้าห้ามเป็นค่าว่างครับ");
                continue;
            }
            
            // Regex ใหม่: 
            // ^[a-zA-Zก-ฮะ-์] แปลว่า "ตัวแรกสุดต้องเป็นตัวอักษร (อังกฤษหรือไทย) เท่านั้น"
            // [a-zA-Z0-9ก-ฮะ-์\\s]*$ แปลว่า "ตัวถัดๆ ไปเป็นตัวอักษร, ตัวเลข, หรือช่องว่างก็ได้"
            if (input.matches("^[a-zA-Zก-ฮะ-์][a-zA-Z0-9ก-ฮะ-์\\s]*$")) {
                return input; 
            } else {
                System.out.println("[Error] ชื่อสินค้าต้องขึ้นต้นด้วยตัวอักษร และห้ามมีสัญลักษณ์พิเศษครับ!");
            }
        }
    }
    
    // ==========================================
    // ตรวจสอบความยาวขั้นต่ำ (ใช้กับ Username และ Password)
    // ==========================================
    private static String getCredentialInput(String prompt, int minLength) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("cancel")) return "CANCEL"; // ทางหนีพิเศษ

            // เช็คว่าความยาวของข้อความน้อยกว่าที่กำหนดหรือไม่
            if (input.length() < minLength) {
                System.out.println("[Error] ข้อมูลต้องมีความยาวอย่างน้อย " + minLength + " ตัวอักษรครับ");
            } else {
                return input;
            }
        }
    }

    // --- Helper Method สำหรับคืนสต๊อกเวลากดยกเลิก ---


    
    private static void setupMockDatabase() {
        // [แก้ไข] โหลด User จากไฟล์ users.json ก่อน
        usersDb = JsonDatabase.loadUsersFromJson("users.json");
        
        // ถ้าไฟล์ไม่มี หรือว่างเปล่า (รันครั้งแรก) ให้สร้าง Manager และ Cashier ตั้งต้นขึ้นมา
        if (usersDb.isEmpty()) {
            usersDb.add(new Manager("6810210428", "admin_kit", "1234", "Kit", "Kittapon", "W", "Songkhla", "081", "Manager", "IT", "StoreMgr", "0200"));
            usersDb.add(new Cashier("6810210533", "cashier_n", "5678", "Niphas", "Niphas", "P", "Songkhla", "082", "Cashier", 1));
            
            // สร้างเสร็จปุ๊บ เซฟลงไฟล์ทันที!
            JsonDatabase.saveUsersToJson(usersDb, "users.json");
        }

        discountsDb = JsonDatabase.loadDiscountsFromJson("discounts.json");
        if (discountsDb.isEmpty()) {
            discountsDb.add(new Discount("DISC10", "ซื้อครบ 1000 บาท ลด 10%", 1000.0, 10.0, java.time.LocalDate.parse("2026-12-31")));
            JsonDatabase.saveDiscountsToJson(discountsDb, "discounts.json");
        }

        productsDb = JsonDatabase.loadProductsFromJson("products.json");
        if (productsDb.isEmpty()) {
            productsDb.add(new Doll("P101", "Care Bears", 399.0, 15, "ตัว", "25cm", "Cheer"));
            productsDb.add(new Stationery("P201", "Moshi Pen", 20.0, 50, "ด้าม", "Gel", "Blue"));
            productsDb.add(new Lifestyle("P301", "Sunscreen", 129.0, 20, "หลอด", "50ml"));
            JsonDatabase.saveProductsToJson(productsDb, "products.json");
        }
    }
}