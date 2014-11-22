package lab.cadl.lirui.webgourmand.core.impl.se94se

import groovy.util.slurpersupport.GPathResult
import lab.cadl.lirui.webgourmand.core.*
import lab.cadl.lirui.webgourmand.core.impl.FileSaver
import lab.cadl.lirui.webgourmand.core.impl.Utils
import lab.cadl.lirui.webgourmand.core.impl.common.PictureList

import java.nio.file.Paths

/**
 * Created on 2014/8/19.
 */
class PictureListAnalyzer extends AbstractConsumer implements HttpConsumer, BaseUrlAware, FetcherAware {
    URL baseUrl
    ContentFetcher contentFetcher
    PictureList pictureList

    PictureListAnalyzer(PictureList pictureList) {
        this.pictureList = pictureList
    }

    @Override
    void consume(GPathResult root) {
        root.'**'.find { it.@class.toString() == 'content' }.'**'.grep { it.name() == 'img' }.each {
            def url = Utils.formatUrl(baseUrl, it.@src.toString())
            contentFetcher.fetch(url, null, new FileSaver(
                    targetFile: Paths.get("pictures", "se94se", Utils.filterSpecialPathChar(pictureList.title), Utils.filterSpecialPathChar(url.file)).toFile()
            ))
        }
    }
}
