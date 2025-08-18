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
import org.apache.pdfbox.pdmodel.PDPageContentStream;

@WebServlet(urlPatterns = {"/export"})
@MultipartConfig
public class ExportServlet extends HttpServlet {

    private static final Map<String, File> fontFiles = new HashMap<>();
    static {
        fontFiles.put("NotoSans-Regular.ttf",    new File("WEB-INF/fonts/NotoSans-Regular.ttf"));
        fontFiles.put("NotoSans-Bold.ttf",       new File("WEB-INF/fonts/NotoSans-Bold.ttf"));
        fontFiles.put("NotoSansTamil-Regular.ttf", new File("WEB-INF/fonts/NotoSansTamil-Regular.ttf"));
        fontFiles.put("NotoSansTelugu-Regular.ttf", new File("WEB-INF/fonts/NotoSansTelugu-Regular.ttf"));
        fontFiles.put("NotoSansBengali-Regular.ttf", new File("WEB-INF/fonts/NotoSansBengali-Regular.ttf"));
        fontFiles.put("NotoSansDevanagari-Regular.ttf", new File("WEB-INF/fonts/NotoSansDevanagari-Regular.ttf"));
        fontFiles.put("NotoSansSymbols-Regular.ttf", new File("WEB-INF/fonts/NotoSansSymbols-Regular.ttf"));
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

            // Extract patient details from request
            String firstName   = val(request.getParameter("firstName"));
            String lastName    = val(request.getParameter("lastName"));
            String dob         = val(request.getParameter("dob"));
            String gender      = val(request.getParameter("gender"));
            String contact     = val(request.getParameter("contact"));
            String altContact  = val(request.getParameter("alternative contact"));
            String email       = val(request.getParameter("email"));
            String address     = val(request.getParameter("address"));
            String aadhar      = val(request.getParameter("aadhar"));
            String bloodGroup  = val(request.getParameter("bloodGroup"));
            String medicalHistory = val(request.getParameter("medicalHistory"));
            String currentMedications = val(request.getParameter("currentMedications"));
            String allergies   = val(request.getParameter("allergies"));
            String insuranceProvider = val(request.getParameter("insuranceProvider"));
            String policyNumber = val(request.getParameter("policyNumber"));

            String visitDate   = val(request.getParameter("visitDate"));
            String consultation= val(request.getParameter("consultation"));
            String serviceDescription = val(request.getParameter("serviceDescription"));
            String testsOrdered = val(request.getParameter("testsOrdered"));
            String testResults  = val(request.getParameter("testResults"));
            String medications  = val(request.getParameter("medications"));
            String consultationFee = val(request.getParameter("consultationFee"));
            String testCharges  = val(request.getParameter("testCharges"));
            String medicineCharges = val(request.getParameter("medicineCharges"));
            String otherCharges = val(request.getParameter("otherCharges"));
            String totalAmount  = val(request.getParameter("totalAmount"));
            String paymentStatus= val(request.getParameter("paymentStatus"));
            String notes        = val(request.getParameter("notes"));

            // Page setup
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                float margin = 50;
                float y = page.getMediaBox().getHeight() - margin;
                float width = page.getMediaBox().getWidth() - 2 * margin;
                float leading = 18;

                // Title
                content.beginText();
                content.setFont(bold, 18);
                content.newLineAtOffset(margin, y);
                content.showText("Healthcare Patient Registration Report");
                content.endText();
                y -= 30;

                // Patient Details
                content.beginText();
                content.setFont(bold, 14);
                content.newLineAtOffset(margin, y);
                content.showText("Personal Details:");
                content.endText();
                y -= 20;

                y = drawWrappedText(content, "Name: " + firstName + " " + lastName, regular, 12, margin, y, width, leading);
                y = drawWrappedText(content, "DOB: " + dob + "   Gender: " + gender, regular, 12, margin, y, width, leading);
                y = drawWrappedText(content, "Contact: " + contact + " | Alt: " + altContact, regular, 12, margin, y, width, leading);
                y = drawWrappedText(content, "Email: " + email, regular, 12, margin, y, width, leading);
                y = drawWrappedText(content, "Address: " + address, regular, 12, margin, y, width, leading);
                y = drawWrappedText(content, "Aadhar: " + aadhar + "   Blood Group: " + bloodGroup, regular, 12, margin, y, width, leading);
                y = drawWrappedText(content, "Medical History: " + medicalHistory, regular, 12, margin, y, width, leading);
                y = drawWrappedText(content, "Current Medications: " + currentMedications, regular, 12, margin, y, width, leading);
                y = drawWrappedText(content, "Allergies: " + allergies, regular, 12, margin, y, width, leading);
                y = drawWrappedText(content, "Insurance: " + insuranceProvider + " | Policy: " + policyNumber, regular, 12, margin, y, width, leading);

                y -= 20;
                content.beginText();
                content.setFont(bold, 14);
                content.newLineAtOffset(margin, y);
                content.showText("Visit & Billing:");
                content.endText();
                y -= 20;

                y = drawWrappedText(content, "Visit Date: " + visitDate, regular, 12, margin, y, width, leading);
                y = drawWrappedText(content, "Consultation: " + consultation, regular, 12, margin, y, width, leading);
                y = drawWrappedText(content, "Service: " + serviceDescription, regular, 12, margin, y, width, leading);
                y = drawWrappedText(content, "Tests Ordered: " + testsOrdered, regular, 12, margin, y, width, leading);
                y = drawWrappedText(content, "Test Results: " + testResults, regular, 12, margin, y, width, leading);
                y = drawWrappedText(content, "Medications Prescribed: " + medications, regular, 12, margin, y, width, leading);
                y = drawWrappedText(content, "Consultation Fee: ₹" + consultationFee, regular, 12, margin, y, width, leading);
                y = drawWrappedText(content, "Test Charges: ₹" + testCharges, regular, 12, margin, y, width, leading);
                y = drawWrappedText(content, "Medicine Charges: ₹" + medicineCharges, regular, 12, margin, y, width, leading);
                y = drawWrappedText(content, "Other Charges: ₹" + otherCharges, regular, 12, margin, y, width, leading);
                y = drawWrappedText(content, "Total Amount: ₹" + totalAmount + "   Payment Status: " + paymentStatus, regular, 12, margin, y, width, leading);
                y = drawWrappedText(content, "Notes: " + notes, regular, 12, margin, y, width, leading);
            }

            // Send PDF response
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"patient_report.pdf\"");
            document.save(response.getOutputStream());

        } catch (Exception e) {
            throw new ServletException("Error generating PDF: " + e.getMessage(), e);
        }
    }

    private static String val(String s) {
        return (s == null || s.trim().isEmpty()) ? "N/A" : s.trim();
    }

    // ✅ Helper to wrap text across lines
    private static float drawWrappedText(PDPageContentStream content, String text, PDType0Font font, int fontSize,
                                         float startX, float startY, float width, float leading) throws IOException {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            String temp = line + word + " ";
            float size = font.getStringWidth(temp) / 1000 * fontSize;
            if (size > width) {
                lines.add(line.toString());
                line = new StringBuilder(word).append(" ");
            } else {
                line.append(word).append(" ");
            }
        }
        if (!line.isEmpty()) lines.add(line.toString());

        for (String l : lines) {
            content.beginText();
            content.setFont(font, fontSize);
            content.newLineAtOffset(startX, startY);
            content.showText(l.trim());
            content.endText();
            startY -= leading;
        }
        return startY;
    }
}

