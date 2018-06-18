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

    private final String id;
    private final AtomicInteger tsn;
    private final List<Stage> stages;
    private final long startTime;
    private final long wallStartTime;

    public Trace() {
        this.id = UUID.randomUUID().toString();
        this.tsn = new AtomicInteger();
        this.stages = new CopyOnWriteArrayList<>();
        this.startTime = System.nanoTime();
        this.wallStartTime = System.currentTimeMillis();
        log();
    }

    private void log() {
        traceLogger.info("{\"id\":\"{}\",\"start\":{},\"wallTime\":{}}", id, startTime,wallStartTime);
    }

    public static boolean inTransaction() {
        return Trace.currentStage.get() != null;
    }

    public static Stage finishStage(Stage stage) {
        if (stage.equals(Trace.currentStage())) {
            return finishStage();
        } else {
            return null;
        }
    }

    public static Stage currentStage() {
        return currentStage.get();
    }

    public static Stage finishStage() {
        Stage currentStage = Trace.currentStage();
        currentStage.finish();

        Stage parent = currentStage.getParent();
        if (parent != null && parent.equals(Trace.externalStage.get())) {
            setCurrentStage(null);
        } else {
            setCurrentStage(parent);
        }
        return currentStage;
    }

    public static void setCurrentStage(Stage currentStage) {
        Trace.currentStage.set(currentStage);
    }

    public static void setExternalStage(Stage currentRoot) {
        externalStage.set(currentRoot);
    }

    public static Stage startStage(String signature) {
        return Trace.startStage(signature, null);
    }

    public static Stage startStage(String signature, Flow flow) {
        Stage currentStage = createStage(flow);

        currentStage.start(signature);

        return currentStage;
    }

    private static Stage createStage(Flow flow) {
        Stage currentStage = Trace.currentStage.get();
        Stage externalStage = Trace.externalStage.get();

        if (currentStage != null) {
            if (!currentStage.isEphemeral()) {
                currentStage = new Stage(currentStage);
            }
        } else if (externalStage != null) {
            currentStage = new Stage(externalStage);
        } else {
            currentStage = new Stage();
        }
        if (flow != null) {
            currentStage.setFlow(flow);
        }
        setCurrentStage(currentStage);
        return currentStage;
    }

    public static Stage startEphemeralStage(String signature, Flow flow) {
        Stage currentStage = Trace.currentStage.get();
        if (currentStage != null && currentStage.isEphemeral()) {
            finishStage();
        }
        currentStage = createStage(flow);

        currentStage.startEphemeral(signature);

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
