package com.example.robot.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Producer extends BasisRobot {

    @Override
    public String mainFunctional() {
        return  "Robot " + getName() + " started produce";
    }

    public Producer(){
        setOperationCode("produce");
        countRobot += 1;
        setName("Producer №" + countRobot);
    }
}
