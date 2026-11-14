package com.onlineexam;
import java.sql.*;
import java.util.*;
public class ExamService {
 public List<Question> getQuestions(int examId)throws SQLException{
  List<Question> list=new ArrayList<>();
  String sql="SELECT id,question_text,option_a,option_b,option_c,option_d,correct_option FROM questions WHERE exam_id=? ORDER BY id";
  
  try(Connection c=DBConnection.getConnection();
          PreparedStatement p=c.prepareStatement(sql)){
      p.setInt(1,examId);
      try(ResultSet r=p.executeQuery()){
          while(r.next()){
              String[] o={
                  r.getString("option_a"),r.getString("option_b"),r.getString("option_c"),r.getString("option_d")
              };
              list.add(new Question(r.getInt("id"),
                      r.getString("question_text"),
                      o,r.getInt("correct_option")-1));
          }
      }
  }
  
  return list;
 
 }
 
 public int calculateScore(List<Question> q,int[] a){
     int s=0;
     for(int i=0;i<Math.min(q.size(),a.length);i++)
         if(a[i]==q.get(i).getCorrectAnswer())s++;
     return s;
 }
}
