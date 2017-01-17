package com.xemplar.utils.pc.leveldesigner.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;

public class NewFileDialog extends JDialog implements ActionListener{
	private static final long serialVersionUID = -6334431453473072467L;
	private final JPanel contentPanel = new JPanel();
	private DialogFinishedListener listener;
	private JSpinner width, height;
	/**
	 * Create the dialog.
	 */
	public NewFileDialog() {
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		setBounds(100, 100, 350, 200);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		SpringLayout sl_contentPanel = new SpringLayout();
		contentPanel.setLayout(sl_contentPanel);
		
		JLabel lbl0 = new JLabel("Enter Info for the New Level Below");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lbl0, 5, SpringLayout.NORTH, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lbl0, 5, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.EAST, lbl0, 5, SpringLayout.EAST, contentPanel);
		contentPanel.add(lbl0);
		
		JLabel lbl1 = new JLabel("Level Width:");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lbl1, 5, SpringLayout.SOUTH, lbl0);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lbl1, 100, SpringLayout.WEST, contentPanel);
		contentPanel.add(lbl1);
		
		width = new JSpinner();
		width.setModel(new SpinnerNumberModel(12, 12, 1024, 1));
		sl_contentPanel.putConstraint(SpringLayout.WEST, width, 10, SpringLayout.EAST, lbl1);
		sl_contentPanel.putConstraint(SpringLayout.EAST, width, 300, SpringLayout.WEST, contentPanel);
		sl_contentPanel.putConstraint(SpringLayout.SOUTH, lbl1, 0, SpringLayout.SOUTH, width);
		sl_contentPanel.putConstraint(SpringLayout.NORTH, width, 6, SpringLayout.SOUTH, lbl0);
		contentPanel.add(width);
		
		height = new JSpinner();
		height.setModel(new SpinnerNumberModel(7, 7, 1024, 1));
		sl_contentPanel.putConstraint(SpringLayout.NORTH, height, 5, SpringLayout.SOUTH, width);
		sl_contentPanel.putConstraint(SpringLayout.WEST, height, 0, SpringLayout.WEST, width);
		sl_contentPanel.putConstraint(SpringLayout.EAST, height, 0, SpringLayout.EAST, width);
		contentPanel.add(height);
		
		JLabel lbl2 = new JLabel("Level Height:");
		sl_contentPanel.putConstraint(SpringLayout.NORTH, lbl2, 0, SpringLayout.NORTH, height);
		sl_contentPanel.putConstraint(SpringLayout.WEST, lbl2, 0, SpringLayout.WEST, lbl1);
		contentPanel.add(lbl2);
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		JButton okButton = new JButton("OK");
		okButton.setActionCommand("OK");
		okButton.addActionListener(this);
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
				
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(this);
		buttonPane.add(cancelButton);

		setVisible(true);
		toFront();
		requestFocus();
	}

	public void setDialogFinishListener(DialogFinishedListener listener){
		this.listener = listener;
	}
	
	public void actionPerformed(ActionEvent e) {
		String name = e.getActionCommand();
		if(name.equals("OK")){
			Object[] ret = {this.width.getValue(), this.height.getValue()};
			listener.dialogFinished(ret);
		} else {
			listener.dialogFinished("cancled");
		}
		
		this.setVisible(false);
		this.dispose();
	}
}
