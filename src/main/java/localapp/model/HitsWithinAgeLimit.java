package localapp.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HitsWithinAgeLimit {
	int hitIndex;
	int quantity;
	int bottomIndex;
	int upperIndex;
}