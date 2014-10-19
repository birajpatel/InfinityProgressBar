package com.birin.infinityprogressbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

//http://en.wikipedia.org/wiki/Lemniscate_of_Bernoulli

public class InfinityProgressBar extends View {

	private static final int FIRST = 1;
	private static final int SECOND = 2;
	private static final int THIRD = 3;
	private static final int FOURTH = 4;

	Paint p1 = new Paint();
	Paint p2 = new Paint();
	Paint p3;

	public InfinityProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public InfinityProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	int progress = 0;

	class MyHandler extends Handler {
		public MyHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			progress += 10;
			if (progress >= pathLength) {
				progress = 0;
			}
			invalidate();
			spinHandler.sendEmptyMessageDelayed(0, 50);
		}
	};

	MyHandler spinHandler;

	public InfinityProgressBar(Context context) {
		super(context);
		p1.setStrokeWidth(10);
		p1.setAntiAlias(true);
		p1.setDither(true);
		p1.setFilterBitmap(true);
		p1.setColor(Color.RED);
		p1.setStyle(Style.FILL);
		p1.setFlags(Paint.ANTI_ALIAS_FLAG);
		p1.setStrokeCap(Paint.Cap.ROUND);

		p2.setStrokeWidth(20);
		p2.setColor(Color.BLUE);
		p2.setStyle(Style.STROKE);
		p2.setAntiAlias(true);
		p2.setDither(true);
		p2.setFilterBitmap(true);
		p2.setFlags(Paint.ANTI_ALIAS_FLAG);
		p2.setStrokeCap(Paint.Cap.ROUND);

		p3 = new Paint(p2);
		p3.setColor(Color.CYAN);

		preCompute();
		spinHandler = new MyHandler(Looper.getMainLooper());
		spinHandler.sendEmptyMessageDelayed(0, 0);
	}

	class MinToMaxComparator implements Comparator<PointF> {

		@Override
		public int compare(PointF lhs, PointF rhs) {
			return Float.valueOf(lhs.x).compareTo(rhs.x);
		}

	}

	private void preCompute() {
		long start = System.currentTimeMillis();
		for (int t = 0; t <= 720; t++) {
			double xDouble = (float) ((radius * Math.sqrt(2) * Math.cos(t)) / (Math
					.sin(t) * Math.sin(t) + 1));
			float x = (float) xDouble;
			float y = (float) (xDouble * Math.sin(t));
			x += startX;
			y += startY;
			if (isForthQuadrant(x, y)) {
				Q4.add(new PointF(x, y));
			}
		}
		Collections.sort(Q4, new MinToMaxComparator());
		pathLength = Q4.size() * 4;
		System.out.println("birajt finally after"
				+ (System.currentTimeMillis() - start));
	}

	ArrayList<PointF> Q4 = new ArrayList<PointF>();

	int pathLength;

	boolean isForthQuadrant(float x, float y) {
		return x >= startX && y >= startY;
	}

	final float radius = 100;
	final float startX = 200;
	final float startY = 200;

	@Override
	protected void onDraw(Canvas canvas) {
		for (PointF p : Q4) {
			canvas.drawPoint(p.x, p.y, p1);
			canvas.drawPoint(p.x, startY - (p.y - startY), p1);
			canvas.drawPoint(startX - (p.x - startX), p.y, p1);
			canvas.drawPoint(startX - (p.x - startX), startY - (p.y - startY),
					p1);
		}
		int ball1Quadrant = getQuadrantFromProgress(progress);
		int ball1Index = getBallIndex(progress, ball1Quadrant);
		PointF ballPoint1 = getBallPoint(ball1Index, ball1Quadrant);
		canvas.drawPoint(ballPoint1.x, ballPoint1.y, p2);

		int ball2Progress = 720 - progress;
		int ball2Quadrant = getQuadrantFromProgress(ball2Progress);
		int ball2Index = getBallIndex(ball2Progress, ball2Quadrant);
		System.out.println("biraj b2quad " + ball2Quadrant + " b2index "
				+ ball2Index);
		PointF ballPoint2 = getBallPoint(ball2Index, ball2Quadrant);
		canvas.drawPoint(ballPoint2.x, ballPoint2.y, p3);
	}

	private int getBallIndex(int progress, int ballQuadrant) {
		int index = 0;
		switch (ballQuadrant) {
		case 1:
			index = 180 - (progress - 540);
			break;
		case 2:
			index = 180 - (progress - 180);
			break;
		case 3:
			index = (progress - 360);
			break;
		case 4:
			index = progress;
			break;
		default:
			break;
		}
		return index;
	}

	private int getQuadrantFromProgress(int progress) {
		int quadrant;
		if (progress > 540) {
			quadrant = FIRST;
		} else if (progress > 360) {
			quadrant = THIRD;
		} else if (progress > 180) {
			quadrant = SECOND;
		} else {
			quadrant = FOURTH;
		}
		return quadrant;
	}

	private PointF getBallPoint(int index, int quadrant) {
		PointF drawing = Q4.get(index);
		float ballX = drawing.x;
		float ballY = drawing.y;
		if (quadrant == SECOND) {
			ballY = startY - (drawing.y - startY);
		} else if (quadrant == THIRD) {
			ballX = startX - (drawing.x - startX);
		} else if (quadrant == FIRST) {
			ballY = startY - (drawing.y - startY);
			ballX = startX - (drawing.x - startX);
		}
		return new PointF(ballX, ballY);
	}

}