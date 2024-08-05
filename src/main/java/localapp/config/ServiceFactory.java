package localapp.config;

import localapp.service.HtmlReaderService;
import localapp.service.HtmlReaderServiceLottery7Impl;
import localapp.service.StatisticServiceEuroJackpotImpl;
import localapp.service.StatisticsService;
import java.util.Objects;

public class ServiceFactory {

    private ServiceFactory() {
    }

    public static HtmlReaderService createHtmlReader(LottoType type) {
        HtmlReaderService result;
        if (Objects.requireNonNull(type) == LottoType.LOTTO_7) {
            result = new HtmlReaderServiceLottery7Impl(type);
        } else {
            result = new HtmlReaderService(type);
        }
        return result;
    }

    public static StatisticsService createStatisticsService(LottoType type) {
        StatisticsService result;
        if (Objects.requireNonNull(type) == LottoType.EURO_JACKPOT) {
            result = new StatisticServiceEuroJackpotImpl(type);
        } else {
            result = new StatisticsService(type);
        }
        return result;
    }

}