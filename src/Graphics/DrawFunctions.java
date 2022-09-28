package Graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import Main.Utg;

public class DrawFunctions
{
    Font TextFont = new Font("SansSerif", Font.PLAIN, 20);
	Font BoldTextFont = new Font("SansSerif", Font.BOLD, 20);
	int stdStroke = 2;
	private Graphics2D G;
	
	public DrawFunctions(Graphics g)
	{
		G = (Graphics2D) g;
	}
	
	public void DrawText(int[] Pos, String Text, String Alignment, float angle, String Style, int size, Color color)
    {
		float TextLength = Utg.TextL(Text, TextFont, size, G), TextHeight = Utg.TextH(size);
    	int[] Offset = new int[2];
		AffineTransform a = null;	// Rotate rectangle
		AffineTransform backup = G.getTransform();
		if (Alignment.equals("Left"))
    	{
			a = AffineTransform.getRotateInstance(-angle*Math.PI/180, Pos[0] - 0.5*TextLength, Pos[1] + 0.5*TextHeight);	// Rotate text
    	}
		else if (Alignment.equals("Center"))
    	{
			a = AffineTransform.getRotateInstance(-angle*Math.PI/180, Pos[0], Pos[1] + 0.5*TextHeight);	// Rotate text
    		Offset[0] = -Utg.TextL(Text, BoldTextFont, size, G)/2;
    		Offset[1] = Utg.TextH(size)/2;
    	}
    	else if (Alignment.equals("Right"))
    	{
			a = AffineTransform.getRotateInstance(-angle*Math.PI/180, Pos[0], Pos[1] + 0.5*TextHeight);	// Rotate text
    		Offset[0] = -Utg.TextL(Text, BoldTextFont, size, G);
    	}
    	if (Style.equals("Bold"))
    	{
    		G.setFont(new Font(BoldTextFont.getName(), BoldTextFont.getStyle(), size));
    	}
    	else
    	{
    		G.setFont(new Font(TextFont.getName(), TextFont.getStyle(), size));
    	}
    	if (0 < Math.abs(angle))
    	{
    		G.setTransform(a);
    	}
    	G.setColor(color);
    	G.drawString(Text, Pos[0] + Offset[0], Pos[1] + Offset[1]);
        G.setTransform(backup);
    }
	public void DrawLine(int[] PosInit, int[] PosFinal, int thickness, Color color)
    {
    	G.setColor(color);
    	G.setStroke(new BasicStroke(thickness));
    	G.drawLine(PosInit[0], PosInit[1], PosFinal[0], PosFinal[1]);
    	G.setStroke(new BasicStroke(stdStroke));
    }
	public void DrawPolyLine(int[] x, int[] y, int thickness, Color color)
    {
    	G.setColor(color);
    	G.setStroke(new BasicStroke(thickness));
    	G.drawPolyline(x, y, x.length);
    	G.setStroke(new BasicStroke(stdStroke));
    }
	public void DrawRect(int[] Pos, String Alignment, int l, int h, int Thickness, Color color, Color contourColor, boolean contour)
	{
		int[] offset = Utg.OffsetFromPos(Alignment, l, h);
		int[] Corner = new int[] {Pos[0] + offset[0], Pos[1] + offset[1]};
		G.setStroke(new BasicStroke(Thickness));
		if (color != null)
		{
			G.setColor(color);
			G.fillRect(Corner[0], Corner[1], l, h);
		}
		if (contour & contourColor != null)
		{
			G.setColor(contourColor);
			G.drawPolyline(new int[] {Corner[0], Corner[0] + l, Corner[0] + l, Corner[0], Corner[0]}, new int[] {Corner[1], Corner[1], Corner[1] + h, Corner[1] + h, Corner[1]}, 5);
		}
		G.setStroke(new BasicStroke(stdStroke));
        //Ut.CheckIfPosIsOutsideScreen(Pos, new int[] {ScreenL + 55, ScreenH + 19}, "A rect is being drawn outside window");
	}
	public void DrawPoint(int[] Pos, int size, boolean fill, Color ContourColor, Color FillColor)
    {
    	G.setColor(ContourColor);
    	G.drawOval(Pos[0] - size/2, Pos[1] - size/2, size, size);
    	if (fill)
    	{
        	G.setColor(FillColor);
        	G.fillOval(Pos[0] - size/2, Pos[1] - size/2, size, size);
    	}
    }

	public void DrawGrid(int[] InitPos, int[] FinalPos, int NumSpacing, boolean[] Dir)
	{
		int Linethick = 1;
		int[] Length = new int[] {FinalPos[0] - InitPos[0], InitPos[1] - FinalPos[1]};
		int sx = Length[0]/NumSpacing, sy = Length[1]/NumSpacing;
		Color LineColor = Color.gray;
		for (int i = 0; i <= NumSpacing - 1; i += 1)
		{
			if (Dir[0])
			{
				DrawLine(new int[] {InitPos[0], InitPos[1] - (i + 1)*sy}, new int[] {InitPos[0] + Length[0], InitPos[1] - (i + 1)*sy}, Linethick, LineColor);	// Horizontal	
			}
			if (Dir[1])
			{
				DrawLine(new int[] {InitPos[0] + (i + 1)*sx, InitPos[1]}, new int[] {InitPos[0] + (i + 1)*sx, InitPos[1] - Length[1]}, Linethick, LineColor);	// Vertical			
			}
		}
	}
	public void DrawGraph(int[] Pos, String Title, int[] size, Color color, boolean[] ActiveLine)
	{
		int thick = stdStroke;
		int asize = 2 * size[0] / 100;
		DrawText(new int[] {Pos[0] + size[0]/2, (int) (Pos[1] - (size[1] + asize + 20))}, Title, "Center", 0, "Bold", 14, color);
		DrawLine(Pos, new int[] {Pos[0], (int) (Pos[1] - (size[1] + asize + 10))}, thick, Color.black);	// Vertical axis
		DrawLine(Pos, new int[] {(int) (Pos[0] + (size[0] + asize + 10)), Pos[1]}, thick, Color.black);	// Horizontal axis
		DrawPolyLine(new int[] {Pos[0] - asize, Pos[0], Pos[0] + asize}, new int[] {(int) (Pos[1] - (size[1] + asize + 10)) + asize, (int) (Pos[1] - (size[1] + asize + 10)), (int) (Pos[1] - (size[1] + asize + 10)) + asize}, thick, Color.black);
		DrawPolyLine(new int[] {(int) (Pos[0] + (size[0] + asize + 10) - asize), (int) (Pos[0] + (size[0] + asize + 10)), (int) (Pos[0] + (size[0] + asize + 10) - asize)}, new int[] {Pos[1] - asize, Pos[1], Pos[1] + asize}, thick, Color.black);
		DrawGrid(Pos, new int[] {Pos[0] + size[0], Pos[1] - size[1]}, 10, ActiveLine);
	}
	public void DrawBarGraph(int[] Pos, String Title, int[] size, Color color, double[] Var, double Maxvalue)
	{
		int barsize = size[0] / Var.length;
		DrawGraph(Pos, Title, size, color, new boolean[] {false, false});
		for (int i = 0; i <= Var.length - 1; i += 1)
		{
			int barlength = (int)(Var[i] * size[1] / Maxvalue);
			DrawRect(new int[] {Pos[0] + i * barsize, Pos[1]}, "BotLeft", barsize, barlength, 1, Color.blue, new Color(0, 0, 50), false);
		}		
	}
	public void PlotPoints(int[] Pos, String Title, int[] size, Color fillColor, Color contourColor, double[] x, double[] y)
	{
		DrawGraph(Pos, Title, size, Color.black, new boolean[] {true, true});
		x = Utg.MapLinear(Utg.MinAbs(x), Utg.MaxAbs(x), 0, size[0], x);
		y = Utg.MapLinear(Utg.MinAbs(y), Utg.MaxAbs(y), 0, size[1], y);
		for (int p = 0; p <= x.length - 1; p += 1)
		{
			DrawPoint(new int[] {(int) (Pos[0] + x[p]), (int) (Pos[1] - y[p])}, 6, true, contourColor, fillColor);
		}
	}
	public void DrawDynLineGraph(int[] Pos, String Title, double[][] Var, double Maxvalue, Color[] color)
	{
		int[] size = new int[] {100, 100};
		int NumPoints;
		if (Var != null)
		{
			NumPoints = Var.length;
		}
		else
		{
			NumPoints = 0;
		}
		DrawGraph(Pos, Title, size, Color.blue, new boolean[] {true, true});
		if (Var != null)
		{
			if (1 <= Var.length)
			{
				for (int j = 0; j <= Var.length - 1; j += 1)
				{
					NumPoints = Var[j].length;
					DrawText(new int[] {(int) (Pos[0] - 30), (int) (Pos[1] - size[1])}, String.valueOf(Utg.Round(Maxvalue, 2)), "Center", 0, "Bold", 13, color[j]);
					int[] x = new int[NumPoints], y = new int[NumPoints];
					for (int i = 0; i <= Var[j].length - 1; i += 1)
					{
						if (1 < Var[0].length)
						{
							x[i] = Pos[0] + size[0]*i/(Var[0].length - 1);
						}
						else
						{
							x[i] = Pos[0];
						}
						y[i] = Pos[1] - (int) (size[1]*Var[j][i]/(float)Maxvalue);
					}
					DrawText(new int[] {(int) (Pos[0] - 30), (int) (y[y.length - 1])}, String.valueOf(Utg.Round(Var[j][Var[j].length - 1], 2)), "Center", 0, "Bold", 13, color[j]);
					DrawPolyLine(x, y, 2, color[j]);
				}
			}
		}
	}
}
