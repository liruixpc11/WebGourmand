package lab.cadl.lirui.webgourmand.common

import groovy.transform.ToString

/**
 * Created on 2014/8/12.
 */
@ToString
class Chapter {
    Book book
    int index
    String title
    String size
    Date updateDate
    URL downloadUrl

    String s() {
        sprintf("[%s]%04d-%s", book.s(), index, title)
    }
}
