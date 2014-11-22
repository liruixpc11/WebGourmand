package lab.cadl.lirui.webgourmand.core.impl.test

/**
 * Created on 2014/10/5.
 */
class Test {

    static void test(Object... args) {
        for (Object arg : args) {
            println arg.class.name
        }
    }

    static void main(String[] args) {
        test('a', 1, ['a', 1] as Object[])
        println ""

        test(['a', 1] as Object[])
    }
}
