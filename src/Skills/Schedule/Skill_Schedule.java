package Skills.Schedule;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Skill_Schedule {

    private String um_url = "https://timetable.maastrichtuniversity.nl/ical?602942e1&group=false&eu=STYyMjE4NDQ=&h=Msp_x9ez0v2UDuVhKbtJ82wTla65FcnvbVxh-lPS3DM=";
    private ArrayList<Course> courses;
    private UM_Schedule schedule;
    private String today_date;
    private String today_time;
    private int weekday;
    private int month;
    private int year;

    public Skill_Schedule()
    {
        schedule = new UM_Schedule(um_url);
        courses = schedule.getCourses();

        LocalDateTime todayDateTime = LocalDateTime.now();
        weekday = todayDateTime.getDayOfWeek().getValue();
        month = todayDateTime.getMonthValue();
        year = todayDateTime.getYear();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String todayFormattedDateTime = todayDateTime.format(formatter);
        String[] split_today_DateTime = todayFormattedDateTime.split("T");
        today_date = split_today_DateTime[0].replaceAll("-", "");
        if(split_today_DateTime.length > 1)
        {
            today_time = split_today_DateTime[1].replaceAll(":", "");
            today_time = today_time.substring(0,today_time.length()-8);
        }
    }

    public String getNextCourse()
    {
        Course next_Course = null;
        int i = 0;

        while (i < courses.size() && next_Course == null)
        {
            if(courses.get(i).getDate().compareTo(today_date) == 0)
            {
                if(courses.get(i).getStart_Time().compareTo(today_time) >= 0)
                {
                    next_Course = courses.get(i);
                }
            }
            if(courses.get(i).getDate().compareTo(today_date) > 0)
            {
                next_Course = courses.get(i);
            }
            i++;
        }

        if(next_Course != null)
        {
            return next_Course.getCourse();
        }
        else
        {
            return "There is no lecture.";
        }
    }

    public String getCourseOnDate(String date)
    {
        ArrayList<Course> courses_thatDay = new ArrayList<>();
        String onDate_courses = "";

        for(int i = 0; i < courses.size(); i++)
        {
            if(courses.get(i).getDate().equals(date))
            {
                courses_thatDay.add(courses.get(i));
            }
        }

        if(courses_thatDay.isEmpty())
        {
            return "There are no Lecture on that day. Date: "+date;
        }
        else
        {
            for(int j = 0; j < courses_thatDay.size(); j++)
            {
                onDate_courses = onDate_courses + courses_thatDay.get(j).getCourse() + System.lineSeparator();
            }
            return onDate_courses;
        }
    }

    public String getToday()
    {
        return getCourseOnDate(today_date);
    }

    public ArrayList<String> getThisWeek()
    {
        ArrayList<String> this_week = new ArrayList<>();

        int up = weekday;
        int down = weekday-1;

        while(down >= 0)
        {
            this_week.add(getCourseOnDate(dateMinusDate(down)));
            down--;
        }
        while(up < 7)
        {
            this_week.add(getCourseOnDate(datePlusDays(up)));
            up++;
        }
        return this_week;
    }

    public ArrayList<String> getThisMonth()
    {
        ArrayList<String> this_month = new ArrayList<>();
        String this_YearMonth = today_date.substring(0,6);

        for(int i = 0; i < courses.size(); i++)
        {
            if(courses.get(i).getDate().startsWith(this_YearMonth))
            {
                this_month.add(courses.get(i).getCourse());
            }
        }

        return this_month;
    }

    public String dateMinusDate(int minus_days)
    {
        LocalDateTime todayDateTime = LocalDateTime.now();
        LocalDateTime minus_dateTime = todayDateTime.minusDays(minus_days);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String minus_FormattedDateTime = minus_dateTime.format(formatter);
        String[] split_minus_DateTime = minus_FormattedDateTime.split("T");
        return split_minus_DateTime[0].replaceAll("-", "");
    }

    public String datePlusDays(int plus_days)
    {
        LocalDateTime todayDateTime = LocalDateTime.now();
        LocalDateTime plus_dateTime = todayDateTime.plusDays(plus_days);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        String minus_FormattedDateTime = plus_dateTime.format(formatter);
        String[] split_plus_DateTime = minus_FormattedDateTime.split("T");
        return split_plus_DateTime[0].replaceAll("-", "");
    }
}
