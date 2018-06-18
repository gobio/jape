package eu.gobio.jape.rxjava;

import eu.gobio.jape.Stage;
import io.reactivex.Observer;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Subscription extends AbstractStep<Observer, Subscription> {
    private Assembly assembly;
    private ConcurrentLinkedQueue<Stage> flow = new ConcurrentLinkedQueue<>();

    public Subscription(Observer referent, Assembly assembly, Subscription downstream) {
        super(referent);
        if (downstream != null) {
            composeWithDownstream(downstream);
        }
        this.assembly = assembly;
    }

    public Assembly getAssembly() {
        return assembly;
    }

    public Stage getFlow() {
        return flow.poll();
    }

    public void setFlow(Stage flow) {
        this.flow.add(flow);
    }
}
