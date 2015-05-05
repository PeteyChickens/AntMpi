import java.awt.*;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.lang.Math.*;
import java.util.Random;
class BoardFrame extends JPanel
{
	Space [][] board;				//spaces are objects that make up the tiles of the board
	int rows;						// rows and cols constructed when boardFrame instanciated
	int cols;
	int PAD = 5;

	public BoardFrame(int x, int y)
	{
		rows = x;
		cols = y;
		board = new Space[x][y];		// build up the playing field
		 JFrame f = new JFrame();
		 f.setTitle("J THread Ants");
	     f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	     f.add(this);
	     f.setSize(400, 400);
	     f.setLocation(100,100);
	     f.setVisible(true);
	     
	     int w = getWidth();
	     int h = getHeight();
	     double xInc = (double)(w - 2*PAD)/cols;
	     double yInc = (double)(h - 2*PAD)/rows;
	     for(int i = 0; i < rows; i++) 
	     {
	    	 double t = PAD + i*yInc;
	         for(int j = 0; j < cols; j++) 
	         {
			 	        	 double z = PAD + j*xInc;
	             Rectangle2D.Double r = new Rectangle2D.Double(z, t, xInc, yInc);
	             board[i][j] = new Space(r, i, j);
	         }
	     }
		
	}
	public void populate(int hr, int hc, int br, int bc, int z)				// turns null spaces into ones with food, ants, and obsticals later
	{
		board[hr][hc].set_home();			
		board[rows-1][cols-1].food_count = 10000;	// home and food locations temporarily hard coded. will be assigned random values at each startup
		board[hr][hc].num_ants = z;
		//board[hr][hc].scent = 500;
		//array of ants here
		if(br >=hr && bc >= hc)
		{
			int count  = 3;
			for(int i = br; i < rows; i++)
			{
				board[i][bc].is_blocked = true;
				int roll = g_rand(10);
				if(roll == 5 && count >0)
				{
					 count--;
					 board[i][bc].is_blocked = false;
				}
			}
			count = 3;
			for(int i = bc; i < cols; i++)
			{
				board[br][i].is_blocked = true;
				int roll = g_rand(10);
				if(roll == 5 && count >0)
				{
					 count--;
					 board[br][i].is_blocked = false;
				}
			}
			count = 3;

			br=br+((rows-br)/2);
			bc = bc+((cols-bc)/2);
			for(int i = br; i < rows; i++)
			{
				board[i][bc].is_blocked = true;
				int roll = g_rand(10);
				if(roll == 5 && count >0)
				{
					 count--;
					 board[i][bc].is_blocked = false;
				}
							}
			count = 3;
			for(int i = bc; i < cols; i++)
			{
				board[br][i].is_blocked = true;
				int roll = g_rand(10);
				if(roll == 5 && count >0)
				{
					 count--;
					 board[br][i].is_blocked = false;
				}
			}
		}
		
		for(int i = 0; i < rows; i++)
		{
			for(int j = 0; j < cols; j++)
			{
				double dist = ((rows-i)*(rows-i))+((cols-j)*(cols-j));
				if(dist < 0)
					dist = dist*-1;
				dist = Math.sqrt(dist);
				
				board[i][j].nScent = (60-dist);
				board[j][i].nScent = (60-dist);
			//	System.out.print(board[i][j].scent+" ");
			}
			System.out.println();
		}
		
		
		
		
	}
	protected void paintComponent (Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    initSquares();
	    for(int i = 0; i < rows; i++)
	    {
	    	for(int j = 0; j < cols; j++) 
	    	{
	    		board[i][j].draw(g2);
	        }
	    }
	}
		
	
	 public void initSquares() 
	 {
		 int w = getWidth();
	     int h = getHeight();
	     double xInc = (double)(w - 2*PAD)/cols;
	     double yInc = (double)(h - 2*PAD)/rows;
	     for(int i = 0; i < rows; i++) 
	     {
	    	 double y = PAD + i*yInc;
	         for(int j = 0; j < cols; j++) 
	         {
	        	 double x = PAD + j*xInc;
	             Rectangle2D.Double r =new Rectangle2D.Double(x, y, xInc, yInc);
	             board[i][j].row = i;
	             board[i][j].col = j;
	             board[i][j].rect = r;
	             }
	         }
	     }
        
	
	public Space[][] get_board()
	{
		return board;
	}
	
	public static int g_rand(int max)
	{
		Random t = new Random();
		int m = t.nextInt(max);
		if(m < 0)
		{
			m = m*-1;
		}
		return m;
	}
	
}