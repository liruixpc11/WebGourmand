package lab.cadl.lirui.webgourmand.core.impl.cnporn8

import groovy.util.slurpersupport.GPathResult
import lab.cadl.lirui.webgourmand.core.*
import lab.cadl.lirui.webgourmand.core.impl.Utils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created on 2014/9/3.
 */
class XingAiZiPaiListAnalyzer extends AbstractConsumer<GPathResult> implements HttpConsumer, BaseUrlAware, UrlProvider {
    private static final Logger logger = LoggerFactory.getLogger(XingAiZiPaiListAnalyzer.class)
    URL url = new URL("${Constants.urlBase}thread-htm-fid-158.html")
    URL baseUrl
    Set<String> listUrlVisited = []

    @Override
    void consume(GPathResult root) {
        logger.info("analyzing list {}", baseUrl)
        root.'**'.find { it.@id == 'ajaxtable' }.'**'.findAll {
            it.@href && it.@id?.toString()?.startsWith("a_ajax_")
        }.each {
            def listInfo = new PictureListInfo(
                    url: Utils.formatUrl(baseUrl, it.@href.toString()),
                    title: it.toString().trim()
            )
            contentFetcher.fetch(listInfo.url, null, HttpOptions.utf8(), new PictureListAnalyzer(listInfo))
        }

        root.'**'.find { it.@class == 'pages' }.'**'.findAll { it.@href }.each {
            if (shouldVisit(it.@href.toString())) {
                contentFetcher.fetch(Utils.formatUrl(baseUrl, it.@href.toString()), null, HttpOptions.utf8(), this)
            }
        }
    }

    private synchronized boolean shouldVisit(String url) {
        if (listUrlVisited.contains(url)) {
            return false;
        }

        listUrlVisited.add(url)
        return true
    }
}
