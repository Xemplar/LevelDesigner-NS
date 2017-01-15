package com.xemplar.utils.pc.leveldesigner.dialogs;

import oracle.jrockit.jfr.JFR;

import javax.swing.*;
import java.awt.*;

/**
 * Created by roxas on 1/2/17.
 */
public class JProgressDialog extends JFrame {
    private JProgressBar progress;
    private int max;

    public JProgressDialog(String title, String message, int amnt){
        progress = new JProgressBar();
        progress.setMaximum(amnt);
        progress.setMinimum(0);

        progress.setStringPainted(true);

        this.max = amnt;
        this.setTitle(title);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setLayout(new BorderLayout());

        JLabel lbl = new JLabel(message);
        this.add(lbl, BorderLayout.NORTH);
        this.add(progress, BorderLayout.SOUTH);

        this.pack();
        this.setLocationRelativeTo(null);

        this.setVisible(true);
    }

    public JProgressDialog(String message, int amnt){
        this("", message, amnt);
    }

    public void updateProgress(int amnt){
        float percent = ((float) amnt / this.max) * 100F;
        progress.setValue(amnt);
        progress.setString(percent + "%");
    }
}
