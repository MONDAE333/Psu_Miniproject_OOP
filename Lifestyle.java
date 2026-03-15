public class Lifestyle extends Product {
    private String volume;

    public Lifestyle(String id, String name, double price, int stock, String unit, String volume) {
        super(id, name, price, stock, unit);
        this.volume = volume;
    }

    @Override
    public String getDetails() {
        return name + " (Vol: " + volume + ")";
    }
}