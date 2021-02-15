package Skills.Schedule;

public class Course {

    private String Date;
    private String start_Time;
    private String end_Time;
    private String summary;
    private String location;

    public Course(String pStart_DateTime, String pEnd_DateTime, String pSummary, String pLocation)
    {
        String[] split_start_DateTime = pStart_DateTime.split(":");
        String[] split_start_Time = split_start_DateTime[1].split("T");
        String[] split_end_DateTime = pEnd_DateTime.split(":");
        String[] split_end_Time = split_end_DateTime[1].split("T");

        Date = split_start_Time[0];
        if(split_start_Time.length > 1)
        {
            start_Time = split_start_Time[1];
        }
        if(split_end_Time.length >1)
        {
            end_Time = split_end_Time[1];
        }

        String[] split_summary = pSummary.split(":");

        summary = split_summary[1];

        String[] split_location = pLocation.split(":");

        if(split_location.length > 1)
        {
            location = split_location[1];
        }
    }

    public String getDate() {
        return Date;
    }

    public String getStart_Time() {
        return start_Time;
    }

    public String getEnd_Time() {
        return end_Time;
    }

    public String getSummary() {
        return summary;
    }

    public String getLocation() {
        return location;
    }

    public String getCourse()
    {
        return getSummary()+" Date: "+getDate()+" Begins at: "+getStart_Time()+" Ends at: "+getEnd_Time()+" At place: "+getLocation();
    }
}
