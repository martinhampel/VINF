package org.fiit.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


// Java class for performing search operations on an index of documents using Apache Lucene.
// The class has several instance variables, including an IndexSearcher object, a QueryParser object, and a Query object,
// all of which are used for searching the index. \The class also has a constructor that initializes these objects and sets up the IndexSearcher to read
// the index from a directory on the file system. The search method takes a search query as input, uses the QueryParser to parse the query,
// and then uses the IndexSearcher to search the index and return the top 10 results.
// The getDocument method takes a ScoreDoc object and uses the IndexSearcher to retrieve the full Document object that corresponds to the search result.\

public class Searcher {

    IndexSearcher indexSearcher;
    QueryParser queryParser;
    Query query;

    public Searcher() {
        try {
            Directory directory = FSDirectory.open(new File("lucene-index").toPath());
            indexSearcher = new IndexSearcher(DirectoryReader.open(directory));
            queryParser = new QueryParser("name", new StandardAnalyzer());
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public TopDocs search( String searchQuery) {
        try {
            query = queryParser.parse(searchQuery);
            return indexSearcher.search(query, 10);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Document getDocument(ScoreDoc scoreDoc) {
        try {
            return indexSearcher.doc(scoreDoc.doc);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}