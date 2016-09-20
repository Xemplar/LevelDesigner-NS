package com.xemplar.utils.pc.leveldesigner.dialogs;

import com.xemplar.utils.pc.leveldesigner.Drawspace;
import com.xemplar.utils.pc.leveldesigner.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Created by LYHS-JavaAP on 9/19/16.
 */
public class JContextMenu extends JPopupMenu{
    protected ActionListener listener;
    protected JMenuItem title;

    public JContextMenu(ActionListener listener){
        this.listener = listener;
    }

    public void show(Component comp, int x, int y, int tx, int ty){
        if(setup(tx, ty)) {
            this.show(comp, x, y);
        }
    }

    public boolean setup(int tx, int ty){
        String id = Main.instance.field.getIdAt(tx, ty);
        if(id.equals("00")){
            return false;
        } else {
            if (id.startsWith("e") && !id.equals("ext")) {
                if(id.startsWith("e03")){
                    setupAnimated(tx, ty);
                } else {
                    setupEntity(tx, ty);
                }
            } else {
                setupBlock(tx, ty);
            }
        }

        return true;
    }

    private void setupAnimated(int tx, int ty){
        title = new JMenuItem("Animated Block");
        title.setEnabled(false);

        this.add(title);
        this.add(new JSeparator());

        JMenuItem u = new JMenuItem("Un-Animate");
        JMenuItem r = new JMenuItem("Replace");
        JMenuItem d = new JMenuItem("Delete");

        u.setActionCommand("uan:" + tx + ":" + ty);
        r.setActionCommand("rep:" + tx + ":" + ty);
        d.setActionCommand("del:" + tx + ":" + ty);

        u.addActionListener(this.listener);
        r.addActionListener(this.listener);
        d.addActionListener(this.listener);

        this.add(u);
        this.add(r);
        this.add(d);
    }

    private void setupEntity(int tx, int ty){
        title = new JMenuItem("Entity");
        title.setEnabled(false);

        this.add(title);
        this.add(new JSeparator());

        JMenuItem e = new JMenuItem("Edit");
        JMenuItem r = new JMenuItem("Replace");
        JMenuItem d = new JMenuItem("Delete");

        e.setActionCommand("edi:" + tx + ":" + ty);
        r.setActionCommand("rep:" + tx + ":" + ty);
        d.setActionCommand("del:" + tx + ":" + ty);

        e.addActionListener(this.listener);
        r.addActionListener(this.listener);
        d.addActionListener(this.listener);

        this.add(e);
        this.add(r);
        this.add(d);
    }

    private void setupBlock(int tx, int ty){
        title = new JMenuItem("Static Block");
        title.setEnabled(false);

        this.add(title);
        this.add(new JSeparator());

        JMenuItem a = new JMenuItem("Animate");
        JMenuItem r = new JMenuItem("Replace");
        JMenuItem d = new JMenuItem("Delete");

        a.setActionCommand("ani:" + tx + ":" + ty);
        r.setActionCommand("rep:" + tx + ":" + ty);
        d.setActionCommand("del:" + tx + ":" + ty);

        a.addActionListener(this.listener);
        r.addActionListener(this.listener);
        d.addActionListener(this.listener);

        this.add(a);
        this.add(r);
        this.add(d);
    }

    public void clean(){
        this.removeAll();
        title = null;
    }

    public void setVisible(boolean val) {
        super.setVisible(val);

        if(!val){
           clean();
        }
    }
}
