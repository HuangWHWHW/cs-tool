package utils;

public class URLParser {
    private static final String DWS_URL_START_WITH = "jdbc:postgresql://";
    private String domain;
    private String port;
    private String dbname;

    private URLParser(String url) {
        try {
            domain = url.replace(DWS_URL_START_WITH, "").split(":")[0];
            port = url.replace(DWS_URL_START_WITH, "").split(":")[1].split("/")[0];
            dbname = url.replace(DWS_URL_START_WITH + domain + ":" + port + "/", "");
        } catch (Exception e) {
            throw new IllegalArgumentException("Illegal dws url: " + url);
        }

    }

    public static URLParser parse(String url) {
        return new URLParser(url);
    }

    public URLParser replaceDomain(String domain) {
        this.domain = domain;
        return this;
    }

    public String toString() {
        return DWS_URL_START_WITH + domain + ":" + port + "/" + dbname;
    }
}
