package urlshortener.domain;

public class ClickInfo {
    private String osMostUsed;
    private String browserMostUsed;
    private String browserLastUsed;
    private String osLastUsed;

    public ClickInfo(String osMostUsed, String browserMostUsed, String browserLastUsed, String osLastUsed) {
        this.osMostUsed = osMostUsed;
        this.browserMostUsed = browserMostUsed;
        this.browserLastUsed = browserLastUsed;
        this.osLastUsed = osLastUsed;
    }

    public String getOsMostUsed() {
        return osMostUsed;
    }

    public String getBrowserMostUsed() {
        return browserMostUsed;
    }

    public String getBrowserLastUsed() {
        return browserLastUsed;
    }

    public String getOsLastUsed() {
        return osLastUsed;
    }
}
