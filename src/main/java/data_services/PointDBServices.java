package data_services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import models.Point;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PointDBServices {
    
    @Inject
    private DatabaseConnection dbConnection;

    private static final String INSERT_POINT = 
            "INSERT INTO session_results (jsessionid, x, y, r, hit, calculation_time, released_time) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_BY_SESSION = 
            "SELECT id, jsessionid, x, y, r, hit, calculation_time, released_time " +
            "FROM session_results WHERE jsessionid = ? " +
            "ORDER BY released_time DESC LIMIT ? OFFSET ?";
    
    private static final String COUNT_BY_SESSION = 
            "SELECT COUNT(*) FROM session_results WHERE jsessionid = ?";
    
    private static final String DELETE_BY_SESSION = 
            "DELETE FROM session_results WHERE jsessionid = ?";

    public Point save(Point point) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(INSERT_POINT, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, point.getSessionId());
            stmt.setBigDecimal(2, point.getX());
            stmt.setBigDecimal(3, point.getY());
            stmt.setBigDecimal(4, point.getR());
            stmt.setBoolean(5, point.isHit());
            stmt.setDouble(6, point.getCalTime());
            stmt.setTimestamp(7, Timestamp.valueOf(point.getReleaseTime()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    point.setId(rs.getInt(1));
                }
            }
            
            return point;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error saving point: " + e.getMessage(), e);
        } finally {
            closeResources(rs, stmt, conn);
        }
    }

    public List<Point> findRangeBySession(String sessionId, int first, int pageSize) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Point> points = new ArrayList<>();
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(SELECT_BY_SESSION);
            
            stmt.setString(1, sessionId);
            stmt.setInt(2, pageSize);
            stmt.setInt(3, first);
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Point point = new Point();
                point.setId(rs.getInt("id"));
                point.setSessionId(rs.getString("jsessionid"));
                point.setX(rs.getBigDecimal("x"));
                point.setY(rs.getBigDecimal("y"));
                point.setR(rs.getBigDecimal("r"));
                point.setHit(rs.getBoolean("hit"));
                point.setCalTime(rs.getDouble("calculation_time"));
                point.setReleaseTime(rs.getTimestamp("released_time").toLocalDateTime());
                
                points.add(point);
            }
            
            return points;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error finding points: " + e.getMessage(), e);
        } finally {
            closeResources(rs, stmt, conn);
        }
    }

    public long countBySession(String sessionId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(COUNT_BY_SESSION);
            stmt.setString(1, sessionId);
            
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            
            return 0;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error counting points: " + e.getMessage(), e);
        } finally {
            closeResources(rs, stmt, conn);
        }
    }

    public int deleteAllBySession(String sessionId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = dbConnection.getConnection();
            stmt = conn.prepareStatement(DELETE_BY_SESSION);
            stmt.setString(1, sessionId);
            
            return stmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting points: " + e.getMessage(), e);
        } finally {
            closeResources(null, stmt, conn);
        }
    }

    private void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Error closing ResultSet: " + e.getMessage());
            }
        }
        
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing Statement: " + e.getMessage());
            }
        }
        
        if (conn != null) {
            dbConnection.closeConnection(conn);
        }
    }
}
