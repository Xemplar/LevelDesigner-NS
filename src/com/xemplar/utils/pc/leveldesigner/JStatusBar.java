package com.xemplar.utils.pc.leveldesigner;

import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

public class JStatusBar extends JPanel{
	private static final long serialVersionUID = 1L;
	private JLabel boardSize, coords, runServer;
	private JPanel selectedBlock;
	private String imgID;
	
	public JStatusBar(){
		this.setBounds(0, 0, 300, 25);
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		JLabel lblSelected = new JLabel("Selected: ");
		springLayout.putConstraint(SpringLayout.NORTH, lblSelected, 0, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, lblSelected, 5, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, lblSelected, 0, SpringLayout.SOUTH, this);
		add(lblSelected);
		
		selectedBlock = new JPanel(){
			private static final long serialVersionUID = 1157509861047277132L;
			public void paint(Graphics g){
				g.clearRect(0, 0, this.getWidth(), this.getHeight());
				g.drawImage(TileButton.IMAGES.get(imgID), 0, 0, getWidth(), getHeight(), null); 
			}
		};
		springLayout.putConstraint(SpringLayout.NORTH, selectedBlock, 0, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, selectedBlock, 5, SpringLayout.EAST, lblSelected);
		springLayout.putConstraint(SpringLayout.SOUTH, selectedBlock, 0, SpringLayout.SOUTH, lblSelected);
		springLayout.putConstraint(SpringLayout.EAST, selectedBlock, 25, SpringLayout.EAST, lblSelected);
		add(selectedBlock);
		
		JSeparator sep0 = new JSeparator();
		springLayout.putConstraint(SpringLayout.NORTH, sep0, 0, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, sep0, 5, SpringLayout.EAST, selectedBlock);
		springLayout.putConstraint(SpringLayout.SOUTH, sep0, 0, SpringLayout.SOUTH, this);
		sep0.setOrientation(SwingConstants.VERTICAL);
		add(sep0);

		boardSize = new JLabel("Size: 0 x 0");
		springLayout.putConstraint(SpringLayout.NORTH, boardSize, 0, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, boardSize, 5, SpringLayout.WEST, sep0);
		springLayout.putConstraint(SpringLayout.SOUTH, boardSize, 0, SpringLayout.SOUTH, this);
		add(boardSize);

		JSeparator sep1 = new JSeparator();
		springLayout.putConstraint(SpringLayout.NORTH, sep1, 0, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, sep1, 5, SpringLayout.EAST, boardSize);
		springLayout.putConstraint(SpringLayout.SOUTH, sep1, 0, SpringLayout.SOUTH, this);
		sep1.setOrientation(SwingConstants.VERTICAL);
		add(sep1);

		runServer = new JLabel("Test Server Status: stopped");
		springLayout.putConstraint(SpringLayout.NORTH, runServer, 0, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, runServer, 5, SpringLayout.WEST, sep1);
		springLayout.putConstraint(SpringLayout.SOUTH, runServer, 0, SpringLayout.SOUTH, this);
		add(runServer);
		
		coords = new JLabel("(0, 0)");
		springLayout.putConstraint(SpringLayout.NORTH, coords, 0, SpringLayout.NORTH, lblSelected);
		springLayout.putConstraint(SpringLayout.SOUTH, coords, 0, SpringLayout.SOUTH, lblSelected);
		springLayout.putConstraint(SpringLayout.EAST, coords, -5, SpringLayout.EAST, this);
		add(coords);
		
		JSeparator sep2 = new JSeparator();
		springLayout.putConstraint(SpringLayout.NORTH, sep2, 0, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, sep2, 5, SpringLayout.EAST, boardSize);
		sep2.setOrientation(SwingConstants.VERTICAL);
		springLayout.putConstraint(SpringLayout.SOUTH, sep2, 0, SpringLayout.SOUTH, lblSelected);
		add(sep2);
	}
	
	public void setCoords(int x, int y){
		coords.setText("(" + x + ", " + y + ")");
	}
	
	public void setBoardSize(int width, int height){
		boardSize.setText("Size: " + width + " x " + height);
	}
	
	public void setSelectedBlock(String ID){
		this.imgID = ID;
		selectedBlock.repaint();
	}

	public void setServerStatus(boolean status){
	    if(status){
	        runServer.setText("Test Server Status: started");
        } else {
            runServer.setText("Test Server Status: stopped");
        }
    }
}
