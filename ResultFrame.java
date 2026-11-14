package com.onlineexam;
import javax.swing.*;import java.awt.*;
public class ResultFrame extends JFrame {
 public ResultFrame(String name,String exam,int score,int total,boolean saved,String error){
     setTitle("Examination Result");
     setSize(480,300);
     setLocationRelativeTo(null);
     setDefaultCloseOperation(DISPOSE_ON_CLOSE);
     
     double percent=total>0?score*100.0/total:0;
        JLabel title=new JLabel("Examination Result",SwingConstants.CENTER);
        title.setFont(new Font("Arial",Font.BOLD,22));
        JPanel info=new JPanel(new GridLayout(0,1,5,5));
        
        info.add(new JLabel("Student: "+name));
        info.add(new JLabel("Exam: "+exam));
        info.add(new JLabel("Score: "+score+" / "+total));
        info.add(new JLabel(String.format("Percentage: %.2f%%",percent)));
        info.add(new JLabel(saved?"Result saved successfully.":"Result was NOT saved."));
        if(!saved&&error!=null)info.add(new JLabel(error));
    JButton close=new JButton("Close");
    close.addActionListener(e->dispose());
    JPanel p=new JPanel(new BorderLayout(12,18));
    p.setBorder(BorderFactory.createEmptyBorder(22,25,22,25));
    p.add(title,BorderLayout.NORTH);
    p.add(info,BorderLayout.CENTER);
    p.add(close,BorderLayout.SOUTH);
    
    setContentPane(p);
 }
}
