package Source.Components;

public class GameTime {
    private final long durationTime = 1 * 60 * 1000;

    private long time;

    public GameTime() {
        time = System.currentTimeMillis();
    }

    public void startGameTime() {
        time = System.currentTimeMillis();
    }

    public boolean isFinish() {
        return System.currentTimeMillis() - time >= durationTime;
    }

    public long getGameTime() {
        return time;
    }

    public long getDurationTime() {
        return durationTime;
    }

    public void resetGameTime() {
        startGameTime();
    }
}
