package lab.cadl.lirui.webgourmand.core.impl

/**
 * Created on 2014/8/12.
 */
class Utils {
    private Utils() {}

    static URL formatUrl(URL baseUrl, String url) {
        if (url.startsWith("http")) {
            return url.toURL()
        }

        return ("${baseUrl.protocol}://${baseUrl.host}" +
                (baseUrl.port ? ":${baseUrl.port}" : "") +
                (url.startsWith("/") ? url : "${baseUrl.path}/${url}")).toURL()
    }

    static String filterSpecialPathChar(String file, String replacement='') {
        return file.replaceAll(/[<>:"\/\\\|\?\*]/, replacement)
    }

    static void main(String[] args) {
        println filterSpecialPathChar("< > : \" / \\ | ? * abc", '_')
    }
}
