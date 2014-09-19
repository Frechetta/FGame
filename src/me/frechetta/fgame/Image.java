package me.frechetta.fgame;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import me.frechetta.lwjglutil.Utils;

public class Image
{
	private int width;
	private int height;
	
	private int texture;
	
	
	public Image(String path)
	{
		BufferedImage image = null;
		
		try
		{
			image = ImageIO.read(new File(path));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		width = image.getWidth();
		height = image.getHeight();
		
		texture = Utils.loadTexture(image);
	}
	
	
	public void draw(int x, int y)
	{
		
	}
	
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
}
