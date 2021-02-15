package Skills.Schedule;

import java.util.ArrayList;

public class Skill_Schedule {

    private String um_url = "https://timetable.maastrichtuniversity.nl/ical?602942e1&group=false&eu=STYyMjE4NDQ=&h=Msp_x9ez0v2UDuVhKbtJ82wTla65FcnvbVxh-lPS3DM=";
    private ArrayList<Course> courses;
    private UM_Schedule schedule;

    public Skill_Schedule()
    {
        schedule = new UM_Schedule(um_url);

        courses = schedule.getCourses();
    }

    public String getCourse()
    {
        return courses.get(1).getCourse();
    }
}
