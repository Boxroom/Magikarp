package main;

import javafx.animation.AnimationTimer;
import simulation.Simulation;

/**
 * @author nilsw
 */
public class SimTimer extends AnimationTimer {

    private final Simulation sim;
    private       long       before;
    private boolean running = false;

    private int  frames;
    private long firstFrame, currentFrame;

    SimTimer(final Simulation sim) {
        this.sim = sim;
    }

    @Override
    public void handle(final long now) {
        //measureAndPrintFPS();

        final long elapsed = now - before;
        before = now;
        sim.handle(elapsed);
    }

    private void measureAndPrintFPS() {
        ++frames;
        currentFrame = System.currentTimeMillis();
        if (currentFrame >= firstFrame + 1000) {
            System.out.println(frames + " FPS");
            firstFrame = currentFrame;
            frames = 0;
        }
    }

    @Override
    public void start() {
        firstFrame = System.currentTimeMillis();

        before = System.nanoTime();
        super.start();
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
