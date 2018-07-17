package se.mah.af2015.p4;

/**
 * OnMovementChangeListener interface provides us with a method for updating values in Activity after SensorEvents are triggered in our Service
 *
 * @author Alexander Johansson (AF2015).
 */
public interface OnMovementChangeListener {

    /**
     * Method is sent upon triggering a sensor event
     */
    void update();
}
