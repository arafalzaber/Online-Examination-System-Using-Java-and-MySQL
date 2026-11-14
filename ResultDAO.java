package com.onlineexam;
import java.sql.*;
public class ResultDAO {
 public void saveResult(int studentId,int examId,int score,int total)throws SQLException{
  double percent=total>0?score*100.0/total:0;
  
  String sql="INSERT INTO results(student_id,exam_id,score,total_questions,percentage) VALUES(?,?,?,?,?)";
  try(Connection c=DBConnection.getConnection();
          PreparedStatement p=c.prepareStatement(sql)){
            p.setInt(1,studentId);
            p.setInt(2,examId);
            p.setInt(3,score);
            p.setInt(4,total);
            p.setDouble(5,percent);
            p.executeUpdate();
  }
 }
}
