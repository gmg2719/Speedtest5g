//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package cn.nokia.speedtest5g.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;

import cn.nokia.speedtest5g.R;

public class RadarView extends View {
    public int a;
    public int b;
    public int c;
    public int d;
    public int e;
    public int f;
    public boolean g;
    public boolean h;
    public float i;
    public float j;
    public Paint k;
    public Paint l;
    public Paint m;
    public float n;
    public boolean o;
    public ArrayList<RadarView.a> p;

    public RadarView(Context var1) {
        super(var1);
        this.a = Color.parseColor("#101B2D");
        int var2;
        int var10009 = var2 = this.a;
        this.b = var2;
        this.c = 3;
        this.d = var2;
        this.e = var10009;
        this.f = 4;
        this.g = true;
        this.h = true;
        this.i = 3.0F;
        this.j = 3.0F;
        this.o = false;
        this.p = new ArrayList();
        this.a();
    }

    public RadarView(Context var1, @Nullable AttributeSet var2) {
        super(var1, var2);
        Context var10002 = var1;
        this.a = Color.parseColor("#101B2D");
        int var3;
        int var10012 = var3 = this.a;
        this.b = var3;
        this.c = 3;
        this.d = var3;
        this.e = var10012;
        this.f = 4;
        this.g = true;
        this.h = true;
        this.i = 3.0F;
        this.j = 3.0F;
        this.o = false;
        this.p = new ArrayList();
        this.a(var10002, var2);
        this.a();
    }

    public RadarView(Context var1, @Nullable AttributeSet var2, int var3) {
        super(var1, var2, var3);
        Context var10002 = var1;
        this.a = Color.parseColor("#101B2D");
        int var4;
        int var10012 = var4 = this.a;
        this.b = var4;
        this.c = 3;
        this.d = var4;
        this.e = var10012;
        this.f = 4;
        this.g = true;
        this.h = true;
        this.i = 3.0F;
        this.j = 3.0F;
        this.o = false;
        this.p = new ArrayList();
        this.a(var10002, var2);
        this.a();
    }

    public static int a(int var0, int var1) {
        return Color.argb(var1, Color.red(var0), Color.green(var0), Color.blue(var0));
    }

    public void onMeasure(int var1, int var2) {
        Context var10001 = this.getContext();
        float var3 = 400.0F;
        DisplayMetrics var4 = var10001.getResources().getDisplayMetrics();
        int var5 = (int)TypedValue.applyDimension(1, var3, var4);
        int var6;
        int var10000 = var6 = MeasureSpec.getMode(var1);
        var1 = MeasureSpec.getSize(var1);
        if (var10000 == 1073741824) {
            var6 = var1;
        } else {
            var10000 = var6;
            var6 = this.getPaddingLeft() + var5;
            var6 += this.getPaddingRight();
            if (var10000 == -2147483648) {
                var6 = Math.min(var6, var1);
            }
        }

        var1 = Math.max(var6, this.getSuggestedMinimumWidth());
        var10000 = var6 = MeasureSpec.getMode(var2);
        var2 = MeasureSpec.getSize(var2);
        if (var10000 == 1073741824) {
            var5 = var2;
        } else {
            var5 += this.getPaddingTop();
            var5 += this.getPaddingBottom();
            if (var6 == -2147483648) {
                var5 = Math.min(var5, var2);
            }
        }

        this.setMeasuredDimension(var1, Math.max(var5, this.getSuggestedMinimumHeight()));
    }

    public void onDraw(Canvas var1) {
        int var2 = Math.min(this.getWidth() - this.getPaddingLeft() - this.getPaddingRight(), this.getHeight() - this.getPaddingTop() - this.getPaddingBottom()) / 2;
        int var3 = this.getPaddingLeft();
        var3 += (this.getWidth() - this.getPaddingLeft() - this.getPaddingRight()) / 2;
        int var4 = this.getPaddingTop();
        var4 += (this.getHeight() - this.getPaddingTop() - this.getPaddingBottom()) / 2;

        int var5;
        int var6;
        for(var5 = 0; var5 < (var6 = this.c); ++var5) {
            var1.drawCircle((float)var3, (float)var4, (float)(var2 - var2 / var6 * var5), this.k);
        }

        if (this.g) {
            var1.drawLine((float)(var3 - var2), (float)var4, (float)(var3 + var2), (float)var4, this.k);
            var1.drawLine((float)var3, (float)(var4 - var2), (float)var3, (float)(var4 + var2), this.k);
        }

        if (this.o) {
            if (this.h) {
                if (this.p.size() < this.f && (int)(Math.random() * 20.0D) == 0) {
                    double var12;
                    int var7 = (int)(Math.random() * (var12 = (double)(var2 - 20)));
                    var5 = (int)(Math.random() * (double)((int)Math.sqrt(var12 * 1.0D * var12 - (double)(var7 * var7))));
                    if ((int)(Math.random() * 2.0D) == 0) {
                        var6 = var3 - var7;
                    } else {
                        var6 = var3 + var7;
                    }

                    if ((int)(Math.random() * 2.0D) == 0) {
                        var5 = var4 - var5;
                    } else {
                        var5 += var4;
                    }

                    this.p.add(new RadarView.a(var6, var5, 0.0F, this.e));
                }

                Iterator var13;
                RadarView.a var14;
                float var15;
                RadarView.a var10000;
                for(var13 = this.p.iterator(); var13.hasNext(); var10000.e -= 4.25F / var15) {
                    var10000 = var14 = (RadarView.a)var13.next();
                    this.m.setColor(a(var14.d, (int)var14.e));
                    var1.drawCircle((float)var14.a, (float)var14.b, var14.c, this.m);
                    float var16 = var14.c;
                    var15 = this.j;
                    var10000.c = 0.33333334F / var15 + var16;
                }

                var13 = this.p.iterator();

                label54:
                while(true) {
                    do {
                        if (!var13.hasNext()) {
                            break label54;
                        }
                    } while((var14 = (RadarView.a)var13.next()).c <= 20.0F && var14.e >= 0.0F);

                    var13.remove();
                }
            }

            float var17;
            float var10005 = var17 = (float)var3;
            float var11;
            float var10006 = var11 = (float)var4;
            int[] var9;
            int[] var10014 = var9 = new int[5];
            var9[0] = 0;
            var9[1] = a(this.d, 0);
            var9[2] = a(this.d, 168);
            var9[3] = a(this.d, 255);
            var10014[4] = a(this.d, 255);
            SweepGradient var10 = new SweepGradient(var17, var11, var10014, new float[]{0.0F, 0.6F, 0.99F, 0.998F, 1.0F});
            this.l.setShader(var10);
            var1.rotate(this.n + -90.0F, var17, var11);
            var1.drawCircle(var10005, var10006, (float)var2, this.l);
            float var8 = this.n;
            this.n = (360.0F / this.i / 60.0F + var8) % 360.0F;
            this.invalidate();
        }

    }

    public void b() {
        if (!this.o) {
            this.o = true;
            this.invalidate();
        }

    }

    public void c() {
        if (this.o) {
            this.o = false;
            this.p.clear();
            this.n = 0.0F;
        }

    }

    public final void a(Context var1, AttributeSet var2) {
        if (var2 != null) {
            TypedArray var3;
            TypedArray var10002 = var3 = var1.obtainStyledAttributes(var2, R.styleable.RadarView);
            this.b = var3.getColor(R.styleable.RadarView_circleColor, this.a);
            this.c = var10002.getInt(R.styleable.RadarView_circleNum, this.c);
            if (this.c < 1) {
                this.c = 3;
            }

            this.d = var3.getColor(R.styleable.RadarView_sweepColor, this.a);
            this.e = var3.getColor(R.styleable.RadarView_raindropColor, this.a);
            this.f = var3.getInt(R.styleable.RadarView_raindropNum, this.f);
            this.g = var3.getBoolean(R.styleable.RadarView_showCross, true);
            this.h = var3.getBoolean(R.styleable.RadarView_showRaindrop, true);
            this.i = var3.getFloat(R.styleable.RadarView_speed, this.i);
            if (this.i <= 0.0F) {
                this.i = 3.0F;
            }

            this.j = var3.getFloat(R.styleable.RadarView_flicker, this.j);
            if (this.j <= 0.0F) {
                this.j = 3.0F;
            }

            var3.recycle();
        }

    }

    public final void a() {
        this.k = new Paint();
        this.k.setColor(this.b);
        this.k.setStrokeWidth(5.0F);
        this.k.setStyle(Style.STROKE);
        this.k.setAntiAlias(true);
        this.m = new Paint();
        this.m.setStyle(Style.FILL);
        this.m.setAntiAlias(true);
        this.l = new Paint();
        this.l.setAntiAlias(true);
    }

    private static class a {
        public int a;
        public int b;
        public float c;
        public int d;
        public float e = 255.0F;

        public a(int var1, int var2, float var3, int var4) {
            this.a = var1;
            this.b = var2;
            this.c = var3;
            this.d = var4;
        }
    }
}
