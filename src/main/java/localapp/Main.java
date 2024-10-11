package localapp;

import localapp.config.LottoType;
import localapp.config.ServiceFactory;
import localapp.model.LotteryDrawn;
import localapp.model.WeeksAgoWasItPulled;
import localapp.service.*;
import localapp.view.Show;
import java.io.*;
import java.util.List;


public class Main {
    public static void main(String[] args) {

        LottoType type = selectType();

        FileReaderService reader = ServiceFactory.createHtmlReader(type);
        StatisticsService statistics = ServiceFactory.createStatisticsService(type);
        Show show = new Show(type);

        try {
            // beolvasuk az adott játék összes kihúzott számait
            List<int[]> drawnedNumbers = reader.getAllNumbers();
            System.out.println("-- A(z) " + type.getName() + " STATISZTIKA --");
            System.out.println("Húzások mennyisége: " + drawnedNumbers.size());

            // Hétről hétre visszanézzük, hogy az egyes számokat milyen régen húzták ki, és ez alapján statisztikai listát készítünk.
            List<LotteryDrawn> historyData = statistics.getLotteryDrawPreviousWeeksHistory(drawnedNumbers);

            // mennyi számot vegyünk ki az eredménylistából: -1 all, 20% --> 0.2, 10% --> 0.1, 33% --> 0.33333
            int maxItem = (int) (type.getAllNumber() * 0.2) + 1;
//            maxItem = -1;
            List<WeeksAgoWasItPulled> oldestMainNumbers = statistics.getOldestNumbers(historyData, maxItem, true);

            // Eurojackpot-nál vannak segédszámok, itt csak a 3 legrégebbit vesszük ki a kapot eredménylistából
            maxItem = 10;
            List<WeeksAgoWasItPulled> oldestSideNumbers = statistics.getOldestNumbers(historyData, maxItem, false);
            show.oldestNumbers(oldestMainNumbers, oldestSideNumbers);

            // kikeressük a legrégebbi számmal együttesen kihúzott másik legrégebbi számot
            List<WeeksAgoWasItPulled> oldestPairsOfMain = statistics.oldestPairOfMainNumber(
                    drawnedNumbers,
                    oldestMainNumbers.get(0).getNumber()
            );

            // kikeressük a legrégebbi számmal együttesen kihúzott másik legrégebbi számmal együtt kihúzott szám :)
            List<WeeksAgoWasItPulled> secondOldestPairsOfMain = statistics.oldestPairOfMainNumber(drawnedNumbers,
                    oldestPairsOfMain.get(0).getNumber());

            // kiiratás
            System.out.println("A(z) " + oldestMainNumbers.get(0).getNumber() +
                    " számmal együtt a legrégebben kihúzott másik szám: " + oldestPairsOfMain.get(0).getNumber());
            System.out.println("A(z) " + oldestPairsOfMain.get(0).getNumber() +
                    " számmal együtt a legrégebben kihúzott harmadik szám: " +
                    secondOldestPairsOfMain.get(0).getNumber());

            if (type == LottoType.EURO_JACKPOT) {
                List<WeeksAgoWasItPulled> oldestPairsOfSide = statistics.oldestPairOfSideNumber(
                        drawnedNumbers,
                        oldestSideNumbers.get(0).getNumber());

                System.out.println("A(z) " + oldestSideNumbers.get(0).getNumber() +
                        " SEGÉDszámmal együtt kihúzott másik SEGÉDszám: " +
                        oldestPairsOfSide.get(0).getNumber());
            }

        } catch (Exception exp) {
            String error = String.format("Hiba történt! %s", exp.getMessage());
            System.out.println(error);
        }

    }

    private static LottoType selectType() {
        System.out.println("Válasz egy LOTTÓ típust!");
        System.out.println("------------------------");
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.println("1. Ötös lottó");
            System.out.println("2. Hatos lottó");
            System.out.println("3. Skandináv lottó");
            System.out.println("4. Euro Jackpot");
            System.out.println("5. Kenó");
            System.out.print("Írd be a megfelelő sorszámot a fentiek közül: ");
            try {
                int answer = Integer.parseInt(reader.readLine());
                return switch (answer) {
                    case 1 -> LottoType.LOTTO_5;
                    case 2 -> LottoType.LOTTO_6;
                    case 3 -> LottoType.LOTTO_7;
                    case 4 -> LottoType.EURO_JACKPOT;
                    case 5 -> LottoType.KENO;
                    default -> throw new IllegalStateException("Nem megfelelő a beírt sorszám!");
                };
            } catch (IOException exp) {
                System.out.println("Csak számot adj meg!");
            } catch (IllegalStateException exp) {
                System.out.println(exp.getMessage());
            }
        }
    }

}