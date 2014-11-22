package lab.cadl.lirui.webgourmand.core.impl._775hh

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
        root.'**'.find { it.@class.toString() == 'box1' }.'**'.grep { it.name() == 'img' }.eachWithIndex { it, index ->
            def url = Utils.formatUrl(baseUrl, it.@src.toString())
            contentFetcher.fetch(url, null, new FileSaver(
                    targetFile: Paths.get("pictures", "775hh", Utils.filterSpecialPathChar(pictureList.section), Utils.filterSpecialPathChar(pictureList.title), index.toString() + "." + url.file.toString().substring(url.file.toString().length() - 3)).toFile()
            ))
        }
    }
}
