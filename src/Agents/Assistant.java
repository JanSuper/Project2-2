package Agents;

import DataBase.Data;
import Interface.Screens.MainScreen;
import Skills.Schedule.Skill_Schedule;

import java.awt.desktop.AboutEvent;
import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class Assistant {
    private List<String> messages;
    private File dataBase = new File("src\\DataBase\\textData.txt");
    private Random random = new Random();
    private MainScreen mainScreen;
    private String user_name;
    private List<String> assistantMessage;
    private ArrayList SkillKeys;
    private Properties keySet;
    private String lastWord;
    private String response;
    private final int max_Distance = 5;

    public void loadKeys() throws IOException {
        Properties keys = new Properties();
        String fileName = "keys.properties";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream != null) {
            keys.load(inputStream);
            keySet = keys;
            Set<Object> allKeys = keys.keySet();
            for(Object k:allKeys)
            {
                String key = (String) k;
                SkillKeys.add(k);
            }

        } else {
            throw new FileNotFoundException("property file '" + fileName + "' not found in the classpath");
        }

    }

    public Assistant(MainScreen pMainScreen, String pUser_name, List pAssistantMessage) throws IOException {
        mainScreen = pMainScreen;
        user_name = pUser_name;
        messages = new ArrayList<>();
        assistantMessage = pAssistantMessage;
        lastWord = "";
        response = "";
    }

    public String getResponse(String uMessage) throws Exception
    {
        lastWord = "";
        int nbrOfTrail = 0;
        //String clean_uMessage = removePunctuation(uMessage).toLowerCase();
        String clean_uMessage = uMessage.toLowerCase();
        while(!getInfo(clean_uMessage)){
            //System.out.println("Question not known");
            //setLastWord(clean_uMessage);
            //clean_uMessage = removeLastWord(clean_uMessage);
            clean_uMessage = removeRandomWord(uMessage);
            if(clean_uMessage.isEmpty()||nbrOfTrail>=1000||clean_uMessage.length()==0){
                /*
                String searchURL = "https://www.google.com/search" + "?q=" + messageToUrl(clean_uMessage);
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start chrome.exe " + searchURL});

                 */
                response = "I could not understand your demand...";
                break;
            }
            nbrOfTrail++;
        }
        return response;
    }

    public String removeLastWord(String message){
        var lastIndex = message.lastIndexOf(" ");
        return message.substring(0, lastIndex);
    }

    public String removeRandomWord(String message){
        String [] arr = message.split(" ");
        int randomNbr = new Random().nextInt(arr.length);
        System.out.println(randomNbr);
        String randomWord = arr[randomNbr];
        lastWord = randomWord;
        System.out.println("last word : " + lastWord);
        if(randomNbr!=arr.length-1){
            randomWord = addCharToString(randomWord,' ',randomWord.length());
        }
        String newMessage = message.replaceAll(randomWord, "");
        String newMessage1 = "";
        if(randomNbr==arr.length-1){
            for (int i = 0; i < newMessage.length()-1; i++) {
                newMessage1+=newMessage.charAt(i);
            }
            newMessage = newMessage1;
        }
        System.out.println(newMessage);
        return newMessage;
    }
    public String addCharToString(String str, char c, int pos) {
        StringBuilder stringBuilder = new StringBuilder(str);
        stringBuilder.insert(pos, c);
        return stringBuilder.toString();
    }


    public boolean getInfo(String clean_uMessage) throws Exception{
        //System.out.println(clean_uMessage);
        ArrayList<String> res = new ArrayList<>();
        int score = 0;

        try{
            BufferedReader data = new BufferedReader(new FileReader(dataBase));

            String s;
            while ((s = data.readLine()) != null)
            {
                if(s.startsWith("U"))
                {
                    if(s.toLowerCase().contains(clean_uMessage))
                    {
                        String r = "";
                        while ((r = data.readLine()).startsWith("B"))
                        {
                            if(!Data.getVariables().contains(r)){
                                res.add(r.substring(2));
                            }
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
            response = handleNewSkill(clean_uMessage);
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

    public String handleNewSkill(String clean_uMessage) throws IOException {
        int result = addNewSkill(clean_uMessage);
        if(result == 1)
        {
            response =  "The new skill was successfully added to the database.";
        }
        else if(result==-2){
            response = "Task already implemented.";
        }
        else
        {
            response =  "Sorry something went wrong, the new skill could not be added to the database";
        }
        return response;
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
            //TODO get user's city and country when creating a new account and use it here
            String city = "Maastricht";
            String country = "NL";
            mainScreen.setWeatherDisplay(city,country);
            final_answer = "This is what I found for the weather in "+ city + ", " + country + ". If you want to change the location type 'Change weather location to City, Country.' (e.g. Amsterdam, NL).";
        }
        else if(skill_num == 2){
            try {
                String city = "cityName";  //TODO being able to recognize if there is only a city name, a country name or both
                String country = lastWord;
                mainScreen.setWeatherDisplay(city, country);
                final_answer = "This is what I found for the weather in "+ city + ", " + country + ".";

            } catch (Exception ex) {
                final_answer = "Something went wrong! Please try again.";
            }
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
            if (message.toLowerCase().contains("timer")) {
                if (message.toLowerCase().contains("start") && !mainScreen.clockAppDisplay.timerVBox.timerTime.getText().equals("00 : 00 : 00") && mainScreen.clockAppDisplay.timerVBox.startPauseResume.getText().equals("Start")) {
                    mainScreen.clockAppDisplay.timerVBox.startTimer();
                    final_answer = "The timer started. Type 'Pause/Cancel timer' or use the options on the left screen.";
                }
                else if (message.toLowerCase().contains("pause") &&  mainScreen.clockAppDisplay.timerVBox.startPauseResume.getText().equals("Pause")) {
                    mainScreen.clockAppDisplay.timerVBox.pauseTimer();
                    final_answer = "The timer is paused. Type 'Resume/Cancel timer' or use the options on the left screen.";
                }
                else if (message.toLowerCase().contains("resume") &&  mainScreen.clockAppDisplay.timerVBox.startPauseResume.getText().equals("Resume")) {
                    mainScreen.clockAppDisplay.timerVBox.resumeTimer();
                    final_answer = "The timer is resumed. Type 'Pause/Cancel timer' or use the options on the left screen.";
                }
                else if (message.toLowerCase().contains("cancel") &&  !mainScreen.clockAppDisplay.timerVBox.cancel.isDisabled()) {
                    mainScreen.clockAppDisplay.timerVBox.cancelTimer();
                    final_answer = "The timer is canceled. To set a new timer use the options on the left screen or type 'Set/Start a timer for hh:mm:ss'.";
                }
                else {
                    final_answer = "Here's the timer! To set a new timer use the options on the left screen or type 'Set/Start a timer for hh:mm:ss'.";
                }
                mainScreen.setClockAppDisplay("Timer");
            }
            else if (message.toLowerCase().contains("clock") || message.toLowerCase().contains("time")) {
                mainScreen.setClockAppDisplay("Clock");
            }
            else if (message.toLowerCase().contains("stopwatch")) {
                if (message.toLowerCase().contains("pause") && mainScreen.clockAppDisplay.stopwatchVBox.startPause.getText().equals("Pause")) {
                    mainScreen.clockAppDisplay.stopwatchVBox.pauseStopwatch();
                    final_answer = "The stopwatch is paused! Type 'reset/start stopwatch' or use the buttons on the left screen.";
                }
                else if (message.toLowerCase().contains("lap") && mainScreen.clockAppDisplay.stopwatchVBox.lapReset.getText().equals("Lap") && !mainScreen.clockAppDisplay.stopwatchVBox.lapReset.isDisabled()) {
                    mainScreen.clockAppDisplay.stopwatchVBox.lapStopwatch();
                    final_answer = mainScreen.clockAppDisplay.stopwatchVBox.lap.getText();
                }
                else if (message.toLowerCase().contains("reset") && mainScreen.clockAppDisplay.stopwatchVBox.lapReset.getText().equals("Reset")) {
                    mainScreen.clockAppDisplay.stopwatchVBox.resetStopwatch();
                }
                else if ((message.toLowerCase().contains("set") && !message.toLowerCase().contains("reset")) || message.toLowerCase().contains("start")) {
                    mainScreen.clockAppDisplay.stopwatchVBox.startStopwatch();
                    final_answer = "The stopwatch has been started! Type 'lap/pause stopwatch' or use the buttons on the left screen.";
                }
                mainScreen.setClockAppDisplay("Stopwatch");
            }
            else {
                mainScreen.setClockAppDisplay("Alarm");
            }
        }
        else if(skill_num == 21)
        {
            System.out.println("Get time in " + lastWord);
            mainScreen.clockAppDisplay.clockVBox.setCountry(lastWord);
            mainScreen.setClockAppDisplay("Clock");
        }
        else if(skill_num == 22){
            mainScreen.setClockAppDisplay("Alarm");
            mainScreen.clockAppDisplay.alarmVBox.addAlarm(lastWord,"no desc");
        }
        else if(skill_num == 23){
            String err = "Something went wrong! To set a new timer use the options on the left screen or type 'Set/Start a timer for hh:mm:ss'.";
            mainScreen.setClockAppDisplay("Timer");
            if (lastWord.length() == 8) {
                String[] arr = new String[lastWord.length()];
                for(int i = 0; i < lastWord.length(); i++)
                {
                    arr[i] = String.valueOf(lastWord.charAt(i));
                }
                if ((arr[2].equals(":") || arr[2].equals(".")) && (arr[5].equals(":")|| arr[5].equals("."))) {
                    try {
                        mainScreen.clockAppDisplay.timerVBox.hoursTimer = Integer.parseInt(arr[0] + arr[1]);
                        mainScreen.clockAppDisplay.timerVBox.minutesTimer = Math.min(Integer.parseInt(arr[3] + arr[4]), 59); //max value for seconds and minutes is 59
                        mainScreen.clockAppDisplay.timerVBox.secondsTimer = Math.min(Integer.parseInt(arr[6] + arr[7]), 59);

                        mainScreen.clockAppDisplay.timerVBox.setTimerTime();
                        mainScreen.clockAppDisplay.timerVBox.startTimer();
                        final_answer = "A timer has been set for " + mainScreen.clockAppDisplay.timerVBox.timerTime.getText() + ". Type 'Pause/Cancel timer' or use the options on the left screen.";
                    }
                    catch (NumberFormatException e) {
                        final_answer = err;
                    }
                }
                else { final_answer = err; }
            }
            else { final_answer = err; }
        }
        else if(skill_num == 30)
        {
            mainScreen.setSkillEditorAppDisplay("Add skill");
            final_answer = "To add a new skill to the assistant you have to follow these rules:" + System.lineSeparator() +
                           "1. Write down the question(s) you will ask to the assistant. If there is more than one question (for the same answer) make sure to separate them with a comma , " + System.lineSeparator() +
                           "2. After the question(s) add a semicolon ; " + System.lineSeparator() +
                           "3. Write down the answer(s) you want from the assistant. If there is more than one answer (for the same question) make sure to separate them with a comma , " + System.lineSeparator() +
                           "4. Send everything into one message.";
        }else if(skill_num == 31){
            mainScreen.setSkillEditorAppDisplay("Edit skill");
        }
        else if(skill_num == 40){
            String searchURL = "https://www.google.com/search" + "?q=" + messageToUrl(lastWord);
            Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start chrome.exe " + searchURL});
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
        System.out.println("last word : " + lastWord);
    }

    public int addNewSkill(String uMessage) throws IOException {
        int success = -1;
        String[] split_uMessage = uMessage.split(";");
        String[] uQuestions = split_uMessage[0].split(",");
        String[] bAnswers = split_uMessage[1].split(",");
        if(split_uMessage.length > 2 || split_uMessage.length < 2)
        {
            success = 0;
        }else if(skillAlreadyIn(uQuestions)){
            success = -2;
        }
        else
        {

            try{BufferedWriter newData = new BufferedWriter(new FileWriter(dataBase, true));
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

    public boolean skillAlreadyIn(String[] uQuestion) throws IOException {
        String question = "";
        String actual = Files.readString(dataBase.toPath()).toLowerCase();
        for(int j = 0; j <= uQuestion.length-1; j++)
        {
            if(actual.contains(uQuestion[j])){
                return true;
            }
        }
        return false;
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

    public int LevenshteinDistance(String uMessage, String dataBase_message, int threshold)
    {
        int uM = uMessage.length();
        int dB = dataBase_message.length();
        int[] cost_previous = new int[uM + 1];
        int[] cost_distance = new int[uM + 1];
        int[] cost_memory;
        int limit = Math.min(uM,threshold);
        int score = -1;

        if(uM == 0 || dB == 0)
        {
            return score;
        }

        if(uM > dB)
        {
            String temp = uMessage;
            uMessage = dataBase_message;
            dataBase_message = temp;
            int temp2 = uM;
            uM = dB;
            dB = temp2;
        }

        for(int i = 0; i <= limit; i++)
        {
            cost_previous[i] = i;
        }
        Arrays.fill(cost_previous, limit, cost_previous.length, Integer.MAX_VALUE);
        Arrays.fill(cost_distance, Integer.MAX_VALUE);

        for(int i = 1; i <= dB; i++)
        {
            char database_char = dataBase_message.charAt(i-1);
            cost_distance[0] = i;

            int min = Math.max(1, i-threshold);
            int max = i > Integer.MAX_VALUE - threshold ? uM : Math.min(uM,threshold+i); //TODO Noo kucken

            if(min > max)
            {
                return -1;
            }

            if(min > 1)
            {
                cost_distance[min-1] = Integer.MAX_VALUE;
            }

            for(int j = min; j <= max; j++)
            {
                if(uMessage.charAt(j-1) == database_char)
                {
                    cost_distance[j] = cost_previous[j-1];
                }
                else
                {
                    cost_distance[j] = 1 + Math.min(Math.min(cost_distance[j-1], cost_previous[j]), cost_previous[j-1]);
                }
            }

            cost_memory = cost_previous;
            cost_previous = cost_distance;
            cost_distance = cost_memory;
        }

        if(cost_previous[uM] <= threshold)
        {
            score = cost_previous[uM];
        }
        return score;
    }

}
