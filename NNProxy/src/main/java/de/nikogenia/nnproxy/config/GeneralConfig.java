package de.nikogenia.nnproxy.config;

public class GeneralConfig {

    private String apiKey;
    private Integer apiConnectionRetry;

    private String name;
    private String fullName;

    private String timeZone;

    public String getAPIKey() {
        return apiKey;
    }

    public void setAPIKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Integer getAPIConnectionRetry() {
        return apiConnectionRetry;
    }

    public void setAPIConnectionRetry(Integer apiConnectionRetry) {
        this.apiConnectionRetry = apiConnectionRetry;
    }

}
