public class Product {
    private String productId;
    private String name;
    private double price;
    private int stockQuantity;
    private String unit;

    public Product(String id, String name, double price, int stock, String unit) {
        this.productId = id;
        this.name = name;
        this.price = price;
        this.stockQuantity = stock;
        this.unit = unit;
    }

    public double getPrice() { return price; }
    public String getProductId() { return productId; }
    public String getName() { return name; }
    public int getStockQuantity() { return stockQuantity; }
    public String getUnit() { return unit; }
    
    public void updateStock(int amount) {
        this.stockQuantity += amount;
    }

    public boolean isActive() {
        return this.stockQuantity > 0;
    }

    public String getDetails() {
        return this.name;
    }

    // เพิ่ม Setter สำหรับอัปเดตข้อมูล (นำไปใช้ในหน้า Edit Product ของ Manager)
    public void setName(String newName) { 
        if(newName != null && !newName.trim().isEmpty()) {
            this.name = newName; 
        }
    }

    public void setPrice(double newPrice) { 
        if(newPrice >= 0) {
            this.price = newPrice; 
        }
    }
    
    public void setUnit(String newUnit) { 
        if(newUnit != null && !newUnit.trim().isEmpty()) {
            this.unit = newUnit; 
        }
    }
}