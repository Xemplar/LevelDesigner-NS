package com.xemplar.utils.pc.leveldesigner.dialogs;

import com.xemplar.utils.pc.leveldesigner.Drawspace;
import com.xemplar.utils.pc.leveldesigner.TileButton;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExtraDialog extends JDialog implements ListSelectionListener, ActionListener{
    private static ExtraDialog instance;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JList<String> extras;
    private JSpinner xPos;
    private JSpinner yPos;
    private JButton selectPos;
    private JList swatches;
    private JButton addSwatch;
    private JPanel imgDisp;
    private JSpinner xDest;
    private JSpinner yDest;
    private JButton selectDest;

    private BlockModel model;
    private DialogFinishedListener listener;
    private String current, image = "";
    private int currentID;

    public ExtraDialog(DialogFinishedListener listener) {
        instance = this;
        this.listener = listener;

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
            public void windowClosed(WindowEvent e) {
                onCancel();
            }
        });
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                xPos.setEnabled(false);
                yPos.setEnabled(false);
                xDest.setEnabled(false);
                yDest.setEnabled(false);

                swatches.setEnabled(false);
                addSwatch.setEnabled(false);
                selectPos.setEnabled(false);
                selectDest.setEnabled(false);
            }
        });
    }

    private void onOK() {
        int[] swatches = null;
        if(addSwatch.isEnabled()) {
            swatches = new int[model.getSize() * 2];
            for (int i = 0; i < swatches.length; i += 2) {
                Swatch curr = model.getSwatchAt(i / 2);
                swatches[i] = curr.x;
                swatches[i + 1] = curr.y;
            }
        }

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", xPos.getValue());
        params.put("y", yPos.getValue());
        params.put("id", currentID);
        switch(currentID){
            case 1:
                params.put("s", swatches.length / 2);
                params.put("swatches", swatches);
                break;

            case 5:
                params.put("s", swatches.length / 2);
                params.put("swatches", swatches);
            case 4:
                params.put("dx", xDest.getValue());
                params.put("dy", yDest.getValue());
                break;
        }

        listener.dialogFinished(params);
        dispose();
    }

    private void onCancel() {
        Drawspace.COMMAND = "";
        this.setOpacity(1F);
        listener.dialogFinished(null);
        Drawspace.SELECT = false;
        dispose();
    }

    public void actionPerformed(ActionEvent e) {
        String name = e.getActionCommand();
        System.out.println(name);

        if(name.contains("select")){
            Drawspace.SELECT = true;
            Drawspace.COMMAND = "pos";
            this.setOpacity(0.3F);
        } else if(name.contains("dest")){
            Drawspace.SELECT = true;
            Drawspace.COMMAND = "dest";
            this.setOpacity(0.3F);
        } else if(name.contains("swatch")){
            Drawspace.SELECT = true;
            Drawspace.COMMAND = "swatch";
            this.setOpacity(0.3F);
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        current = nameLookup.get(((JList<String>)e.getSource()).getSelectedValue());
        if(current.equals("wind")){
            xPos.setEnabled(true);
            yPos.setEnabled(true);
            xDest.setEnabled(false);
            yDest.setEnabled(false);

            swatches.setEnabled(false);
            addSwatch.setEnabled(false);
            selectPos.setEnabled(true);
            selectDest.setEnabled(false);

            setImage("window");
            currentID = 2;
        } else if(current.equals("toch")){
            xPos.setEnabled(true);
            yPos.setEnabled(true);
            xDest.setEnabled(false);
            yDest.setEnabled(false);

            swatches.setEnabled(true);
            addSwatch.setEnabled(true);
            selectPos.setEnabled(true);
            selectDest.setEnabled(false);

            setImage("torch");
            currentID = 1;
        } else if(current.equals("door")){
            xPos.setEnabled(true);
            yPos.setEnabled(true);
            xDest.setEnabled(true);
            yDest.setEnabled(true);

            swatches.setEnabled(false);
            addSwatch.setEnabled(false);
            selectPos.setEnabled(true);
            selectDest.setEnabled(true);

            setImage("door_open");
            currentID = 4;
        } else if(current.equals("lkdr")){
            xPos.setEnabled(true);
            yPos.setEnabled(true);
            xDest.setEnabled(true);
            yDest.setEnabled(true);

            swatches.setEnabled(true);
            addSwatch.setEnabled(true);
            selectPos.setEnabled(true);
            selectDest.setEnabled(true);

            setImage("door_locked");
            currentID = 5;
        }
        imgDisp.repaint();
    }

    private void createUIComponents() {
        contentPane = new JPanel();
        this.setContentPane(contentPane);

        model = new BlockModel();
        swatches = new JList<>();
        swatches.setModel(model);

        extras = new JList<>();
        extras.addListSelectionListener(this);
        xPos = new JSpinner();
        yPos = new JSpinner();
        xDest = new JSpinner();
        yDest = new JSpinner();

        imgDisp = new JPanel(){
            private static final long serialVersionUID = 1157509861047277132L;
            public void paint(Graphics g){
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getWidth(), getHeight());
                if(image != null && !image.equals("")){
                    g.drawImage(TileButton.EXTRAS.get(image), 0, 0, getWidth(), getHeight(), null);
                }
            }
        };

        selectPos  = new JButton("Select Position");
        selectDest = new JButton("Select Destination");
        addSwatch  = new JButton("Add Swatch");

        selectPos.addActionListener(this);
        selectDest.addActionListener(this);
        addSwatch.addActionListener(this);

        this.setMaximumSize(new Dimension(600, 300));
        this.setMinimumSize(new Dimension(600, 300));
        this.setSize(new Dimension(600, 300));
        this.pack();
    }

    public static void setValue(String command, int x, int y, Object args){
        if(command.equals("pos")) {
            instance.xPos.setValue(x);
            instance.yPos.setValue(y);
        } else if(command.equals("dest")) {
            instance.xDest.setValue(x);
            instance.yDest.setValue(y);
        } else if(command.equals("swatch")){
            if(args.equals("00")){
                Drawspace.SELECT = true;
                Drawspace.COMMAND = "swatch";
                JOptionPane.showMessageDialog(instance, "Must Select a Swatch");
                return;
            }
            String block = (String)args;
            ((BlockModel)instance.swatches.getModel()).addSwatch(new Swatch(block, x, y));
        }

        instance.setVisible(true);
        instance.setOpacity(1F);
    }

    private void setImage(String name){
        image = name;
        imgDisp.repaint();
    }

    private static Map<String, String> nameLookup = new HashMap<String, String>(); static{
        nameLookup.put("Window", "wind");
        nameLookup.put("Torch", "toch");
        nameLookup.put("Door", "door");
        nameLookup.put("Locked Door", "lkdr");
    }
    private static final class Swatch{
        public String block;
        public int x, y;
        public Swatch(String block, int x, int y){
            this.block = block;
            this.x = x;
            this.y = y;
        }

        public String toString(){
            return "[" + x + ", " + y + "] " + block;
        }
    }
    private static final class BlockModel extends AbstractListModel{
        public ArrayList<Swatch> swatches = new ArrayList<Swatch>();

        public void addSwatch(Swatch s){
            this.swatches.add(s);
            System.out.println("Size: " + swatches.size());
            update();
        }

        public Swatch getSwatchAt(int index){
            return swatches.get(index);
        }

        public int getSize() {
            return swatches.size();
        }

        public String getElementAt(int index) {
            System.out.println("Asked for: " + index);
            return swatches.get(index).toString();
        }

        public void update(){
            this.fireContentsChanged(this, 0, swatches.size() - 1);
        }
    }
}
