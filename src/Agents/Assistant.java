package Agents;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Assistant {
    private List<String> messages;
    private File dataBase = new File("src\\DataBase\\textData.txt");
    private Random random = new Random();

    public Assistant()
    {
        messages = new ArrayList<>();
    }

    public String getResponse(String uMessage)
    {
        String clean_uMessage = removePunctuation(uMessage);
        ArrayList<String> res = new ArrayList<>();
        try{
            BufferedReader data = new BufferedReader(new FileReader(dataBase));

            String s;
            while ((s = data.readLine()) != null)
            {
                if(s.startsWith("U"))
                {
                    if(s.toLowerCase().contains(clean_uMessage))
                    {
                        String r;
                        while ((r = data.readLine()).startsWith("B"))
                        {
                            res.add(r.substring(2));
                        }
                    }
                }
            }
            data.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(res.isEmpty())
        {
            return "Could not understand your demand...";
        }
        else
        {
            //Here check if the answer is a number, if it is then call the method in question -> example: if code = 11 then call method thisWeekCourse() in Skill_Schedule
            int max = res.size();
            int n = random.nextInt(max);
            return res.get(n);
        }
    }

    public String getSkill(String pNumb)
    {
        return null;
    }

    public void addNewSkill(String[] pBot, String[] pUser)
    {

    }

    public String removePunctuation(String uMessage)
    {
        String clean_uMessage;
        String temp = uMessage.replaceAll("\\p{Punct}","");
        clean_uMessage = temp.trim().replaceAll(" +", " ");
        return clean_uMessage;
    }
}
