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

            // ✅ Resolve fonts directory inside WEB-INF
            String fontDir = getServletContext().getRealPath("/WEB-INF/fonts");
            if (fontDir == null) {
                throw new ServletException("Fonts folder not found: /WEB-INF/fonts");
            }

            // ✅ Build font map
            Map<String, File> fontFiles = new HashMap<>();
            fontFiles.put("NotoSans-Regular.ttf", new File(fontDir, "NotoSans-Regular.ttf"));
            fontFiles.put("NotoSans-Bold.ttf", new File(fontDir, "NotoSans-Bold.ttf"));
            fontFiles.put("NotoSansTamil-Regular.ttf", new File(fontDir, "NotoSansTamil-Regular.ttf"));
            fontFiles.put("NotoSansTelugu-Regular.ttf", new File(fontDir, "NotoSansTelugu-Regular.ttf"));
            fontFiles.put("NotoSansBengali-Regular.ttf", new File(fontDir, "NotoSansBengali-Regular.ttf"));
            fontFiles.put("NotoSansDevanagari-Regular.ttf", new File(fontDir, "NotoSansDevanagari-Regular.ttf"));
            fontFiles.put("NotoSansSymbols-Regular.ttf", new File(fontDir, "NotoSansSymbols-Regular.ttf"));

            // ✅ Load fonts
            PDType0Font regular    = PDType0Font.load(document, fontFiles.get("NotoSans-Regular.ttf"));
            PDType0Font bold       = PDType0Font.load(document, fontFiles.get("NotoSans-Bold.ttf"));
            PDType0Font tamil      = PDType0Font.load(document, fontFiles.get("NotoSansTamil-Regular.ttf"));
            PDType0Font telugu     = PDType0Font.load(document, fontFiles.get("NotoSansTelugu-Regular.ttf"));
            PDType0Font bengali    = PDType0Font.load(document, fontFiles.get("NotoSansBengali-Regular.ttf"));
            PDType0Font devanagari = PDType0Font.load(document, fontFiles.get("NotoSansDevanagari-Regular.ttf"));
            PDType0Font symbols    = PDType0Font.load(document, fontFiles.get("NotoSansSymbols-Regular.ttf"));

            // ✅ Extract all patient details from request (must match JSP form name="...")
            String patientName      = request.getParameter("patientName");
            String age              = request.getParameter("age");
            String gender           = request.getParameter("gender");
            String contactNumber    = request.getParameter("contactNumber");
            String address          = request.getParameter("address");
            String admissionDate    = request.getParameter("admissionDate");
            String dischargeDate    = request.getParameter("dischargeDate");
            String diagnosis        = request.getParameter("diagnosis");
            String treatment        = request.getParameter("treatment");
            String medications      = request.getParameter("medications");
            String followUp         = request.getParameter("followUp");
            String dischargeSummary = request.getParameter("dischargeSummary");
            String lang             = request.getParameter("lang"); // en, ta, te, bn, hi

            // ✅ Defaults if null
            if (patientName == null) patientName = "Unknown";
            if (age == null) age = "N/A";
            if (gender == null) gender = "N/A";
            if (contactNumber == null) contactNumber = "N/A";
            if (address == null) address = "N/A";
            if (admissionDate == null) admissionDate = "N/A";
            if (dischargeDate == null) dischargeDate = "N/A";
            if (diagnosis == null) diagnosis = "N/A";
            if (treatment == null) treatment = "N/A";
            if (medications == null) medications = "N/A";
            if (followUp == null) followUp = "N/A";
            if (dischargeSummary == null) dischargeSummary = "No summary provided.";

            // ✅ Create a new page
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {
                int y = 750;

                // Title
                content.beginText();
                content.setFont(bold, 18);
                content.newLineAtOffset(70, y);
                content.showText("Hospital Discharge Report");
                content.endText();
                y -= 40;

                // Patient Details Section
                content.beginText(); content.setFont(bold, 14); content.newLineAtOffset(70, y);
                content.showText("Patient Details"); content.endText(); 
                y -= 25;

                content.beginText(); content.setFont(regular, 12); content.newLineAtOffset(70, y);
                content.showText("Name: " + patientName); content.endText(); y -= 20;

                content.beginText(); content.setFont(regular, 12); content.newLineAtOffset(70, y);
                content.showText("Age: " + age + "   Gender: " + gender); content.endText(); y -= 20;

                content.beginText(); content.setFont(regular, 12); content.newLineAtOffset(70, y);
                content.showText("Contact: " + contactNumber); content.endText(); y -= 20;

                content.beginText(); content.setFont(regular, 12); content.newLineAtOffset(70, y);
                content.showText("Address: " + address); content.endText(); y -= 20;

                content.beginText(); content.setFont(regular, 12); content.newLineAtOffset(70, y);
                content.showText("Admission Date: " + admissionDate + "   Discharge Date: " + dischargeDate);
                content.endText(); y -= 30;

                // Medical Info Section
                content.beginText(); content.setFont(bold, 14); content.newLineAtOffset(70, y);
                content.showText("Medical Information"); content.endText(); 
                y -= 25;

                content.beginText(); content.setFont(regular, 12); content.newLineAtOffset(70, y);
                content.showText("Diagnosis: " + diagnosis); content.endText(); y -= 20;

                content.beginText(); content.setFont(regular, 12); content.newLineAtOffset(70, y);
                content.showText("Treatment: " + treatment); content.endText(); y -= 20;

                content.beginText(); content.setFont(regular, 12); content.newLineAtOffset(70, y);
                content.showText("Medications: " + medications); content.endText(); y -= 20;

                content.beginText(); content.setFont(regular, 12); content.newLineAtOffset(70, y);
                content.showText("Follow-Up: " + followUp); content.endText(); y -= 30;

                // Discharge Summary Section
                content.beginText(); content.setFont(bold, 14); content.newLineAtOffset(70, y);
                content.showText("Discharge Summary:"); content.endText(); y -= 25;

                // Choose font based on language
                PDType0Font chosenFont = regular; // default English
                if ("ta".equals(lang)) chosenFont = tamil;
                else if ("te".equals(lang)) chosenFont = telugu;
                else if ("bn".equals(lang)) chosenFont = bengali;
                else if ("hi".equals(lang)) chosenFont = devanagari;

                content.beginText();
                content.setFont(chosenFont, 12);
                content.newLineAtOffset(70, y);
                content.showText(dischargeSummary);
                content.endText();
            }

            // ✅ Send PDF response
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"report.pdf\"");
            document.save(response.getOutputStream());

        } catch (Exception e) {
            throw new ServletException("Error generating PDF: " + e.getMessage(), e);
        }
    }
}

