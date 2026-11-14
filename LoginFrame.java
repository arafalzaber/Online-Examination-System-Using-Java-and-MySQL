package com.onlineexam;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
public class LoginFrame extends JFrame {
 private JTextField username=new JTextField(18);
 private JPasswordField password=new JPasswordField(18);
 private JComboBox<String> role=new JComboBox<>(new String[]{"STUDENT","TEACHER","ADMIN"});
 public LoginFrame(){
  setTitle("Online Examination System - Login");
  setSize(440,310);
  setLocationRelativeTo(null);
  setDefaultCloseOperation(EXIT_ON_CLOSE);
  
  JLabel title=new JLabel("ONLINE EXAMINATION SYSTEM",SwingConstants.CENTER);
  title.setFont(new Font("Arial",Font.BOLD,19));
  
  JPanel form=new JPanel(new GridLayout(3,2,10,12));
  form.add(new JLabel("Role:"));
  form.add(role);
  form.add(new JLabel("Username:"));
  form.add(username);
  form.add(new JLabel("Password:"));
  form.add(password);
  
  JButton login=new JButton("Login");
  login.addActionListener(e->doLogin());
  JButton exit=new JButton("Exit");
  exit.addActionListener(e->System.exit(0));
  JPanel buttons=new JPanel();
  buttons.add(login);buttons.add(exit);
  
  JPanel p=new JPanel(new BorderLayout(15,20));
  p.setBorder(BorderFactory.createEmptyBorder(25,30,25,30));
  p.add(title,BorderLayout.NORTH);
  p.add(form,BorderLayout.CENTER);
  p.add(buttons,BorderLayout.SOUTH);
  setContentPane(p);
  getRootPane().setDefaultButton(login);
 }
 private void doLogin(){
     try{
         User u=new AuthService().login(username.getText().trim(),
                 new String(password.getPassword()),
                 (String)role.getSelectedItem());
         if(u==null){
             JOptionPane.showMessageDialog(this,"Invalid login details or inactive account.");
             return;
         }
  
         dispose();
         if(u.role.equals("ADMIN"))new AdminDashboard(u).setVisible(true);
         else if(u.role.equals("TEACHER"))new TeacherDashboard(u).setVisible(true);
         else new StudentDashboard(u).setVisible(true);
 }
     catch(SQLException ex){
         JOptionPane.showMessageDialog(this,"Database error: "+ex.getMessage());
     }
 }
}
