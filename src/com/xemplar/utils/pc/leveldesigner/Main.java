package com.xemplar.utils.pc.leveldesigner;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import com.xemplar.utils.pc.leveldesigner.TileButton.TileGroup;
import com.xemplar.utils.pc.leveldesigner.dialogs.DialogFinishedListener;
import com.xemplar.utils.pc.leveldesigner.dialogs.InsertEntityDialog;
import com.xemplar.utils.pc.leveldesigner.dialogs.NewFileDialog;

public class Main extends JFrame implements ActionListener{
	private static final long serialVersionUID = -2895410634768339711L;
	private static Main instance;

	public boolean HAS_BEEN_SAVED = false;
	public static String CURRENT_ID = "";
	public String FILE_NAME = "";
	
	public static boolean hasEntity = false;
	public static String entity = "";
	public static int MAX_INDEX = 0;
	public static int CURRENT_SET = 0;
	
	private ArrayList<ArrayList<JButton>> buttons = new ArrayList<ArrayList<JButton>>();
	private JPanel contentPane, buttonContainer;
	private JScrollPane scrollPane;
	private JStatusBar status;
	public Drawspace field;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e){
			
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					instance = new Main();
					instance.setVisible(true);
					instance.setTitle("New File");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public static void setCoords(int x, int y){
		instance.status.setCoords(x, y);
	}
	
	public static void setBoardSize(int width, int height){
		instance.status.setBoardSize(width, height);
	}
	
	public static void setSelectedBlock(String ID){
		instance.status.setSelectedBlock(ID);
	}
	
	/**
	 * Create the frame.
	 */
	public Main() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 480);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmNew = new JMenuItem("New");
		mnFile.add(mntmNew);
		mntmNew.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				NewFileDialog dialog = new NewFileDialog();
				dialog.setDialogedFinishListener(new DialogFinishedListener(){
					public void dialogFinished(Object arg) {
						if(arg.equals("cancled")) return;
						
						Object[] args = (Object[]) arg;
						
						int width = (int)args[0];
						int height = (int)args[1];
						
						field.resizeField(width, height);
						HAS_BEEN_SAVED = false;
					}
				});
				setTitle("New Level");
			}
		});
		
		JMenuItem mntmOpen = new JMenuItem("Open");
		mnFile.add(mntmOpen);
		mntmOpen.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				field.loadLevel(openFile());
				if(!FILE_NAME.equals("")){
					setTitle(FILE_NAME);
				}
			}
		});
		
		
		JSeparator sep0 = new JSeparator();
		mnFile.add(sep0);
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mnFile.add(mntmSave);
		mntmSave.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				System.out.println(HAS_BEEN_SAVED);
				if(HAS_BEEN_SAVED){
					save(FILE_NAME);
				} else {
					saveFile();
				}
				if(!FILE_NAME.equals("")){
					setTitle(FILE_NAME);
				}
			}
		});
		
		JMenuItem mntmExport = new JMenuItem("Export");
		mnFile.add(mntmExport);
		mntmExport.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				save("/home/roxas/levelExp.txt");
				if(!FILE_NAME.equals("")){
					setTitle(FILE_NAME);
				}
			}
		});
		
		JMenuItem mntmSaveAs = new JMenuItem("Save As");
		mnFile.add(mntmSaveAs);
		mntmSaveAs.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				saveFile();
				if(!FILE_NAME.equals("")){
					setTitle(FILE_NAME);
				}
			}
		});
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenuItem mntmUndo = new JMenuItem("Undo");
		mnEdit.add(mntmUndo);
		
		JMenuItem mntmRedo = new JMenuItem("Redo");
		mnEdit.add(mntmRedo);
		
		JSeparator sep1 = new JSeparator();
		mnEdit.add(sep1);
		
		JMenuItem mntmCut = new JMenuItem("Cut");
		mnEdit.add(mntmCut);
		
		JMenuItem mntmCopy = new JMenuItem("Copy");
		mnEdit.add(mntmCopy);
		
		JMenuItem mntmPaste = new JMenuItem("Paste");
		mnEdit.add(mntmPaste);
		
		JMenuItem mntmDelete = new JMenuItem("Delete");
		mnEdit.add(mntmDelete);
		
		JMenu mnInsert = new JMenu("Insert");
		menuBar.add(mnInsert);
		
		JMenuItem mntmEntity = new JMenuItem("Entity");
		mnInsert.add(mntmEntity);
		mntmEntity.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				InsertEntityDialog dialog = new InsertEntityDialog();
				dialog.setDialogedFinishListener(new DialogFinishedListener(){
					public void dialogFinished(Object arg) {
						if(arg.equals("cancled")) return;
						
						hasEntity = true;
						entity = (String) arg;
					}
				});
			}
		});
		
		
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_1.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane_1.setPreferredSize(new Dimension(((48 + 5) * 4) + 20, 0));
		scrollPane_1.setMinimumSize(new Dimension(((48 + 5) * 4) + 20, 0));
		
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane_1.fill = GridBagConstraints.VERTICAL;
		gbc_scrollPane_1.gridx = 0;
		gbc_scrollPane_1.gridy = 0;
		contentPane.add(scrollPane_1, gbc_scrollPane_1);
		
		buttonContainer = new JPanel();
		buttonContainer.setPreferredSize(new Dimension((48 + 5) * 4, ((TileButton.ALL.size() / 4) * (48 + 5)) + 10));
		scrollPane_1.setViewportView(buttonContainer);
		
		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.weightx = 1;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 0;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		field = new Drawspace(20, 20);
		scrollPane.setViewportView(field);
		
		status = new JStatusBar();
		status.setMinimumSize(new Dimension(0, 25));
		status.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 2;
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		contentPane.add(status, gbc_panel);
		
		Dimension d = new Dimension(48, 48);
		MAX_INDEX = TileButton.groups.size();
		
		for(TileGroup current : TileButton.groups){
			ArrayList<JButton> group = new ArrayList<JButton>();
			for(int i = 0; i < current.getCount(); i++){
				TileButton button = new TileButton(current.getKeyAt(i));
				button.addActionListener(this);
				button.setPreferredSize(d);
				button.setMaximumSize(d);
				button.setMinimumSize(d);
				group.add(button);
			}
			
			buttons.add(group);
		}
		
		setSet(0);
		HAS_BEEN_SAVED = false;
	}
	
	public void save(String path){
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)));
			writer.write(field.getData());
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setSet(int index){
		buttonContainer.removeAll();
		ArrayList<JButton> group = buttons.get(index);
		for(int i = 0; i < group.size(); i++){
			buttonContainer.add(group.get(i));
		}
	}
	
	public void saveFile(){
		FileDialog dialog = new FileDialog(this, "Open Nerd Shooter Level", FileDialog.SAVE);
		if(!FILE_NAME.equals("")){
			dialog.setFile(FILE_NAME);
		}
		dialog.setFilenameFilter(new FilenameFilter(){
			public boolean accept(File dir, String name) {
				String extension = "";

				int i = name.lastIndexOf('.');
				if (i > 0) {
				    extension = name.substring(i+1);
				}
				
				switch(extension){
				case "nsl":
				case "txt":
					return true;
				default:
					return false;
				}
			}
		});
		
		dialog.setVisible(true);
		dialog.toFront();
		dialog.requestFocus();
		FILE_NAME = dialog.getDirectory() + dialog.getFile();
		save(FILE_NAME);

		HAS_BEEN_SAVED = true;
	}
	
	public String[] openFile(){
		FileDialog dialog = new FileDialog(this, "Open Nerd Shooter Level", FileDialog.LOAD);
		if(!FILE_NAME.equals("")){
			dialog.setFile(FILE_NAME);
		}
		dialog.setFilenameFilter(new FilenameFilter(){
			public boolean accept(File dir, String name) {
				String extension = "";

				int i = name.lastIndexOf('.');
				if (i > 0) {
				    extension = name.substring(i+1);
				}
				
				switch(extension){
				case "nsl":
				case "txt":
					return true;
				default:
					return false;
				}
			}
		});
		
		dialog.setVisible(true);
		dialog.toFront();
		dialog.requestFocus();
		FILE_NAME = dialog.getDirectory() + dialog.getFile();
		System.out.println(FILE_NAME);
		File f = new File(FILE_NAME);
		
		if(f.exists()){
			try{
				ArrayList<String> r = new ArrayList<String>();
				BufferedReader read = new BufferedReader(new FileReader(f));
			
				String curr = "";
				while((curr = read.readLine()) != null){
					r.add(curr);
				}
				
				read.close();
				
				String[] out = new String[r.size()];
				out = r.toArray(out);
				 
				HAS_BEEN_SAVED = true;
				
				return out;
			} catch(Exception e){
				HAS_BEEN_SAVED = false;
				return new String[]{"null_file"};
			}
		} else {
			HAS_BEEN_SAVED = false;
			return new String[]{"null_file"};
		}
	}
	
	public void actionPerformed(ActionEvent e){
		CURRENT_ID = ((JButton)e.getSource()).getName();
		Main.setSelectedBlock(CURRENT_ID);
		
		for(JButton button : buttons.get(0)){
			((TileButton)button).setSelected(false);
		}
		((TileButton)e.getSource()).setSelected(true);
	}
	
	public void setTitle(String title){
		super.setTitle("Level Designer - " + title);
	}
}
