package spacerace.level.graphics;

import java.awt.Color;
import java.awt.Graphics2D;

public class OrbitAnimation {

    private double sphereRelativeCenterX;
    private double angle = 0;
    private final Ellipse ellipse;
    private final Sphere  sphere;
    private final double  speed;

    private OrbitAnimation(final int ellipseCenterX, final int ellipseCenterY, final int a, final int b, final Color ellipseColor, final int sphereRadius, final Color sphereColor, final Color sphereShadowColor) {
        this.ellipse = new Ellipse(a, b, ellipseCenterX, ellipseCenterY, ellipseColor);
        this.sphere = new Sphere(sphereColor, sphereShadowColor, sphereRadius, ellipseCenterX + a, ellipseCenterY); // Start on right side of ellipse
        this.sphereRelativeCenterX = 1;
        this.speed = 0.01 / (a / 200d);
    }

    public static OrbitAnimationBuilder anOrbitAnimation() {
        return ellipseCenterX -> ellipseCenterY -> a -> b -> ellipseColor -> sphereRadius -> sphereColor -> sphereShadowColor -> () ->
                new OrbitAnimation(ellipseCenterX, ellipseCenterY, a, b, ellipseColor, sphereRadius, sphereColor, sphereShadowColor);
    }

    public interface OrbitAnimationBuilder {
        EllipseCenterXBuilder withCenterX(int ellipseCenterX);
    }

    public interface EllipseCenterXBuilder {
        EllipseCenterYBuilder withCenterY(int ellipseCenterY);
    }

    public interface EllipseCenterYBuilder {
        ABuilder withA(int a);
    }

    public interface ABuilder {
        BBuilder withB(int b);
    }

    public interface BBuilder {
        EllipseColorBuilder withEllipseColor(Color ellipseColor);
    }

    public interface EllipseColorBuilder {
        SphereRadiusBuilder withSphereRadius(int sphereRadius);
    }

    public interface SphereRadiusBuilder {
        SphereColorBuilder withSphereColor(Color sphereColor);
    }

    public interface SphereColorBuilder {
        SphereShadowColorBuilder withSphereShadowColor(Color sphereShadowColor);
    }

    public interface SphereShadowColorBuilder {
        OrbitAnimation build();
    }

    void paintUpperPart(final Graphics2D graphics) {
        ellipse.paintUpperPart(graphics);
        if (isSphereInUpperQuadrants()) {
            paintPlanet(graphics);
        }
    }

    void paintLowerPart(final Graphics2D graphics) {
        ellipse.paintLowerPart(graphics);
        if (!isSphereInUpperQuadrants()) {
            paintPlanet(graphics);
        }
    }

    private boolean isSphereInUpperQuadrants() {
        return sphere.getCenterY() <= ellipse.getCenterY();
    }

    private void paintPlanet(final Graphics2D graphics) {
        sphere.paint(graphics);

        //        final OrbitAnimation orbitAnimation = OrbitAnimation.anOrbitAnimation()
        //                .withCenterX(sphere.getCenterX())
        //                .withCenterY(sphere.getCenterY())
        //                .withA((int) (sphere.getRadius() * 1.5))
        //                .withB((int) (sphere.getRadius() * 0.5))
        //                .withEllipseColor(new Color(1, 1, 1, 0))
        //                .withSphereRadius(20)
        //                .withSphereColor(Color.PINK)
        //                .withSphereShadowColor(Color.BLACK)
        //                .build();
        //        orbitAnimation.paintUpperPart(graphics);
        //        orbitAnimation.paintLowerPart(graphics);
    }

    void tickAnimation() {
        if ((angle >= (2 * Math.PI))) {
            angle = 0;
        }
        angle += speed * Math.PI;
        sphereRelativeCenterX = Math.cos(angle) * ellipse.getA();

        final double sphereRelativeCenterY = Math.copySign(ellipse.calculateY(sphereRelativeCenterX), -Math.sin(angle));
        final double sphereAbsoluteCenterX = sphereRelativeCenterX + ellipse.getCenterX();
        final double sphereAbsoluteCenterY = sphereRelativeCenterY + ellipse.getCenterY();
        sphere.setCenterX((int) sphereAbsoluteCenterX);
        sphere.setCenterY((int) sphereAbsoluteCenterY);
    }
}
