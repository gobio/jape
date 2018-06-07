package eu.gobio.jape;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ObserverWrapper<T> implements Observer<T> {
    private final Observer<T> source;
    private final Stage stage;

    public ObserverWrapper(Observer<T> source, Stage stage) {
        this.source = source;
        this.stage = stage;
    }

    @Override
    public void onSubscribe(Disposable d) {
        source.onSubscribe(d);
    }

    @Override
    public void onNext(T t) {
        if (Trace.inTransaction()) {
            source.onNext(t);
        } else {
            Trace.setExternalStage(stage);
            try {
                source.onNext(t);
            } finally {
                Trace.setExternalStage(null);
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        source.onError(e);
    }

    @Override
    public void onComplete() {
        source.onComplete();

    }
}
