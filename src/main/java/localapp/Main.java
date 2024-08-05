package localapp;

import localapp.config.LottoType;
import localapp.config.ServiceFactory;
import localapp.model.LotteryDrawn;
import localapp.model.WeeksAgoWasItPulled;
import localapp.service.*;
import localapp.view.Show;
import java.util.List;


public class Main {
    public static void main(String[] args) {

        LottoType type = LottoType.KENO;

        HtmlReaderService reader = ServiceFactory.createHtmlReader(type);
        StatisticsService statistics = ServiceFactory.createStatisticsService(type);
        Show show = new Show(type);

        try {
            // load all drawned lottery number from website
            List<int[]> drawnedNumbers = reader.getAllNumbers();

            // FYI: -1 all, 20% --> 0.2, 10% --> 0.1, 33% --> 0.33333
            int maxItem = (int) (type.getAllNumber() * 0.2);

            // Week by week, we look back to see how long ago each number was drawn and create a statistical list based on this.
            List<LotteryDrawn> historyData = statistics.getLotteryDrawPreviousWeeksHistory(drawnedNumbers);

            // Csak az EuroJackpotnál van segédszámok, itt fixen a legrégebbi 3-at keresi ki és írja ki
            List<WeeksAgoWasItPulled> oldestMainNumbers = statistics.getOldestNumbers(historyData, maxItem, true);
            List<WeeksAgoWasItPulled> oldestSideNumbers = statistics.getOldestNumbers(historyData, 3, false);
            show.oldestNumbers(oldestMainNumbers, oldestSideNumbers);

        } catch (Exception exp) {
            String error = String.format("Hiba történt! %s", exp.getMessage());
            System.out.println(error);
        }

    }

}