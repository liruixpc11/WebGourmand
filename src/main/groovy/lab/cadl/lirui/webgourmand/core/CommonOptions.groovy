package lab.cadl.lirui.webgourmand.core

/**
 * Created on 2014/8/26.
 */
class CommonOptions <T extends CommonOptions> {
    Map<String, String> postArgs

    public T withPostArgs(Map<String, String> args) {
        if (postArgs == null) {
            postArgs = [:]
        }

        postArgs.putAll(args)
        (T) this
    }
}
