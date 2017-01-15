package com.xemplar.utils.pc.leveldesigner.dialogs;

import com.xemplar.utils.pc.leveldesigner.TileButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class AnimateBlock extends JDialog implements ActionListener{
    private DialogFinishedListener listener;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JCheckBox horizontalCheckBox;
    private JSpinner spinner1;
    private JSpinner spinner2;
    private JPanel panel1;

    private int x, y;
    private String id;

    public AnimateBlock(DialogFinishedListener listener, String id, int x, int y) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        this.listener = listener;
        this.id = id;
        this.x = x;
        this.y = y;
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
        });
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public void actionPerformed(ActionEvent e){

    }

    private void onOK() {
        Map<String, Object> params = new HashMap<>();
        params.put("dir", horizontalCheckBox.isSelected());
        params.put("dist", spinner1.getValue());
        params.put("sped", spinner2.getValue());
        params.put("id", id);
        params.put("x", x);
        params.put("y", y);
        listener.dialogFinished(params);
        dispose();
    }

    private void onCancel() {
        listener.dialogFinished(null);
        dispose();
    }

    private void createUIComponents() {
        spinner1 = new JSpinner();
        spinner2 = new JSpinner();
        spinner1.setModel(new SpinnerNumberModel(0D, -128D, 128D, 0.1D));
        spinner2.setModel(new SpinnerNumberModel(0D, -128D, 128D, 0.1D));
        horizontalCheckBox = new JCheckBox();
        panel1 = new JPanel(){
            private static final long serialVersionUID = 1157509861047277132L;
            public void paint(Graphics g){
                super.paint(g);
                g.drawImage(TileButton.IMAGES.get(id), 0, 0, getWidth(), getHeight(), null);
            }
        };

        contentPane = new JPanel();
        this.setContentPane(contentPane);

        this.setMaximumSize(new Dimension(400, 200));
        this.setMinimumSize(new Dimension(400, 200));
        this.setSize(new Dimension(400, 200));
        this.pack();
    }
}
