package localapp.config;

public enum LottoType {

    LOTTO_5("Ötös lottó", 5, 90,5, "https://bet.szerencsejatek.hu/cmsfiles/otos.html"),
    LOTTO_6("Hatos lottó", 6, 45,6, "https://bet.szerencsejatek.hu/cmsfiles/hatos.html"),
    LOTTO_7("Skandináv lottó", 7, 35,7, "https://bet.szerencsejatek.hu/cmsfiles/skandi.html"),
    EURO_JACKPOT("Euro Jackpot", 7, 50, 7,"https://bet.szerencsejatek.hu/cmsfiles/eurojackpot.html"),
    KENO("Kenó", 20,80, 20, "https://bet.szerencsejatek.hu/cmsfiles/keno.html");


    private final String name;
    private final int type;
    private final int allNumber;
    private final int drawnedNumber;
    private final String url;


    private LottoType(String name, int type, int all, int drawned, String url) {
        this.name = name;
        this.type = type;
        this.allNumber = all;
        this.drawnedNumber = drawned;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public int getAllNumber() {
        return allNumber;
    }

    public int getDrawnedNumber() {
        return drawnedNumber;
    }

    public String getUrl() {
        return url;
    }

}