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

    public void show(Component comp, int x, int y, int tx, int ty, String[] extras){
        if(setup(tx, ty, extras)) {
            this.show(comp, x, y);
        }
    }

    public void show(Component comp, int x, int y, int tx, int ty){
        show(comp, x, y, tx, ty, null);
    }

    public boolean setup(int tx, int ty, String[] extras){
        String id = Main.instance.field.getIdAt(tx, ty);
        boolean ret = true;
        if(id.equals("00")){
            ret = false;
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
        if(extras != null && extras.length > 0){
            if(ret == false){
                title = new JMenuItem("Extra");
                title.setEnabled(false);

                this.add(title);
            }
            this.add(new JSeparator());
            for(int i = 0; i < extras.length; i++){
                String[] args = extras[i].substring(1).split("#");
                JMenu extra = new JMenu("Extra: " + args[0]);

                JMenuItem delete = new JMenuItem("Delete");
                delete.setActionCommand("context:" + args[1] + ":" + args[2] + ":delete");
                delete.addActionListener(Main.instance.field);

                extra.add(delete);
                this.add(extra);
            }
            ret |= true;
        }
        return ret;
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
