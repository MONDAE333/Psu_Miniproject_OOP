import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * [เพิ่มใหม่] Service class สำหรับจัดการ Sales Report
 * Responsibility: จัดการการสร้าง Sales Report จากไฟล์ JSON
 * ไม่มี System.out.println - ให้ Main จัดการ output
 */
public class SalesReportService {
    
    /**
     * สร้าง Sales Report จากไฟล์ JSON
     * @param jsonFilePath ที่อยู่ไฟล์ JSON
     * @return String ที่มี report ทั้งหมด (Main จะ print)
     */
    public String generateSalesReport(String jsonFilePath) {
        StringBuilder report = new StringBuilder();
        report.append("\n=======================================================\n");
        report.append("                 SALES HISTORY REPORT                  \n");
        report.append("=======================================================\n");
        
        try {
            List<String> lines = Files.readAllLines(Paths.get(jsonFilePath));
            boolean inItems = false;
            double grandTotalAllOrders = 0.0; 

            // ตัวแปรพักข้อมูล
            String tempTotal = "";
            String tempCash = "";
            String tempChange = "";
            String tempDiscount = "";
            String tempVat = "";

            for (String line : lines) {
                line = line.trim(); 
                
                if (line.startsWith("\"orderId\":")) {
                    report.append("\n[ ใบเสร็จรับเงิน: ").append(extractValue(line)).append(" ]\n");
                    tempTotal = ""; tempCash = ""; tempChange = ""; tempDiscount = ""; tempVat = "";
                } 
                else if (line.startsWith("\"timestamp\":")) {
                    report.append("เวลา: ").append(extractValue(line)).append("\n");
                } 
                else if (line.startsWith("\"cashier\":")) {
                    report.append("พนักงานขาย: ").append(extractValue(line)).append("\n");
                } 
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
                else if (line.startsWith("\"items\":")) {
                    report.append("รายการสินค้า:\n");
                    inItems = true; 
                } 
                else if (inItems) {
                    if (line.startsWith("\"name\":")) {
                        report.append("  - ").append(extractValue(line));
                    } else if (line.startsWith("\"pricePerUnit\":")) {
                        report.append(" (@").append(extractValue(line)).append(" THB) ");
                    } else if (line.startsWith("\"quantity\":")) {
                        report.append("x").append(extractValue(line));
                    } else if (line.startsWith("\"subTotal\":")) {
                        report.append(" = ").append(extractValue(line)).append(" THB\n");
                    } 
                    else if (line.startsWith("]")) { 
                        inItems = false; 
                        report.append("-----------------------------------\n");
                        
                        if (!tempDiscount.isEmpty() && Double.parseDouble(tempDiscount) > 0) {
                            report.append(String.format("ส่วนลด (Discount): %.2f THB\n", Double.parseDouble(tempDiscount)));
                        }
                        if (!tempVat.isEmpty()) {
                            report.append(String.format("ภาษี (VAT 7%%): %.2f THB\n", Double.parseDouble(tempVat)));
                        }
                        if (!tempTotal.isEmpty()) {
                            report.append(String.format("ยอดรวมสุทธิ (Grand Total): %.2f THB\n", Double.parseDouble(tempTotal)));
                        }
                        
                        report.append("-----------------------------------\n");
                        if (!tempCash.isEmpty())  report.append(String.format("รับเงินมา: %.2f THB\n", Double.parseDouble(tempCash)));
                        if (!tempChange.isEmpty()) report.append(String.format("เงินทอน: %.2f THB\n", Double.parseDouble(tempChange)));
                        report.append("=======================================================\n");
                    }
                } 
            }
            
            report.append("\n>>> ยอดขายรวมทั้งหมด (Total Revenue): ").append(String.format("%.2f", grandTotalAllOrders)).append(" THB <<<\n");
            
        } catch (Exception e) {
            report.append("[System] ยังไม่มีข้อมูลการขาย หรือไม่สามารถอ่านไฟล์ได้ (").append(e.getMessage()).append(")\n");
        }
        
        return report.toString();
    }

    private String extractValue(String line) {
        String[] parts = line.split(":", 2);
        if (parts.length > 1) {
            return parts[1].replace("\"", "").replace(",", "").trim();
        }
        return "";
    }
}
