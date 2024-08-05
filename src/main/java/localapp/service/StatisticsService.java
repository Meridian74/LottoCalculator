package localapp.service;

import localapp.config.LottoType;
import localapp.model.LotteryDrawn;
import localapp.model.WeeksAgoWasItPulled;
import java.util.*;


public class StatisticsService {

    protected final LottoType type;

    public StatisticsService(LottoType type) {
        this.type = type;
    }

    protected List<WeeksAgoWasItPulled> getPreviousWeekOfNumbers(List<int[]> data) {
        List<WeeksAgoWasItPulled> lotteryNumbers = new ArrayList<>();

        for (int id = 1; id <= this.type.getAllNumber(); id++) {
            WeeksAgoWasItPulled num = new WeeksAgoWasItPulled(id);
            lotteryNumbers.add(num);
        }
        int currentWeek = 0;
        int setCounter = 0;
        for (int[] currentData : data) {
            for (int index = 0; index < this.type.getType(); index++) {
                int number = currentData[index];
                int numberAsId = number - 1;
                WeeksAgoWasItPulled storedNumber = lotteryNumbers.get(numberAsId);
                if (currentWeek < storedNumber.getWeeksAgo()) {
                    storedNumber.setWeeksAgo(currentWeek);
                }
            }

            // have all the numbers been already found?
            if (setCounter == type.getAllNumber() + 10) {
                break;
            }

            // step to next week
            currentWeek++;
        }
        return lotteryNumbers;
    }


    /**
     * Az isMain paraméter a EuroJackpot miatt kell, itt két külön viszgálat van.. a főszámokra, és a mellékszámokra.
     * Más esetben üres listával térünk vissza ha false ez az érték, és rendes listával ha true.
     *
     * @param lotteryHistory
     * @param maxItem
     * @param isMain
     * @return
     */
    public List<WeeksAgoWasItPulled> getOldestNumbers(List<LotteryDrawn> lotteryHistory, int maxItem, boolean isMain) {

        if (!isMain) {
            return Collections.emptyList();
        }

        // az összes szám közül mennyit vegyünk
        if (maxItem < 1) maxItem = 1;
        if (maxItem > type.getAllNumber()) maxItem = type.getAllNumber();

        // beállítjuk a szűrési határindexeket, a 0ik indexen a legrégebbi szám lesz a sorrendbe rendezés után
        int theOldestIndex = 0;
        int theMostRecentIndex = theOldestIndex + maxItem - 1;
        List<WeeksAgoWasItPulled> weekHistory = new ArrayList<>(lotteryHistory.get(1).getWeeksHistory());

        // eurojackpotnál ezt másképpen kell majd! a plusz két szám miatt
        weekHistory.sort(Comparator.comparingInt(WeeksAgoWasItPulled::getWeeksAgo).reversed());

        // visszatérünk a megfelelő mennyiséggel a lista elejéről, azaz a legrégebbi számokkal
        return new ArrayList<>(weekHistory.subList(theOldestIndex, theMostRecentIndex));
    }

    public List<LotteryDrawn> getLotteryDrawPreviousWeeksHistory(List<int[]> data) {
        ArrayList<LotteryDrawn> result = new ArrayList<>();
        int lastIndex = data.size();
        // take every lottery draw in order
        for (int startIndex = 0; startIndex < lastIndex; startIndex++) {
            LotteryDrawn lotteryDrawn = new LotteryDrawn();

            // save numbers of current week
            lotteryDrawn.setCurrentWeekNumbers(
                    Arrays.stream(data.get(startIndex))
                            .boxed()
                            .toList()
            );

            // calculate weekHistories of all number (1-90) and save
            List<WeeksAgoWasItPulled> currentWeekHistories = getPreviousWeekOfNumbers(data.subList(startIndex, lastIndex));
            lotteryDrawn.setWeeksHistory(currentWeekHistories);
            result.add(lotteryDrawn);
        }

        return result;
    }

}