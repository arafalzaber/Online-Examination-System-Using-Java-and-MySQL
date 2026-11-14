package com.onlineexam;
import java.sql.*;
public class AuthService {
 public User login(String username,String password,String role) throws SQLException {
  String sql="SELECT id,full_name,username,role FROM users WHERE username=? AND password=? AND role=? AND active=1";
  try(Connection c=DBConnection.getConnection();
          PreparedStatement p=c.prepareStatement(sql))
  {
   p.setString(1,username);
   p.setString(2,password);
   p.setString(3,role);
   try(ResultSet r=p.executeQuery()){
       if(r.next())return new User(r.getInt(1),r.getString(2),r.getString(3),r.getString(4));
   }
  } 
  return null;
 }
}
