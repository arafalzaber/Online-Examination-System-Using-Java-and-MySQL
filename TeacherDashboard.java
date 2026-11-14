package com.onlineexam;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
public class TeacherDashboard extends JFrame {
 private final User teacher;
 private DataPanel exams,questions,results;
 public TeacherDashboard(User u){
     teacher=u;setTitle("Teacher Dashboard - "+u.fullName);
     setSize(950,600);
     setLocationRelativeTo(null);
     setDefaultCloseOperation(EXIT_ON_CLOSE);
  exams=new DataPanel("My Examinations","SELECT id,title,duration_minutes FROM exams WHERE created_by="+u.id+" ORDER BY id DESC",new String[]{"ID","Title","Minutes"},new Object[][]{
      {
          "Create Exam",(Runnable)this::createExam
      },
      {
          "Delete Exam",(Runnable)this::deleteExam
      }
  });
  questions=new DataPanel("My Question Bank","SELECT q.id,e.title,q.question_text,q.correct_option FROM questions q JOIN exams e ON e.id=q.exam_id WHERE e.created_by="+u.id+" ORDER BY q.id",new String[]{"ID","Exam","Question","Correct option"},new Object[][]{{"Add MCQ",(Runnable)this::addQuestion},{"Delete MCQ",(Runnable)this::deleteQuestion}});
  results=new DataPanel("Results for My Exams","SELECT r.id,u.full_name,e.title,r.score,r.total_questions,r.percentage,r.submitted_at FROM results r JOIN users u ON u.id=r.student_id JOIN exams e ON e.id=r.exam_id WHERE e.created_by="+u.id+" ORDER BY r.id DESC",new String[]{"ID","Student","Exam","Score","Total","%","Submitted"},new Object[][]{});
  JTabbedPane tabs=new JTabbedPane();tabs.add("Examinations",exams);tabs.add("Questions",questions);tabs.add("Results",results);add(tabs);
  JMenuBar bar=new JMenuBar();JMenu m=new JMenu("Account");JMenuItem out=new JMenuItem("Logout");out.addActionListener(e->{dispose();new LoginFrame().setVisible(true);});m.add(out);bar.add(m);setJMenuBar(bar);
 }
 private void createExam(){
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
             p.setInt(3,teacher.id);
             p.executeUpdate();
             exams.load();
         }
         catch(SQLException e){
             msg(e);
         }
 }
 private void deleteExam(){
     int r=exams.selectedRow();
     if(r<0)return;
     try(Connection c=DBConnection.getConnection();
             PreparedStatement p=c.prepareStatement("DELETE FROM exams WHERE id=? AND created_by=?")){
         p.setInt(1,(int)exams.value(r,0));
         p.setInt(2,teacher.id);
         p.executeUpdate();
         exams.load();
         questions.load();
     }
     catch(SQLException e){
         msg(e);
     }
 }
 private void addQuestion(){
     JTextField id=new JTextField(),
             q=new JTextField(),
             a=new JTextField(),
             b=new JTextField(),
             c1=new JTextField(),
             d=new JTextField();
     JComboBox<Integer> correct=new JComboBox<>(new Integer[]{1,2,3,4});
     Object[] f={
         "Your Exam ID",id,"Question",q,"Option A",a,"Option B",b,"Option C",c1,"Option D",d,"Correct option",correct
     };
     if(JOptionPane.showConfirmDialog(this,f,"Add MCQ",JOptionPane.OK_CANCEL_OPTION)==0)
         try(Connection c=DBConnection.getConnection();
                 PreparedStatement p=c.prepareStatement("INSERT INTO questions(exam_id,question_text,option_a,option_b,option_c,option_d,correct_option) SELECT id,?,?,?,?,?,? FROM exams WHERE id=? AND created_by=?")){
             p.setString(1,q.getText());
             p.setString(2,a.getText());
             p.setString(3,b.getText());
             p.setString(4,c1.getText());
             p.setString(5,d.getText());
             p.setInt(6,(int)correct.getSelectedItem());
             p.setInt(7,Integer.parseInt(id.getText()));
             p.setInt(8,teacher.id);
             if(p.executeUpdate()==0)throw new SQLException("Exam not found or not owned by you.");
             questions.load();
         }
         catch(Exception e){
             msg(e);}
 }
 private void deleteQuestion(){
     int r=questions.selectedRow();
     if(r<0)return;
     try(Connection c=DBConnection.getConnection();
             PreparedStatement p=c.prepareStatement("DELETE q FROM questions q JOIN exams e ON e.id=q.exam_id WHERE q.id=? AND e.created_by=?")){
         p.setInt(1,(int)questions.value(r,0));
         p.setInt(2,teacher.id);
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
