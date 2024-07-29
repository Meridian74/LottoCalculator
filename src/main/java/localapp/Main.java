package localapp;

import localapp.model.NumbersByAged;
import localapp.service.HtmlReaderService;
import localapp.service.StatisticsService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class Main {
	public static void main(String[] args) {
		HtmlReaderService reader = new HtmlReaderService();
		StatisticsService statistics = new StatisticsService();

		try {
			List<byte[]> lottery5numbers =  reader.getAllNumbersOfLottery5();
			// take only 20 percentage of the numbers (--> 18 oldest numbers of the 90)
			List<NumbersByAged> oldestNumbers = statistics.getOldestNumbers(lottery5numbers, 18);

			int[] nums = new int[5];
			// mix the sequence of numbers...
			Collections.shuffle(oldestNumbers);
			// then take the first five numbers you can play in the Lottery :)
			for(int i = 0; i < nums.length; i++) {
				nums[i] = oldestNumbers.get(i).getNumber();
				System.out.println( (i + 1) + ". szám: " + nums[i] + ", " +  oldestNumbers.get(i).getWeeksAgo() + " hete húzták ki utoljára.");
			}

			System.out.println("Megjátszandó számok erre a hétre: " + Arrays.toString(nums));

		} catch (Exception exp) {
			String error = String.format("Hiba történt! %s", exp.getMessage());
			System.out.println(error);
		}
		
	}
	
}