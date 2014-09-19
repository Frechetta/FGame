package me.frechetta.fgame;

import static org.lwjgl.opengl.GL11.glViewport;
import me.frechetta.lwjglutil.Input;
import me.frechetta.lwjglutil.ShaderProgram;
import me.frechetta.lwjglutil.Utils;

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
			initGL();
			
			program = new ShaderProgram(readFromFile("shader.vert"), readFromFile("shader.frag"));
			
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
				
				render();
				
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
			dispose();
			destroy();
		}
	}
	
	private void initGL()
	{
		
	}
	
	
	public abstract void init();
	
	public abstract void update(long deltaTime);
	
	public abstract void render();
	
	public abstract void dispose();
	
	
	public void resized()
	{
		glViewport(0, 0, getWidth(), getHeight());
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
	
	
	public int getWidth()
	{
		return Display.getWidth();
	}
	
	public int getHeight()
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