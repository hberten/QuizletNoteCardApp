import org.junit.Test

class MainTest extends GroovyTestCase {
    String testJsonFile = "{\n" +
            "    \"id\": 415,\n" +
            "    \"url\": \"https://quizlet.com/415/us-state-capitals-flash-cards/\",\n" +
            "    \"title\": \"U.S. State Capitals\",\n" +
            "    \"created_by\": \"asuth\",\n" +
            "    \"term_count\": 50,\n" +
            "    \"created_date\": 1144296408,\n" +
            "    \"modified_date\": 1478298235,\n" +
            "    \"published_date\": 1144296408,\n" +
            "    \"has_images\": false,\n" +
            "    \"subjects\": [],\n" +
            "    \"visibility\": \"public\",\n" +
            "    \"editable\": \"only_me\",\n" +
            "    \"has_access\": true,\n" +
            "    \"can_edit\": false,\n" +
            "    \"description\": \"\",\n" +
            "    \"lang_terms\": \"en\",\n" +
            "    \"lang_definitions\": \"en\",\n" +
            "    \"password_use\": 0,\n" +
            "    \"password_edit\": 0,\n" +
            "    \"access_type\": 2,\n" +
            "    \"creator_id\": 1,\n" +
            "    \"creator\": {\n" +
            "        \"username\": \"asuth\",\n" +
            "        \"account_type\": \"teacher\",\n" +
            "        \"profile_image\": \"https://up.quizlet.com/1-PD8Db-256s.jpg\",\n" +
            "        \"id\": 1\n" +
            "    },\n" +
            "    \"terms\": [\n" +
            "        {\n" +
            "            \"id\": 1277349735,\n" +
            "            \"term\": \"Alabama\",\n" +
            "            \"definition\": \"Montgomery\",\n" +
            "            \"image\": null,\n" +
            "            \"rank\": 0\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": 842513565,\n" +
            "            \"term\": \"Alaska\",\n" +
            "            \"definition\": \"Juneau\",\n" +
            "            \"image\": null,\n" +
            "            \"rank\": 1\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": 413281845,\n" +
            "            \"term\": \"Arizona\",\n" +
            "            \"definition\": \"Phoenix\",\n" +
            "            \"image\": null,\n" +
            "            \"rank\": 2\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": 413281846,\n" +
            "            \"term\": \"Arkansas\",\n" +
            "            \"definition\": \"Little Rock\",\n" +
            "            \"image\": null,\n" +
            "            \"rank\": 3\n" +
            "        },\n" +
            "        {\n" +
            "            \"id\": 413281847,\n" +
            "            \"term\": \"California\",\n" +
            "            \"definition\": \"Sacramento\",\n" +
            "            \"image\": null,\n" +
            "            \"rank\": 4\n" +
            "        },\n" +
            "    ]\n" +
            "}"

    void testWriteToTextFile() {
        // Create file and call method tested
        File file = new File("testTextFile.txt")
        Main.writeToTextFile(testJsonFile, file)

        // Check if file was created
        assert file.exists()

        // Get text from file
        Scanner input = new Scanner(file)
        String fileString = ""
        while (input.hasNext()) {
            fileString += input.nextLine()
        }

        // Ensure file text is correct
        assert fileString == "Alabama:" +
                "Montgomery" +
                "Alaska:" +
                "Juneau" +
                "Arizona:" +
                "Phoenix" +
                "Arkansas:" +
                "Little Rock" +
                "California:" +
                "Sacramento"
    }

    void testWriteToJSONFile() {
        // Create file and call method tested
        File file = new File("testJSONFile.txt")
        Main.writeToTextFile(testJsonFile, file)

        // Check if file was created
        assert file.exists()

        // Get text from file
        Scanner input = new Scanner(file)
        String fileString = ""
        while (input.hasNext()) {
            fileString += input.nextLine()
        }

        // Ensure file text is correct
        assert fileString == testJsonFile
    }

}
