package CFGrammar;

import java.util.*;

public class Rule
{
    private ArrayList<String> r_side = new ArrayList<>();
    private ArrayList<String> r_side_2 = new ArrayList<>();

    private String l_side;
    private boolean multiple = true;

    public Rule(String pl_side, String pr_side)
    {
        l_side = pl_side;
        r_side.add(pr_side);
        multiple = false;
    }

    public Rule(String pl_side, String pr_side, String pr_side_2)
    {
        l_side = pl_side;
        r_side.add(pr_side);
        r_side_2.add(pr_side_2);
        multiple = true;
    }

    public void multipleRule(String rule_1, String rule_2)
    {
        boolean res = false;
        for(int i = 0; i < r_side.size(); i++)
        {
            if(r_side.get(i).equals(rule_1) && r_side_2.get(i).equals(rule_2))
            {
                res = true;
            }
        }
        if(!res)
        {
            r_side.add(rule_1);
            r_side_2.add(rule_2);
            multiple = true;
        }
    }

    public void oneRule(String rule_1)
    {
        boolean res = false;
        for(int i = 0; i < r_side.size(); i++)
        {
            if(r_side.get(i).equals(rule_1))
            {
                res = true;
            }
        }
        if(!res)
        {
            r_side.add(rule_1);
            multiple = false;
        }
    }

    public boolean getMultiple()
    {
        return multiple;
    }

    public String getL_side()
    {
        return l_side;
    }

    public ArrayList<String> getR_side()
    {
        return r_side;
    }

    public ArrayList<String> getR_side_2()
    {
        return r_side_2;
    }

    public int getScore() { return 1; }
}
