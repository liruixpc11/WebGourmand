package lab.cadl.lirui.webgourmand.core.impl

import lab.cadl.lirui.webgourmand.core.AbstractConsumer
import lab.cadl.lirui.webgourmand.core.BaseUrlAware
import lab.cadl.lirui.webgourmand.core.StreamConsumer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Created on 2014/8/12.
 */
class FileSaver extends AbstractConsumer implements StreamConsumer, BaseUrlAware {
    private static final Logger logger = LoggerFactory.getLogger(FileSaver)

    URL baseUrl
    File targetFile
    boolean overwrite = false

    @Override
    boolean preRequest() {
        return overwrite || !targetFile.exists()
    }

    @Override
    void consume(InputStream inputStream) {
        logger.info("save {} to {}", baseUrl, targetFile)
        Files.createDirectories(targetFile.toPath().parent)
        File tmpFile = new File(targetFile.toString() + ".download")
        tmpFile.withOutputStream { it << inputStream }
        Files.move(tmpFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
    }
}
