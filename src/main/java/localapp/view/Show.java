package localapp.view;

import localapp.config.LottoType;
import localapp.model.WeeksAgoWasItPulled;
import java.util.List;


public class Show {

    private final LottoType type;

    public Show(LottoType type) {
        this.type = type;
    }


    public void oldestNumbers(List<WeeksAgoWasItPulled> oldestMainNumbers, List<WeeksAgoWasItPulled> oldestSideNumbers) {
        int oldestNumber = oldestMainNumbers.get(0).getNumber();
        System.out.println("-- A(z) " + type.getName() + " STATISZTIKA --");
        System.out.println("A legrégebben kihúzott szám: " + oldestNumber);
        if (type == LottoType.EURO_JACKPOT) {
            int oldestSideNumber = oldestSideNumbers.get(0).getNumber();
            System.out.println("Legrégebbi segéd száma: " + oldestSideNumber);
        }
        System.out.println("--------- FŐ SZÁMOK ---------");
        for (int i = 0; i < oldestMainNumbers.size(); i++) {
            int currentNumber = oldestMainNumbers.get(i).getNumber();
            int weekAgo = oldestMainNumbers.get(i).getWeeksAgo();
            String dump = "";
            if (currentNumber < 10) {
                dump = " ";
            }
            System.out.println(dump + currentNumber + ": " + weekAgo + " húzással ezelőtt volt.");
        }
        if (type == LottoType.EURO_JACKPOT) {
            System.out.println("------- SEGÉD SZÁMOK --------");
            for (int i = 0; i < oldestSideNumbers.size(); i++) {
                int currentSide = oldestSideNumbers.get(i).getNumber();
                int weekAgo = oldestSideNumbers.get(i).getWeeksAgo();
                String dump = "";
                if (currentSide < 10) {
                    dump = " ";
                }
                System.out.println(dump + currentSide + ": " + weekAgo + " húzással ezelőtt volt.");
            }
        }
    }

}