package lab.cadl.lirui.webgourmand.core.impl.longtengnet

import groovy.util.slurpersupport.GPathResult
import lab.cadl.lirui.webgourmand.core.HttpOptions
import lab.cadl.lirui.webgourmand.core.impl.SimpleModule
import lab.cadl.lirui.webgourmand.core.impl.Utils
import lab.cadl.lirui.webgourmand.core.impl.common.Book
import lab.cadl.lirui.webgourmand.core.impl.common.BookState
import lab.cadl.lirui.webgourmand.core.impl.common.Chapter
import lab.cadl.lirui.webgourmand.core.impl.longteng.BookListAnalyzer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * Created on 2014/10/7.
 */
class LongtengnetModule extends SimpleModule {
    private static final Logger logger = LoggerFactory.getLogger(LongtengnetModule.class)
    private ConcurrentMap<URL, Boolean> visitedUrls = new ConcurrentHashMap<>()

    void handleChapterContent(GPathResult result, Chapter chapter) {
        def book = chapter.book
        def path = Paths.get(
                "fiction",
                "longtengnet",
                Utils.filterSpecialPathChar(sprintf("%05d-%s", book.id, book.title)),
                Utils.filterSpecialPathChar(sprintf("%04d-%s.txt", chapter.index, chapter.title))
        )
        path.parent.toFile().mkdirs()
        logger.info("save {}", chapter.s())
        path.toFile().withWriter { writer ->
            result.'**'.find { it.@id.toString() == "main" }.'**'.grep { it.name() == 'h1' || it.name() == "p" }.each {
                writer.println(it.toString())
            }
        }
    }

    void analyzeChapterList(GPathResult result, URL baseUrl, Book book) {
        result.'**'.grep { it.name() == 'a' && it.@href.toString().startsWith("novel-read-") }.eachWithIndex { it, index ->
            def chapter = new Chapter(
                    title: it.toString().trim(),
                    index: index + 1,
                    book: book,
                    downloadUrl: Utils.formatUrl(baseUrl, it.@href.toString())
            )

            fetchHtml(chapter.downloadUrl,
                    HttpOptions.gbk(),
                    this.&handleChapterContent,
                    chapter
            )
        }
    }

    void analyzeBookList(GPathResult result, URL baseUrl) {
        result.'**'.find { it.name() == 'table' }.'**'.findAll { it.name() == 'tr' }.each {
            def a = it.'**'.find {it.name() == 'a' && it.@class.toString() == "title"}
            if (!a) {
                return
            }

            def relativeUrl = a.@href.toString()
            def id = Integer.parseInt(relativeUrl.substring("novel-view-".length(), relativeUrl.length() - ".html".length()))
            def url = Utils.formatUrl(baseUrl, relativeUrl)
            def title = a.toString().trim()
            def author = it.'**'.find {it.name() == 'a' && it.@class.toString() == "author"}.toString().trim()
            def book = new Book(
                    id: id,
                    title: title,
                    url: url,
                    author: author,
                    lastChapterTitle: it.'**'.find {it.name() == 'a' && it.@class.toString() == 'chapter'}.toString(),
                    words: Integer.parseInt(it.td[4].toString()),
                    updateDate: new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(it.td[5].toString()),
                    state: it.td[3].toString().trim() == "正在连载" ? BookState.ON_GOING : BookState.COMPLETED,
            )

            fetchHtml(Utils.formatUrl(baseUrl, "/novel-chapter-${id}.html"),
                    HttpOptions.gbk(),
                    this.&analyzeChapterList,
                    book
            )
        }

        visitedUrls.put(baseUrl, true)
        result.'**'.grep { it.name() == 'a' && it.@class.toString() == "f_a" }.each {
            def url = Utils.formatUrl(baseUrl, it.@href.toString())
            if (visitedUrls.putIfAbsent(url, true) == null) {
                fetchHtml(url, HttpOptions.gbk(), this.&analyzeBookList)
            }
        }
    }

    void login() {

    }

    @Override
    void start() {
        fetchHtml("http://www.ltshu.net/novel-list-0-0-0-0-0-0-0-1.html".toURL(),
                HttpOptions.gbk(),
                this.&analyzeBookList
        )
    }
}
