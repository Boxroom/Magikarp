package Dhimulate;

import javafx.animation.AnimationTimer;
import simulation.Simulation;

/**
 * @author nilsw
 */
public class SimTimer extends AnimationTimer {

    private Simulation sim;
    private long    before  = 0;
    private boolean running = false;

    SimTimer(final Simulation sim) {
        this.sim = sim;
    }

    @Override
    public void handle(final long now) {
        long elapsed = now - before;
        before = now;
        sim.handle(elapsed);
    }

    @Override
    public void start() {
        super.start();
        before = System.nanoTime();
        running = true;
    }

    @Override
    public void stop() {
        super.stop();
        running = false;
    }

    public boolean isRunning() {
        return running;
    }
}
