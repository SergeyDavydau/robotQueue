package com.example.robot.model;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Setter
@Getter
public abstract class BasisRobot {

    private String name;

    private Date dateEnd;

    private List<String> operationCode = new ArrayList<>(Arrays.asList("kill"));

    public abstract String mainFunctional();

    public  String killYourSef(){
        return "Robot " + name +  " kill himself";
    };

    public String chooseMethod(String task, List<BasisRobot> robotList, BasisRobot robot){
        if(task != null){
            if(!task.equals("kill")){
                return mainFunctional();
            }else{
                robotList.remove(robot);
                return killYourSef();
            }
        }
        return  "";
    }


}
