package localapp;

import localapp.model.AgeHit;
import localapp.model.HitCase;
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

			// -1 összes hét, 18 -> 20%
			int range = 18;
			List<AgeHit> hits = statistics.getValidHitsWithWeekAgeIndexRange(lottery5numbers, -1, range);
			List<HitCase> hitCases = statistics.getMaximumCaseByHitIndex(hits);
			show.showHitCasesMaximum(hitCases);
			int bottom = hitCases.get(5).getBottomIndex();
			int upper = hitCases.get(5).getUpperIndex();
			List<PreviousWeekOfNumber> oldestNumbers = statistics.getOldestNumberWithBoundaries(lottery5numbers, bottom, upper);
			
			// show selectable Lottery numbers
			show.showLotteryNumbersCanChoose(oldestNumbers);

			// show your lottery numbers what you should you play
			show.select5number(oldestNumbers);
			System.out.print("alsó index: " + bottom);
			System.out.println(", felső index: " + upper);

		} catch (Exception exp) {
			String error = String.format("Hiba történt! %s", exp.getMessage());
			System.out.println(error);
		}
		
	}
	
}