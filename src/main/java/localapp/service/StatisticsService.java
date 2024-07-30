package localapp.service;

import localapp.model.AgeHit;
import localapp.model.HitCase;
import localapp.model.LotteryDraw;
import localapp.model.PreviousWeekOfNumber;
import java.util.*;


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
	
	
	
	
	public List<PreviousWeekOfNumber> getOldestNumberWithBoundaries(List<int[]> data, int bottomBoundary, int upBoundary) {
		List<PreviousWeekOfNumber> lotteryNumbers = getPreviousWeekOfNumbers(data);
		
		// Sort the list by the weeksAgo field, in descending order
		lotteryNumbers.sort(Comparator.comparingInt(PreviousWeekOfNumber::getWeeksAgo).reversed());
		
		List<PreviousWeekOfNumber> result = new ArrayList<>();
		// take per limit
		for (int id = bottomBoundary; id < upBoundary; id++) {
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
	
	public List<AgeHit> getValidHitsWithWeekAgeIndexRange(List<int[]> lotteryNumbers, int lastWeekLimit, int range) {
		// a statisztikai adatok gyűjtője
		List<AgeHit> hitList = new ArrayList<>();
		
		// limitáljuk a keresést vagy sem
		if (lastWeekLimit < 1) {
			lastWeekLimit = lotteryNumbers.size() - 1;
		}
		
		// meghatározzuk a korábbi húzásoknál milyen régiek voltak az kihúzott számok
		List<LotteryDraw> data = this.getLotteryDrawPreviousWeeksHistory(lotteryNumbers);
		
		// végig nézzük különböző sávokal, amit az alsó és felső határindex jelöl ki (régiség rorrendben kiválasztott sáv)
		for (int round = 0; round < (89 - range); round++) {
			int bottomIndex = round;
			int upperIndex = bottomIndex + range;
			
			// ebben tároljuk el, hogy adott sávhatárokkal végignézve a kihúzott számokat, húzásonként mennyi találat volt a megfelelő régiségi korral
			AgeHit currentAgeHit = new AgeHit();
			currentAgeHit.setBottomBoundaryIndex(bottomIndex);
			currentAgeHit.setUpperBoundaryIndex(upperIndex);
			
			// végig megyünk minden egyes hét kihúzott számait, meg vizsgálva ekkor ezek milyen régiek voltak
			for (int index = 0; index < lastWeekLimit; index++) {
				
				// kell az adott héten a számok régiségi adatai, ezt csökkenő sorrendbe rendezünk a határértékek vizsgálathoz
				List<PreviousWeekOfNumber> weekHistory = new ArrayList<>(data.get(index).getWeeksHistory());
				weekHistory.sort(Comparator.comparingInt(PreviousWeekOfNumber::getWeeksAgo).reversed());
				
				// vesszük az adott hét korhatárait az indexhatárok segítségével
				int weekAgoBottomBoundaryValue = weekHistory.get(bottomIndex).getWeeksAgo();
				int weekAgoUpperBoundaryValue = weekHistory.get(upperIndex).getWeeksAgo();
				
				int hit = 0;
				// vesszük az adott hét lottószámait és megnézzük benne voltak-e az indexsávok közti korok között
				for (Integer lotteryNumber : data.get(index).getCurrentWeekNumbers()) {
					// az adott szám mennyire volt régi - figyelni kell arra, hogy a szám és a szám által meghatározott számindex az eggyel kisebb
					int weekAgoOflotteryNumber = data.get(index + 1).getWeeksHistory().get(lotteryNumber - 1).getWeeksAgo();
					if (weekAgoOflotteryNumber >= weekAgoUpperBoundaryValue
						&& weekAgoOflotteryNumber <= weekAgoBottomBoundaryValue) {
						// ha benne volt a két korhatár között akkor találat vamn, jó a szám
						hit++;
					}
					
				}
				
				// elmentjük hány - számunkra megfelelően - régi szám volt: 0-összes
				currentAgeHit.increaseHitByIndex(hit);
			}
			
			hitList.add(currentAgeHit);
		}
		
		return hitList;
	}
	
	public List<HitCase> getMaximumCaseByHitIndex(List<AgeHit> hits) {
		List<HitCase> result = new ArrayList<>();
		
		int maxStep = hits.get(0).getHits().length;
		AgeHit selectedAgeHit = null;
		for (int i = 0; i < maxStep; i++) {
			int max = -1;
			for (AgeHit ageHit : hits) {
				if (ageHit.getHits()[i] > max) {
					max = ageHit.getHits()[i];
					selectedAgeHit = ageHit;
				}
			}
			HitCase hitCase = new HitCase();
			hitCase.setHit(i);
			hitCase.setVolume(selectedAgeHit.getHits()[i]);
			hitCase.setBottomIndex(selectedAgeHit.getBottomBoundaryIndex());
			hitCase.setUpperIndex(selectedAgeHit.getUpperBoundaryIndex());
			result.add(hitCase);
		}
		
		return result;
	}
}