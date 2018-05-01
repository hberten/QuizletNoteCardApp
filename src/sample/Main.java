/*
Hunter Berten
Hmwk06 JavaFX
This JavaFX app allows the user to search a US state and city, then displays the current weather in it.
 */

package sample;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.jsoup.nodes.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {
    private static TextField urlField = new TextField();
    private static VBox vBox = new VBox();
    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
//        vBox.setSpacing(10);
        stage = primaryStage;
        ScrollPane scrollPane = new ScrollPane();

        Image weatherUndergroundLogo = new Image("https://quizlet.com/static/ThisUsesQuizlet-Blue.png");

//        anchorPane.getChildren().addAll(gridPane, new ImageView(weatherUndergroundLogo));

        Label urlLabel = new Label("URL:");
        Button searchButton = new Button("Search");
        searchButton.setOnAction(x -> searchButtonClick());

        HBox upperHBox = new HBox(new ImageView(weatherUndergroundLogo), urlLabel, urlField, searchButton);
        upperHBox.setSpacing(10);
//        gridPane.add(new ImageView(weatherUndergroundLogo), 0, 0);
//
//        gridPane.add(urlLabel, 1, 0);
//        gridPane.add(urlField, 2, 0);

        vBox.getChildren().add(upperHBox);
        scrollPane.setContent(vBox);
        primaryStage.setTitle("Quizlet App");
        primaryStage.setScene(new Scene(scrollPane, 800, 500));
        primaryStage.show();
    }

    private static void searchButtonClick() {
        String url = urlField.getText();
        Pattern setPattern = Pattern.compile("https://quizlet.com/(\\d+)/.*");
        Matcher setMatcher = setPattern.matcher(url);
        String notecardSet = "";
        if (setMatcher.find()) {
            notecardSet = setMatcher.group(1);
        }
        Credentials.setNotecardSet(notecardSet);
        Document quizletDoc = Credentials.getQuizletDoc();

        if (quizletDoc == null) return;

        String docString = quizletDoc.toString();

        Pattern termPattern = Pattern.compile("\"term\": \"(.*?)\",");
        Matcher termMatcher = termPattern.matcher(docString);
        Pattern defPattern = Pattern.compile("\"definition\": \"(.*?)\",");
        Matcher defMatcher = defPattern.matcher(docString);
//        int i = 1;
        while (termMatcher.find()) {
            HBox termDefHBox = new HBox();
            termDefHBox.setBorder(
                new Border(
                    new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)
                )
            );
            termDefHBox.setSpacing(100);
            String term = termMatcher.group(1);
            Label termLabel = new Label(term);
            termLabel.setWrapText(true);
            termLabel.setPrefWidth(150);
            termDefHBox.getChildren().add(termLabel);
            if (defMatcher.find()) {
                String def = defMatcher.group(1);
                Label defLabel = new Label(def);
                defLabel.setWrapText(true);
                defLabel.setPrefWidth(stage.getWidth()-160);
                termDefHBox.getChildren().add(defLabel);
            }
            vBox.getChildren().add(termDefHBox);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
