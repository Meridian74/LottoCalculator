package localapp.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NumbersByAged {

	// lottery number
	private int number;
	
	// how many weeks ago was the lottery drawn?
	private int weeksAgo = Integer.MAX_VALUE;


	// constructor
	public NumbersByAged(int number) {
		this.number = number;
	}

}