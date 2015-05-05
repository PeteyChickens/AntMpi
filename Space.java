import java.awt.*;
import java.awt.geom.*;


public class Space
{
	int num_ants;	// each space in the grid tracks the number of ants, the food, the pheramone strength, and obstacles
	int food_count;
	double nScent;	// drop this while looking for food
	double fScent; // drop this when it has found food
	boolean is_nest;	//boolean value to let the ants know they are home
	boolean is_blocked;
	
	int row;
	int col;
	Rectangle2D.Double rect;
	Color color = new Color(150, 200,200);
	Color bg = Color.white;
	Color wall = Color.red;
	Color smell = Color.blue;
	Color ant = Color.black;
	Color home = Color.magenta;
	Color food = Color.green;
	double freshtime;
	
	public Space(Rectangle2D.Double r, int x, int y)		//construct to empty everything
	{
		num_ants =0;
		food_count = 0;
		nScent = 0;
		fScent = 0;
		is_nest = false;
		is_blocked = false;
		this.rect = r;
		row = x;
		freshtime = System.nanoTime();
		col = y;
	}
	public void add_ant()	// use this to add an ant to a space
	{
		if(num_ants == 0)
			num_ants++;
	}
	public void remove_ant()	//remove an ant from a space, provided there is at least 1 ant
	{
		if(num_ants>0)
		{
			num_ants--;
		}
	}
	public void set_home()		// give a space nest status
	{
			is_nest = true;
	}
	public boolean home_yet()  //return if the space being looked at is home
	{
		return is_nest;
	}
	public void draw(Graphics2D g2)
	{
		if(is_blocked)
		{
			g2.setPaint(wall);
		}
		else if(food_count > 0)
		{
			g2.setPaint(food);
		}
		else if(is_nest)
		{
			g2.setPaint(home);
		}
		
		else if(num_ants > 0)
		{
			g2.setPaint(ant);
		}
		else if(nScent+fScent > 0)
		{
			int a = 255;
			int b = 255;
			int c = 255;
			int t = 0;
			a = a-(1*(int)(nScent+fScent));
			if(a <=0)
			{	
				t = a*-1;
				a = 0;
				b = b-(1*t);
				if(b<=0)
				{
					t = b*-1;
					b = 0;
					c = c-(1*t);
					if(c<=0)
					{
						c = 0;
					}
				}
			}
			if(a > 255)
			{
				a = 255;
			}
			if(b > 255)
			{
				b = 255;
			}
						if(c > 255)
			{
				c = 255;
			}
			if(a <0)
			{
				a = 0;
			}
			if(b <0)
			{
				b = 0;
			}
			if(c <0)
			{
				c = 0;
			}
			Color color = new Color(a, b, c);
			//System.out.println(a+" "+ b+ " "+ c);
			g2.setPaint(color);
		}
		else
		{
			g2.setPaint(bg);
		}
		
		g2.fill(rect);
		g2.setPaint(color);
		g2.draw(rect);
	}
}