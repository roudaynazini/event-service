package com.roudaynazini;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.roudaynazini.model.Contract;
import com.roudaynazini.model.Reservation;

public class PDFExporterTest {
    
    @Test
    void testContractExport(@TempDir Path tempDir) throws Exception {
        // Create a sample contract
        Contract contract = new Contract();
        contract.setId(1L);
        contract.setContractNumber("TEST-001");
        contract.setContractType("Standard");
        contract.setStatus("Active");
        contract.setStartDate(LocalDate.now());
        contract.setEndDate(LocalDate.now().plusDays(30));
        contract.setTotalAmount(1000.0);
        contract.setNotes("Test contract export");

        // Export the contract
        String pdfPath = tempDir.resolve("test_contract.pdf").toString();
        PDFExporter.exportContractToPDF(contract, pdfPath);

        // Verify the PDF was created
        File pdfFile = new File(pdfPath);
        assertTrue(pdfFile.exists(), "PDF file should be created");
        assertTrue(pdfFile.length() > 0, "PDF file should not be empty");
    }

    @Test
    void testReservationExport(@TempDir Path tempDir) throws Exception {
        // Create a sample reservation
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setClientName("Test Client");
        reservation.setEventDate(LocalDate.now());
        reservation.setStatus("Confirmed");
        reservation.setNotes("Test reservation export");

        // Export the reservation
        String pdfPath = tempDir.resolve("test_reservation.pdf").toString();
        PDFExporter.exportReservationToPDF(reservation, pdfPath);

        // Verify the PDF was created
        File pdfFile = new File(pdfPath);
        assertTrue(pdfFile.exists(), "PDF file should be created");
        assertTrue(pdfFile.length() > 0, "PDF file should not be empty");
    }
}