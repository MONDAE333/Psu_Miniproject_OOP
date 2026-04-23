public class Stationery extends Product {
    private String type;
    private String color;

    public Stationery(String id, String name, double price, int stock, String unit, String type, String color) {
        super(id, name, price, stock, unit);
        this.type = type;
        this.color = color;
    }

    @Override
    public String getDetails() {
        return this.getName() + " (Type: " + type + ", Color: " + color + ")";
    }
}