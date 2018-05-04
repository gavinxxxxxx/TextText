package me.gavin.app;

import android.graphics.Path;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.gavin.app.core.model.Book;
import me.gavin.app.core.model.Line;
import me.gavin.app.core.model.Page;
import me.gavin.app.core.model.Word;

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

    public static Page lastLocal(Page page, long offset) throws IOException {
        page.isReverse = true;
        page.end = Math.min(page.book.getLength(), offset);
        page.isLast = page.end == page.book.getLength();
        // 起始位置
        long start = Math.max(0, page.end - Config.pagePreCount);
        // 页前多加载字符 - 用来判断首行是否缩进
        int preCountH = start > Config.segmentPreCount ? Config.segmentPreCount : (int) page.start;
        int preCountF = page.end < page.book.length - Config.segmentPreCount ? Config.segmentPreCount : (int) (page.book.length - page.end);
        String text = StreamHelper.getText(page.book.open(), page.book.getCharset(),
                start - preCountH, (int) (page.end - start) + preCountH + preCountF);
        page.mText = text.substring(preCountH, text.length() - preCountF);
        page.mTextSp = Utils.trim(page.mText).split(Config.REGEX_SEGMENT);
        // 首行缩进条件： 是第一页 || 开头符合分段后 || 前一截符合分段前
        page.indent = start == 0 || page.mText.matches(Config.REGEX_SEGMENT_SUFFIX)
                || text.substring(0, preCountH).matches(Config.REGEX_SEGMENT_PREFIX);
        page.align = !page.isLast && !page.mText.matches(Config.REGEX_SEGMENT_PREFIX)
                && !text.substring(text.length() - preCountF).matches(Config.REGEX_SEGMENT_SUFFIX);
        page.suffix = !page.isLast && text.substring(text.length() - preCountF - 1,
                text.length() - preCountF + 1).matches(Config.REGEX_WORD);
        perpare(page);
        page.ready = true;
        return page;
    }

    public static Page lastOnline(Page page, long offset) {
        page.isReverse = true;
        page.end = Math.min(page.chapter.length(), offset);
        page.isLast = page.index >= page.book.getCount() - 1 && page.end == page.chapter.length();
        // 起始位置
        int start = Math.max(0, (int) page.end - Config.pagePreCount);
        page.mText = page.chapter.substring(start, (int) page.end);
        page.mTextSp = Utils.trim(page.mText).split(Config.REGEX_SEGMENT);
        page.indent = start == 0 || page.mText.matches(Config.REGEX_SEGMENT_SUFFIX)
                || page.chapter.substring(0, start).matches(Config.REGEX_SEGMENT_PREFIX);
        page.align = !page.isLast && !page.mText.matches(Config.REGEX_SEGMENT_PREFIX)
                && !page.chapter.substring((int) page.end).matches(Config.REGEX_SEGMENT_SUFFIX);
        page.suffix = page.end < page.chapter.length()
                && page.chapter.substring((int) page.end - 1, (int) page.end + 1).matches(Config.REGEX_WORD);
        perpare(page);
        page.ready = true;
        return page;
    }

    public static Page nextLocal(Page page, long offset) throws IOException {
        page.isReverse = false;
        page.start = Math.max(0, offset);
        page.isFirst = page.start == 0;
        // 页前多加载字符 - 用来判断首行是否缩进
        int preCountH = page.start > Config.segmentPreCount ? Config.segmentPreCount : (int) page.start;
        String text = StreamHelper.getText(page.book.open(), page.book.getCharset(),
                page.start - preCountH, Config.pagePreCount + preCountH);
        page.mText = text.substring(preCountH);
        page.mTextSp = Utils.trim(page.mText).split(Config.REGEX_SEGMENT);
        // 首行缩进条件： 是第一页 || 开头符合分段后 || 前一截符合分段前
        page.indent = page.isFirst || page.mText.matches(Config.REGEX_SEGMENT_SUFFIX)
                || text.substring(0, preCountH).matches(Config.REGEX_SEGMENT_PREFIX);
        perpare(page);
        page.ready = true;
        return page;
    }

    public static Page nextOnline(Page page, long offset) {
        page.isReverse = false;
        page.start = Math.max(0, offset);
        page.isFirst = page.index == 0 && page.start == 0;
        page.mText = page.chapter.substring((int) page.start, Math.min((int) page.start + Config.pagePreCount, page.chapter.length()));
        page.mTextSp = Utils.trim(page.mText).split(Config.REGEX_SEGMENT);
        page.indent = page.isFirst || page.mText.matches(Config.REGEX_SEGMENT_SUFFIX)
                || page.chapter.substring(0, (int) page.start).matches(Config.REGEX_SEGMENT_PREFIX);
        perpare(page);
        page.ready = true;
        return page;
    }


    private static void perpare(Page page) {
        text2Line(page);
        for (Line line : page.lineList) {
            line2Words(page, line);
        }
        word2Path(page);
    }

    private static void text2Line(Page page) { // TODO: 2018/5/2 反向如果只有单行逻辑上有问题
        long length = page.book.type == Book.TYPE_ONLINE ? page.chapter.length() : page.book.getLength();
        int y = Config.topPadding; // 行文字顶部
        String subText = Utils.trim(page.mText);
        for (int i = 0; i < page.mTextSp.length; i++) {
            String segment = page.mTextSp[i];
            int segmentStart = 0;
            while (segmentStart < segment.length()) {
                if (!page.isReverse && y + Config.textHeight > Config.height - Config.bottomPadding) { // 正向 & 已排满页面
                    page.limit = page.mText.indexOf(subText);
                    page.end = page.start + page.limit;
                    page.isLast = page.book.type == Book.TYPE_LOCAL ? page.end >= length
                            : page.index >= page.book.getCount() - 1 && page.end >= length;
                    return;
                }
                String remaining = segment.substring(segmentStart);
                boolean lineIndent = i == 0 && segmentStart == 0 && page.indent || i != 0 && segmentStart == 0;
                int count = breakText(remaining, lineIndent);
                String suffix = count < 0 || page.suffix ? "-" : "";
                count = Math.abs(count);
                String text = segment.substring(segmentStart, segmentStart + count);
                boolean lineAlignNo = segmentStart + count >= segment.length() // 不对齐 - 段落尾行 && !(反向尾行)
                        && !(page.isReverse && page.align && i == page.mTextSp.length - 1);
                Line line = new Line(Utils.trim(text), suffix, y - Config.textTop, lineIndent, lineAlignNo);
                page.lineList.add(line);

                y += Config.textHeight + Config.lineSpacing;
                segmentStart += count;
                subText = subText.substring(subText.indexOf(text) + text.length());
            }
            y += Config.segmentSpacing;
        }
        if (!page.isReverse && page.start + page.mText.length() >= length) { // 正向 & 还能显示却没有了
            page.end = length;
            page.limit = (int) (page.end - page.start);
            if (page.book.type == Book.TYPE_ONLINE) {
                page.isLast = page.index >= page.book.getCount() - 1;
            } else {
                page.isLast = true;
            }
        } else if (page.isReverse) { // 反向
            List<Line> lines = new ArrayList<>();
            y = y - Config.segmentSpacing - Config.lineSpacing; // 最后一行文字底部 - 去掉多余的空隙
            subText = page.mText; // 子字符串 - 计算字符数量

            int ey = y - Config.height + Config.bottomPadding + Config.topPadding; // 超出的高度
            for (Line line : page.lineList) {
                if (line.y + Config.textTop < ey) { // 底部对齐后去掉顶部超出的行
                    subText = subText.substring(subText.indexOf(line.src) + line.src.length());
                } else {
                    lines.add(line);
                }
            }

            ey = lines.isEmpty() ? -1 : lines.get(0).y - Config.topPadding; // 距顶部对齐行偏移量
            for (Line line : lines) {
                line.y = line.y - ey + Config.textSize;
            }
            page.lineList.clear();
            page.lineList.addAll(lines);

            page.limit = subText.length();
            page.start = page.end - page.limit;
            if (page.book.type == Book.TYPE_ONLINE) {
                page.isFirst = page.index <= 0 && page.start <= 0;
            } else {
                page.isFirst = page.start <= 0;
            }
        }
    }

    private static int breakText(String remaining, boolean lineIndent) {
        int count = Config.textPaint.breakText(remaining, true, Config.width
                - Config.leftPadding - Config.rightPadding - (lineIndent ? Config.indent : 0), null);
        return count >= remaining.length() ? count : countReset(remaining, count, lineIndent);
    }

    /**
     * 分行衔接调整 - 标点 & 单词 - 只考虑标准情况
     *
     * @return count 数量 负数代表要加 -
     */
    private static int countReset(String remaining, int count, boolean lineIndent) {
        if (remaining.substring(count, count + 1).matches(Config.REGEX_PUNCTUATION_N)) { // 下一行第一个是标点
            if (!remaining.substring(count - 2, count).matches(Config.REGEX_PUNCTUATION_N + "*")) { // 当前行末两不都是标点 - 标准状况下
                count -= 1;
                return countReset(remaining, count, lineIndent);
            }
        } else if (remaining.substring(count - 1, count + 1).matches(Config.REGEX_WORD)) { // 上一行最后一个字符和下一行第一个字符符合单词形式
            String line = remaining.substring(0, count);
            Matcher matcher = Pattern.compile(Config.REGEX_CHARACTER).matcher(line);
            int groupCount = 0;
            String group = "";
            while (matcher.find()) {
                groupCount++;
                group = matcher.group();
            }
            int end = line.lastIndexOf(group);

            float indent = lineIndent ? Config.indent : 0;
            float textWidth = Config.textPaint.measureText(line.substring(0, end));
            float lineWidth = Config.width - Config.leftPadding - Config.rightPadding - indent;
            float extraSpace = lineWidth - textWidth; // 剩余空间

            float spacing = extraSpace / (groupCount > 1 ? groupCount - 1 : 1);
            if (spacing > Config.wordSpacingMax) { // 单词间距过大 - 改为 abc- 形式
                return 1 - count;
            }
            return end;
        }
        return count;
    }

    /**
     * 显示文字
     *
     * @param line 单行文字 & 当前行y坐标 & 缩进 & 分散对齐
     */
    private static void line2Words(Page page, Line line) {
        float indent = line.lineIndent ? Config.indent : 0;
        if (line.lineAlign || line.text.length() <= 1) { // 不需要分散对齐 | 只有一个字符
            page.wordList.add(new Word(line.text, Config.leftPadding + indent, line.y));
            return;
        }

        float textWidth = Config.textPaint.measureText(line.text);
        float lineWidth = Config.width - Config.leftPadding - Config.rightPadding - indent;
        float extraSpace = lineWidth - textWidth; // 剩余空间
        if (extraSpace <= 0) { // 没有多余空间 - 不需要分散对齐
            page.wordList.add(new Word(line.text, Config.leftPadding + indent, line.y));
            return;
        }

        Matcher matcher = Pattern.compile(Config.REGEX_CHARACTER).matcher(line.text);
        int wordCount = 0;
        while (matcher.find()) {
            wordCount++;
        }
        if (wordCount > 1) { // 多个单词 - 词间距
            float workSpacing = extraSpace / (wordCount - 1);
            float startX = Config.leftPadding + indent;
            float x;
            StringBuilder sb = new StringBuilder();
            int spacingCount = 0;
            matcher.reset();
            while (matcher.find()) {
                String word = matcher.group();
                x = startX + Config.textPaint.measureText(sb.toString()) + workSpacing * spacingCount;
                page.wordList.add(new Word(word, x, line.y));
                sb.append(word);
                spacingCount++;
            }
        } else { // 单个单词 - 字间距
            float workSpacing = extraSpace / (line.text.length() - 1);
            float startX = Config.leftPadding + indent;
            float x;
            for (int i = 0; i < line.text.length(); i++) {
                String word = String.valueOf(line.text.charAt(i));
                x = startX + Config.textPaint.measureText(line.text.substring(0, i)) + workSpacing * i;
                page.wordList.add(new Word(word, x, line.y));
            }
        }
    }

    private static void word2Path(Page page) {
        for (Word word : page.wordList) {
            Path temp = new Path();
            Config.textPaint.getTextPath(word.text, 0, word.text.length(), word.x, word.y, temp);
//            page.paths.add(temp);
            page.path.addPath(temp);
        }
    }
}
