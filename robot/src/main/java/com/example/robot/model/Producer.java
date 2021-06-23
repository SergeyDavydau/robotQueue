package com.example.robot.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Producer extends BasisRobot {
    protected static int countRobot = 0;

    @Override
    public String mainFunctional() {
        return  "Robot " + getName() + " started produce";
    }

    public Producer(){
        getOperationCode().add("produce");
        countRobot += 1;
        setName("Producer #" + countRobot);
    }
}
