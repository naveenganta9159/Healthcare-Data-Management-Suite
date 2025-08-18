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

    private static final Map<String, File> fontFiles = new HashMap<>();
    static {
        String basePath = "/opt/tomcat/webapps/hospital-records/WEB-INF/fonts/";
        fontFiles.put("NotoSans-Regular.ttf", new File(basePath + "NotoSans-Regular.ttf"));
        fontFiles.put("NotoSans-Bold.ttf", new File(basePath + "NotoSans-Bold.ttf"));
        fontFiles.put("NotoSansTamil-Regular.ttf", new File(basePath + "NotoSansTamil-Regular.ttf"));
        fontFiles.put("NotoSansTelugu-Regular.ttf", new File(basePath + "NotoSansTelugu-Regular.ttf"));
        fontFiles.put("NotoSansBengali-Regular.ttf", new File(basePath + "NotoSansBengali-Regular.ttf"));
        fontFiles.put("NotoSansDevanagari-Regular.ttf", new File(basePath + "NotoSansDevanagari-Regular.ttf"));
        fontFiles.put("NotoSansSymbols-Regular.ttf", new File(basePath + "NotoSansSymbols-Regular.ttf"));
    }

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

            // ✅ Select language font
            String lang = request.getParameter("lang"); // en, ta, te, bn, hi
            PDType0Font activeFont = regular;
            if ("ta".equals(lang)) activeFont = tamil;
            else if ("te".equals(lang)) activeFont = telugu;
            else if ("bn".equals(lang)) activeFont = bengali;
            else if ("hi".equals(lang)) activeFont = devanagari;

            // ✅ Extract fields from request
            String patientId      = request.getParameter("patientId");   // NEW
            String doctorName     = request.getParameter("doctorName");  // NEW
            String department     = request.getParameter("department");  // NEW

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

            // Start first page
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream content = new PDPageContentStream(document, page);

            int y = 750;

            // Hospital Header
            y = writeLine(content, bold, bold, "Hospital Name", "ABC Multispeciality Hospital", y, 18);
            y = writeLine(content, regular, regular, "Patient Report", "", y, 16);
            y -= 20;

            // Patient + Doctor Info
            y = writeLine(content, bold, regular, "Patient ID", patientId, y, 14);
            y = writeLine(content, bold, regular, "Doctor", doctorName, y, 14);
            y = writeLine(content, bold, regular, "Department", department, y, 14);

            // Personal Details
            y = writeLine(content, bold, regular, "Personal Details", "", y - 10, 16);
            y = writeLine(content, regular, activeFont, "First Name", firstName, y, 14);
            y = writeLine(content, regular, activeFont, "Last Name", lastName, y, 14);
            y = writeLine(content, regular, activeFont, "Date of Birth", dob, y, 14);
            y = writeLine(content, regular, activeFont, "Gender", gender, y, 14);
            y = writeLine(content, regular, activeFont, "Contact Number", contact, y, 14);
            y = writeLine(content, regular, activeFont, "Alternative Contact", altContact, y, 14);
            y = writeLine(content, regular, activeFont, "Email", email, y, 14);
            y = writeMultiline(content, regular, activeFont, "Address", address, y, 14, document);
            y = writeLine(content, regular, activeFont, "Aadhar Number", aadhar, y, 14);
            y = writeLine(content, regular, activeFont, "Blood Group", bloodGroup, y, 14);

            // Medical Info
            y = writeLine(content, bold, regular, "Medical Information", "", y - 10, 16);
            y = writeMultiline(content, regular, activeFont, "History", medicalHistory, y, 14, document);
            y = writeMultiline(content, regular, activeFont, "Medications", medications, y, 14, document);
            y = writeMultiline(content, regular, activeFont, "Allergies", allergies, y, 14, document);

            // Insurance
            y = writeLine(content, bold, regular, "Insurance", "", y - 10, 16);
            y = writeLine(content, regular, activeFont, "Provider", insurance, y, 14);
            y = writeLine(content, regular, activeFont, "Policy Number", policyNumber, y, 14);

            // Visit & Billing
            y = writeLine(content, bold, regular, "Visit & Billing", "", y - 10, 16);
            y = writeLine(content, regular, activeFont, "Visit Date", visitDate, y, 14);
            y = writeLine(content, regular, activeFont, "Consultation Type", consultation, y, 14);
            y = writeMultiline(content, regular, activeFont, "Service Description", serviceDesc, y, 14, document);
            y = writeMultiline(content, regular, activeFont, "Tests Ordered", testsOrdered, y, 14, document);
            y = writeMultiline(content, regular, activeFont, "Test Results", testResults, y, 14, document);
            y = writeMultiline(content, regular, activeFont, "Prescribed Medications", prescribedMeds, y, 14, document);
            y = writeLine(content, regular, activeFont, "Consultation Fee", consultationFee, y, 14);
            y = writeLine(content, regular, activeFont, "Test Charges", testCharges, y, 14);
            y = writeLine(content, regular, activeFont, "Medicine Charges", medicineCharges, y, 14);
            y = writeLine(content, regular, activeFont, "Other Charges", otherCharges, y, 14);
            y = writeLine(content, regular, activeFont, "Total Amount", totalAmount, y, 14);
            y = writeLine(content, regular, activeFont, "Payment Status", paymentStatus, y, 14);

            // Notes
            y = writeLine(content, bold, regular, "Notes", "", y - 10, 16);
            y = writeMultiline(content, regular, activeFont, "", notes, y, 14, document);

            content.close();

            // ✅ Send PDF
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"report.pdf\"");
            document.save(response.getOutputStream());

        } catch (Exception e) {
            throw new ServletException("Error generating PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Helper method to print single line
     */
    private int writeLine(PDPageContentStream content, PDType0Font labelFont, PDType0Font valueFont,
                          String label, String value, int y, int fontSize) throws IOException {
        if (value == null || value.isEmpty()) value = "N/A";
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

    /**
     * Multiline writer with wrapping
     */
    private int writeMultiline(PDPageContentStream content, PDType0Font labelFont, PDType0Font valueFont,
                               String label, String value, int y, int fontSize, PDDocument doc) throws IOException {
        if (value == null || value.isEmpty()) value = "N/A";
        int maxWidth = 400; // wrap width
        String[] words = value.split(" ");
        StringBuilder line = new StringBuilder();
        List<String> lines = new ArrayList<>();

        for (String word : words) {
            String temp = line + word + " ";
            float size = valueFont.getStringWidth(temp) / 1000 * fontSize;
            if (size > maxWidth) {
                lines.add(line.toString());
                line = new StringBuilder(word).append(" ");
            } else {
                line.append(word).append(" ");
            }
        }
        lines.add(line.toString());

        // First line with label
        content.beginText();
        content.setFont(labelFont, fontSize);
        content.newLineAtOffset(100, y);
        if (!label.isEmpty()) content.showText(label + ": ");
        content.setFont(valueFont, fontSize);
        content.showText(lines.get(0).trim());
        content.endText();
        y -= 20;

        // Remaining lines
        for (int i = 1; i < lines.size(); i++) {
            content.beginText();
            content.setFont(valueFont, fontSize);
            content.newLineAtOffset(120, y); // indent
            content.showText(lines.get(i).trim());
            content.endText();
            y -= 20;
        }
        return y;
    }
}

