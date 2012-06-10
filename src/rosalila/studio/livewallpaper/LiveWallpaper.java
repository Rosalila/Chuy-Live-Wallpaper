package rosalila.studio.livewallpaper;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.drawable.PictureDrawable;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.Window;
import android.view.WindowManager;

public class LiveWallpaper extends WallpaperService
{

	public static final String	SHARED_PREFS_NAME	= "livewallpapersettings";

	@Override
	public void onCreate()
	{
		super.onCreate();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}
	


	@Override
	public Engine onCreateEngine()
	{
		return new TestPatternEngine();
	}

	class TestPatternEngine extends Engine //implements
			//SharedPreferences.OnSharedPreferenceChangeListener
	{
		private final Handler		mHandler		= new Handler();
		private float				mTouchX			= -1;
		private float				mTouchY			= -1;
		private final Paint			mPaint			= new Paint();
		private final Runnable		mDrawPattern	= new Runnable()
													{
														public void run()
														{
															drawFrame();
														}
													};

        //mis variables
	    ArrayList<Cloud> clouds;
	    int screen_height;
	    int screen_width;
	    int new_cloud_iteration=0;
	    int current_chuy=0;
	    Bitmap background,cloud,chuy1,chuy2,chuy3,chuy4;
	    final SurfaceHolder holder = getSurfaceHolder();
	    int max_width, max_height;
	    
													
		private boolean				mVisible;
		//private SharedPreferences	mPreferences;

		// private
		GradientDrawable			mGradient;

		TestPatternEngine()
		{
			final Paint paint = mPaint;
			paint.setColor(0xffffffff);
			paint.setAntiAlias(true);
			paint.setStrokeWidth(2);
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setStyle(Paint.Style.STROKE);
			
			
			clouds=new ArrayList<Cloud>();
			clouds.add(new Cloud(10,50));
			//mision= diversas resoluciones
			//max_height = holder.getSurfaceFrame().height();
			//max_width = holder.getSurfaceFrame().width();
			background = BitmapFactory.decodeResource(getResources(), R.drawable.cielo2);
			
			cloud = BitmapFactory.decodeResource(getResources(), R.drawable.nube);
			chuy1 = BitmapFactory.decodeResource(getResources(), R.drawable.chuy1);
			chuy2 = BitmapFactory.decodeResource(getResources(), R.drawable.chuy2);
			chuy3 = BitmapFactory.decodeResource(getResources(), R.drawable.chuy3);
			chuy4 = BitmapFactory.decodeResource(getResources(), R.drawable.chuy4);
		}

		@Override
		public void onCreate(SurfaceHolder surfaceHolder)
		{
			super.onCreate(surfaceHolder);
			setTouchEventsEnabled(true);
		}

		@Override
		public void onDestroy()
		{
			super.onDestroy();
			mHandler.removeCallbacks(mDrawPattern);
		}

		@Override
		public void onVisibilityChanged(boolean visible)
		{
			mVisible = visible;
			if (visible)
			{
				drawFrame();
			}
			else
			{
				mHandler.removeCallbacks(mDrawPattern);
			}
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height)
		{
			super.onSurfaceChanged(holder, format, width, height);

			drawFrame();
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder)
		{
			super.onSurfaceCreated(holder);
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder)
		{
			super.onSurfaceDestroyed(holder);
			mVisible = false;
			mHandler.removeCallbacks(mDrawPattern);
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset, float xStep,
				float yStep, int xPixels, int yPixels)
		{

			drawFrame();
		}

		/*
		 * Store the position of the touch event so we can use it for drawing
		 * later
		 */
		@Override
		public void onTouchEvent(MotionEvent event)
		{
			if (event.getAction() == MotionEvent.ACTION_MOVE)
			{
				mTouchX = event.getX();
				mTouchY = event.getY();
			}
			else
			{
				//mTouchX = -1;
				//mTouchY = -1;
			}
			super.onTouchEvent(event);
		}

		/*
		 * Draw one frame of the animation. This method gets called repeatedly
		 * by posting a delayed Runnable. You can do any drawing you want in
		 * here. This example draws a wireframe cube.
		 */
		void drawFrame()
		{
			//final SurfaceHolder holder = getSurfaceHolder();


			Canvas c = null;
			try
			{
				c = holder.lockCanvas();
				if (c != null)
				{
					// draw something
					drawPattern(c);
				}
			}
			finally
			{
				if (c != null)
					holder.unlockCanvasAndPost(c);
			}

			mHandler.removeCallbacks(mDrawPattern);
			if (mVisible)
			{
				mHandler.postDelayed(mDrawPattern, 1000 / 25);
			}
		}

		void drawPattern(Canvas c)
		{
			c.save();
			c.drawColor(0xff000000);

			//Paint paint = new Paint();
			
			//my code
			screen_width=c.getWidth();
			screen_height=c.getHeight();
			
			//add new cloud
			if(new_cloud_iteration > 50+(int)(Math.random()*1000%100) )
			{
				clouds.add(new Cloud((int)(Math.random()*1000%screen_width),0));
				new_cloud_iteration=0;
			}
			new_cloud_iteration++;
			
			//my paint background
			c.drawBitmap(background, 0, 0, null);
			
			//my paint clouds
			for(int i=0;i<clouds.size();i++)
			{
			    c.drawBitmap(cloud,
			    		clouds.get(i).x-cloud.getWidth()/2,
			    		clouds.get(i).y-cloud.getHeight()/2,
			    		null);
			    
			    clouds.get(i).y++;
			    
			    if(clouds.get(i).y>screen_height)
			    {
			    	clouds.remove(i);
			    	i--;
			    }
			}
			
			//my paint chuy
			if (mTouchX >= 0 && mTouchY >= 0)
			{				
				if(current_chuy==0)
				{
					c.drawBitmap(chuy1, mTouchX-chuy1.getWidth()/2, mTouchY-chuy1.getHeight()/2, null);
				}
				else if(current_chuy==1)
				{
					c.drawBitmap(chuy2, mTouchX-chuy2.getWidth()/2, mTouchY-chuy2.getHeight()/2, null);
				}
				else if(current_chuy==2)
				{
					c.drawBitmap(chuy3, mTouchX-chuy3.getWidth()/2, mTouchY-chuy3.getHeight()/2, null);
				}
				else if(current_chuy==3)
				{
					c.drawBitmap(chuy4, mTouchX-chuy4.getWidth()/2, mTouchY-chuy4.getHeight()/2, null);
				}
				current_chuy++;
				if(current_chuy>=4)
					current_chuy=0;
			}
			
			c.restore();
		}
	}
}