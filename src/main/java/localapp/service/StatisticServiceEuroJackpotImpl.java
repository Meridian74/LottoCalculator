package localapp.service;

import localapp.config.LottoType;
import localapp.model.LotteryDrawn;
import localapp.model.WeeksAgoWasItPulled;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StatisticServiceEuroJackpotImpl extends StatisticsService {

    public StatisticServiceEuroJackpotImpl(LottoType type) {
        super(type);
    }


    @Override
    public List<WeeksAgoWasItPulled> getPreviousWeekOfNumbers(List<int[]> data) {
        List<WeeksAgoWasItPulled> lotteryNumbers = new ArrayList<>();

        // EuroJacpot main numbers 1-50
        for (int id = 1; id <= super.type.getAllNumber(); id++) {
            WeeksAgoWasItPulled num = new WeeksAgoWasItPulled(id);
            lotteryNumbers.add(num);
        }

        // EuroJacpot side numbers 1-12
        for (int id = 1; id <= 12; id++) {
            WeeksAgoWasItPulled num = new WeeksAgoWasItPulled(id);
            lotteryNumbers.add(num);
        }

        int currentWeek = 0;
        int setCounter = 0;
        for (int[] currentData : data) {
            // check main numbers, thus first 5 nums (1-50)
            for (int index = 0; index < 5; index++) {
                int number = currentData[index];
                int numberAsId = number - 1;
                WeeksAgoWasItPulled storedNumber = lotteryNumbers.get(numberAsId);
                if (currentWeek < storedNumber.getWeeksAgo()) {
                    storedNumber.setWeeksAgo(currentWeek);
                    setCounter++;
                }
            }
            // check side numbers, thus last 2 nums (1-10)
            for (int index = 5; index < 7; index++) {
                int number = currentData[index];
                int numberAsId = number - 1 + 50; // side numbers indexes, 50-59!
                WeeksAgoWasItPulled storedNumber = lotteryNumbers.get(numberAsId);
                if (currentWeek < storedNumber.getWeeksAgo()) {
                    storedNumber.setWeeksAgo(currentWeek);
                    setCounter++;
                }
            }

            // have all the numbers been already found? (50 main and 12 side number)
            if (setCounter == type.getAllNumber() + 12) {
                break;
            }

            // step to next week
            currentWeek++;
        }

        return lotteryNumbers;
    }

    @Override
    public List<WeeksAgoWasItPulled> getOldestNumbers(List<LotteryDrawn> lotteryHistory, int maxItem, boolean isMain) {

        int firstIndex = 0;
        int lastIndex = 50;
        if (!isMain) {
            firstIndex = 50;
            lastIndex = 62;
        }

        // az összes szám közül mennyit vegyünk
        if (maxItem < 1) maxItem = 1;
        if (maxItem > super.type.getAllNumber()) maxItem = super.type.getAllNumber();

        // beállítjuk a szűrési határindexeket, a 0ik indexen a legrégebbi szám lesz a sorrendbe rendezés után
        int theOldestIndex = 0;
        int theMostRecentIndex = theOldestIndex + maxItem;
        List<WeeksAgoWasItPulled> weekHistory = new ArrayList<>(
                lotteryHistory.get(0)
                .getWeeksHistory()
                .subList(firstIndex, lastIndex)
        );

        // eurojackpotnál ezt másképpen kell majd! a plusz két szám miatt
        weekHistory.sort(Comparator.comparingInt(WeeksAgoWasItPulled::getWeeksAgo).reversed());

        // visszatérünk a megfelelő mennyiséggel a lista elejéről, azaz a legrégebbi számokkal
        return new ArrayList<>(weekHistory.subList(theOldestIndex, theMostRecentIndex));
    }

}