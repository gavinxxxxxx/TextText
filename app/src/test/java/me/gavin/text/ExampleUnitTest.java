package me.gavin.text;

import org.junit.Test;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.gavin.db.dao.SourceDao;

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
        Collections.sort(list, (s1, s2) -> {
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
        });

        System.out.println("排序后-->" + list);
        Collections.reverse(list);
        System.out.println("排序后逆序-->" + list);
    }

    @Test
    public void textReplace() {
        String s = "hello<br/><br>world";
        System.out.println(s);
        s = s.replaceAll("<br/?>", "\\\\n");
        System.out.println(s);
        s = s.replaceAll("\\\\n", "\n");
        System.out.println(s);
    }

    @Test
    public void textRegex() {
        String s = "<div class='abc'>abc</div>";
        System.out.println(s.matches("(<div class=.*</div>|<span class=.*</span>|<i class=.*</i>|<p class=.*</p>)"));
    }

    @Test
    public void textRegex2() {
        String s = "<p>这要是个地道入口的话，发现前面情况不对我们完全可以在对方开火之前跑掉，毕竟我们的反射神经不是人类可以比的，但问题是后面就没地方让你跑。电梯的反应速度大家应该都见识过，等电梯关门再上升，站在这里的人估计已经被打成筛子了吧！ <span class='Xpf937'>www.daocaorenshuwu.com</span> </p>\n" +
                "<p>“防御！”在电梯门打开的瞬间我们就扫描了外面的情况，然后在惊讶了一秒之后我突然大喊一声。我们两个人瞬间站成了一列纵队，然后凌从后面顶住了我的肩膀帮助前面的我稳定身体。 <i class='Xpf937'>内容来自daocaoＲenshuwu.coＭ</i> </p>\n" +
                "<p>我是站在最前面的，在后面肩膀被顶住的同时我的面前已经张开了巨大的电磁屏障，而在此时对面的那两门速射反坦克炮其实已经开火了，只不过此时发射药还在膨胀阶段，炮弹依然在炮膛中处于加速阶段。 <p class='Xpf937'>内容来自daocaorenshuwu.ＣＯm</p> </p>\n" +
                "<p>几乎在我们这边的屏障展开的瞬间就突然激荡出巨大的火焰，一发炮弹正面命中我的防护屏障，然后被直接弹飞，但是间隔不到一秒我们的屏障上就爆出第二个火球，然后防线后的机枪也开始轰鸣，还有手雷之类的玩意被扔过来。顶部的火焰喷射器突然启动，我们两个人瞬间就彻底被淹没在了火焰之中。 <p class='Xpf937'>欢迎到稻草人书屋看书</p> </p>\n" +
                "<p>尽管弹药库被确定了没有爆炸，但是我却一点也高兴不起来，因为这样一来就是说十五条通道全都是死胡同，我们进入的电梯井成了唯一的出入口，而敌人不可能只有那么几个人，所以对方一定是跑掉了。现在没有发现人就说明在已经搜查过的地方必然存在没有被发现的暗道。 <span class='Xpf937'>copyright 稻草人书屋</span> </p>\n" +
                "<p>美国人虽然没有我们的探测能力，但他们的各种乱七八糟的探测器也不少，按说不应该会错过通道入口才对，现在这种情况只能说明对方把通道口藏的非常好，至少美国人的探测器是发现不了的。 <div class='Xpf937'>欢迎到稻草人书屋看书</div> </p>\n" +
                "<p>“现在怎么办？我们总不能自己再把通道全都检查一遍吧？” <p class='Xpf937'>内容来自daoＣaorenshuwu.com</p> </p>";
//        System.out.println(s.replaceAll("<[a-z]+ class=[^<]+</[a-z]+>",""));
        System.out.println(s.replaceAll("<([a-z]+) class=[^<]+?</\\1>", ""));
    }

    @Test
    public void textRegex3() {
        String s = "<h1>abcdefg</h1>\n" +
                "<h1>abcdefg</h2>\n" +
                "<h2>abcdefg</h2>\n" +
                "<h3>abcdefg</h2>\n" +
                "<h3>abcdefg</h3>\n" +
                "<h3>abcdefg</h4>\n" +
                "<h4>abcdefg</h4>\n" +
                "<h5>abcdefg</h4>\n" +
                "<h5>abcdefg</h5>";
        Matcher matcher = Pattern.compile("<[hH]([1-6])>.*?</[hH]\\1>").matcher(s);
        while (matcher.find()) {
            System.out.println(matcher.group());
        }
    }

    @Test
    public void textRegex4() {
        String s = "\n" +
                "        <div id=\"cont-text\" class=\"cont-text\">\n" +
                "          <script>DaoCaoRen.getCode(\"ui-content\");</script>\n" +
                "          <style> .Rtb759 { display:none; } </style>\n" +
                "<div>\n" +
                "\t　　水兰中学外，小雨淅沥沥的下着。\n" +
                "</div>\n" +
                "<div>\n" +
                "\t　　校门外，挤满了撑着雨伞的家长，他们被雨水淋湿的脸上满是期望。</div>\n" +
                "<div>\n" +
                "\t　　今天可是他们孩子的升学大事啊，能不能上天澜魔法高中，能不能将来考上一个好大学就看孩子今日的表现了！</div>\n" +

                "<p>“你算什么！我可是连早饭都没来及吃就来了，你好歹还吃了个汉堡！”阿伟嘟囔着。 <i class='Xld851'>欢迎到稻草人书屋看书</i> </p>\n" +
                "<p>“你算什么！我可是连早饭都没来及吃就来了，你好歹还吃了个汉堡！”阿伟嘟囔着。 <i class='Xld851'>欢迎到稻草人书屋看书</i> </p>\n" +
                "<p>“你算什么！我可是连早饭都没来及吃就来了，你好歹还吃了个汉堡！”阿伟嘟囔着。 <i class='Xld851'>欢迎到稻草人书屋看书</i> </p>\n" +

                "<div>\n" +
                "\t　　你没看错。</div>\n" +
                " \n" +
                "        </div>\n";
//        Matcher matcher = Pattern.compile("<(p|div)>(?s)(.*?)</\\1>").matcher(s);
//        while (matcher.find()) {
//            System.out.println(matcher.group());
//            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//        }

        String result = s.replaceAll("<(p|div)>(?s)(.*?)</\\1>","$2");
        System.out.println(result);
    }

    @Test
    public void sql() {
        int fieldCount = 5;
        StringBuilder sql = new StringBuilder()
                .append(" INSERT INTO ")
                .append(SourceDao.TABLENAME)
                .append(" VALUES ");
        for (int j = 0; j < 2; j++) {
            // for (Source source : sources) {
            sql.append("(");
            for (int i = 0; i < fieldCount; i++) {
                sql.append("?,");
            }
            sql.deleteCharAt(sql.lastIndexOf(",")).append("),");
        }
        sql.deleteCharAt(sql.lastIndexOf(","));
        System.out.println(sql.toString());
    }
}