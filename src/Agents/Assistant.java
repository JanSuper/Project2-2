package Agents;

import DataBase.Data;
import Interface.Chat.MessageBubble;
import Interface.Display.CalendarDisplay2;
import Interface.Display.MediaPlayerDisplay;
import Interface.Screens.MainScreen;
import Skills.Schedule.Skill_Schedule;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Assistant {
    private File dataBase = new File("src\\DataBase\\textData.txt");
    private Random random = new Random();
    private MainScreen mainScreen;
    private String user_name;
    private List<String> assistantMessage;
    private ArrayList SkillKeys;
    private Properties keySet;
    private String randomWord;
    private String response;
    private int max_Distance = 2;
    private String originalMessage;
    private String actual_lastWord;
    private boolean firstLoop;
    private String cleanMessageWithNoPonct;

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
        assistantMessage = pAssistantMessage;
        randomWord = "";
        response = "";
    }

    public String getResponse(String uMessage) throws Exception
    {
        this.firstLoop = true;
        this.originalMessage = uMessage;
        this.actual_lastWord = uMessage.substring(uMessage.lastIndexOf(" ")+1);
        if(actual_lastWord.endsWith("?")) {actual_lastWord = actual_lastWord.replaceAll("[?]", ""); }
        else if((actual_lastWord.endsWith("."))) { actual_lastWord = actual_lastWord.substring(0,actual_lastWord.length()-1);}

        String cleanMessage = removePunctuation(uMessage);
        this.cleanMessageWithNoPonct = cleanMessage;
        max_Distance = Math.max(2, (int)(cleanMessage.length()*0.15));
        randomWord = "";
        int nbrOfTrail = 0;
        String messageWithHole = cleanMessage;
        if(getInfo_withLevenshtein(cleanMessage))
        {
            return response;
        }
        else
        {
            while(!getInfo(messageWithHole)){
                firstLoop = false;
                messageWithHole = removeRandomWord(cleanMessage);
                if(messageWithHole.isEmpty()||nbrOfTrail>=1000||messageWithHole.length()==0){
                    response = "I could not understand your demand...";
                    break;
                }
                nbrOfTrail++;
            }
            return response;
        }
    }

    public String removeRandomWord(String message){
        String [] arr = message.split(" ");
        int randomNbr = new Random().nextInt(arr.length);
        this.randomWord = arr[randomNbr];
        System.out.println("random word : " + this.randomWord);
        if(randomNbr!=arr.length-1){
            randomWord = addCharToString(randomWord,' ',randomWord.length());
        }
        String newMessage = "";
        if(isValidURL(randomWord)){
            System.out.println("random word is a url");
            newMessage = removeUrl(message);
        }else{
            newMessage = message.replaceAll(randomWord, "");
        }
        System.out.println("message without the random word : " + newMessage);
        return newMessage;
    }

    public static boolean isValidURL(String urlString)
    {
        try
        {
            URL url = new URL(urlString);
            url.toURI();
            return true;
        } catch (Exception exception)
        {
            return false;
        }
    }

    private String removeUrl(String commentstr)
    {
        // rid of ? and & in urls since replaceAll can't deal with them
        String commentstr1 = commentstr.replaceAll("\\?", "").replaceAll("\\&", "");

        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(commentstr);
        int i = 0;
        while (m.find()) {
            commentstr = commentstr1.replaceAll(m.group(i).replaceAll("\\?", "").replaceAll("\\&", ""),"").trim();
            i++;
        }
        return commentstr;
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
                    if(s.contains(clean_uMessage.substring(2)))
                    {
                        String r = "";
                        while ((r = data.readLine())!=null && (r.startsWith("B")))
                        {
                            if(firstLoop){
                                res.add(r.substring(2));
                            }else{
                                if(!Data.getVariables().contains(r)){
                                    res.add(r.substring(2));
                                }
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
            if(clean_uMessage.toLowerCase().equals("cancel") || clean_uMessage.toLowerCase().equals("cancel skill editor"))
            {
                response = "Canceled the skill editor, you can now type in your request.";
            }
            else
            {
                response = handleNewSkill(clean_uMessage);
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

    /**
     * Tries to find the nearest possible answer in a given range of errors max_Distance
     * @param clean_uMessage the user message without punctuation
     */
    public boolean getInfo_withLevenshtein(String clean_uMessage) throws Exception{
        ArrayList<Answers> res = new ArrayList<>();
        ArrayList<Answers> best_score_res = new ArrayList<>();
        int score = -1;
        int best_score = Integer.MAX_VALUE;

        try{
            BufferedReader data = new BufferedReader(new FileReader(dataBase));

            String s;
            while ((s = data.readLine()) != null)
            {
                score = -1;
                if(s.startsWith("U"))
                {

                    score = LevenshteinDistance(clean_uMessage.toLowerCase(), s.substring(2).toLowerCase(), max_Distance);

                    if(score != -1)
                    {
                        if(score < best_score)
                        {
                            best_score = score;
                        }

                        String r;
;                        while ((r = data.readLine())!=null && (r.startsWith("B")))
                        {
                            if(firstLoop){
                                res.add(new Answers(score,r.substring(2)));
                            }else{
                                if(!Data.getVariables().contains(r)){
                                    res.add(new Answers(score,r.substring(2)));
                                }
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
            if(clean_uMessage.toLowerCase().equals("cancel") || clean_uMessage.toLowerCase().equals("cancel skill editor"))
            {
                response = "Canceled the skill editor, you can now type in your request.";
            }
            else
            {
                response = handleNewSkill(clean_uMessage);
            }
        }
        else if(res.isEmpty())
        {
            return false;
        }
        else
        {
            for(int i = 0; i < res.size(); i++)
            {
                if(res.get(i).getScore() == best_score)
                {
                    System.out.println("The score is : " + best_score + " | And the answer is: "+res.get(i).getAnswer());
                    best_score_res.add(res.get(i));
                }
            }

            if(isNumber(best_score_res.get(0).getAnswer()))
            {
                String skill_answer = getSkill(res.get(0).getAnswer(),clean_uMessage);
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
                int max = best_score_res.size();
                int n = random.nextInt(max);
                response =  best_score_res.get(n).getAnswer();
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

    /**
     * Every skill has it's specific number, finds the corresponding skill and does the appropriate
     * action to use the skill.
     * @param pNumb the number from the database corresponding to an action
     * @param message the user message without punctuation
     */
    public String getSkill(String pNumb, String message) throws Exception
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
            final_answer = "This is what I found for the weather in "+ city + ", " + country + ". " + mainScreen.weatherDisplay.currentDataString() + "If you want to change the location, type 'Change weather location to City,Country.' (e.g. Amsterdam,NL).";
        }
        else if(skill_num == 2){
            try {
                String city;
                String country = "";
                String[] split;
                if(originalMessage.contains("in")) {
                    split = originalMessage.split("in");
                }
                else { split = originalMessage.split("to"); }

                String temp = split[1];
                if (temp.contains(",")) {
                    String[] split2 = temp.split(",");
                    city = split2[0].replace(" ", "");
                    country = split2[1];
                }
                else {
                    city = temp.replace(" ", "");
                }

                mainScreen.setWeatherDisplay(city, country);
                final_answer = "This is what I found for the weather in "+ city + ". " + mainScreen.weatherDisplay.currentDataString() + "If you want to change the location, type 'Change weather location to City,Country.' (e.g. Amsterdam,NL).";
            }
            catch (Exception ex) {
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
                final_answer = "Here's the clock! To add a new clock use the options on the left screen or type 'Add a new clock for Continent/City'. If you want the available areas you can add, type 'What areas can I add to the clock'.";
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
            String messageT = "";
            for (String string : mainScreen.clockAppDisplay.clockVBox.listOfZoneIDs) {
                messageT += string + ", ";
            }
            final_answer = "The available timezones you can add to the clock are:  " + messageT;
        }
        else if(skill_num == 22){
            mainScreen.setClockAppDisplay("Alarm");
            mainScreen.clockAppDisplay.alarmVBox.addAlarm(randomWord,"no desc");
        }
        else if(skill_num == 23){
            String err = "Something went wrong! To set a new timer use the options on the left screen or type 'Set/Start a timer for hh:mm:ss'.";
            mainScreen.setClockAppDisplay("Timer");
            if (actual_lastWord.length() == 8) {
                String[] arr = new String[actual_lastWord.length()];
                for(int i = 0; i < actual_lastWord.length(); i++)
                {
                    arr[i] = String.valueOf(actual_lastWord.charAt(i));
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
        else if(skill_num == 24) {
            if(originalMessage.toLowerCase().contains("what time is it in")) {
                if(mainScreen.clockAppDisplay.clockVBox.tempTimeZoneIDs.contains(actual_lastWord)) {
                    final_answer = mainScreen.clockAppDisplay.clockVBox.getTimeFromZoneID(actual_lastWord) + " If you want to add a new clock use the options on the left screen or type 'Add a new clock for Continent/City'.";
                }
                else {
                    final_answer = "The area you requested the time for couldn't be found. If you want the available areas, type 'What are the time-zone IDs'.";
                }
            }
            else {
                if(mainScreen.clockAppDisplay.clockVBox.tempTimeZoneIDs.contains(actual_lastWord)) {
                    mainScreen.clockAppDisplay.clockVBox.addClock(actual_lastWord);
                    final_answer = "The clock was successfully added!";
                }
                else {
                    final_answer = "The area you requested couldn't be found. If you want the available areas, type 'What areas can I add to the clock' or use the options on the left screen.";
                }
                mainScreen.setClockAppDisplay("Clock");
            }
        }
        else if(skill_num == 30)
        {
            mainScreen.setSkillEditorAppDisplay("Add skill");
            final_answer = "To add a new skill to the assistant you have to follow these rules:" + System.lineSeparator() +
                           "1. Write down the question(s) you will ask to the assistant. If there is more than one question (for the same answer) make sure to separate them with a comma , " + System.lineSeparator() +
                           "2. After the question(s) add a semicolon ; " + System.lineSeparator() +
                           "3. Write down the answer(s) you want from the assistant. If there is more than one answer (for the same question) make sure to separate them with a comma , " + System.lineSeparator() +
                           "4. Send everything into one message." +System.lineSeparator() +
                           "If you don't want to add a skill write: Cancel";
        }else if(skill_num == 31){
            mainScreen.setSkillEditorAppDisplay("Edit skill");
        }
        else if(skill_num == 40){
            String searchURL = "https://www.google.com/search" + "?q=" + messageToUrl(randomWord);
            Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start chrome.exe " + searchURL});
        }
        else if(skill_num == 50){
            FileChooser fileChooser = new FileChooser();
            File selectedFile = fileChooser.showOpenDialog(mainScreen.stage);
            try {
                Media media = new Media (selectedFile.toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setAutoPlay(true);
                MediaPlayerDisplay mediaControl = new MediaPlayerDisplay(mediaPlayer);
                mainScreen.displayUrlMediaPlayer(mediaControl);
            } catch(NullPointerException e){
                mainScreen.chat.messages.add(new MessageBubble(mainScreen.chat,"No file chosen",0));
            } catch(MediaException e){
                mainScreen.chat.messages.add(new MessageBubble(mainScreen.chat,"Filetype not supported",0));
            }
        }
        else if(skill_num == 51){
            WebView webview = new WebView();
            webview.getEngine().load(
                    randomWord
            );
            Pane pane = new Pane();
            pane.getChildren().add(webview);
            mainScreen.displaySkill(pane);
        }
        else if(skill_num == 60){
            mainScreen.displaySkill(new CalendarDisplay2(mainScreen));
        }
        else if(skill_num == 70){
            mainScreen.setMapDisplay(cleanMessageWithNoPonct);
        }
        else if(skill_num == 71){
            mainScreen.setMapDisplay("");
        }
        else if(skill_num == 80){
            if(!randomWord.contains(" ")){
                if(!changePassword(randomWord)){
                    mainScreen.chat.messages.add(new MessageBubble(mainScreen.chat, "Couldn't change the password for some reasons",0));
                }
            }else{
                mainScreen.chat.messages.add(new MessageBubble(mainScreen.chat, "Please remove the space in the password",0));
            }
        }
        else if(skill_num == 90)
        {
            mainScreen.exitWindow();
        }
        return final_answer;
    }

    public boolean changePassword(String message){
        String[][]dataset = Data.getDataSet();
        for (int i = 0; i < dataset.length; i++) {
            for (int j = 0; j < dataset[i].length; j++) {
                if(dataset[i][j].equals(Data.getPassword())&&j == 1){
                    dataset[i][j] = message;
                    Data.setPassword(message);
                    rewriteUsers(dataset);
                    mainScreen.chat.messages.add(new MessageBubble(mainScreen.chat, "Your new password is " + message,0));
                    return true;
                }
            }
        }
        return false;
    }

    public void rewriteUsers(String[][]dataset){
        FileWriter writer;
        {
            try {
                writer = new FileWriter(Data.getUsersFile());
                PrintWriter out = new PrintWriter(writer);
                for (int i = 0; i < dataset.length; i++) {
                    for (int j = 0; j < dataset[i].length; j++) {
                        if(j==1){
                            out.print(dataset[i][j]);
                        }else{
                            out.print(dataset[i][j] + " ");
                        }
                    }
                    out.println();
                }

                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setRandomWord(String message){
        String newLastWord = "";
        int counter1 = message.length()-1;
        while(message.charAt(counter1)!=' '){
            newLastWord+=message.charAt(counter1--);
        }
        newLastWord = new StringBuilder(newLastWord).reverse().toString() + " ";
        randomWord = newLastWord + randomWord;
        System.out.println("last word : " + randomWord);
    }

    /**
     * Adds a new skill in the database, the user has to follow a specific structure to
     * add question(s) and answer(s) to the database.
     * @param uMessage the message from the user containing the new skill
     */
    public int addNewSkill(String uMessage) throws IOException {
        int success = -1;
        String[] split_uMessage = uMessage.split(";");

        //Ajouter des if pour les commas
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
                String all_question = "U ";
                for(int j = 0; j <= uQuestions.length-1; j++)
                {
                    uQuestions[j] = removePunctuation(uQuestions[j]);
                    all_question = all_question + uQuestions[j] + ", ";
                }
                newData.append(all_question + System.lineSeparator());
                for(int y = 0; y <= bAnswers.length-1; y++)
                {
                    bAnswers[y] = removePunctuation(bAnswers[y]);
                    newData.append("B " + bAnswers[y] + System.lineSeparator());
                    System.out.println("Written from here (ASSISTANT)");
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
        String clean_uMessage = "";
        String temp = uMessage.replaceAll("\\p{Punct}&&[^/]]","");
        clean_uMessage = temp.trim().replaceAll(" +", " ");
        if(clean_uMessage.endsWith("?")) {clean_uMessage = clean_uMessage.replaceAll("[?]", ""); }
        else if((clean_uMessage.endsWith("."))) { clean_uMessage = clean_uMessage.substring(0,clean_uMessage.length()-1);}
        return clean_uMessage;
    }

    public void setAssistantMessage(List pAssistantMessage)
    {
        assistantMessage = pAssistantMessage;
    }

    /**
     * Compares the difference between two Strings using the Levenshtein algorithm.
     * @param uMessage the user message without punctuation
     * @param dataBase_message the message in the database
     * @param threshold the maximum accepted distance between the Strings
     * @return the score between -1 and threshold
     */
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

    public class Answers{
        private String answer;
        private int score = -1;

        /**
         * Used by the Levenshtein distance to rank the possible answers with their
         * corresponding score in an ArrayList
         * @param pScore the LevenshteinDistance
         * @param pAnswer the answer from the database
         */
        public Answers(int pScore, String pAnswer)
        {
            this.answer = pAnswer;
            this.score = pScore;
        }

        public int getScore()
        {
            return score;
        }

        public String getAnswer()
        {
            return answer;
        }
    }

}
