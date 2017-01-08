package spacerace.gameengine;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import spacerace.domain.Acceleration;
import spacerace.domain.Action;
import spacerace.domain.ShipState;

public class ManualGameEngine extends KeyAdapter implements SpaceRaceGameEngine {

    private volatile Acceleration accelerationX = Acceleration.NONE;
    private volatile Acceleration accelerationY = Acceleration.NONE;
    private volatile boolean      stabilize     = false;

    @Override
    public Action getAction(final ShipState shipState) {
        if (stabilize) {
            return Action.withStabilize(true);
        }
        else {
            return Action.withAcceleration(accelerationX, accelerationY);
        }
    }

    public void keyPressed(final KeyEvent event) {
        final int key = event.getKeyCode();

        if (key == KeyEvent.VK_SPACE) {
            stabilize = true;
        }
        else if (key == KeyEvent.VK_LEFT) {
            accelerationX = Acceleration.NEGATIVE;
        }
        else if (key == KeyEvent.VK_RIGHT) {
            accelerationX = Acceleration.POSITIVE;
        }
        else if (key == KeyEvent.VK_UP) {
            accelerationY = Acceleration.NEGATIVE;
        }
        else if (key == KeyEvent.VK_DOWN) {
            accelerationY = Acceleration.POSITIVE;
        }
        else {
            System.out.println("Unknown user input");
        }
    }

    public void keyReleased(final KeyEvent event) {
        final int    key = event.getKeyCode();
        final Action action;

        if (key == KeyEvent.VK_SPACE) {
            stabilize = false;
        }
        else if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) {
            accelerationX = Acceleration.NONE;
        }
        else if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN) {
            accelerationY = Acceleration.NONE;
        }
        else {
            System.out.println("Unknown user input");
        }
    }
}