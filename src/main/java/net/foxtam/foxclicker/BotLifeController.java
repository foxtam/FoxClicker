package net.foxtam.foxclicker;

import net.foxtam.foxclicker.exceptions.InterruptBotException;

import static net.foxtam.foxclicker.GlobalLogger.exception;

public class BotLifeController {
    private boolean interruptedWithKey = false;
    private boolean globalPause = false;

    public void sleep(int millis) {
        int timeQuantum = 500;
        for (int i = 1; i <= millis / timeQuantum; i++) {
            checkPauseOrInterrupt();
            Robo.INSTANCE.delay(timeQuantum);
        }
        checkPauseOrInterrupt();
        Robo.INSTANCE.delay(millis % timeQuantum);
    }

    public void checkPauseOrInterrupt() {
        if (interruptedWithKey) {
            interruptBot("Stopped by user");
        } else {
            userPause();
        }
    }

    private void interruptBot(String msg) {
        throw exception(new InterruptBotException(msg));
    }

    private void userPause() {
        if (globalPause) {
            System.out.println("User pause");
            while (globalPause) {
                if (interruptedWithKey) interruptBot("Stopped by user");
                Robo.INSTANCE.delay(10);
            }
            System.out.println("Unpause");
        }
    }

    public void interrupt() {
        interruptedWithKey = true;
    }

    public boolean isGlobalPause() {
        return globalPause;
    }

    public void setGlobalPause() {
        globalPause = true;
    }

    public void cancelGlobalPause() {
        globalPause = false;
    }
}
