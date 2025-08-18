package com.hospital;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.*;

@WebServlet(urlPatterns = {"/export", "/exportPDF"})
public class ExportServlet extends HttpServlet {

    // Store loaded fonts in memory
    private final Map<String, File> fontFiles = new HashMap<>();

    // ✅ Utility method to resolve font path
    private File resolveFontPath(ServletContext context, String fontFile) {
        // First try WEB-INF/fonts
        String webInfPath = context.getRealPath("/WEB-INF/fonts/" + fontFile);
        if (webInfPath != null && new File(webInfPath).exists()) {
            return new File(webInfPath);
        }

        // Then try /fonts
        String fontsPath = context.getRealPath("/fonts/" + fontFile);
        if (fontsPath != null && new File(fontsPath).exists()) {
            return new File(fontsPath);
        }

        throw new RuntimeException("❌ Font file not found: " + fontFile);
    }

    @Override
    public void init() throws ServletException {
        try {
            ServletContext context = getServletContext();

            // ✅ Preload all required fonts
            String[] fontNames = {
                "NotoSans-Regular.ttf",
                "NotoSans-Bold.ttf",
                "NotoSansTamil-Regular.ttf",
                "NotoSansTelugu-Regular.ttf",
                "NotoSansBengali-Regular.ttf",
                "NotoSansDevanagari-Regular.ttf",
                "NotoSansSymbols-Regular.ttf"
            };

            for (String fontName : fontNames) {
                File f = resolveFontPath(context, fontName);
                fontFiles.put(fontName, f);
                System.out.println("✅ Loaded font path: " + f.getAbsolutePath());
            }

        } catch (Exception e) {
            throw new ServletException("Failed to initialize fonts: " + e.getMessage(), e);
        }
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

        // ✅ Extract patient details from request
        String patientName      = request.getParameter("patientName");
        String age              = request.getParameter("age");
        String gender           = request.getParameter("gender");
        String diagnosis        = request.getParameter("diagnosis");
        String dischargeSummary = request.getParameter("dischargeSummary");
        String lang             = request.getParameter("lang"); // en, ta, te, bn, hi

        if (patientName == null) patientName = "Unknown";
        if (age == null) age = "N/A";
        if (gender == null) gender = "N/A";
        if (diagnosis == null) diagnosis = "N/A";
        if (dischargeSummary == null) dischargeSummary = "No summary provided.";

        // ✅ Add a page
        PDPage page = new PDPage();
        document.addPage(page);

        try (PDPageContentStream content = new PDPageContentStream(document, page)) {
            int y = 720;

            // Title
            content.beginText();
            content.setFont(bold, 18);
            content.newLineAtOffset(100, y);
            content.showText("Hospital Discharge Report");
            content.endText();
            y -= 40;

            // Patient Details
            content.beginText();
            content.setFont(regular, 14);
            content.newLineAtOffset(100, y);
            content.showText("Patient Name: " + patientName);
            content.endText();
            y -= 25;

            content.beginText();
            content.setFont(regular, 14);
            content.newLineAtOffset(100, y);
            content.showText("Age: " + age + "   Gender: " + gender);
            content.endText();
            y -= 25;

            content.beginText();
            content.setFont(regular, 14);
            content.newLineAtOffset(100, y);
            content.showText("Diagnosis: " + diagnosis);
            content.endText();
            y -= 25;

            // Discharge Summary (multi-language support)
            content.beginText();
            content.setFont(bold, 14);
            content.newLineAtOffset(100, y);
            content.showText("Discharge Summary:");
            content.endText();
            y -= 25;

            content.beginText();
            PDType0Font chosenFont = regular; // default
            if ("ta".equals(lang)) chosenFont = tamil;
            else if ("te".equals(lang)) chosenFont = telugu;
            else if ("bn".equals(lang)) chosenFont = bengali;
            else if ("hi".equals(lang)) chosenFont = devanagari;

            content.setFont(chosenFont, 14);
            content.newLineAtOffset(100, y);
            content.showText(dischargeSummary);
            content.endText();
        }

        // ✅ Send PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"report.pdf\"");
        document.save(response.getOutputStream());

    } catch (Exception e) {
        throw new ServletException("Error generating PDF: " + e.getMessage(), e);
    }
}

