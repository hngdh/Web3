package data_services;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@ApplicationScoped
public class DatabaseConnection {
    
    private DataSource dataSource;
    
    @PostConstruct
    public void init() {
        try {
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("java:/PostgresDS");
            System.out.println("DataSource initialized successfully");
        } catch (NamingException e) {
            throw new RuntimeException("Failed to lookup DataSource: " + e.getMessage(), e);
        }
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource is not initialized");
        }
        return dataSource.getConnection();
    }

    public void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    @PreDestroy
    public void cleanup() {
        System.out.println("DatabaseConnection cleanup");
    }
}
