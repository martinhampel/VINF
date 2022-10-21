import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) throws Exception {
        String filePath = "C:/Users/hampelm/Documents/škola/VINF/project/dataset/freebase-head-10000000";
        System.out.println("Zadajte rok z ktorého chcete nájsť človeka: ");
        Scanner inputScanner = new Scanner(System.in);
        String yearInput = inputScanner.nextLine();
        byBufferedReader(filePath, yearInput);
    }

    public static List getValues(String[] line) {
        List values = new ArrayList<>();
        Pattern tagPattern = Pattern.compile("^(.*?)>");
        Pattern quotesPattern = Pattern.compile("\"(.*?)\"");

        for (String data : line) {
            Matcher tagMatcher = tagPattern.matcher(data);
            Matcher quotesMatcher = quotesPattern.matcher(data);
            if (tagMatcher.find()) {
                values.add(tagMatcher.group(1));
            }
            if (quotesMatcher.find()) {
                values.add(quotesMatcher.group(1));
            }
        }
        return values;
    }

    public static Map<String, HashMap<String, String>> byBufferedReader(String filePath, String yearOption) {
        HashMap<String, HashMap<String, String>> map = new HashMap<>();
        String line;

        String objectId = null;
        String typeName = null;
        String typeValue = null;

        List lineData = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while ((line = reader.readLine()) != null) {
                String[] keyValuePair = line.split(
                        "((http:\\/\\/rdf.freebase.com\\/ns\\/)|(http:\\/\\/www.w3.org\\/[0-9]*\\/[0-9]*\\/[0-9]*-*))");
                if (keyValuePair.length > 1) {
                    lineData = getValues(keyValuePair);
                    if (lineData.size() == 3) {
                        objectId = (String) lineData.get(0);
                        typeName = (String) lineData.get(1);
                        typeValue = (String) lineData.get(2);
                    }
                    HashMap data = map.getOrDefault(objectId, new HashMap<>());
                    data.put(typeName, typeValue);
                    map.put(objectId, data);
                } else {
                    System.out.println("No Key:Value found in line, ignoring: " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, HashMap<String, String>> peopleMap = map.entrySet()
                .stream()
                .filter(theObject -> theObject.getValue().keySet().stream()
                        .anyMatch(theTypename -> theTypename.contains("people.person")))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, HashMap<String, String>> searchedPersons = peopleMap.entrySet()
                .stream()
                .filter(theObject -> theObject.getValue().values().stream()
                        .anyMatch(theTypeValue -> theTypeValue.contains(yearOption)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        System.out.println(searchedPersons.keySet().toString());
        return map;
    }
}
