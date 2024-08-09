package localapp.model;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class WeeksAgoWasItPulled {

	// lottery number
	private int number;
	
	// how many weeks ago was the lottery drawn?
	private int latestWeek = Integer.MAX_VALUE;


	// constructor
	public WeeksAgoWasItPulled(int number) {
		this.number = number;
	}

}