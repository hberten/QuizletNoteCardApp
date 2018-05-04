/*
Hunter Berten
Quizlet App
This application allows the user to enter a URL for a Quizlet note card set. It will then parse the set ID from this URL
and use the Quizlet API to grab the note cards and display them in a JavaFX UI. These note cards can then be saved as
JSON files in the form that they were obtained using the Quizlet API, or into text files in a more easily
human-readable format.
 */

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {
    private static TextField urlField;
    private static VBox vBox = new VBox();
    private static VBox termDefVBox = new VBox();
    private static int textSize = 18;
    private static String jsonString;
    private static Button zoomInButton;
    private static Button zoomOutButton;
    private static Stage stage;
    private static MenuButton exportToButton;

    @Override
    public void start(Stage primaryStage) {
        // Initializes static fields (must be done here to fix issue with test class)
        urlField = new TextField();
        zoomInButton = new Button("+");
        zoomOutButton = new Button("-");
        stage = primaryStage;
        exportToButton = new MenuButton("Export To...");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);

        // Gets the Quizlet logo and creates a view for it to display in
        ImageView weatherUndergroundLogo = new ImageView(new Image("https://quizlet.com/static/ThisUsesQuizlet-Blue.png"));

        // Creates URL label and Search button.
        Label urlLabel = new Label("URL:");
        urlLabel.setTextFill(Paint.valueOf("EEEEEE"));
        urlLabel.setFont(new Font("Arial", textSize));
        Button searchButton = new Button("Search");
        searchButton.setOnAction(x -> {
            setNoteCardSet(urlField.getText());
            displayNoteCardUI();
        });

        // Setup Open button
        Button openButton = new Button("Open");
        openButton.setOnAction(x -> openJsonFile());

        // Setup zoom in/out buttons
        zoomInButton.setOnAction(x -> increaseTextSize());
        zoomOutButton.setOnAction(x -> decreaseTextSize());

        // Set up "Export To..." MenuButton.
        exportToButton.setDisable(true);
        MenuItem exportToText = new MenuItem("Text");
        exportToText.setOnAction(x -> exportToText(jsonString));
        MenuItem exportToJSON = new MenuItem("JSON");
        exportToJSON.setOnAction(x -> exportToJSON(jsonString));
        exportToButton.getItems().addAll(exportToText, exportToJSON);

        // Creates the box at the top of the application and places elements into it.
        HBox upperHBox = new HBox(weatherUndergroundLogo, urlLabel, urlField, searchButton, zoomInButton, zoomOutButton,
            exportToButton, openButton);
        upperHBox.setSpacing(8);
        upperHBox.setAlignment(Pos.BASELINE_LEFT);
        upperHBox.setPadding(new Insets(0, 0, 20, 0));
        vBox.getChildren().addAll(upperHBox, termDefVBox);
        scrollPane.setContent(vBox);

        // Creates and shows the window
        primaryStage.setTitle("Quizlet App");
        Scene scene = new Scene(scrollPane, 800, 500, Color.BLACK);
        resizeWindowWidth(800);
        vBox.setPadding(new Insets(20, 40, 20, 40));
        scene.widthProperty().addListener((observable, oldValue, newValue) -> resizeWindowWidth(newValue));
        primaryStage.setScene(scene);
        primaryStage.show();

        // Style settings
        scene.getStylesheets().add("/stylesheet.css");
        vBox.setStyle("-fx-background-color: #216CCF;");
        scrollPane.setStyle("-fx-background-color: #216CCF;");
        termDefVBox.setStyle("-fx-background-color: #FFFFFF;" +
                             "-fx-background-radius: 10");
    }

    void exportToText(String exportString) {
        // Open FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TEXT FILE", "*.txt"));
        fileChooser.setTitle("Save To Text");
        fileChooser.setInitialFileName("NoteCards.txt");
        File workingDirectory = new File(System.getProperty("user.dir"));
        fileChooser.setInitialDirectory(workingDirectory);
        File file = fileChooser.showSaveDialog(stage);

        writeToTextFile(exportString, file);
    }

    static void writeToTextFile(String exportString, File file) {
        if (file != null) {
            try {
                // Write file
                FileWriter fileWriter = new FileWriter(file);
                Pattern termPattern = Pattern.compile("\"term\": \"(.*?)\",");
                Matcher termMatcher = termPattern.matcher(exportString);
                Pattern defPattern = Pattern.compile("\"definition\": \"(.*?)\",");
                Matcher defMatcher = defPattern.matcher(exportString);
                while (termMatcher.find()) {
                    String term = termMatcher.group(1);
                    fileWriter.write(term + ":\n");
                    if (defMatcher.find()) {
                        String def = defMatcher.group(1);
                        fileWriter.write(def + "\n\n");
                    }
                }
                fileWriter.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    void exportToJSON(String exportString) {
        // Open FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON FILE", "*.json"));
        fileChooser.setTitle("Save To JSON");
        fileChooser.setInitialFileName("NoteCards.json");
        File workingDirectory = new File(System.getProperty("user.dir"));
        fileChooser.setInitialDirectory(workingDirectory);
        File file = fileChooser.showSaveDialog(stage);

        writeToJSONFile(exportString, file);
    }

    static void writeToJSONFile(String exportString, File file) {
        if (file != null) {
            try {
                // Write file
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(exportString);
                fileWriter.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    static void setNoteCardSet(String url) {
        // Parses the set number out of the URL and sends it to the Credentials class
        Pattern setPattern = Pattern.compile("https://quizlet.com/(\\d+)/.*");
        Matcher setMatcher = setPattern.matcher(url);
        String notecardSet;
        if (setMatcher.find()) {
            notecardSet = setMatcher.group(1);
        } else {
            return;
        }
        Credentials.setNotecardSet(notecardSet);

        // Gets the quizlet JSON using the Quizlet API and the set obtained from the URL
        Document quizletDoc = Credentials.getQuizletDoc();
        if (quizletDoc != null) jsonString = quizletDoc.toString();
    }

    static String displayNoteCardUI() {
        if (jsonString == null || jsonString.equals("")) return null;

        termDefVBox.getChildren().clear();
        exportToButton.setDisable(false);

        // Creates a pattern matcher that can parse the JSON's terms and definitions. Because the extraction is simple,
        // only simple regular expressions are necessary.
        Pattern termPattern = Pattern.compile("\"term\": \"(.*?)\",");
        Matcher termMatcher = termPattern.matcher(jsonString);
        Pattern defPattern = Pattern.compile("\"definition\": \"(.*?)\",");
        Matcher defMatcher = defPattern.matcher(jsonString);

        while (termMatcher.find()) {
            // Setup termDefHBox for this term/definition set
            HBox termDefHBox = new HBox();
            termDefHBox.setBorder(
                new Border(
                    new BorderStroke(Color.valueOf("#216CCF"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)
                )
            );
            termDefHBox.setSpacing(40);

            // Get term and place label in HBox
            String term = termMatcher.group(1);
            term = term.replace("\\n", "\n");
            term = term.replace("\\\"", "\"");
            Label termLabel = new Label(term);
            termLabel.setTextFill(Paint.valueOf("#455358"));
            termLabel.setFont(new Font("Arial", textSize));
            termLabel.setWrapText(true);
            termLabel.setMinWidth(150);
            termLabel.setMaxWidth(150);
            termDefHBox.getChildren().add(termLabel);

            // Get definition and place label in HBox
            if (defMatcher.find()) {
                String def = defMatcher.group(1).replace("\\n", "\n");
                def = def.replace("\\n", "\n");
                def = def.replace("\\\"", "\"");
                Label defLabel = new Label(def);
                defLabel.setFont(new Font("Arial", textSize));
                defLabel.setTextFill(Paint.valueOf("#455358"));
                defLabel.setWrapText(true);
                termDefHBox.getChildren().add(defLabel);
            }

            termDefVBox.getChildren().add(termDefHBox);
        }
        return jsonString;
    }

    private void resizeWindowWidth(Number newValue) {
        // Keeps UI to scale with window
        int width = newValue.intValue();
        vBox.setMaxWidth(width-16);
        vBox.setMinWidth(width-16);
    }

    private void increaseTextSize() {
        // Increases the textSize variable and updates the UI to reflect it
        textSize += 2;
        displayNoteCardUI();
        if (textSize >= 32) zoomInButton.setDisable(true);
        zoomOutButton.setDisable(false);
    }

    private void decreaseTextSize() {
        // Decreases the textSize variable and updates the UI to reflect it
        textSize -= 2;
        displayNoteCardUI();
        if (textSize <= 8) zoomOutButton.setDisable(true);
        zoomInButton.setDisable(false);
    }

    private void openJsonFile() {
        // Opens a JSON file of note cards and displays it in the UI
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON FILE", "*.json"));
        fileChooser.setTitle("Open JSON");
        File workingDirectory = new File(System.getProperty("user.dir"));
        fileChooser.setInitialDirectory(workingDirectory);
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                // Parse file
                jsonString = "";
                Scanner input = new Scanner(file);
                while (input.hasNext()) {
                    jsonString += input.nextLine();
                }

                // Display file
                displayNoteCardUI();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public static void main(String[] args) { launch(args); }
}
