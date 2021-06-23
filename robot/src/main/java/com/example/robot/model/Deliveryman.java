package com.example.robot.model;

public class Deliveryman extends BasisRobot {
    protected static int countRobot = 0;
    @Override
    public String mainFunctional() {
        return  "Robot " + getName() + " started delivery";
    }

    public Deliveryman(){
        getOperationCode().add("delivery");
        countRobot += 1;
        setName("Deliveryman #" + countRobot);
    }
}
