package com.example.robot.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cleaner extends BasisRobot {
    protected static int countRobot = 0;
    @Override
    public String mainFunctional() {
        return   "Robot " + getName() + " started clean";
    }

    public Cleaner(){
        getOperationCode().add("clean");
        countRobot += 1;
        setName("Cleaner #" + countRobot);
    }
}
