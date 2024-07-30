package localapp.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgeHit {
	
	private int[] hits = new int[6]; // ez 5-ös lotónak megfelelő. 0 és az 5 szám. Ha Kenó, akkor 21 lesz a jó szám.
	private int bottomBoundaryIndex;
	private int upperBoundaryIndex;
	
	public void increaseHitByIndex(int index) {
		hits[index]++;
	}
	
}