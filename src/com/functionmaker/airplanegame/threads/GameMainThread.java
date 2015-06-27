package com.functionmaker.airplanegame.threads;

import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.SurfaceHolder;

import com.functionmaker.airplanegame.R;
import com.functionmaker.airplanegame.ai.AI;
import com.functionmaker.airplanegame.objects.Airplane;
import com.functionmaker.airplanegame.objects.Enemy;
import com.functionmaker.airplanegame.util.DrawTool;
import com.functionmaker.airplanegame.util.WindowSize;

public class GameMainThread extends Thread {
	private Airplane airplane;
	private Context context;
	private List<Enemy> enemies;
	private Bitmap enemyBitmap;
	private int enemyHeight;
	private int enemyWidth;
	private Bitmap explosionBitmap;
	private Handler handler;
	private SurfaceHolder holder;
	private boolean isGameContinue;
	private boolean isGamePause;
	private WindowSize windowSize;

	public GameMainThread(SurfaceHolder paramSurfaceHolder,
			Airplane paramAirplane, List<Enemy> paramList,
			Context paramContext, WindowSize paramWindowSize,
			Handler paramHandler) {
		this.holder = paramSurfaceHolder;
		this.airplane = paramAirplane;
		this.enemies = paramList;
		this.windowSize = paramWindowSize;
		this.context = paramContext;
		this.handler = paramHandler;
		this.explosionBitmap = BitmapFactory.decodeResource(
				paramContext.getResources(), R.drawable.explosion);
		this.enemyBitmap = BitmapFactory.decodeResource(
				paramContext.getResources(), R.drawable.enemy);
		this.enemyWidth = this.enemyBitmap.getWidth();
		this.enemyHeight = this.enemyBitmap.getHeight();
		this.isGameContinue = true;
	}

	public void run() {
		Paint localPaint = new Paint();
		localPaint.setAntiAlias(true);
		localPaint.setTextSize(20.0F);
		localPaint.setColor(Color.YELLOW);
		int bulletsCount = 0;
		int enemyCount = 0;
		while (isGameContinue) {
			while (!isGamePause) {
				if (bulletsCount == 30) {
					this.airplane.produceBullet();
					bulletsCount = 0;
				}
				bulletsCount++;
				if (enemyCount == 60) {
					produceEnemies();
					enemyCount = 0;
				}
				enemyCount++;
				Canvas localCanvas = this.holder.lockCanvas();
				localCanvas.drawColor(Color.BLACK);
				DrawTool.drawAirplane(localCanvas, localPaint, this.airplane);
				DrawTool.drawEnemies(localCanvas, localPaint, this.enemies,
						this.windowSize);
				DrawTool.drawBullet(localCanvas, localPaint, this.airplane);
				AI.destroyDeal(this.airplane, this.enemies, localCanvas,
						localPaint, this.explosionBitmap);
				localCanvas
						.drawText(context.getResources().getString(R.string.score) + AI.score, 20.0F, 20.0F, localPaint);
				AI.isGameOver(this.enemies, this.airplane, this.windowSize,
						this.handler,context);
				this.holder.unlockCanvasAndPost(localCanvas);
			}
		}
	}

	public void setGamePause() {
		this.isGamePause = true;
	}

	public void exitGame() {
		this.isGameContinue = false;
	}

	public boolean getGameState() {
		return this.isGamePause;
	}

	public void produceEnemies() {
		Enemy localEnemy = new Enemy(this.enemyBitmap, this.enemyWidth,
				this.enemyHeight, new Random().nextInt(this.windowSize
						.getWidth() - this.enemyWidth), 0);
		this.enemies.add(localEnemy);
	}

	public void resumeGame() {
		this.isGamePause = false;
	}
}
