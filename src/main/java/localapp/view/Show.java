package localapp.view;

import localapp.model.LotteryDraw;
import localapp.model.PreviousWeekOfNumber;
import localapp.service.StatisticsService;

import java.util.*;

public class Show {
	
	private final StatisticsService statisticsService;
	
	public Show(StatisticsService statisticsService) {
		this.statisticsService = statisticsService;
	}
	
	
	public void select5number(List<PreviousWeekOfNumber> oldestNumbers) {
		int[] nums = new int[5];
		// mix the sequence of numbers...
		Collections.shuffle(oldestNumbers);
		// then take the first five numbers you can play in the Lottery :)
		for (int i = 0; i < nums.length; i++) {
			nums[i] = oldestNumbers.get(i).getNumber();
			System.out.println((i + 1) + ". szám: " + nums[i] + ", " + oldestNumbers.get(i).getWeeksAgo() + " hete húzták ki utoljára.");
		}
		
		System.out.println("Megjátszandó számok erre a hétre: " + Arrays.toString(nums));
	}
	
	public void showNumberStatAboutWeeksAgo(List<int[]> lotteryNumbers, int lastWeekLimit, int percentLimit) {
		List <LotteryDraw> data = this.statisticsService.getLotteryDrawPreviousWeeksHistory(lotteryNumbers);
		if (lastWeekLimit >= lotteryNumbers.size() - 1) {
			lastWeekLimit = lotteryNumbers.size() - 1;
		}
		if (percentLimit <= 0) {
			percentLimit = 1;
		}
		for (int index = 0; index < lastWeekLimit; index++) {
			System.out.println("\n\n***************************************");
			System.out.println(index + ". héttel ezelőtti kihúzott számok:");
			System.out.println("***************************************");
			
			List<PreviousWeekOfNumber> weekHistory = new ArrayList(data.get(index).getWeeksHistory());
			weekHistory.sort(Comparator.comparingInt(PreviousWeekOfNumber::getWeeksAgo).reversed());
			
			for (Integer lotteryNumber : data.get(index).getCurrentWeekNumbers()) {
				StringBuilder sb = new StringBuilder();
				
				// FYI: for example: 20% of 90 = 18. But 18 equals index of 17!
				int weekAgoBoundary = weekHistory.get(percentLimit - 1).getWeeksAgo();

				// FYI: 'index + 1': you need the status of one week earlier (when it has not yet been drawn).
				// FYI: 'lotteryNumber - 1': lottery numbers are from 1 to 90 and their indices are from 0 to 89.
				int weekAgoOflotteryNumber = data.get(index + 1).getWeeksHistory().get(lotteryNumber - 1).getWeeksAgo();
				if (weekAgoOflotteryNumber >= weekAgoBoundary) {
					sb.append("++ ");
				}
				else {
					sb.append("   ");
				}
				if (lotteryNumber <10) {
					sb.append(" ");
				}
				sb.append(lotteryNumber)
						.append(": ")
						// FYI: 'index + 1': you need the status of one week earlier (when it has not yet been drawn).
						// FYI: 'lotteryNumber - 1': lottery numbers are from 1 to 90 and their indices are from 0 to 89.
						.append(data.get(index + 1).getWeeksHistory().get(lotteryNumber - 1).getWeeksAgo()) // index + 1 --> eggyel korábbi hét adataira van szükség
						.append(" héttel ezelőtti. ");

				System.out.println(sb);
			}
		}
		System.out.println("---------------------------------------");
		System.out.println();
		System.out.println();
	}
	
}