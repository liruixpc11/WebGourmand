package lab.cadl.lirui.webgourmand.core.impl.tools

import java.nio.charset.StandardCharsets

/**
 * Created on 2014/8/14.
 */
class FictionCombiner {
    static void combine(String fictionDir) {
        println "combine " + fictionDir
        new File(fictionDir + ".txt").withOutputStream { OutputStream outputStream ->
            new File(fictionDir).listFiles().grep { it.isFile() }.sort().eachWithIndex { File chapterFile, index ->
                println "combine " + chapterFile + " to " + fictionDir + " " + chapterFile.size()
                chapterFile.withInputStream {
                    outputStream << "$index ${chapterFile.name.substring(0, chapterFile.name.lastIndexOf('.') - 1)}\r\n".getBytes("GBK")
                    outputStream << it
                }
            }
        }
    }

    static void main(String[] args) {
        new File("fiction/longteng").listFiles().grep { it.isDirectory() }.each { combine(it.toString()) }
    }
}
