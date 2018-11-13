import java.awt.Font;

import javax.swing.JButton;


public class MyButton extends JButton{

	private int x;
	private int y;
	
	private int n;
	
	public MyButton(int x, int y){
		super();
		this.x = x;
		this.y = y;
		n = 0;
	}

	public int getValue() {
		return n;
	}
	
	public void setValue(int value) {
		this.n = value;
		if(n == 0){
			setText("");
		} else {
			int logn = (int) (Math.log(n) / Math.log(2));
			setFont(new Font("Arial", Font.PLAIN, 20 + 2*logn));
			setText(Integer.toString(n));
		}
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public void multiply(){
		setValue(n*2);
	}

	
}
