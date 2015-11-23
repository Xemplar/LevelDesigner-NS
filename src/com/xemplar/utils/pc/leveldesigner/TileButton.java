package com.xemplar.utils.pc.leveldesigner;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JButton;

import com.xemplar.utils.pc.leveldesigner.dialogs.InsertEntityDialog;

public class TileButton extends JButton{
	private static final long serialVersionUID = -6168609316617502199L;
	public static final Map<String, String> ALL= new HashMap<String, String>();
	public static final Map<String, BufferedImage> IMAGES= new HashMap<String, BufferedImage>();
	public static ArrayList<TileGroup> groups = new ArrayList<TileGroup>();
	
	public static String PACK_NAME;
	public static String DIR;
	
	static { try{
		BufferedReader read = new BufferedReader(new FileReader(new File("res/nerdshooter.ldc")));
		String curr = "";
		int count = 0;
		ArrayList<String> file = new ArrayList<String>();
		while((curr = read.readLine()) != null){
			file.add(curr);
		}
		for(String current : file){
			if(count == 0){
				PACK_NAME = current;
			} else if(count == 1){
				String[] dir = current.split(":");
				if(dir[0].equalsIgnoreCase("internal")){
					DIR = dir[1];
				} else {
					
				}
			} else {
				String[] com = current.split(":");
				if(com[0].equalsIgnoreCase("group")){
					groups.add(new TileGroup(com[1]));
				} else if(com[0].equalsIgnoreCase("entity")){
					int id = Integer.parseInt(com[4]);
					String[] args = com[2].split("/");
					InsertEntityDialog.creators.add(new EntityCreator(com[1], id, com[3], args));
				} else if(com[0].equalsIgnoreCase("all")){
					for(TileGroup g : groups){
						g.put(com[2], com[1]);
					}
					ALL.put(com[2], com[1]);
					
					BufferedImage in = ImageIO.read(new File("res/" + DIR + "/" + com[1]));
					
					int padding = 0;
					BufferedImage image = new BufferedImage(in.getWidth() + (padding * 2), in.getHeight() + (padding * 2), in.getType());
					
					Graphics g = image.getGraphics();
					g.setColor(new Color(0x00FFFFFF));
					g.fillRect(0, 0, image.getWidth(), image.getHeight());
					
					g.drawImage(in, padding, padding, image.getWidth() - (padding * 2), image.getHeight() - (padding * 2), null);
					g.dispose();
					
					IMAGES.put(com[2], removeBG(image));
				}
			}
			count++;
		}
		read.close();
	} catch(Exception e){}}
	
	public static String id_pressed = "";
	
	protected final String ID;
	protected boolean selected;
	public TileButton(String ID){
		this.ID = ID;
		this.setName(ID);
	}
	
	public void paint(Graphics g){
		g.drawImage(IMAGES.get(ID), 0, 0, getWidth() - 5, getHeight() - 5, null);
	}
	
	public void setSelected(boolean selected){
		this.selected = selected;
		repaint();
	}
	
	public static class TileGroup{
		private ArrayList<String> ids = new ArrayList<String>();
		private ArrayList<String> vals = new ArrayList<String>();
		
		private final String id;
		public TileGroup(String id){
			this.id = id;
		}
		
		public String getID(){
			return id;
		}
		
		public void put(String key, String val){
			this.ids.add(key);
			this.vals.add(val);
		}
		
		public String getVal(String key){
			for(int i = 0; i < ids.size(); i++){
				if(ids.get(i).equals(key)){
					return ids.get(i);
				}
			}
			
			return null;
		}
		
		public String getKeyAt(int index){
			return ids.get(index);
		}
		
		public String getValAt(int index){
			return vals.get(index);
		}
		
		public int getCount(){
			return vals.size();
		}
	}
	
	private static BufferedImage removeBG(BufferedImage in){
		BufferedImage out = new BufferedImage(in.getWidth(), in.getHeight(), in.getType());
		for(int y = 0; y < in.getHeight(); y++){
			for(int x = 0; x < in.getWidth(); x++){
				int col = in.getRGB(x, y);
				if(col == 0xFFFFFFFF) continue;
				out.setRGB(x, y, col);
			}
		}
		
		return out;
	}
}
