package me.frechetta.fgame;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;
import me.frechetta.lwjglutil.Input;
import me.frechetta.lwjglutil.ShaderProgram;
import me.frechetta.lwjglutil.Utils;
import me.frechetta.lwjglutil.math.Matrix4;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.PixelFormat;

public abstract class FGame
{
	private int targetFPS = 60;
	private int fps = 0;
	
	private boolean running = true;
	
	private ShaderProgram program;
	
	public static int translationUnif;
	public static int rotationUnif;
	public static int scaleUnif;
	
	private static int projectionUnif;
	
	public static int positionAtt;
	public static int colorAtt;
	public static int texcoordAtt;
	
	private Matrix4 projection;
	
	
	public FGame(String name, int width, int height, boolean fullscreen)
	{
		Display.setTitle(name);
		
		try
		{
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.setFullscreen(fullscreen);
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
		}
	}
	
	
	public final void run()
	{
		try
		{
			ContextAttribs attribs = null;
			Display.create(new PixelFormat(), attribs);
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
			System.exit(1);
		}
		
		gameLoop();
	}
	
	
	private void gameLoop()
	{
		try
		{
			program = new ShaderProgram(readFromFile("shader.vert"), readFromFile("shader.frag"));
			
			translationUnif = program.getUniformLocation("vertTranslation");
			rotationUnif = program.getUniformLocation("vertRotation");
			scaleUnif = program.getUniformLocation("vertScale");
			
			projectionUnif = program.getUniformLocation("vertProjection");
			
			positionAtt = program.getAttribLocation("vertPosition");
			colorAtt = program.getAttribLocation("vertColor");
			texcoordAtt = program.getAttribLocation("vertTexcoord");
			
			projection = new Matrix4().clearToOrtho(0.0f, getWidth(), getHeight(), 0.0f, 0, 1);
			
			program.begin();
			glUniformMatrix4(projectionUnif, false, projection.toBuffer());
			program.end();
			
			initGL();
			
			init();
			Utils.checkGLError("init");
			
			resized();
			Utils.checkGLError("resized");
			
			long lastTime, lastFPS;
			lastTime = lastFPS = System.nanoTime();
			
			while(!Display.isCloseRequested() && running)
			{
				long deltaTime = System.nanoTime() - lastTime;
				lastTime += deltaTime;
				
				if(Display.wasResized()) resized();
				
				Input.update();
				
				update(deltaTime);
				
				Utils.checkGLError("update");
				
				glClear(GL_COLOR_BUFFER_BIT);
				
				program.begin();
				render();
				program.end();
				
				Utils.checkGLError("render");
				
				Display.update();
				
				fps++;
				if(System.nanoTime() - lastFPS >= 1e9)
				{
					//System.out.println("FPS: ".concat(String.valueOf(fps)));
					lastFPS += 1e9;
					fps = 0;
				}
				
				Display.sync(targetFPS);
			}
		}
		catch(Throwable exc)
		{
			exc.printStackTrace();
		}
		finally
		{
			program.dispose();
			dispose();
			destroy();
		}
	}
	
	private void initGL()
	{
		glEnable(GL_TEXTURE_2D);
	}
	
	
	public abstract void init();
	
	public abstract void update(long deltaTime);
	
	public abstract void render();
	
	public abstract void dispose();
	
	
	public void resized()
	{
		glViewport(0, 0, getWidth(), getHeight());
		
		projection.clearToOrtho(0.0f, getWidth(), getHeight(), 0.0f, 0, 1);
		
		program.begin();
		glUniformMatrix4(projectionUnif, false, projection.toBuffer());
		program.end();
	}
	
	
	public void stop()
	{
		running = false;
	}
	
	public void destroy()
	{
		Display.destroy();
		System.exit(0);
	}
	
	
	public static int getWidth()
	{
		return Display.getWidth();
	}
	
	public static int getHeight()
	{
		return Display.getHeight();
	}
	
	public int getTargetFPS()
	{
		return targetFPS;
	}
	
	public int getFPS()
	{
		return fps;
	}
	
	public void setTargetFPS(int targetFPS)
	{
		this.targetFPS = targetFPS;
	}
	
	public void setResizable(boolean resizable)
	{
		Display.setResizable(resizable);
	}
	
	public void setFullscreen(boolean fullscreen)
	{
		try
		{
			Display.setFullscreen(fullscreen);
		}
		catch (LWJGLException e)
		{
			e.printStackTrace();
		}
	}
	
	public void setVSync(boolean vSync)
	{
		Display.setVSyncEnabled(vSync);
	}
	
	
	protected String readFromFile(String file)
	{
		try
		{
			return Utils.readFully(getClass().getResourceAsStream(file));
		}
		catch(Exception exc)
		{
			throw new RuntimeException("Failure reading file " + file, exc);
		}
	}
}