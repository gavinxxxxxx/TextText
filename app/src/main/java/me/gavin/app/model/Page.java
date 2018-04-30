package me.gavin.app.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.gavin.app.Config;
import me.gavin.app.StreamHelper;
import me.gavin.app.Utils;

/**
 * Page
 *
 * @author gavin.xiong 2017/12/14
 */
public class Page {

    public Book book;
    public String chapter;

    public int index;
    public long start;
    public int limit;
    public long end;

    public boolean isFirst;
    public boolean isLast;

    public boolean isReverse; // 反向

    public boolean indent; // 页面第一行是否缩进 - 第一行可能不是自然段起始位置
    public boolean align; // 页面最后一行是否分散对齐 - 只反向有用

    public String mText; // 页面预加载文字 - 直接读取
    public String[] mTextSp; // 页面段落数组

    public final List<Line> lineList; // 页面文字分行
    public final List<Word> wordList; // 页面按字词拆分

    public int type;
    public boolean ready;

    public Page() {
        lineList = new ArrayList<>();
        wordList = new LinkedList<>();
    }

    public static Page fromBook(Book book, long offset, boolean isReverse) throws IOException {
        Page page = new Page();
        page.book = book;
        page.isReverse = isReverse;
        if (!isReverse) { // 正向
            page.start = offset;
            page.isFirst = page.start <= 0;
            page.mText = StreamHelper.getText(book.open(), book.getCharset(), page.start, (int) Math.min(Config.pagePreCount, book.getLength() - page.start));
            page.mTextSp = Utils.trim(page.mText).split(Config.REGEX_SEGMENT);
            page.align = true;
            if (page.isFirst || page.mText.matches(Config.REGEX_SEGMENT_SUFFIX)) {
                page.indent = true;
            } else {
                int preCount = (int) (page.start >= Config.segmentPreCount ? Config.segmentPreCount : page.start);
                String fix = StreamHelper.getText(book.open(), book.getCharset(), page.start - preCount, preCount);
                page.indent = fix.matches(Config.REGEX_SEGMENT_PREFIX);
            }
        } else { // 反向
            page.end = offset;
            page.isLast = page.end >= book.getLength();
            page.mText = StreamHelper.getText(book.open(), book.getCharset(), Math.max(page.end - Config.pagePreCount, 0), (int) Math.min(Config.pagePreCount, page.end));
            page.mTextSp = Utils.trim(page.mText).split(Config.REGEX_SEGMENT);
            page.indent = true;
            String fix = StreamHelper.getText(book.open(), book.getCharset(), page.end, Config.segmentPreCount);
            page.align = !page.mText.matches(Config.REGEX_SEGMENT_PREFIX) && !fix.matches(Config.REGEX_SEGMENT_SUFFIX);
        }
        page.text2Line();
        for (Line line : page.lineList) {
            page.line2Words(line);
        }
        return page;
    }

    public static Page fromChapter(String chapter, long offset, boolean isReverse) throws IOException {
        Page page = new Page();
        page.isReverse = isReverse;
        if (!isReverse) { // 正向
            page.start = offset;
            page.isFirst = page.start <= 0;
            page.mText = chapter.substring((int) page.start, Math.min((int) page.start + Config.pagePreCount, chapter.length()));
            page.mTextSp = Utils.trim(page.mText).split(Config.REGEX_SEGMENT);
            page.align = true;
            if (page.isFirst || page.mText.matches(Config.REGEX_SEGMENT_SUFFIX)) {
                page.indent = true;
            } else {
                int preCount = (int) (page.start >= Config.segmentPreCount ? Config.segmentPreCount : page.start);
                String fix = chapter.substring((int) page.start - preCount, (int) page.start);
                page.indent = fix.matches(Config.REGEX_SEGMENT_PREFIX);
            }
        } else { // 反向
            page.end = offset;
            page.isLast = page.end >= chapter.length();
            int si = Math.max((int) page.end - Config.pagePreCount, 0);
            page.mText = chapter.substring(si, si + Math.min(Config.pagePreCount, (int) page.end));
            page.mTextSp = Utils.trim(page.mText).split(Config.REGEX_SEGMENT);
            page.indent = true;
            String fix = chapter.substring((int) page.end, (int) page.end + Config.segmentPreCount);
            page.align = !page.mText.matches(Config.REGEX_SEGMENT_PREFIX) && !fix.matches(Config.REGEX_SEGMENT_SUFFIX);
        }
        page.text2Line();
        for (Line line : page.lineList) {
            page.line2Words(line);
        }
        return page;
    }

    public Page last() throws IOException {
        return isFirst ? null : Page.fromBook(book, start, true);
    }

    public Page next() throws IOException {
        return isLast ? null : Page.fromBook(book, end, false);
    }

    public void prepare() {
        if (book.getType() == Book.TYPE_LOCAL) {
            text2Line();
        } else {
            text2LineForOnline();
        }
        for (Line line : lineList) {
            line2Words(line);
        }
    }

    private void text2Line() {
        int y = Config.topPadding; // 行文字顶部
        String subText = Utils.trim(mText);
        for (int i = 0; i < mTextSp.length; i++) {
            String segment = mTextSp[i];
            int segmentStart = 0;
            while (segmentStart < segment.length()) {
                if (!isReverse && y + Config.textHeight > Config.height - Config.bottomPadding) { // 正向 & 已排满页面
                    limit = mText.indexOf(subText);
                    end = start + limit;
                    isLast = end >= book.getLength();
                    return;
                }
                String remaining = segment.substring(segmentStart);
                boolean lineIndent = i == 0 && segmentStart == 0 && indent
                        || i != 0 && segmentStart == 0;
                int count = breakText(remaining, lineIndent);
                String suffix = count < 0 ? "-" : "";
                count = Math.abs(count);
                String text = segment.substring(segmentStart, segmentStart + count);
                boolean lineAlignNo = segmentStart + count >= segment.length() // 不对齐 - 段落尾行 && 非反向最后一行
                        && !(isReverse && align && i == mTextSp.length - 1);
                Line line = new Line(Utils.trim(text), suffix, y - Config.textTop, lineIndent, lineAlignNo);
                lineList.add(line);

                y += Config.textHeight + Config.lineSpacing;
                segmentStart += count;
                subText = subText.substring(subText.indexOf(text) + text.length());
            }
            y += Config.segmentSpacing;
        }
        if (!isReverse && start + mText.length() >= book.getLength()) { // 正向 & 还能显示却没有了
            end = book.getLength();
            limit = (int) (end - start);
            isLast = true;
        } else if (isReverse) { // 反向
            List<Line> lines = new ArrayList<>();
            y = y - Config.segmentSpacing - Config.lineSpacing; // 最后一行文字底部 - 去掉多余的空隙
            subText = mText; // 子字符串 - 计算字符数量

            int ey = y - Config.height + Config.bottomPadding + Config.topPadding; // 超出的高度
            for (Line line : lineList) {
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
            lineList.clear();
            lineList.addAll(lines);

            limit = subText.length();
            start = end - limit;
            isFirst = start <= 0;
        }
    }

    private void text2LineForOnline() {
        int y = Config.topPadding; // 行文字顶部
        String subText = Utils.trim(mText);
        for (int i = 0; i < mTextSp.length; i++) {
            String segment = mTextSp[i];
            int segmentStart = 0;
            while (segmentStart < segment.length()) {
                if (!isReverse && y + Config.textHeight > Config.height - Config.bottomPadding) { // 正向 & 已排满页面
                    limit = mText.indexOf(subText);
                    end = start + limit;
                    isLast = end >= book.getText().length();
                    return;
                }
                String remaining = segment.substring(segmentStart);
                boolean lineIndent = i == 0 && segmentStart == 0 && indent
                        || i != 0 && segmentStart == 0;
                int count = breakText(remaining, lineIndent);
                String suffix = count < 0 ? "-" : "";
                count = Math.abs(count);
                String text = segment.substring(segmentStart, segmentStart + count);
                boolean lineAlignNo = segmentStart + count >= segment.length() // 不对齐 - 段落尾行 && 非反向最后一行
                        && !(isReverse && align && i == mTextSp.length - 1);
                Line line = new Line(Utils.trim(text), suffix, y - Config.textTop, lineIndent, lineAlignNo);
                lineList.add(line);

                y += Config.textHeight + Config.lineSpacing;
                segmentStart += count;
                subText = subText.substring(subText.indexOf(text) + text.length());
            }
            y += Config.segmentSpacing;
        }
        if (!isReverse && start + mText.length() >= book.getLength()) { // 正向 & 还能显示却没有了
            end = book.getText().length();
            limit = (int) (end - start);
            isLast = true;
        } else if (isReverse) { // 反向
            List<Line> lines = new ArrayList<>();
            y = y - Config.segmentSpacing - Config.lineSpacing; // 最后一行文字底部 - 去掉多余的空隙
            subText = mText; // 子字符串 - 计算字符数量

            int ey = y - Config.height + Config.bottomPadding + Config.topPadding; // 超出的高度
            for (Line line : lineList) {
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
            lineList.clear();
            lineList.addAll(lines);

            limit = subText.length();
            start = end - limit;
            isFirst = start <= 0;
        }
    }

    private int breakText(String remaining, boolean lineIndent) {
        int count = Config.textPaint.breakText(remaining, true, Config.width
                - Config.leftPadding - Config.rightPadding - (lineIndent ? Config.indent : 0), null);
        return count >= remaining.length() ? count : countReset(remaining, count, lineIndent);
    }

    /**
     * 分行衔接调整 - 标点 & 单词 - 只考虑标准情况
     *
     * @return count 数量 负数代表要加 -
     */
    private int countReset(String remaining, int count, boolean lineIndent) {
        if (remaining.substring(count, count + 1).matches(Config.REGEX_PUNCTUATION_N)) { // 下一行第一个是标点
            if (!remaining.substring(count - 2, count)
                    .matches(Config.REGEX_PUNCTUATION_N + "*")) { // 当前行末两不都是标点 - 标准状况下
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
    private void line2Words(Line line) {
        float indent = line.lineIndent ? Config.indent : 0;
        if (line.lineAlign || line.text.length() <= 1) { // 不需要分散对齐 | 只有一个字符
            wordList.add(new Word(line.text, Config.leftPadding + indent, line.y));
            return;
        }

        float textWidth = Config.textPaint.measureText(line.text);
        float lineWidth = Config.width - Config.leftPadding - Config.rightPadding - indent;
        float extraSpace = lineWidth - textWidth; // 剩余空间
        if (extraSpace <= 0) { // 没有多余空间 - 不需要分散对齐
            wordList.add(new Word(line.text, Config.leftPadding + indent, line.y));
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
                wordList.add(new Word(word, x, line.y));
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
                wordList.add(new Word(word, x, line.y));
            }
        }
    }
}
