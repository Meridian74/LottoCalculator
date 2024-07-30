package localapp.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HitCase {
	int hit;
	int volume;
	int bottomIndex;
	int upperIndex;
}