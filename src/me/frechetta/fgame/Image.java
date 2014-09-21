package me.frechetta.fgame;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_SHORT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glUniformMatrix4;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.imageio.ImageIO;

import me.frechetta.lwjglutil.Utils;
import me.frechetta.lwjglutil.math.Matrix4;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;


public class Image implements Disposable
{
	private int width;
	private int height;
	
	private float r;
	private float g;
	private float b;
	private float a;
	
	private int texture;
	
	private int vertexBuffer;
	private int colorBuffer;
	private int texcoordBuffer;
	private int elementBuffer;
	
	private Matrix4 translationMatrix;
	private Matrix4 rotationMatrix;
	private Matrix4 scaleMatrix;
	
	private float rotation;
	private float scale;
	
	
	public Image(String path)
	{
		BufferedImage image = null;
		
		try
		{
			image = ImageIO.read(new File(path));
		}
		catch (IOException e)
		{
			//e.printStackTrace();
			System.err.println("Could not find file: " + path);
		}
		
		width = image.getWidth();
		height = image.getHeight();
		
		r = 1.0f;
		g = 1.0f;
		b = 1.0f;
		a = 1.0f;
		
		texture = Utils.loadTexture(image);
		
		float x0 = 0.0f;
		float y0 = 0.0f;
		float x1 = x0 + width;
		float y1 = y0 + height;
		
		FloatBuffer vertexBufferData = BufferUtils.createFloatBuffer(2 * 4);
		vertexBufferData.put(new float[]
			{
				x0, y0,
				x1, y0,
				x1, y1,
				x0, y1
			});
		vertexBufferData.rewind();
		
		FloatBuffer colorBufferData = BufferUtils.createFloatBuffer(4 * 4);
		colorBufferData.put(new float[]
			{
				r, g, b, a,
				r, g, b, a,
				r, g, b, a,
				r, g, b, a
			});
		colorBufferData.rewind();
		
		FloatBuffer texcoordBufferData = BufferUtils.createFloatBuffer(2 * 4);
		texcoordBufferData.put(new float[]
			{
				0.0f, 0.0f,
				1.0f, 0.0f,
				1.0f, 1.0f,
				0.0f, 1.0f
			});
		texcoordBufferData.rewind();
		
		ShortBuffer elementBufferData = BufferUtils.createShortBuffer(3 * 2);
		elementBufferData.put(new short[]
			{
			    0, 1, 2,
			    2, 3, 0
			});
		elementBufferData.rewind();
		
		vertexBuffer = Utils.createBuffer(GL_ARRAY_BUFFER, vertexBufferData);
		colorBuffer = Utils.createBuffer(GL_ARRAY_BUFFER, colorBufferData);
		texcoordBuffer = Utils.createBuffer(GL_ARRAY_BUFFER, texcoordBufferData);
		elementBuffer = Utils.createBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBufferData);
		
		translationMatrix = new Matrix4().clearToIdentity();
		rotationMatrix = new Matrix4().clearToIdentity();
		scaleMatrix = new Matrix4().clearToIdentity();
		
		rotation = 0.0f;
		scale = 0.0f;
	}
	
	
	public void draw(int x, int y)
	{
		glUniformMatrix4(FGame.translationUnif, false, translationMatrix.clearToIdentity().translate(x, y, 0).toBuffer());
		glUniformMatrix4(FGame.rotationUnif, false, rotationMatrix.toBuffer());
		glUniformMatrix4(FGame.scaleUnif, false, scaleMatrix.toBuffer());
		
		glActiveTexture(GL13.GL_TEXTURE0);
		glBindTexture(GL11.GL_TEXTURE_2D, texture);
		
		glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
		glEnableVertexAttribArray(FGame.positionAtt);
		glVertexAttribPointer(FGame.positionAtt, 2, GL_FLOAT, false, 0, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, colorBuffer);
		glEnableVertexAttribArray(FGame.colorAtt);
		glVertexAttribPointer(FGame.colorAtt, 4, GL_FLOAT, false, 0, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, texcoordBuffer);
		glEnableVertexAttribArray(FGame.texcoordAtt);
		glVertexAttribPointer(FGame.texcoordAtt, 2, GL_FLOAT, false, 0, 0);
		
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, elementBuffer);
		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_SHORT, 0);
		
		glDisableVertexAttribArray(FGame.positionAtt); 
		glDisableVertexAttribArray(FGame.colorAtt);
		glDisableVertexAttribArray(FGame.texcoordAtt);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	
	public void rotate(float amount)
	{
		rotation -= amount;
		rotation = rotation % 360;
		
		rotationMatrix.rotateDeg(-amount, 0, 0, 1);
	}
	
	public void setRotation(float rotation)
	{
		this.rotation = -rotation % 360;
		
		rotationMatrix.clearToIdentity().rotateDeg(this.rotation, 0, 0, 1);
	}
	
	
	public void scale(float amount)
	{
		scale = amount;
		scaleMatrix.scale(scale);
	}
	
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public float getR()
	{
		return r;
	}
	
	public float getG()
	{
		return g;
	}
	
	public float getB()
	{
		return b;
	}
	
	public float getA()
	{
		return a;
	}
	
	public float getRotation()
	{
		return rotation;
	}
	
	public float getScale()
	{
		return scale;
	}
	
	
	public void setColor(float r, float g, float b, float a)
	{
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		
		FloatBuffer colorBufferData = BufferUtils.createFloatBuffer(4 * 4);
		colorBufferData.put(new float[]
			{
				r, g, b, a,
				r, g, b, a,
				r, g, b, a,
				r, g, b, a
			});
		colorBufferData.rewind();
		
		colorBuffer = Utils.createBuffer(GL_ARRAY_BUFFER, colorBufferData);
	}
	
	
	@Override
	public void dispose()
	{
		glDeleteTextures(texture);
		glDeleteBuffers(vertexBuffer);
		glDeleteBuffers(colorBuffer);
		glDeleteBuffers(texcoordBuffer);
		glDeleteBuffers(elementBuffer);
	}
}
