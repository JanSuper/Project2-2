package com.mygdx.game.Logic.WebSiteSketch;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class LoginPage implements ActionListener {
    public static int counter = 0;
    public static JFrame frame;

    public static JTextField usernameTxt;
    public static JTextField passwordTxt;

    public static String [][] dataSet;

    public static JTextArea textArea;

    @Override
    public void actionPerformed(ActionEvent e) {
        MainPage.frame.setVisible(false);

        frame = new JFrame("Login");
        frame.setSize(500,100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel panel1 = new JPanel();

        JLabel usernameLabel = new JLabel("Username :");
        usernameTxt = new JTextField(10);

        JLabel passwordLabel = new JLabel("Password :");
        passwordTxt = new JTextField(10);

        JButton loginButton = new JButton("Login");

        textArea = new JTextArea(1, 5);
        textArea.setText("You have 3 attemps");

        panel1.add(usernameLabel);
        panel1.add(usernameTxt);
        panel1.add(passwordLabel);
        panel1.add(passwordTxt);

        panel1.add(loginButton);

        panel1.add(textArea);

        frame.add(panel1);

        frame.setVisible(true);

        //create 2 arrays, one to store the data of the user and the other to split the data between password and username
        String [] splitData;
        String [][] data = new String [0][];

        // Begin a new file reader object directed at the text file we want to read (input)
        File file = new File("Data\\users.txt");
        // We want to cast out file reader to a buffered reader! (for reasons which will be clear next lecture).
        BufferedReader br;
        {
            // If the file does not exist, we will get an error, so try catch to make java happy
            try {
                // create buffered reader
                br = new BufferedReader(new FileReader(file));
                String st = "";

                // while another line exists in our text file, we read it!
                while ((st = br.readLine()) != null) {
                    // instead of printing them, here we can also store the users in an array
                    splitData = st.split(" ",2);

                    //split the array bewteen usernames and password
                    if(splitData.length == 2) {
                        String[][] res = new String[data.length+1][splitData.length];
                        for(int i=0;i<data.length;i++){
                            for(int j=0;j<res[0].length;j++){
                                res[i][j]=data[i][j];
                            }
                        }
                        res[data.length][0]=splitData[0];
                        res[data.length][1]=splitData[1];

                        data = res;
                    }
                }
                // catch exceptions if the files are not found
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        //the array we use in the actionlistener need to be final, or we can't use it
        dataSet = data;

        ActionListener listener = new AfterLoginPage();
        loginButton.addActionListener(listener);
    }
}
