package com.onlineexam;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
public class DataPanel extends JPanel {
 private final String sql;
 private final Object[][] actions;
 private final DefaultTableModel model=new DefaultTableModel();
 private final JTable table=new JTable(model);
 
 public DataPanel(String title,String query,String[] columns,Object[][] buttons){
  super(new BorderLayout(8,8));
  this.sql=query;
  this.actions=buttons;
  model.setColumnIdentifiers(columns);
  add(new JLabel(title),BorderLayout.NORTH);
  add(new JScrollPane(table),BorderLayout.CENTER);
  
  JPanel bar=new JPanel();
  for(Object[] b:buttons){
      JButton x=new JButton((String)b[0]);
      x.addActionListener(e->((Runnable)b[1]).run());
      bar.add(x);
  }
  JButton refresh=new JButton("Refresh");
  refresh.addActionListener(e->load());
  bar.add(refresh);
  add(bar,BorderLayout.SOUTH);
  load();
 }
 public void load(){
     model.setRowCount(0);
     try(Connection c=DBConnection.getConnection();
             Statement s=c.createStatement();
             ResultSet r=s.executeQuery(sql)){
         int n=r.getMetaData().getColumnCount();
         
         while(r.next()){
             Object[] row=new Object[n];
             for(int i=0;i<n;i++)row[i]=r.getObject(i+1);
             model.addRow(row);
         }
     }
     catch(SQLException e){
         JOptionPane.showMessageDialog(this,e.getMessage(),"Database error",JOptionPane.ERROR_MESSAGE);
     }
 }
 public Object value(int row,int col){
     return table.getValueAt(row,col);
 }
 public int selectedRow(){
     return table.getSelectedRow();
 }
 public JTable getTable(){
     return table;
 }
}
