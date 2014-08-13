package lab.cadl.lirui.webgourmand.core.impl

import groovy.util.slurpersupport.GPathResult
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import lab.cadl.lirui.webgourmand.core.*
import lab.cadl.lirui.webgourmand.core.impl.longteng.BookListAnalyzer
import org.apache.http.conn.HttpHostConnectException
import org.ccil.cowan.tagsoup.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created on 2014/8/12.
 */
class ContentFetcherImpl implements ContentFetcher {
    private final static Logger logger = LoggerFactory.getLogger(ContentFetcherImpl.class)

    private ExecutorService defaultExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2)
    private ConcurrentMap<String, ExecutorService> taggedExecutors = new ConcurrentHashMap<>()
    private AtomicInteger workCount = new AtomicInteger(0)

    private static ThreadLocal<HTTPBuilder> httpBuilderThreadLocal = new ThreadLocal<>()

    private String proxyHost
    private int proxyPort
    private String proxyScheme

    private HTTPBuilder getHttp() {
        if (httpBuilderThreadLocal.get()) {
            return httpBuilderThreadLocal.get()
        }

        def http = new HTTPBuilder()
        if (proxyHost) {
            http.setProxy(proxyHost, proxyPort, proxyScheme)
        }
        httpBuilderThreadLocal.set(http)
        return http
    }

    @Override
    void shutdown() {
        while (workCount.get() != 0) {
            Thread.sleep(1000)
        }

        defaultExecutor.shutdown()
        defaultExecutor.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS)
        taggedExecutors.values().each { it.shutdown() }
    }

    @Override
    void setProxy(String host, int port, String scheme) {
        proxyHost = host
        proxyPort = port
        proxyScheme = scheme
    }

    @Override
    void fetch(URL url, String tag, HttpOptions options, HttpConsumer consumer) {
        handleAware(consumer, url)

        this.<GPathResult> doFetch(url, tag, consumer) { HttpResponseDecorator response, InputStream inputStream ->
            new XmlSlurper(new Parser()).parse(
                    options?.encoding ? new InputStreamReader(inputStream, options.encoding) : new InputStreamReader(inputStream)
            )
        }
    }

    @Override
    void fetch(URL url, String tag, TextOptions options, TextConsumer consumer) {
        handleAware(consumer, url)

        this.<String> doFetch(url, tag, consumer) { HttpResponseDecorator response, InputStream inputStream ->
            (options?.encoding ? new InputStreamReader(inputStream, options.encoding) : new InputStreamReader(inputStream)).text
        }
    }

    @Override
    void fetch(URL url, String tag, StreamConsumer consumer) {
        handleAware(consumer, url)

        this.<InputStream> doFetch(url, tag, consumer)
    }

    private handleAware(BaseConsumer consumer, URL url) {
        if (consumer instanceof BaseUrlAware) {
            consumer.baseUrl = url
        }

        if (consumer instanceof FetcherAware) {
            consumer.contentFetcher = this
        }
    }

    private <R> void doFetch(URL url, String tag, BaseConsumer<? super R> consumer, Closure<? extends R> parser = null) {
        workCount.incrementAndGet()
        execute(tag) {
            boolean done = false
            while (!done) {
                try {
                    http.get(uri: url, contentType: ContentType.BINARY) { HttpResponseDecorator response, InputStream inputStream ->
                        if (response.status < 400) {
                            consumer.consume(parser ? parser.call(response, inputStream) : (R) inputStream)
                        } else {
                            consumer.handleError(response.status, "[${response.status}] ${response.statusLine.reasonPhrase}")
                        }
                    }

                    done = true
                } catch (FileNotFoundException ex) {
                    logger.error("request {} failed: {}", url, ex)
                    break
                } catch (IOException ex) {
                    logger.warn("request {} failed because of network[{}], try again", url, ex)
                    // wait [0, 10]s
                    Thread.sleep((1000 * Math.random() * 10).toLong())
                } catch (Exception ex) {
                    logger.error("request {} failed: {}", url, ex)
                    break
                }
            }

            workCount.decrementAndGet()
        }
    }

    private ExecutorService getExecutor(String tag = null) {
        tag == null ? defaultExecutor : taggedExecutors.getOrDefault(tag, defaultExecutor)
    }

    private void execute(String tag, Closure closure) {
        getExecutor(tag).execute(closure)
    }

    public static void main(String[] args) {
        def fetcher = new ContentFetcherImpl()
        fetcher.fetch("http://www.ltshu.com/modules/article/index.php".toURL(),
                null,
                HttpOptions.gbk(),
                new BookListAnalyzer())
        fetcher.shutdown()
    }
}
