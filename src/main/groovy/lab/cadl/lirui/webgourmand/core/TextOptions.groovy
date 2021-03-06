package lab.cadl.lirui.webgourmand.core

import java.nio.charset.Charset

/**
 * Created on 2014/8/12.
 */
class TextOptions <T extends TextOptions> extends CommonOptions<T> {
    Charset encoding

    static TextOptions gbk() {
        return new TextOptions(encoding: Charset.forName("GBK"))
    }
}
