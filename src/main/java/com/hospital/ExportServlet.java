package com.hospital;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

@WebServlet(urlPatterns = {"/export", "/exportPDF"})
@MultipartConfig
public class ExportServlet extends HttpServlet {

@Override
protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    try (PDDocument document = new PDDocument()) {

        // Load fonts
        PDType0Font regular    = PDType0Font.load(document, fontFiles.get("NotoSans-Regular.ttf"));
        PDType0Font bold       = PDType0Font.load(document, fontFiles.get("NotoSans-Bold.ttf"));
        PDType0Font tamil      = PDType0Font.load(document, fontFiles.get("NotoSansTamil-Regular.ttf"));
        PDType0Font telugu     = PDType0Font.load(document, fontFiles.get("NotoSansTelugu-Regular.ttf"));
        PDType0Font bengali    = PDType0Font.load(document, fontFiles.get("NotoSansBengali-Regular.ttf"));
        PDType0Font devanagari = PDType0Font.load(document, fontFiles.get("NotoSansDevanagari-Regular.ttf"));
        PDType0Font symbols    = PDType0Font.load(document, fontFiles.get("NotoSansSymbols-Regular.ttf"));

        // ✅ Extract fields from request
        String firstName   = request.getParameter("firstName");
        String lastName    = request.getParameter("lastName");
        String dob         = request.getParameter("dob");
        String gender      = request.getParameter("gender");
        String contact     = request.getParameter("contact");
        String altContact  = request.getParameter("altContact");
        String email       = request.getParameter("email");
        String address     = request.getParameter("address");
        String aadhar      = request.getParameter("aadhar");
        String bloodGroup  = request.getParameter("bloodGroup");

        String medicalHistory = request.getParameter("medicalHistory");
        String medications    = request.getParameter("medications");
        String allergies      = request.getParameter("allergies");

        String insurance      = request.getParameter("insurance");
        String policyNumber   = request.getParameter("policyNumber");

        String visitDate      = request.getParameter("visitDate");
        String consultation   = request.getParameter("consultation");
        String serviceDesc    = request.getParameter("serviceDesc");
        String testsOrdered   = request.getParameter("testsOrdered");
        String testResults    = request.getParameter("testResults");
        String prescribedMeds = request.getParameter("prescribedMeds");

        String consultationFee= request.getParameter("consultationFee");
        String testCharges    = request.getParameter("testCharges");
        String medicineCharges= request.getParameter("medicineCharges");
        String otherCharges   = request.getParameter("otherCharges");
        String totalAmount    = request.getParameter("totalAmount");
        String paymentStatus  = request.getParameter("paymentStatus");

        String notes          = request.getParameter("notes");

        String lang           = request.getParameter("lang"); // en, ta, te, bn, hi

        // ✅ Add a page
        PDPage page = new PDPage();
        document.addPage(page);

        try (PDPageContentStream content = new PDPageContentStream(document, page)) {
            int y = 750;

            // Title
            content.beginText();
            content.setFont(bold, 18);
            content.newLineAtOffset(100, y);
            content.showText("Healthcare Patient Registration Report");
            content.endText();
            y -= 40;

            // Personal Details
            y = writeLine(content, bold, regular, "Personal Details", "", y, 16);
            y = writeLine(content, regular, regular, "First Name", firstName, y, 14);
            y = writeLine(content, regular, regular, "Last Name", lastName, y, 14);
            y = writeLine(content, regular, regular, "Date of Birth", dob, y, 14);
            y = writeLine(content, regular, regular, "Gender", gender, y, 14);
            y = writeLine(content, regular, regular, "Contact Number", contact, y, 14);
            y = writeLine(content, regular, regular, "Alternative Contact", altContact, y, 14);
            y = writeLine(content, regular, regular, "Email", email, y, 14);
            y = writeLine(content, regular, regular, "Address", address, y, 14);
            y = writeLine(content, regular, regular, "Aadhar Number", aadhar, y, 14);
            y = writeLine(content, regular, regular, "Blood Group", bloodGroup, y, 14);

            // Medical Info
            y = writeLine(content, bold, regular, "Medical Information", "", y - 10, 16);
            y = writeLine(content, regular, regular, "History", medicalHistory, y, 14);
            y = writeLine(content, regular, regular, "Medications", medications, y, 14);
            y = writeLine(content, regular, regular, "Allergies", allergies, y, 14);

            // Insurance
            y = writeLine(content, bold, regular, "Insurance", "", y - 10, 16);
            y = writeLine(content, regular, regular, "Provider", insurance, y, 14);
            y = writeLine(content, regular, regular, "Policy Number", policyNumber, y, 14);

            // Visit & Billing
            y = writeLine(content, bold, regular, "Visit & Billing", "", y - 10, 16);
            y = writeLine(content, regular, regular, "Visit Date", visitDate, y, 14);
            y = writeLine(content, regular, regular, "Consultation Type", consultation, y, 14);
            y = writeLine(content, regular, regular, "Service Description", serviceDesc, y, 14);
            y = writeLine(content, regular, regular, "Tests Ordered", testsOrdered, y, 14);
            y = writeLine(content, regular, regular, "Test Results", testResults, y, 14);
            y = writeLine(content, regular, regular, "Prescribed Medications", prescribedMeds, y, 14);
            y = writeLine(content, regular, regular, "Consultation Fee", consultationFee, y, 14);
            y = writeLine(content, regular, regular, "Test Charges", testCharges, y, 14);
            y = writeLine(content, regular, regular, "Medicine Charges", medicineCharges, y, 14);
            y = writeLine(content, regular, regular, "Other Charges", otherCharges, y, 14);
            y = writeLine(content, regular, regular, "Total Amount", totalAmount, y, 14);
            y = writeLine(content, regular, regular, "Payment Status", paymentStatus, y, 14);

            // Notes
            y = writeLine(content, bold, regular, "Notes", "", y - 10, 16);
            y = writeLine(content, regular, regular, "", notes, y, 14);
        }

        // ✅ Send PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"report.pdf\"");
        document.save(response.getOutputStream());

    } catch (Exception e) {
        throw new ServletException("Error generating PDF: " + e.getMessage(), e);
    }
}

/**
 * Helper method to print label + value in one line
 */
private int writeLine(PDPageContentStream content, PDType0Font labelFont, PDType0Font valueFont,
                      String label, String value, int y, int fontSize) throws IOException {
    if (value == null) value = "N/A";

    content.beginText();
    content.setFont(labelFont, fontSize);
    content.newLineAtOffset(100, y);
    if (!label.isEmpty()) {
        content.showText(label + ": ");
    }
    content.setFont(valueFont, fontSize);
    content.showText(value);
    content.endText();

    return y - 20;
}

