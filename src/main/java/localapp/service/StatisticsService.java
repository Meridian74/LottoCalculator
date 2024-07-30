package localapp.service;

import localapp.model.LotteryDraw;
import localapp.model.PreviousWeekOfNumber;
import java.util.*;
import java.util.stream.Collectors;


public class StatisticsService {
	
	private static List<PreviousWeekOfNumber> getPreviousWeekOfNumbers(List<int[]> data) {
		List<PreviousWeekOfNumber> lotteryNumbers = new ArrayList<>();
		
		for (byte id = 1; id <= 90; id++) {
			PreviousWeekOfNumber num = new PreviousWeekOfNumber(id);
			lotteryNumbers.add(num);
		}
		int currentWeek = 0;
		for (int[] currentData : data) {
			for (int index = 0; index < 5; index++) {
				int number = currentData[index];
				int numberAsId = number - 1;
				PreviousWeekOfNumber storedNumber = lotteryNumbers.get(numberAsId);
				if (currentWeek < storedNumber.getWeeksAgo()) {
					storedNumber.setWeeksAgo(currentWeek);
				}
			}
			currentWeek++;
		}
		return lotteryNumbers;
	}
	
	
	
	
	public List<PreviousWeekOfNumber> getOldestNumbers(List<int[]> data, int limit) {
		List<PreviousWeekOfNumber> lotteryNumbers = getPreviousWeekOfNumbers(data);
		
		// Sort the list by the weeksAgo field, in descending order
		lotteryNumbers.sort(Comparator.comparingInt(PreviousWeekOfNumber::getWeeksAgo).reversed());
		
		List<PreviousWeekOfNumber> result = new ArrayList<>();
		// take per limit
		for (byte id = 0; id < limit; id++) {
			PreviousWeekOfNumber num = lotteryNumbers.get(id);
			result.add(num);
		}
		
		return result;
	}
	
	public List<LotteryDraw> getLotteryDrawPreviousWeeksHistory(List<int[]> data) {
		ArrayList<LotteryDraw> result = new ArrayList<>();
		int lastIndex = data.size();
		// take every lottery draw in order
		for (int startIndex = 0; startIndex < lastIndex; startIndex++) {
			LotteryDraw lotteryDraw = new LotteryDraw();
			
			// save numbers of current week
			lotteryDraw.setCurrentWeekNumbers(
					Arrays.stream(data.get(startIndex))
					.boxed()
					.toList()
			);
			
			// calculate weekHistories of all number (1-90) and save
			List<PreviousWeekOfNumber> currentWeekHistories = getPreviousWeekOfNumbers(data.subList(startIndex, lastIndex));
			lotteryDraw.setWeeksHistory(currentWeekHistories);
			result.add(lotteryDraw);
		}
		
		return result;
	}
	
}