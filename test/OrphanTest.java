import org.junit.*;
import java.util.*;

import play.Logger;
import play.test.*;
import models.*;

public class OrphanTest extends UnitTest {

    @Test
    public void main() {

        Book book = new Book("Test book");
        book.name = "Test book";
        book.insert();
        Long bookId = book.id;

        Page page = new Page(book);
        page.insert();
        Long pageId = page.id;

        Sentence sentence = new Sentence(page);
        sentence.text = "lorem ipsum";
        sentence.insert();

        book.delete();

        assertEquals(0, Page.listByBook(bookId).size());
        //assertEquals(0, Sentence.listByPage(pageId).size());

    }

}
