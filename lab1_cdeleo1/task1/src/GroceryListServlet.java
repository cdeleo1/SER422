import javax.servlet.*;
import javax.servlet.http.*;

import java.io.*;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class GroceryListServlet extends HttpServlet {
    private static GroceryList _glist = null;
    private static Logger log = Logger.getLogger(GroceryListServlet.class.getName());

    public void init(ServletConfig config) throws ServletException {
	// if you forget this your getServletContext() will get a NPE! 
	super.init(config);
	String filename = config.getInitParameter("grocerylist");
	if (filename == null || filename.length() == 0) {
	    throw new ServletException();
	}
	// now get the phonebook file as an input stream
	log.info("Opening file " + filename);
	InputStream is = this.getClass().getClassLoader().getResourceAsStream(filename);
	try {
	    _glist = new GroceryList(is);
	} catch (IOException exc) {
	    exc.printStackTrace();
	    throw new ServletException(exc);
	}
	System.out.println("Loaded init param grocerylist with value " + filename);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) 
	throws ServletException, IOException	{

	res.setContentType("text/html");
	PrintWriter out= res.getWriter();
	out.println("<HTML><HEAD><TITLE>Lab 1: Task 1</TITLE></HEAD><BODY>");

	String action = req.getParameter("Action");
	if (action == null || action.length() == 0) {
	    out.println("No Action provided");
	    out.println("</BODY></HTML>"); 
	    return;
	}

	try {
	    if (action != null) {
		if (action.equals("Add")) {
		    GroceryEntry pentry = new GroceryEntry(
                            req.getParameter("name"),
                            req.getParameter("brand"), 
                            req.getParameter("aisle"),
                            req.getParameter("custom"),
                            req.getParameter("quantity"));

		    _glist.addEntry(req.getParameter("name"), pentry);
		    _glist.saveGroceryList(getServletContext().getRealPath("/WEB-INF/classes/" + 
									 GroceryList.DEFAULT_FILENAME));
		    out.println("Entry added to grocery list");
		} else if (action.equals("List")) {
		    String[] entries = _glist.listEntries();
		    for (int i = 0; i < entries.length; i++)
			out.println("<b>" + i + ":</b> " + entries[i] + "<br>");
		} else if (action.equals("Remove")) {
		    GroceryEntry pentry = _glist.removeEntry(req.getParameter("name"));
		    if (pentry == null) {
			out.println("No entry with grocery item " + req.getParameter("name"));
		    } else {
			out.println("Removed entry " + pentry);
			_glist.saveGroceryList(getServletContext().getRealPath(
                                "/WEB-INF/classes/" + GroceryList.DEFAULT_FILENAME));
		    }
		}
	    } else {
		out.println("<em>No valid Action provided in the parameters</em>");
	    }
	}
	catch (Exception exc)
	    {
		out.println("<p>Java exception satisfying request</p>");
		exc.printStackTrace();
	    }
	out.println("</BODY></HTML>");
    }
	
    public void doGet(HttpServletRequest req, HttpServletResponse res) 
	throws ServletException, IOException {
	res.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, 
                "GET not supported by this servlet");
    }
}
