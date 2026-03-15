public class Product {
    protected String productId;
    protected String name;
    protected double price;
    protected int stockQuantity;
    protected String unit;

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
    
    public void updateStock(int amount) {
        this.stockQuantity += amount;
    }

    public boolean isActive() {
        return this.stockQuantity > 0;
    }

    public String getDetails() {
        return this.name;
    }
}