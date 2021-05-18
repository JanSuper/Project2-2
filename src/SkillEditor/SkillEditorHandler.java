package SkillEditor;

import FileParser.FileParser;
import Interface.Screens.MainScreen;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SkillEditorHandler {

    private List<List<String>> allSkills;
    private FileParser fileParser;

    public SkillEditorHandler(){
        fileParser = new FileParser();
        allSkills = fileParser.getAllSkills();
    }

    public String allOperations(){
        String r = "";
        for (List<String> operation:allSkills) {
            r+=("Main skill: " + operation.get(0) +", description the operation: " + operation.get(2) +  ", required nbr of variables: " + operation.get(3)+", corresponding task number: " + operation.get(1) + "\n");
        }
        return r;
    }

    public List<String> getMainSkills(){
        List<String> mainSkills = new ArrayList<>();
        for (List<String> row:allSkills) {
            if(!mainSkills.contains(row.get(0))){
                mainSkills.add(row.get(0));
            }
        }
        return mainSkills;
    }
    public List<String> getTasks(String skill){
        List<String> allTasks = new ArrayList<>();
        for (List<String> row:allSkills) {
            if(row.get(0).equals(skill)){
                allTasks.add(row.get(2));
            }
        }
        return allTasks;
    }

    public String handleAddSkill(String question, String answer){
        String response = "";
        if (question.isEmpty()||question.isBlank()) {
            response = ("Question : \"" + question + "\" is not under the correct form.");
        } else {
            int result = 0;
            result = addSkill(question,answer);
            if (result == 1) {
                response = "Question : \"" + question+ "\" was successfully added to the database.";
            } else if(result == -1) {
                response = "Question : \"" + question + "\" could not be added to the database";
            }else if(result==-2){
                response = "Question : \"" + question +  "\" does not contain the required number of variables";
            }
        }
        return response;
    }

    public int addSkill(String question, String answer){
        int success = -1;
        try{
            BufferedWriter newData = new BufferedWriter(new FileWriter(new File("src\\DataBase\\textData.txt"), true));
            success = 1;
            newData.append("U " + question + System.lineSeparator());
            newData.append("B " + answer + System.lineSeparator());
            newData.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return success;
    }

    public String skillToDisplay(String question,String skill,String task){
        String displayNbr = "-1";
        for (List<String> row:allSkills) {
            if(row.get(0).equals(skill)&&row.get(2).equals(task)){
                if(containsSameNbrOfVariables(question,Integer.valueOf(row.get(3)))){
                    displayNbr = row.get(1);
                }
            }
        }
        return displayNbr;
    }

    /**
     * @param s message from the skill database
     * @return true if s contains the same nbr of variables than the message stored in the current node
     */
    public boolean containsSameNbrOfVariables(String s, int nbrOfVar){
        int nbrOfRandomWords = 0;
        for (int i = 0; i < s.length(); i++) {
            if(s.charAt(i)=='<'){
                nbrOfRandomWords++;
            }
        }
        if(nbrOfRandomWords==nbrOfVar){
            return true;
        }
        return false;
    }
}