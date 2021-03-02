package Agents;

import Interface.Screens.MainScreen;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import Agents.Assistant;
import DataBase.Data;
import Interface.Display.MediaPlayerDisplay;
import Skills.Schedule.Skill_Schedule;
import Skills.SkillEditor;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;

public class Assistant {
    private List<String> messages;
    private File dataBase = new File("src\\DataBase\\textData.txt");
    private Random random = new Random();
    private MainScreen mainScreen;
    private String user_name;

    public Assistant(MainScreen pMainScreen, String pUser_name)
    {
        mainScreen = pMainScreen;
        user_name = pUser_name;
        messages = new ArrayList<>();
    }

    public String getResponse(String uMessage) throws Exception
    {
        String clean_uMessage = removePunctuation(uMessage).toLowerCase();
        ArrayList<String> res = new ArrayList<>();
        try{
            BufferedReader data = new BufferedReader(new FileReader(dataBase));

            String s;
            while ((s = data.readLine()) != null)
            {
                if(s.startsWith("U"))
                {
                    if(s.toLowerCase().contains(clean_uMessage))
                    {
                        String r;
                        while ((r = data.readLine()).startsWith("B"))
                        {
                            res.add(r.substring(2));
                        }
                    }
                }
            }
            data.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(res.isEmpty())
        {
            return "I could not understand your demand...";
        }
        else
        {
            if(isNumber(res.get(0)))
            {
                String skill_answer = getSkill(res.get(0));
                if(skill_answer == null)
                {
                    return "This is what I found for your request.";
                }
                else
                {
                    return skill_answer;
                }
            }
            else
            {
                int max = res.size();
                int n = random.nextInt(max);
                return res.get(n);
            }
        }
    }

    public boolean isNumber(String res)
    {
        try{
            int d = Integer.parseInt(res);
        } catch (NumberFormatException e)
        {
            return false;
        }
        return true;
    }

    public String getSkill(String pNumb) throws Exception
    {
        //The specific Skills will be called here
        int skill_num = Integer.parseInt(pNumb);
        String final_answer = null;
        if(skill_num == 1)
        {
            mainScreen.setWeatherDisplay("Maastricht","NL");
        }
        else if(skill_num == 10)
        {
            final_answer = "Your next lecture is:" + System.lineSeparator();
            final_answer = final_answer + new Skill_Schedule().getNextCourse();
        }
        else if(skill_num == 20)
        {
            mainScreen.setClockAppDisplay();
        }
        return final_answer;
    }

    public void addNewSkill(String[] pBot, String[] pUser)
    {

    }

    public String removePunctuation(String uMessage)
    {
        String clean_uMessage;
        String temp = uMessage.replaceAll("\\p{Punct}","");
        clean_uMessage = temp.trim().replaceAll(" +", " ");
        return clean_uMessage;
    }
}
