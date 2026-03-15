public class Doll extends Product {
    private String size;
    private String collectionName;

    public Doll(String id, String name, double price, int stock, String unit, String size, String collection) {
        super(id, name, price, stock, unit);
        this.size = size;
        this.collectionName = collection;
    }

    @Override
    public String getDetails() {
        return name + " (" + collectionName + " Size: " + size + ")";
    }
}