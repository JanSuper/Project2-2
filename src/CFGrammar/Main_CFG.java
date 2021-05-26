package CFGrammar;

import FileParser.FileParser;
import TextRecognition.TextRecognition;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class Main_CFG {
    /**
     * Use this main to test the CFG without running the whole project
     * [TO DELETE LATER]
     * @param args
     */
    public static void main(String[] args) throws IOException {
        String test = "How is the weather";

        Main_CFG cfg = new Main_CFG(test);

        JsonReader jr = new JsonReader();
        ArrayList<String> grammar = jr.getAllRules();

        /*cfg.splitGrammar(grammar);
        cfg.initialize_Tree();
        cfg.implement_Tree();

        StringBuffer result = new StringBuffer();
        cfg.getEndSplit(result);
        System.out.println(result.toString());

        cfg.getSkill();*/

        for(int i = 0; i < grammar.size(); i++)
        {
            System.out.println("Old: "+grammar.get(i));
        }
        //jr.addRules("NB:Z,FG" , false);
        //jr.addRules("N:Deng",true);
        jr.removeRule("N:Deng",true);
        grammar = jr.getAllRules();
        for(int i = 0; i < grammar.size(); i++)
        {
            System.out.println("New : "+grammar.get(i));
        }
        //reader.getAllRules();
        //cfg.splitGrammar(grammar);

        //cfg.toPrint();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private ArrayList<Rule> rules = new ArrayList<>();
    private ArrayList<String> variable_words;
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

        /*JsonReader reader = new JsonReader();
        ArrayList<String> checkgrammar = reader.getAllRules();
        splitGrammar(checkgrammar);
        initialize_Tree();
        implement_Tree();

        StringBuffer result = new StringBuffer();
        getEndSplit(result);
        System.out.println(result.toString());

        getSkill();*/
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
                //System.out.println("Word rule : "+rule[0]);
                WR.addRule(rule);
            }
            else
            {
                //System.out.println("Branch rule : "+rule[0]);
                BR.addRule(rule);
            }
        }
    }

    public int getCYK(String this_rule, int iter, int word_length)
    {
        return 1;
    }

    public int getSkill()
    {
        int score = 0;
        int best_score = 0;
        int final_skill_nbr = 0;
        ArrayList<Integer> possible_skills = new ArrayList<>();
        ArrayList<String> words_toSearch = new ArrayList<>();
        variable_words = new ArrayList<>();
        TextRecognition TR = new TextRecognition();

        ArrayList<String> main_words = WR.getResult_array();
        for(int i = 0; i < main_words.size(); i++)
        {
            System.out.println(main_words.get(i));
        }
        FileParser sk_file = new FileParser();
        List<List<String>> allSkills = sk_file.getAllSkills();

        for(int i = 0; i < main_words.size(); i++)
        {
            String[] main_word = main_words.get(i).split(":");
            String category = main_word[0];
            String word = main_word[1];

            if(category.contains("_word") || category.equals("VB") || category.equals(("VBZ")))
            {
                System.out.println("Added word to the list: "+ word);
                words_toSearch.add(word);
            }

            if(category.equals("FW"))
            {
                System.out.println("Added FOREIGN word to the list: "+word);
                variable_words.add(word);
            }
        }

        for(int i = 0; i < allSkills.size(); i++)
        {
            score = 0;
            String verb = allSkills.get(i).get(4);
            String noun = allSkills.get(i).get(5);
            String var = allSkills.get(i).get(6);
            String skill_nbr = allSkills.get(i).get(1);

            //TODO: adapt the score!
            for(int j = 0; j < words_toSearch.size(); j++)
            {
                if(words_toSearch.get(j).equals(verb))
                {
                    System.out.println("Added to score: "+verb);
                    score = score +2;
                }
                if(words_toSearch.get(j).equals(noun))
                {
                    System.out.println("Added to score: "+noun);
                    score = score + 3;
                }
            }

            // "y" means there is a variable for this skill
            if(var.equals("y"))
            {
                String var_nbr_s = allSkills.get(i).get(3);
                int var_nbr = Integer.parseInt(var_nbr_s);
                if(var_nbr == variable_words.size())
                {
                    score++;
                }
                //score++;
            }

            if(score > best_score)
            {
                possible_skills.clear();
                possible_skills.add(Integer.parseInt(skill_nbr));
                best_score = score;
            }
            else if(score == best_score)
            {
                possible_skills.add(Integer.parseInt(skill_nbr));
            }
        }

        for(int i = 0; i < variable_words.size(); i++)
        {
            System.out.println("Final variable : "+ variable_words.get(i));
        }

        System.out.println("Final skill list size : "+possible_skills.size());
        for(int z = 0; z < possible_skills.size(); z++)
        {
            System.out.println("Skill nbr : "+possible_skills.get(z));
        }

        System.out.println("With best score: "+ best_score);

        if(best_score > 3)
        {
            int n = new Random().nextInt(possible_skills.size());
            final_skill_nbr = possible_skills.get(n);
        }
        else
        {
            final_skill_nbr = 0;
        }
        return final_skill_nbr;
    }

    public String addOrRemoveRule(String message) throws IOException
    {
        Boolean terminal = true;
        String[] rule = message.split("/");
        if(rule[0].equals("ADD"))
        {
            String[] add = rule[1].split(":");
            String LHS = add[0];
            if(add[1].contains(","))
            {
                String[] RHS = add[1].split(",");
                terminal = false;
            }
            JsonReader jr = new JsonReader();
            jr.addRules(rule[1], terminal);
            return "The rule was added to the grammar.";
        }
        else if(rule[0].equals("REMOVE"))
        {
            String[] add = rule[1].split(":");
            String LHS = add[0];
            if(add[1].contains(","))
            {
                String[] RHS = add[1].split(",");
                terminal = false;
            }
            JsonReader jr = new JsonReader();
            jr.removeRule(rule[1], terminal);
            return "The rule was removed to the grammar.";
        }
        else
        {
            return "Error - could not add/remove the rule";
        }
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
    }

    public void getEndSplit(StringBuffer sentence)
    {
        Branch end_branch = Br[0][message_length-1];
        end_branch.get_endResult(sentence);
    }

    public ArrayList<String> getVariable_words()
    {
        return variable_words;
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


    private int kk = 0;
    public void initialize_Branch(int nbr)
    {
        String Br_word = u_message[nbr];
        kk++;
        //System.out.println("Branch init. : "+kk);
        ArrayList<Branch> Br_ter = WR.interpret(Br_word);
        //System.out.println("Size with unknown word: "+Br_ter.size());
        for(int i = 0; i < Br_ter.size(); i++)
        {
            //Br[i][i].addRule(Br_ter.get(i), null, null);
            addRuleToBranch(Br[nbr][nbr], Br_ter.get(i), null, null);
        }
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

    public void implement_Branch(int i, int j, int words_plus)
    {
        Branch this_Branch = Br[i][j];
        ArrayList<Branch> subTree = this_Branch.getValues();
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

    /**
     * Compares the difference between two Strings using the Levenshtein algorithm.
     * @param uMessage the user message without punctuation
     * @param dataBase_message the message in the database
     * @param threshold the maximum accepted distance between the Strings
     * @return the score between -1 and threshold
     */
    public int LevenshteinDistance(String uMessage, String dataBase_message, int threshold)
    {
        int uM = uMessage.length();
        int dB = dataBase_message.length();
        int[] cost_previous = new int[uM + 1];
        int[] cost_distance = new int[uM + 1];
        int[] cost_memory;
        int limit = Math.min(uM,threshold);
        int score = -1;

        if(uM == 0 || dB == 0)
        {
            return score;
        }

        if(uM > dB)
        {
            String temp = uMessage;
            uMessage = dataBase_message;
            dataBase_message = temp;
            int temp2 = uM;
            uM = dB;
            dB = temp2;
        }

        for(int i = 0; i <= limit; i++)
        {
            cost_previous[i] = i;
        }
        Arrays.fill(cost_previous, limit, cost_previous.length, Integer.MAX_VALUE);
        Arrays.fill(cost_distance, Integer.MAX_VALUE);

        for(int i = 1; i <= dB; i++)
        {
            char database_char = dataBase_message.charAt(i-1);
            cost_distance[0] = i;

            int min = Math.max(1, i-threshold);
            int max = i > Integer.MAX_VALUE - threshold ? uM : Math.min(uM,threshold+i);

            if(min > max)
            {
                return -1;
            }

            if(min > 1)
            {
                cost_distance[min-1] = Integer.MAX_VALUE;
            }

            for(int j = min; j <= max; j++)
            {
                if(uMessage.charAt(j-1) == database_char)
                {
                    cost_distance[j] = cost_previous[j-1];
                }
                else
                {
                    cost_distance[j] = 1 + Math.min(Math.min(cost_distance[j-1], cost_previous[j]), cost_previous[j-1]);
                }
            }

            cost_memory = cost_previous;
            cost_previous = cost_distance;
            cost_distance = cost_memory;
        }

        if(cost_previous[uM] <= threshold)
        {
            score = cost_previous[uM];
        }
        return score;
    }

}
