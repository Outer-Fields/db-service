package io.mindspice.okradatabaseservice.database;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.mindspice.okradatabaseservice.Settings;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;


public class ConnectionManager {
    private static HikariConfig config = new HikariConfig();
    private static DataSource dataSource;

    static {
        config.setJdbcUrl(Settings.get().databaseUrl());
        config.setUsername(Settings.get().databaseUsername());
        config.setPassword(Settings.get().dataBasePassword());
        config.setMaximumPoolSize(Settings.get().maxPoolSize());
        config.setIdleTimeout(Settings.get().connTimeout());
        config.setMinimumIdle(Settings.get().idleConnMin());

        Properties props = new Properties();
        props.setProperty("cachePrepStmts", String.valueOf(Settings.get().cachePStatements()));
        props.setProperty("prepStmtCacheSize", String.valueOf(Settings.get().pStatementCacheSize()));
        props.setProperty("prepStmtCacheSqlLimit", String.valueOf(Settings.get().pStatementLengthLimit()));
        config.setDataSourceProperties(props);

        dataSource = new HikariDataSource(config);
    }


    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }


}
