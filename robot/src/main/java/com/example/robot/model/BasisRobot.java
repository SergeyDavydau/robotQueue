package com.example.robot.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;

@Setter
@Getter
public abstract class BasisRobot {
    protected static int countRobot = 0;

    private String name;

    private Date dateEnd;

    private String operationCode;

    public abstract String mainFunctional();

    public  String killYourSef(){
        return "Robot " + name +  "kill himself";
    };

}
