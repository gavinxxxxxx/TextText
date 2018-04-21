package me.gavin.app;

/**
 * Utils
 *
 * @author gavin.xiong 2017/12/15
 */
public final class Utils {

    public static String trim(String string) {
        int len = string.length();
        int st = 0;

        while ((st < len) && (string.charAt(st) <= ' ' || string.charAt(st) == '　')) {
            st++;
        }
        while ((st < len) && (string.charAt(len - 1) <= ' ' || string.charAt(len - 1) == '　')) {
            len--;
        }
        return ((st > 0) || (len < string.length())) ? string.substring(st, len) : string;
    }

    /**
     * 是否是中文标点
     */
    public static boolean isPunctuation(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    /**
     * 英文标点判断
     */
    public static boolean isHalfPunctuation(char c) {
        int count = (int) c;

        if (count >= 33 && count <= 47) {
            // !~/
            return true;
        } else if (count >= 58 && count <= 64) {
            // :~@
            return true;
        } else if (count >= 91 && count <= 96) {
            // [~
            return true;
        } else if (count >= 123 && count <= 126) {
            // {~~
            return true;
        }
        return false;
    }

    /**
     * 左侧标点  For example:" ( < [ { "
     */
    public static boolean isLeftPunctuation(char c) {
        int count = (int) c;
        return count == 8220 || count == 12298 || count == 65288 || count == 12304
                || count == 40 || count == 60 || count == 91 || count == 123;
    }

    /**
     * 右侧标点  For example:" ) > ] } "
     */
    public static boolean isRightPunctuation(char c) {
        int count = (int) c;
        return count == 8221 || count == 12299 || count == 65289 || count == 12305
                || count == 41 || count == 62 || count == 93 || count == 125;
    }

    /**
     * 获取翻页动画时长
     */
    public static long getDuration(float diff) {
        return Math.round(Math.abs(diff) * Config.flipAnimDuration);
    }
}
