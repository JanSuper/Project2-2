package CFGrammar;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;


public class JsonReader {
    private ArrayList<Rule> rules = new ArrayList<>();

    /**
     * Should do the same as getAllRules from Main_CFG but with the json file
     */
    public void getAllRules() throws FileNotFoundException {
        FileReader reader = new FileReader("..\\grammar.json");
        JSONParser parser = new JSONParser();
        try {
            JSONObject grammar = (JSONObject) parser.parse(reader);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JSONObject grammar = new JSONObject();
        Iterator<String> keys = grammar.keySet().iterator();
        while(keys.hasNext()){
            String key = keys.next();
            boolean found = false;
            for(int f=0;f <rules.size(); f++) {
                Rule temp = rules.get(f);
                if (temp.getL_side().equals(key)) {
                    found = true;
                }
                String lefthand = key;
                ArrayList<String> righthand = (ArrayList<String>) grammar.get(key);
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
        System.out.println("number of rules: "+rules.size());
    }

    /**
     * Should be able to add a rule to the json file in the right place
     * @param rule
     */
    public void addRules(String rule) {}

    /**
     * Should remove the specific rule from the json file
     * @param rule
     */
    public void removeRule(String rule) {}
}
