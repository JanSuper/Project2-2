package CFGrammar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

public class Main_CFG {

    /**
     * Use this main to test the CFG without running the whole project
     * @param args
     */
    public static void main(String[] args) {
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private ArrayList<HashSet<String>> rules;
    private int noOfRules;
    private int size;
    private boolean[] hasVisitedWords;
    private int[] indexes;

    private String user_message;
    private String agent_message;

    public Main_CFG(String user_message)
    {

    }

    public void addProductionRule(String rule)
    {
        //Replaces skill editor, add grammar rules to the json file
    }

    public void removeProductionRule(String rule)
    {
        //Removes an unwanted rule
    }

    public void addWords(String word_cat, String word)
    {
        //Adds a single word to the jason file in the right category "N","V"...
    }

    public void readfromJson()
    {

    }

    public Map<String,Object> getSentenceStructure(String sentence)
    {
        return null;
    }

}
