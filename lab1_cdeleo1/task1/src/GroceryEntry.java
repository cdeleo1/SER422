package task1;

public class GroceryEntry {
    private String itemname;
    private String quantity;
    private String category;
    private String brand;
    private String aisle;

    public GroceryEntry(String itemname, String quantity, String category,
                String brand, String aisle)
    {
	this.itemname  = itemname;
	this.quantity  = quantity;
	this.category = category;
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
    { return itemname + "\n" + quantity + "\n" + category + "\n" + brand
            + "\n" + aisle; }
}



