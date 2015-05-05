import mpi.*;
import java.util.*;

public class AntsMPI {
	
	static BoardFrame frame;
	static Space[][] grid;
	static int row;
	static int col;
	static AntAlgo[] swarm;
	
	public static void main(String[] args) throws MPIException {
		
		MPI.Init(args);		//Initialize MPI
		int rank = MPI.COMM_WORLD.Rank();
		int size = MPI.COMM_WORLD.Size();
		
		//make board
		row = 60;
		col = 60;
		frame = new BoardFrame(row, col);
		
		swarm = new AntAlgo[5];
		int hrPos = 13;
		int hcPos = 13;
		frame.populate(hrPos, hcPos , (hrPos*2), (hcPos*2), swarm.length);
		grid = frame.get_board();
		
		//create ants
		AntAlgo ant1 = new AntAlgo(hrPos, hcPos+1, row, col, frame, hrPos, hcPos);
		AntAlgo ant2 = new AntAlgo(hrPos, hcPos-1, row, col, frame, hrPos, hcPos);
		AntAlgo ant3 = new AntAlgo(hrPos-1, hcPos, row, col, frame, hrPos, hcPos);
		AntAlgo ant4 = new AntAlgo(hrPos-1, hcPos-1, row, col, frame, hrPos, hcPos);
		AntAlgo ant5 = new AntAlgo(hrPos+1, hcPos, row, col, frame, hrPos, hcPos);
		
		//add each ant to the swarm
		swarm[0] = ant1;
		swarm[1] = ant2;
		swarm[2] = ant3;
		swarm[3] = ant4;
		swarm[4] = ant5;
		
		boolean stillFood = true;
		
		while(stillFood) {
			
			if(rank == 0){
				
				int [] spaces = new Space[5];
				int [] message = new Space[2];
				MPI.COMM_WORLD.recv(message, 2, MPI.INT, 1, 17);
				spaces[0] = message[0];
				spaces[1] = message[1];
				MPI.COMM_WORLD.recv(message, 2, MPI.INT, 2, 17);
				spaces[2] = message[0];
				spaces[3] = message[1];
				MPI.COMM_WORLD.recv(message, 2, MPI.INT, 3, 17);
				spaces[4] = message[0];
				spaces[5] = message[1];
				MPI.COMM_WORLD.recv(message, 2, MPI.INT, 4, 17);
				spaces[6] = message[0];
				spaces[7] = message[1];
				MPI.COMM_WORLD.recv(message, 2, MPI.INT, 5, 17);
				spaces[8] = message[0];
				spaces[9] = message[1];
				
				for(int i = 0; i < spaces.length; i+=2) {
					updateBoard(spaces[i], spaces[i+1], (i/2));
				}
				
				for(int i = 0; i < swarm.length; i++) {
					swarm[i].setNewBoard(grid);
				}
				
			}else if(rank <=5) {
				
				int r = MPI.COMM_WORLD.getRank();
				Space s = swarm[r-1].nextPosition();
				int[] message = new int[2];
				message[0] = s.row;
				message[1] = s.col;
				MPI.COMM_WORLD.send(message, 2, MPI.INT, 0, 17); 
				
			}
			
		}
		
		MPI.Finalize();
	}
	
	public static void updateBoard(int r, int c, int id) {
		
		boolean hasFood = swarm[id].ifHasFood();
		
		//checks to see if the position the ant moved to has food on it
		if(grid[r][c].food_count > 0) {
			grid[r][c].food_count--;
		}
		
		//adds some scent to the path that the ant took
		if(hasFood) {
			grid[r][c].fScent += 5;
		}else {
			grid[r][c].nScent += 10;
		}
			
		double max = 0;
		double min = 300;
		//sets what the max and min scent are
		for(int k = 0; k < row; k++) {
				
			for(int j = 0 ; j < col; j++) {
				if(max < grid[k][j].nScent)
					max = grid[k][j].nScent;
				if(min > grid[k][j].nScent)
					min = grid[k][j].nScent;
				if(max < grid[k][j].fScent)
					max = grid[k][j].fScent;
				if(min > grid[k][j].fScent)
					min = grid[k][j].fScent;
			}
		}
			
		for(int k = 0; k < row; k++) {
				
			for(int j = 0 ; j < col; j++) {
				grid[k][j].nScent = ((grid[k][j].nScent)/min)*((max - min)/max)*.95;
						
				if(grid[k][j].nScent <=0)
					grid[k][j].nScent = ((max - min)/max);
				if(grid[k][j].nScent >600)
					grid[k][j].nScent = 600;
							
				grid[k][j].fScent = ((grid[k][j].fScent)/min)*((max - min)/max)*.95;
						
				if(grid[k][j].fScent <=0)
					grid[k][j].fScent = ((max - min)/max);
				if(grid[k][j].fScent >600)
					grid[k][j].fScent = 600;

			}
		}
		
		System.out.println(grid[row-1][col-1].food_count);

		
	}

}
