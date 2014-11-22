package lab.cadl.lirui.webgourmand.core.impl.sis001

import groovy.util.slurpersupport.GPathResult
import lab.cadl.lirui.webgourmand.core.HttpOptions
import lab.cadl.lirui.webgourmand.core.impl.SimpleModule
import lab.cadl.lirui.webgourmand.core.impl.Utils
import lab.cadl.lirui.webgourmand.core.impl.common.Book
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

/**
 * Created on 2014/10/18.
 */
class Sis001Module extends SimpleModule {
    private static final Logger logger = LoggerFactory.getLogger(Sis001Module.class)
    private int contentThreshold = 256

    @Override
    void start() {
        [
                ['http://67.220.93.4/forum/forum-96-1.html', '武侠玄幻'],
                ['http://67.220.93.4/forum/forum-279-1.html', '人妻意淫'],
                ['http://67.220.93.4/forum/forum-83-1.html', '乱伦迷情'],
                ['http://67.220.93.4/forum/forum-31-8.html', '另类其它'],
//                ['http://174.127.195.200/bbs/forumdisplay.php?fid=96&filter=0&orderby=dateline&ascdesc=DESC', 'SexInSex武侠玄幻']
        ].each {
            fetchHtml(new URL(it[0]), HttpOptions.gbk(), this.&analyzeBookList, it[1])
        }
    }

    void analyzeBookList(GPathResult result, URL baseUrl, String section) {
        result.'**'.grep { it.@id.toString().startsWith("normalthread_") }.each {
            def url = it.'**'.find { it.name() == 'a' && it.@href.toString().startsWith("thread-") }.@href.toString()
            def book = new Book(
                    id: it.tr.th.span.@id.toString().substring("thread_".length()).toInteger(),
                    url: Utils.formatUrl(baseUrl, url).toString(),
                    title: it.tr.th.span.a.toString(),
                    author: it.tr.td[2].cite.a.toString(),
                    section: section
            )

            if (formatBookFile(book).exists()) {
                logger.info("ignore {} because of exists", book.s())
                return
            }

            fetchHtml(book.url.toURL(), HttpOptions.gbk(), this.&analyzeBookContent, book)
        }

        def a = result.'**'.find { it.name() == 'a' && it.toString() == '››' }
        if (a) {
            def url = Utils.formatUrl(baseUrl, a.@href.toString())
            fetchHtml(url, HttpOptions.gbk(), this.&analyzeBookList, section)
        }
    }

    static File formatBookFile(Book book) {
        return Paths.get("fiction", "sis001", book.section, Utils.filterSpecialPathChar("${book.id}-${book.title}.txt")).toFile()
    }

    void analyzeBookContent(GPathResult result, URL baseUrl, Book book, boolean append = false) {
        def targetFile = formatBookFile(book)
        logger.info("save book {} into {}", book.s(), targetFile)
        targetFile.parentFile.mkdirs()
        def tmpFile = new File(targetFile.toString() + ".download")
        if (append) {
            tmpFile.withWriterAppend {
                writeContent(it, result)
            }
        } else {
            tmpFile.withWriter {
                writeContent(it, result)
            }
        }


        def a = result.'**'.find { it.name() == 'a' && it.toString() == '››' }
        if (a) {
            def url = Utils.formatUrl(baseUrl, a.@href.toString())
            fetchHtml(url, HttpOptions.gbk(), this.&analyzeBookContent, book, true)
        } else {
            Files.move(tmpFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
    }

    private void writeContent(Writer writer, GPathResult result) {
        result.'**'.grep { it.name() == 'div' && it.@class.toString() == 'mainbox viewthread' }.each {
            def content = it.'**'.find { it.@id.toString().startsWith("postmessage_") }.toString()
            if (content.length() < contentThreshold) {
                return
            }

            writer.write(content)
            writer.write("\n")
        }
    }
}
