package com.onlineexam;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;import java.sql.*;

public class StudentDashboard extends JFrame {
 private final User student;
 private final DefaultTableModel model=new DefaultTableModel(new String[]{"Exam ID","Examination","Duration (minutes)","Teacher"},0);
 private final JTable table=new JTable(model);
 public StudentDashboard(User u){
     student=u;
     setTitle("Student Dashboard - "+u.fullName);
     setSize(780,480);
     setLocationRelativeTo(null);
     setDefaultCloseOperation(EXIT_ON_CLOSE);
  JLabel title=new JLabel("Available Examinations",SwingConstants.CENTER);
  title.setFont(new Font("Arial",Font.BOLD,20));
  JButton start=new JButton("Start Selected Exam"),refresh=new JButton("Refresh"),logout=new JButton("Logout");
  
  start.addActionListener(e->startExam());
  refresh.addActionListener(e->load());
  logout.addActionListener(e->{dispose();
  
  new LoginFrame().setVisible(true);});
  JPanel bottom=new JPanel();
  bottom.add(start);
  bottom.add(refresh);
  bottom.add(logout);
  add(title,BorderLayout.NORTH);
  add(new JScrollPane(table),BorderLayout.CENTER);
  add(bottom,BorderLayout.SOUTH);load();
 }
 
 private void load(){model.setRowCount(0);
 String sql="SELECT e.id,e.title,e.duration_minutes,u.full_name FROM exams e JOIN users u ON u.id=e.created_by WHERE e.active=1 ORDER BY e.id DESC";
 try(Connection c=DBConnection.getConnection();
         Statement s=c.createStatement();
         ResultSet r=s.executeQuery(sql)){
     while(r.next())model.addRow(new Object[]{r.getInt(1),r.getString(2),r.getInt(3),r.getString(4)});
 }
 
 catch(SQLException e){
     JOptionPane.showMessageDialog(this,e.getMessage());
 }
  }
 
 private void startExam(){
     int row=table.getSelectedRow();
     if(row<0){
         JOptionPane.showMessageDialog(this,"Select an examination first.");
         return;
     }
     int examId=(int)model.getValueAt(row,0),minutes=(int)model.getValueAt(row,2);
     String title=(String)model.getValueAt(row,1);
     try{
         ExamService service=new ExamService();
         java.util.List<Question> qs=service.getQuestions(examId);
         if(qs.isEmpty()){JOptionPane.showMessageDialog(this,"This exam has no questions yet.");
         return;
         }
         new QuizFrame(student,examId,title,minutes,service,qs).setVisible(true);
         dispose();
     }
     
     catch(SQLException e){
         JOptionPane.showMessageDialog(this,e.getMessage(),"Database error",JOptionPane.ERROR_MESSAGE);
     }
 }
}
