package com.example.robot.controller;

import com.example.robot.model.BasisRobot;
import com.example.robot.model.Cleaner;
import com.example.robot.model.Deliveryman;
import com.example.robot.model.Producer;
import com.sun.istack.internal.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class MainController {
    //    Величина при достижении которой нужно подключать новых роботов к работе
    static final int LIMIT_TASC = 4;

    static volatile Queue<String> robotTask = new LinkedList<>();

    static Map<String ,List<BasisRobot>> robotStorage = new HashMap<>();
    // Промежуточный лист для назначений(что бы не создавать каждый раз в цикле)
    static List<BasisRobot> transitRobotList = new ArrayList<>();

    static List<String> roboLogs = new ArrayList<>();

    @ResponseBody
    @RequestMapping(value = "/sendComand", method = RequestMethod.POST)
    public String setComand(String comand) {
            robotTask.add(comand);
        return "";
    }

    @ResponseBody
    @PostMapping("/getLogs")
    public String getLogs() {
        JSONObject object = new JSONObject();
        JSONArray logsArr = new JSONArray();
        JSONArray robotArr = new JSONArray();
        JSONArray taskArr = new JSONArray();

        roboLogs.forEach(e -> {
            logsArr.put(new JSONObject().put("value", e));
        });

        List<BasisRobot> baseRobotList = robotStorage.values().stream().flatMap(e -> e.stream()).collect(Collectors.toList());
        baseRobotList.forEach( f -> {
                robotArr.put(new JSONObject().put("value", f.getName()));
        });

        robotTask.forEach(e -> {
            taskArr.put(new JSONObject().put("value", e));
        });

        object.put("logsArr", logsArr);
        object.put("robotArr", robotArr);
        object.put("taskArr", taskArr);
        roboLogs.clear();
        return object.toString();
    }

    @GetMapping("/")
    public String overview() {
        return "robotMenu.html";
    }

    @Scheduled(fixedRate = 2000)
    public void roboTracker() {
        while (robotTask.peek() != null) {
            String task = robotTask.peek();
            if (robotStorage.get(task) != null && robotStorage.get(task).size() > 0) {

                Date currentTime = new Date();
                transitRobotList = robotStorage.get(task).stream().filter(f -> f.getDateEnd().compareTo(currentTime) <= 0 && f.getOperationCode().equals(task)).collect(Collectors.toList());

                if (transitRobotList.size() > 0) {
                    roboLogs.add(transitRobotList.get(0).mainFunctional());
                    setWorckPeriod(transitRobotList.get(0));
                    robotTask.remove();
                } else if (robotTask.size() > LIMIT_TASC) {
                    sendRobotTask(task);
                    robotTask.remove();
                    roboLogs.add(transitRobotList.get(transitRobotList.size() - 1).mainFunctional());
                }
            } else {
                sendRobotTask(task);
            }

        }
    }

    @NotNull
    public BasisRobot getRobotByTask(String task) {
        BasisRobot robot = null;
        switch (task) {
            case "produce":
                robot = new Producer();
                break;
            case "delivery":
                robot = new Deliveryman();
                break;
            case "clean":
                robot = new Cleaner();
                break;
        }

        setWorckPeriod(robot);

        return robot;
    }

    public void sendRobotTask(String task) {
        BasisRobot newRobot = getRobotByTask(task);
        robotTask.remove();
        if(robotStorage.get(task) == null){
            robotStorage.put(task, new ArrayList<>(Arrays.asList(newRobot)));
        }else{
            robotStorage.get(task).add(newRobot);
        }
        roboLogs.add(newRobot.mainFunctional());
    }

    public void setWorckPeriod(BasisRobot robot) {
        Date start = new Date();
        Date dateEnd = new Date(start.getTime() + 5000);
        //Назначение промежутка времени в котором робот будет недоступен для нового задани
        robot.setDateEnd(dateEnd);
    }
}
