/* A simple snake game 

/* @Author : Fabrice Appolinary

* CopyRight (C) 2014 by NiiMs Inc, All rights reserved
*/

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

class label extends JLabel{

public label (){
    setPreferredSize(new Dimension(20,20));
    setBackground(Color.BLACK);
    setOpaque(true);
}
public void setSnakeBody(){
    setBackground(Color.red);
    setBorder(BorderFactory.createLineBorder(Color.gray));
}
public void setFood(){
    setBackground(Color.blue);
}
public void reset(){
    setBackground(Color.BLACK);
    setBorder(BorderFactory.createLineBorder(Color.black));
}
}
//====================================
public class Game extends JPanel implements KeyListener{
    private  int X,Y, foodEaten , DELAY = 200;
    private label [][] grid;
    private final int UP = 2 , DOWN = 4 , LEFT = -1 , RIGHT = 3 , PLAYING = -2, PAUSED = -4;
    private label head = null , tail = null, food ; 
    private ArrayList <label> SnakeList = new ArrayList <label>();
    private Random rand = new Random();
    private JFrame frame;  
    private int STATE = PAUSED ;
    private int DIRECTION = RIGHT;
    private Container content;        
    private JButton NewGame = new JButton("New"), Play = new JButton("Play"), Quit = new JButton("Quit");
    private JLabel Score = new JLabel("Score : " + foodEaten) ;
    private JPanel panel = new JPanel();
    private Object controller = new Object();
    private Container panel2;

public Game(){
     setSize(400,400);
     setFocusable(true);
     addKeyListener(this);
     setLayout(new GridLayout(20,20));
     grid= new label[20][20];
     
     for(int i = 0 ; i < 20 ; i++){
        for(int j = 0 ; j < 20 ; j++){
            grid[i][j] = new label();
            add(grid[i][j]);
        }
     }
     panel.setPreferredSize(new Dimension(400,40));
     panel.setBackground(Color.gray);
     NewGame.setPreferredSize(new Dimension(80, 20));
     panel.add(NewGame);
     Play.setPreferredSize(new Dimension(80,20));
     panel.add(Play);
     Quit.setPreferredSize(new Dimension(80,20));
     panel.add(Quit);
     Score.setForeground(Color.white);
     panel.add(Score);
     
     head = grid[Y][X];
     SnakeList.add(head);
     head.setBackground(Color.red);
     createFood();
     
     frame = new JFrame();
     frame.add(this);
     frame.add(panel, BorderLayout.SOUTH);
     frame.setSize(400,440);
     frame.setLocation(300,150);
     frame.pack();
     frame.setTitle("Fabrice's Snake Game ");
     frame.setResizable(false);
     frame.setBackground(Color.gray);
     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     frame.setVisible(true);
     
     content = frame.getContentPane();
     panel2 = this;
     
     Thread thread = new Thread(new Runnable(){
    	 public void run(){
    		 while(true){
    		synchronized(controller){
    		if(STATE == PAUSED){
    			try{
    				controller.wait();
    			}catch(Exception ed){}
    		}
    		head.setBackground(Color.green);
    		checkDirection();
    		move();
    		try{
    			Thread.sleep(DELAY);
    		}catch(Exception en){};
    	 }
    	 }
    	 }
    }
     );
   thread.start();  
   Play.addActionListener(
          new ActionListener(){
    	    public void actionPerformed(ActionEvent e){
    	    	
    	    	if(STATE == PAUSED){
    	    		panel2.requestFocus();
    	    		STATE = PLAYING;
    	    		Play.setText("PAUSE");
    	    		synchronized(controller){
    	    		controller.notify();
    	    		}
    	    	}else if(STATE == PLAYING){
    	    		STATE = PAUSED;
    	    		Play.setText("PLAY");
    	    	}
    	    }
     }
   );
   Quit.addActionListener(new ActionListener(){
	   public void actionPerformed(ActionEvent e){
		   int state = STATE;
		   if (state == PLAYING)STATE = PAUSED;
		   int option = JOptionPane.showConfirmDialog(null, "Are you sure\n"+ "you want to quit ? ?", "Fabrice's Snake", JOptionPane.INFORMATION_MESSAGE);
		   if(option == JOptionPane.YES_OPTION) {System.exit(0);}
		   else{
			   panel2.requestFocus();
			   synchronized(controller){
				   controller.notify();
			   }
               STATE = state;
		        return;
		   }
	   }
   });
   NewGame.addActionListener(new ActionListener(){
	   public void actionPerformed(ActionEvent e){
		   int state =STATE;
		   if (state == PLAYING)STATE = PAUSED;
		   int option = JOptionPane.showConfirmDialog(null, "Are you sure\n"+ "you want to start a new game ? ?", "Fabrice's Snake", JOptionPane.INFORMATION_MESSAGE);
		   if (option == JOptionPane.YES_OPTION) newGame();
		   else {
               panel2.requestFocus();
			   synchronized(controller){ controller.notify();}
               STATE = state;
			   return;
		   }
	   }
   });
}
public void keyPressed(KeyEvent e){
    int key = e.getKeyCode();

    if(key==KeyEvent.VK_LEFT && DIRECTION != RIGHT){
       DIRECTION = LEFT;
    }
    else if(key==KeyEvent.VK_RIGHT && DIRECTION != LEFT){
       DIRECTION = RIGHT; 
    }
    else if(key==KeyEvent.VK_UP && DIRECTION != DOWN){
      DIRECTION = UP ;
    }
    else if(key==KeyEvent.VK_DOWN && DIRECTION != UP){
      DIRECTION = DOWN ;
    }
}
public void newGame(){	
	for(int i=0; i < SnakeList.size(); i++) {
		  SnakeList.get(i).reset();
	}
	   SnakeList.removeAll(SnakeList);
	   food.reset();
	   X=0; Y=0;
	   head = grid[Y][X];
	   SnakeList.add(head);
	   head.setSnakeBody();
	   createFood();
	   Play.setText("PLAY");
	   DIRECTION = RIGHT;
	   foodEaten = 0;
	   Score.setText("Score : " +foodEaten);
	   STATE = PAUSED;
	   panel2.requestFocus();
}
public void keyTyped(KeyEvent e){}
public void keyReleased(KeyEvent e){}

public void move(){

    if(X > 19 ) X = 0;
    if(X < 0 ) X = 19;
    if(Y < 0)  Y =19;
    if(Y > 19) Y = 0;
    if(hasCollided()){
        head = grid[Y][X];
        SnakeList.add(0,head);
        head.setSnakeBody();
        createFood();
        Score.setText("Score : "+ ++foodEaten);
        return;
    }
    else if(SnakeEatenIteself()){
        gameOver();
    }
    else{
    head = grid[Y][X];
    SnakeList.add(0,head);
    head.setSnakeBody();
    tail = SnakeList.remove(SnakeList.size()-1);
    tail.reset();
    }
}
private void checkDirection(){
	if(DIRECTION == UP) Y--;
	else if(DIRECTION == DOWN) Y++;
	else if(DIRECTION == LEFT) X--;
	else if(DIRECTION == RIGHT) X++;
}
private void createFood(){
    int i = rand.nextInt(20);
    int j = rand.nextInt(20);
    if(isSnake(grid[i][j])) createFood();
    else{    
    food = grid[i][j];
    food.setFood(); 
    return; 
    }        
} 
private void gameOver(){
      JOptionPane.showMessageDialog(null, " Game Over ! ! ! \n "+ "Start a New Game", "Fabrice's Snake ", JOptionPane.PLAIN_MESSAGE);
      newGame();
}
private boolean SnakeEatenIteself(){
    for (int i = 0 ; i < SnakeList.size(); i++)
        if(head == SnakeList.get(i) && i != 0){ return true;}
    return false;
}
private boolean hasCollided(){
    return (head == food);
}
private boolean isSnake(label body){
    for(int i = 0; i < SnakeList.size(); i++)
        if(SnakeList.get(i) == body) return true;
return false;
}  
public static void main(String args[]){
    new Game();    
}


}

