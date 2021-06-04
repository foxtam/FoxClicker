package net.foxtam.foxclicker;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Screen {
    
    public Image getCapture(Rectangle rectangle) {
        BufferedImage screenCapture = BotRobot.INSTANCE.createScreenCapture(rectangle);
        
    }
}
