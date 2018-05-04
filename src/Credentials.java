import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;

public class Credentials {
    private static String url = "r";
    private static Document quizletDoc;
    private static String notecardSet = "c";

    private static boolean populate() {
        boolean success = false;
        if (url.length() < 2){
            File inputFile = new File("credentials.xml");
            String xml = "";

            //read the file into a single string
            try {
                Scanner input = new Scanner(inputFile);
                while (input.hasNextLine()) {
                    xml += input.nextLine();
                }

            } catch (IOException e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }

            // PROCESS THE URL
            //This uses the XMLParser which does not assume html tags.
            Document credentialsDoc = Jsoup.parse(xml, "", Parser.xmlParser());
            //System.out.println(doc.select("credentials"));

            //get the URL
            Elements urlNodes = credentialsDoc.select("url");
            //There should be only one url node
            Element  urlNode = urlNodes.first();
            url = urlNode.text();
            //TODO:   recover the port # from urlNode

            //get the API key
            Elements apiNodes = credentialsDoc.select("quizlet");
            //There should be only one url node
            String apiKey = apiNodes.attr("key");
            String finalUrl = url + notecardSet + "?client_id=" + apiKey + "&whitespace=1";
            quizletDoc = Jsoup.parse(readFromURL(finalUrl), "", Parser.xmlParser());

        }
        // If no errors are in the doc, the fetch succeeded.
        if (quizletDoc.select("error").isEmpty()) success = true;
        return success;
    }

    public static String readFromURL(String urlString) {
        String data = "";
        try {
            URL url = new URL(urlString);
            BufferedReader input = new BufferedReader(new InputStreamReader(url.openStream()));
            String s;
            while((s = input.readLine()) !=null){
                data += s;
            }

            input.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }
        return data;
    }

    public static Document getQuizletDoc() {
//        populate();
        return populate() ? quizletDoc : null;
    }

    public static void setNotecardSet(String notecardSet) {
        Credentials.notecardSet = notecardSet;
    }
}
