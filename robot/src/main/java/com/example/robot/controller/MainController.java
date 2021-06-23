package com.example.robot.controller;

import com.example.robot.model.BasisRobot;
import com.example.robot.model.Cleaner;
import com.example.robot.model.Deliveryman;
import com.example.robot.model.Producer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;

@Controller
public class MainController {
    //    Величина при достижении которой нужно подключать новых роботов к работе
    static final int LIMIT_TASC = 4;

    static volatile Queue<String[]> robotTask = new LinkedList<>();

    static volatile List<BasisRobot> robotStorage = new ArrayList<>();
    // Промежуточный лист для назначений(что бы не создавать каждый раз в цикле)
    static List<BasisRobot> transitRobotList = new ArrayList<>();

    static volatile List<String> roboLogs = new ArrayList<>();
    //Для валидации актуальных комманд
    static volatile List<String> robotMainCommand = new ArrayList<>(Arrays.asList("produce", "delivery", "clean", "kill"));

    @ResponseBody
    @RequestMapping(value = "/sendCommand", method = RequestMethod.POST)
    public String setComand(@RequestParam("command") String command, @RequestParam(name = "name", required = false, defaultValue = "noName") String name) {
        if (robotMainCommand.contains(command)) {
            //проверка на наличие имени робота с вве денным именем
            long actualNameSize = robotStorage.stream().filter(robot -> robot.getName().equals(name)).collect(Collectors.counting());
            if (!command.equals("kill")) {
                robotTask.add(new String[]{command, name != null && actualNameSize > 0 ? name : null});
            }
            //Команду убить себя реализуем только с существующим именем робота
            else if (command.equals("kill") && actualNameSize > 0) {
                robotTask.add(new String[]{command, name});
            }
        }
        return "";
    }

    @ResponseBody
    @PostMapping("/getLogs")
    public String getLogs() {
        JSONObject object = new JSONObject();
        JSONArray logsArr = new JSONArray();
        JSONArray robotArr = new JSONArray();
        JSONArray taskArr = new JSONArray();

        roboLogs.forEach(logs -> {
            logsArr.put(new JSONObject().put("value", logs));
        });

        robotStorage.forEach(robot -> {
            robotArr.put(new JSONObject().put("value", robot.getName()));
        });

        robotTask.forEach(task -> {
            taskArr.put(new JSONObject().put("value", task[0]));
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

    @Scheduled(fixedRate = 500)
    public void roboTracker() {
        while (robotTask.peek() != null) {
            String[] task = robotTask.peek();
            //Проверка есть ли в базе роботы способные выполнить комманду
            if (robotStorage.stream().filter(robot -> robot.getOperationCode().contains(task[0])).collect(Collectors.counting()) > 0) {

                Date currentTime = new Date();
                //Если с командой нету имени ишем свободных по времени. Иначе ищем еще и по соответствию имени
                transitRobotList = task[1] == null ?
                        robotStorage.stream()
                                .filter(robot -> robot.getDateEnd().compareTo(currentTime) <= 0 && robot.getOperationCode().contains(task[0]))
                                .collect(Collectors.toList()) :
                        robotStorage.stream()
                                .filter(robot -> robot.getDateEnd().compareTo(currentTime) <= 0 && robot.getOperationCode().contains(task[0]) && robot.getName().equals(task[1]))
                                .collect(Collectors.toList());

                if (transitRobotList.size() > 0) {
                    roboLogs.add(transitRobotList.get(0).chooseMethod(task[0], robotStorage, transitRobotList.get(0)));
                    setWorkPeriod(transitRobotList.get(0));
                    robotTask.remove();
                }
                //Если лимит загрузки позволяет подождать освобождения занятого робота - ждем
                else if (robotTask.size() > LIMIT_TASC && task[1] == null) {
                    sendRobotTask(task[0]);
                }
            } else {
                sendRobotTask(task[0]);
            }
        }
    }

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
        if (robot != null) {
            setWorkPeriod(robot);
        }

        return robot;
    }

    public void sendRobotTask(String task) {
        BasisRobot newRobot = getRobotByTask(task);
        if (newRobot != null) {
            robotStorage.add(newRobot);
            roboLogs.add("Create new robot");
            roboLogs.add(newRobot.mainFunctional());
            robotTask.remove();
        }
    }

    public void setWorkPeriod(BasisRobot robot) {
        Date start = new Date();
        Date dateEnd = new Date(start.getTime() + 3000);
        //Назначение промежутка времени в котором робот будет недоступен для нового задани
        robot.setDateEnd(dateEnd);
    }
}
