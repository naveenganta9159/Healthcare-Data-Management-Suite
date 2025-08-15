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

@WebServlet("/exportPdf")
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"report.pdf\"");

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(fonts.get(0), 12); // Default font
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("Hello, PDF with multiple fonts!");
                contentStream.endText();
            }

            document.save(response.getOutputStream());
        }
    }
}

