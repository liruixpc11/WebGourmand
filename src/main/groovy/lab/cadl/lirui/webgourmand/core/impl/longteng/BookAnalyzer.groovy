package lab.cadl.lirui.webgourmand.core.impl.longteng

import groovy.util.slurpersupport.GPathResult
import lab.cadl.lirui.webgourmand.core.AbstractConsumer
import lab.cadl.lirui.webgourmand.core.BaseUrlAware
import lab.cadl.lirui.webgourmand.core.HttpConsumer
import lab.cadl.lirui.webgourmand.core.HttpOptions
import lab.cadl.lirui.webgourmand.core.impl.Utils
import lab.cadl.lirui.webgourmand.core.impl.common.Book
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created on 2014/8/12.
 */
class BookAnalyzer extends AbstractConsumer implements HttpConsumer, BaseUrlAware {
    private final static Logger logger = LoggerFactory.getLogger(BookAnalyzer.class)

    URL baseUrl
    Book book

    @Override
    void consume(GPathResult root) {
        logger.info("analyze book [{}-{}] home page", book.id, book.title)
        String chapterUrl = root.'**'.find { it == 'TXT 单章' && it.@href}?.@href?.toString()
        contentFetcher.fetch(Utils.formatUrl(baseUrl, chapterUrl), null, HttpOptions.gbk(), new ChapterListAnalyzer(book: book))
    }
}
