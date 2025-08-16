package com.hospital;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.*;

@WebServlet("/export")
@MultipartConfig
public class ExportServlet extends HttpServlet {

    private static final String REPORTS_DIR = "/opt/hospital_reports";

    // Fonts
    private PDFont regularFont;
    private PDFont boldFont;
    private PDFont devanagariFont;
    private PDFont tamilFont;
    private PDFont teluguFont;
    private PDFont bengaliFont;
    private PDFont symbolsFont;

    @Override
    public void init() throws ServletException {
        try (PDDocument dummyDoc = new PDDocument()) {
            String fontsPath = getServletContext().getRealPath("/fonts/");

            // Ensure fonts directory exists
            File fontsDir = new File(fontsPath);
            if (!fontsDir.exists()) {
                if (fontsDir.mkdirs()) {
                    log("✅ Created missing /fonts directory at: " + fontsPath);
                } else {
                    log("⚠ Failed to create /fonts directory at: " + fontsPath);
                }
            }

            // Always load Regular first
            regularFont = loadFont(dummyDoc, fontsPath, "NotoSans-Regular.ttf", null);
            boldFont    = loadFont(dummyDoc, fontsPath, "NotoSans-Bold.ttf", regularFont);

            // Regional scripts
            devanagariFont = loadFont(dummyDoc, fontsPath, "NotoSansDevanagari-Regular.ttf", regularFont);
            tamilFont      = loadFont(dummyDoc, fontsPath, "NotoSansTamil-Regular.ttf", regularFont);
            teluguFont     = loadFont(dummyDoc, fontsPath, "NotoSansTelugu-Regular.ttf", regularFont);
            bengaliFont    = loadFont(dummyDoc, fontsPath, "NotoSansBengali-Regular.ttf", regularFont);

            // Symbols
            symbolsFont    = loadFont(dummyDoc, fontsPath, "NotoSansSymbols-Regular.ttf", regularFont);

        } catch (IOException e) {
            throw new ServletException("Error initializing fonts: " + e.getMessage(), e);
        }
    }

    private PDFont loadFont(PDDocument doc, String path, String fileName, PDFont fallback) {
        try {
            File fontFile = new File(path, fileName);
            if (fontFile.exists()) {
                return PDType0Font.load(doc, fontFile);
            } else {
                log("⚠ Font missing: " + fileName + " → falling back.");
                return fallback != null ? fallback : PDType1Font.HELVETICA;
            }
        } catch (Exception e) {
            log("⚠ Error loading font " + fileName + ": " + e.getMessage() + " → falling back.");
            return fallback != null ? fallback : PDType1Font.HELVETICA;
        }
    }

    private PDFont selectFontForText(String text, boolean bold) {
        if (text == null || text.isEmpty()) return regularFont;

        char c = text.charAt(0);
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);

        if (block == Character.UnicodeBlock.DEVANAGARI) {
            return devanagariFont;
        } else if (block == Character.UnicodeBlock.TAMIL) {
            return tamilFont;
        } else if (block == Character.UnicodeBlock.TELUGU) {
            return teluguFont;
        } else if (block == Character.UnicodeBlock.BENGALI) {
            return bengaliFont;
        } else if (Character.getType(c) == Character.MATH_SYMBOL
                || Character.getType(c) == Character.CURRENCY_SYMBOL
		|| Character.getType(c) == Character.MODIFIER_SYMBOL
                || Character.getType(c) == Character.OTHER_SYMBOL) {
            return symbolsFont;
        } else {
            return bold ? boldFont : regularFont;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Ensure reports directory exists
        File reportsDir = new File(REPORTS_DIR);
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }

        // Collect patient details
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String dob = request.getParameter("dob");
        String gender = request.getParameter("gender");
        String contact = request.getParameter("contact");
        String altContact = request.getParameter("altContact");
        String email = request.getParameter("email");
        String address = request.getParameter("address");
        String aadhar = request.getParameter("aadhar");
        String bloodGroup = request.getParameter("bloodGroup");

        String medicalHistory = request.getParameter("medicalHistory");
        String medications = request.getParameter("medications");
        String allergies = request.getParameter("allergies");

        String insurance = request.getParameter("insurance");
        String policyNumber = request.getParameter("policyNumber");

        String visitDate = request.getParameter("visitDate");
        String consultationType = request.getParameter("consultationType");
        String serviceDescription = request.getParameter("serviceDescription");
        String testsOrdered = request.getParameter("testsOrdered");
        String testResults = request.getParameter("testResults");
        String medicationsPrescribed = request.getParameter("medicationsPrescribed");
        String consultationFee = request.getParameter("consultationFee");
        String testCharges = request.getParameter("testCharges");
        String medicineCharges = request.getParameter("medicineCharges");
        String otherCharges = request.getParameter("otherCharges");
        String totalAmount = request.getParameter("totalAmount");
        String paymentStatus = request.getParameter("paymentStatus");

        String notes = request.getParameter("notes");

        // Create PDF filename
        String fileName = firstName + "_" + lastName + "_" + System.currentTimeMillis() + ".pdf";
        File pdfFile = new File(REPORTS_DIR, fileName);

        // Generate PDF
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);

            content.beginText();
            content.setLeading(20f);
            content.newLineAtOffset(50, 750);

            // Title
            content.setFont(selectFontForText("Healthcare Patient Report", true), 16);
            content.showText("Healthcare Patient Report");
            content.newLine();

            // Personal Details
            content.setFont(selectFontForText("", false), 12);
            content.showText("Name: " + firstName + " " + lastName);
            content.newLine();
            content.showText("DOB: " + dob + " | Gender: " + gender);
            content.newLine();
            content.showText("Contact: " + contact + " | Alt: " + altContact);
            content.newLine();
            content.showText("Email: " + email);
            content.newLine();
            content.showText("Address: " + address);
            content.newLine();
            content.showText("Aadhar: " + aadhar + " | Blood Group: " + bloodGroup);
            content.newLine();

            // Medical
            content.newLine();
            content.showText("Medical History: " + medicalHistory);
            content.newLine();
            content.showText("Medications: " + medications);
            content.newLine();
            content.showText("Allergies: " + allergies);
            content.newLine();

            // Insurance
            content.newLine();
            content.showText("Insurance: " + insurance + " | Policy: " + policyNumber);
            content.newLine();

            // Visit & Billing
            content.newLine();
            content.showText("Visit Date: " + visitDate + " | Consultation: " + consultationType);
            content.newLine();
            content.showText("Service: " + serviceDescription);
            content.newLine();
            content.showText("Tests Ordered: " + testsOrdered);
            content.newLine();
            content.showText("Test Results: " + testResults);
            content.newLine();
            content.showText("Medications Prescribed: " + medicationsPrescribed);
            content.newLine();
            content.showText("Consultation Fee: ₹" + consultationFee);
            content.newLine();
            content.showText("Test Charges: ₹" + testCharges);
            content.newLine();
            content.showText("Medicine Charges: ₹" + medicineCharges);
            content.newLine();
            content.showText("Other Charges: ₹" + otherCharges);
            content.newLine();
            content.showText("Total Amount: ₹" + totalAmount + " | Status: " + paymentStatus);
            content.newLine();

            // Notes
            content.newLine();
            content.showText("Notes: " + notes);

            content.endText();
            content.close();

            document.save(pdfFile);
        }

        // Redirect back to viewReports.jsp
        response.sendRedirect("viewReports.jsp");
    }
}

