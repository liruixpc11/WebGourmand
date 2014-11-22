package lab.cadl.lirui.webgourmand.core.impl.longtengnet

import groovy.util.slurpersupport.GPathResult
import lab.cadl.lirui.webgourmand.core.HttpOptions
import lab.cadl.lirui.webgourmand.core.impl.SimpleModule
import lab.cadl.lirui.webgourmand.core.impl.Utils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

/**
 * Created on 2014/11/10.
 */
class LongtengnetSoundModule extends SimpleModule {
    private static final Logger logger = LoggerFactory.getLogger(LongtengnetSoundModule.class)

    void analyzeSoundFile(GPathResult result, URL baseUrl, String name) {
        result.'**'.findAll { it.name() == "a" && it.@href.toString().endsWith(".mp3") }.each {
            def url = Utils.formatUrl(baseUrl, it.@href.toString())
            def file = Paths.get("sounds", "longtengnet", Utils.filterSpecialPathChar(name), Utils.filterSpecialPathChar(url.file)).toFile()
            saveFile(url, file)
        }
    }

    void analyzeSoundList(GPathResult result, URL baseUrl) {
        result.'**'.findAll { it.name() == "a" && it.@class.toString() == 'name' }.each {
            fetchHtml(Utils.formatUrl(baseUrl, it.@href.toString()), HttpOptions.gbk(), this.&analyzeSoundFile, it.toString())
        }

        def nextA = result.'**'.find { it.name() == 'a' && it.toString() == '下一页' }
        if (nextA) {
            fetchHtml(Utils.formatUrl(baseUrl, nextA.@href.toString()),
                    HttpOptions.gbk(),
                    this.&analyzeSoundList
            )
        }
    }

    @Override
    void start() {
        fetchHtml("http://www.ltshu.net/portal.php?mod=list&catid=2".toURL(),
                HttpOptions.gbk(),
                this.&analyzeSoundList
        )
    }
}
