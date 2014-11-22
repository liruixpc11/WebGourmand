package lab.cadl.lirui.webgourmand.core.impl.common

import groovy.transform.ToString

/**
 * Created on 2014/8/12.
 */
@ToString
class Book {
    int id
    String title
    String section
    String url
    String author
    String lastChapterTitle
    int words
    Date updateDate
    BookState state

    String s() {
        sprintf("%04d-%s", id, title)
    }
}
