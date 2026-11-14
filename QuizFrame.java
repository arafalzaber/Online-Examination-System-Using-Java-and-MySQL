package com.onlineexam;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class QuizFrame extends JFrame {
    private final User student;
    private final int examId;
    private final String examTitle;
    private final ExamService service;
    private final java.util.List<Question> questions;
    private final int[] answers;
 
    private int current=0,seconds;
    private boolean submitted=false;
 
    private final JLabel question=new JLabel(),clock=new JLabel();
    
    private final JRadioButton[] choices=new JRadioButton[4];
    private final ButtonGroup group=new ButtonGroup();
 
    private final JButton previous=new JButton("Previous"),next=new JButton("Next"),submit=new JButton("Submit Exam");
    private javax.swing.Timer timer;
    
    public QuizFrame(User student,int examId,String examTitle,int minutes,ExamService service,java.util.List<Question> questions){
        
        this.student=student;
        this.examId=examId;
        this.examTitle=examTitle;
        this.service=service;
        this.questions=questions;
        this.seconds=minutes*60;
        answers=new int[questions.size()];
        Arrays.fill(answers,-1);
  
        setTitle("Examination - "+examTitle);
        setSize(760,430);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
  
        question.setFont(new Font("Arial",Font.BOLD,16));
        clock.setForeground(Color.RED);
        updateClock();
    
        JPanel head=new JPanel(new BorderLayout());
        head.add(question,BorderLayout.CENTER);
        head.add(clock,BorderLayout.EAST);
  
        JPanel opts=new JPanel();
        opts.setLayout(new BoxLayout(opts,BoxLayout.Y_AXIS));
            for(int i=0;i<4;i++){
                
                choices[i]=new JRadioButton();
                group.add(choices[i]);
                opts.add(choices[i]);
            }
  
            JPanel nav=new JPanel();
            nav.add(previous);
            nav.add(next);
            nav.add(submit);
            
            JPanel panel=new JPanel(new BorderLayout(15,20));
            panel.setBorder(BorderFactory.createEmptyBorder(25,25,25,25));
            panel.add(head,BorderLayout.NORTH);
            panel.add(opts,BorderLayout.CENTER);
            panel.add(nav,BorderLayout.SOUTH);
            setContentPane(panel);
  
            previous.addActionListener(e->move(-1));
            next.addActionListener(e->move(1));
            submit.addActionListener(e->submitExam(false));
            showQuestion();
  
            timer=new javax.swing.Timer(1000,e->{
                seconds--;
                updateClock();
                if(seconds<=0){
                    ((javax.swing.Timer)e.getSource()).stop();
                    submitExam(true);
                }
            });
            
            timer.start();
 }
    private void updateClock(){
        clock.setText("Time left: "+seconds/60+":"+String.format("%02d",seconds%60));
    }
    
    private void showQuestion(){
        Question q=questions.get(current);
        question.setText("<html><body style='width:470px'>Question "+(current+1)+" of "+questions.size()+": "+q.getQuestionText()+"</body></html>");
        group.clearSelection();
        String[] o=q.getOptions();
            
            for(int i=0;i<4;i++){
                choices[i].setText((char)('A'+i)+". "+o[i]);
                choices[i].setSelected(answers[current]==i);
            }
            
            previous.setEnabled(current>0);
            next.setEnabled(current<questions.size()-1);
    }
 
    private void save(){answers[current]=-1;
        
        for(int i=0;i<4;i++)
            if(choices[i].isSelected()){
                answers[current]=i;break;}
    }
    private void move(int d){
        save();
        int n=current+d;
        if(n>=0&&n<questions.size()){
            current=n;showQuestion();
        }
    }
    private void submitExam(boolean expired){
        if(submitted)return;
        save();
        if(!expired&&JOptionPane.showConfirmDialog(this,"Submit "+examTitle+"?","Confirm",JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION)
            return;
        submitted=true;
        timer.stop();
        int score=service.calculateScore(questions,answers);
        boolean saved=true;
        String error=null;
        try{
            new ResultDAO().saveResult(student.id,examId,score,questions.size());
        }
        catch(SQLException e){
            saved=false;
            error=e.getMessage();
        }
        new ResultFrame(student.fullName,examTitle,score,questions.size(),saved,error).setVisible(true);
        dispose();
    }
}
