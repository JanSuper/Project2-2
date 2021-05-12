package CFGrammar;

import java.util.*;
import java.util.stream.Collectors;


public class Main_CFG {

    /**
     * Use this main to test the CFG without running the whole project
     * @param args
     */
    public static void main(String[] args) {
        HashMap<String, ArrayList<String>> grammar = new HashMap<>();

        grammar.put("S", new ArrayList<>(Collections.singletonList(String.join("",(CharSequence[]) "NP VP".split(" ")))));
        grammar.put("S", new ArrayList<>(Collections.singletonList(String.join("",(CharSequence[]) "Interj NP VP".split(" ")))));
        grammar.put("NP", new ArrayList<>(Collections.singletonList(String.join("",(CharSequence[]) "Det N".split(" ")))));
        grammar.put("NP", new ArrayList<>(Collections.singletonList(String.join("",(CharSequence[]) "Det N that VP".split(" ")))));
        grammar.put("NP", new ArrayList<>(Collections.singletonList(String.join("",(CharSequence[]) "Det Adj N".split(" ")))));
        grammar.put("NP", new ArrayList<>(Collections.singletonList(String.join("",(CharSequence[]) "Det N PP".split(" ")))));
        grammar.put("PP", new ArrayList<>(Collections.singletonList(String.join("",(CharSequence[]) "Prep NP".split(" ")))));
        grammar.put("VP", new ArrayList<>(Collections.singletonList(String.join("",(CharSequence[]) "Vtrans NP".split(" ")))));
        grammar.put("VP", new ArrayList<>(Collections.singletonList(String.join("",(CharSequence[]) "Vintr".split(" ")))));
        /*"S":"NP VP",
        "S":"Interj NP VP",
        "NP":"Det N",
        "NP":"Det N that VP",
        "NP":"Det Adj N",
        "NP":"Det N PP",
        "PP":"Prep NP",
        "VP":"Vtrans NP",
        "VP":"Vintr",*/

        String from_user = "The restaurant agree";
        /*
        The restaurant agree (accepted)
	    The restaurant bring a phone (accepted)
	    The big restaurant look (accpeted)
	    The home on house (wrong but every word are in the database)
	    The house on a hill (wrong with unknown words)
         */


        Main_CFG CFG_RUN = new Main_CFG(grammar);
        String test = "S NP VP";

        System.out.println((CFG_RUN.checkUserMessage(test)) ? "Is valid" : "Is not valid");

        /*
        Check if the grammar doesn't contain any errors
         */

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////



    private HashMap<String, ArrayList<String>> rules;
    private String user_Message;
    private String cfg_answer;

    public Main_CFG(HashMap<String, ArrayList<String>> pRules)
    {
        rules = pRules;
    }

    public boolean checkUserMessage(String pUser_Message)
    {
        String[] words = pUser_Message.split(" ");
        int nbr_words = words.length;
        ArrayList<String>[][] usedRules = new ArrayList[nbr_words][nbr_words];

        for(int i = 0; i < nbr_words; i++)
        {
            usedRules[0][i] = getRule(String.valueOf(words[i]));
            System.out.println(usedRules[0][i]);
        }

        for(int j = 1; j < nbr_words; j++)
        {
            for(int y = 0; y < (nbr_words-j); y++)
            {
                ArrayList<String> tryRules = new ArrayList<>();
                int line = j;
                int word_pos = y;

                for(int x = 0; x < line; x++)
                {
                    ArrayList<String> leftSide = usedRules[x][word_pos];
                    ArrayList<String> rightSide = usedRules[line-1][word_pos+1];
                    checkInRules(mergeRules(leftSide,rightSide), tryRules);
                }
                usedRules[line][word_pos] = tryRules;
            }
        }
        return usedRules[nbr_words-1][0].contains("S");
    }

    public ArrayList<String> getRule(String rule)
    {
        return rules.keySet().stream().filter(key -> rules.get(key).contains(rule))
            .collect(Collectors.toCollection(ArrayList::new));
    }

    public void checkInRules(ArrayList<String> from, ArrayList<String> to)
    {
        from.stream().filter(key -> to.indexOf(key) == -1).forEach(to::add);
    }

    public ArrayList<String> mergeRules(ArrayList<String> leftSide, ArrayList<String> rightSide)
    {
        ArrayList<String> merge_Rules = new ArrayList<>();
        if(!leftSide.isEmpty() && !rightSide.isEmpty())
        {
            for(String left_rule : leftSide)
            {
                for(String right_rule : rightSide)
                {
                    String merge = left_rule + right_rule;
                    checkInRules(getRule(merge), merge_Rules);
                }
            }
        }
        return merge_Rules;
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
