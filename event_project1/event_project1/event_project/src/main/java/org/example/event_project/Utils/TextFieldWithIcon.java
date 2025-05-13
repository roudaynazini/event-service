package org.example.event_project.Utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Container that combines a TextField with an icon on the left
 */
public class TextFieldWithIcon extends HBox {

    private final TextField textField;
    private final StackPane iconContainer;

    public TextFieldWithIcon() {
        this(new TextField());
    }

    public TextFieldWithIcon(TextField textField) {
        super(5); // 5px spacing between elements
        this.textField = textField;

        // Create default search icon
        FontIcon searchIcon = new FontIcon(FontAwesomeSolid.SEARCH);
        searchIcon.setIconSize(16);
        searchIcon.setIconColor(Color.web("#ff8a00"));

        // Create container for icon
        iconContainer = new StackPane(searchIcon);
        iconContainer.setAlignment(Pos.CENTER);
        iconContainer.setPadding(new Insets(0, 5, 0, 5));

        // Style text field
        textField.getStyleClass().add("search-box");
        textField.setPadding(new Insets(4));

        // Style container
        this.setAlignment(Pos.CENTER_LEFT);
        this.getStyleClass().add("search-container");

        // Add components
        this.getChildren().addAll(iconContainer, textField);

        // Ensure text field takes all remaining horizontal space
        HBox.setHgrow(textField, javafx.scene.layout.Priority.ALWAYS);

        // Default prompt text
        textField.setPromptText("Rechercher un événement...");
    }

    public void setIcon(Node icon) {
        iconContainer.getChildren().clear();
        iconContainer.getChildren().add(icon);
    }

    public TextField getTextField() {
        return textField;
    }

    public StackPane getIconContainer() {
        return iconContainer;
    }
}
