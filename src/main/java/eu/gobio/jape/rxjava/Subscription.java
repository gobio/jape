package eu.gobio.jape.rxjava;

import eu.gobio.jape.Stage;
import io.reactivex.Observer;

public class Subscription extends AbstractStep<Observer, Subscription> {
    private Assembly assembly;
    private Stage flow;

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
        return flow;
    }

    public void setFlow(Stage flow) {
        this.flow = flow;
    }
}
