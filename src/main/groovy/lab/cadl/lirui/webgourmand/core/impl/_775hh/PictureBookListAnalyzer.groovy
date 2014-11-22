package lab.cadl.lirui.webgourmand.core.impl._775hh

import groovy.util.slurpersupport.GPathResult
import lab.cadl.lirui.webgourmand.core.AbstractConsumer
import lab.cadl.lirui.webgourmand.core.BaseUrlAware
import lab.cadl.lirui.webgourmand.core.ContentFetcher
import lab.cadl.lirui.webgourmand.core.FetcherAware
import lab.cadl.lirui.webgourmand.core.HttpConsumer
import lab.cadl.lirui.webgourmand.core.HttpOptions
import lab.cadl.lirui.webgourmand.core.impl.Utils
import lab.cadl.lirui.webgourmand.core.impl.common.PictureList

/**
 * Created on 2014/9/20.
 */
class PictureBookListAnalyzer extends AbstractConsumer implements HttpConsumer, BaseUrlAware, FetcherAware {
    URL baseUrl
    ContentFetcher contentFetcher
    String section

    @Override
    void consume(GPathResult root) {
        root.'**'.find { it.@class == 'zxlist' }.'**'.grep { it.@href?.toString()?.endsWith(".html") }.each {
            def pictureList = new PictureList(
                    section: section,
                    title: it.toString(),
                    url: Utils.formatUrl(baseUrl, it.@href.toString())
            )

            contentFetcher.fetch(pictureList.url, null, HttpOptions.gbk(), new PictureListAnalyzer(pictureList))
        }

        def nextA = root.'**'.find { it.@href?.toString() && it.toString() == '下一页' }
        if (nextA) {
            contentFetcher.fetch(Utils.formatUrl(baseUrl, nextA.@href.toString()), null, HttpOptions.gbk(), this)
        }
    }
}
