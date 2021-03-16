package Agents;

import Interface.Screens.MainScreen;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Skills.Schedule.Skill_Schedule;

public class Assistant {
    private List<String> messages;
    private File dataBase = new File("src\\DataBase\\textData.txt");
    private Random random = new Random();
    private MainScreen mainScreen;
    private String user_name;
    private List<String> assistantMessage;
    private String lastWord;
    private String response;

    public Assistant(MainScreen pMainScreen, String pUser_name, List pAssistantMessage)
    {
        mainScreen = pMainScreen;
        user_name = pUser_name;
        messages = new ArrayList<>();
        assistantMessage = pAssistantMessage;
        lastWord = "";
        response = "";
    }

    public String getResponse(String uMessage) throws Exception
    {
        String clean_uMessage = removePunctuation(uMessage).toLowerCase();
        while(!getInfo(clean_uMessage)){
            setLastWord(clean_uMessage);
            clean_uMessage = removeLastWord(clean_uMessage);
            if(clean_uMessage.isEmpty()){
                String searchURL = "https://www.google.com/search" + "?q=" + messageToUrl(clean_uMessage);
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start chrome.exe " + searchURL});
                response = "I could not understand your demand...";
                break;
            }
        }
        return response;
    }

    public String removeLastWord(String message){
        var lastIndex = message.lastIndexOf(" ");
        return message.substring(0, lastIndex);
    }

    public boolean getInfo(String clean_uMessage) throws Exception{
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
            if(addNewSkill(clean_uMessage) == 1)
            {
                response =  "The new skill was successfully added to the database.";
            }
            else
            {
                response =  "Sorry something went wrong, the new skill could not be added to the database";
            }
        }
        else if(res.isEmpty())
        {
            return false;
        }
        else
        {
            if(isNumber(res.get(0)))
            {
                String skill_answer = getSkill(res.get(0),clean_uMessage);
                if(skill_answer == null)
                {
                    response =  "This is what I found for your request.";
                }
                else
                {
                    response =  skill_answer;
                }
            }
            else
            {
                int max = res.size();
                int n = random.nextInt(max);
                response =  res.get(n);
            }
        }
        return true;
    }

    public String messageToUrl(String message){
        String url = "";
        for (int i = 0; i < message.length(); i++) {
            if(message.charAt(i) == ' '){
                url+='+';
            }else{
                url+=message.charAt(i);
            }
        }
        return url;
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

    public String getSkill(String pNumb,String message) throws Exception
    {
        //The specific Skills will be called here
        int skill_num = Integer.parseInt(pNumb);
        String final_answer = null;
        if(skill_num == 1)
        {
            mainScreen.setWeatherDisplay("Maastricht","NL");
        }
        else if(skill_num == 2){
            mainScreen.setWeatherDisplay(lastWord.substring(0, lastWord.indexOf(' ')),lastWord.substring(0, lastWord.indexOf(' ')));
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
        else if(skill_num == 21)
        {
            mainScreen.clockAppDisplay.clockVBox.setCountry(lastWord);
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

    public void setLastWord(String message){
        String newLastWord = "";
        int counter1 = message.length()-1;
        while(message.charAt(counter1)!=' '){
            newLastWord+=message.charAt(counter1--);
        }
        newLastWord = new StringBuilder(newLastWord).reverse().toString() + " ";
        lastWord = newLastWord + lastWord;
        System.out.println(lastWord);
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
