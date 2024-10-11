package localapp.service;

import localapp.config.LottoType;
import localapp.model.LotteryDrawn;
import localapp.model.WeeksAgoWasItPulled;
import java.util.*;


public class StatisticsService {

    protected final LottoType type;

    // konstruktor - beállítja melyik típusú játékról van szó.
    public StatisticsService(LottoType type) {
        this.type = type;
    }


    /**
     * Visszamenőlegesen elkészül egy heti számstatisztika, arról hogy melyik számot milyen régen (hány húzással ezelőtt)
     * húzták ki.
     * @param data a hetente kihúzott számok listája. Az összes hét adatai együtt.
     * @return lista, benne minden egyes szám és az, hogy ezt a számot hány húzással ezelőtt húzták ki.
     */
    protected List<WeeksAgoWasItPulled> getPreviousWeekOfNumbers(List<int[]> data) {
        List<WeeksAgoWasItPulled> result = new ArrayList<>();

        // beállítjuk az eredménylistába a játék összes számát sorrendben
        for (int id = 1; id <= this.type.getAllNumber(); id++) {
            WeeksAgoWasItPulled num = new WeeksAgoWasItPulled(id);
            result.add(num);
        }

        // az éppen aktuális legutolsó hetet kihagytuk a 'List<int[]> data'-val kapott listában!
        int currentWeek = 1;
        int already = 0;

        // végigmegyünk a hetente kihúzott számokon
        for (int[] drawnedNumbers : data) {

            // szerepelt a kihúzott számok közt a kihúzott szám, akkor beállítjuk ez milyen régen volt
            for (int index = 0; index < this.type.getType(); index++) {
                int number = drawnedNumbers[index];
                int numberAsId = number - 1;
                WeeksAgoWasItPulled storedNumber = result.get(numberAsId);
                if (currentWeek < storedNumber.getLatestWeek()) {
                    storedNumber.setLatestWeek(currentWeek);
                }
            }

            // az eredménylistában minden szám egyszer már beállításra került?
            if (already == type.getAllNumber()) {
                break;
            }

            currentWeek++;
        } // end for

        return result;
    }


    /**
     *
     * Az isMain paraméter a EuroJackpot miatt kell, itt két külön vizsgálat van.. a főszámokra, és a mellékszámokra.
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

        // az összes szám közül mennyit vegyünk - ha nem megfelelő az élték (nincs tartományon belül) akkor az összeset
        if (maxItem < 1 || maxItem > type.getAllNumber()) {
            maxItem = type.getAllNumber();
        }

        // beállítjuk a szűrési határindexeket, a 0ik indexen a legrégebbi szám lesz a sorrendbe rendezés után
        int theOldestIndex = 0;
        int theMostRecentIndex = theOldestIndex + maxItem;
        List<WeeksAgoWasItPulled> weekHistory = new ArrayList<>(lotteryHistory.get(0).getWeeksHistory());

        // ...EuroJackpot-nál ezt másképpen kell elvégezni, a plusz két szám miatt - lásd override-olt metódusban!
        weekHistory.sort(Comparator.comparingInt(WeeksAgoWasItPulled::getLatestWeek).reversed());

        // visszatérünk a megfelelő mennyiséggel a lista elejéről, azaz a legrégebbi számokkal
        return new ArrayList<>(weekHistory.subList(theOldestIndex, theMostRecentIndex));
    }

    public List<LotteryDrawn> getLotteryDrawPreviousWeeksHistory(List<int[]> data) {
        ArrayList<LotteryDrawn> result = new ArrayList<>();
        int lastIndex = data.size();
        // Vesszük a húzások számait a húzások szerinti sorrendben
        for (int startIndex = 0; startIndex < lastIndex; startIndex++) {
            LotteryDrawn lotteryDrawn = new LotteryDrawn();

            // elmentjük a számokat
            lotteryDrawn.setCurrentWeekNumbers(
                    Arrays.stream(data.get(startIndex))
                            .boxed()
                            .toList()
            );

            // megvizsgáljuk minden egyes héten állva, hogy onnan visszamenőleg milyen régen voltak az egyes számok kihúzva
            List<WeeksAgoWasItPulled> currentWeekHistories = getPreviousWeekOfNumbers(data.subList(startIndex, lastIndex));
            lotteryDrawn.setWeeksHistory(currentWeekHistories);
            result.add(lotteryDrawn);
        }

        return result;
    }

    public List<WeeksAgoWasItPulled> oldestPairOfMainNumber(List<int[]> data, int number) {
        List<WeeksAgoWasItPulled> result = new ArrayList<>();

        // amennyi számunk van annyiféle adat lesz majd, mindegyikhez külön-külön eltárolva.
        int numberOfNums = this.type.getAllNumber();
        for (int id = 1; id <= numberOfNums; id++) {
            WeeksAgoWasItPulled num = new WeeksAgoWasItPulled(id);
            result.add(num);
        }

        int already = 0;
        int currentWeek = 0;
        // végig megyünk a heti sorosolások kihúzott számait vizsgálva
        for (int[] currentWeekData : data) {
            // első menetben megnézzük, a kérdéses 'number' benne van-e a húzott számok között?
            boolean isContained = false;
            for (int index = 0; index < this.type.getType(); index++) {
                if (currentWeekData[index] == number) {
                    isContained = true;
                    break;
                }
            }
            // ha nincs, akkor vesszük e következő hét sorosála alkalmával kihúzott számokat
            if (!isContained) {
                currentWeek++;
                continue;
            }
            // szerepelt a kihúzott számok közt a számunk, akkor beállítjuk ez milyen régen volt
            for (int index = 0; index < this.type.getType(); index++) {
                // a saját számunkat kihagyjuk
                if (currentWeekData[index] == number) {
                    continue;
                }

                // beírjuk az éppen adott hét idejét, de csak akkor ha még nem volt (ha a tárolt értéknél kisebb az idő
                // ez értelemszerűen csak egyszer fog előfordulni mert a currentweek növekszik mindig)
                int drawnedNumber = currentWeekData[index];
                int transformedToIndex = drawnedNumber - 1;
                WeeksAgoWasItPulled escortNumber = result.get(transformedToIndex);
                if (currentWeek < escortNumber.getLatestWeek()) {
                    escortNumber.setLatestWeek(currentWeek);
                    already++;
                }
            }

            // have all the numbers been already found?
            if (already == type.getAllNumber()) {
                break;
            }

            // step to next week
            currentWeek++;
        }

        // a saját számot (number) kivesszük a listából, majd időrendi sorrendbe rendezve vissszatérünk az eredménylistával
        result.remove(number - 1);
        result.sort(Comparator.comparingInt(WeeksAgoWasItPulled::getLatestWeek).reversed());
        return result;
    }

    public List<WeeksAgoWasItPulled> oldestPairOfSideNumber(List<int[]> data, int number) {
        return Collections.emptyList();
    }
}