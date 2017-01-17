package com.xemplar.utils.pc.leveldesigner;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import com.xemplar.utils.pc.leveldesigner.TileButton.TileGroup;
import com.xemplar.utils.pc.leveldesigner.dialogs.*;

public class Main extends JFrame implements ActionListener{
	private static final long serialVersionUID = -2895410634768339711L;
	private static final String LD_VERSION = "0.1.4_1036 Beta", UPDATE_URL = "https://www.xemplarsoft.com/apps/ldns/version.php",
                                DOWNLOAD_URL = "https://www.xemplarsoft.com/apps/ldns/ldns.zip";
	public static Main instance;

	public static final int ACTION_DRAW = 0x01;
    public static final int ACTION_SELC = 0x02;
    public static final int ACTION_ERAS = 0x03;
    public static int CURRENT_ACTION = ACTION_DRAW;

	public boolean HAS_BEEN_SAVED = false;
	public static String CURRENT_ID = "";
	public String FILE_NAME = "";
	
	public static boolean hasEntity = false;
	public static String entity = "";
	public static int MAX_INDEX = 0;
	public static int CURRENT_SET = 0;
	
	private ArrayList<ArrayList<JButton>> buttons = new ArrayList<ArrayList<JButton>>();
	public DialogFinishedListener extra = new DialogFinishedListener() {
		public void dialogFinished(Object arg) {
			if(arg == null) return;
			Map<String, Object> params = (Map<String, Object>)arg;
			int id = (int) params.get("id");
			int x = (int)params.get("x");
			int y = (int)params.get("y");
			boolean edit = (boolean)params.get("edit");

            int x_old = 0;
            int y_old = 0;

			if(edit){
                x_old = (int)params.get("x-old");
                y_old = (int)params.get("y-old");

                edit &= !(x == x_old && y == y_old);
            }

			String data = "";
			switch(id){
				case 1: {
					data = "x" + id + "#" + x + "#" + y + "#" + params.get("s");
					int[] choords = (int[])params.get("swatches");
					for(int i = 0; i < choords.length; i+=2){
						data += "#" + choords[i] + "#" + choords[i + 1];
					}
				} break;
				case 2: {
					data = "x" + id + "#" + x + "#" + y;
				} break;
				case 5: {
					int dx = (int)params.get("dx");
					int dy = (int)params.get("dy");
					data = "e" + id + "#" + dx + "#" + dy + "#" + params.get("s");
					int[] choords = (int[])params.get("swatches");
					for(int i = 0; i < choords.length; i+=2){
						data += "#" + choords[i] + "#" + choords[i + 1];
					}
				} break;
				case 4: {
					int dx = (int)params.get("dx");
					int dy = (int)params.get("dy");
					data = "e" + id + "#" + dx + "#" + dy;
				} break;
			}

			System.out.println(data);
			if(data.startsWith("e")){
				field.setIdAt(x, y, data);
			} else if(data.startsWith("x")){
				field.extras.add(data);
			}
			if(edit){
			    field.setIdAt(x_old, y_old, "00");
            }
			field.repaint();
		}
	};
	private JPanel contentPane, buttonContainer;
	private static JScrollPane scrollPane;
	private JStatusBar status;
	private JToolBar tools;
	private ButtonGroup actions;
	public Drawspace field;

	public static void main(String[] args) {
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);

        String newest = checkUpdate();
		System.out.println(newest);
		if(newest != null && !newest.equalsIgnoreCase(LD_VERSION)){
		    int res = JOptionPane.showConfirmDialog(null, "There is an update available: v" + newest + " do you want to download? You must manually extract.", "Update Available", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
		    if(res == JOptionPane.YES_OPTION){
		        download(DOWNLOAD_URL);
            }
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
	
	public static void setChoords(int x, int y){
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
        // MenuBar Items
		JMenu mnFile = new JMenu("File");
        JMenu mnEdit = new JMenu("Edit");
        JMenu mnInsert = new JMenu("Insert");
		menuBar.add(mnFile);
        menuBar.add(mnEdit);
        menuBar.add(mnInsert);

        // FileMenu Items
		JMenuItem mntmNew = new JMenuItem("New");
		JMenuItem mntmOpen = new JMenuItem("Open");
        JMenuItem mntmExport = new JMenuItem("Export");
        JMenuItem mntmSave = new JMenuItem("Save");
        JMenuItem mntmSaveAs = new JMenuItem("Save As");
        JMenuItem mntmExit = new JMenuItem("Exit");

		mnFile.add(mntmNew);
        mnFile.add(mntmOpen);
        mnFile.add(new JSeparator());
        mnFile.add(mntmExport);
        mnFile.add(mntmSave);
        mnFile.add(mntmSaveAs);
        mnFile.add(new JSeparator());
        mnFile.add(mntmExit);

		mntmNew.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				NewFileDialog dialog = new NewFileDialog();
				dialog.setDialogFinishListener(new DialogFinishedListener(){
					public void dialogFinished(Object arg) {
						if(arg.equals("cancled")) return;
						
						Object[] args = (Object[]) arg;
						
						int width = (int)args[0];
						int height = (int)args[1];
						
						field.resizeField(width, height);
						field.extras.clear();
						HAS_BEEN_SAVED = false;
					}
				});
				setTitle("New Level");
			}
		});
		mntmOpen.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				field.loadLevel(openFile());
				if(!FILE_NAME.equals("")){
					setTitle(FILE_NAME);
				}
			}
		});
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
		mntmExport.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				save("/home/roxas/levelExp.txt");
				if(!FILE_NAME.equals("")){
					setTitle(FILE_NAME);
				}
			}
		});
		mntmSaveAs.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				saveFile();
				if(!FILE_NAME.equals("")){
					setTitle(FILE_NAME);
				}
			}
		});
        mntmExit.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

		// EditMenu Items
		JMenuItem mntmUndo = new JMenuItem("Undo");
        JMenuItem mntmRedo = new JMenuItem("Redo");
        JMenuItem mntmCut = new JMenuItem("Cut");
        JMenuItem mntmCopy = new JMenuItem("Copy");
        JMenuItem mntmPaste = new JMenuItem("Paste");
        JMenuItem mntmDelete = new JMenuItem("Delete");

		mnEdit.add(mntmUndo);
		mnEdit.add(mntmRedo);
		mnEdit.add(new JSeparator());
		mnEdit.add(mntmCut);
		mnEdit.add(mntmCopy);
		mnEdit.add(mntmPaste);
		mnEdit.add(mntmDelete);

		// InsertMenu Items
		JMenuItem mntmEntity = new JMenuItem("Object");
        JMenuItem mntExtra = new JMenuItem("Extra");
		mnInsert.add(mntmEntity);
        mnInsert.add(mntExtra);

		mntmEntity.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				InsertEntityDialog dialog = new InsertEntityDialog();
				dialog.setDialogFinishListener(new DialogFinishedListener(){
					public void dialogFinished(Object arg) {
						if(arg.equals("cancled")) return;
						
						hasEntity = true;
						entity = (String) arg;
					}
				});
			}
		});
        mntExtra.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
			    System.out.println("Extra");
				ExtraDialog dialog = new ExtraDialog(extra);
                dialog.pack();
				dialog.setVisible(true);
			}
		});

        // Content Items
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{0, 0, 0};
		gbl_contentPane.rowHeights = new int[]{0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);

        //ToolBar Items

        actions = new ButtonGroup();
        tools = new JToolBar();
        tools.setFloatable(false);

        JCheckBox cb_lines = makeLayer("Lines", "lines");
        JCheckBox cb_block = makeLayer("Blocks", "block");
        JCheckBox cb_extra = makeLayer("Extras", "extra");

        actions.add(makeTool("cursor", "select", "Select Block", "select"));
        actions.add(makeTool("pencil", "draw", "Place Blocks", "draw"));
        actions.add(makeTool("eraser", "erase", "Erase Blocks", "erase"));

        Enumeration<AbstractButton> elements = actions.getElements();
        while(elements.hasMoreElements()){
            tools.add(elements.nextElement());
        }

        tools.add(cb_lines);
        tools.add(cb_block);
        tools.add(cb_extra);

        GridBagConstraints gbc_tools = new GridBagConstraints();
        gbc_tools.insets = new Insets(0, 0, 5, 5);
        gbc_tools.fill = GridBagConstraints.HORIZONTAL;
        gbc_tools.gridx = 0;
        gbc_tools.gridy = 0;
        gbc_tools.gridwidth = 2;
        gbc_tools.gridheight = 1;
        contentPane.add(tools, gbc_tools);


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
		gbc_scrollPane_1.gridy = 1;
		contentPane.add(scrollPane_1, gbc_scrollPane_1);
		
		buttonContainer = new JPanel();
		buttonContainer.setPreferredSize(new Dimension((48 + 5) * 4, ((TileButton.ALL.size() / 4) * (48 + 5)) + 10));
		scrollPane_1.setViewportView(buttonContainer);
		
		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.weightx = 1;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 1;
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
		gbc_panel.gridy = 2;
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

	public static String checkUpdate(){ try {
        URL updateLoc = new URL(UPDATE_URL);
        BufferedReader data = new BufferedReader(new InputStreamReader(updateLoc.openStream()));

        String curr_version = data.readLine();
        data.close();

        return curr_version;
    } catch(Exception e){ return null; }}

    public static void zoomOut(Drawspace field, Point point) {
        /*field.setScale(field.getScale() * 0.9f);
        Point pos = scrollPane.getViewport().getViewPosition();

        int newX = (int)(point.x*(0.9f - 1f) + 0.9f*pos.x);
        int newY = (int)(point.y*(0.9f - 1f) + 0.9f*pos.y);
        scrollPane.getViewport().setViewPosition(new Point(newX, newY));

        field.revalidate();
        field.repaint();*/
    }
    public static void zoomIn(Drawspace field, Point point) {
        /*field.setScale(field.getScale() * 1.1f);
        Point pos = scrollPane.getViewport().getViewPosition();

        int newX = (int)(point.x*(1.1f - 1f) + 1.1f*pos.x);
        int newY = (int)(point.y*(1.1f - 1f) + 1.1f*pos.y);
        scrollPane.getViewport().setViewPosition(new Point(newX, newY));

        field.revalidate();
        field.repaint();*/
    }

	public void save(String path) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path)));
			writer.write(field.saveLevel());
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

	public static boolean download(final String file){ try {
	    Thread t = new Thread(new Runnable() {
            public void run() { try {
                Download d = new Download(new URL(file), new File("ldns.zip"));
                JProgressDialog dialog = new JProgressDialog("Update Download", "Downloading...", d.getSize());
                while(d.getStatus() == Download.DOWNLOADING){
                    dialog.updateProgress(d.getProgress());
                    Thread.sleep(10);
                }
                dialog.setVisible(false);
            } catch(Exception e) {}}
        });
	    t.start();

	    return true;
    } catch(Exception e) { return false; }}

	public void actionPerformed(ActionEvent e){
	    String command = e.getActionCommand();
	    if(command.startsWith("tool")){
	        String[] args = command.split(":");
            if(args[1].equals("select")) CURRENT_ACTION = ACTION_SELC;
            if(args[1].equals("draw"))   CURRENT_ACTION = ACTION_DRAW;
            if(args[1].equals("erase"))  CURRENT_ACTION = ACTION_ERAS;
        } else if(command.startsWith("draw")){
	        boolean state = ((JCheckBox) e.getSource()).isSelected();

            String[] args = command.split(":");
            if(args[1].equals("lines")) Drawspace.DRAW_LINES = state;
            if(args[1].equals("block")) Drawspace.DRAW_BLOCK = state;
            if(args[1].equals("extra")) Drawspace.DRAW_EXTRA = state;

            field.repaint();
        } else {
            CURRENT_ID = ((JButton) e.getSource()).getName();
            Main.setSelectedBlock(CURRENT_ID);

            for (JButton button : buttons.get(0)) {
                ((TileButton) button).setSelected(false);
            }
            ((TileButton) e.getSource()).setSelected(true);

            CURRENT_ACTION = ACTION_DRAW;
            actions.clearSelection();
            for(JToggleButton button : tool_arr){
                if(button.getActionCommand().equals("draw")){
                    button.doClick();
                    button.setSelected(true);
                    actions.setSelected(button.getModel(), false);
                } else {
                    button.setSelected(false);
                }
                button.updateUI();
            }
        }
	}
	public void setTitle(String title){
		super.setTitle("Level Designer - " + title);
	}

	private ArrayList<JToggleButton> tool_arr = new ArrayList<JToggleButton>();
    private JToggleButton makeTool(String imageName, String actionID, String tip, String alt) {
        Image img = null;
        try{
            img = ImageIO.read(new File("res/icons/" + imageName + ".png"));
        } catch (Exception e){
            e.printStackTrace();
        }

        JToggleButton button = new JToggleButton();
        button.setActionCommand("tool:" + actionID);
        button.setToolTipText(tip);
        button.addActionListener(this);
        button.setFocusPainted(true);

        if (img != null) {
            button.setIcon(new ImageIcon(img, alt));
        }

        tool_arr.add(button);
        return tool_arr.get(tool_arr.size() - 1);
    }
    private JCheckBox makeLayer(String text, String actionID){
	    JCheckBox box = new JCheckBox(text);
	    box.setActionCommand("draw:" + actionID);
	    box.addActionListener(this);
	    box.setSelected(true);

	    return box;
    }
}
