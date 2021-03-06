package lab.cadl.lirui.webgourmand.core

/**
 * Created on 2014/8/12.
 */
public interface ContentFetcher {
    void shutdown()
    ContentFetcher drive(Driver driver)
    ContentFetcher setProxy(String host, int port, String scheme)
    void fetch(URL url, String tag, HttpOptions options, HttpConsumer consumer)
    void fetch(URL url, String tag, TextOptions options, TextConsumer consumer)
    void fetch(URL url, String tag, StreamConsumer consumer)
    void fetch(URL url, String tag, CommonOptions options, StreamConsumer consumer)
}