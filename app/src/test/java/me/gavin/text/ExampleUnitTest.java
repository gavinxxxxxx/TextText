package me.gavin.text;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void split() {
        String str = "==========================================================\n" +
                "更多精校小说尽在知轩藏书下载：http://www.zxcs8.com/\n" +
                "==========================================================\n" +
                "《诛仙》\n" +
                "作者：萧鼎\n" +
                "\n" +
                "   　　　 内容简介：\n" +
                "　　天地不仁，以万物为刍狗！\n";

        String[] ss = str.split("[\\s　]*\\n[\\s　]*");
        for (String s : ss) {
            System.out.println(s.trim());
        }
        System.out.println(ss.length);
    }
}