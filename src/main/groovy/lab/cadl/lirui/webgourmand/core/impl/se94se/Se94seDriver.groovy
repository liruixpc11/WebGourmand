package lab.cadl.lirui.webgourmand.core.impl.se94se

import lab.cadl.lirui.webgourmand.core.ContentFetcher
import lab.cadl.lirui.webgourmand.core.Driver
import lab.cadl.lirui.webgourmand.core.HttpOptions

/**
 * Created on 2014/8/19.
 */
class Se94seDriver implements Driver {
    ContentFetcher contentFetcher

    void start() {
        contentFetcher.fetch("http://www.93oxo.com/sextu/zipaitoupai/".toURL(),
                null,
                HttpOptions.gbk(),
                new PictureBookListAnalyzer()
        )
    }
}
