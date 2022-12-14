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

        System.out.print("Pod??a ??oho chcete vyh??ad??va?? ??ud???\n\n 1 - pod??a mena ??loveka\n 2 - pod??a roku v ktorom si mysl??te ??e ??il\n 3 - meno + rok\n");
        search_type = scanner.nextLine();

        switch (search_type) {
            case "1": {
                System.out.print("Zadajte meno ??loveka: \n");
                name = scanner.nextLine();
                year = null;
                hits = searcher.search(name);
                break;
            }
            case "2": {
                System.out.print("Zadajte rok v ktorom pod??a V??s ??il: \n");
                year = scanner.nextLine();
                name = null;
                hits = searcher.search(year);
                break;
            }
            case "3": {
                System.out.print("Zadajte meno ??loveka: \n");
                name = scanner.nextLine();
                System.out.print("Zadajte rok v ktorom ??il: \n");
                year = scanner.nextLine();
                hits = searcher.search(name);
                break;
            }
            default: {
                name = null;
                year = null;
                hits = null;
                System.out.print("Zadali ste nespr??vnu mo??nos??.\n");
            }
        }

        if (hits.scoreDocs.length == 0) {
            System.out.println("??iadny ??lovek sa pod??a vstupn??ch parametrov nena??iel.");
            return;
        }

        doc = searcher.getDocument(hits.scoreDocs[0]);
        if (search_type.equals("3")) {
            boolean match = wasAlive(year, doc.get("birth_date"), doc.get("death_date"));
            if (match) {
                if (!doc.get("death_date").equals("null")) {
                    System.out.print("\nN??jden?? v??sledky:\n\nID v databaze: " + doc.get("id") + "\nCel?? meno: " + doc.get("name") + "\nD??tum narodenia: " + doc.get("birth_date") + "\nD??tum ??mrtia: " + doc.get("death_date") + "\n");
                } else {
                    System.out.print("\nN??jden?? v??sledky:\n\nID v databaze: " + doc.get("id") + "\nCel?? meno: " + doc.get("name") + "\nD??tum narodenia: " + doc.get("birth_date") + "\n");
                }
            } else {
                System.out.print("Dan?? ??lovek v po??adovanom roku ne??il." + "\n");
            }
        return;
        }
        System.out.print("\nN??jden?? v??sledky:\n\nID v databaze: " + doc.get("id") + "\nCel?? meno: " + doc.get("name") + "\nD??tum narodenia: " + doc.get("birth_date") + "\nD??tum ??mrtia: " + doc.get("death_date") + "\n" );
    }
}