package lab.cadl.lirui.webgourmand.core.impl

import groovy.util.slurpersupport.GPathResult
import lab.cadl.lirui.webgourmand.core.AbstractConsumer
import lab.cadl.lirui.webgourmand.core.BaseUrlAware
import lab.cadl.lirui.webgourmand.core.HttpConsumer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created on 2014/9/20.
 */
class HttpClosureConsumer  extends AbstractConsumer<GPathResult> implements HttpConsumer, BaseUrlAware {
    private static final Logger logger = LoggerFactory.getLogger(HttpClosureConsumer.class)
    URL baseUrl
    Closure closure

    HttpClosureConsumer(Closure closure) {
        this.closure = closure
    }

    @Override
    void consume(GPathResult result) {
        switch (closure.maximumNumberOfParameters) {
            case 0:
                closure()
                break
            case 1:
                closure(result)
                break
            case 2:
                closure(result, contentFetcher)
                break
            case 3:
                closure(result, contentFetcher, baseUrl)
                break
            default:
                throw new IllegalArgumentException("parameters count ${closure.maximumNumberOfParameters} more than 3")
        }
    }
}
