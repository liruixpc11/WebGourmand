package lab.cadl.lirui.webgourmand.core.impl

import lab.cadl.lirui.webgourmand.core.AbstractConsumer
import lab.cadl.lirui.webgourmand.core.BaseUrlAware
import lab.cadl.lirui.webgourmand.core.StreamConsumer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.file.Files

/**
 * Created on 2014/8/12.
 */
class TextFileSaver extends AbstractConsumer implements StreamConsumer, BaseUrlAware {
    private static final Logger logger = LoggerFactory.getLogger(TextFileSaver)

    URL baseUrl
    File targetFile

    @Override
    void consume(InputStream inputStream) {
        logger.info("save {} to {}", baseUrl, targetFile)
        Files.createDirectories(targetFile.toPath().parent)
        targetFile.withOutputStream { it << inputStream }
    }
}
