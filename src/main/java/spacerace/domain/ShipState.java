package spacerace.domain;

import java.awt.Color;
import java.io.Serializable;

public class ShipState implements Serializable {

    private String   name;
    private Vector2D position;
    private Vector2D speed;
    private Vector2D accelerationDirection;
    private boolean  stabilize;
    private Color    color;

    public ShipState() {
        // For JSON conversion
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(final Vector2D position) {
        this.position = position;
    }

    public Vector2D getSpeed() {
        return speed;
    }

    public void setSpeed(final Vector2D speed) {
        this.speed = speed;
    }

    public Vector2D getAccelerationDirection() {
        return accelerationDirection;
    }

    public void setAccelerationDirection(final Vector2D accelerationDirection) {
        this.accelerationDirection = accelerationDirection;
    }

    public boolean isStabilize() {
        return stabilize;
    }

    public void setStabilize(final boolean stabilize) {
        this.stabilize = stabilize;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(final Color color) {
        this.color = color;
    }
}