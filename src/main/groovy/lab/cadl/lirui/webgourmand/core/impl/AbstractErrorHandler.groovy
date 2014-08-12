package lab.cadl.lirui.webgourmand.core.impl

import lab.cadl.lirui.webgourmand.core.HttpErrorHandler

/**
 * Created on 2014/8/12.
 */
class AbstractErrorHandler implements HttpErrorHandler {
    private Closure errorHandler

    void onError(Closure closure) {
        this.errorHandler = closure
    }

    @Override
    void handleError(int code, String reason) {
        if (errorHandler) {
            switch (errorHandler.maximumNumberOfParameters) {
                case 0:
                    errorHandler.call()
                    break
                case 1:
                    errorHandler.call(code)
                    break
                case 2:
                    errorHandler.call(reason)
                    break
                default:
                    throw new IllegalArgumentException("错误处理器参数超过2")
            }
        }
    }
}
