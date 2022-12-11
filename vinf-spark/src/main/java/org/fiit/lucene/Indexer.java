package org.fiit.lucene;
import java.io.*;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;


//This class is an implementation of an indexer for creating an index of people data.
// The index is created using the Lucene library, and the data is read from a RandomAccessFile
// that is assumed to contain comma-separated values for each person's data.
// The createIndex method reads each line from the file, creates a Document object for the person using the createPersonDocument method,
// and adds the Document to the index using an IndexWriter. Finally, the close method is used to close the IndexWriter when indexing is complete.


public class Indexer {
    public RandomAccessFile parsedFile;
    public IndexWriter writerIndex;

    public Indexer() {
        try {
            // Create the index directory and the IndexWriter
            Directory indexDirectory = FSDirectory.open(new File("lucene-index").toPath());
            writerIndex = new IndexWriter(indexDirectory, new IndexWriterConfig(new StandardAnalyzer()));

            // Create the RandomAccessFile
            parsedFile = new RandomAccessFile("parsed-data/parsed-people/part-00000-a2647134-15db-4130-a671-69f5711547ee-c000.csv", "r");
        } catch (IOException e) {
            // Print the stack trace if an error occurs
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void createIndex() {
        String line;
        try {
            while ((line = parsedFile.readLine()) != null) {
                String[] columns = line.split(",");
                Document doc = createPersonDocument(columns[0], columns[2], columns[3], columns[4]);
                writerIndex.addDocument(doc);
            }
        } catch (IOException e) {
            System.err.println("Error creating IndexWriter: " + e.getMessage());
        } finally {
            // close the file and writer in a finally block to make sure
            // they are always closed, even if an exception is thrown
            close();
        }
    }

    public void close() {
        try {
            writerIndex.close();
        } catch (IOException e) {
            System.err.println("Error closing IndexWriter: " + e.getMessage());
        }
    }
    public Document createPersonDocument(String id, String name, String dateOfBirth, String dateOfDeath) {
        Document document = new Document();

        document.add(new TextField("id", id, TextField.Store.YES));
        document.add(new TextField("name", name, TextField.Store.YES));
        document.add(new TextField("birth_date", dateOfBirth, TextField.Store.YES));

        boolean isDeceased = !dateOfDeath.equals("\"\"");
        document.add(new TextField("is_deceased", Boolean.toString(isDeceased), TextField.Store.YES));
        document.add(new TextField("death_date", isDeceased ? dateOfDeath : "null", TextField.Store.YES));

        return document;
    }
}
