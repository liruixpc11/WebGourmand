package lab.cadl.lirui.webgourmand.core.impl.longteng

import groovy.util.slurpersupport.GPathResult
import lab.cadl.lirui.webgourmand.core.AbstractConsumer
import lab.cadl.lirui.webgourmand.core.BaseUrlAware
import lab.cadl.lirui.webgourmand.core.HttpConsumer
import lab.cadl.lirui.webgourmand.core.TextConsumer
import lab.cadl.lirui.webgourmand.core.impl.AbstractErrorHandler
import lab.cadl.lirui.webgourmand.core.impl.TextFileSaver
import lab.cadl.lirui.webgourmand.core.impl.Utils
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
                    title: it.td[1].toString(),
                    size: it.td[2].toString(),
                    updateDate: new SimpleDateFormat("yyyy-MM-dd").parse(it.td[3].toString()),
                    downloadUrl: Utils.formatUrl(baseUrl, it.td[4].a.@href.toString())
            )
            contentFetcher.fetch(chapter.downloadUrl, null, new TextFileSaver(
                    targetFile: Paths.get(
                            "longteng",
                            Utils.filterSpecialPathChar(sprintf("%04d-%s", book.id, book.title)),
                            Utils.filterSpecialPathChar(sprintf("%04d-%s", chapter.index, chapter.title))
                    ).toFile()
            ))
        }
    }
}
