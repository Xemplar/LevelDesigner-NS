package com.xemplar.utils.pc.leveldesigner;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.*;

import com.xemplar.utils.pc.leveldesigner.dialogs.*;

public class Drawspace extends JPanel implements MouseListener, MouseMotionListener, ActionListener, MouseWheelListener{
	private static final long serialVersionUID = 831560106889837755L;
	public static final int SIZE = 48;

    public static boolean DRAW_LINES = true, DRAW_BLOCK = true, DRAW_EXTRA = true;
    public static int SELECTED_X = -1, SELECTED_Y = -1;
    public static boolean SELECT = false;
	public static String COMMAND = "";

    public ArrayList<String> extras = new ArrayList<String>();
	public int width, height;
	private JContextMenu menu;
	private String[] ids;
	private float scale = 1F;
	private int mID, mX, mY;
	
	public Drawspace(int width, int height){
		resizeField(width, height);

		this.setAutoscrolls(true);
		this.setFocusable(true);

		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addMouseWheelListener(this);

		menu = new JContextMenu(this);
	}
	
	public void loadLevel(String[] data){
		if(data[0].equals("null_file")){
			System.out.println("failed to load");
			return;
		}
		int count = 0;
		for(int i = 0; i < data.length; i++){
		    if(data[i].startsWith("$")) count++;
        }

		int width = data[0].split(",").length;
		int height = data.length - count;
		
		System.out.println(width + " " + height);
		
		resizeField(width, height);
		
		for(int i = 0; i < data.length; i++){
		    if(data[i].startsWith("$")){
		        String[] ex = data[i].substring(1).split(",");
		        for(String s : ex){
		            extras.add(s);
                }
            } else {
                String[] current = data[i].split(",");

                for (int b = 0; b < width; b++) {
                    ids[b + i * width] = current[b];
                }
            }
		}
	}
    public String saveLevel(){
        String out = "";
        for(int y = 0; y < height; y++){
            String row = "";
            for(int x = 0; x < width; x++){
                row += ids[x + y * width] + (x < (width - 1) ? "," : "");
            }
            out += row + (y < (height - 1) ? "\n" : "");
        }
        if(extras.size() > 0){
            out += "\n$";
            for(int i = 0; i < extras.size(); i++){
                out += extras.get(i) + (i == extras.size() - 1 ? "" : ",");
            }
        }
        return out;
    }
	
	public void paint(Graphics g){
        g.setColor(new Color(0x000000));
        g.fillRect(0, 0, getWidth(), getHeight());

        Graphics2D g2 = (Graphics2D)g;

        if(DRAW_BLOCK) drawBlocks(g, g2);
        if(DRAW_EXTRA) drawExtras(g, g2);
        if(DRAW_LINES) drawLines(g, g2);
        drawHighlight(g, g2);
	}

	private void drawLines(Graphics g, Graphics2D g2){
	    g.setColor(new Color(0xAAAAAA));

        for(int i = 0; i <= width; i++){
            g.drawLine(i * SIZE, 0, i * SIZE, height * SIZE);
        }

        for(int i = 0; i <= height; i++){
            g.drawLine(0, i * SIZE, width * SIZE, i * SIZE);
        }
    }
	private void drawBlocks(Graphics g, Graphics2D g2){
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                String current = ids[x + y * width];
                if(current != "00"){
                    if(current.startsWith("e03")) {
                        String[] params = current.split("#");
                        g.drawImage(TileButton.IMAGES.get(params[1]), x * SIZE, y * SIZE, SIZE, SIZE, null);
                    } else if(current.startsWith("e") && !current.equals("ext")){
                        int id = Integer.parseInt(current.substring(1, current.indexOf("#")));
                        drawEntity(g, g2, x, y, id);
                    } else {
                        g.drawImage(TileButton.IMAGES.get(current), x * SIZE, y * SIZE, SIZE, SIZE, null);
                    }
                }
            }
        }
    }
	private void drawExtras(Graphics g, Graphics2D g2){
        for(String s : extras){
            String[] args = s.substring(1).split("#");
            int id = Integer.parseInt(args[0]);
            int x = Integer.parseInt(args[1]);
            int y = Integer.parseInt(args[2]);
            switch(id){
                case 1:{
                    g.drawImage(TileButton.EXTRAS.get("torch"), x * SIZE, y * SIZE, SIZE, SIZE, null);
                } break;
                case 2:{
                    g.drawImage(TileButton.EXTRAS.get("window"), x * SIZE, y * SIZE, SIZE, SIZE, null);
                } break;
            }
        }
    }
	private void drawHighlight(Graphics g, Graphics2D g2) {
        highlightAt(mX, mY, g, g2);
        highlightAt(SELECTED_X, SELECTED_Y, g, g2);
    }
	private void drawEntity(Graphics g, Graphics2D g2, int x, int y, int id){
        BufferedImage img = null;
	    if(id <= 3){
            for(EntityCreator create : InsertEntityDialog.creators){
                if(create.getID() == id){
                    img = create.getImage();
                    break;
                }
            }
        } else {
	        if(id == 4){
                img = TileButton.EXTRAS.get("door_open");
            }
            if(id == 5){
                img = TileButton.EXTRAS.get("door_locked");
            }
        }

        if(img != null){
            g.drawImage(img, x * SIZE, y * SIZE, SIZE, SIZE, null);
        }
    }

    private void highlightAt(int mX, int mY, Graphics g, Graphics2D g2){
        g2.setComposite(AlphaComposite.SrcOver.derive(0.3F));
        if (mX > -1) {
            String current = ids[mX + mY * width];
            int mID = -1;
            if(!current.startsWith("e")){
                boolean found = false;
                for(String extra : extras){
                    String[] args = extra.substring(1).split("#");
                    int x = Integer.parseInt(args[1]);
                    int y = Integer.parseInt(args[2]);

                    found |= mX == x && mY == y;
                    if(found){
                        mID = Integer.parseInt(args[0]);
                        break;
                    }
                }
                if(!found) return;
            } else {
                mID = Integer.parseInt(current.substring(1, current.indexOf("#")));
            }

            if (mID == 3) {
                g2.setColor(new Color(0x0000FF));

                String[] params = current.split("#");
                int x = mX * SIZE;
                int y = mY * SIZE;
                int dir = Integer.parseInt(params[2]);
                int amnt = (int) (Double.parseDouble(params[3]) * SIZE);
                switch (dir) {
                    case 1: {
                        if (amnt < 0) {
                            g2.fillRect(x, y, SIZE, (amnt - SIZE) * -1);
                        } else {
                            g2.fillRect(x, y - amnt, SIZE, amnt + SIZE);
                        }
                        break;
                    }
                    case 0: {
                        if (amnt < 0) {
                            g2.fillRect(x + amnt, y, (amnt - SIZE) * -1, SIZE);
                        } else {
                            g2.fillRect(x, y, amnt + SIZE, SIZE);
                        }
                        break;
                    }
                }
            } else if (mID == 4) {
                String[] params = current.split("#");
                int x = mX * SIZE;
                int y = mY * SIZE;

                int dx = Integer.parseInt(params[1]) * SIZE;
                int dy = Integer.parseInt(params[2]) * SIZE;

                g2.setColor(new Color(0x0000FF));
                g2.fillRect(x, y, SIZE, SIZE);
                g2.setColor(new Color(0x00FFFF));
                g2.fillRect(dx, dy, SIZE, SIZE);
            } else if (mID == 5) {
                String[] params = current.split("#");
                int x = mX * SIZE;
                int y = mY * SIZE;

                int dx = Integer.parseInt(params[1]) * SIZE;
                int dy = Integer.parseInt(params[2]) * SIZE;

                g2.setColor(new Color(0x0000FF));
                g2.fillRect(x, y, SIZE, SIZE);
                g2.setColor(new Color(0x00FFFF));
                g2.fillRect(dx, dy, SIZE, SIZE);

                g2.setColor(new Color(0x00FF00));
                for (int i = 4; i < params.length; i += 2) {
                    int sx = Integer.parseInt(params[i]);
                    int sy = Integer.parseInt(params[i + 1]);
                    g2.fillRect(sx * SIZE, sy * SIZE, SIZE, SIZE);
                }
            } else if(mID == 1){
                String[] params = current.split("#");
                int x = mX * SIZE;
                int y = mY * SIZE;

                g2.setColor(new Color(0x0000FF));
                g2.fillRect(x, y, SIZE, SIZE);

                g2.setColor(new Color(0x00FF00));
                for(int i = 4; i < params.length; i+= 2){
                    int sx = Integer.parseInt(params[i]);
                    int sy = Integer.parseInt(params[i+1]);
                    g2.fillRect(sx * SIZE, sy * SIZE, SIZE, SIZE);
                }
            }
        }
        g2.setComposite(AlphaComposite.SrcOver);
    }
	public String getIdAt(int x, int y){
		return ids[x + y * width];
	}
    public void setIdAt(int x, int y, String data){
	    ids[x + y * width] = data;
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
            if(id.startsWith("e") && !id.equals("ext")){
                String[] args = id.substring(1).split("#");
                int type = Integer.parseInt(args[0]);

                switch(type){
                    case 5:
                    case 4: {
                        ExtraDialog dialog = new ExtraDialog(Main.instance.extra, id, tx, ty);
                        dialog.pack();
                        dialog.setVisible(true);
                    } break;
                }
            }
        } else if(action.equals("context")){
            if(dat[3].equals("delete")){
                for(int i = 0 ; i < extras.size(); i++){
                    String curr = extras.get(i);
                    String[] args = curr.substring(1).split("#");
                    if(args[1].equals(tx + "") && args[2].equals(ty + "")){
                        extras.remove(i);
                        break;
                    }
                }
            }
        }
        repaint();
    }

    public void mouseClicked(MouseEvent e) {
        int mouseX = (int)(e.getX() / scale) / SIZE;
        int mouseY = (int)(e.getY() / scale) / SIZE;

        if(SELECT){
            System.out.println(ids[mouseX + mouseY * width]);
            ExtraDialog.setValue(COMMAND, mouseX, mouseY, ids[mouseX + mouseY * width]);

            SELECT = false;
            repaint();
            return;
        }
        if(e.getButton() == MouseEvent.BUTTON1){
            if(getIdAt(mouseX, mouseY).startsWith("e")) return;
            switch(Main.CURRENT_ACTION){
                case Main.ACTION_DRAW: {
                    if(Main.hasEntity){
                        ids[mouseX + mouseY * width] = Main.entity;
                    } else {
                        ids[mouseX + mouseY * width] = Main.CURRENT_ID;
                    }
                } break;
                case Main.ACTION_ERAS: {
                    ids[mouseX + mouseY * width] = "00";
                } break;
                case Main.ACTION_SELC: {
                    SELECTED_X = mouseX;
                    SELECTED_Y = mouseY;
                } break;
            }
        } else {
            if(Main.hasEntity){

            } else {
                ArrayList<String> exts = new ArrayList<>();
                for(String s : extras){
                    String[] args = s.substring(1).split("#");
                    int tx = Integer.parseInt(args[1]);
                    int ty = Integer.parseInt(args[2]);
                    if(tx == mouseX && ty == mouseY) exts.add(s);
                }
                String[] re = new String[exts.size()];
                re = exts.toArray(re);
                System.out.println("Length: " + re.length);
                menu.show(this, e.getX(), e.getY(), mouseX, mouseY, re);
            }
        }
        repaint();
    }
    public void mouseMoved(MouseEvent e) {
        int mouseX = (int)(e.getX() / scale) / SIZE;
        int mouseY = (int)(e.getY() / scale) / SIZE;

        if(mouseX < 0 || mouseX >= width) return;
        if(mouseY < 0 || mouseY >= height) return;

		Main.setChoords(mouseX, mouseY);
        String data = ids[mouseX + mouseY * width];
		if((data.startsWith("e") && !data.startsWith("ext"))){
		    this.mX = mouseX;
		    this.mY = mouseY;
		    this.mID = Integer.parseInt(data.substring(1, data.indexOf("#")));
		    repaint();
        } else if(mouseX != -1 && mouseY != -1){
            this.mX = -1;
            this.mY = -1;
            repaint();
        }

        for(String s : extras) {
            String[] args = s.substring(1).split("#");
            int id = Integer.parseInt(args[0]);

            if(args[1].equals(mouseX + "") && args[2].equals(mouseY + "")){
                this.mX = mouseX;
                this.mY = mouseY;
                this.mID = id;
            }
        }
	}
    public void mouseDragged(MouseEvent e) {
        int mouseX = (int)(e.getX() / scale) / SIZE;
        int mouseY = (int)(e.getY() / scale) / SIZE;

        int b1 = MouseEvent.BUTTON1_DOWN_MASK;
        int b2 = MouseEvent.BUTTON3_DOWN_MASK;

        if(getIdAt(mouseX, mouseY).startsWith("e")) return;

        switch(Main.CURRENT_ACTION){
            case Main.ACTION_DRAW: {
                if ((e.getModifiersEx() & (b1 | b2)) == b1) {
                    if(Main.hasEntity){
                        ids[mouseX + mouseY * width] = Main.entity;
                    } else {
                        ids[mouseX + mouseY * width] = Main.CURRENT_ID;

                    }
                }
            } break;
            case Main.ACTION_ERAS: {
                if ((e.getModifiersEx() & (b1 | b2)) == b1) {
                    ids[mouseX + mouseY * width] = "00";
                }
            } break;
            case Main.ACTION_SELC: {
                //TODO - Add Select commands
            } break;
        }
        repaint();
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

    public Dimension getPreferredSize() {
        return new Dimension((int)(scale * SIZE * width), (int)(scale * SIZE * height));
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
    public void setScale(float amt){ this.scale = amt; }
    public float getScale(){ return this.scale; }
}
