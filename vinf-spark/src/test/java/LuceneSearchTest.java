import org.apache.lucene.search.TopDocs;
import org.apache.lucene.document.Document;

import org.fiit.lucene.Searcher;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class LuceneSearchTest {
public Searcher toTest;

    public void prepare() {
        toTest = new Searcher();
    }

    @Test
    public void searchTest() {
        prepare();
        String expected = "Theodor Hampe";
        TopDocs hits = toTest.search("Theodor Hampe");
        Document doc = toTest.getDocument(hits.scoreDocs[0]);

        assertEquals(expected, doc.getFields().get(1).toString().substring(30,43));
    }
}
