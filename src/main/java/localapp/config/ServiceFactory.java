package localapp.config;

import localapp.service.*;
import java.util.Objects;


public class ServiceFactory {

    private ServiceFactory() {
    }

    public static FileReaderService createHtmlReader(LottoType type) {
        return switch (type) {
            case LOTTO_7 -> new FileReaderServiceLottery7Impl(type);
            case KENO -> new FileReaderServiceKenoImpl(type);
            default -> new FileReaderService(type);
        };
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