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
    private List<String> assistantMessage;

    public Assistant(MainScreen pMainScreen, String pUser_name, List pAssistantMessage)
    {
        mainScreen = pMainScreen;
        user_name = pUser_name;
        messages = new ArrayList<>();
        assistantMessage = pAssistantMessage;
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

        if(assistantMessage.get(assistantMessage.size()-1).startsWith("To add a new skill to the assistant you have to follow these rules:"))
        {
            if(addNewSkill(uMessage) == 1)
            {
                return "The new skill was successfully added to the database.";
            }
            else
            {
                return "Sorry something went wrong, the new skill could not be added to the database";
            }
        }
        else if(res.isEmpty())
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
        else if(skill_num == 11)
        {
            final_answer = "Your lectures this week are:" + System.lineSeparator();
            final_answer = final_answer + new Skill_Schedule().getThisWeek();
        }
        else if(skill_num == 12)
        {
            ArrayList<String> this_month = new Skill_Schedule().getThisMonth();
            final_answer = "Your lectures this month are:" + System.lineSeparator();

            for(int i = 0; i < this_month.size(); i++)
            {
                final_answer = final_answer + System.lineSeparator() + System.lineSeparator() + this_month.get(i);
            }
        }
        else if(skill_num == 20)
        {
            mainScreen.setClockAppDisplay();
        }
        else if(skill_num == 30)
        {
            final_answer = "To add a new skill to the assistant you have to follow these rules:" + System.lineSeparator() +
                           "1. Write down the question(s) you will ask to the assistant. If there is more than one question (for the same answer) make sure to separate them with a comma , " + System.lineSeparator() +
                           "2. After the question(s) add a semicolon ; " + System.lineSeparator() +
                           "3. Write down the answer(s) you want from the assistant. If there is more than one answer (for the same question) make sure to separate them with a comma , " + System.lineSeparator() +
                           "4. Send everything into one message.";
        }
        return final_answer;
    }

    public int addNewSkill(String uMessage)
    {
        int success = -1;
        String[] split_uMessage = uMessage.split(";");
        if(split_uMessage.length > 2 || split_uMessage.length < 2)
        {
            success = 0;
        }
        else
        {
            String[] uQuestions = split_uMessage[0].split(",");
            String[] bAnswers = split_uMessage[1].split(",");

            try{BufferedWriter newData = new BufferedWriter(new FileWriter(dataBase, true));
                System.out.println("Here now");
                for(int j = 0; j <= uQuestions.length-1; j++)
                {
                    uQuestions[j] = removePunctuation(uQuestions[j]);
                    newData.append("U " + uQuestions[j] + System.lineSeparator());
                }
                for(int y = 0; y <= bAnswers.length-1; y++)
                {
                    bAnswers[y] = removePunctuation(bAnswers[y]);
                    newData.append("B " + bAnswers[y] + System.lineSeparator());
                }
                success = 1;
                newData.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    public String removePunctuation(String uMessage)
    {
        String clean_uMessage;
        String temp = uMessage.replaceAll("\\p{Punct}","");
        clean_uMessage = temp.trim().replaceAll(" +", " ");
        return clean_uMessage;
    }

    public void setAssistantMessage(List pAssistantMessage)
    {
        assistantMessage = pAssistantMessage;
    }
}
