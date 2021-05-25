package CFGrammar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;


public class JsonReader {
    private ArrayList<String> rules = new ArrayList<>();

    /**
     * Gets all rules from JSON file and saves them as Strings
     * @return ArrayList filled with String Rules
     */
    public ArrayList<String> getAllRules() {
        /*
        Open file
         */
        FileReader reader = null;
        try {
            reader = new FileReader("src\\CFGrammar\\grammar.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JSONParser parser = new JSONParser();
        JSONObject grammar = null;
        /*
        Parse the JSON file if found
         */
        try {
            grammar = (JSONObject) parser.parse(reader);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        /*
        Iterate over all key = Lefthand sides of rules.
        Get Righthand and concat to string rule.
         */

        Iterator<String> keys = grammar.keySet().iterator();

        while (keys.hasNext()) {
            String key = keys.next();
            String lefthand = key;
            if(grammar.get(key) instanceof JSONObject){
                processObject((JSONObject) grammar.get(key), lefthand);
                System.out.println("JSONObject");

            }

            else if (grammar.get(key) instanceof JSONArray){
                System.out.println("JSONArray");
                processArray((JSONArray) grammar.get(key), lefthand);

            }
        }
        System.out.println("number of rules: " + rules.size());
        System.out.println(rules);

        return rules;
    }
    public void processArray(JSONArray array, String lhs){
        for(int i = 0; i< array.size();i++){
            if(array.get(i) instanceof JSONArray){
                processArray((JSONArray)array.get(i), lhs);
            }
            else if(array.get(i) instanceof JSONObject){
                processObject((JSONObject) array.get(i),lhs);
            }
            else{
                rules.add(lhs+" : "+array.get(i).toString());
            }
        }
    }
    public void processObject(JSONObject object, String lhs){
        Iterator keys = object.keySet().iterator();
        while(keys.hasNext()){
            String key = (String) keys.next();
            if(object.get(key) instanceof JSONArray){
                processArray((JSONArray) object.get(key), key);
            }
            else if(object.get(key) instanceof JSONObject){
                processObject((JSONObject) object.get(key), key);
            }
            else{
                rules.add(key + " : " + object.get(key).toString());
            }
        }

    }


    /**
     * Should be able to add a rule to the json file in the right place
     * @param rule
     */
    public void addRules(Rule rule) {
        FileReader reader = null;
        try {
            reader = new FileReader("..\\grammar.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JSONParser parser = new JSONParser();
        JSONObject grammar = null;
        try {
            grammar = (JSONObject) parser.parse(reader);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(grammar.containsKey(rule.getL_side())){
            JSONArray ruleAddition = (JSONArray) grammar.get(rule.getL_side());
            ruleAddition.add(rule.getR_side());
            if(rule.getMultiple()){
                ruleAddition.add(rule.getR_side_2());
            }
        }
        else{
            grammar.put(rule.getL_side(), rule.getR_side());
            //rules.add(rule);
        }
    }

    /**
     * Should remove the specific rule from the json file
     * @param rule
     */
    public void removeRule(Rule rule) {
        FileReader reader = null;
        try {
            reader = new FileReader("..\\grammar.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JSONParser parser = new JSONParser();
        JSONObject grammar = null;
        try {
            grammar = (JSONObject) parser.parse(reader);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(grammar.containsKey(rule.getL_side()))
        {
            grammar.remove(rule.getL_side());
        }
        else{
            System.out.println("Rule not in specified file");
        }
    }

    public ArrayList<String> getRules() {
        return rules;
    }
}
