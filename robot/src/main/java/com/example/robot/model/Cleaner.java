package com.example.robot.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Cleaner extends BasisRobot {
    private String lomb;
    @Override
    public String mainFunctional() {
        return   "Robot " + getName() + " started clean";
    }

    public Cleaner(){
        setOperationCode("clean");
        countRobot += 1;
        setName("Cleaner №" + countRobot);
    }
}
