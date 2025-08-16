package com.hospital;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

@WebServlet("/export")
public class ExportPdfServlet extends HttpServlet {

    private List<PDFont> fonts;

    @Override
    public void init() throws ServletException {
        try (PDDocument dummyDoc = new PDDocument()) {
            fonts = Arrays.asList(
                PDType0Font.load(dummyDoc, new File(getServletContext().getRealPath("/fonts/NotoSans-Regular.ttf"))),
                PDType0Font.load(dummyDoc, new File(getServletContext().getRealPath("/fonts/NotoSansSymbols-Regular.ttf"))),
                PDType0Font.load(dummyDoc, new File(getServletContext().getRealPath("/fonts/NotoSansDevanagari-Regular.ttf"))),
                PDType0Font.load(dummyDoc, new File(getServletContext().getRealPath("/fonts/NotoSansTamil-Regular.ttf")))
            );
        } catch (IOException e) {
            throw new ServletException("Error loading fonts: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ✅ Get form values
        String name = request.getParameter("name");
        String dob = request.getParameter("dob");
        String contact = request.getParameter("contact");
        String address = request.getParameter("address");

        generatePdf(response, name, dob, contact, address);
    }

    private void generatePdf(HttpServletResponse response,
                             String name, String dob, String contact, String address) throws IOException {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"patient_report.pdf\"");

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                // ✅ Title
                contentStream.beginText();
                contentStream.setFont(fonts.get(0), 16);
                contentStream.newLineAtOffset(220, 750);
                contentStream.showText("Patient Report");
                contentStream.endText();

                // ✅ Line separator
                contentStream.moveTo(50, 740);
                contentStream.lineTo(550, 740);
                contentStream.stroke();

                // ✅ Patient details
                int y = 700; // starting Y position
                int lineGap = 25;

                contentStream.beginText();
                contentStream.setFont(fonts.get(0), 12);
                contentStream.newLineAtOffset(50, y);

                contentStream.showText("Name: " + (name != null ? name : ""));
                contentStream.newLineAtOffset(0, -lineGap);

                contentStream.showText("Date of Birth: " + (dob != null ? dob : ""));
                contentStream.newLineAtOffset(0, -lineGap);

                contentStream.showText("Contact: " + (contact != null ? contact : ""));
                contentStream.newLineAtOffset(0, -lineGap);

                contentStream.showText("Address: " + (address != null ? address : ""));

                contentStream.endText();
            }

            document.save(response.getOutputStream());
        }
    }
}

