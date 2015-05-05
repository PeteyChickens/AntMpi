import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class AntAlgo {
	
	int rowPos;  	//current row position of the ant
	int colPos;	    //current column position of the ant
	int rowLen;		//how many rows are in the grid
	int colLen;		//how many columns are in the grid
	boolean hasFood;	//if the ant has food with him or not
	Space[][] grid;		//the board 
	int prevRow;		//the previous row position of the ant
	int prevCol;		//the previous column position of the ant
	boolean sawFood;	//if the ant has already seen food or not
	boolean firstTime;	//first time the any has seen food
	double dis;			//displacement of the ant according to home spot
	int hPosR;			//home position of row
	int hPosC;			//home position of column
	
	public AntAlgo(int rowPos, int colPos, int rowLength, int colLength, BoardFrame frame, int hPosR, int hPosC) {
		
		this.rowPos = rowPos;
		this.colPos = colPos;
		rowLen = rowLength;
		colLen = colLength;
		hasFood = false;
		prevRow = rowPos;
		prevCol = colPos;
		grid = frame.get_board();
		sawFood = false;
		firstTime = true;
		dis = checkDisplace(rowPos, colPos);
		this.hPosR = hPosR;
		this.hPosC = hPosC;
		
	}

	public Space nextPosition() {
			
		Space nextSpace = nextLocation(rowPos, colPos);  //gets the next space the ant is moving to
		prevRow = rowPos;
		prevCol = colPos;
		rowPos = nextSpace.row;
		colPos = nextSpace.col;
		dis = checkDisplace(nextSpace.row, nextSpace.col);
				
		//checks to see if the position the ant moved to has food on it
		if(grid[rowPos][colPos].food_count > 0) {
			hasFood = true;
			prevRow = rowPos;
			prevCol = colPos;
		}
						
		if(grid[rowPos][colPos].home_yet()) {
			prevRow = rowPos;
			prevCol = colPos;
			hasFood = false;
		}	

		return nextSpace;
			
	}
	
	//getting the next location for the ant to move
	public Space nextLocation(int cur_row, int cur_col) {
		
		Space[] possible = checkMoves(cur_row, cur_col);
		
		if(!hasFood) {		//Ant doesn't have food
			int index = getIndexWithoutFood(possible);
			if(index == -1) {
				Random rands = new Random();
				index = rands.nextInt();
				if(index < 0) {
					index = -index;
				}
				index = index % possible.length;
				int whileCounter = 0;
				//checks if the ant would be going to home space
				while((possible[index].home_yet() || possible[index].nScent > 0) && whileCounter < possible.length) {
					index = rands.nextInt();
					if(index < 0) {
						index = -index;
					}
					whileCounter++;
					index = index % possible.length;
				}
			}
			return possible[index];
		}else {		//Ant has food
			int index = getIndexWithFood(possible);
			return possible[index];
		}
		
	}
	
	public int getIndexWithFood(Space[] possible) {
		
		int index = -1;
		//If the ant hasn't seen food, will look at the nScent to get back to home
		if(!sawFood) {
			for(int i = 0; i < possible.length; i++) {
				//if any of the places it looks at is home, will go there no matter what if it has food
				if(hasFood) {
					if(possible[i].home_yet()) {
						return i;
					}
				}
				//checks to see the normal scent on that space, if there is, the index becomes that space to send back
				if(possible[i].nScent > 0) {
					if(index != -1) {
						if(possible[i].nScent > possible[index].nScent) {
							index = i;
						}
					}else {
						index = i;
					}
				}
			}
			sawFood = true;
			return index;
		//Has seen food before, will look for fScents to get back to home	
		}else {
			for(int i = 0; i < possible.length; i++) {
				//if any of the places it looks at is home, will go there no matter what if it has food
				if(hasFood) {
					if(possible[i].home_yet()) {
						firstTime = false;
						return i;
					}
				}
				if(!firstTime) {
					//checks to see if there is a food scent on that space, if there is, the index becomes that space to send back
					if(possible[i].fScent > 0) {
						if(possible[i].row != prevRow && possible[i].col != prevCol) {
							if(index != -1) {
								if(dis >= checkDisplace(possible[i].row, possible[i].col)) {
									if(possible[i].fScent > possible[index].fScent) {
										index = i;
									}
								}
							}else {
								index = i;
							}
						}
					}
				}else{
					//didn't see food scent so looks for nScent
					if(possible[i].nScent > 0) {
						if(possible[i].row != prevRow && possible[i].col != prevCol) {
							if(index != -1) {
								if(dis >= checkDisplace(possible[i].row, possible[i].col)) { 
									index = i;
								}
							}else {
								index = i;
							}
						}
					}
				}
			}
			if(index == -1) {
				index = 0;
			}
			return index;
		}
		
	}
	
	//get the index of the next space to move to with food
	public int getIndexWithoutFood(Space[] possible) {
		
		int index = -1;
		for(int i = 0; i < possible.length; i++) {
			
			if(sawFood) {
				//checks to see if there is a food scent on that space, if there is, the index becomes that space to send back
				if(possible[i].fScent > 0) {
					if(possible[i].row != prevRow && possible[i].col != prevCol) {
						if(index != -1) {
							if(possible[i].fScent > possible[index].fScent) {
								index = i;
							}
						}else {
							index = i;
						}
					}
				}
			}
		}
		return index;
	}
	
	//getting all possible moves the ant could make
	public Space[] checkMoves(int row, int col) {
		
		ArrayList<Space> nextMoves = new ArrayList<Space>();
		
		//checks to the top of the ant
		if(row-1 >= 0) {
			if(!grid[row-1][col].is_blocked) {
				nextMoves.add(grid[row-1][col]);
			}
		}/*else {		//wrap around board
			if(!grid[rowLen-1][col].is_blocked) {
				nextMoves.add(grid[rowLen-1][col]);
			}
		}*/
		
		//checks to the bottom of the ant
		if(row+1 < rowLen) {
			if(!grid[row+1][col].is_blocked) {
				nextMoves.add(grid[row+1][col]);
			}
		}/*else {		//wrap around board
			if(!grid[0][col].is_blocked){
				nextMoves.add(grid[0][col]);
			}
		}*/
		
		//checks the left of the ant
		if(col-1 >= 0) {
			if(!grid[row][col-1].is_blocked) {
				nextMoves.add(grid[row][col-1]);
			}
		}/*else {		//wrap around board
			if(!grid[row][colLen-1].is_blocked) {
				nextMoves.add(grid[row][colLen-1]);
			}
		}*/
		
		//checks the right of the ant
		if(col+1 < colLen) {
			if(!grid[row][col+1].is_blocked) {
				nextMoves.add(grid[row][col+1]);
			}
		}/*else {		//wrap around board
			if(!grid[row][0].is_blocked) {
				nextMoves.add(grid[row][0]);
			}
		}*/
		/*
		//checks the top left and top right of the ant
		if(row-1 >= 0) {
			if(col-1 >= 0) {	//top left
				if(!grid[row-1][col-1].is_blocked) {
					nextMoves.add(grid[row-1][col-1]);
				}
			}else {
				if(!grid[row-1][colLen-1].is_blocked) {
					nextMoves.add(grid[row-1][colLen-1]);
				}
			}
			if(col+1 < colLen) {	//top right
				if(!grid[row-1][col+1].is_blocked) {
					nextMoves.add(grid[row-1][col+1]);
				}
			}else {
				if(!grid[row-1][0].is_blocked) {
					nextMoves.add(grid[row-1][0]);
				}
			}
		}else {		//wrap around board
			if(col-1 >= 0) {	//top left 
				if(!grid[rowLen-1][col-1].is_blocked) {
					nextMoves.add(grid[rowLen-1][col-1]);
				}
			}else {
				if(!grid[rowLen-1][colLen-1].is_blocked) {
					nextMoves.add(grid[rowLen-1][colLen-1]);
				}
			}
			if(col+1 < colLen) {	//top right
				if(!grid[rowLen-1][col+1].is_blocked) {
					nextMoves.add(grid[rowLen-1][col+1]);
				}
			}else {
				if(!grid[rowLen-1][0].is_blocked) {
					nextMoves.add(grid[rowLen-1][0]);
				}
			}
		}
		
		//checks the bottom left and bottom right of the ant
		if(row+1 < rowLen) {
			if(col-1 >= 0) {	//bottom left
				if(!grid[row+1][col-1].is_blocked) {
					nextMoves.add(grid[row+1][col-1]);
				}
			}else {
				if(!grid[row+1][colLen-1].is_blocked) {
					nextMoves.add(grid[row+1][colLen-1]);
				}
			}
			if(col+1 < colLen) {	//bottom right
				if(!grid[row+1][col+1].is_blocked) {
					nextMoves.add(grid[row+1][col+1]);
				}else {
					if(!grid[row+1][0].is_blocked) {
						nextMoves.add(grid[row+1][0]);
					}
				}
			}
		}else {		//wrap around board
			if(col-1 >= 0) {	//bottom left
				if(!grid[0][col-1].is_blocked) {
					nextMoves.add(grid[0][col-1]);
				}
			}else {
				if(!grid[0][colLen-1].is_blocked) {
					nextMoves.add(grid[0][colLen-1]);
				}
			}
			if(col+1 < colLen) {	//bottom right
				if(!grid[0][col+1].is_blocked) {
					nextMoves.add(grid[0][col+1]);
				}else {
					if(!grid[0][0].is_blocked) {
						nextMoves.add(grid[0][0]);
					}
				}
			}
		}*/
		Space[] nm = new Space[nextMoves.size()];
		for(int i = 0; i < nextMoves.size(); i++) {
			nm[i] = nextMoves.get(i);
		}
		return nm;
	}
	
	//checks the displacement of the ant from its home
	public double checkDisplace(int r, int c) {
		int sum;
		if(r > hPosR) {
			sum = (r-hPosR)*(r-hPosR);
		}else {
			sum = (hPosR-r)*(hPosR-r);
		}
		if(c > hPosC) {
			sum = sum + (c-hPosC)*(c-hPosC);
		}else {
			sum = sum + (hPosC-c)*(hPosC-c);
		}
		return (int) Math.sqrt(sum);
	}
	
	public boolean ifHasFood() {
		return hasFood;
	}
	
	public void setNewBoard(Space[][] newFrame) {
		grid = newFrame;
	}
}
