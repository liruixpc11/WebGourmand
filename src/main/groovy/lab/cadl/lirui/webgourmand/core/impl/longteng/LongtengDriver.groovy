package lab.cadl.lirui.webgourmand.core.impl.longteng

import lab.cadl.lirui.webgourmand.core.ContentFetcher
import lab.cadl.lirui.webgourmand.core.Driver
import lab.cadl.lirui.webgourmand.core.HttpOptions

/**
 * Created on 2014/8/14.
 */
class LongtengDriver implements Driver {
    ContentFetcher contentFetcher

    void start() {
        contentFetcher.fetch("http://www.long44.com/modules/article/index.php".toURL(),
                null,
                HttpOptions.gbk(),
                new BookListAnalyzer()
        )
    }
}
