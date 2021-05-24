package CFGrammar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;


public class JsonReader {
    private ArrayList<Rule> rules = new ArrayList<>();


    /**
     * Should do the same as getAllRules from Main_CFG but with the json file
     */

    public ArrayList getAllRules(String fileName) {
        FileReader reader = null;
        try {
            reader = new FileReader("..\\"+fileName);
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
        Iterator<String> keys = grammar.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            boolean found = false;
            for (int f = 0; f < rules.size(); f++) {
                Rule temp = rules.get(f);
                if (temp.getL_side().equals(key)) {
                    found = true;
                }
                String lefthand = key;
                String tmprhs = (String) grammar.get(key);
                String[] righthandside =  tmprhs.split(", ");
                ArrayList<String> righthand = new ArrayList<>();
                Collections.addAll(righthand,righthandside);
                if (!found) {
                    if (righthand.size() == 1) {
                        Rule one = new Rule(lefthand, righthand.get(0));
                        rules.add(one);
                    } else {
                        Rule two = new Rule(lefthand, righthand.get(0), righthand.get(1));
                        rules.add(two);
                    }
                } else {
                    if (righthand.size() == 1) {
                        temp.oneRule(righthand.get(0));
                    } else {
                        temp.multipleRule(righthand.get(0), righthand.get(1));
                    }
                }
            }
        }
        System.out.println("number of rules: " + rules.size());
        return rules;
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
            rules.add(rule);
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
}
