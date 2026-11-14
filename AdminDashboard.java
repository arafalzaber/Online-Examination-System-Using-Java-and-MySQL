package com.onlineexam;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
public class AdminDashboard extends JFrame {
 private final User admin;
 private DataPanel users,exams,questions,results;
 public AdminDashboard(User u){
     admin=u;
     setTitle("Admin Dashboard - "+u.fullName);
     setSize(1000,650);
     setLocationRelativeTo(null);
     setDefaultCloseOperation(EXIT_ON_CLOSE);
  users=new DataPanel("User Accounts","SELECT id,full_name,username,role,active FROM users ORDER BY id",new String[]{
      "ID","Full name","Username","Role","Active"},new Object[][]{
          {
              "Add User",(Runnable)this::addUser},{
                  "Toggle Active",(Runnable)this::toggleUser},{
                  "Delete User",(Runnable)this::deleteUser
             }
      });
  exams=new DataPanel("Examinations","SELECT id,title,duration_minutes,created_by FROM exams ORDER BY id DESC",
          new String[]{
      "ID","Title","Minutes","Teacher ID"
          },
          new Object[][]{
              {
                  "Add Exam",(Runnable)this::addExam},
              {
                  "Delete Exam",(Runnable)this::deleteExam
              }
          });
  questions=new DataPanel("Question Bank","SELECT q.id,e.title,q.question_text,q.correct_option FROM questions q JOIN exams e ON e.id=q.exam_id ORDER BY q.id",
          new String[]{
              "ID","Exam","Question","Correct option"
          },
          new Object[][]{
              {
                  "Add Question",(Runnable)this::addQuestion
              },
              {
                  "Delete Question",
                  (Runnable)this::deleteQuestion}
          });
  results=new DataPanel("All Results","SELECT r.id,u.full_name,e.title,r.score,r.total_questions,r.percentage,r.submitted_at FROM results r JOIN users u ON u.id=r.student_id JOIN exams e ON e.id=r.exam_id ORDER BY r.id DESC",new String[]{"ID","Student","Exam","Score","Total","%","Submitted"},
          new Object[][]{
          });
  
  
  JTabbedPane tabs=new JTabbedPane();
  tabs.add("Users",users);
  tabs.add("Exams",exams);
  tabs.add("Questions",questions);
  tabs.add("Results",results);
  add(tabs);
  setJMenuBar(menu());
 }
 private JMenuBar menu(){
     JMenuBar b=new JMenuBar();
     JMenu m=new JMenu("Account");
     JMenuItem out=new JMenuItem("Logout");
     out.addActionListener(e->{
         
         dispose();
         new LoginFrame().setVisible(true);
     });
     m.add(out);
     b.add(m);
     return b;
 }
 private void addUser(){
     JTextField n=new JTextField(),u=new JTextField(),p=new JTextField();
     JComboBox<String> r=new JComboBox<>(new String[]{
         "ADMIN","TEACHER","STUDENT"});
     Object[] f={
         "Full name",n,"Username",u,"Password",p,"Role",r
     };
     if(JOptionPane.showConfirmDialog(this,f,"Create account",JOptionPane.OK_CANCEL_OPTION)==0){
         try(Connection c=DBConnection.getConnection();PreparedStatement s=c.prepareStatement("INSERT INTO users(full_name,username,password,role) VALUES(?,?,?,?)")){
             
             s.setString(1,n.getText().trim());
             s.setString(2,u.getText().trim());
             s.setString(3,p.getText());
             s.setString(4,(String)r.getSelectedItem());
             s.executeUpdate();
             users.load();
         }
         
         catch(SQLException e){
             msg(e);
         }
     }
 }
 
 private void toggleUser(){
     int row=users.selectedRow();
     if(row<0)
         
         return;
     
     try(Connection c=DBConnection.getConnection();
             PreparedStatement p=c.prepareStatement("UPDATE users SET active=1-active WHERE id=?")){
         
         p.setInt(1,(int)users.value(row,0));
         p.executeUpdate();
         users.load();
     }
     catch(SQLException e){
         msg(e);
     }
 }
 private void deleteUser(){
     int row=users.selectedRow();
     if(row<0)
         return;
     int id=(int)users.value(row,0);
     if(id==admin.id){
         JOptionPane.showMessageDialog(this,"You cannot delete your own account.");
         return;
     }
     try(Connection c=DBConnection.getConnection();
             PreparedStatement p=c.prepareStatement("DELETE FROM users WHERE id=?")){
         p.setInt(1,id);
         p.executeUpdate();
         users.load();
     }
     catch(SQLException e){
         msg(e);
     }
 }
 private void addExam(){
     JTextField t=new JTextField();
     JSpinner d=new JSpinner(new SpinnerNumberModel(10,1,240,1));
     Object[] f={
         "Exam title",t,"Duration (minutes)",d
     };
     if(JOptionPane.showConfirmDialog(this,f,"Create exam",JOptionPane.OK_CANCEL_OPTION)==0)
         try(Connection c=DBConnection.getConnection();
                 PreparedStatement p=c.prepareStatement("INSERT INTO exams(title,duration_minutes,created_by) VALUES(?,?,?)")){
             p.setString(1,t.getText().trim());
             p.setInt(2,(int)d.getValue());
             p.setInt(3,admin.id);
             p.executeUpdate();
             exams.load();
             questions.load();
         }
         
         catch(SQLException e){
             msg(e);
         }
 }
 private void deleteExam(){
     int r=exams.selectedRow();
     if(r<0)return;
     try(Connection c=DBConnection.getConnection();
             PreparedStatement p=c.prepareStatement("DELETE FROM exams WHERE id=?")){
         p.setInt(1,(int)exams.value(r,0));
         p.executeUpdate();
         exams.load();
         questions.load();
     }
     catch(SQLException e){
         msg(e);
     }
 }
 private void addQuestion(){
     JTextField exam=new JTextField(),
             q=new JTextField(),
             a=new JTextField(),
             b=new JTextField(),
             c1=new JTextField(),
             d=new JTextField();
     JComboBox<Integer> correct=new JComboBox<>(new Integer[]{1,2,3,4});
     Object[] f={"Exam ID",exam,"Question",q,"Option A",a,"Option B",b,"Option C",c1,"Option D",d,"Correct option (1-4)",correct
     };
     if(JOptionPane.showConfirmDialog(this,f,"Add MCQ",JOptionPane.OK_CANCEL_OPTION)==0)
         try(Connection c=DBConnection.getConnection();
                 PreparedStatement p=c.prepareStatement("INSERT INTO questions(exam_id,question_text,option_a,option_b,option_c,option_d,correct_option) VALUES(?,?,?,?,?,?,?)")){
             p.setInt(1,Integer.parseInt(exam.getText()));
             p.setString(2,q.getText());
             p.setString(3,a.getText());
             p.setString(4,b.getText());
             p.setString(5,c1.getText());
             p.setString(6,d.getText());
             p.setInt(7,(int)correct.getSelectedItem());
             p.executeUpdate();
             questions.load();
         }
         catch(Exception e){
             msg(e);
         }
 }
 private void deleteQuestion(){
     int r=questions.selectedRow();
     if(r<0)return;
     try(Connection c=DBConnection.getConnection();
             PreparedStatement p=c.prepareStatement("DELETE FROM questions WHERE id=?")){
         p.setInt(1,(int)questions.value(r,0));
         p.executeUpdate();
         questions.load();
     }
     
     catch(SQLException e){
         msg(e);
     }
 }
 private void msg(Exception e){
     JOptionPane.showMessageDialog(this,e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
 }
}
