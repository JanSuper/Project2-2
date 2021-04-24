package Agents;

import FileParser.FileParser;
import Interface.Screens.MainScreen;
import TextRecognition.TextRecognition;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class Assistant {
    private File dataBase;
    public MainScreen mainScreen;
    public FileParser fileParser;
    private String user_name;
    public List<String> assistantMessage;
    private ArrayList SkillKeys;
    private Properties keySet;
    private Stack<String> randomWords;
    private String response;

    public TextRecognition textRecognition;

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
        fileParser = new FileParser();
        user_name = pUser_name;
        assistantMessage = pAssistantMessage;
        response = "";
        randomWords = new Stack<>();
        if (System.getProperty("os.name").contains("Mac OS"))
        {
            dataBase = new File("src/DataBase/textData.txt");
        }
        else
        {
            dataBase = new File("src\\DataBase\\textData.txt");
        }

        textRecognition = new TextRecognition(this);
    }

    public String removeVariables(String s){
        String newS = "";
        //REMOVE VARIABLES <CITY>,<DAY>,... from the sentence starting with U
        String[] message = s.split(" ");
        for (int i = 0; i < message.length; i++) {
            if(!message[i].equals("<VARIABLE>")){
                if(i==message.length-1){
                    newS+=message[i];
                }else{
                    newS+=message[i] + " ";
                }
            }
        }
        return newS;
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

}
