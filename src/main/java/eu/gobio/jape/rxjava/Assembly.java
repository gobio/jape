package eu.gobio.jape.rxjava;

import io.reactivex.Observable;

public class Assembly extends AbstractStep<Observable,Assembly> {
    public Assembly(Observable referent, Assembly upstream) {
        super(referent);
        if (upstream!=null){
            composeWithUpstream(upstream);
        }
    }
}
