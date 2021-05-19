package CFGrammar;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class JsonReader {

    /**
     * Should do the same as getAllRules from Main_CFG but with the json file
     */
    public ArrayList getAllRules(String fileName) throws FileNotFoundException, ParseException {

        ArrayList rules = null;

        File json_rules = new File(fileName);
        Scanner sc= new Scanner(json_rules);
        String json_string = "";
        while(sc.hasNext())
        {
            json_string+=sc.nextLine();
        }
        sc.close();

        // Parse the datas into json
        JSONParser parse = new JSONParser();
        JSONObject json_obj = (JSONObject)parse.parse(json_string);

        /*
        while ((startLine = br.readLine()) != null)   {
            String[] result = startLine.trim().split("\\s");
            for (int x=0; x<result.length; x++){
            }
            String lefthand="S";
            if(result.length > 0){
                lefthand= result[0];
            }
            if(result.length > 1){
                double aDouble = Double.parseDouble(result[result.length-1]);
                ArrayList<String> righthand = new ArrayList<String>();
                for (int x=2; x<result.length-2; x++){
                    righthand.add(result[x]);
                }

                boolean found=false;
                Rule temp=null;
                for(int f=0;f <rules.size(); f++){
                    temp=rules.get(f);
                    if(temp.getL_side().equals(lefthand)){
                        found=true;
                    }
                }
                if(!found){
                    if(righthand.size()==1){
                        Rule one=new Rule(lefthand, righthand.get(0));
                        rules.add(one);
                    }else{
                        Rule two=new Rule(lefthand, righthand.get(0), righthand.get(1));
                        rules.add(two);
                    }
                }else{
                    if(righthand.size()==1){
                        temp.oneRule(righthand.get(0));
                    }
                    else{
                        temp.multipleRule(righthand.get(0), righthand.get(1));
                    }
                }
            }
        }
        */

        return rules;
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
