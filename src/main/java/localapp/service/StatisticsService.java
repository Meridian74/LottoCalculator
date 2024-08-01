package localapp.service;

import localapp.model.HitsStatWithinAgeLimits;
import localapp.model.HitsWithinAgeLimit;
import localapp.model.LotteryDrawn;
import localapp.model.WeeksAgoWasItPulled;
import java.util.*;


public class StatisticsService {
	
	private static List<WeeksAgoWasItPulled> getPreviousWeekOfNumbers(List<int[]> data) {
		List<WeeksAgoWasItPulled> lotteryNumbers = new ArrayList<>();
		
		for (byte id = 1; id <= 90; id++) {
			WeeksAgoWasItPulled num = new WeeksAgoWasItPulled(id);
			lotteryNumbers.add(num);
		}
		int currentWeek = 0;
		for (int[] currentData : data) {
			for (int index = 0; index < 5; index++) {
				int number = currentData[index];
				int numberAsId = number - 1;
				WeeksAgoWasItPulled storedNumber = lotteryNumbers.get(numberAsId);
				if (currentWeek < storedNumber.getWeeksAgo()) {
					storedNumber.setWeeksAgo(currentWeek);
				}
			}
			currentWeek++;
		}
		return lotteryNumbers;
	}
	
	
	
	
	public List<WeeksAgoWasItPulled> getOldestNumberWithBoundaries(List<int[]> data, int bottomBoundary, int upBoundary) {
		List<WeeksAgoWasItPulled> lotteryNumbers = getPreviousWeekOfNumbers(data);
		
		// Sort the list by the weeksAgo field, in descending order
		lotteryNumbers.sort(Comparator.comparingInt(WeeksAgoWasItPulled::getWeeksAgo).reversed());
		
		List<WeeksAgoWasItPulled> result = new ArrayList<>();
		// take the numbers that are included in the indexes
		for (int id = bottomBoundary; id <= upBoundary; id++) {
			WeeksAgoWasItPulled num = lotteryNumbers.get(id);
			result.add(num);
		}
		
		return result;
	}
	
	public List<LotteryDrawn> getLotteryDrawPreviousWeeksHistory(List<int[]> data) {
		ArrayList<LotteryDrawn> result = new ArrayList<>();
		int lastIndex = data.size();
		// take every lottery draw in order
		for (int startIndex = 0; startIndex < lastIndex; startIndex++) {
			LotteryDrawn lotteryDrawn = new LotteryDrawn();
			
			// save numbers of current week
			lotteryDrawn.setCurrentWeekNumbers(
					Arrays.stream(data.get(startIndex))
					.boxed()
					.toList()
			);
			
			// calculate weekHistories of all number (1-90) and save
			List<WeeksAgoWasItPulled> currentWeekHistories = getPreviousWeekOfNumbers(data.subList(startIndex, lastIndex));
			lotteryDrawn.setWeeksHistory(currentWeekHistories);
			result.add(lotteryDrawn);
		}
		
		return result;
	}
	
	
	/**
	 * Kigyűjti azokat az találati információkat amelyek egy meghatározott korhatáron belül lévő számokat kihúzták. <br>
	 * Minden egyes héten megnézi a lottószámokat, hogy az adott héten mennyire rég voltak kisorsolva.
	 * Ezt a statisztikát elmenti<br>
	 * <br>
	 * Ezután vesszük az összes korlimitációkat a korhatár intervallumának szélessét is figyelembe véve.<br>
	 * A korhatár index 0 értéke az előbb sorrendbe rendezett lottószámoknál azt jelenti, hogy azt a számot húzták ki a
	 * legrégebben, az 5-ös lottó esetén a 89-es index pedig azt a számot mutatja meg amelyiket a legutóbb húzták ki az
	 * adott héten vizsgálva. A statisztika nem a teljes tartományban készül el, hanem mindig annak egy előre
	 * meghatározott szeletén. A szelet szélességét határozza a 'rangeOfAge' paraméter. <br>
	 * <br>
	 * A találati statisztika abból áll végül össze, hogy minden korhatárszélességel vett kombináción végigmenve
	 * megnézzük mikor mennyi számot húztak ki abból a lottószám-részhalmazból amit ez a korhatár intervallum
	 * meghatároz. <br>
	 * Például: ha a korhatár indexek 0 és 18 (a rangeOfAge = 18), akkor a húzási régiség szerint sorrendbe állított
	 * lottó számok közül vesszük az első 18 darabot. Ha az indexek 20 és 37 (a rangeOfAge = 18) itt a 21-ik legrébbei
	 * és a 38ik legrégebbi számok közötti számokat vesszük a részhalmaznak.<br>
	 * <br>
	 * A statisztikai vizsgálat abból áll, hogy ezen korfeltéleknek (a feltételt az kor indexhatárai adják meg)
	 * megfelelő  számrészhalmazból az adott héten hány számot húztak ki. Ezt visszamenőleg az összes hét húzására
	 * megnézzük, és a elmentjük. Amikor semmit sem húztak ki az adott héten a részhalmazból az a nullás index van
	 * számolva, ha egyet húztak csak ki, az az egyes indexen, a kettő a kettesen, és így tovább (ötös lottó esetén az
	 * összes szám szerepelt a részhalmazban, az az 5-ös indexen van). Ezeket a HitsStatWithinAgeLimits objektumban
	 * tároljuk le, és ebből lista készül a korhatár index kombinációk szerint... <br>
	 * <br>
	 * Ez utóbbi magyarázata:<br>
	 * A 'rangeOfAge' adva van, és ennek alapján vesszük a kombinációkat erre nézve. Az ötös lottón a számok 0-89 (1-90)
	 * lehetnek. A számok nem értékük szerint vannak sorrendben rendezve, hanem úgy hogy mennyire régen húzták ezeket
	 * ki. Visszafele van a rendezés azaz sorrendben elől állnak azok a számok amelyeket legrégebben húztak ki az adott
	 * héten, és leghátul amiket legutóbb, például az előző héten is kihúzták. A kombinációk pedig abból ál, hogy
	 * először vesszük mindig azt a részhalmazt az így sorrendben rendezet számokból, amelyek a '0 és a rangeOfAge'
	 * között vannak, majd ezután vesszük a '1 és a (rangeOfAge + 1)' közötti számokat, majd a '2 és a (rangeOfAge + 2)',
	 * és így tovább, egészen amíg a n és (rangeOfAge + n) esetén a rangeOfAge+n értéke már az utolsó számra mutat. <br>
	 * Végig vesszük az összes ilyen kombinációt, és minden kombinációnál az összes húzást végignézve találati
	 * statsztikát készítünk, amit (a listát) végül eredményként visszadunk.
	 *
	 * @param lotteryNumbers -- kihúzott lottószámok, heti húzási sorrendben
	 * @param lastWeekLimit -- mennyi hétre visszamőleg nézzük meg a statisztikát
	 * @param rangeOfAge -- a korhatár intervallumának szélessége
	 * @return
	 */
	public List<HitsStatWithinAgeLimits> getAllHitsStatWithinAgeLimits(List<int[]> lotteryNumbers,
																	   int lastWeekLimit,
																	   int rangeOfAge) {
		
		// minimum value of rangeOfAge is 1. In case 1, only one lottery number will be in the set
		if (rangeOfAge < 1) {
			rangeOfAge = 1;
		}
		// maximum value of rangeOfAge
		if (rangeOfAge > 90) {
			rangeOfAge = 90;
		}
		
		// collector of statistics data
		List<HitsStatWithinAgeLimits> hitList = new ArrayList<>();
		
		// limit the search to all weeks backwards or specify the number of weeks backwards
		if (lastWeekLimit < 1) {
			lastWeekLimit = lotteryNumbers.size() - 1;
		}
		
		// create history data about how old the numbers drawn were in previous draws
		List<LotteryDrawn> historyData = this.getLotteryDrawPreviousWeeksHistory(lotteryNumbers);
		
		// we look through the different bands, marked by the lower and upper border index (band selected in antiquity order)
		for (int round = 0; round < (90 - rangeOfAge + 1); round++) {
			int bottomIndex = round;
			int upperIndex = bottomIndex + rangeOfAge - 1;
			
			// this stores the number of hits per draw for the corresponding antiquity, looking at the numbers drawn with the given band limits
			HitsStatWithinAgeLimits currentHitsStatWithinAgeLimits = new HitsStatWithinAgeLimits();
			currentHitsStatWithinAgeLimits.setBottomBoundaryIndex(bottomIndex);
			currentHitsStatWithinAgeLimits.setUpperBoundaryIndex(upperIndex);
			
			// going through the numbers pulled for each week, looking at how old they were
			for (int currentWeek = 0; currentWeek < lastWeekLimit; currentWeek++) {
				
				// should be for the week in question the numbers of old data, this is sorted in descending order for the limits test
				List<WeeksAgoWasItPulled> weekHistory = new ArrayList<>(historyData.get(currentWeek + 1).getWeeksHistory());
				weekHistory.sort(Comparator.comparingInt(WeeksAgoWasItPulled::getWeeksAgo).reversed());
				
				// make partial Set of number by weekAgoIndexes
				List partialSetOfNumbers = new ArrayList();
				for (int i = bottomIndex; i <= upperIndex; i++) {
					partialSetOfNumbers.add(weekHistory.get(i).getNumber());
				}
				Collections.sort(partialSetOfNumbers);
				
				// calculate hit statistics of weekly lottery numbers
				int hit = 0;
				for (int lotteryNumber : historyData.get(currentWeek).getCurrentWeekNumbers()) {
					if (partialSetOfNumbers.contains(lotteryNumber)) {
						hit++;
					}
				}
				
				// as many hits as there were, we increase the saved value in the place with the corresponding index
				currentHitsStatWithinAgeLimits.increaseHitByIndex(hit);
			}
			
			// done the hit statistics between the given age limits - stored in
			hitList.add(currentHitsStatWithinAgeLimits);
		}
		
		return hitList;
	}
	
	public List<HitsWithinAgeLimit> getMaximumCaseByHitIndex(List<HitsStatWithinAgeLimits> hits) {
		List<HitsWithinAgeLimit> result = new ArrayList<>();
		
		int maxStep = hits.get(0).getHits().length;
		HitsStatWithinAgeLimits selectedHitsStatWithinAgeLimits = null;
		for (int i = 0; i < maxStep; i++) {
			int max = -1;
			for (HitsStatWithinAgeLimits hitsStatWithinAgeLimits : hits) {
				if (hitsStatWithinAgeLimits.getHits()[i] > max) {
					max = hitsStatWithinAgeLimits.getHits()[i];
					selectedHitsStatWithinAgeLimits = hitsStatWithinAgeLimits;
				}
			}
			HitsWithinAgeLimit hitsWithinAgeLimit = new HitsWithinAgeLimit();
			hitsWithinAgeLimit.setHitIndex(i);
			hitsWithinAgeLimit.setQuantity(selectedHitsStatWithinAgeLimits.getHits()[i]);
			hitsWithinAgeLimit.setBottomIndex(selectedHitsStatWithinAgeLimits.getBottomBoundaryIndex());
			hitsWithinAgeLimit.setUpperIndex(selectedHitsStatWithinAgeLimits.getUpperBoundaryIndex());
			result.add(hitsWithinAgeLimit);
		}
		
		return result;
	}
}