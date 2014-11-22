package lab.cadl.lirui.webgourmand.core.impl

import groovy.util.slurpersupport.GPathResult
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.HttpResponseDecorator
import lab.cadl.lirui.webgourmand.core.*
import org.ccil.cowan.tagsoup.Parser
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created on 2014/8/12
 */
class ContentFetcherImpl implements ContentFetcher {
    static enum FetchMode {
        Performance,
        BetterThanHuman,
        Human
    }

    private final static Logger logger = LoggerFactory.getLogger(ContentFetcherImpl.class)

    private static final long maxSecondsPerRequest = 60 * 10 // 10 min
    private static final long maxTriesPerRequest = 4

    private ExecutorService defaultExecutor
    private ConcurrentMap<String, ExecutorService> taggedExecutors = new ConcurrentHashMap<>()
    private AtomicInteger workCount = new AtomicInteger(0)

    private static ThreadLocal<HTTPBuilder> httpBuilderThreadLocal = new ThreadLocal<>()

    private String proxyHost
    private int proxyPort
    private String proxyScheme

    ContentFetcherImpl(FetchMode fetchMode = FetchMode.Performance) {
        switch (fetchMode) {
            case FetchMode.Performance:
                defaultExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2)
                break
            case FetchMode.BetterThanHuman:
                defaultExecutor = Executors.newFixedThreadPool(2)
                break
            case FetchMode.Human:
                defaultExecutor = Executors.newSingleThreadExecutor()
                break
            default:
                throw new IllegalArgumentException("unknown FetchMode: ${fetchMode}")
        }
    }

    private HTTPBuilder getHttp() {
        if (httpBuilderThreadLocal.get()) {
            return httpBuilderThreadLocal.get()
        }

        def http = new HTTPBuilder()
        http.setAutoAcceptHeader(false)
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
    ContentFetcher drive(Driver driver) {
        driver.setContentFetcher(this)
        driver.start()
        this
    }

    @Override
    ContentFetcher setProxy(String host, int port, String scheme) {
        proxyHost = host
        proxyPort = port
        proxyScheme = scheme
        this
    }

    @Override
    void fetch(URL url, String tag, HttpOptions options, HttpConsumer consumer) {
        handleAware(consumer, url)

        this.<GPathResult> doFetch(url, tag, consumer, { HttpResponseDecorator response, InputStream inputStream ->
            new XmlSlurper(new Parser()).parse(
                    options?.encoding ? new InputStreamReader(inputStream, options.encoding) : new InputStreamReader(inputStream)
            )
        }, options.postArgs)
    }

    @Override
    void fetch(URL url, String tag, TextOptions options, TextConsumer consumer) {
        handleAware(consumer, url)

        this.<String> doFetch(url, tag, consumer, { HttpResponseDecorator response, InputStream inputStream ->
            (options?.encoding ? new InputStreamReader(inputStream, options.encoding) : new InputStreamReader(inputStream)).text
        }, options.postArgs)
    }

    @Override
    void fetch(URL url, String tag, StreamConsumer consumer) {
        handleAware(consumer, url)
        this.<InputStream> doFetch(url, tag, consumer)
    }

    @Override
    void fetch(URL url, String tag, CommonOptions options, StreamConsumer consumer) {
        handleAware(consumer, url)
        this.<InputStream> doFetch(url, tag, consumer, null, options.postArgs)
    }

    private handleAware(BaseConsumer consumer, URL url) {
        if (consumer instanceof BaseUrlAware) {
            consumer.baseUrl = url
        }

        if (consumer instanceof FetcherAware) {
            consumer.contentFetcher = this
        }
    }

    private <R> void doFetch(URL url, String tag, BaseConsumer<? super R> consumer, Closure<? extends R> parser = null, Map<String, String> postArgs = null) {
        if (!consumer.preRequest()) {
            logger.info("pre check failed: ignore {}", url)
            return
        }

        logger.info("requesting {}", url)
        workCount.incrementAndGet()
        execute(tag) {
            boolean done = false
            long secondsBegin = new Date().time / 1000
            int tries = 0
            while (!done) {
                if (new Date().time / 1000 - secondsBegin > maxSecondsPerRequest) {
                    logger.warn("cancel request {} because of time out")
                    break
                }

                if (tries++ > maxTriesPerRequest) {
                    logger.warn("cancel request {} because of too many tries")
                }

                try {
                    def responseHandler = { HttpResponseDecorator response, InputStream inputStream ->
                        if (response.status < 300) {
                            consumer.consume(parser ? parser.call(response, inputStream) : (R) inputStream)
                        } else if (response.status < 400) {
                            def location = response.headers['Location'].value
                            doFetch(new URL(url, location), tag, consumer, parser, postArgs)
                        } else {
                            consumer.handleError(response.status, "[${response.status}] ${response.statusLine.reasonPhrase}")
                        }
                    }
                    if (postArgs) {
                        http.post(uri: url, body: postArgs, contentType: ContentType.BINARY, responseHandler)
                    } else {
                        http.get(uri: url, contentType: ContentType.BINARY, responseHandler)
                    }
                    done = true
                } catch (FileNotFoundException | MalformedURLException ex) {
                    logger.error("request {} failed: {}", url, ex)
                    break
                } catch (IOException ex) {
                    logger.warn("request {} failed because of network[{}], try again", url, ex)
                    // wait [5, 15]s
                    Thread.sleep((1000 * (Math.random() * 10 + 5)).toLong())
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
}
