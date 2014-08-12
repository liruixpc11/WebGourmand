package lab.cadl.lirui.webgourmand.core.impl.longteng

/**
 * Created on 2014/8/12.
 */
public enum BookState {
    COMPLETED,
    ON_GOING;

    static BookState parse(String text) {
        text == "连载" ? ON_GOING : COMPLETED
    }
}