package CFGrammar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
                //System.out.println("JSONObject");

            }

            else if (grammar.get(key) instanceof JSONArray){
                //System.out.println("JSONArray");
                processArray((JSONArray) grammar.get(key), lefthand);

            }
        }
        System.out.println("number of rules: " + rules.size());
        //System.out.println(rules);

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
                rules.add(lhs+":"+array.get(i).toString());
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
                rules.add(key + ":" + object.get(key).toString());
            }
        }

    }


    /**
     * Should be able to add a rule to the json file in the right place
     * @param rule
     */
    public void addRules(String rule, boolean terminal) throws IOException {
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

        String[] rule_split = rule.split(":");
        String lhs = rule_split[0];
        String righthand = rule_split[1];
        String[] rhs;
        if(righthand.contains(","))
        {
            rhs = righthand.split(",");
        }
        else {
            rhs = new String[]{righthand};
        }
        if(terminal){
            if(grammar.containsKey("terminals")){
                if(grammar.get("terminals") instanceof JSONObject){
                    JSONObject terminals = (JSONObject) grammar.get("terminals");
                    if(terminals.containsKey(lhs)){
                        if(terminals.get(lhs) instanceof JSONArray){
                            JSONArray values = (JSONArray) terminals.get(lhs);
                            for(int k = 0; k<rhs.length;k++)
                            {
                                values.add(rhs[k]);
                            }
                            terminals.put(lhs,values);
                        }
                    }
                    else {
                        JSONArray values = new JSONArray();
                        for(int k = 0; k<rhs.length;k++)
                        {
                            values.add(rhs[k]);
                        }
                        terminals.put(lhs,values);
                    }
                }
            }
        }
        else if (grammar.containsKey(lhs)){
            if(grammar.get(lhs) instanceof JSONArray){
                JSONArray values = (JSONArray) grammar.get(lhs);
                JSONArray value = new JSONArray();
                for(int k = 0; k<rhs.length;k++)
                {
                    value.add(rhs[k]);
                }
                values.add(value);
                grammar.put(lhs,values);
            }
        }
        else{
            JSONArray values = new JSONArray();
            JSONArray value = new JSONArray();
            for(int k = 0; k<rhs.length;k++)
            {
                value.add(rhs[k]);
            }
            values.add(value);
            grammar.put(lhs,values);
        }
        FileWriter writer = new FileWriter("src\\CFGrammar\\grammar.json");
        writer.write(grammar.toJSONString());
        writer.close();
    }

    /**
     * Should remove the specific rule from the json file
     * @param rule
     */
    public void removeRule(String rule, boolean terminal) throws IOException {
        FileReader reader = null;
        try {
            reader = new FileReader("src\\CFGrammar\\grammar.json");
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
        String[] rule_split = rule.split(":");
        String lhs = rule_split[0];
        String righthand = rule_split[1];
        String[] rhs;
        if(righthand.contains(","))
        {
            rhs = righthand.split(",");
        }
        else {
            rhs = new String[]{righthand};
        }
        if(terminal){
            if(grammar.containsKey("terminals")){
                if(grammar.get("terminals") instanceof JSONObject) {
                    JSONObject terminals = (JSONObject) grammar.get("terminals");
                    if (terminals.containsKey(lhs)) {
                        boolean remove = false;
                        if (terminals.get(lhs) instanceof JSONArray) {
                            JSONArray values = (JSONArray) terminals.get(lhs);
                            for (int k = 0; k < values.size(); k++) {
                                for(int j = 0; j < rhs.length; j++){
                                    if(values.contains(rhs[j])) {
                                        remove = true;
                                    }
                                }
                                if(remove) {
                                    for(int c = 0; c < rhs.length; c++)
                                    {
                                        if(values.size()>1) {
                                            values.remove(rhs[c]);
                                            terminals.put(lhs, values);
                                        }
                                        else{
                                            terminals.remove(lhs);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        else if (grammar.containsKey(lhs)) {
            boolean remove = false;
            if (grammar.get(lhs) instanceof JSONArray) {
                JSONArray values = (JSONArray) grammar.get(lhs);
                for (int k = 0; k < values.size(); k++) {
                    if(values.get(k) instanceof JSONArray){
                        JSONArray singleton = (JSONArray) values.get(k);
                        for(int l = 0; l < rhs.length; l++)
                        {
                            if(singleton.contains(rhs[l])){
                                remove = true;
                            }
                        }
                        if(remove){
                            for(int c = 0; c < rhs.length; c++)
                            {
                                if(values.size()>1) {
                                    values.remove(singleton);
                                    grammar.put(lhs, values);
                                }
                                else{
                                    grammar.remove(lhs);
                                }
                            }
                        }
                    }
                }
            }
        }
        FileWriter writer = new FileWriter("src\\CFGrammar\\grammar.json");
        writer.write(grammar.toJSONString());
        writer.close();
    }

    public ArrayList<String> getRules() {
        return rules;
    }
}
