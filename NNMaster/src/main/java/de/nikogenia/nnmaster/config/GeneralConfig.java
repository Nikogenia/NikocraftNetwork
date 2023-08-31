package de.nikogenia.nnmaster.config;

public class GeneralConfig {

    private Integer apiPort;
    private String apiKey;

    private String name;
    private String fullName;
    private String dockerPrefix;
    private String rootPath;

    public Integer getAPIPort() {
        return apiPort;
    }

    public void setAPIPort(Integer apiPort) {
        this.apiPort = apiPort;
    }

    public String getAPIKey() {
        return apiKey;
    }

    public void setAPIKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getDockerPrefix() {
        return dockerPrefix;
    }

    public void setDockerPrefix(String dockerPrefix) {
        this.dockerPrefix = dockerPrefix;
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

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

}
