package com.hospital;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.*;

@WebServlet(urlPatterns = {"/export", "/exportPDF"})
@MultipartConfig
public class ExportServlet extends HttpServlet {

    private static final String REPORTS_DIR = "/opt/hospital_reports";

    // font file references
    private File regularFontFile;
    private File boldFontFile;
    private File devanagariFontFile;
    private File tamilFontFile;
    private File teluguFontFile;
    private File bengaliFontFile;
    private File symbolsFontFile;

    @Override
    public void init() throws ServletException {
        String fontsPath = getServletContext().getRealPath("/fonts/");
        File fontsDir = new File(fontsPath);
        if (!fontsDir.exists()) {
            fontsDir.mkdirs();
        }

        // remember font files
        regularFontFile    = new File(fontsPath, "NotoSans-Regular.ttf");
        boldFontFile       = new File(fontsPath, "NotoSans-Bold.ttf");
        devanagariFontFile = new File(fontsPath, "NotoSansDevanagari-Regular.ttf");
        tamilFontFile      = new File(fontsPath, "NotoSansTamil-Regular.ttf");
        teluguFontFile     = new File(fontsPath, "NotoSansTelugu-Regular.ttf");
        bengaliFontFile    = new File(fontsPath, "NotoSansBengali-Regular.ttf");
        symbolsFontFile    = new File(fontsPath, "NotoSansSymbols-Regular.ttf");
    }

    // utility: load font per document
    private PDFont loadFont(PDDocument doc, File file, PDFont fallback) {
        try {
            if (file.exists()) {
                return PDType0Font.load(doc, file);
            } else {
                log("⚠ Missing font: " + file.getName() + " → fallback");
                return fallback != null ? fallback : PDType1Font.HELVETICA;
            }
        } catch (Exception e) {
            log("⚠ Error loading font " + file.getName() + ": " + e.getMessage());
            return fallback != null ? fallback : PDType1Font.HELVETICA;
        }
    }

    private PDFont selectFontForText(String text,
                                     boolean bold,
                                     PDFont regular,
                                     PDFont boldF,
                                     PDFont devanagari,
                                     PDFont tamil,
                                     PDFont telugu,
                                     PDFont bengali,
                                     PDFont symbols) {
        if (text == null || text.isEmpty()) return regular;

        char c = text.charAt(0);
        Character.UnicodeBlock block = Character.UnicodeBlock.of(c);

        if (block == Character.UnicodeBlock.DEVANAGARI) {
            return devanagari;
        } else if (block == Character.UnicodeBlock.TAMIL) {
            return tamil;
        } else if (block == Character.UnicodeBlock.TELUGU) {
            return telugu;
        } else if (block == Character.UnicodeBlock.BENGALI) {
            return bengali;
        } else if (Character.getType(c) == Character.MATH_SYMBOL
                || Character.getType(c) == Character.CURRENCY_SYMBOL
                || Character.getType(c) == Character.MODIFIER_SYMBOL
                || Character.getType(c) == Character.OTHER_SYMBOL) {
            return symbols;
        } else {
            return bold ? boldF : regular;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ensure reports directory exists
        File reportsDir = new File(REPORTS_DIR);
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }

        // collect patient details
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

        // output filename
        String fileName = firstName + "_" + lastName + "_" + System.currentTimeMillis() + ".pdf";
        File pdfFile = new File(REPORTS_DIR, fileName);

        // generate PDF
        try (PDDocument document = new PDDocument()) {
            // load fonts into this document
            PDFont regular   = loadFont(document, regularFontFile, null);
            PDFont boldF     = loadFont(document, boldFontFile, regular);
            PDFont devanagari= loadFont(document, devanagariFontFile, regular);
            PDFont tamil     = loadFont(document, tamilFontFile, regular);
            PDFont telugu    = loadFont(document, teluguFontFile, regular);
            PDFont bengali   = loadFont(document, bengaliFontFile, regular);
            PDFont symbols   = loadFont(document, symbolsFontFile, regular);

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.beginText();
                content.setLeading(20f);
                content.newLineAtOffset(50, 750);

                // title
                content.setFont(selectFontForText("Healthcare Patient Report", true,
                        regular, boldF, devanagari, tamil, telugu, bengali, symbols), 16);
                content.showText("Healthcare Patient Report");
                content.newLine();

                // personal details
                content.setFont(regular, 12);
                content.showText("Name: " + firstName + " " + lastName); content.newLine();
                content.showText("DOB: " + dob + " | Gender: " + gender); content.newLine();
                content.showText("Contact: " + contact + " | Alt: " + altContact); content.newLine();
                content.showText("Email: " + email); content.newLine();
                content.showText("Address: " + address); content.newLine();
                content.showText("Aadhar: " + aadhar + " | Blood Group: " + bloodGroup); content.newLine();

                // medical
                content.newLine();
                content.showText("Medical History: " + medicalHistory); content.newLine();
                content.showText("Medications: " + medications); content.newLine();
                content.showText("Allergies: " + allergies); content.newLine();

                // insurance
                content.newLine();
                content.showText("Insurance: " + insurance + " | Policy: " + policyNumber); content.newLine();

                // visit & billing
                content.newLine();
                content.showText("Visit Date: " + visitDate + " | Consultation: " + consultationType); content.newLine();
                content.showText("Service: " + serviceDescription); content.newLine();
                content.showText("Tests Ordered: " + testsOrdered); content.newLine();
                content.showText("Test Results: " + testResults); content.newLine();
                content.showText("Medications Prescribed: " + medicationsPrescribed); content.newLine();
                content.showText("Consultation Fee: ₹" + consultationFee); content.newLine();
                content.showText("Test Charges: ₹" + testCharges); content.newLine();
                content.showText("Medicine Charges: ₹" + medicineCharges); content.newLine();
                content.showText("Other Charges: ₹" + otherCharges); content.newLine();
                content.showText("Total Amount: ₹" + totalAmount + " | Status: " + paymentStatus); content.newLine();

                // notes
                content.newLine();
                content.showText("Notes: " + notes);

                content.endText();
            }

            document.save(pdfFile);
        }

        // redirect to reports list
        response.sendRedirect("viewReports.jsp");
    }
}

