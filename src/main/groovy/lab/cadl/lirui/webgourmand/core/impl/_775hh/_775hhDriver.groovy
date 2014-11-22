package lab.cadl.lirui.webgourmand.core.impl._775hh

import lab.cadl.lirui.webgourmand.core.ContentFetcher
import lab.cadl.lirui.webgourmand.core.Driver
import lab.cadl.lirui.webgourmand.core.HttpOptions

/**
 * Created on 2014/9/20.
 */
class _775hhDriver implements Driver {
    ContentFetcher contentFetcher

    void start() {
        [
            ['http://www.775hh.com/zipai/', '自拍偷拍'],
            ['http://www.775hh.com/asia/', '亚洲色图'],
            ['http://www.775hh.com/meimei/', '清纯唯美'],
            ['http://www.775hh.com/meitui/', '美腿丝袜'],
            ['http://www.775hh.com/mingxing/', '明星淫乱'],
            ['http://www.775hh.com/cartoon/', '卡通动漫'],
            ['http://www.775hh.com/luanlun/', '少女熟妇'],
            ['http://www.775hh.com/linglei/', '另类图片']
        ].each {

            contentFetcher.fetch(it[0].toURL(),
                    null,
                    HttpOptions.gbk(),
                    new PictureBookListAnalyzer(section: it[1])
            )
        }
    }
}
