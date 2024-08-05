package localapp.service;

import localapp.config.LottoType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HtmlReaderService {
    protected final int type;
    protected final int allNumber;
    protected final int drawnedNumber;
    private final String url;


    public HtmlReaderService(LottoType type) {
        this.type = type.getType();
        this.allNumber = type.getAllNumber();
        this.drawnedNumber = type.getDrawnedNumber();
        this.url = type.getUrl();
    }

    private Document getDocument() {
        Document document = null;
        try {
            document = Jsoup.connect(this.url).get();
        } catch (IOException exp) {
            String error = String.format("Nem sikerült elérni ezt a file-t: %s", this.url);
            throw new IllegalStateException(error);
        }
        return document;
    }

    private int getNumbersStartIndex(Elements headers) {
        int numbersStartIndex = -1;

        // Determine the index of the desired columns
        for (int i = 0; i < headers.size(); i++) {
            String header = headers.get(i).text();
            if (header.equalsIgnoreCase("Számok")) {
                numbersStartIndex = i;
            }
        }

        if (numbersStartIndex == -1) {
            throw new IllegalStateException("Nem találtam meg a HTML táblázatban a 'Számok' nevű oszlopot, " +
                    "ami a kisorsolt számokat tartalmazza!");
        }

        return numbersStartIndex;
    }

    protected int[] getValuesFromRow(int numbersStartIndex, Element row) {
        Elements cells = row.select("td");
        int[] values = new int[this.type];
        if (drawnedNumber == 20 && cells.get(3).getAllElements().text().contains("2004.03.13")) {
            values = new int[]{1, 2, 17, 19, 20, 22, 24, 26, 30, 38, 39, 46, 47, 52, 57, 65, 66, 71, 75, 76};
            return values;
        }

        // Collect the number values
        int index = 0;
        for (int i = numbersStartIndex; i < numbersStartIndex + drawnedNumber; i++) {
            values[index] = Integer.parseInt(cells.get(i).text());
            index++;
        }
        return values;
    }

    protected List<int[]> readNumbersFromRows(Elements rows, int numbersStartIndex) {
        List<int[]> results = new ArrayList<>();
        for (Element row : rows) {
            results.add(getValuesFromRow(numbersStartIndex, row)); // store returning an int[] filled with lottery numbers
        }
        return results;
    }

    public List<int[]> getAllNumbers() {
        List<int[]> results;

        // Load the HTML document from the URL
        Document document = getDocument();

        // Find the table in the HTML document
        Element table = document.select("table").first();

        // Extract the header row
        assert table != null;
        Elements headers = Objects.requireNonNull(table.select("tr").first()).select("th");

        // find first column that contains lottery numbers
        int numbersStartIndex = getNumbersStartIndex(headers);

        // Extract the data rows
        Elements rows = table.select("tr:gt(0)"); // Skip the header row
        results = readNumbersFromRows(rows, numbersStartIndex);

        return results;
    }

}