package eu.gobio.jape.tracker;

import eu.gobio.jape.ExplainerActivator;

import java.util.ServiceLoader;

public class AbstractTracker {
    static{
        ServiceLoader.load(ExplainerActivator.class).forEach(ExplainerActivator::activate);
    }
}
