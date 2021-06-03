package net.foxtam;

import java.awt.*;

public class BotLifeController {
    private boolean interruptedWithKey = false;
    private boolean globalPause = false;
    private final Robot robot;

    public BotLifeController(Robot robot) {
        this.robot = robot;
    }

    public void sleep(int millis) {
        int timeQuantum = 500;
        for (int i = 1; i <= millis / timeQuantum; i++) {
            checkPauseOrInterrupt();
            robot.delay(timeQuantum);
        }
        checkPauseOrInterrupt();
        robot.delay(millis % timeQuantum);
    }

    private void checkPauseOrInterrupt() {
        if (interruptedWithKey) {
            interruptBot("Ручное завершение");
        } else {
            userPause();
        }
    }

    public void interrupt() {
        interruptedWithKey = true;
    }

    public boolean isGlobalPause() {
        return globalPause;
    }

    private void userPause() {
        while (globalPause) {
            if (interruptedWithKey) interruptBot("Ручное завершение");
            robot.delay(10);
        }
    }

    public void setGlobalPause() {
        globalPause = true;
    }

    public void cancelGlobalPause() {
        globalPause = false;
    }

    private void interruptBot(String msg) {
        throw new InterruptBotException(msg);
    }
}
