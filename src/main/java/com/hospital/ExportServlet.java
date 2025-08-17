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

        String patientName = request.getParameter("patientName");
        String patientId = request.getParameter("patientId");
        String diagnosis = request.getParameter("diagnosis");
        String languageText = request.getParameter("languageText"); // Telugu/Tamil/Bengali/₹ etc.

        if (patientName == null) patientName = "Unknown";
        if (patientId == null) patientId = "N/A";
        if (diagnosis == null) diagnosis = "N/A";
        if (languageText == null) languageText = "";

        // Ensure reports dir exists
        File reportsDir = new File(REPORTS_DIR);
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }

        // Create PDF
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        // ==== ✅ Load Unicode Fonts (NotoSans) ====
        PDFont regular;
        PDFont boldF;
        try {
            File fontDir = new File(getServletContext().getRealPath("/WEB-INF/fonts"));
            File regularFontFile = new File(fontDir, "NotoSans-Regular.ttf");
            File boldFontFile = new File(fontDir, "NotoSans-Bold.ttf");

            if (regularFontFile.exists() && boldFontFile.exists()) {
                regular = PDType0Font.load(document, new FileInputStream(regularFontFile), true);
                boldF = PDType0Font.load(document, new FileInputStream(boldFontFile), true);
                System.out.println("✅ Loaded NotoSans Unicode fonts.");
            } else {
                throw new IOException("❌ NotoSans fonts not found in WEB-INF/fonts");
            }
        } catch (Exception e) {
            throw new ServletException("Cannot load Unicode fonts. Please check WEB-INF/fonts folder.", e);
        }

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.beginText();
            contentStream.setFont(boldF, 18);
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Patient Report");
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(regular, 12);
            contentStream.newLineAtOffset(50, 700);
            contentStream.showText("Patient Name: " + patientName);
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(regular, 12);
            contentStream.newLineAtOffset(50, 680);
            contentStream.showText("Patient ID: " + patientId);
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(regular, 12);
            contentStream.newLineAtOffset(50, 660);
            contentStream.showText("Diagnosis: " + diagnosis);
            contentStream.endText();

            // ✅ Add multilingual / special symbol text (₹, తెలుగు, தமிழ், বাংলা)
            contentStream.beginText();
            contentStream.setFont(regular, 12);
            contentStream.newLineAtOffset(50, 640);
            contentStream.showText("Additional Notes: " + languageText);
            contentStream.endText();
        }

        // Save PDF
        String fileName = "report_" + patientId + ".pdf";
        File pdfFile = new File(reportsDir, fileName);
        document.save(pdfFile);
        document.close();

        // Send PDF back
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        try (FileInputStream fis = new FileInputStream(pdfFile);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        }
    }
}

