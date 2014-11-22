package lab.cadl.lirui.webgourmand.core.impl.cnporn8

import groovy.util.slurpersupport.GPathResult
import lab.cadl.lirui.webgourmand.core.AbstractConsumer
import lab.cadl.lirui.webgourmand.core.BaseUrlAware
import lab.cadl.lirui.webgourmand.core.HttpConsumer
import lab.cadl.lirui.webgourmand.core.impl.FileSaver
import lab.cadl.lirui.webgourmand.core.impl.Utils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.file.Paths

/**
 * Created on 2014/9/3.
 */
class PictureListAnalyzer extends AbstractConsumer<GPathResult> implements HttpConsumer, BaseUrlAware {
    private static final Logger logger = LoggerFactory.getLogger(PictureListAnalyzer.class)
    URL baseUrl
    PictureListInfo pictureListInfo

    PictureListAnalyzer(PictureListInfo pictureListInfo) {
        this.pictureListInfo = pictureListInfo
    }

    @Override
    void consume(GPathResult root) {
        logger.info("analyzing {}", baseUrl)
        root.'**'.find {it.@id == 'read_tpc'}.'**'.findAll{ it.name() == 'img' }.each {
            def url = it.@src.toString().toURL()
            contentFetcher.fetch(url, null, new FileSaver(
                    targetFile: Paths.get("pictures", "cnporn8", Utils.filterSpecialPathChar(pictureListInfo.title), Utils.filterSpecialPathChar(url.file)).toFile()
            ))
        }
    }
}
