package com.example.robot.model;

public class Deliveryman extends BasisRobot {
    @Override
    public String mainFunctional() {
        return  "Robot " + getName() + " started delivery";
    }

    public Deliveryman(){
        setOperationCode("delivery");
        countRobot += 1;
        setName("Deliveryman №" + countRobot);
    }
}
