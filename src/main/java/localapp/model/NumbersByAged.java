package localapp.model;

public class NumbersByAged {
	// kihúzott szám
	int number;
	
	// hány héttel ezelőtt húzták ki?
	int weeksAgo = Integer.MAX_VALUE;
	
	public NumbersByAged(int number) {
		this.number = number;
	}
	
	public int getNumber() {
		return number;
	}
	
	public void setNumber(int number) {
		this.number = number;
	}
	
	public int getWeeksAgo() {
		return weeksAgo;
	}
	
	public void setWeeksAgo(int weeksAgo) {
		this.weeksAgo = weeksAgo;
	}
	
}