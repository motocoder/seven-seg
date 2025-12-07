package llc.berserkr.sevensegment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;

public class SevenSegView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    //#################### SurfaceView mechanics ####################
    private final SurfaceHolder holder;
    private boolean running = false;

    //######################
    private int w;
    private int h;
    private float centerX;
    private float centerY;
    private float hundred60DP;
    private float eightyEightDP;
    private float channelDigitWidth;
    private float channelDigitHeight;
    private int twentyDP;
    private SevenSegmentPainter painter;

    /**
     * these constructors are defined to use this view in the xml file
     * @param context
     */
    public SevenSegView(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);

        init(context);

    }

    /**
     * these constructors are defined to use this view in the xml file
     *
     * @param context
     * @param attrs
     */
    public SevenSegView(Context context, AttributeSet attrs) {
        super(context, attrs);

        holder = getHolder();
        holder.addCallback(this);

        init(context);
    }

    /**
     * these constructors are defined to use this view in the xml file
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public SevenSegView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        holder = getHolder();
        holder.addCallback(this);

        init(context);
    }

    /**
     * these constructors are defined to use this view in the xml file
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     * @param defStyleRes
     */
    public SevenSegView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        holder = getHolder();
        holder.addCallback(this);

        init(context);

    }

    private void init(Context context) {
        this.painter = new SevenSegmentPainter(context);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        running = true;
        Thread renderThread = new Thread(this);
        renderThread.start();

        takeMeasurements(holder.getSurfaceFrame().width(), holder.getSurfaceFrame().height());

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        // Handle surface changes (e.g., orientation changes)
        takeMeasurements(width, height);

    }

    private static int NUM_POINTS = 50;
    private static int MAX_DIGITS = 5;

    private final Set<Pair<Float, Float>> points = new HashSet<>();
    private final IdentityHashMap<Pair<Float, Float>, Float> pointAngles = new IdentityHashMap<>();
    private final IdentityHashMap<Pair<Float, Float>, Integer> pointCounts = new IdentityHashMap<>();

    private final IdentityHashMap<Pair<Float, Float>, Float> pointWidths = new IdentityHashMap<>();
    private final IdentityHashMap<Pair<Float, Float>, Float> pointHeights = new IdentityHashMap<>();

    private void takeMeasurements(int width, int height) {

        this.w = width;
        this.h = height;

        centerX = w / 2f;
        centerY = h / 2f;

        //couple magic numbers here from the seven segment vectors to maintaint he aspect ratio of the digits
        //better way to get this might be from the loaded bitmap
        hundred60DP = dpToPx(160, getContext());
        eightyEightDP = dpToPx(88, getContext());

        this.channelDigitWidth = w / 30F; //arbitrary size to draw them

        this.channelDigitHeight = channelDigitWidth * hundred60DP / eightyEightDP; //math to calculate the digit height and maintain aspect ratio

        this.twentyDP = dpToPx(20, getContext());

        this.painter.setWidth(channelDigitWidth);
        this.painter.setHeight(channelDigitHeight);

        for(int i = 0; i < NUM_POINTS; i++) {

            final Pair<Float, Float> point = new Pair<>(
                    (float) (Math.random() * w), //random value between 0 and w
                    (float) (Math.random() * h) //random value between 0 and h
            );

            points.add(point);

            pointAngles.put(point, (float) (Math.random() * 360F));
            pointCounts.put(point, (int) (Math.random() * 6));

            final float pointWidth = w / ((float) (Math.random() * 15F) + 10F);
            final float pointHeight = pointWidth * hundred60DP / eightyEightDP;
            pointWidths.put(point, pointWidth);
            pointHeights.put(point, pointHeight);

        }

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        running = false;
    }



    @Override
    public void run() {
        while (running) {

            if (!holder.getSurface().isValid()) {
                continue;
            }

            final Canvas canvas = holder.lockCanvas();
            if (canvas != null) {

                // Clear the canvas (optional, but often necessary for full redraw)
                canvas.drawColor(Color.BLACK); // Or any background color

                for(final Pair<Float, Float> point : points) {

                    final float angle = pointAngles.get(point);

                    canvas.save();

                    final float pointWidth = pointWidths.get(point);

                    canvas.translate(point.first, point.second - pointWidth / 2f);
                    canvas.rotate(angle);

//                    canvas.translate(centerX, centerY -channelDigitHeight / 2f);

                    painter.setWidth(pointWidth);
                    painter.setHeight(pointHeights.get(point));

                    final int digitCount = pointCounts.get(point);

                    int [] digitValues = new int[digitCount];
                    for(int i = digitCount - 1; i >= 0; i--) {
                        digitValues[(digitCount-1) - i] = (int) (((System.currentTimeMillis()/10) / Math.pow(10, i)) % 10);
                    }

                    painter.drawDigits(canvas, digitCount, digitValues);

//                    //draw digit one with a value derived from the system clock
//                    painter.drawDigit(canvas, (int) ((System.currentTimeMillis() / 10000) % 10));
//                    canvas.translate(channelDigitWidth + twentyDP, 0);
//                    //draw digit two with a value derived from the system clock
//                    painter.drawDigit(canvas, (int) ((System.currentTimeMillis() / 1000) % 10));
                    canvas.restore();
                }

                // Perform drawing operations on the canvas
                holder.unlockCanvasAndPost(canvas);

            }
        }
    }


    /**
     * This returns the pixels for the dp value
     *
     * @param dp
     * @param context
     * @return
     */
    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /**
     * This method converts a vector drawable to a bitmap.
     *
     * @param context
     * @param drawableId
     * @return
     */
    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * This method changes the color of all the pixels in the bitmap (that aren't opaque) to the specified color.
     * @param bitmap
     * @param color
     * @return
     */
    public static Bitmap tintImage(Bitmap bitmap, int color) {
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        Bitmap bitmapResult = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapResult);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bitmapResult;
    }

}
