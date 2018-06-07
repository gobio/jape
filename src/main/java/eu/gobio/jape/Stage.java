package eu.gobio.jape;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;

public class Stage {

    private static Logger startLogger = LoggerFactory.getLogger("jape.stage.start");
    private static Logger finishLogger = LoggerFactory.getLogger("jape.stage.end");

    private final Trace trace;
    private final int stageId;
    private long startTime;
    private long finishTime;
    private String name;
    private String threadName;
    private Stage parent;
    private int level = 0;


    public Stage() {
        this(new Trace());
    }

    public Stage(Trace trace) {
        this.trace = trace;
        this.stageId = trace.getSequenceNumber();
        this.trace.addStage(this);
    }

    public Stage(Stage parent) {
        this(parent.trace);
        this.parent = parent;
        this.level = parent != null ? parent.level + 1 : 0;
    }

    public void start(String name) {
        this.startTime = System.currentTimeMillis();
        this.threadName = Thread.currentThread().getName();
        this.name = name;

        logStart();
    }

    public void logStart() {
        startLogger.info("{\"trace\":\"{}\", \"stage\":{},\"parent\":{},\"start\":{},\"name\":\"{}\"," +
                        "\"thread\":\"{}\"}",
                trace.getId(),
                stageId,
                parent != null ? parent.stageId : null,
                startTime,
                name,
                threadName);
    }

    public long getStartTime() {
        return startTime;
    }

    public Stage getParent() {
        return parent;
    }

    @Override
    public String toString() {
        char[] tabs = new char[level];
        Arrays.fill(tabs, '\t');
        return String.format("%s%s [%s] (%d - %d)", new String(tabs), name, threadName, startTime,
                finishTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stage)) return false;
        Stage that = (Stage) o;
        return stageId == that.stageId &&
                Objects.equals(trace, that.trace);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trace, stageId);
    }

    void finish() {
        this.finishTime = System.currentTimeMillis();
        logEnd();
    }

    private void logEnd() {
        finishLogger.info("{\"trace\":\"{}\", \"stage\":{},\"end\":{}}", trace.getId(), stageId, finishTime);
    }
}
