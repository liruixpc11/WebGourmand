package lab.cadl.lirui.webgourmand.core.impl.baixingge

import groovy.util.slurpersupport.GPathResult
import lab.cadl.lirui.webgourmand.core.HttpOptions
import lab.cadl.lirui.webgourmand.core.impl.SimpleModule
import lab.cadl.lirui.webgourmand.core.impl.Utils
import lab.cadl.lirui.webgourmand.core.impl.common.PictureList

import java.nio.file.Paths

/**
 * Created on 2014/10/5.
 */
class BaixinggeModule extends SimpleModule {
    String urlPrefix = "http://174.139.109.38/"

    URL toUrl(String path) {
        (urlPrefix + path).toURL()
    }

    void analyzePictureList(GPathResult result, URL baseUrl, PictureList pictureList) {
        result.'**'.find { it.@class.toString() == 'pcb' }.'**'.grep { it.name() == 'img' }.eachWithIndex { it, index ->
            def url = Utils.formatUrl(baseUrl, it.@src.toString())
            saveFile(url,
                    Paths.get("pictures",
                            "BaiXingGe",
                            Utils.filterSpecialPathChar(pictureList.section),
                            Utils.filterSpecialPathChar(pictureList.title),
                            index.toString() + "." + url.file.toString().substring(url.file.toString().length() - 3)
                    ).toFile())
        }
    }

    void analyzePictureBookList(GPathResult result, URL baseUrl, String sectionName = "default") {
        result.'**'.findAll { it.@id.toString().startsWith("normalthread_") }.each { GPathResult entry ->
            entry.'**'.find { it.name() == 'th' }.'**'.grep { it.@href.toString().startsWith("thread-") }.each {
                def url = Utils.formatUrl(baseUrl, it.@href.toString())
                fetchHtml(url, HttpOptions.utf8(),
                        this.&analyzePictureList,
                        new PictureList(
                                section: sectionName,
                                url: url,
                                title: it.toString()
                        )
                )
            }
        }

        def next = result.'**'.find { it.name().toString() == 'a' && it.@class.toString() == 'nxt'}
        if (next) {
            fetchHtml(Utils.formatUrl(baseUrl, next.@href.toString()), HttpOptions.utf8(), this.&analyzePictureBookList, "自拍专区")
        }
    }

    @Override
    void start() {
        [
                ["forum-42-1.html", "自拍专区"],
                ['forum-51-1.html', "国产裸模"],
                ['forum-43-1.html', "制服丝袜"],
                ['forum-44-1.html', "街拍偷拍"],
                ['forum-45-1.html', "亚洲贴图"],
                ['forum-46-1.html', "欧美贴图"],
                ['forum-47-1.html', "综合另类"],
                ['forum-48-1.html', "明星贴图"],
                ['forum-49-1.html', "卡通漫画"],
                ['forum-50-1.html', "精品套图"]
        ].each {
            fetchHtml(toUrl(it[0]), HttpOptions.utf8(), this.&analyzePictureBookList, it[1])
        }
    }
}
