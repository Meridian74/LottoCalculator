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

        // EuroJackpot fő számai, azaz 1-50-ig
        for (int id = 1; id <= super.type.getAllNumber(); id++) {
            WeeksAgoWasItPulled num = new WeeksAgoWasItPulled(id);
            lotteryNumbers.add(num);
        }

        // EuroJackpot segédszámai, amik 1-12-ig (korábban 1-10 voltak - de ez nem zavar be, mert már elég régen voltak)
        for (int id = 1; id <= 12; id++) {
            WeeksAgoWasItPulled num = new WeeksAgoWasItPulled(id);
            lotteryNumbers.add(num);
        }

        int currentWeek = 0;
        int setCounter = 0;
        for (int[] currentData : data) {
            // A 1-50-ig terjedő fő számokat, aza az első 5 kihúzott számot nézzük külön csak az összesen 7 számból
            for (int index = 0; index < 5; index++) {
                int number = currentData[index];
                int numberAsId = number - 1;
                WeeksAgoWasItPulled storedNumber = lotteryNumbers.get(numberAsId);
                if (currentWeek < storedNumber.getLatestWeek()) {
                    storedNumber.setLatestWeek(currentWeek);
                    setCounter++;
                }
            }
            // Itt a 1-12-ig terjedő mellékszámokat, azaz az utolsó 2 számot nézzük a 7 kihúzott számból
            for (int index = 5; index < 7; index++) {
                int number = currentData[index];
                // az eredménylistában ezek a 50-61-es indexen vannak mentve - ezért a '+ 50'!
                int numberAsId = number - 1 + 50;
                WeeksAgoWasItPulled storedNumber = lotteryNumbers.get(numberAsId);
                if (currentWeek < storedNumber.getLatestWeek()) {
                    storedNumber.setLatestWeek(currentWeek);
                    setCounter++;
                }
            }

            // minden számot egyszer beállítottunk már? Ha igen akkor nem kell tovább nézni a heti kihúzott számokat
            if (setCounter == type.getAllNumber() + 12) {
                break;
            }

            currentWeek++;
        } // end of for

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

        // az összes szám közül mennyit vegyünk, ha tartományon kívüli az érték akkor az összeset
        if (maxItem < 1 || maxItem > super.type.getAllNumber()) {
            maxItem = super.type.getAllNumber();
        }

        // beállítjuk a szűrési határindexeket, a 0ik indexen a legrégebbi szám lesz a sorrendbe rendezés után
        int theOldestIndex = 0;
        int theMostRecentIndex = theOldestIndex + maxItem;
        List<WeeksAgoWasItPulled> weekHistory = new ArrayList<>(
                lotteryHistory.get(0)
                .getWeeksHistory()
                .subList(firstIndex, lastIndex)
        );

        // eurojackpotnál ezt másképpen kell majd! a plusz két szám miatt
        weekHistory.sort(Comparator.comparingInt(WeeksAgoWasItPulled::getLatestWeek).reversed());

        // visszatérünk a megfelelő mennyiséggel a lista elejéről, azaz a legrégebbi számokkal
        return new ArrayList<>(weekHistory.subList(theOldestIndex, theMostRecentIndex));
    }

    @Override
    public List<WeeksAgoWasItPulled> oldestPairOfSideNumber(List<int[]> data, int number) {
        List<WeeksAgoWasItPulled> result = new ArrayList<>();

        for (int id = 1; id <= 12; id++) {
            WeeksAgoWasItPulled num = new WeeksAgoWasItPulled(id);
            result.add(num);
        }

        int setCounter = 0;
        int currentWeek = 0;
        // végig megyünk a heti sorosolások kihúzott számait vizsgálva
        for (int[] currentData : data) {
            // első menetben megnézzük, a kérdéses 'number' benne van-e a húzott számok között?
            boolean isContained = false;
            for (int index = 5; index < 7; index++) {
                if (currentData[index] == number) {
                    isContained = true;
                    break;
                }
            }
            // ha nincs, akkor vesszük e következő hét sorosála alkalmával kihúzott számokat
            if (!isContained) {
                continue;
            }
            // szerepelt a kihúzott számok közt a számunk, akkor beállítjuk ez milyen régen volt
            for (int index = 5; index < 7; index++) {
                // a saját számunkat kihagyjuk
                if (currentData[index] == number) {
                    continue;
                }

                // beírjuk az éppen adott hét idejét, de csak akkor ha még nem volt (ha a tárolt értéknél kisebb az idő
                // ez értelemszerűen csak egyszer fog előfordulni mert a currentweek növekszik mindig)
                int checkedNumber = currentData[index];
                int checkedNumberAsId = checkedNumber - 1;
                WeeksAgoWasItPulled storedNumber = result.get(checkedNumberAsId);
                if (currentWeek < storedNumber.getLatestWeek()) {
                    storedNumber.setLatestWeek(currentWeek);
                }
            }


            // have all the numbers been already found?
            if (setCounter == 12) {
                break;
            }

            // step to next week
            currentWeek++;
        }

        // mielőtt visszatérnénk időrendi sorrendbe állítjuk a számokat a listában
        result.sort(Comparator.comparingInt(WeeksAgoWasItPulled::getLatestWeek).reversed());
        // a vizsgált saját számot (number) levesszük a lista elejéről
        result.remove(0);
        return result;
    }

}