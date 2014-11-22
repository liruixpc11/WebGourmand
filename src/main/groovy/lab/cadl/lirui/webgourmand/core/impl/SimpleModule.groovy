package lab.cadl.lirui.webgourmand.core.impl

import groovy.util.slurpersupport.GPathResult
import lab.cadl.lirui.webgourmand.core.CommonOptions
import lab.cadl.lirui.webgourmand.core.ContentFetcher
import lab.cadl.lirui.webgourmand.core.Driver
import lab.cadl.lirui.webgourmand.core.HttpOptions
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created on 2014/10/5.
 */
abstract class SimpleModule implements Driver {
    private static final Logger logger = LoggerFactory.getLogger(SimpleModule.class)
    ContentFetcher contentFetcher

    SimpleModule() {
    }

    SimpleModule(ContentFetcher contentFetcher) {
        this.contentFetcher = contentFetcher
    }

    protected void fetchHtml(URL url, HttpOptions options, Closure handler, Object ... args) {
        contentFetcher.fetch(url, null, options, new HttpClosureConsumer({ GPathResult result, ContentFetcher _, URL baseUrl ->
            if (handler.maximumNumberOfParameters >= 1) {
                if (handler.parameterTypes[0] == GPathResult.class) {
                    if (handler.maximumNumberOfParameters >= 2 && handler.parameterTypes[1] == URL.class) {
                        handler.call(([result, baseUrl] + args.toList()).toArray())
                    } else {
                        def a = [result]
                        a += args.toList()
                        handler.call(a.toArray())
                    }
                } else if (handler.parameterTypes[0] == URL.class) {
                    def a = [baseUrl]
                    a += args.toList()
                    handler.call(a.toArray())
                } else {
                    handler.call(args)
                }
            }
        }))
    }

    protected void saveFile(URL url, File localFile) {
        logger.debug("save {} to {}", url, localFile)
        contentFetcher.fetch(url, null, new FileSaver(targetFile: localFile))
    }

    protected void savePostFile(URL url, Map<String, String> args, File localFile) {
        logger.debug("save {} to {}", url, localFile)
        contentFetcher.fetch(url, null, new CommonOptions().withPostArgs(args), new FileSaver(targetFile: localFile))
    }
}
