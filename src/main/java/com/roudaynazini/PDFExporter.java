package com.roudaynazini;

import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.roudaynazini.model.Contract;
import com.roudaynazini.model.Reservation;

public class PDFExporter {

    private static final String LOGO_PATH = "src/main/resources/com/roudaynazini/images/logo.jpg";
    private static final String APP_NAME = "Event Up: Event Planning App";

    public static void exportContractToPDF(Contract contract, String filePath) throws IOException {
        try (PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Add header with logo and app name
            addHeader(document);

            // Add contract details
            document.add(new Paragraph("Contract Details").setBold().setFontSize(14));
            Table table = new Table(2);
            table.addCell("Contract ID");
            table.addCell(String.valueOf(contract.getId()));
            table.addCell("Contract Number");
            table.addCell(contract.getContractNumber());
            table.addCell("Type");
            table.addCell(contract.getContractType());
            table.addCell("Status");
            table.addCell(contract.getStatus());
            table.addCell("Start Date");
            table.addCell(contract.getStartDate() != null ? contract.getStartDate().toString() : "N/A");
            table.addCell("End Date");
            table.addCell(contract.getEndDate() != null ? contract.getEndDate().toString() : "N/A");
            table.addCell("Total Amount");
            table.addCell(String.valueOf(contract.getTotalAmount()));
            table.addCell("Notes");
            table.addCell(contract.getNotes() != null ? contract.getNotes() : "N/A");
            document.add(table);
        }
    }

    public static void exportReservationToPDF(Reservation reservation, String filePath) throws IOException {
        try (PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Add header with logo and app name
            addHeader(document);

            // Add reservation details
            document.add(new Paragraph("Reservation Details").setBold().setFontSize(14));
            Table table = new Table(2);
            table.addCell("Reservation ID");
            table.addCell(String.valueOf(reservation.getId()));
            table.addCell("Client Name");
            table.addCell(reservation.getClientName() != null ? reservation.getClientName() : "N/A");
            table.addCell("Event Date");
            table.addCell(reservation.getEventDate() != null ? reservation.getEventDate().toString() : "N/A");
            table.addCell("Status");
            table.addCell(reservation.getStatus() != null ? reservation.getStatus() : "N/A");
            table.addCell("Notes");
            table.addCell(reservation.getNotes() != null ? reservation.getNotes() : "N/A");
            document.add(table);
        }
    }

    private static void addHeader(Document document) throws IOException {
        // Add logo
        ImageData imageData = ImageDataFactory.create(LOGO_PATH);
        Image logo = new Image(imageData);
        logo.scaleToFit(50, 50);
        logo.setHorizontalAlignment(HorizontalAlignment.LEFT);
        document.add(logo);

        // Add app name
        Paragraph appName = new Paragraph(APP_NAME)
                .setFontSize(16)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
        document.add(appName);

        // Add some spacing
        document.add(new Paragraph("\n"));
    }
}