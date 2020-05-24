package task1;

import java.io.*;
import java.util.*;

public class GroceryList {
	public static final String DEFAULT_FILENAME = "lab1data.txt";

	private Map<String, GroceryEntry> _glist = new HashMap<String, GroceryEntry>();

	public GroceryList() throws IOException {
		this(DEFAULT_FILENAME); 
	}
	public GroceryList(String fname) throws IOException {
		this(GroceryList.class.getClassLoader().getResourceAsStream(fname));
	}
	public GroceryList(InputStream is) throws IOException {
		this(new BufferedReader(new InputStreamReader(is)));
	}
	private GroceryList(BufferedReader br) throws IOException {	
		String itemname = null;
		String quantity = null;
		String category = null;

		try {
			String nextLine = null;
			while ( (nextLine=br.readLine()) != null)
			{
				itemname  = nextLine;
				quantity = br.readLine();
				category = br.readLine();
				addEntry(itemname, quantity, category);
			}
			br.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Error process grocery list");
			throw new IOException("Could not process grocery list file");
		}
	}

	public void saveGroceryList(String fname)
	{
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(fname));
			String[] entries = listEntries();
			for (int i = 0; i < entries.length; i++)
				pw.println(entries[i]);

			pw.close();
		}
		catch (Exception exc)
		{ 
			exc.printStackTrace(); 
			System.out.println("Error saving grocery list");
		}
	}

	public void editEntry(String itemname, String quantity, String category,
                String brand, String aisle) {
		GroceryEntry pentry = _glist.get(itemname);
		pentry.changeQuantity(quantity);
	}

	public void addEntry(String itemname, String quantity, String category,
                String brand, String aisle)
	{ 
		addEntry(itemname, new GroceryEntry(itemname, quantity, category,
                    brand, aisle));
	}

	public void addEntry(String itemname, GroceryEntry entry)
	{ _glist.put(itemname, entry); }

	public GroceryEntry removeEntry(String itemname) {
		return _glist.remove(itemname);
	}

	public String[] listEntries()
	{
		String[] rval = new String[_glist.size()];
		int i = 0;
		GroceryEntry nextEntry = null;
		for (Iterator<GroceryEntry> iter = _glist.values().iterator(); iter.hasNext();) {
			nextEntry = iter.next();
			rval[i++] = nextEntry.toString();
		}
		return rval;
	}
}
