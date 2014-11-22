package lab.cadl.lirui.webgourmand.core

import lab.cadl.lirui.webgourmand.core.impl.ContentFetcherImpl
import lab.cadl.lirui.webgourmand.core.impl._775hh._775hhDriver
import lab.cadl.lirui.webgourmand.core.impl.avonline.AvOnlineBitModule
import lab.cadl.lirui.webgourmand.core.impl.baixingge.BaixinggeModule
import lab.cadl.lirui.webgourmand.core.impl.cnporn8.Cnporn8Driver
import lab.cadl.lirui.webgourmand.core.impl.longteng.LongtengDriver
import lab.cadl.lirui.webgourmand.core.impl.longtengnet.LongtengnetModule
import lab.cadl.lirui.webgourmand.core.impl.longtengnet.LongtengnetSoundModule
import lab.cadl.lirui.webgourmand.core.impl.se94se.Se94seDriver
import lab.cadl.lirui.webgourmand.core.impl.sis001.Sis001Module

/**
 * Created on 2014/8/19.
 */
class Main {
    static void main(String[] args) {
        new ContentFetcherImpl(ContentFetcherImpl.FetchMode.Performance).drive(new AvOnlineBitModule()).shutdown()
    }
}
