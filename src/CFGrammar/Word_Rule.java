package CFGrammar;

import java.util.ArrayList;

public class Word_Rule {
    private ArrayList<Rule> word_Rules = null;

    public Word_Rule()
    {
        word_Rules = new ArrayList<>();
    }

    public void addRule(String[] rules)
    {
        Rule rule = new Rule();
        Rule tempRule = new Rule();
        tempRule = rule.create_WordRule(rules);
        word_Rules.add(tempRule);
    }

    public ArrayList<Branch> interprete(String word)
    {
        ArrayList<Branch> subTree = new ArrayList<>();
        for(int i = 0; i < word_Rules.size(); i++)
        {
            // LE PROBLEME EST ICI !!
            //System.out.println(word+" ---- ");
            if(word_Rules.get(i).getRHS_Word().equals(word))
            {
                Branch word_Branch = new Branch();
                word_Branch.setWord_category(word_Rules.get(i).getLHS());
                word_Branch.setWord(word_Rules.get(i).getRHS_Word());
                subTree.add(word_Branch);
            }
        }
        return subTree;
    }
}
