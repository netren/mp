package com.gtphoto.widget.common.util;

import android.graphics.Rect;
import android.text.TextPaint;

/**
 * Created by kennymac on 15/10/20.
 */
public class FontUtils {
    static public int getHeightOfMultiLineText(String text,int textSize, int maxWidth) {
        TextPaint paint = new TextPaint();
        paint.setTextSize(textSize);
        int index = 0;
        int lineCount = 0;
        while (index < text.length()) {
            index += paint.breakText(text, index, text.length(), true, maxWidth, null);
            lineCount++;
        }

        Rect bounds = new Rect();
        paint.getTextBounds("Yy", 0, 2, bounds);
        // obtain space between lines
        double lineSpacing = Math.max(0, ((lineCount - 1) * bounds.height() * 0.25));

        return (int) Math.floor(lineSpacing + lineCount * bounds.height());
    }

    static public boolean canFitWidthOfText(String text, int textSize, int maxWidth) {
        TextPaint paint = new TextPaint();
        paint.setTextSize(textSize);
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return maxWidth > rect.width();

    }

    //返回null 代表不能适应
    static public Integer checkFitTextWidth(String text, int maxFontSize, int minFontSize, int maxWidth) {
        for (int i = maxFontSize; i >= minFontSize; --i ) {
            if (canFitWidthOfText(text, i, maxWidth)) {
                return i;
            }
        }
        return null;
    }
}
