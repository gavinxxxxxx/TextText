package me.gavin.app.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.gavin.app.Config;
import me.gavin.app.StreamHelper;
import me.gavin.app.Utils;
import me.gavin.util.DisplayUtil;

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
    public boolean reverseFlag;

    public String mText;
    public String[] mTextSp;

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
        page.reverseFlag = isReverse;
        if (!isReverse) { // 正向
            page.pageStart = offset;
            page.isFirst = page.pageStart <= 0;
            page.mText = StreamHelper.getText(book.open(), page.pageStart, Config.pagePreCount);
            page.mTextSp = page.mText == null ? null : page.mText.split(Config.REGEX_SEGMENT);
            page.firstLineIndent = true;
            page.lastLineAlign = true;
        } else { // 反向
            page.pageEnd = offset;
            page.isLast = page.pageEnd >= book.getLength();
            page.mText = StreamHelper.getText(book.open(), page.pageEnd - Config.pagePreCount, Config.pagePreCount);
            page.mTextSp = page.mText == null ? null : page.mText.split(Config.REGEX_SEGMENT);
            page.firstLineIndent = true;
            page.lastLineAlign = true;
        }
        page.layoutText();
        return page;
    }

    private void layoutText() {
        lineList.clear();

        float y = Config.topPadding - Config.textTop;
        String subText = mText; // 子字符串 - 计算字符数量
        for (int i = 0; i < mTextSp.length; i++) {
            String segment = mTextSp[i];
            int start = 0;
            while (start < segment.length()) {
                String remaining = segment.substring(start);
                if (!reverseFlag && y + Config.textBottom - 2 > height - Config.bottomPadding) {
                    pageLimit = mText.indexOf(subText); // 计算字符数量
                    pageEnd = pageStart + pageLimit;
                    isLast = pageEnd >= book.getLength();
                    return;
                }

                boolean lineIndent = i == 0 && start == 0 && firstLineIndent
                        || i != 0 && start == 0;
                int count = breakText(remaining, lineIndent);
                boolean lineAlign = start + Math.abs(count) >= segment.length()
                        && !(isReverse && lastLineAlign && i == mTextSp.length - 1);

                String text = segment.substring(start, start + Math.abs(count));
                Line line = new Line(text.trim() + (count > 0 ? "" : "-"), y, lineIndent, lineAlign);
                lineList.add(line);

                y = y + Config.textHeight + Config.lineSpacing; // TODO: 2017/12/18 *? | 分段没分好
                start += Math.abs(count);
                subText = subText.substring(subText.indexOf(text) + text.length());
            }
            // 段间距
            y += Config.segmentSpacing;
        }
        if (reverseFlag) {
            float maxY = y - Config.segmentSpacing - Config.lineSpacing; // y 最大值
            float extraY = maxY - height + Config.bottomPadding - Config.textTop;
            subText = mText; // 子字符串 - 计算字符数量
            for (Line line : lineList) {
                if (line.y - 2 < extraY) { // TODO: 2017/12/18 *? -> line
                    subText = subText.substring(subText.indexOf(line.text) + line.text.length());
                } else {
                    break;
                }
            }
            pageLimit = subText.length();
            pageStart = pageEnd - pageLimit;
            isFirst = pageStart <= 0;
            mText = Utils.trim(mText.substring(mText.length() - pageLimit));
            mTextSp = mText.split(Config.REGEX_SEGMENT);
            reverseFlag = false;
            layoutText();
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
        } else if (remaining.substring(count - 1, count + 1).matches(Config.REGEX_WORD2)) { // 上一行最后一个字符和下一行第一个字符符合单词形式
            String line = remaining.substring(0, count);
            Matcher matcher = Pattern.compile(Config.REGEX_WORD3).matcher(line);
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
