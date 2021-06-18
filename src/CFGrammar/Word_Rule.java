package CFGrammar;

import java.util.ArrayList;

public class Word_Rule {
    private ArrayList<Rule> word_Rules = null;
    private ArrayList<String> result_array = null;
    private ArrayList<Double> prob_word = null;

    //TODO add a probability part in here for each words

    public Word_Rule()
    {
        word_Rules = new ArrayList<>();
        result_array = new ArrayList<>();
        prob_word = new ArrayList<>();
    }

    public void addRule(String[] rules)
    {
        Rule rule = new Rule();
        Rule tempRule = new Rule();
        tempRule = rule.create_WordRule(rules);
        word_Rules.add(tempRule);
    }

    public ArrayList<Branch> interpret(String word)
    {
        ArrayList<Branch> subTree = new ArrayList<>();
        boolean foreign_word = true;
        for(int i = 0; i < word_Rules.size(); i++)
        {
            if(word_Rules.get(i).getRHS_Word().equals(word))
            {
                String res = (word_Rules.get(i).getLHS() +":"+word_Rules.get(i).getRHS_Word());
                result_array.add(res);

                Branch word_Branch = new Branch();
                word_Branch.setWord_category(word_Rules.get(i).getLHS());
                word_Branch.setWord(word_Rules.get(i).getRHS_Word());
                subTree.add(word_Branch);
                foreign_word = false;
                System.out.println("Added word : "+ word);
            }
            /*if(word_Rules.get(i).getLHS().equals("FW"))
            {
                String res = (word_Rules.get(i).getLHS() +":"+ word);
                result_array.add(res);
            }*/
        }
        if(foreign_word)
        {
            System.out.println("Didn't found in corpus: "+word);

            String res = ("FW:"+word);
            result_array.add(res);

            Branch foreign_word_Branch = new Branch();
            foreign_word_Branch.setWord_category("FW");
            foreign_word_Branch.setWord(word);
            subTree.add(foreign_word_Branch);
        }
        return subTree;
    }

    public ArrayList<Rule> getWord_Rules()
    {
        return word_Rules;
    }

    public ArrayList<String> getResult_array()
    {
        return result_array;
    }
}
