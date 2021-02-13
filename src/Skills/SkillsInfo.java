package Skills;

import Interface.Screens.MainScreen;

import java.io.*;

public class SkillsInfo {

    public void getWeather(){
        String [][] dataSet;
        //create 2 arrays, one to store the data of the user and the other to split the data between password and username
        String [] splitData;
        String [][] data = new String [0][];

        // Begin a new file reader object directed at the text file we want to read (input)
        File file = new File("src\\Skills\\Weather\\users.txt");
        // We want to cast out file reader to a buffered reader! (for reasons which will be clear next lecture).
        BufferedReader br;
        {
            // If the file does not exist, we will get an error, so try catch to make java happy
            try {
                // create buffered reader
                br = new BufferedReader(new FileReader(file));
                String st = "";

                // while another line exists in our text file, we read it!
                while ((st = br.readLine()) != null) {
                    // instead of printing them, here we can also store the users in an array
                    splitData = st.split(" ",2);

                    //split the array bewteen usernames and password
                    if(splitData.length == 2) {
                        String[][] res = new String[data.length+1][splitData.length];
                        for(int i=0;i<data.length;i++){
                            for(int j=0;j<res[0].length;j++){
                                res[i][j]=data[i][j];
                            }
                        }
                        res[data.length][0]=splitData[0];
                        res[data.length][1]=splitData[1];

                        data = res;
                    }
                }
                // catch exceptions if the files are not found
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        //the array we use in the actionlistener need to be final, or we can't use it
        dataSet = data;
    }
}
