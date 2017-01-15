package com.xemplar.utils.pc.leveldesigner.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.xemplar.utils.pc.leveldesigner.EntityCreator;
import com.xemplar.utils.pc.leveldesigner.EntityCreatorModel;

public class InsertEntityDialog extends JDialog implements ActionListener, ListSelectionListener{
	public static final ArrayList<EntityCreator> creators = new ArrayList<EntityCreator>();
	private static final long serialVersionUID = -5318864827049366051L;
	private final JPanel contentPanel = new JPanel();
	
	private ArrayList<JTextField> args = new ArrayList<JTextField>();
	private DialogFinishedListener listener;
	private EntityCreator current;
	private JPanel imagePanel;

	/**
	 * Create the dialog.
	 */
	public InsertEntityDialog() {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		EntityCreatorModel<EntityCreator> model = new EntityCreatorModel<EntityCreator>();
		for(int i = 0; i < creators.size(); i++){
			model.addItem(creators.get(i));
		}
		
		JList<EntityCreator> list = new JList<EntityCreator>();
		list.setModel(model);
		list.addListSelectionListener(this);
		list.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		list.setBounds(12, 12, 187, 199);
		contentPanel.add(list);
		
		JLabel lblArguments = new JLabel("Arguments");
		lblArguments.setHorizontalAlignment(SwingConstants.CENTER);
		lblArguments.setBounds(287, 13, 151, 15);
		contentPanel.add(lblArguments);
		
		imagePanel = new JPanel(){
			private static final long serialVersionUID = 1157509861047277132L;
			public void paint(Graphics g){
				super.paint(g);
				if(current != null){
					g.drawImage(current.getImage(), 0, 0, getWidth(), getHeight(), null);
				}
			}
		};
		imagePanel.setBackground(Color.WHITE);
		imagePanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		imagePanel.setBounds(211, 147, 64, 64);
		contentPanel.add(imagePanel);
		
		JLabel lblImage = new JLabel("Image");
		lblImage.setHorizontalAlignment(SwingConstants.CENTER);
		lblImage.setBounds(211, 120, 64, 15);
		contentPanel.add(lblImage);
		
		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel.setBackground(Color.WHITE);
		panel.setBounds(287, 40, 151, 171);
		contentPanel.add(panel);
		SpringLayout sl_panel = new SpringLayout();
		panel.setLayout(sl_panel);
		
		JLabel lblQty = new JLabel("Qty: ");
		sl_panel.putConstraint(SpringLayout.NORTH, lblQty, 5, SpringLayout.NORTH, panel);
		sl_panel.putConstraint(SpringLayout.WEST, lblQty, 5, SpringLayout.WEST, panel);
		panel.add(lblQty);
		
		JTextField qtyField = new JTextField();
		qtyField.setName("Qty");
		args.add(qtyField);
		
		sl_panel.putConstraint(SpringLayout.NORTH, qtyField, 0, SpringLayout.NORTH, lblQty);
		sl_panel.putConstraint(SpringLayout.EAST, qtyField, 0, SpringLayout.EAST, panel);
		panel.add(qtyField);
		qtyField.setColumns(10);
		
		JLabel lblHealth = new JLabel("Health:");
		sl_panel.putConstraint(SpringLayout.NORTH, lblHealth, 6, SpringLayout.SOUTH, lblQty);
		sl_panel.putConstraint(SpringLayout.WEST, lblHealth, 0, SpringLayout.WEST, lblQty);
		panel.add(lblHealth);
		
		JTextField healthField = new JTextField();
		healthField.setName("Health");
		args.add(healthField);
		
		sl_panel.putConstraint(SpringLayout.WEST, qtyField, 0, SpringLayout.WEST, healthField);
		sl_panel.putConstraint(SpringLayout.NORTH, healthField, 0, SpringLayout.NORTH, lblHealth);
		sl_panel.putConstraint(SpringLayout.WEST, healthField, 5, SpringLayout.EAST, lblHealth);
		sl_panel.putConstraint(SpringLayout.EAST, healthField, 0, SpringLayout.EAST, panel);
		panel.add(healthField);
		healthField.setColumns(10);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				okButton.addActionListener(this);
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(this);
				buttonPane.add(cancelButton);
			}
		}
		
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
			if(current != null){
				for(String arg : current.getArguments()){
					for(int i = 0; i < args.size(); i++){
						if(args.get(i).getName().equals(arg)){
							current.putForArg(arg, args.get(i).getText());
						}
					}
				}
				
				if(current.isFilledOut()) {
					listener.dialogFinished(current.getLevelID());
				} else {
					JOptionPane.showMessageDialog(this, "Must Fill Out All Arguments", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
			} else {
				JOptionPane.showMessageDialog(this, "No Entity Selected", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
		} else {
			listener.dialogFinished("cancled");
		}
		
		this.setVisible(false);
		this.dispose();
	}

	@SuppressWarnings("unchecked")
	public void valueChanged(ListSelectionEvent e) {
		current = ((JList<EntityCreator>)e.getSource()).getSelectedValue().clone();
		for(JTextField field : args){
			field.setEnabled(false);
		}
		for(int i = 0; i < current.getArguments().length; i++){
			for(int b = 0; b < args.size(); b++){
				if(current.getArguments()[i].equals(args.get(b).getName())){
					args.get(b).setEnabled(true);
					break;
				}
			}
		}
		imagePanel.repaint();
	}
}
