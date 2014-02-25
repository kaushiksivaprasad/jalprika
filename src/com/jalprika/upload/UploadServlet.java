package com.jalprika.upload;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import com.datumbox.opensource.examples.NaiveBayesExample;

/**
 * Servlet implementation class UploadServlet1
 */
@WebServlet("/UploadServlet")
public class UploadServlet extends HttpServlet {
	 private boolean isMultipart;
	    private String filePath;
	    private final int maxFileSize = 3000 * 1024;
	    private final int maxMemSize = 100 * 1024;
	    private File file;

	    BufferedWriter wr;

	    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
	            throws ServletException, IOException, Exception {
	        System.out.println(request.getParameter("email"));
	        System.out.println(request.getParameter("fullname"));
	        System.out.println(request.getParameter("company"));
	        String json = null;
	        isMultipart = ServletFileUpload.isMultipartContent(request);
	        response.setContentType("text/html");
	        java.io.PrintWriter out = response.getWriter();
	        if (!isMultipart) {
	            out.println("<html>");
	            out.println("<head>");
	            out.println("<title>Servlet upload</title>");
	            out.println("</head>");
	            out.println("<body>");
	            out.println("<p>No file uploaded</p>");
	            out.println("</body>");
	            out.println("</html>");
	            out.flush();
	            return;
	        }
	        DiskFileItemFactory factory = new DiskFileItemFactory();
	        factory.setSizeThreshold(maxMemSize);
	        factory.setRepository(new File("D:\\mchacks\\rejects\\"));
	        ServletFileUpload upload = new ServletFileUpload(factory);
	        upload.setSizeMax(maxFileSize);
	        
	        try {

	            List fileItems = upload.parseRequest(request);
	            Iterator i = fileItems.iterator();
	            out.println("<html>");
	            out.println("<head>");
	            out.println("<title>JalPrika</title>");
	            out.println("</head>");
	            out.println("<body>");
	            while (i.hasNext()) {
	                FileItem fi = (FileItem) i.next();
	                if (!fi.isFormField()) {
	                    String fileName = fi.getName();
	                    if (fileName.lastIndexOf("\\") >= 0) {
	                        file = new File(filePath
	                                + fileName.substring(fileName.lastIndexOf("\\")));
	                        wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filePath
	                                + fileName.substring(fileName.lastIndexOf("\\")) + ".txt"))));

	                    } else {
	                        file = new File(filePath
	                                + fileName.substring(fileName.lastIndexOf("\\") + 1));
	                        wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filePath
	                                + fileName.substring(fileName.lastIndexOf("\\") + 1) + ".txt"))));

	                    }

	                    fi.write(file);
	                    PDFTextStripper stripper = new PDFTextStripper();
	                    PDDocument pdfDoc = PDDocument.load(file);
	                    pdfDoc.save("copyOf_" + file.getName());
	                    stripper.writeText(pdfDoc, wr);
	                    pdfDoc.close();
	                    wr.close();

	                    json = ex.doCalculate(filePath
	                            + fileName.substring(fileName.lastIndexOf("\\") + 1) + ".txt");
	                    
	                    
	                }
	            }
	            out.println("</body>");
	            String dummy = "<script type='text/javascript'>parent.doAlert("+json+");</script>";
	            System.out.println("dummy : "+dummy);
	            out.println("<script type='text/javascript'>parent.doAlert("+json+");</script>");
	            out.println("</html>");
	        } catch (IOException ex) {
	            System.out.println(ex);
	        } catch (FileUploadException ex) {
	            System.out.println(ex);
	        } catch (COSVisitorException ex) {
	            System.out.println(ex);
	        }
	    }
	NaiveBayesExample  ex= null;
	    @Override
	    public void init() {
	        try {
	            // Get the file location where it would be stored.
	            filePath
	                    = getServletContext().getInitParameter("file-upload");
	            ex = new NaiveBayesExample();
	            ex.init();
	        } catch (IOException ex1) {
	            Logger.getLogger(UploadServlet.class.getName()).log(Level.SEVERE, null, ex1);
	        }
	    }
	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

	    /**
	     * Handles the HTTP <code>GET</code> method.
	     *
	     * @param request servlet request
	     * @param response servlet response
	     * @throws ServletException if a servlet-specific error occurs
	     * @throws IOException if an I/O error occurs
	     */
	    @Override
	    protected void doGet(HttpServletRequest request, HttpServletResponse response)
	            throws ServletException, IOException {
	        try {
	            processRequest(request, response);
	        } catch (Exception ex) {
	            Logger.getLogger(UploadServlet.class.getName()).log(Level.SEVERE, null, ex);
	        }
	    }

	    /**
	     * Handles the HTTP <code>POST</code> method.
	     *
	     * @param request servlet request
	     * @param response servlet response
	     * @throws ServletException if a servlet-specific error occurs
	     * @throws IOException if an I/O error occurs
	     */
	    @Override
	    protected void doPost(HttpServletRequest request, HttpServletResponse response)
	            throws ServletException, IOException {
	        try {
	            processRequest(request, response);
	        } catch (Exception ex) {
	            Logger.getLogger(UploadServlet.class.getName()).log(Level.SEVERE, null, ex);
	        }
	    }

	    /**
	     * Returns a short description of the servlet.
	     *
	     * @return a String containing servlet description
	     */
	    @Override
	    public String getServletInfo() {
	        return "Short description";
	    }// </editor-fold>

}
