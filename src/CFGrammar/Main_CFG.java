package CFGrammar;

import java.io.*;
import java.util.*;

public class Main_CFG {

    /**
     * Use this main to test the CFG without running the whole project
     * @param args
     */
    public static void main(String[] args) throws IOException {
        String in = "A car cost a house";
        String fileName = "src\\CFGrammar\\grammar.json";
        Main_CFG cfg = new Main_CFG(in, fileName);
        //cfg.checkUserMessage(in);
        //cfg.printBestTree(in);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    private ArrayList<Rule> rules = new ArrayList<>();
    private String user_message;
    private int[][][] best_res;
    private int[][][] best_rule;
    private int[][][] best_tree;
    private String[][][] r_best;
    private String[][][] l_best;
    private String[] u_message;

    public Main_CFG(String pUser_message, String fileName) throws IOException
    {
        user_message = pUser_message;
        JsonReader jReader = new JsonReader();
        jReader.getAllRules();
        rules = jReader.getRules();
        System.out.println(rules.size());
        for(int i = 0; i < rules.size(); i++)
        {
            System.out.println(rules.get(i).getL_side());
        }
        //getAllRules(fileName);
    }

    public double checkUserMessage(String pUser_Message)
    {
        // Using Cocke–Younger–Kasami algorithm, with start symbol "S"
        String[] words = pUser_Message.trim().split("\\s");
        u_message = words;
        int word_length = words.length+1;

        best_rule = new int[rules.size()][word_length][word_length];
        best_res = new int[rules.size()][word_length][word_length];
        r_best = new String[rules.size()][word_length][word_length];
        l_best = new String[rules.size()][word_length][word_length];
        best_tree = new int[rules.size()][word_length][word_length];

        for(int i = 0; i < rules.size(); i++)
        {
            for(int j = 0; j < word_length; j++)
            {
                for(int k = 0; k < word_length; k++)
                {
                    best_res[i][j][k] = -1;
                    best_tree[i][j][k] = -2;
                    best_rule[i][j][k] = -3;
                    r_best[i][j][k] = "";
                    l_best[i][j][k] = "";
                }
            }
        }
        return getCYK("S", 0, word_length-1);
    }

    public int getCYK(String this_rule, int iter, int word_length)
    {
        System.out.println("Rule: " + this_rule);

        if(word_length == iter+1)
        {
            for(int i = 0; i < rules.size(); i++)
            {
                Rule new_rule = rules.get(i);
                if(new_rule.getL_side().equals(this_rule) && !new_rule.getMultiple())
                {
                    for(int j = 0; j < new_rule.getR_side().size(); j++)
                    {
                        if(new_rule.getR_side().get(j).equals(u_message[iter]))
                        {
                            best_res[i][iter][word_length] = new_rule.getScore();
                            best_rule[i][iter][word_length] = j;
                            best_tree[i][iter][word_length] = iter;
                            r_best[i][iter][word_length] = u_message[iter];
                            l_best[i][iter][word_length] = u_message[iter];
                            return best_tree[i][iter][word_length];
                        }
                    }
                }
            }
        }

        int best_score = 0;
        for(int i = iter+1; i < word_length; i++)
        {
            for(int j = 0; j < rules.size(); j++)
            {
                Rule new_rule = rules.get(j);
                if(new_rule.getL_side().equals(this_rule) && new_rule.getMultiple())
                {
                    for(int k = 0; k < new_rule.getR_side().size(); k++)
                    {
                        int score = getCYK(new_rule.getR_side().get(k), iter, i)+
                                getCYK(new_rule.getR_side_2().get(k), i, word_length)
                                +new_rule.getScore();
                        if(score>best_score)
                        {
                            best_res[j][iter][word_length] = score;
                            best_rule[j][iter][word_length] = k;
                            r_best[j][iter][word_length] = new_rule.getR_side_2().get(k);
                            l_best[j][iter][word_length] = new_rule.getR_side().get(k);
                            best_tree[j][iter][word_length] = i;
                        }
                        best_score = Math.max(best_score,score);
                        best_res[j][iter][word_length] = best_score;
                    }
                }
            }
        }
        return best_score;
    }

    /**
     * Partly Stolen from Github, we need the same from json (read,write)
     * @param fileName
     * @throws IOException
     */
    public void getAllRules(String fileName) throws IOException
    {
        FileInputStream fstream = new FileInputStream(fileName);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String startLine;
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
                ArrayList <String> righthand = new ArrayList<String>();
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
        System.out.println("number of rules: "+rules.size());
        br.close();
    }

    public void printBestTree(String pUser_message)
    {
        String[] words = pUser_message.trim().split("\\s");
        System.out.println(toStringTree(0,0,words.length));
    }

    public String toStringTree(int l_side, int iter, int length)
    {
        if(length == iter+1)
        {
            return " "+ rules.get(l_side).getL_side() +" ";// + rules.get(l_side).getR_side().get(best_rule[l_side][iter][length])+" ";
        }
        else
        {
            int l = 0, r = 0;
            for(int i = 0; i < rules.size(); i++)
            {
                if(rules.get(i).getL_side().equals(l_best[l_side][iter][length]))
                {
                    l = i;
                }
                if(rules.get(i).getL_side().equals(r_best[l_side][iter][length]))
                {
                    r = i;
                }
            }

            return "( "+ rules.get(l_side).getL_side() +" ("+ toStringTree(l, iter, best_tree[l_side][iter][length])
                    +" ) ( "+ toStringTree(r, best_tree[l_side][iter][length], length)+" ) )";
        }
    }


    public boolean checkProductionRules()
    {
        return true;
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
        //Adds a single word to the json file in the right category "N","V"...
    }

    public void readfromJson()
    {

    }

    public Map<String,Object> getSentenceStructure(String sentence)
    {
        return null;
    }

}
