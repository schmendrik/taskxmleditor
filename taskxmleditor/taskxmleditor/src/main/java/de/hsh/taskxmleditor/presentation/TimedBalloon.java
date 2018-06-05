package de.hsh.taskxmleditor.presentation;

import net.java.balloontip.BalloonTip;

import javax.swing.*;

public class TimedBalloon {
    public static final int SECONDS = 13;
    private BalloonTip b;

    public TimedBalloon(BalloonTip b) {
        this(b, SECONDS * 1000);
    }

    public TimedBalloon(BalloonTip b, int milliSeconds) {
        this.b = b;

        Timer timer = new Timer(milliSeconds, e2 -> b.closeBalloon());
        timer.setRepeats(false);
        timer.start();
    }

    public TimedBalloon(JComponent comp, String msg) {
        this(new BalloonTip(comp, msg));
    }

    public void closeBalloon() {
        b.closeBalloon();
    }
}
