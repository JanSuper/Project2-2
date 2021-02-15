package Skills.Schedule;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class UM_Schedule{

    private Scanner file_reader = null;
    private String schedule_text = null;
    private ArrayList<Course> courses = new ArrayList<Course>();

    /**
     * Gets the .ics file from Maastricht University Timetable
     * @param UM_file (.ics at url: https://timetable.maastrichtuniversity.nl/ical?602942e1&group=false&eu=STYyMjE4NDQ=&h=Msp_x9ez0v2UDuVhKbtJ82wTla65FcnvbVxh-lPS3DM=)
     */
    public UM_Schedule(URL UM_file)
    {
        try{
            file_reader = new Scanner(UM_file.openStream());
            while (file_reader.hasNextLine())
            {
                schedule_text = file_reader.nextLine();
                if(schedule_text.equals("BEGIN:VEVENT"))
                {
                    file_reader.nextLine();
                    String start_DateTime = file_reader.nextLine();
                    String end_DateTime = file_reader.nextLine();
                    String summary = file_reader.nextLine();
                    String location = null;

                    schedule_text = file_reader.nextLine();
                    if(schedule_text.startsWith("LOCATION"))
                    {
                        location = schedule_text;
                    }
                    else
                    {
                        summary = summary + schedule_text;
                        location = file_reader.nextLine();
                    }

                    Course course = new Course(start_DateTime,end_DateTime,summary,location);
                    courses.add(course);
                }
            }
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public ArrayList<Course> getCourses()
    {
        return courses;
    }
}
