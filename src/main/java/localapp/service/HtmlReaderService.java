package localapp.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HtmlReaderService {
	private static final String LOTTO5_URL = "https://bet.szerencsejatek.hu/cmsfiles/otos.html";
	
	private static Document getDocument(String url) {
		Document document = null;
		try {
			document = Jsoup.connect(url).get();
		} catch (IOException exp) {
			String error = String.format("Nem sikerült elérni ezt a file-t: %s", LOTTO5_URL);
			throw new IllegalStateException(error);
		}
		return document;
	}
	
	private static int getNumbersStartIndex(Elements headers) {
		int numbersStartIndex = -1;
		
		// Determine the index of the desired columns
		for (int i = 0; i < headers.size(); i++) {
			String header = headers.get(i).text();
			if (header.equalsIgnoreCase("Számok")) {
				numbersStartIndex = i;
			}
		}
		
		if (numbersStartIndex == -1) {
			throw new IllegalStateException("Nem találtam meg a HTML táblázatban a 'Számok' nevű oszlopot, ami a kisorsolt számokat tartalmazza!");
		}
		
		return numbersStartIndex;
	}
	
	private static byte[] getValuesFromRow(int numbersStartIndex, Element row) {
		Elements cells = row.select("td");
		byte[] values = new byte[5];
		
		// Collect the number values
		int index = 0;
		for (int i = numbersStartIndex; i < numbersStartIndex + 5; i++) {
			values[index] = Byte.parseByte(cells.get(i).text());
			index++;
		}
		return values;
	}
	
	public List<byte[]> getAllNumbersOfLottery5() {
		List<byte[]> results = new ArrayList<>();
		
		// Load the HTML document from the URL
		Document document = getDocument(LOTTO5_URL);
		// Find the table in the HTML document
		Element table = document.select("table").first();
		// Extract the header row
		assert table != null;
		Elements headers = Objects.requireNonNull(table.select("tr").first()).select("th");

		// find first column that contains lottery numbers
		int numbersStartIndex = getNumbersStartIndex(headers);
		// Extract the data rows
		Elements rows = table.select("tr:gt(0)"); // Skip the header row
		for (Element row : rows) {
			results.add(getValuesFromRow(numbersStartIndex, row)); // store returning an int[] filled with lottery numbers
		}
		
		return results;
	}

}