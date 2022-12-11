package org.fiit;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.TopDocs;
import org.fiit.lucene.Indexer;
import org.fiit.lucene.Searcher;
import org.fiit.parser.Spark;

import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class Main {
    public static void main(String[] args) {

//        sparkParseTest();
//        luceneIndexTest();
        luceneSearchTest();
    }

    public static void sparkParseTest(){
        Spark parser = new Spark();
        parser.parse_people("data/freebase-head-1000000.gz");
    }

    public static void luceneIndexTest(){
        Indexer indexer = new Indexer();
        indexer.createIndex();
    }

    public static boolean wasAlive(String searchDate, String birthDate, String deathDate) {
        int deathYear;

        int searchYear = parseInt(searchDate.substring(0, 4));
        int birthYear = parseInt(birthDate.substring(0, 4));

        if (deathDate.equals("null")) {
            return searchYear >= birthYear;
        } else {
            deathYear = parseInt(deathDate.substring(0, 4));
        }

        return searchYear >= birthYear && searchYear <= deathYear;
    }

    public static void luceneSearchTest(){
        Searcher searcher = new Searcher();

        Scanner scanner = new Scanner(System.in);
        String search_type, name, year;
        TopDocs hits;
        Document doc;


        System.out.print(
                "\n" +
                "__________                    .__           ___________.__            .___            \n" +
                "\\______   \\ ____  ____ ______ |  |   ____   \\_   _____/|__| ____    __| _/___________ \n" +
                " |     ___// __ \\/  _ \\\\____ \\|  | _/ __ \\   |    __)  |  |/    \\  / __ |/ __ \\_  __ \\\n" +
                " |    |   \\  ___(  <_> )  |_> >  |_\\  ___/   |     \\   |  |   |  \\/ /_/ \\  ___/|  | \\/\n" +
                " |____|    \\___  >____/|   __/|____/\\___  >  \\___  /   |__|___|  /\\____ |\\___  >__|   \n" +
                "               \\/      |__|             \\/       \\/            \\/      \\/    \\/       \n\n");

        System.out.print("Podľa čoho chcete vyhľadávať ľudí?\n\n 1 - podľa mena človeka\n 2 - podľa roku v ktorom si myslíte že žil\n 3 - meno + rok\n");
        search_type = scanner.nextLine();

        switch (search_type) {
            case "1": {
                System.out.print("Zadajte meno človeka: \n");
                name = scanner.nextLine();
                year = null;
                hits = searcher.search(name);
                break;
            }
            case "2": {
                System.out.print("Zadajte rok v ktorom podľa Vás žil: \n");
                year = scanner.nextLine();
                name = null;
                hits = searcher.search(year);
                break;
            }
            case "3": {
                System.out.print("Zadajte meno človeka: \n");
                name = scanner.nextLine();
                System.out.print("Zadajte rok v ktorom žil: \n");
                year = scanner.nextLine();
                hits = searcher.search(name);
                break;
            }
            default: {
                name = null;
                year = null;
                hits = null;
                System.out.print("Zadali ste nesprávnu možnosť.\n");
            }
        }

        if (hits.scoreDocs.length == 0) {
            System.out.println("Žiadny človek sa podľa vstupných parametrov nenašiel.");
            return;
        }

        doc = searcher.getDocument(hits.scoreDocs[0]);
        if (search_type.equals("3")) {
            boolean match = wasAlive(year, doc.get("birth_date"), doc.get("death_date"));
            if (match) {
                if (!doc.get("death_date").equals("null")) {
                    System.out.print("\nNájdené výsledky:\n\nID v databaze: " + doc.get("id") + "\nCelé meno: " + doc.get("name") + "\nDátum narodenia: " + doc.get("birth_date") + "\nDátum úmrtia: " + doc.get("death_date") + "\n");
                } else {
                    System.out.print("\nNájdené výsledky:\n\nID v databaze: " + doc.get("id") + "\nCelé meno: " + doc.get("name") + "\nDátum narodenia: " + doc.get("birth_date") + "\n");
                }
            } else {
                System.out.print("Daný človek v požadovanom roku nežil." + "\n");
            }
        return;
        }
        System.out.print("\nNájdené výsledky:\n\nID v databaze: " + doc.get("id") + "\nCelé meno: " + doc.get("name") + "\nDátum narodenia: " + doc.get("birth_date") + "\nDátum úmrtia: " + doc.get("death_date") + "\n" );
    }
}