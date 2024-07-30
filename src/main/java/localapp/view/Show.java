package localapp.view;

import localapp.model.HitCase;
import localapp.model.LotteryDraw;
import localapp.model.PreviousWeekOfNumber;
import localapp.service.StatisticsService;

import java.util.*;

public class Show {
	
	private final StatisticsService statisticsService;
	
	public Show(StatisticsService statisticsService) {
		this.statisticsService = statisticsService;
	}
	
	
	public void showLotteryNumbersCanChoose(List<PreviousWeekOfNumber> numbers) {
		System.out.println("Ajánlottan választható számok: ");
		
		List<PreviousWeekOfNumber> orderedList = new ArrayList<>(numbers);
		orderedList.sort(Comparator.comparingInt(PreviousWeekOfNumber::getNumber));
		List<Integer> playableNums = orderedList.stream().map(PreviousWeekOfNumber::getNumber).toList();
		System.out.println(playableNums);
		
		for (PreviousWeekOfNumber element : orderedList) {
			System.out.println("szám: " + element.getNumber() + " --> " + element.getWeeksAgo() + " héttel ezelőtt húzták ki.");
		}
	}
	
	public void select5number(List<PreviousWeekOfNumber> oldestNumbers) {
		int[] nums = new int[5];
		// mix the sequence of numbers...
		Collections.shuffle(oldestNumbers);
		// then take the first five numbers you can play in the Lottery :)
		for (int i = 0; i < nums.length; i++) {
			nums[i] = oldestNumbers.get(i).getNumber();
		}

		System.out.println("\nMegjátszandó számok erre a hétre: " + Arrays.toString(nums));
		for (int i = 0; i < nums.length; i++) {
			System.out.println((i + 1) + ". szám: " + nums[i] + ", " + oldestNumbers.get(i).getWeeksAgo() + " hete húzták ki utoljára.");
		}
		
	}
	
	public void showNumberStatAboutWeeksAgo(List<int[]> lotteryNumbers, int lastWeekLimit, int bottomIndex) {
		// init
		if (lastWeekLimit >= lotteryNumbers.size() - 1) {
			lastWeekLimit = lotteryNumbers.size() - 1;
		}
		// set bottom and upper boundary index of weekAgo sequence
		if (bottomIndex > 89) {
			bottomIndex = 89;
		}
		if (bottomIndex < 0) {
			bottomIndex = 0;
		}
		
		final int PARTITION = 18; // 20% of 90
		int upperIndex = bottomIndex - PARTITION;
		if (upperIndex < 0) {
			upperIndex = 0;
		}
		
		// meghatározzuk a korábbi húzásoknál milyen régiek voltak az kihúzott számok
		List<LotteryDraw> data = this.statisticsService.getLotteryDrawPreviousWeeksHistory(lotteryNumbers);
		
		
		// végig megyünk minden egyes hét kihúzott számait, meg vizsgálva ekkor ezek milyen régiek voltak
		for (int index = 0; index < lastWeekLimit; index++) {
			System.out.println("\n\n***************************************");
			System.out.println(index + ". héttel ezelőtti kihúzott számok:");
			System.out.println("***************************************");
			
			// kell az adott héten a számok régiségi adatai, ezt csökkenő sorrendbe rendezünk a határértékek vizsgálathoz
			List<PreviousWeekOfNumber> weekHistory = new ArrayList<>(data.get(index).getWeeksHistory());
			weekHistory.sort(Comparator.comparingInt(PreviousWeekOfNumber::getWeeksAgo).reversed());
			
			
			for (Integer lotteryNumber : data.get(index).getCurrentWeekNumbers()) {
				StringBuilder sb = new StringBuilder();
				
				// set age of week boundary values
				int weekAgoBottomBoundaryValue = weekHistory.get(bottomIndex).getWeeksAgo();
				int weekAgoUpperBoundaryValue = weekHistory.get(upperIndex).getWeeksAgo();
				
				// FYI: 'index + 1': you need the status of one week earlier (when it has not yet been drawn).
				// FYI: 'lotteryNumber - 1': lottery numbers are from 1 to 90 and their indices are from 0 to 89.
				int weekAgoOflotteryNumber = data.get(index + 1).getWeeksHistory().get(lotteryNumber - 1).getWeeksAgo();
				if (weekAgoOflotteryNumber <= weekAgoUpperBoundaryValue
					&& weekAgoOflotteryNumber >= weekAgoBottomBoundaryValue) {
					
					sb.append("++ ");
				} else {
					sb.append("   ");
				}
				if (lotteryNumber < 10) {
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
	
	public void showHitCasesMaximum(List<HitCase> hitCases) {
		for (HitCase hitCase: hitCases) {
			System.out.println("maximum " + hitCase.getHit() + " találat");
			System.out.println("----------------------------");
			System.out.println(" maximum esetszám: " + hitCase.getVolume());
			System.out.println(" alsó index érték: " + hitCase.getBottomIndex());
			System.out.println("felső index érték: " + hitCase.getUpperIndex());
			System.out.println("---------------------------");
			System.out.println();
		}
	}
}