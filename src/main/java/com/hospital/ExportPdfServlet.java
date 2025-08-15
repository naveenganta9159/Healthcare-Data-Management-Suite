package com.hospital;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;

@WebServlet("/ExportPdfServlet")
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
            throw new ServletException("Error loading fonts", e);
        }
    }

    private PDFont getFontForText(String text) {
        for (PDFont font : fonts) {
            if (supportsAllChars(font, text)) {
                return font;
            }
        }
        return fonts.get(0); // fallback
    }

    private boolean supportsAllChars(PDFont font, String text) {
        for (char c : text.toCharArray()) {
            try {
                font.encode(Character.toString(c));
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        String content = "Price: ₹500, नमस्ते, Hello, ☀️"; // Replace with your actual text

        PDPageContentStream contentStream = new PDPageContentStream(document, page);
        contentStream.beginText();
        contentStream.setLeading(14.5f);
        contentStream.newLineAtOffset(50, 750);

        // Split into words and pick font for each
        for (String word : content.split(" ")) {
            PDFont font = getFontForText(word);
            contentStream.setFont(font, 12);
            contentStream.showText(word + " ");
        }

        contentStream.endText();
        contentStream.close();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"export.pdf\"");

        document.save(response.getOutputStream());
        document.close();
    }
}

