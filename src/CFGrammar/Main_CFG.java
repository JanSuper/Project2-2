package CFGrammar;

import java.io.*;
import java.util.*;

public class Main_CFG {
    /**
     * Use this main to test the CFG without running the whole project
     * @param args
     */
    public static void main(String[] args) throws IOException {
        String test = "the boy saw a girl with a phone";

        Main_CFG cfg = new Main_CFG(test);

        //Make ArrayList<String> grammar
        ArrayList<String> grammar = getAllRules();
        cfg.splitGrammar(grammar);

        cfg.initialize_Tree();
        cfg.implement_Tree();
        cfg.toPrint();

        StringBuffer result = new StringBuffer();
        cfg.getEndSplit(result);
        System.out.println(result.toString());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private ArrayList<Rule> rules = new ArrayList<>();
    private Branch_Rule BR = null;
    private Word_Rule WR = null;
    private Branch[][] Br = null;
    private String user_message;
    private String[] u_message;
    private int message_length;

    public Main_CFG(String pUser_message)
    {
        user_message = pUser_message;
        u_message = pUser_message.split("\\s");
        message_length = u_message.length;
    }

    public void splitGrammar(ArrayList<String> rules)
    {
        BR = new Branch_Rule();
        WR = new Word_Rule();

        for(int i = 0; i < rules.size(); i++)
        {
            String[] rule = rules.get(i).split("\\s");
            if(rule.length < 3)
            {
                continue;
            }
            else if(rule.length == 3)
            {
                WR.addRule(rule);
            }
            else
            {
                BR.addRule(rule);
            }
        }
    }

    public int getCYK(String this_rule, int iter, int word_length)
    {
        return 1;
    }

    public void getSkill()
    {
        //TODO: Utiliser le fichier .csv
    }

    /**
     * Partly Stolen from Github, we need the same from json (read,write)
     * @throws IOException
     */
    public static ArrayList<String> getAllRules() throws IOException
    {
        ArrayList<String> grammar = null;
        FileReader file = null;
        BufferedReader buffer = null;

        try{
            file = new FileReader(new File("src\\CFGrammar\\grammar.txt"));
            buffer = new BufferedReader(file);
            grammar = new ArrayList<>();
            String rule = null;
            while((rule = buffer.readLine()) != null)
            {
                grammar.add(rule);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(file != null)
            {
                try{
                    file.close();
                }
                catch(Exception e) {}
            }
            if(buffer != null)
            {
                try{
                    buffer.close();
                }
                catch(Exception e) {}
            }
        }

        return grammar;

        /*FileInputStream fstream = new FileInputStream(fileName);
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
        br.close();*/
    }

    public void getEndSplit(StringBuffer sentence)
    {
        Branch end_branch = Br[0][message_length-1];
        end_branch.get_endResult(sentence);
    }

    public String toStringTree(int l_side, int iter, int length)
    {
        return null;
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

    public void initialize_Tree()
    {
        Br = new Branch[message_length][];

        for(int i = 0; i < message_length; i++)
        {
            Br[i] = new Branch[message_length];
            for(int j = i; j < message_length; j++)
            {
                Br[i][j] = new Branch();
            }
        }

        for(int i = 0; i < message_length; i++)
        {
            initialize_Branch(i);
        }
    }

    public void initialize_Branch(int nbr)
    {
        String Br_word = u_message[nbr];
        //System.out.println(Br_word);
        ArrayList<Branch> Br_ter = WR.interprete(Br_word);
        for(int i = 0; i < Br_ter.size(); i++)
        {
            //Br[i][i].addRule(Br_ter.get(i), null, null);
            addRuleToBranch(Br[nbr][nbr], Br_ter.get(i), null, null);
        }
        //System.out.println(Br_ter.size());
    }

    public void addRuleToBranch(Branch parent, Branch pBr, Branch LHS, Branch RHS)
    {
        parent.addRule(pBr, LHS, RHS);
    }

    public void implement_Tree()
    {
        for(int words = 1; words < message_length; words++)
        {
            for(int i = 0; i < message_length-words; i++)
            {
                implement_oneBranch(i,words+i);
            }
        }
    }

    public void implement_oneBranch(int i, int words_plus)
    {
        for(int j = i; j < words_plus; j++)
        {
            implement_Branch(i, j, words_plus);
        }
    }

    private int kk = 0;
    public void implement_Branch(int i, int j, int words_plus)
    {
        //for(int j = i; j < words_plus; j++)
        //{
        //System.out.println("Co: "+i+" : "+j+" : "+words_plus);
        Branch this_Branch = Br[i][j];
        ArrayList<Branch> subTree = this_Branch.getValues();
        //kk = kk + subTree.size();
        //System.out.println(kk);
        for(int k = 0; k < subTree.size(); k++)
        {
            Branch this_Branch2 = Br[j+1][words_plus];
            ArrayList<Branch> sub_subTree = this_Branch2.getValues();
            //kk++;
            //System.out.println("Into the second rule: "+subTree.size()+" : "+kk);
            for(int l = 0; l < sub_subTree.size(); l++)
            {
                //System.out.println("Find "+ subTree.get(k).getWord_category() + " + " + sub_subTree.get(l).getWord_category());
                Branch last_Branch = new Branch();
                last_Branch = BR.test(subTree.get(k), sub_subTree.get(l));

                if(last_Branch != null)
                {
                    //kk++;
                    //System.out.println("--- FINALLY HERE --- "+kk);
                    Br[i][words_plus].addRule(last_Branch, subTree.get(k), sub_subTree.get(l));
                }
            }
        }
        //}
    }

    public Map<String,Object> getSentenceStructure(String sentence)
    {
        return null;
    }

    public void toPrint()
    {
        System.out.println("--- Tree ---");
        for(int i = 0; i < message_length; i++)
        {
            for(int j = 0; j < message_length; j++)
            {
                if(j<i)
                {
                    System.out.print("\t");
                }
                else
                {
                    System.out.print(Br[i][j].toString()+"\t");
                }
            }
            System.out.println();
        }

    }

}
