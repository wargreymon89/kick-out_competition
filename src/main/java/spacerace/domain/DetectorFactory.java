package spacerace.domain;

import java.util.ArrayList;
import java.util.List;

import spacerace.level.Level;
import spacerace.level.ShipGraphics;

import static java.util.stream.Collectors.toList;

public class DetectorFactory {

    private final int DETECTOR_BEAM_MAX_LENGTH = 100;

    private final int                        shipPosX;
    private final int                        shipPosY;
    private final int                        shipHeight;
    private final int                        shipWidth;
    private final List<java.awt.geom.Line2D> trackBorders;

    public DetectorFactory(final ShipState shipState, final ShipGraphics shipGraphics, final Level level) {
        this.shipPosX = (int) shipState.getPosition().getX();
        this.shipPosY = (int) shipState.getPosition().getY();
        this.shipHeight = (int) shipGraphics.getHeight();
        this.shipWidth = (int) shipGraphics.getWidth();
        this.trackBorders = level.getTrackBorders().stream()
                .map(Line2D::convertToAWTLine2D)
                .collect(toList());
    }

    public List<Detector> getDetectors() {
        // Left side
        final Line2D leftLine1 = new Line2D(shipPosX, shipPosY, shipPosX - DETECTOR_BEAM_MAX_LENGTH, shipPosY);
        final Line2D leftLine2 = new Line2D(shipPosX, (shipPosY + shipHeight / 2), shipPosX - DETECTOR_BEAM_MAX_LENGTH, (shipPosY + shipHeight / 2));
        final Line2D leftLine3 = new Line2D(shipPosX, (shipPosY + shipHeight), shipPosX - DETECTOR_BEAM_MAX_LENGTH, (shipPosY + shipHeight));

        // Right side
        final Line2D rightLine1 = new Line2D(shipPosX + shipWidth, shipPosY, shipPosX + shipWidth + DETECTOR_BEAM_MAX_LENGTH, shipPosY);
        final Line2D rightLine2 = new Line2D(shipPosX + shipWidth, (shipPosY + shipHeight / 2), shipPosX + shipWidth + DETECTOR_BEAM_MAX_LENGTH, (shipPosY + shipHeight / 2));
        final Line2D rightLine3 = new Line2D(shipPosX + shipWidth, (shipPosY + shipHeight), shipPosX + shipWidth + DETECTOR_BEAM_MAX_LENGTH, (shipPosY + shipHeight));

        // Upper side
        final Line2D upperLine1 = new Line2D(shipPosX, shipPosY, shipPosX, shipPosY - DETECTOR_BEAM_MAX_LENGTH);
        final Line2D upperLine2 = new Line2D((shipPosX + shipWidth / 2), shipPosY, (shipPosX + shipWidth / 2), shipPosY - DETECTOR_BEAM_MAX_LENGTH);
        final Line2D upperLine3 = new Line2D((shipPosX + shipWidth), shipPosY, (shipPosX + shipWidth), shipPosY - DETECTOR_BEAM_MAX_LENGTH);

        // Lower side
        final Line2D lowerLine1 = new Line2D(shipPosX, shipPosY + shipHeight, shipPosX, shipPosY + shipHeight + DETECTOR_BEAM_MAX_LENGTH);
        final Line2D lowerLine2 = new Line2D((shipPosX + shipWidth / 2), shipPosY + shipHeight, (shipPosX + shipWidth / 2), shipPosY + shipHeight + DETECTOR_BEAM_MAX_LENGTH);
        final Line2D lowerLine3 = new Line2D((shipPosX + shipWidth), shipPosY + shipHeight, (shipPosX + shipWidth), shipPosY + shipHeight + DETECTOR_BEAM_MAX_LENGTH);


        final Detector leftBeam1 = getDetectorBeam(leftLine1, DetectorPosition.LEFT, 1);
        final Detector leftBeam2 = getDetectorBeam(leftLine2, DetectorPosition.LEFT, 2);
        final Detector leftBeam3 = getDetectorBeam(leftLine3, DetectorPosition.LEFT, 3);

        final Detector rightBeam1 = getDetectorBeam(rightLine1, DetectorPosition.RIGHT, 1);
        final Detector rightBeam2 = getDetectorBeam(rightLine2, DetectorPosition.RIGHT, 2);
        final Detector rightBeam3 = getDetectorBeam(rightLine3, DetectorPosition.RIGHT, 3);

        final Detector upperBeam1 = getDetectorBeam(upperLine1, DetectorPosition.UP, 1);
        final Detector upperBeam2 = getDetectorBeam(upperLine2, DetectorPosition.UP, 2);
        final Detector upperBeam3 = getDetectorBeam(upperLine3, DetectorPosition.UP, 3);

        final Detector lowerBeam1 = getDetectorBeam(lowerLine1, DetectorPosition.DOWN, 1);
        final Detector lowerBeam2 = getDetectorBeam(lowerLine2, DetectorPosition.DOWN, 2);
        final Detector lowerBeam3 = getDetectorBeam(lowerLine3, DetectorPosition.DOWN, 3);

        final List<Detector> beams = new ArrayList<>();
        beams.add(leftBeam1);
        beams.add(leftBeam2);
        beams.add(leftBeam3);
        beams.add(rightBeam1);
        beams.add(rightBeam2);
        beams.add(rightBeam3);
        beams.add(upperBeam1);
        beams.add(upperBeam2);
        beams.add(upperBeam3);
        beams.add(lowerBeam1);
        beams.add(lowerBeam2);
        beams.add(lowerBeam3);

        return beams;
    }

    private Detector getDetectorBeam(final Line2D maxBeam, final DetectorPosition detectorPosition, final int detectorNumber) {
        final java.awt.geom.Line2D awtLine2D          = maxBeam.convertToAWTLine2D();
        final Integer              detectorBeamLength = getDetectorBeamLength(awtLine2D, detectorPosition);

        if (detectorBeamLength == null) {
            return new Detector(detectorPosition, detectorNumber, maxBeam.getX1(), maxBeam.getY1(), null);
        }

        return createDetectorBeam(maxBeam, detectorPosition, detectorBeamLength, detectorNumber);
    }

    private Integer getDetectorBeamLength(final java.awt.geom.Line2D awtLine2D, final DetectorPosition detectorPosition) {
        Integer detectorBeamLength = null;

        for (final java.awt.geom.Line2D line : trackBorders) {
            if (line.intersectsLine(awtLine2D)) {

                final int distanceDetectorToRectangle = getDetectorToTrackBoundDistance(detectorPosition, line);

                if (detectorBeamLength == null || distanceDetectorToRectangle < detectorBeamLength) {
                    detectorBeamLength = distanceDetectorToRectangle;
                }
            }
        }
        return detectorBeamLength;
    }

    private int getDetectorToTrackBoundDistance(final DetectorPosition detectorPosition, final java.awt.geom.Line2D line) {
        if (detectorPosition == DetectorPosition.UP) {
            return shipPosY - ((int) line.getY1());
        }
        else if (detectorPosition == DetectorPosition.DOWN) {
            return (int) line.getY1() - (shipPosY + shipHeight);
        }
        else if (detectorPosition == DetectorPosition.LEFT) {
            return shipPosX - ((int) line.getX1());
        }
        else {
            return (int) line.getX1() - (shipPosX + shipWidth);
        }
    }

    private Detector createDetectorBeam(final Line2D maxBeam, final DetectorPosition detectorPosition, final Integer detectorBeamLength, final int detectorNumber) {
        if (detectorPosition == DetectorPosition.UP) {
            final Line2D beam = new Line2D(maxBeam.getX1(), maxBeam.getY1(), maxBeam.getX1(), maxBeam.getY1() - detectorBeamLength);
            return new Detector(DetectorPosition.UP, detectorNumber, maxBeam.getX1(), maxBeam.getY1(), beam);
        }
        else if (detectorPosition == DetectorPosition.DOWN) {
            final Line2D beam = new Line2D(maxBeam.getX1(), maxBeam.getY1(), maxBeam.getX1(), maxBeam.getY1() + detectorBeamLength);
            return new Detector(DetectorPosition.DOWN, detectorNumber, maxBeam.getX1(), maxBeam.getY1(), beam);
        }
        else if (detectorPosition == DetectorPosition.LEFT) {
            final Line2D beam = new Line2D(maxBeam.getX1(), maxBeam.getY1(), maxBeam.getX1() - detectorBeamLength, maxBeam.getY1());
            return new Detector(DetectorPosition.LEFT, detectorNumber, maxBeam.getX1(), maxBeam.getY1(), beam);
        }
        else {
            final Line2D beam = new Line2D(maxBeam.getX1(), maxBeam.getY1(), maxBeam.getX1() + detectorBeamLength, maxBeam.getY1());
            return new Detector(DetectorPosition.RIGHT, detectorNumber, maxBeam.getX1(), maxBeam.getY1(), beam);
        }
    }
}
