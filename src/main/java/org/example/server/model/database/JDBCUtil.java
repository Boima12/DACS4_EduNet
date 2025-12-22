package org.example.server.model.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.example.server.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * File kết nối với CSDL MySQL sử dụng thư viện HikariCP connection pool.
 * Có các mẫu code cho các methods.
 */
@SuppressWarnings("SqlSourceToSinkFlow")
public class JDBCUtil {

    private static HikariDataSource dataSource;
    private static final Logger log = LoggerFactory.getLogger(JDBCUtil.class);

    static {
        try {
            HikariConfig config = new HikariConfig();

            // Thông số kết nối
            config.setJdbcUrl("jdbc:mysql://localhost:3306/dacs4_edunet?useSSL=false");
            config.setUsername(Config.DATABASE_USERNAME);
            config.setPassword(Config.DATABASE_PASSWORD);

            // Tối ưu pool
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);
            config.setIdleTimeout(60000);         // 60s
            config.setConnectionTimeout(30000);    // 30s
            config.setMaxLifetime(1800000);        // 30 phút

            // MySQL driver class
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");

            dataSource = new HikariDataSource(config);

        } catch (Exception e) {
            log.error("Failed to initialize HikariCP", e);
        }
    }

    // Lấy connection từ pool
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // Đóng pool khi chương trình tắt
    public static void shutdown() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    /**
     * Execute INSERT, UPDATE, DELETE statements.
     * example uses:
     * String sql = "UPDATE users SET name=?, age=? WHERE id=?";
     * JDBCUtil.runUpdate(sql, "Boima", 20, 5);
     */
    public static int runUpdate(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            bindParams(stmt, params);

            int result = stmt.executeUpdate();
            log.debug("SQL Update executed: {} | affected rows={}", sql, result);
            return result;

        } catch (SQLException e) {
            log.error("SQL Update failed: {}", sql, e);
            return -1;
        }
    }

    /**
     * Execute SELECT queries.
     * example uses:
     * String sql = "SELECT id, name FROM users WHERE age > ?";
     * JDBCUtil.runQuery(sql, rs -> {
     *     while (rs.next()) {
     *         int id = rs.getInt("id");
     *         String name = rs.getString("name");
     *         System.out.println(id + " | " + name);
     *     }
     * }, 18);
     */
    public static void runQuery(String sql, ResultSetHandler handler, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            bindParams(stmt, params);

            try (ResultSet rs = stmt.executeQuery()) {
                handler.handle(rs);
            }

        } catch (SQLException e) {
            log.error("SQL Query failed: {}", sql, e);
        }
    }

    /**
     * Bind parameters into the PreparedStatement.
     */
    private static void bindParams(PreparedStatement stmt, Object... params) throws SQLException {
        if (params == null) return;

        for (int i = 0; i < params.length; i++) {
            Object p = params[i];

            if (p instanceof Integer) stmt.setInt(i+1, (Integer)p);
            else if (p instanceof Long) stmt.setLong(i+1, (Long)p);
            else if (p instanceof String) stmt.setString(i+1, (String)p);
            else if (p instanceof Boolean) stmt.setBoolean(i+1, (Boolean)p);
            else if (p instanceof Double) stmt.setDouble(i+1, (Double)p);
            else if (p instanceof Float) stmt.setFloat(i+1, (Float)p);
            else if (p instanceof java.sql.Date) stmt.setDate(i+1, (java.sql.Date)p);
            else if (p instanceof java.sql.Timestamp) stmt.setTimestamp(i+1, (java.sql.Timestamp)p);
            else {
                stmt.setObject(i+1, p);
                log.warn("Binding parameter with unknown type: {}", p.getClass());
            }
        }
    }

    /**
     * Handler interface for SELECT results.
     */
    public interface ResultSetHandler {
        void handle(ResultSet rs) throws SQLException;
    }
}
