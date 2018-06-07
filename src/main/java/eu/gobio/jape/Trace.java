package eu.gobio.jape;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Trace {
    private static Logger traceLogger = LoggerFactory.getLogger("jape.trace");
    private static ThreadLocal<Stage> currentStage = new ThreadLocal<>();
    private static ThreadLocal<Stage> externalStage = new ThreadLocal<>();


    private String id;
    private AtomicInteger tsn;
    private List<Stage> stages;
    private long startTime;

    public Trace() {
        this.id = UUID.randomUUID().toString();
        this.tsn = new AtomicInteger();
        this.stages = new CopyOnWriteArrayList<>();
        this.startTime = System.currentTimeMillis();
        log();
    }

    private void log() {
        traceLogger.info("{\"id\":\"{}\",\"start\":{}}", id, startTime);
    }

    public static boolean inTransaction() {
        return Trace.currentStage.get() != null;
    }

    public static Stage currentStage() {
        return currentStage.get();
    }

    public static void finishStage() {
        Stage currentStage = Trace.currentStage.get();
        currentStage.finish();

        Stage parent = currentStage.getParent();
        if (parent!=null && parent.equals(Trace.externalStage.get())) {
            setCurrentStage(null);
        } else{
            setCurrentStage(parent);
        }
    }

    public static void setCurrentStage(Stage currentStage) {
        Trace.currentStage.set(currentStage);
    }

    public static void setExternalStage(Stage currentRoot) {
        externalStage.set(currentRoot);
    }

    public static Stage startStage(String signature) {
        Stage currentStage = Trace.currentStage.get();
        Stage externalStage = Trace.externalStage.get();

        if (currentStage != null) {
            currentStage = new Stage(currentStage);
        } else if (externalStage != null) {
            currentStage = new Stage(externalStage);
        } else {
            currentStage = new Stage();
        }
        currentStage.start(signature);

        setCurrentStage(currentStage);
        return currentStage;
    }


    public int getSequenceNumber() {
        return tsn.incrementAndGet();
    }

    public void addStage(Stage stage) {
        stages.add(stage);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trace)) return false;
        Trace that = (Trace) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        stages.sort(Comparator.comparing(Stage::getStartTime));
        stages.forEach(ts -> result.append(ts + " -> " + ts.getParent()));
        return result.toString();
    }
}
