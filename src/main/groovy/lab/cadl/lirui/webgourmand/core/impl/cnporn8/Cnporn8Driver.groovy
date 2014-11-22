package lab.cadl.lirui.webgourmand.core.impl.cnporn8

import lab.cadl.lirui.webgourmand.core.ContentFetcher
import lab.cadl.lirui.webgourmand.core.Driver
import lab.cadl.lirui.webgourmand.core.HttpOptions

/**
 * Created on 2014/8/26.
 */
class Cnporn8Driver implements Driver {
    ContentFetcher contentFetcher

    @Override
    void start() {
        contentFetcher.fetch("${Constants.urlBase}login.php".toURL(),
                null,
                HttpOptions.utf8().withPostArgs(
                        pwuser: 'liruixpcse01',
                        pwpwd: 'aa23581321',
                        jumpurl: '/index.php',
                        step: '2',
                        cktime: '31356000',
                        lgt: '0'),
                new Login(consumerList: [
                        new XingAiZiPaiListAnalyzer()
                ])
        )
    }
}
