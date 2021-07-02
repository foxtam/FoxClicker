package net.foxtam.foxclicker;

import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import net.foxtam.foxclicker.exceptions.InterruptBotException;

import static net.foxtam.foxclicker.GlobalLogger.exception;

enum State {
    RUN, PAUSE, STOP;

    public boolean isPause() {
        return this == PAUSE;
    }

    public boolean isStop() {
        return this == STOP;
    }
}

public class BotLifeController implements AutoCloseable {
    private final GlobalKeyboardHook keyboardHook = new GlobalKeyboardHook(true);
    private State state = State.RUN;

    public BotLifeController(KeyConfig keyConfig, Runnable onStop, Runnable onPause) {
        keyboardHook.addKeyListener(
                new GlobalKeyAdapter() {
                    @Override
                    public void keyPressed(GlobalKeyEvent event) {
                        if (event.isControlPressed() == keyConfig.isCtrlPressed()
                                && event.isShiftPressed() == keyConfig.isShiftPressed()) {
                            if (event.getVirtualKeyCode() == keyConfig.getStopKey()) {
                                state = State.STOP;
                                onStop.run();
                            } else if (event.getVirtualKeyCode() == keyConfig.getPauseKey()) {
                                state = state.isPause() ? State.RUN : State.PAUSE;
                                onPause.run();
                            }
                        }
                    }
                });
    }

    public void sleep(int millis) {
        int timeQuantum = 100;
        for (int i = 1; i <= millis / timeQuantum; i++) {
            checkPauseOrInterrupt();
            Robo.getInstance().delay(timeQuantum);
        }
        checkPauseOrInterrupt();
        Robo.getInstance().delay(millis % timeQuantum);
    }

    public void checkPauseOrInterrupt() {
        switch (state) {
            case STOP -> interruptBot("Stopped by user");
            case PAUSE -> pause();
        }
    }

    private void interruptBot(String msg) {
        throw exception(new InterruptBotException(msg));
    }

    private void pause() {
        while (state.isPause()) {
            if (state.isStop()) interruptBot("Stopped by user");
            Robo.getInstance().delay(10);
        }
    }

    @Override
    public void close() {
        keyboardHook.shutdownHook();
    }
}