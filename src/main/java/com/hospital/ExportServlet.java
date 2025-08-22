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
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

@WebServlet(urlPatterns = {"/export", "/exportPDF"})
@MultipartConfig
public class ExportServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ✅ Read patient form data
        String patientId = request.getParameter("patientId");
        String doctorName = request.getParameter("doctorName");
        String department = request.getParameter("department");
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
        String consultation = request.getParameter("consultation");
        String serviceDesc = request.getParameter("serviceDesc");
        String testsOrdered = request.getParameter("testsOrdered");
        String testResults = request.getParameter("testResults");
        String prescribedMeds = request.getParameter("prescribedMeds");
        String consultationFee = request.getParameter("consultationFee");
        String testCharges = request.getParameter("testCharges");
        String medicineCharges = request.getParameter("medicineCharges");
        String otherCharges = request.getParameter("otherCharges");
        String totalAmount = request.getParameter("totalAmount");
        String paymentStatus = request.getParameter("paymentStatus");
        String notes = request.getParameter("notes");

        // ✅ Create PDF
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        // Load fonts
        PDType0Font bold;
        PDType0Font regular;
        try {
            bold = PDType0Font.load(document, new File(getServletContext().getRealPath("/WEB-INF/fonts/NotoSans-Bold.ttf")));
            regular = PDType0Font.load(document, new File(getServletContext().getRealPath("/WEB-INF/fonts/NotoSans-Regular.ttf")));
        } catch (Exception e) {
            throw new ServletException("Error loading fonts. Ensure fonts exist in WEB-INF/fonts", e);
        }

        PDPageContentStream content = new PDPageContentStream(document, page);

        // ✅ Header Section (Logo + Title)
        try {
            String logoPath = getServletContext().getRealPath("/WEB-INF/images/logo.png");
            File logoFile = new File(logoPath);
            if (logoFile.exists()) {
                PDImageXObject logo = PDImageXObject.createFromFile(logoPath, document);
                content.drawImage(logo, 50, page.getMediaBox().getHeight() - 100, 80, 60);
            }
        } catch (Exception e) {
            // Logo is optional, ignore if missing
        }

        // Hospital Name
        content.beginText();
        content.setFont(bold, 18);
        content.newLineAtOffset(150, page.getMediaBox().getHeight() - 60);
        content.showText("Multispeciality Hospital");
        content.endText();

        // Report Title
        content.beginText();
        content.setFont(bold, 14);
        content.newLineAtOffset(150, page.getMediaBox().getHeight() - 85);
        content.showText("Patient Report");
        content.endText();

        // ✅ Build rows for the table
        List<String[]> rows = Arrays.asList(
            new String[]{"Patient ID", patientId},
            new String[]{"Doctor", doctorName},
            new String[]{"Department", department},
            new String[]{"First Name", firstName},
            new String[]{"Last Name", lastName},
            new String[]{"Date of Birth", dob},
            new String[]{"Gender", gender},
            new String[]{"Contact Number", contact},
            new String[]{"Alternative Contact", altContact},
            new String[]{"Email", email},
            new String[]{"Address", address},
            new String[]{"Aadhar Number", aadhar},
            new String[]{"Blood Group", bloodGroup},
            new String[]{"History", medicalHistory},
            new String[]{"Medications", medications},
            new String[]{"Allergies", allergies},
            new String[]{"Insurance", insurance},
            new String[]{"Policy Number", policyNumber},
            new String[]{"Visit Date", visitDate},
            new String[]{"Consultation Type", consultation},
            new String[]{"Service Description", serviceDesc},
            new String[]{"Tests Ordered", testsOrdered},
            new String[]{"Test Results", testResults},
            new String[]{"Prescribed Medications", prescribedMeds},
            new String[]{"Consultation Fee", consultationFee},
            new String[]{"Test Charges", testCharges},
            new String[]{"Medicine Charges", medicineCharges},
            new String[]{"Other Charges", otherCharges},
            new String[]{"Total Amount", totalAmount},
            new String[]{"Payment Status", paymentStatus},
            new String[]{"Notes", notes}
        );

        // ✅ Draw the table below header
        drawTable(document, page, content, rows, bold, regular);

        content.close();

        // ✅ Write PDF to response
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"report.pdf\"");
        document.save(response.getOutputStream());
        document.close();
    }

    /**
     * ✅ Draws a simple 2-column table
     */
    private void drawTable(PDDocument doc, PDPage page, PDPageContentStream content,
                           List<String[]> rows, PDType0Font labelFont, PDType0Font valueFont) throws IOException {

        float margin = 50;
        float yStart = page.getMediaBox().getHeight() - 140; // move down below header
        float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
        float y = yStart;
        float rowHeight = 22f;
        float colWidth = tableWidth / 2f;
        float cellMargin = 5f;

        for (String[] row : rows) {
            String label = row[0];
            String value = (row[1] == null || row[1].isEmpty()) ? "N/A" : row[1];

            // Draw left cell
            content.addRect(margin, y - rowHeight, colWidth, rowHeight);
            content.beginText();
            content.setFont(labelFont, 11);
            content.newLineAtOffset(margin + cellMargin, y - 15);
            content.showText(label);
            content.endText();

            // Draw right cell
            content.addRect(margin + colWidth, y - rowHeight, colWidth, rowHeight);
            content.beginText();
            content.setFont(valueFont, 11);
            content.newLineAtOffset(margin + colWidth + cellMargin, y - 15);
            content.showText(value);
            content.endText();

            y -= rowHeight;
        }

        content.stroke();
    }
}

