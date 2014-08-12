package lab.cadl.lirui.webgourmand.core

import java.nio.charset.Charset

/**
 * Created on 2014/8/12.
 */
class HttpOptions extends TextOptions {
    static HttpOptions gbk() {
        new HttpOptions(encoding: Charset.forName("GBK"))
    }
}
