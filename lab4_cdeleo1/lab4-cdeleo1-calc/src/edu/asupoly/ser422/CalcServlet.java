package edu.asupoly.ser422;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.*;
import javax.servlet.http.*;


/**
 * @author Cristi DeLeo
 *
 */
@SuppressWarnings("serial")
public class CalcServlet extends HttpServlet {

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServlet#doGet(
     * javax.servlet.http.HttpServletRequest, 
     * javax.servlet.http.HttpServletResponse)
     */
    // GET 
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        
        StringBuffer pageBuf = new StringBuffer();
        double grade;
        
        String year = req.getParameter("year");
        String subject = req.getParameter("subject");

        if (year != null && !year.trim().isEmpty()) {
            pageBuf.append("<br/>Year: " + year);
        }
        
        if (subject != null && !subject.trim().isEmpty()) {
            pageBuf.append("<br/>Subject: " + subject);
        }
        
        // Create new CalcService
        CalcService calcService = null;
        
        try {
            calcService = CalcService.getService();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if (calcService == null) {
            pageBuf.append("\tSERVICE NOT AVAILABLE");
        } else {
            grade = calcService.calculateGrade(year, subject);
            pageBuf.append("\n\t<br/>Grade: " + grade);
        }

        // some generic setup - our content type and output stream
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        out.println(pageBuf.toString());
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        doGet(req, res);
    }
}
