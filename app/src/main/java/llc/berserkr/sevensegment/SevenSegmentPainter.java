package llc.berserkr.sevensegment;

import static llc.berserkr.sevensegment.SevenSegView.getBitmapFromVectorDrawable;
import static llc.berserkr.sevensegment.SevenSegView.tintImage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * this is a utility class for drawing the seven segment display
 */
public class SevenSegmentPainter {

    private static Bitmap blackNumber;
    private static Map<SevenSeg, Bitmap> blackSevenSeg = new HashMap<>();
    private static Map<SevenSeg, Bitmap> blueSevenSeg = new HashMap<>();

    private float width;
    private float height;

    public SevenSegmentPainter(final Context context) {

        initBitmaps(context);

    }

    private static synchronized void initBitmaps(Context context) {

        if(blackNumber == null) { //only load bitmaps first try

            final Bitmap blackNumber = getBitmapFromVectorDrawable(context, R.drawable.number);
            SevenSegmentPainter.blackNumber = tintImage(blackNumber, Color.GRAY);

            final Bitmap sevenSegA = getBitmapFromVectorDrawable(context, R.drawable.sevenseg_a);
            final Bitmap sevenSegB = getBitmapFromVectorDrawable(context, R.drawable.sevenseg_b);
            final Bitmap sevenSegC = getBitmapFromVectorDrawable(context, R.drawable.sevenseg_c);
            final Bitmap sevenSegD = getBitmapFromVectorDrawable(context, R.drawable.sevenseg_d);
            final Bitmap sevenSegE = getBitmapFromVectorDrawable(context, R.drawable.sevenseg_e);
            final Bitmap sevenSegF = getBitmapFromVectorDrawable(context, R.drawable.sevenseg_f);
            final Bitmap sevenSegG = getBitmapFromVectorDrawable(context, R.drawable.sevenseg_g);

            {
                blackSevenSeg.put(SevenSeg.A, sevenSegA);
                blackSevenSeg.put(SevenSeg.B, sevenSegB);
                blackSevenSeg.put(SevenSeg.C, sevenSegC);
                blackSevenSeg.put(SevenSeg.D, sevenSegD);
                blackSevenSeg.put(SevenSeg.E, sevenSegE);
                blackSevenSeg.put(SevenSeg.F, sevenSegF);
                blackSevenSeg.put(SevenSeg.G, sevenSegG);
            }

            {
                blueSevenSeg.put(SevenSeg.A, tintImage(sevenSegA, Color.BLUE));
                blueSevenSeg.put(SevenSeg.B, tintImage(sevenSegB, Color.BLUE));
                blueSevenSeg.put(SevenSeg.C, tintImage(sevenSegC, Color.BLUE));
                blueSevenSeg.put(SevenSeg.D, tintImage(sevenSegD, Color.BLUE));
                blueSevenSeg.put(SevenSeg.E, tintImage(sevenSegE, Color.BLUE));
                blueSevenSeg.put(SevenSeg.F, tintImage(sevenSegF, Color.BLUE));
                blueSevenSeg.put(SevenSeg.G, tintImage(sevenSegG, Color.BLUE));
            }
        }
    }

    private static final Map<Integer, Set<SevenSeg>> digits = new HashMap<>();

    static {
        digits.put(0, getSegmentsForDigit(0));
        digits.put(1, getSegmentsForDigit(1));
        digits.put(2, getSegmentsForDigit(2));
        digits.put(3, getSegmentsForDigit(3));
        digits.put(4, getSegmentsForDigit(4));
        digits.put(5, getSegmentsForDigit(5));
        digits.put(6, getSegmentsForDigit(6));
        digits.put(7, getSegmentsForDigit(7));
        digits.put(8, getSegmentsForDigit(8));
        digits.put(9, getSegmentsForDigit(9));
    }

    public void drawDigit(Canvas canvas, int digit) {

        if(digit < 0 || digit > 9) {
            throw new RuntimeException("digit must be between 0 and 9");
        }

        final RectF destRect = new RectF(0, 0, width, height);
        final Rect srcRect = new Rect(0, 0, blackNumber.getWidth(), blackNumber.getHeight()); // Source rectangle in the bitmap

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(false);

        int alpha = 60; // For 50% opacity
        paint.setAlpha(alpha);
        canvas.drawBitmap(blackNumber, srcRect, destRect, paint);
        paint.setAlpha(255);

        for(final SevenSeg seg : digits.get(digit)) {
            final Bitmap bitmapSeg = blueSevenSeg.get(seg);
            canvas.drawBitmap(bitmapSeg, srcRect, destRect, paint);

        }

    }

    public void drawDigits(Canvas canvas, int digitCount, int [] digitValues) {

        canvas.save();

        canvas.translate(-(getWidth() / 2.0f * digitCount + digitCount * getWidth() * 0.1f), 0);

        for(int i = 0; i < digitCount; i++) {
            drawDigit(canvas, digitValues[i]);
            canvas.translate(getWidth() + i * getWidth() * 0.1f, 0);
        }

        canvas.restore();

    }


    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    private enum SevenSeg {
        A, B, C, D, E, F, G
    }

    private static Set<SevenSeg> getSegmentsForDigit(int digit) {

        if(digit < 0 || digit > 9) {
            throw new RuntimeException("digit must be between 0 and 9");
        }

        final Set<SevenSeg> drawingSegs = new HashSet<>();

        switch (digit) {
            case 2:
            {
                drawingSegs.add(SevenSeg.A);
                drawingSegs.add(SevenSeg.B);
                drawingSegs.add(SevenSeg.D);
                drawingSegs.add(SevenSeg.E);
                drawingSegs.add(SevenSeg.G);
                break;
            }
            case 5:
            {
                drawingSegs.add(SevenSeg.A);
                drawingSegs.add(SevenSeg.C);
                drawingSegs.add(SevenSeg.D);
                drawingSegs.add(SevenSeg.F);
                drawingSegs.add(SevenSeg.G);
                break;
            }
            case 6:
            {
                drawingSegs.add(SevenSeg.A);
                drawingSegs.add(SevenSeg.C);
                drawingSegs.add(SevenSeg.D);
                drawingSegs.add(SevenSeg.E);
                drawingSegs.add(SevenSeg.F);
                drawingSegs.add(SevenSeg.G);
                break;
            }
            case 3:
            {
                drawingSegs.add(SevenSeg.A);
                drawingSegs.add(SevenSeg.B);
                drawingSegs.add(SevenSeg.C);
                drawingSegs.add(SevenSeg.D);
                drawingSegs.add(SevenSeg.G);
                break;
            }
            case 4:
            {

                drawingSegs.add(SevenSeg.B);
                drawingSegs.add(SevenSeg.C);
                drawingSegs.add(SevenSeg.F);
                drawingSegs.add(SevenSeg.G);
                break;
            }

            case 1:
            {

                drawingSegs.add(SevenSeg.B);
                drawingSegs.add(SevenSeg.C);
                break;

            }
            case 7:
            {
                drawingSegs.add(SevenSeg.A);
                drawingSegs.add(SevenSeg.B);
                drawingSegs.add(SevenSeg.C);
                break;
            }
            case 8:
            {
                drawingSegs.add(SevenSeg.A);
                drawingSegs.add(SevenSeg.B);
                drawingSegs.add(SevenSeg.C);
                drawingSegs.add(SevenSeg.D);
                drawingSegs.add(SevenSeg.E);
                drawingSegs.add(SevenSeg.F);
                drawingSegs.add(SevenSeg.G);
                break;
            }
            case 9:
            {
                drawingSegs.add(SevenSeg.A);
                drawingSegs.add(SevenSeg.B);
                drawingSegs.add(SevenSeg.C);
                drawingSegs.add(SevenSeg.F);
                drawingSegs.add(SevenSeg.G);
                break;
            }
            case 0:
            {
                drawingSegs.add(SevenSeg.A);
                drawingSegs.add(SevenSeg.B);
                drawingSegs.add(SevenSeg.C);
                drawingSegs.add(SevenSeg.D);
                drawingSegs.add(SevenSeg.E);
                drawingSegs.add(SevenSeg.F);
                break;
            }

        }
        return drawingSegs;
    }

}
