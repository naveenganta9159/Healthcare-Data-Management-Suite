package com.hospital;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

@WebServlet("/generate")
public class GenerateReportServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String firstName = req.getParameter("firstName");
        String lastName = req.getParameter("lastName");

        // generate unique filename
        String fileName = firstName + "_" + lastName + "_" + System.currentTimeMillis() + ".pdf";

        File reportsDir = new File("/opt/hospital_reports/");
        if (!reportsDir.exists()) reportsDir.mkdirs();

        File pdfFile = new File(reportsDir, fileName);

        // --- create simple PDF with PDFBox ---
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage();
            doc.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
                cs.newLineAtOffset(50, 700);
                cs.showText("Patient Report for " + firstName + " " + lastName);
                cs.endText();
            }

            doc.save(pdfFile);
        }

        // Redirect to reports page
        resp.sendRedirect("reports");
    }
}

