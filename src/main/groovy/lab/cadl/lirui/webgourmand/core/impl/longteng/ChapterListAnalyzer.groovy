package lab.cadl.lirui.webgourmand.core.impl.longteng

import groovy.util.slurpersupport.GPathResult
import lab.cadl.lirui.webgourmand.core.AbstractConsumer
import lab.cadl.lirui.webgourmand.core.BaseUrlAware
import lab.cadl.lirui.webgourmand.core.HttpConsumer
import lab.cadl.lirui.webgourmand.core.impl.FileSaver
import lab.cadl.lirui.webgourmand.core.impl.Utils
import lab.cadl.lirui.webgourmand.core.impl.common.Book
import lab.cadl.lirui.webgourmand.core.impl.common.Chapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.file.Paths
import java.text.SimpleDateFormat

/**
 * Created on 2014/8/12.
 */
class ChapterListAnalyzer extends AbstractConsumer implements HttpConsumer, BaseUrlAware {
    private static final Logger logger = LoggerFactory.getLogger(ChapterListAnalyzer.class)

    URL baseUrl
    Book book

    @Override
    void consume(GPathResult root) {
        logger.info("analyze book [{}-{}] chapter list page", book.id, book.title)
        root.'**'.find { it.@id == "content" }.table.tr.grep { it.@align != "center" } .each {
            Chapter chapter = new Chapter(
                    book: book,
                    index: Integer.parseInt(it.td[0].toString()),
                    title: it.td[1].toString().trim(),
                    size: it.td[2].toString(),
                    updateDate: new SimpleDateFormat("yyyy-MM-dd").parse(it.td[3].toString()),
                    downloadUrl: Utils.formatUrl(baseUrl, it.td[4].a.@href.toString())
            )
            File targetFile = Paths.get(
                    "fiction",
                    "longteng",
                    Utils.filterSpecialPathChar(sprintf("%04d-%s", book.id, book.title)),
                    Utils.filterSpecialPathChar(sprintf("%04d-%s.txt", chapter.index, chapter.title))
            ).toFile()

            if (!targetFile.exists()) {
                logger.info("begin download chapter {}", chapter.s())
                contentFetcher.fetch(chapter.downloadUrl, null, new FileSaver(
                        targetFile: targetFile
                ))
            } else {
                logger.info("ignore chapter {} because of exists", chapter.s())
            }
        }
    }
}
