package lab.cadl.lirui.webgourmand.core.impl.longteng

import groovy.util.slurpersupport.GPathResult
import lab.cadl.lirui.webgourmand.core.AbstractConsumer
import lab.cadl.lirui.webgourmand.core.BaseUrlAware
import lab.cadl.lirui.webgourmand.core.HttpConsumer
import lab.cadl.lirui.webgourmand.core.HttpOptions
import lab.cadl.lirui.webgourmand.core.impl.Utils
import lab.cadl.lirui.webgourmand.core.impl.common.Book
import lab.cadl.lirui.webgourmand.core.impl.common.BookState
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.text.SimpleDateFormat

/**
 * Created on 2014/8/12.
 */
class BookListAnalyzer extends AbstractConsumer implements HttpConsumer, BaseUrlAware {
    private static final Logger logger = LoggerFactory.getLogger(BookListAnalyzer.class)
    URL baseUrl

    @Override
    void consume(GPathResult root) {
        logger.info("analyze book list [{}]", baseUrl)
        root.'**'.find { it.@id == 'lbox' }.ul.each {
            def book = new Book(
                    title: it.li[0].toString().trim(),
                    url: it.li[0].a.@href.toString(),
                    author: it.li[1].toString(),
                    lastChapterTitle: it.li[2].toString(),
                    words: Integer.parseInt(it.li[3].toString()),
                    updateDate: new SimpleDateFormat("yyyy-MM-dd").parse("20" + it.li[4].toString()),
                    state: BookState.parse(it.li[5].toString())
            )

            book.id = Integer.parseInt(book.url.substring(book.url.lastIndexOf("=") + 1))
            contentFetcher.fetch(formatUrl(book.url), null, HttpOptions.gbk(), new BookAnalyzer(book: book))
        }

        String nextUrl = root.'**'.find { it.@class == 'next' && it.@href }?.@href
        if (nextUrl) {
            contentFetcher.fetch(formatUrl(nextUrl), null, HttpOptions.gbk(), this)
        }
    }

    private URL formatUrl(String url) {
        Utils.formatUrl(baseUrl, url)
    }
}
