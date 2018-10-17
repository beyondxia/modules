package com.pingan.aptdemo;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void dotTest() {
        String className = "com.test.Test";
        String substring = className.substring(className.lastIndexOf(".") + 1, className.length());
        String packageName = className.substring(0, className.lastIndexOf("."));
//        System.err.println(className.substring(className.lastIndexOf("."), className.length()));
        System.err.println(packageName);
        System.err.println(substring);
    }

    @Test
    public void regTest() {
        String className = "/User/chenwei/aptDemo/app/build/intermediates/classes/xXx/debug/com.test.Test";
        String regex = ".*/build/intermediates/classes/[a-z,A-Z]*[/]?debug/.*";
        String[] split = className.split(regex);
        for (String s : split) {
            System.err.println(s);
        }

        System.err.println(className.matches(regex));
    }
}