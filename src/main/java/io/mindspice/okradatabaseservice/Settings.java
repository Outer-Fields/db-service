package io.mindspice.okradatabaseservice;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;


public class Settings {
    private static Settings instance = null;
    // DB Connection
    private boolean isPsql;
    private String databaseUrl;
    private String databaseUsername;
    private String dataBasePassword;
    // Connection Pool Settings
    private int maxPoolSize;
    private int idleConnMin;
    private long connTimeout;
    private boolean cachePStatements;
    private int pStatementCacheSize;
    private int pStatementLengthLimit;
    // For connections
    private String restPassword;





    public static Settings get() {
        if (instance == null) {
            String config = System.getProperty("user.dir") + File.separator + "config.yaml";
            System.out.println(config);
            File configFile = Path.of(config).toFile();
            var mapper = new ObjectMapper(new YAMLFactory());
            try {
                instance = mapper.readValue(configFile, Settings.class);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                throw new RuntimeException("Failed To Load Config");
            }
            return instance;
        } else {
            return instance;
        }
    }



     public String databaseUrl() {
        return databaseUrl;
    }

    public String databaseUsername() {
        return databaseUsername;
    }

    public String dataBasePassword() {
        return dataBasePassword;
    }

    public int maxPoolSize() {
        return maxPoolSize;
    }

    public int idleConnMin() {
        return idleConnMin;
    }

    public long connTimeout() {
        return connTimeout;
    }

    public boolean cachePStatements() {
        return cachePStatements;
    }

    public int pStatementCacheSize() {
        return pStatementCacheSize;
    }

    public int pStatementLengthLimit() {
        return pStatementLengthLimit;
    }

    public boolean isPsql() {
        return isPsql;
    }

    public String getRestPassword() {
        return restPassword;
    }

    // For jackson

    private static void setInstance(Settings instance) {
        Settings.instance = instance;
    }

    private void setDatabaseUrl(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }

    private void setDatabaseUsername(String databaseUsername) {
        this.databaseUsername = databaseUsername;
    }

    private void setDataBasePassword(String dataBasePassword) {
        this.dataBasePassword = dataBasePassword;
    }

    private void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    private void setIdleConnMin(int idleConnMin) {
        this.idleConnMin = idleConnMin;
    }

    private void setConnTimeout(long connTimeout) {
        this.connTimeout = connTimeout;
    }

    private void setCachePStatements(boolean cachePStatements) {
        this.cachePStatements = cachePStatements;
    }

    private void setpStatementCacheSize(int pStatementCacheSize) {
        this.pStatementCacheSize = pStatementCacheSize;
    }

    private void setpStatementLengthLimit(int pStatementLengthLimit) {
        this.pStatementLengthLimit = pStatementLengthLimit;
    }

    public void setIsPsql(boolean psql) {
        isPsql = psql;
    }

    public void setRestPassword(String restPassword) {
        this.restPassword = restPassword;
    }
}
