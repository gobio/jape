package eu.gobio.jape.rxjava;

import eu.gobio.jape.Stage;
import eu.gobio.jape.utils.Utils;

import java.lang.ref.WeakReference;

public class AbstractStep<T, S extends AbstractStep> {
    private final WeakReference<T> reference;
    private AbstractStep upstream;
    private AbstractStep downstream;
    private Stage stage;

    public AbstractStep(T referent) {
        this.reference = new WeakReference<>(referent);
    }

    protected void composeWithUpstream(S upstream){
        this.upstream = upstream;
        upstream.setDownstream(this);
    }

    protected void composeWithDownstream(S downstream){
        this.downstream = downstream;
        downstream.setUpstream(this);
    }

    public S getDownstream() {
        return (S) downstream;
    }

    protected void setDownstream(AbstractStep downstream) {
        this.downstream = downstream;
    }

    public S getUptream() {
        return (S) downstream;
    }

    protected void setUpstream(AbstractStep upstream) {
        this.upstream = upstream;
    }

    public T getReference() {
        return reference.get();
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public S head(){
        if (upstream==null){
            return (S)this;
        } else{
            return (S)upstream.head();
        }
    }

    public String printAll(){
        return head().printDownstream();
    }

    public String printUpstream() {
        if (upstream == null) {
            return "* " + Utils.objectCode(reference.get());
        } else {
            return upstream.printUpstream() + " -> " + Utils.objectCode(reference.get());
        }
    }

    public String printDownstream() {
        if (downstream == null) {
            return Utils.objectCode(reference.get());
        } else {
            return Utils.objectCode(reference.get()) + " -> " + downstream.printDownstream();
        }
    }

    public boolean isFirst() {
        return upstream == null;
    }

    public boolean isLast() {
        return downstream == null;
    }
}
