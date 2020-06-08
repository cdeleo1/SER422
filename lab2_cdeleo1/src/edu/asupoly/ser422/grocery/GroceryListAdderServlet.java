package edu.asupoly.ser422.grocery;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javafx.util.Pair;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.json.JSONTokener;

import edu.asupoly.ser422.renderers.MyLab2Renderer;
import edu.asupoly.ser422.renderers.MyLab2RendererFactory;

/** 
    Servlet for handling CRUD operations on the "/grocery" resource.
 */
@SuppressWarnings("serial")
public class GroceryListAdderServlet extends HttpServlet {
	private static Logger log = Logger.getLogger(GroceryListAdderServlet.class.getName());
	private static String _filename = null;
	private static String _outFileName = null;
	private static String _refererURL = null;
	
	/**
       Method to initialize servlet behaviour, environment and config.
       @param config. First parameter. Servlet config.
       @return void.
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		_filename = config.getInitParameter(Constants.INIT_FILENAME_PROP);
		log.info("_filename: " + _filename);
		if (_filename == null || _filename.length() == 0) {
			throw new ServletException();
		} else {
			_outFileName = getServletContext().getRealPath(
                                "/WEB-INF/classes/" + _filename);
			log.info("_outFileName: " + _outFileName);
		}
		_refererURL = ServletHelper.getResourcePath(
                        Constants.LANDING_PAGE, getServletContext());
		
		log.info("refererURL: " + _refererURL);
	}

	/**
       This method handles the 'GET' HTTP requests.

       @param request. First parameter, represents the HTTP request to get the resource.
       @param response. Second parameter, represents the server's response.
       @return void.
	 */
	public void doGet(HttpServletRequest request, 
                HttpServletResponse response) 
			throws ServletException, IOException	{
		log.info("In doGet of adder not allowed");
		response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, 
                        "GET not supported by this servlet");
	}

	/**
       This method handles the 'POST' HTTP requests to the.

       @param request. First parameter, represents the HTTP request to get the resource.
       @param response. Second parameter, represents the server's response.
       @return void.
	 */
	public void doPost(HttpServletRequest request, 
                HttpServletResponse response) 
			throws ServletException, IOException {
		
		// we need 3 things to to compose our response: a code, a 
                // payload, and a content-type header
		int responseCode = HttpServletResponse.SC_CREATED;  // by default assuming success
		ArrayList<String> responsePayload = new ArrayList<String>();
		String responseContentType = 
                        ServletHelper.getResponseContentType(
                                request.getHeader("Accept"));
		MyLab2Renderer renderer = null;
		
		// Step 1. Process request headers. In this case Task 3 asks for 
                // an Accept header
		if (responseContentType == null) {
			log.info("Adder: No content type!");
			// didn't find a type we could use as a return type in 
                        // the Accept header, return a 406
			responseCode = HttpServletResponse.SC_NOT_ACCEPTABLE;
			responsePayload.add("This application understands " + Constants.CONTENT_HTML + ", " + Constants.CONTENT_TEXT + ", or " + Constants.CONTENT_JSON);
			// Set the response content type to HTML and pass back as such
			responseContentType = Constants.CONTENT_HTML;
			try {
				renderer = MyLab2RendererFactory.getRenderer(responseContentType, _refererURL);
			} catch (Throwable t) {
				// we have a big problem. We don't know how to render. Barf
				throw new ServletException(t.getMessage());
			}
			response.setStatus(responseCode);
			renderer.renderResponse(responsePayload, response.getWriter());
			return;
		}
		log.info("Adder: Content type is " + responseContentType);
		
		// Setting this up now. Once we know a response type we can use a renderer for it, even for errors.
		response.setContentType(responseContentType);
		try {
			renderer = MyLab2RendererFactory.getRenderer(responseContentType, _refererURL);	
		} catch (Throwable t) {
			// we have a big problem. We don't know how to render. Barf
			throw new ServletException(t.getMessage());
		}
		
		// Step 2. Process request parameters and request payload
		// Step 3. Perform processing (business logic). This could be refactored elsewhere
		Map<String,String[]> blueprint = request.getParameterMap();
		
		GroceryItem groceryItem = null;
		try {
			groceryItem = GroceryItem.getGroceryItemObjFromBlueprint(blueprint);
		} catch (MyHttpException ex) {
			log.info("Adder: exception trying to get a grocery item from blueprint");
			responseCode = ex.getResponseCode();
			responsePayload.add(ex.getMessage());
			response.setStatus(responseCode);
			renderer.renderResponse(responsePayload, response.getWriter());
			return;
		}
		
		// Step 4. Assemble response payload
		// Step 5. Set response headers (if not done so already)
		// At this point the groceryItem has to be valid so we can safely move forward and
		// set up the grocery list from the persistent store and then add to it. Put these
		// together in code as we need to synchronize the read then write to prevent lost updates
		// XXX synch block needed
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(_filename);
		Pair<Pair<Boolean, String>, GroceryList> loadBundle = ServletHelper.loadBootstrapFile(is);
		Pair<Boolean, String> loadStatus = loadBundle.getKey();
		GroceryList groceryListObj = loadBundle.getValue();
		Boolean hasErrored = loadStatus.getKey();
		is.close();
		if (hasErrored) {
			log.info("Adder: Error trying to read in the json input file");
			responseCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
			responsePayload.add(loadStatus.getValue());
			response.setStatus(responseCode);
			renderer.renderResponse(responsePayload, response.getWriter());
			return;
		}
		groceryListObj.addToGroceryList(groceryItem.getPname(), groceryItem, _outFileName);
		responseCode = HttpServletResponse.SC_CREATED;
		responsePayload.add("Successfully added: " + groceryItem.getPname() + "!");
		int currentItemCount = groceryListObj.getTotalItems();
		responsePayload.add("Total items in grocery list: " + currentItemCount);

		log.info("Adder: Successfully added item, going for render");
		// Step 6. Write out results. At this point we should know our our content type, our response code, and our payload.
		response.setStatus(responseCode);
		renderer.renderResponse(responsePayload, response.getWriter());
	}
}
