package localapp.model;

import java.util.*;

public class LotteryDraw {
	
	// az adott héten kisorsolt számok
	List<Integer> currentWeekNumbers = new ArrayList<>();
	
	// az adott héten az összes számról adatlista, hogy hány héttel korábban voltak kisorsolva
	List<PreviousWeekOfNumber> weeksHistory = new ArrayList<>();
	
	
	
	
	public void setCurrentWeekNumbers(List<Integer> numbers) {
		this.currentWeekNumbers = numbers;
	}
	
	public void setWeeksHistory(List<PreviousWeekOfNumber> weeksHistory){
		this.weeksHistory = weeksHistory;
	}

	public List<Integer> getCurrentWeekNumbers() {
		return this.currentWeekNumbers;
	}
	
	public List<PreviousWeekOfNumber> getWeeksHistory() {
		return this.weeksHistory;
	}

}