package lab.cadl.lirui.webgourmand.core

/**
 * Created on 2014/8/12.
 */
class HttpException extends RuntimeException {
    String url

    HttpException(String url) {
        this(url, null)
    }

    HttpException(String url, Throwable throwable) {
        super("get ${url} failed: ${throwable}", throwable)
        this.url = url
    }
}
