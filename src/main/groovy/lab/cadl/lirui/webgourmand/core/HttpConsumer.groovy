package lab.cadl.lirui.webgourmand.core

import groovy.util.slurpersupport.GPathResult

/**
 * Created on 2014/8/12.
 */
interface HttpConsumer extends BaseConsumer<GPathResult> {
    void consume(GPathResult root)
}
