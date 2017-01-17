package com.xemplar.utils.pc.leveldesigner;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.imageio.ImageIO;

public class EntityCreator {
	private static final NumberFormat format = new DecimalFormat("00");
	private final String[] args, vals;
	private BufferedImage img;
	private final String name;
	private final int id;
	
	public EntityCreator(String name, int id, String image, String[] args){
		try{
			img = ImageIO.read(new File("res/" + TileButton.DIR + "/" + image));
		} catch(Exception e){
			e.printStackTrace();
		}
		
		this.id = id;
		this.args = args;
		this.name = name;
		this.vals = new String[args.length];
	}
	
	private EntityCreator(String name, int id, BufferedImage img, String[] args){
		this.id = id;
		this.img = img;
		this.args = args;
		this.name = name;
		this.vals = new String[args.length];
	}
	
	public int getID(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	public BufferedImage getImage(){
		return img;
	}
	
	public boolean putForArg(String arg, String val){
		for(int i = 0; i < args.length; i++){
			if(args[i].equals(arg)){
				vals[i] = val;
				return true;
			}
		}
		
		return false;
	}
	
	public String[] getArguments(){
		return args;
	}
	
	public boolean isFilledOut(){
		for(int i = 0; i < args.length; i++){
			if(vals[i] == null){
				return false;
			} else if(vals[i] == ""){
				return false;
			}
		}
		
		return true;
	}
	
	public String getLevelID(){
		String arg = "";
		for(int i = 0; i < args.length; i++){
			arg += vals[i] + (i < args.length - 1 ? "#" : "");
		}
		return "e" + format.format(id) + "#" + arg;
	}
	
	public String toString(){
		return name;
	}
	
	public EntityCreator clone(){
		return new EntityCreator(name, id, img, args);
	}
}
