import org.fiit.lucene.Indexer;
import org.junit.Test;

public class LuceneIndexTest {
    public Indexer toTest;

    public void prepare() {
        toTest = new Indexer();
    }

    @Test()
    public void createIndex() {
        prepare();
        toTest.createIndex();
    }
}
