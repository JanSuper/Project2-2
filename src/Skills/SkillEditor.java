package Skills;

import Interface.Screens.MainScreen;

import java.io.*;

public class SkillEditor {

    public void createSkill(String skillName){
        FileWriter writer;
        {
            try {
                writer = new FileWriter(new File("src\\Skills\\NewSkills\\"+skillName + ".txt"));
                PrintWriter out = new PrintWriter(writer);
                out.println(skillName);

                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
