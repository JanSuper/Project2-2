package Agents;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private int age;

    private String city;
    private String street;
    private int streetNbr;

    private int[] phoneNbr;

    private List<String> messages;

    public User(){
        messages = new ArrayList<>();
    }

}
