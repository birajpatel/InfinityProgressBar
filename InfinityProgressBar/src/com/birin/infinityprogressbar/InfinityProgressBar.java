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
import android.util.AttributeSet;
import android.view.View;

//http://en.wikipedia.org/wiki/Lemniscate_of_Bernoulli
//http://mathworld.wolfram.com/EightCurve.html

public class InfinityProgressBar extends View {

	private static final int FIRST = 1;
	private static final int SECOND = 2;
	private static final int THIRD = 3;
	private static final int FOURTH = 4;
	private static final int PRECOMPUTE_LENGTH = 720;
	private final int PATH_LENGTH;
	private final int SINGLE_QUADRANT_LENGTH;

	final float radius = 100;
	final float startX = 200;
	final float startY = 200;

	Paint p1 = new Paint();
	Paint p2 = new Paint();
	Paint p3;
	Paint p4;
	Paint p5;
	Paint p6;
	Paint p7;
	Paint p8;
	Paint p9;

	public InfinityProgressBar(Context context) {
		super(context);
		init(null, -1);
		SINGLE_QUADRANT_LENGTH = Q4.size();
		PATH_LENGTH = SINGLE_QUADRANT_LENGTH * 4;
	}

	public InfinityProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, -1);
		SINGLE_QUADRANT_LENGTH = Q4.size();
		PATH_LENGTH = SINGLE_QUADRANT_LENGTH * 4;
	}

	public InfinityProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs, defStyle);
		SINGLE_QUADRANT_LENGTH = Q4.size();
		PATH_LENGTH = SINGLE_QUADRANT_LENGTH * 4;
	}

	private void init(AttributeSet attrs, int defStyle) {
		p1.setStrokeWidth(5);
		p1.setAntiAlias(true);
		p1.setDither(true);
		p1.setFilterBitmap(true);
		p1.setColor(Color.BLACK);
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
		p3.setColor(Color.GREEN);

		p4 = new Paint(p2);
		p4.setColor(Color.YELLOW);

		p5 = new Paint(p2);
		p5.setColor(Color.BLACK);

		p6 = new Paint(p2);
		p6.setColor(Color.MAGENTA);

		p7 = new Paint(p2);
		p7.setColor(Color.LTGRAY);

		p8 = new Paint(p2);
		p8.setColor(Color.rgb(255, 192, 0));

		p9 = new Paint(p2);
		p9.setColor(Color.RED);

		preCompute();
	}

	int progress = 0;

	class MinToMaxComparator implements Comparator<PointF> {

		@Override
		public int compare(PointF lhs, PointF rhs) {
			return Float.valueOf(lhs.x).compareTo(rhs.x);
		}

	}

	private void preCompute() {
		long start = System.currentTimeMillis();
		for (int t = 0; t <= PRECOMPUTE_LENGTH; t++) {
			double xDouble = (float) ((radius * Math.sqrt(2) * Math.cos(t)) / (Math
					.sin(t) * Math.sin(t) + 1));
			float x = (float) xDouble;
			float y = (float) (xDouble * Math.sin(t));
			// x = a * sin(t)
			// y = x * cos(t)
			x += startX;
			y += startY;
			if (isForthQuadrant(x, y)) {
				Q4.add(new Point(x, y));
			}
		}
		Collections.sort(Q4, new MinToMaxComparator());
		System.out.println("biraj precompute "
				+ (System.currentTimeMillis() - start));
	}

	ArrayList<Point> Q4 = new ArrayList<Point>();

	boolean isForthQuadrant(float x, float y) {
		return x >= startX && y >= startY;
	}

	@Override
	protected void onDraw(Canvas canvas) {
//		drawPath(canvas);
		drawBall(canvas, p2, 0);
		drawBall(canvas, p3, 1);
		drawBall(canvas, p4, 2);
		drawBall(canvas, p5, 3);

		drawBall(canvas, p6, 4);
		drawBall(canvas, p7, 5);
		drawBall(canvas, p8, 6);
		drawBall(canvas, p9, 7);
		progress += 10;
		if (progress >= PATH_LENGTH) {
			progress = 0;
		}
		postInvalidateDelayed(50);
	}

	int totalBalls = 8;

	private void drawBall(Canvas canvas, Paint ballPaint, int quadrantOffset) {
		final int trailLength = PATH_LENGTH / totalBalls;
		
		for (int i = progress, j = trailLength; i > (progress - trailLength); i--, j--) {
			int ballProgress = ((i + quadrantOffset * trailLength) % PATH_LENGTH);
			if (ballProgress < 0) {
				ballProgress += PATH_LENGTH;
			}
			if (trailLength != 1) {
				int alpha = (int) (40 * ((j * 1.0) / trailLength));
				ballPaint.setAlpha(alpha);
			}
			int ballQuadrant = getQuadrantFromProgress(ballProgress);
			Point fourthQuadrantBallPoint = pickCorrespondingFourthQuadrantPoint(
					ballProgress, ballQuadrant);
			final float ballX;
			final float ballY;
			if (ballQuadrant == SECOND) {
				ballX = fourthQuadrantBallPoint.x;
				ballY = fourthQuadrantBallPoint.ny;
			} else if (ballQuadrant == THIRD) {
				ballX = fourthQuadrantBallPoint.nx;
				ballY = fourthQuadrantBallPoint.y;
			} else if (ballQuadrant == FIRST) {
				ballX = fourthQuadrantBallPoint.nx;
				ballY = fourthQuadrantBallPoint.ny;
			} else {
				ballX = fourthQuadrantBallPoint.x;
				ballY = fourthQuadrantBallPoint.y;
			}
			canvas.drawPoint(ballX, ballY, ballPaint);
		}
	}

	private void drawPath(Canvas canvas) {
		for (Point p : Q4) {
			canvas.drawPoint(p.x, p.y, p1);
			canvas.drawPoint(p.x, p.ny, p1);
			canvas.drawPoint(p.nx, p.y, p1);
			canvas.drawPoint(p.nx, p.ny, p1);
		}
	}

	private Point pickCorrespondingFourthQuadrantPoint(int progress,
			int quadrant) {
		int relativeIndexForData = 0;
		switch (quadrant) {
		case FIRST:
			relativeIndexForData = (4 * SINGLE_QUADRANT_LENGTH) - progress - 1;
			break;
		case SECOND:
			relativeIndexForData = (2 * SINGLE_QUADRANT_LENGTH) - progress - 1;
			break;
		case THIRD:
			relativeIndexForData = (progress - (2 * SINGLE_QUADRANT_LENGTH));
			break;
		case FOURTH:
			relativeIndexForData = progress;
			break;
		default:
			break;
		}
		return Q4.get(relativeIndexForData);
	}

	private int getQuadrantFromProgress(int progress) {
		int quadrant;
		if (progress >= (3 * SINGLE_QUADRANT_LENGTH)) {
			quadrant = FIRST;// (-,-)
		} else if (progress >= (2 * SINGLE_QUADRANT_LENGTH)) {
			quadrant = THIRD;// (-,+)
		} else if (progress >= SINGLE_QUADRANT_LENGTH) {
			quadrant = SECOND;// (+,-)
		} else {
			quadrant = FOURTH;// (+,+)
		}
		return quadrant;
	}

	class Point extends PointF {

		final float nx;
		final float ny;

		Point(float x, float y) {
			super(x, y);
			nx = (2 * startX - x);
			ny = (2 * startY - y);
		}

	}

}