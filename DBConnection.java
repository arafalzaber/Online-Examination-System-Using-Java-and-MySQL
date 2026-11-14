package com.onlineexam;
import java.sql.*;
public final class DBConnection {
 private static final String URL="jdbc:mysql://localhost:3306/online_exam?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
 private static final String USER="root";
 private static final String PASSWORD="";
 private DBConnection(){}
 public static Connection getConnection() throws SQLException { 
     return DriverManager.getConnection(URL,USER,PASSWORD);
 }
}
