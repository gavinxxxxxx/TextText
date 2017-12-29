package me.gavin.app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.gavin.app.Config;
import me.gavin.app.StreamHelper;
import me.gavin.app.Utils;
import me.gavin.util.DisplayUtil;
import me.gavin.util.L;

/**
 * ViewModel
 *
 * @author gavin.xiong 2017/12/14
 */
public class Page {

    public Book book;

    public long pageStart;
    public int pageLimit;
    public long pageEnd;

    public boolean isFirst;
    public boolean isLast;

    public boolean firstLineIndent; // 第一行缩进
    public boolean lastLineAlign; // 最后一行对齐 - 只反向有用

    public boolean isReverse;

    public String mText;
    public String[] mTextSp;
    public String fix;

    public int width = DisplayUtil.getScreenWidth();
    public int height = DisplayUtil.getScreenHeight();

    public final List<Line> lineList;

    private Page() {
        lineList = new ArrayList<>();
    }

    public static Page fromBook(Book book, long offset, boolean isReverse) {
        Page page = new Page();
        page.book = book;
        page.isReverse = isReverse;
        if (!isReverse) { // 正向
            page.pageStart = offset;
            page.isFirst = page.pageStart <= 0;
            page.mText = StreamHelper.getText(book.open(), page.pageStart, Config.pagePreCount);
            page.fix = StreamHelper.getText(book.open(), page.pageStart - Config.segmentPreCount, Config.segmentPreCount);
            page.mTextSp = page.mText == null ? null : Utils.trim(page.mText).split(Config.REGEX_SEGMENT);
            page.firstLineIndent = page.mText.matches(Config.REGEX_SEGMENT_SUFFIX) || page.fix.matches(Config.REGEX_SEGMENT_PREFIX);
            page.lastLineAlign = true;
        } else { // 反向
            page.pageEnd = offset;
            page.isLast = page.pageEnd >= book.getLength();
            page.mText = StreamHelper.getText(book.open(), Math.max(page.pageEnd - Config.pagePreCount, 0), Config.pagePreCount);
            page.fix = StreamHelper.getText(book.open(), page.pageEnd, Config.segmentPreCount);
            page.mTextSp = page.mText == null ? null : Utils.trim(page.mText).split(Config.REGEX_SEGMENT);
            page.firstLineIndent = true;
            page.lastLineAlign = !page.mText.matches(Config.REGEX_SEGMENT_PREFIX) && !page.fix.matches(Config.REGEX_SEGMENT_SUFFIX);
        }
        L.e(page.mText.length());
        page.layoutText();
        return page;
    }

    private void layoutText() {
        lineList.clear();
        int y = Config.topPadding;
        String subText = Utils.trim(mText);
        for (int i = 0; i < mTextSp.length; i++) {
            String segment = mTextSp[i];
            int segmentStart = 0;
            while (segmentStart < segment.length()) {
                if (!isReverse && y + Config.textHeight > height - Config.bottomPadding) {
                    pageLimit = mText.indexOf(subText);
                    pageEnd = pageStart + pageLimit;
                    isLast = pageEnd > book.getLength();
                    return;
                }
                String remaining = segment.substring(segmentStart);
                boolean lineIndent = i == 0 && segmentStart == 0 && firstLineIndent
                        || i != 0 && segmentStart == 0;
                int count = breakText(remaining, lineIndent);
                String suffix = count < 0 ? "-" : "";
                count = Math.abs(count);
                String text = segment.substring(segmentStart, segmentStart + count);
                boolean lineAlignNo = segmentStart + count >= segment.length() // 不对齐 - 段落尾行 && 非反向最后一行
                        && !(isReverse && lastLineAlign && i == mTextSp.length - 1);
                Line line = new Line(Utils.trim(text), suffix, y - Config.textTop, lineIndent, lineAlignNo);
                lineList.add(line);

                y += Config.textHeight + Config.lineSpacing;
                segmentStart += count;
                subText = subText.substring(subText.indexOf(text) + text.length());
            }
            y += Config.segmentSpacing;
        }
        if (isReverse) {
            List<Line> lines = new ArrayList<>();
            y = y - Config.segmentSpacing - Config.lineSpacing;
            subText = mText; // 子字符串 - 计算字符数量

            int ey = y - height + Config.bottomPadding + Config.topPadding;
            for (Line line : lineList) {
                if (line.y + Config.textTop < ey) {
                    subText = subText.substring(subText.indexOf(line.src) + line.src.length());
                } else {
                    lines.add(line);
                }
            }

            ey = lines.get(0).y - Config.topPadding;
            for (Line line : lines) {
                line.y = line.y - ey + Config.textSize;
            }
            lineList.clear();
            lineList.addAll(lines);

            pageLimit = subText.length();
            pageStart = pageEnd - pageLimit;
            isFirst = pageStart <= 0;
        }
    }

    private int breakText(String remaining, boolean lineIndent) {
        int count = Config.textPaint.breakText(remaining, true, width
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
            float lineWidth = width - Config.leftPadding - Config.rightPadding - indent;
            float extraSpace = lineWidth - textWidth; // 剩余空间

            float spacing = extraSpace / (groupCount > 1 ? groupCount - 1 : 1);
            if (spacing > Config.wordSpacingMax) { // 单词间距过大 - 改为 abc- 形式
                return 1 - count;
            }
            return end;
        }
        return count;
    }
}
