package dal;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author FPT University - PRJ30X
 */
public class DBContext {
    protected Connection connection;
    public DBContext() {
        //@Students: You are not allowed to edit this method  
        try {
            Properties properties = new Properties();
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("ConnectDB.properties");
            if (inputStream == null) {
                inputStream = getClass().getClassLoader().getResourceAsStream("dal/ConnectDB.properties");
            }
            if (inputStream == null) {
                Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, "ConnectDB.properties not found in classpath");
                throw new IllegalStateException("ConnectDB.properties not found in classpath");
            }
            try {
                properties.load(inputStream);
            } catch (IOException ex) {
                Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
                throw new IllegalStateException("Failed to load ConnectDB.properties", ex);
            }
            String user = properties.getProperty("userID");
            String pass = properties.getProperty("password");
            String url = properties.getProperty("url");
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
            throw new IllegalStateException("Database connection failed. Check SQL Server, credentials, and URL.", ex);
        }
    }
    
    
    
public static void main(String[] args) {
    try {
        DBContext db = new DBContext();
        if (db.connection != null && !db.connection.isClosed()) {
            System.out.println("Chúc mừng! Kết nối Database thành công.");
        } else {
            System.out.println("Kết nối thất bại!");
        }
    } catch (SQLException ex) {
        System.out.println("Lỗi khi kiểm tra trạng thái kết nối: " + ex.getMessage());
    } catch (Exception e) {
        System.out.println("Lỗi hệ thống: " + e.getMessage());
    }
}
    
}


