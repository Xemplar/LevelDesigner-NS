package com.xemplar.utils.pc.leveldesigner;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Drawspace extends JPanel implements MouseListener, MouseMotionListener{
	private static final long serialVersionUID = 831560106889837755L;
	
	public static final int SIZE = 48;
	
	private int width, height;
	private String[] ids;
	
	public Drawspace(int width, int height){
		resizeField(width, height);

		this.setAutoscrolls(true);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}
	
	public void resizeField(int width, int height){
		this.width = width;
		this.height = height;
		
		System.out.println(width + " " + height);
		ids = new String[width * height];
		
		for(int i = 0; i < ids.length; i++){
			ids[i] = "00";
		}
		
		Dimension d = new Dimension(width * SIZE + 1, height * SIZE + 1);
		
		this.setSize(d);
		this.setMinimumSize(d);
		this.setMaximumSize(d);
		this.setPreferredSize(d);
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				Main.setBoardSize(width, height);
			}
		});
		
		this.repaint();
	}
	
	public void loadLevel(String[] data){
		if(data[0].equals("null_file")){
			System.out.println("failed to load");
			return;
		}
		
		int width = data[0].split(",").length;
		int height = data.length;
		
		System.out.println(width + " " + height);
		
		resizeField(width, height);
		
		for(int i = 0; i < data.length; i++){
			String[] current = data[i].split(",");
			
			for(int b = 0; b < width; b++){
				ids[b + i * width] = current[b];
			}
		}
	}
	
	public void paint(Graphics g){
		g.setColor(new Color(0x000000));
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.setColor(new Color(0xAAAAAA));
		
		for(int i = 0; i <= width; i++){
			g.drawLine(i * SIZE, 0, i * SIZE, height * SIZE);
		}
		
		for(int i = 0; i <= height; i++){
			g.drawLine(0, i * SIZE, width * SIZE, i * SIZE);
		}
		
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				if(ids[x + y * width] != "00"){
					g.drawImage(TileButton.IMAGES.get(ids[x + y * width]), x * SIZE, y * SIZE, SIZE, SIZE, null);
				}
			}
		}
	}
	
	public String getData(){
		String out = "";
		for(int y = 0; y < height; y++){
			String row = "";
			for(int x = 0; x < width; x++){
				row += ids[x + y * width] + (x < (width - 1) ? "," : "");
			}
			out += row + (y < (height - 1) ? "\n" : "");
		}
		
		return out;
	}
	
	public void mouseClicked(MouseEvent e) {
		int mouseX = e.getX() / SIZE;
		int mouseY = e.getY() / SIZE;
		
		if(e.getButton() == MouseEvent.BUTTON1){
			ids[mouseX + mouseY * width] = Main.CURRENT_ID;
		} else {
			ids[mouseX + mouseY * width] = "00";
		}
		repaint();
	}

	public void mouseMoved(MouseEvent e) {
		int mouseX = e.getX() / SIZE;
		int mouseY = e.getY() / SIZE;
		
		Main.setCoords(mouseX, mouseY);
	}
	
	
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mouseDragged(MouseEvent e) { }
}
