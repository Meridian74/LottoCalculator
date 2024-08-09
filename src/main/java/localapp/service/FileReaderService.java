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


public class FileReaderService {
    protected final int type;
    protected final int allNumber;
    protected final int drawnedNumber;
    protected final String url;


    public FileReaderService(LottoType type) {
        this.type = type.getType();
        this.allNumber = type.getAllNumber();
        this.drawnedNumber = type.getDrawnedNumber();
        this.url = type.getUrl();
    }


    private Document getDocument() {
        Document document;
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

        // A fejléc sorban a 'Számok' tartalmú cella indexe adja meg melyik oszlopindextől kezdődnek a számok adatai
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
        int[] values = new int[this.drawnedNumber];

        // Kigyűjtjük a sorban szereplő számokat, sorban attól az oszlopindextől kezdve ahonna a számok adatai kezdődnek
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
            // elmentjük a lottószámokkal feltöltött int[] tömböt
            results.add(getValuesFromRow(numbersStartIndex, row));
        }
        return results;
    }

    public List<int[]> getAllNumbers() {
        List<int[]> results;
        Document document = getDocument();

        // Megkeressük a tábla elemet a HTML file-ban
        Element table = document.select("table").first();

        // Vesszük a fejléc sorát a táblázatnak
        assert table != null;
        Elements headers = Objects.requireNonNull(table.select("tr").first()).select("th");

        // Meghatározza a Számok kezdő oszlopának a helyét a táblázatban
        int numbersStartIndex = getNumbersStartIndex(headers);

        // Vesszük a táblázat sorait, kihagyva a fejléc sort
        Elements rows = table.select("tr:gt(0)");

        // Beolvassuk a számokat amik a sorokban vannak
        results = readNumbersFromRows(rows, numbersStartIndex);

        return results;
    }

}