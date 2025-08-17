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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Collect patient data
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

        // Ensure reports dir exists
        File reportsDir = new File(REPORTS_DIR);
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }

        // Unique filename
        String fileName = firstName + "_" + lastName + "_" + System.currentTimeMillis() + ".pdf";
        File pdfFile = new File(reportsDir, fileName);

        // ==== ✅ PDF GENERATION + VALIDATION ====
        try (PDDocument document = new PDDocument()) {

            // ==== ✅ Font Loading with Fallback ====
            PDFont regular;
            PDFont boldF;
            try {
                File fontDir = new File(getServletContext().getRealPath("/WEB-INF/fonts"));
                File regularFontFile = new File(fontDir, "NotoSans-Regular.ttf");
                File boldFontFile = new File(fontDir, "NotoSans-Bold.ttf");

                if (regularFontFile.exists() && boldFontFile.exists()) {
                    regular = PDType0Font.load(document, new FileInputStream(regularFontFile));
                    boldF = PDType0Font.load(document, new FileInputStream(boldFontFile));
                    System.out.println("✅ Loaded NotoSans fonts from " + fontDir.getAbsolutePath());
                } else {
                    throw new IOException("NotoSans fonts not found, using fallback.");
                }
            } catch (Exception e) {
                System.err.println("⚠️ Font load failed: " + e.getMessage() + " → Falling back to Helvetica");
                regular = PDType1Font.HELVETICA;
                boldF = PDType1Font.HELVETICA_BOLD;
            }

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                content.beginText();
                content.setLeading(20f);
                content.newLineAtOffset(50, 750);

                // Title
                content.setFont(boldF, 16);
                content.showText("Healthcare Patient Report");
                content.newLine();

                // Personal details
                content.setFont(regular, 12);
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

                // Visit & billing
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
            }

            // Save PDF
            document.save(pdfFile);
        }

        // ✅ Validation step
        try (PDDocument testDoc = PDDocument.load(pdfFile)) {
            if (testDoc.getNumberOfPages() == 0) {
                throw new IOException("Generated PDF has 0 pages → invalid.");
            }
            System.out.println("✅ PDF generated successfully: " + pdfFile.getAbsolutePath());
        } catch (Exception e) {
            if (pdfFile.exists()) {
                pdfFile.delete();
            }
            throw new ServletException("PDF generation failed validation: " + e.getMessage(), e);
        }

        // Response to user
        response.setContentType("text/html");
        response.getWriter().println("<h2>Report generated successfully!</h2>");
        response.getWriter().println("<p><a href='ViewReport.jsp'>View Reports</a></p>");
    }
}

