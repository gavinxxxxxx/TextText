package me.gavin.text;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.junit.Test;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
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
        String str = "。\n" +
                "　　同时，金瓶儿";
        System.out.println(str.matches("。\\s*\\n\\s*.+"));
    }

    @Test
    public void matches4() {
        double a = -1.55d;
        System.out.println(Math.floor(a));
        System.out.println(Math.round(a));
        System.out.println(Math.ceil(a));
    }

    @Test
    public void testCollator() {
        List<String> list = new ArrayList<>();
        list.add("中国");
        list.add("英国");
        list.add("美国");
        list.add("档案酷");
        list.add("abc");
        list.add("知乎.fe");
        list.add("lll");
        System.out.println("排序前-->" + list);
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                String o1 = "";
                String o2 = "";
                if (s1 != null) {
                    o1 = s1;
                }
                if (s2 != null) {
                    o2 = s2;
                }
                Collator instance = Collator.getInstance(Locale.CHINA);
                return instance.compare(o1, o2);
            }
        });

        System.out.println("排序后-->" + list);
        Collections.reverse(list);
        System.out.println("排序后逆序-->" + list);

    }
}