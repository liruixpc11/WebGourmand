package lab.cadl.lirui.webgourmand.core.impl.cnporn8

import groovy.util.slurpersupport.GPathResult
import lab.cadl.lirui.webgourmand.core.AbstractConsumer
import lab.cadl.lirui.webgourmand.core.BaseConsumer
import lab.cadl.lirui.webgourmand.core.HttpConsumer
import lab.cadl.lirui.webgourmand.core.HttpOptions
import lab.cadl.lirui.webgourmand.core.UrlProvider

/**
 * Created on 2014/8/13.
 */
class Login extends AbstractConsumer<GPathResult> implements HttpConsumer {
    def consumerList = []

    @Override
    void consume(GPathResult root) {
        consumerList.each {
            if (it instanceof List && it.size() == 2 && it[1] instanceof HttpConsumer) {
                contentFetcher.fetch(new URL(it[0] as String), null, HttpOptions.utf8(), (HttpConsumer) it[1])
            } else if (it instanceof UrlProvider && it instanceof HttpConsumer) {
                contentFetcher.fetch(it.url, null, HttpOptions.utf8(), it)
            } else {
                throw new Exception("列表中只能包含[url, consumer]或者<T extends UrlProvider && HttpConsumer>")
            }
        }
    }
}
