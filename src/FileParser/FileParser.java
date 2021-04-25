package FileParser;

import DataBase.Data;
import Interface.Screens.MainScreen;
import javafx.scene.text.Text;

import java.io.*;

public class FileParser {

    public FileParser(){

    }

    public String getUsersPicture(String type){
        File userPicture = new File("src/DataBase/Users/"+ Data.getUsername()+"/"+type+".png");
        if(!userPicture.exists()||!userPicture.isFile()){
            userPicture = new File("src/DataBase/Users/"+Data.getUsername()+"/"+type+".jpg");
            if(!userPicture.exists()||!userPicture.isFile()){
                System.out.println("Something went wrong charging your " + type);
                if(!userPicture.exists()){
                    System.out.println("It seems like your " + type + " does not exists");
                }
                return null;
            }else{
                return "src/DataBase/Users/"+Data.getUsername()+"/"+type+".jpg";
            }
        }else{
            return "src/DataBase/Users/"+Data.getUsername()+"/"+type+".png";
        }
    }

    public void createUser(String user,String psw, Text left){
        //Creating a File object
        File file = new File("src/DataBase/Users/"+user);
        //Creating the directory
        boolean bool = file.mkdir();
        if(bool){
            System.out.println("Directory created successfully");
        }else {
            System.out.println("Sorry couldn’t create specified directory");
        }
        try {
            FileWriter writer;
            {
                try {
                    writer = new FileWriter("src/DataBase/Users/"+user+"/"+user+".txt");
                    PrintWriter out = new PrintWriter(writer);
                    out.println("-Password: "+"\n" + psw);
                    out.println("-Location: "+"\n" + "/");
                    out.println("-Age: "+"\n" + "/");
                    out.println("-Profession: "+"\n" + "/");
                    writer.close();
                } catch (IOException e) {
                    left.setText("Sorry, something went wrong");
                    e.printStackTrace();
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getUserInfo(String info){
        String result = "";
        File userFile = new File("src/DataBase/Users/"+Data.getUsername()+"/"+Data.getUsername()+".txt");

        try{
            BufferedReader reader = new BufferedReader(new FileReader(userFile));
            String line = "", oldtext = "";
            while((line = reader.readLine()) != null)
            {
                oldtext += line + "\r\n";
            }
            reader.close();

            String[] lines = oldtext.split(System.getProperty("line.separator"));
            for (int i = 0; i < lines.length; i++) {
                if(lines[i].startsWith(info))
                {
                    result = lines[i+1];
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean changeUserInfo(String info, String edit, MainScreen mainScreen){
        File userFile = new File("src/DataBase/Users/"+Data.getUsername()+"/"+Data.getUsername()+".txt");

        try{
            BufferedReader reader = new BufferedReader(new FileReader(userFile));
            String line = "", oldtext = "";
            while((line = reader.readLine()) != null)
            {
                oldtext += line + "\r\n";
            }
            reader.close();

            String[] lines = oldtext.split(System.getProperty("line.separator"));
            for (int i = 0; i < lines.length; i++) {
                if(lines[i].startsWith(info))
                {
                    lines[i+1] = edit;
                    break;
                }
            }
            StringBuffer sb = new StringBuffer();
            for(int i = 0; i < lines.length; i++) {
                sb.append(lines[i] + "\n");
            }
            String str = sb.toString();

            FileWriter writer = new FileWriter(userFile);
            writer.write(str);
            writer.close();
            mainScreen.chat.receiveMessage("Your new "+info+ " is " + edit);
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkUserInfo(String info, String user, String psw){
        File userFile = new File("src/DataBase/Users/"+user+"/"+user+".txt");

        try{
            BufferedReader data = new BufferedReader(new FileReader(userFile));

            String s;
            while ((s = data.readLine()) != null)
            {
                if(s.startsWith(info))
                {
                    if(psw.equals(data.readLine())){
                        return true;
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
        return false;
    }
}
