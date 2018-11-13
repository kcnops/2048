import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.BorderLayout;

import javax.swing.JTextField;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.awt.Choice;
import java.awt.Panel;


public class GameScreen {

	private JFrame frame;
	private JButton btnNewGame;
	private JButton btnSave;
	private JPanel panel;
	private JPanel textPanel;
	private JTextField topscoreField;
	private JTextField scoreField;

	private Choice choice;
	private int fieldSize;
		
	private Map<Integer,Map<Integer,MyButton>> fieldsMap;
	
	private int score;
	private HashMap<Integer,Integer> topscores;
	private final String filename = "scores.txt";
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new GameScreen();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws IOException 
	 */
	public GameScreen() throws IOException {
		initialize();
		frame.setVisible(true);
		loadTopscores();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setFocusable(true);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setPreferredSize(new Dimension(300,300));
		
		textPanel = new JPanel();
		frame.getContentPane().add(textPanel, BorderLayout.SOUTH);
		textPanel.setLayout(new BorderLayout(0, 0));
		
		scoreField = new JTextField();
		textPanel.add(scoreField, BorderLayout.CENTER);
		scoreField.setEditable(false);
		scoreField.setFocusable(false);
		
		topscoreField = new JTextField();
		textPanel.add(topscoreField, BorderLayout.EAST);
		topscoreField.setColumns(10);
		topscoreField.setEditable(false);
		topscoreField.setFocusable(false);
		
		frame.pack();

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		menuBar.setFocusable(false);
		
		btnNewGame = new JButton("New Game");
		menuBar.add(btnNewGame);
		btnNewGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newGame();
			}
		});
		
		btnSave = new JButton("Save Score");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveTopscore();
			}
		});
		menuBar.add(btnSave);
		
		choice = new Choice();
		menuBar.add(choice);
		choice.add("2");
		choice.add("3");
		choice.add("4");
		
		KeyListener keyListener = new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {
				Direction direction;
				if(e.getKeyCode() == KeyEvent.VK_UP){
					direction = Direction.UP;
					move(direction);
				} else if(e.getKeyCode() == KeyEvent.VK_DOWN){
					direction = Direction.DOWN;
					move(direction);
				} else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
					direction = Direction.RIGHT;
					move(direction);
				} else if(e.getKeyCode() == KeyEvent.VK_LEFT){
					direction = Direction.LEFT;
					move(direction);
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {}
		};
				
//		frame.addKeyListener(keyListener);
//		panel.addKeyListener(keyListener);
//		menuBar.addKeyListener(keyListener);
		btnNewGame.addKeyListener(keyListener);
		btnSave.addKeyListener(keyListener);
		choice.addKeyListener(keyListener);

	}
	
	private void newGame(){
		// clear
		fieldSize = Integer.parseInt(choice.getSelectedItem());
		score = 0;
		panel.removeAll();
		panel.setLayout(new GridLayout(fieldSize, fieldSize, 1, 1));
		panel.setPreferredSize(new Dimension(100*fieldSize,100*fieldSize));
		fieldsMap = new HashMap<Integer,Map<Integer,MyButton>>();
		// setup
		for(int i=1; i<=fieldSize; i++){
			HashMap<Integer, MyButton> tempMap = new HashMap<Integer,MyButton>();
			for(int j=1; j<=fieldSize; j++){
				MyButton tempField = new MyButton(i,j);
				tempField.setBackground(Color.WHITE);
				tempField.setFocusable(false);
				panel.add(tempField);
				tempMap.put(j, tempField);
			}
			fieldsMap.put(i, tempMap);
		}
		// 2's
		addRandom();
		addRandom();
		// Show topscore
		topscoreField.setText("Topscore: " + topscores.get(fieldSize));
		// revalidate
		panel.revalidate();
	}
	
	private ArrayList<MyButton> getFields(){
		ArrayList<MyButton> fields = new ArrayList<MyButton>();
		for(Map<Integer,MyButton> map : fieldsMap.values()){
			for(MyButton button : map.values()){
				fields.add(button);
			}
		}
		return fields;
	}
	
	private void addRandom(){
		Random ran = new Random();
		Boolean finished = false;
		while(!finished){
			int index = ran.nextInt(getFields().size());
			MyButton field = getFields().get(index);
			if(field.getValue() == 0){
				finished = true;
				field.setValue(2);
			}
		}
	}
	
	private void move(Direction direction){
		// First do doubles, a field can only double once
		// Hardcoded for 4 fields
		// Should be refactored to new method "findDoubles", returning true when one found
		// and calls itself after finding one (or after a 0), to start again with next field.
		List<MyButton> buttonsToStartWith = new ArrayList<MyButton>();
		if(direction == Direction.DOWN){
			buttonsToStartWith.addAll(fieldsMap.get(fieldSize).values());
		} else if (direction == Direction.UP) {
			buttonsToStartWith.addAll(fieldsMap.get(1).values());
		} else if (direction == Direction.LEFT) {
			for(int i = 1; i<=fieldSize; i++){
				buttonsToStartWith.add(fieldsMap.get(i).get(1));
			}
		} else if (direction == Direction.RIGHT){
			for(int i = 1; i<=fieldSize; i++){
				buttonsToStartWith.add(fieldsMap.get(i).get(fieldSize));
			}
		}

		Boolean anyMove = false;
		for(MyButton button : buttonsToStartWith){
			Boolean didMove = findDouble(button, direction);
			if(didMove && !anyMove){
				anyMove = true;
			}
		}
						
		Boolean changed = true;
		while(changed){
			changed = false;
			for(MyButton button : getFields()){
				int value = button.getValue();
				if(value > 0){
					MyButton nextButton = getNextButton(button, direction);
					if(nextButton != null){
						int nextValue = nextButton.getValue();
							if(nextValue == 0){
								button.setValue(0);
								nextButton.setValue(value);
								changed = true;
								anyMove = true;
							}
					}
				}
			}
		}
		if(anyMove){
			addRandom();
			checkFinished();
		}
	}
	
	private Boolean findDouble(MyButton button, Direction direction){
		if(button == null){return false;}
		MyButton previousButton = getPreviousButton(button, direction);
		if(previousButton == null){return false;}
		int value = button.getValue();
		if(value == 0){
			return findDouble(previousButton, direction);
		} else {
			int previousValue = previousButton.getValue();
			if(previousValue == value){
				multiply(button);
				previousButton.setValue(0);
				findDouble(getPreviousButton(previousButton, direction), direction);
				return true;
			}
			else if(previousValue != 0){
				return findDouble(previousButton, direction);
			}
			else if(previousValue == 0){
				previousButton = getPreviousButton(previousButton, direction);
				while(previousButton != null){
					previousValue = previousButton.getValue();
					if(previousValue == value){
						multiply(button);
						previousButton.setValue(0);
						findDouble(getPreviousButton(previousButton, direction),direction);
						return true;
					}
					else if(previousValue != 0){
						return findDouble(previousButton, direction);
					}
					previousButton = getPreviousButton(previousButton, direction);
				}
				return false;
			}
		}
		return false;
	}
	
	private MyButton getNextButton(MyButton button, Direction direction){
		int x = button.getX();
		int y = button.getY();
		if(direction == Direction.DOWN){
			if(x == fieldSize){ return null;}
			else {x++;}
			}
		else if(direction == Direction.UP){
			if(x == 1) {return null;}
			else {x--;}
		}
		else if(direction == Direction.RIGHT){
			if(y == fieldSize) {return null;}
			else {y++;}
		}
		else if(direction == Direction.LEFT){
			if(y == 1) {return null;}
			else {y--;}
		}
		return fieldsMap.get(x).get(y);
	}
	
	private MyButton getPreviousButton(MyButton button, Direction direction){
		int x = button.getX();
		int y = button.getY();
		if(direction == Direction.DOWN){
			if(x == 1){ return null;}
			else {x--;}
			}
		else if(direction == Direction.UP){
			if(x == fieldSize) {return null;}
			else {x++;}
		}
		else if(direction == Direction.RIGHT){
			if(y == 1) {return null;}
			else {y--;}
		}
		else if(direction == Direction.LEFT){
			if(y == fieldSize) {return null;}
			else {y++;}
		}
		return fieldsMap.get(x).get(y);
	}
	
	private void multiply(MyButton button){
		button.multiply();
		score += button.getValue();
		setText("Score: " + Integer.toString(score));
	}
	
	private void setText(String text){
		scoreField.setText(text);
	}
	
	private void loadTopscores() throws IOException{
		try {
			FileInputStream is = new FileInputStream(filename);
			DataInputStream in = new DataInputStream(is);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			readTopscores(br);
		} catch (FileNotFoundException e) {
			Writer writer = null;
			try {
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("scores.txt"), "utf-8"));
			} catch (Exception e2) {
				e2.printStackTrace();
			} finally {
				if(writer != null) {
					try {
						writer.close();
					} catch (IOException e1) {}
				}
			}
		}
	}
	
	private void readTopscores(BufferedReader br) throws IOException{
		topscores = new HashMap<Integer,Integer>();
		String nextLine;
		while((nextLine = br.readLine()) != null){
			String[] splittedString = nextLine.split(" ");
			topscores.put(Integer.parseInt(splittedString[0]), Integer.parseInt(splittedString[1]));
		}
		int amount = choice.getItemCount();
		if(topscores.size() != amount){
			for(int i=0; i < amount; i++){
				int size = Integer.parseInt(choice.getItem(i));
				if(!topscores.containsKey(size)){
					topscores.put(size, 0);
				}
			}
		}
	}
	
	private void saveTopscore(){
		if(score > topscores.get(fieldSize)){
			topscores.put(fieldSize, score);		
		}
		topscoreField.setText("Topscore: " + topscores.get(fieldSize));
		writeTopscores();
	}
	
	private void writeTopscores(){
		try {
			FileWriter fw = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(fw);
			for(int fieldSize : topscores.keySet()){
				out.write(fieldSize + " " + topscores.get(fieldSize) + "\n");
			}
			out.close();
		} catch (IOException e) {
			System.out.println("Could not write file.");
			e.printStackTrace();
		}
	}
	
	private void resetTopscores(){
		try {
			FileWriter fw = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(fw);
			out.close();
		} catch (IOException e) {
			System.out.println("Could not write file.");
			e.printStackTrace();
		}
	}
	
	private void checkFinished(){
		for(MyButton button1 : getFields()){
			if(button1.getValue() == 0){
				return;
			}
			for(MyButton button2 : getFields()){
				if(button1.getValue() == button2.getValue()){
					if((Math.abs(button1.getX() - button2.getX()) + Math.abs(button1.getY() - button2.getY())) == 1){
						return;
					}
				}
			}
		}
		//Finished
		if(score > topscores.get(fieldSize)){
			JOptionPane.showMessageDialog(frame, "You reached a score of " + score + ".\n" + "That's a new record, congratulations!", "Game Over!", JOptionPane.PLAIN_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(frame, "You reached a score of " + score + ".", "Game Over!", JOptionPane.PLAIN_MESSAGE);
		}
		saveTopscore();
		topscoreField.setText("Topscore: " + topscores.get(fieldSize));
	}
	
}
