package localapp;

import localapp.model.PreviousWeekOfNumber;
import localapp.service.HtmlReaderService;
import localapp.service.StatisticsService;
import localapp.view.Show;
import java.util.List;


public class Main {
	public static void main(String[] args) {
		HtmlReaderService reader = new HtmlReaderService();
		StatisticsService statistics = new StatisticsService();
		Show show = new Show(statistics);

		try {
			// load lottery number from website
			List<int[]> lottery5numbers =  reader.getAllNumbersOfLottery5();
			show.showNumberStatAboutWeeksAgo(lottery5numbers, 52, 18);
			
			
			// take only top 20 percentage of the numbers (--> 18 oldest numbers of the 90)
			List<PreviousWeekOfNumber> oldestNumbers = statistics.getOldestNumbers(lottery5numbers, 18);
			// show information
			show.select5number(oldestNumbers);

		} catch (Exception exp) {
			String error = String.format("Hiba történt! %s", exp.getMessage());
			System.out.println(error);
		}
		
	}
	
}