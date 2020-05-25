package task1;

public class GroceryEntry {
    private String name;
    private String quantity;
    private String custom;
    private String brand;
    private String aisle;

    public GroceryEntry(String name, String quantity, String custom,
                String brand, String aisle) {
	this.name  = name;
	this.quantity  = quantity;
	this.custom = custom;
        this.brand = brand;
        this.aisle = aisle;
    }

    public void changeQuantity(String newquantity) {
    	quantity = newquantity;
    	// This is here to introduce artifical latency for testing purposes
    	try {
			Thread.sleep(3000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    
    public String toString()
    { return name + "\n" + brand + "\n" + quantity + "\n" + custom 
            + "\n" + aisle; }
}



