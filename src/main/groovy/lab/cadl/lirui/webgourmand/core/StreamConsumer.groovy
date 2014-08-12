package lab.cadl.lirui.webgourmand.core

/**
 * Created on 2014/8/12.
 */
public interface StreamConsumer extends BaseConsumer<InputStream> {
    void consume(InputStream inputStream)
}