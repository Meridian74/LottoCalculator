package localapp.service;

import localapp.model.NumbersByAged;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class StatisticsService {
	
	public List<NumbersByAged> getOldestNumbers(List<byte[]> data, int limit) {
		List<NumbersByAged> lotteryNumbers = new ArrayList<>();
		
		for (byte id = 1; id <= 90; id++) {
			NumbersByAged num = new NumbersByAged(id);
			lotteryNumbers.add(num);
		}
		int currentWeek = 0;
		for (byte[] currentData : data) {
			for (int index = 0; index < 5; index++) {
				byte number = currentData[index];
				int numberAsId = number - 1;
				NumbersByAged storedNumber = lotteryNumbers.get(numberAsId);
				if (currentWeek < storedNumber.getWeeksAgo()) {
					storedNumber.setWeeksAgo(currentWeek);
				}
			}
			currentWeek++;
		}
		
		// A lista rendezése a weeksAgo mező szerint, csökkenő sorrendben
		lotteryNumbers.sort(Comparator.comparingInt(NumbersByAged::getWeeksAgo).reversed());
		
		List<NumbersByAged> result = new ArrayList<>();
		for (byte id = 0; id < limit; id++) {
			NumbersByAged num = lotteryNumbers.get(id);
			result.add(num);
		}
		
		return result;
	}
	
}