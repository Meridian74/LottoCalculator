package localapp.service;

import localapp.config.LottoType;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.List;

public class FileReaderServiceLottery7Impl extends FileReaderService {

    public FileReaderServiceLottery7Impl(LottoType type) {
        super(type);
    }


    @Override
    public List<int[]> readNumbersFromRows(Elements rows, int numbersStartIndex) {
        List<int[]> results = new ArrayList<>();
        // A Skandináv lottó HTML-jében a táblázat fejléce duplasoros, ezért itt az index 1-től indul!
        for (int index = 1; index < rows.size(); index++) {
            var row = rows.get(index);
            // Skandináv lottónál alkalmanként 2 húzás is van, ez az első húzás 7 száma
            results.add(getValuesFromRow(numbersStartIndex, row));
            // A második húzás 7 száma
            results.add(getValuesFromRow(numbersStartIndex + 7, row));
        }
        return results;
    }

}