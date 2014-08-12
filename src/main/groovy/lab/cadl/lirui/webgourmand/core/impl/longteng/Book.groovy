package lab.cadl.lirui.webgourmand.core.impl.longteng

import groovy.transform.ToString

/**
 * Created on 2014/8/12.
 */
@ToString
class Book {
    int id
    String title
    String url
    String author
    String lastChapterTitle
    int words
    Date updateDate
    BookState state
}
