package com.xemplar.utils.pc.leveldesigner;

import java.awt.*;
import java.awt.event.*;
import java.util.Map;

import javax.swing.*;

import com.xemplar.utils.pc.leveldesigner.dialogs.AnimateBlock;
import com.xemplar.utils.pc.leveldesigner.dialogs.DialogFinishedListener;
import com.xemplar.utils.pc.leveldesigner.dialogs.InsertEntityDialog;
import com.xemplar.utils.pc.leveldesigner.dialogs.JContextMenu;

public class Drawspace extends JPanel implements MouseListener, MouseMotionListener, ActionListener, MouseWheelListener{
	private static final long serialVersionUID = 831560106889837755L;
	
	public static final int SIZE = 48;
	
	private int width, height;
	private JContextMenu menu;
	private String[] ids;
	private float scale = 1F;
	
	public Drawspace(int width, int height){
		resizeField(width, height);

		this.setAutoscrolls(true);
		this.setFocusable(true);

		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);

		menu = new JContextMenu(this);
	}

    public void setScale(float amt){
        this.scale = amt;
    }

    public float getScale(){
        return this.scale;
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
        Graphics2D g2 = (Graphics2D) g;
        int w = this.width * SIZE;
        int h = this.width * SIZE;

        g2.translate(w/2, h/2);
        g2.scale(scale, scale);
        g2.translate(-w/2, -h/2);

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
				String current = ids[x + y * width];
				if(current != "00"){
				    if(current.startsWith("e03")) {
                        String[] params = current.split("#");
                        g.drawImage(TileButton.IMAGES.get(params[1]), x * SIZE, y * SIZE, SIZE, SIZE, null);
                    } else if(current.startsWith("e") && !current.equals("ext")){
						int id = Integer.parseInt(current.charAt(1) + "" + current.charAt(2));
						for(EntityCreator create : InsertEntityDialog.creators){
							if(create.getID() == id){
								g.drawImage(create.getImage(), x * SIZE, y * SIZE, SIZE, SIZE, null);
								break;
							}
						}
						
					} else {
						g.drawImage(TileButton.IMAGES.get(current), x * SIZE, y * SIZE, SIZE, SIZE, null);
					}
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

	public String getIdAt(int x, int y){
		return ids[x + y * width];
	}

	public void mouseClicked(MouseEvent e) {
        int mouseX = (int)(e.getX() / scale) / SIZE;
        int mouseY = (int)(e.getY() / scale) / SIZE;
		
		if(e.getButton() == MouseEvent.BUTTON1){
			if(Main.hasEntity){
				ids[mouseX + mouseY * width] = Main.entity;
			} else {
				ids[mouseX + mouseY * width] = Main.CURRENT_ID;
			}
		} else {
		    if(Main.hasEntity){

            } else {
                menu.show(this, e.getX(), e.getY(), mouseX, mouseY);
            }
		}
		repaint();
	}

	public void actionPerformed(ActionEvent e){
		String[] dat = e.getActionCommand().split(":");
        String action = dat[0];
        int tx = Integer.parseInt(dat[1]);
        int ty = Integer.parseInt(dat[2]);
        String id = getIdAt(tx, ty);

        if(action.equals("uan")){        // Un Animate
            String[] op = id.split("#");
            ids[tx + ty * width] = op[1];
        } else if(action.equals("ani")){ // Animate
			AnimateBlock dialog = new AnimateBlock(new DialogFinishedListener(){
				public void dialogFinished(Object arg) {
					if(arg == null) return;
					Map<String, Object> map = (Map<String, Object>)arg;
					int x = (int)map.get("x");
					int y = (int)map.get("y");
					int dir = map.get("dir").equals(true) ? 1 : 0;
                    ids[x + y * width] = "e03#" + map.get("id") + "#" + dir + "#" + map.get("dist") + "#" + map.get("sped");
				}
			}, id, tx, ty);
            dialog.setVisible(true);

            for(int i = 0; i < ids.length; i++){
                if(i % width == 0){
                    System.out.println();
                }
                System.out.print(ids[i] + " ");
            }
        } else if(action.equals("rep")){ // Replace

        } else if(action.equals("del")){ // Delete
            ids[tx + ty * width] = "00";
        } else if(action.equals("edi")){ // Edit Entity

        }
        repaint();
    }

    public Dimension getPreferredSize() {
        return new Dimension((int)(scale * SIZE * width), (int)(scale * SIZE * height));
    }

    public void mouseMoved(MouseEvent e) {
        int mouseX = (int)(e.getX() / scale) / SIZE;
        int mouseY = (int)(e.getY() / scale) / SIZE;
		
		Main.setCoords(mouseX, mouseY);
	}

    public void mouseWheelMoved(MouseWheelEvent e) {
        int b1 = MouseWheelEvent.CTRL_DOWN_MASK;
        if ((e.getModifiersEx() & (b1)) != b1) return;
        switch (e.getWheelRotation()) {
            case -1:
                Main.zoomIn(this, e.getLocationOnScreen());
                break;
            case 1:
                Main.zoomOut(this, e.getLocationOnScreen());
                break;
        }

        this.repaint();
    }

    public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mouseDragged(MouseEvent e) {
        int mouseX = (int)(e.getX() / scale) / SIZE;
        int mouseY = (int)(e.getY() / scale) / SIZE;

        int b1 = MouseEvent.BUTTON1_DOWN_MASK;
        int b2 = MouseEvent.BUTTON2_DOWN_MASK;
        if ((e.getModifiersEx() & (b1 | b2)) == b1) {
            if(Main.hasEntity){
                ids[mouseX + mouseY * width] = Main.entity;
            } else {
                ids[mouseX + mouseY * width] = Main.CURRENT_ID;

            }
        } else if ((e.getModifiersEx() & (b1 | b2)) == b2) {
            ids[mouseX + mouseY * width] = "00";
        }
        repaint();
    }
}
