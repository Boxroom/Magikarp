package Dhimulate;

import javafx.animation.AnimationTimer;
import simulation.Simulation;

/**
 * @author nilsw
 */
public class SimTimer extends AnimationTimer {
    private Simulation sim;

    public SimTimer(final Simulation sim) {
        this.sim = sim;
    }

    @Override
    public void handle(final long now) {
        sim.handle(now);
    }

    @Override
    public void start() {
        super.start();
        sim.lastnano = System.nanoTime();
        sim.running = true;
    }

    @Override
    public void stop() {
        super.stop();
        sim.running = false;
    }
}
