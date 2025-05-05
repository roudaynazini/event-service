package com.example.bouthaina.util;

import com.example.bouthaina.BackOffice.models.Categorie;
import com.example.bouthaina.BackOffice.models.Evenement;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Service pour exporter les événements au format HTML/CSS
 * (peut être utilisé pour l'impression ou pour créer un PDF)
 */
public class PdfExportService {

    /**
     * Génère un fichier HTML contenant la liste des événements
     */
    public static File exportEventsToHtml(List<Evenement> events, Map<Integer, Categorie> categoriesMap) {
        try {
            File tempFile = File.createTempFile("evenements_", ".html");
            FileOutputStream out = new FileOutputStream(tempFile);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>\n")
                    .append("<html lang=\"fr\">\n")
                    .append("<head>\n")
                    .append("    <meta charset=\"UTF-8\">\n")
                    .append("    <title>Liste des événements</title>\n")
                    .append("    <style>\n")
                    .append("        body { font-family: Arial, sans-serif; margin: 20px; }\n")
                    .append("        h1 { color: #6a11cb; text-align: center; margin-bottom: 30px; }\n")
                    .append("        .event { border: 1px solid #ddd; padding: 15px; margin-bottom: 20px; border-radius: 8px; }\n")
                    .append("        .event-title { color: #333; font-size: 18px; font-weight: bold; margin-bottom: 10px; }\n")
                    .append("        .event-category { color: #9b4dff; font-weight: bold; }\n")
                    .append("        .event-date { margin-bottom: 5px; }\n")
                    .append("        .event-location { margin-bottom: 10px; }\n")
                    .append("        .event-description { color: #555; }\n")
                    .append("        .header { display: flex; justify-content: space-between; }\n")
                    .append("        .badge { background: linear-gradient(to right, #9b4dff, #ff3377); color: white; padding: 2px 8px; border-radius: 10px; font-size: 12px; }\n")
                    .append("        .footer { text-align: center; margin-top: 30px; color: #777; font-size: 12px; }\n")
                    .append("        @media print {\n")
                    .append("            body { font-size: 12px; }\n")
                    .append("            .event { page-break-inside: avoid; }\n")
                    .append("        }\n")
                    .append("    </style>\n")
                    .append("</head>\n")
                    .append("<body>\n")
                    .append("    <h1>Liste des événements ConnectArt</h1>\n");

            for (Evenement event : events) {
                html.append("    <div class=\"event\">\n")
                        .append("        <div class=\"header\">\n")
                        .append("            <div class=\"event-title\">").append(event.getTitre()).append("</div>\n")
                        .append("            <div class=\"badge\">").append(event.getStatus()).append("</div>\n")
                        .append("        </div>\n");

                // Catégorie
                String categoryName = "Catégorie inconnue";
                Categorie categorie = categoriesMap.get(event.getCategorie());
                if (categorie != null) {
                    categoryName = categorie.getType();
                }
                html.append("        <div class=\"event-category\">").append(categoryName).append("</div>\n")
                        .append("        <div class=\"event-date\">Du ").append(event.getDateDebut().format(formatter))
                        .append(" au ").append(event.getDateFin().format(formatter)).append("</div>\n")
                        .append("        <div class=\"event-location\">Lieu: ").append(event.getLieu())
                        .append("</div>\n")
                        .append("        <div class=\"event-description\">").append(event.getDescription())
                        .append("</div>\n")
                        .append("    </div>\n");
            }

            html.append("    <div class=\"footer\">Document généré le ")
                    .append(java.time.LocalDate.now().format(formatter))
                    .append(" - ConnectArt</div>\n")
                    .append("</body>\n")
                    .append("</html>");

            out.write(html.toString().getBytes(StandardCharsets.UTF_8));
            out.close();

            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
