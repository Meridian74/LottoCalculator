package localapp.service;

import localapp.config.LottoType;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.List;

public class HtmlReaderServiceLottery7Impl extends HtmlReaderService {

    public HtmlReaderServiceLottery7Impl(LottoType type) {
        super(type);
    }


    @Override
    public List<int[]> readNumbersFromRows(Elements rows, int numbersStartIndex) {
        List<int[]> results = new ArrayList<>();
        // lotter7 data table has double row header, so index must start from 1
        for (int index = 1; index < rows.size(); index++) {
            var row = rows.get(index);
            // store returning an int[] filled with lottery numbers, first 7 numbers
            results.add(getValuesFromRow(numbersStartIndex, row));
            // store returning an int[] filled with lottery numbers, second 7 numbers
            results.add(getValuesFromRow(numbersStartIndex + 7, row));
        }
        return results;
    }

}