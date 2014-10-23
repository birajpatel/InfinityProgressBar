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
		p5.setColor(Color.RED);

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
				Q4.add(new PointF(x, y));
			}
		}
		Collections.sort(Q4, new MinToMaxComparator());
	}

	ArrayList<PointF> Q4 = new ArrayList<PointF>();

	boolean isForthQuadrant(float x, float y) {
		return x >= startX && y >= startY;
	}

	@Override
	protected void onDraw(Canvas canvas) {
//		drawPath(canvas);
		drawBalls(canvas);
		progress += 10;
		if (progress >= PATH_LENGTH) {
			progress = 0;
		}
		postInvalidateDelayed(50);
	}

	private void drawBalls(Canvas canvas) {
		 drawBall1(canvas);
		 drawBall2(canvas);
		drawBall3(canvas);
		drawBall4(canvas);
	}

	private void drawBall4(Canvas canvas) {
//		int ball4Prog = ((progress + 3 * SINGLE_QUADRANT_LENGTH) % PATH_LENGTH);
//		drawBall(canvas, p5, ball4Prog);
		for (int i = progress, j = SINGLE_QUADRANT_LENGTH; i > (progress - SINGLE_QUADRANT_LENGTH); i--, j--) {
			int ball3Prog = ((i + 3 * SINGLE_QUADRANT_LENGTH) % PATH_LENGTH);
			int alpha = (int) (40 * ((j * 1.0) / SINGLE_QUADRANT_LENGTH));
			p5.setAlpha(alpha);
			drawBall(canvas, p5, ball3Prog);
		}
	}

	private void drawBall3(Canvas canvas) {
		for (int i = progress, j = SINGLE_QUADRANT_LENGTH; i > (progress - SINGLE_QUADRANT_LENGTH); i--, j--) {
			int ball3Prog = ((i + 2 * SINGLE_QUADRANT_LENGTH) % PATH_LENGTH);
			int alpha = (int) (40 * ((j * 1.0) / SINGLE_QUADRANT_LENGTH));
			p2.setAlpha(alpha);
			drawBall(canvas, p2, ball3Prog);
		}
	}

	private void drawBall2(Canvas canvas) {
//		int ball2Prog = ((progress + 1 * SINGLE_QUADRANT_LENGTH) % PATH_LENGTH);
//		drawBall(canvas, p3, ball2Prog);
		for (int i = progress, j = SINGLE_QUADRANT_LENGTH; i > (progress - SINGLE_QUADRANT_LENGTH); i--, j--) {
			int ball3Prog = ((i + 1 * SINGLE_QUADRANT_LENGTH) % PATH_LENGTH);
			int alpha = (int) (40 * ((j * 1.0) / SINGLE_QUADRANT_LENGTH));
			p3.setAlpha(alpha);
			drawBall(canvas, p3, ball3Prog);
		}
	}

	private void drawBall1(Canvas canvas) {
//		int ball1Prog = ((progress + 0 * SINGLE_QUADRANT_LENGTH) % PATH_LENGTH);
//		drawBall(canvas, p2, progress);
		for (int i = progress,j = SINGLE_QUADRANT_LENGTH; i > (progress - SINGLE_QUADRANT_LENGTH) ; i--,j--) {
			int ball3Prog = ((i + PATH_LENGTH) % PATH_LENGTH);
			int alpha = (int) (40 * ((j * 1.0) / SINGLE_QUADRANT_LENGTH));
			p4.setAlpha(alpha);
			drawBall(canvas, p4, ball3Prog);
		}
	}

	private void drawBall(Canvas canvas, Paint ballPaint, int progress) {
		int ballQuadrant = getQuadrantFromProgress(progress);
		PointF fourthQuadrantBallPoint = pickCorrespondingFourthQuadrantPoint(
				progress, ballQuadrant);
		PointF ballPoint = adjustBallCordinatesByQuadrant(
				fourthQuadrantBallPoint, ballQuadrant);
		canvas.drawPoint(ballPoint.x, ballPoint.y, ballPaint);
	}

	private void drawPath(Canvas canvas) {
		for (PointF p : Q4) {
			float x = p.x;
			float y = p.y;
			float negativeX = getNegativeX(x);
			float negativeY = getNegativeX(y);
			canvas.drawPoint(x, y, p1);
			canvas.drawPoint(x, negativeY, p1);
			canvas.drawPoint(negativeX, y, p1);
			canvas.drawPoint(negativeX, negativeY, p1);
		}
	}

	private PointF pickCorrespondingFourthQuadrantPoint(int progress,
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

	private PointF adjustBallCordinatesByQuadrant(
			PointF ballCordinateInFourthQuadrant, int quadrant) {
		float ballX = ballCordinateInFourthQuadrant.x;
		float ballY = ballCordinateInFourthQuadrant.y;
		float ballNegativeX = getNegativeX(ballX);
		float ballNegativeY = getNegativeY(ballY);
		if (quadrant == SECOND) {
			ballY = ballNegativeY;
		} else if (quadrant == THIRD) {
			ballX = ballNegativeX;
		} else if (quadrant == FIRST) {
			ballX = ballNegativeX;
			ballY = ballNegativeY;
		}
		return new PointF(ballX, ballY);
	}

	private float getNegativeY(float y) {
		return (2 * startY - y);
	}

	private float getNegativeX(float x) {
		return (2 * startX - x);
	}

}