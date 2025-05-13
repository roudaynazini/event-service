package org.example.event_project.Utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.paint.Color;

/**
 * Custom TextField with search icon
 */
public class SearchTextField extends TextField {

    private final StackPane leftIcon;

    public SearchTextField() {
        super();

        // Create the search icon
        FontIcon searchIcon = new FontIcon(FontAwesomeSolid.SEARCH);
        searchIcon.setIconSize(16);
        searchIcon.setIconColor(Color.web("#ff8a00"));

        // Create container for icon
        leftIcon = new StackPane(searchIcon);
        leftIcon.setAlignment(Pos.CENTER);
        leftIcon.setPadding(new Insets(0, 5, 0, 5));

        // Style the text field
        this.getStyleClass().add("search-box");

        // Use custom padding to accommodate the icon instead of setLeft
        this.setPadding(new Insets(0, 10, 0, 30));

        // Default prompt text
        this.setPromptText("Rechercher un événement...");

        // Add a listener to ensure the icon is positioned correctly
        this.layoutBoundsProperty().addListener((obs, oldVal, newVal) -> {
            double height = newVal.getHeight();
            leftIcon.setPrefHeight(height);
        });

        // Create an HBox to contain both the icon and the textfield
        HBox parentContainer = new HBox();
        parentContainer.getChildren().add(leftIcon);

        // Set style to position the icon over the text field using CSS
        this.setStyle("-fx-padding: 4 4 4 30; -fx-background-insets: 0;");
    }

    public void setIconColor(Color color) {
        if (leftIcon.getChildren().get(0) instanceof FontIcon) {
            ((FontIcon) leftIcon.getChildren().get(0)).setIconColor(color);
        }
    }

    /**
     * Override the lookup method to position the icon relatively to the text field
     */
    @Override
    public void layoutChildren() {
        super.layoutChildren();

        // Position the icon on the left side of the text field
        double iconWidth = leftIcon.prefWidth(-1);
        double height = getHeight();
        leftIcon.resizeRelocate(10, 0, iconWidth, height);

        // If the SearchTextField is already in a parent, add the icon to the parent
        if (this.getParent() != null && !this.getParent().getChildrenUnmodifiable().contains(leftIcon)) {
            this.getParent().getChildrenUnmodifiable().add(leftIcon);
        }
    }
}
