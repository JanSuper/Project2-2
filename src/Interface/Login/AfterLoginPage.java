package com.mygdx.game.Logic.WebSiteSketch;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AfterLoginPage implements ActionListener {

    public static JFrame frame;
    @Override
    public void actionPerformed(ActionEvent e) {
        boolean login = false;//boolean to know if the username and login are right
        LoginPage.counter++;
        String username = LoginPage.usernameTxt.getText();
        String password = LoginPage.passwordTxt.getText();
        //for every lines of the file, if the username  is on the same line than the password , it's correct
        for(int i=0;!login&&i<LoginPage.dataSet.length;i++){
            if (username.equals(LoginPage.dataSet[i][0])&&password.equals(LoginPage.dataSet[i][1])){
                login=true;
            }
        }
        if(login) {

            //new frame if the user and password are right
            LoginPage.textArea.setText("Right username and password");
            LoginPage.frame.setVisible(false);
            frame = new JFrame("Account");
            frame.setSize(350, 300);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            JPanel panel2 = new JPanel();
            JButton addOffer = new JButton("Add offer");
            panel2.add(addOffer);


            frame.add(panel2);
            frame.setVisible(true);

            ActionListener listener = new InsertNewOffer();
            addOffer.addActionListener(listener);

        }else if(LoginPage.counter ==1 ) {
            LoginPage.textArea.setText("Try again, 2 attempts left");
        }else if(LoginPage.counter ==2 ) {
            LoginPage.textArea.setText("Try again, 1 attempts left");
        }else if(LoginPage.counter >=3 ) {
            //new frame if all the attempts have been used
            LoginPage.frame.setVisible(false);
            JFrame error = new JFrame();
            error.setSize(230,75);
            error.setTitle("Error");
            error.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JPanel errorPanel = new JPanel();
            JTextArea errorText = new JTextArea(1, 5);
            errorText.setText("Sorry, you have used your 3 attempts");
            errorPanel.add(errorText);
            error.add(errorPanel);
            error.setVisible(true);
        }


    }
}
