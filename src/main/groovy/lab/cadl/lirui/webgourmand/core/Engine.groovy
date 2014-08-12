package lab.cadl.lirui.webgourmand.core

import lab.cadl.lirui.webgourmand.core.impl.ContentFetcherImpl

/**
 * Created on 2014/8/12.
 */
@Singleton
class Engine {
    private ContentFetcher contentFetcher = new ContentFetcherImpl()

    public ContentFetcher getContentFetcher() {
        return contentFetcher
    }
}
