package localapp.model;


import java.util.ArrayList;
import java.util.List;

public class LotteryDrawn {
	
	// az adott héten kisorsolt számok
	List<Integer> currentWeekNumbers = new ArrayList<>();
	
	// az adott héten az összes számról adatlista, hogy hány héttel korábban voltak kisorsolva
	List<WeeksAgoWasItPulled> weeksHistory = new ArrayList<>();
	
	
	
	
	public void setCurrentWeekNumbers(List<Integer> numbers) {
		this.currentWeekNumbers = numbers;
	}
	
	public void setWeeksHistory(List<WeeksAgoWasItPulled> weeksHistory){
		this.weeksHistory = weeksHistory;
	}

	public List<Integer> getCurrentWeekNumbers() {
		return this.currentWeekNumbers;
	}
	
	public List<WeeksAgoWasItPulled> getWeeksHistory() {
		return this.weeksHistory;
	}

}