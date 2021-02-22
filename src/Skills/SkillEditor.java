package Skills;

import Interface.Screens.MainScreen;

import java.io.*;

public class SkillEditor {

    public boolean createSkill(String skillName){
        FileWriter writer;
        {
            try {
                writer = new FileWriter(new File("src\\Skills\\NewSkills\\"+skillName + ".txt"));
                PrintWriter out = new PrintWriter(writer);
                out.println(skillName);

                writer.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
