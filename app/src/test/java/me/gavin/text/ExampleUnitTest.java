package me.gavin.text;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public void matches() {
        String r = "[`~!@#$^&*)=|}:;,\\].>/?~！@#￥……&*）——|}】‘；：”'。，、？]";
        String str = "==";

        System.out.println(str.matches(r));
    }

    @Test
    public void matches2() {
        String r = "(\\W|\\w+)";
        String str = "lwijoads nadkg 故意";
        Pattern pattern = Pattern.compile(r);
        Matcher matcher = pattern.matcher(str);
        System.out.println(matcher.groupCount());
        while (matcher.find()) {
            System.out.println(matcher.group());
        }
    }

    @Test
    public void matches3() {
        String str = "我石fewfe fewfe";
        System.out.println(str.matches(".*[A-Za-z0-9-]+"));
    }
}