package lab.cadl.lirui.webgourmand.core

import lab.cadl.lirui.webgourmand.core.impl.AbstractErrorHandler

/**
 * Created on 2014/8/12.
 */
abstract class AbstractConsumer <R> extends AbstractErrorHandler implements BaseConsumer<R> {
    ContentFetcher contentFetcher
}
