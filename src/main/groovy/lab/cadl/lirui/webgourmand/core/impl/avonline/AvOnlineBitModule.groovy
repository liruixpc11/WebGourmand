package lab.cadl.lirui.webgourmand.core.impl.avonline

import groovy.util.slurpersupport.GPathResult
import lab.cadl.lirui.webgourmand.core.HttpOptions
import lab.cadl.lirui.webgourmand.core.impl.SimpleModule
import lab.cadl.lirui.webgourmand.core.impl.Utils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.file.Paths

/**
 * Created on 2014/11/12.
 */
class AvOnlineBitModule extends SimpleModule {
    private static final Logger logger = LoggerFactory.getLogger(AvOnlineBitModule.class)

    void extractBitFile(GPathResult result, URL baseUrl, AvInfo avInfo) {
        def form = result.'**'.find { it.name() == 'form' }
        def url = Utils.formatUrl(baseUrl, form.@action.toString())
        savePostFile(
                url,
                ['ref': form.'**'.find { it.name() == 'input' && it.@name.toString() == 'ref' }.@value.toString() ],
                Paths.get("bits", "avonline", avInfo.section, avInfo.name + '.torrent').toFile()
        )
    }

    void analyzeAvInfo(GPathResult result, URL baseUrl, AvInfo avInfo) {
        println result.'**'.find { it.name() == 'div' && it.h3.toString() == 'BT下载种子下载' }.'**'.find { it.name() == 'textarea' }
        def urlString = result.'**'.find { it.name() == 'div' && it.h3.toString() == 'BT下载种子下载' }.'**'.find { it.name() == 'textarea' }.toString()
        def url = Utils.formatUrl(baseUrl, urlString)
        fetchHtml(url, HttpOptions.utf8(), this.&extractBitFile, avInfo)
    }

    void analyzeAvList(GPathResult result, URL baseUrl, String sectionName) {
        result.'**'.findAll { it.name() == 'div' && it.@class.toString() == 'list1' && it.a.toString() }.each { GPathResult div ->
            def avInfo = new AvInfo(
                    section: sectionName,
                    name: div.a.toString()
            )

            fetchHtml(Utils.formatUrl(baseUrl, div.a.@href.toString()), HttpOptions.utf8(), this.&analyzeAvInfo, avInfo)
        }

        def a = result.'**'.find { it.name() == 'a' && it.toString() == '下一页' }
        if (a) {
            def url = Utils.formatUrl(baseUrl, a.@href.toString())
            if (baseUrl.file != url.file) {
                fetchHtml(url, HttpOptions.utf8(), this.&analyzeAvList, sectionName)
            }
        }
    }

    @Override
    void start() {
        [
                ['http://www.905zy.com/list_html/1.html', '亚洲电影'],
                ['http://www.905zy.com/list_html/3.html', '欧美电影'],
                ['http://www.905zy.com/list_html/4.html', '国产电影'],
                ['http://www.905zy.com/list_html/16.html', '经典电影'],
                ['http://www.905zy.com/list_html/18.html', '强奸乱伦'],
                ['http://www.905zy.com/list_html/20.html', '变态另类'],
                ['http://www.905zy.com/list_html/24.html', '成人动画'],
                ['http://www.905zy.com/list_html/26.html', '制服丝袜'],
                ['http://www.905zy.com/list_html/27.html', '熟女人妻'],
                ['http://www.905zy.com/list_html/28.html', '中文字幕'],
                ['http://www.905zy.com/list_html/29.html', '女优专辑-爱花沙也'],
        ].each {
            fetchHtml(new URL(it[0]), HttpOptions.utf8(), this.&analyzeAvList, it[1])
        }
    }
}
