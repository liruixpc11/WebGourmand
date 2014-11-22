package lab.cadl.lirui.webgourmand.core

/**
 * Created on 2014/8/12.
 */
public interface BaseConsumer <R> extends HttpErrorHandler, FetcherAware {
    boolean preRequest();
    void consume(R result)
}