package net.foxtam.foxclicker;

import com.sun.jna.platform.win32.WinDef;
import net.foxtam.foxclicker.context.Context;
import net.foxtam.foxclicker.exceptions.FoxClickerException;
import net.foxtam.foxclicker.exceptions.InterruptBotException;

import java.util.List;
import java.util.Objects;

import static net.foxtam.foxclicker.GlobalLogger.*;

public abstract class Bot {
    private final LifeController lifeController;

    protected Bot(KeyConfig keyConfig, Runnable onStop, Runnable onPause) {
        this.lifeController = new LifeController(keyConfig, onStop, onPause);
    }

    public void run() {
        enter();
        try {
            action();
        } catch (FoxClickerException e) {
            trace("Interruption reason: " + e.getMessage());
            System.out.println("Interruption reason: " + e.getMessage());
            if (!(e instanceof InterruptBotException)) throw exception(e);
        } catch (RuntimeException e) {
            throw exception(e);
        } finally {
            lifeController.close();
        }
        exit();
    }

    protected abstract void action();

    protected void sleep(double seconds) {
        enter(seconds);
        lifeController.sleep((int) (seconds * 1000));
        exit();
    }

    protected Frame createFrame(Context context,
                                double timeLimitInSeconds,
                                double tolerance,
                                boolean inColor) {
        return new Frame(lifeController, context, timeLimitInSeconds, tolerance, inColor);
    }
}
