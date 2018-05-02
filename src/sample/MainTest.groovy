package sample

class MainTest extends GroovyTestCase {
    void setUp() {
        super.setUp()
    }

    void searchButtonClick() {
        String result = Main.displayNoteCardUI("https://quizlet.com/6345837/astronomy-chapter-1-4-flash-cards/");
        assert result != null
        assert result != ""
        assert result == Credentials.readFromURL("https://quizlet.com/6345837/astronomy-chapter-1-4-flash-cards/")

    }
}
