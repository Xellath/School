package assignment1;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.*;

/**
 * The GUI for assignment 1, DualThreads
 * 
 * @author Farid Naisan/Stig, edited and completed by Alexander Johansson (AF2015)
 */
public class GUIDualThreads
{
	/*
	 * These are the components you need to handle.
	 * You have to add listeners and/or code
	 */
	private JFrame frame;		// The Main window
	private JButton btnDisplay;	// Start thread moving display
	private JButton btnDStop;	// Stop moving display thread
	private JButton btnImage;// Start moving graphics thread
	private JButton btnIStop;	// Stop moving graphics thread
	private JPanel pnlMove;		// The panel to move display in
	private JPanel pnlBounce;	// The panel to move graphics in
	
	/* 
	 * Threads
	 */
	private TextMover textMover;
	private ImageBouncer imageBouncer;
	
	private Thread textThread;
	private Thread imageThread;

	/**
	 * Constructor
	 */
	public GUIDualThreads()
	{
	}
	
	/**
	 * Starts the application
	 */
	public void Start()
	{
		frame = new JFrame();
		frame.setBounds(0, 0, 494, 332);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null);
		frame.setTitle("Multiple Thread Demonstrator");
		InitializeGUI();					// Fill in components
		frame.setVisible(true);
		frame.setResizable(false);			// Prevent user from change size
		frame.setLocationRelativeTo(null);	// Start middle screen
		
		// Start text and image threads, but don't show contents (content is toggled to run by using UI)
		textMover = new TextMover(pnlMove);
		textThread = new Thread(textMover);
		textThread.start();
		
		imageBouncer = new ImageBouncer(pnlBounce);
		imageThread = new Thread(imageBouncer);
		imageThread.start();
	}
	
	/**
	 * Sets up the GUI with components
	 */
	private void InitializeGUI()
	{
		// The moving display outer panel
		JPanel pnlDisplay = new JPanel();
		Border b2 = BorderFactory.createTitledBorder("Display Thread");
		pnlDisplay.setBorder(b2);
		pnlDisplay.setBounds(12, 12, 222, 269);
		pnlDisplay.setLayout(null);
		
		// Add buttons and drawing panel to this panel
		btnDisplay = new JButton("Start Display");
		btnDisplay.setBounds(10, 226, 121, 23);
		btnDisplay.addActionListener(new ActionListener() { // Register action listener, event fired when pressed button
			@Override
			public void actionPerformed(ActionEvent e) {
				// Toggle thread execution
				textMover.toggleRunning();
				
				btnDisplay.setEnabled(false);
				btnDStop.setEnabled(true);
			}
		});
		pnlDisplay.add(btnDisplay);
		
		btnDStop = new JButton("Stop");
		btnDStop.setBounds(135, 226, 75, 23);
		btnDStop.setEnabled(false);
		btnDStop.addActionListener(new ActionListener() { // Register action listener, event fired when pressed button
			@Override
			public void actionPerformed(ActionEvent e) {
				// Toggle thread execution
				textMover.toggleRunning();
				
				btnDisplay.setEnabled(true);
				btnDStop.setEnabled(false);
			}
		});
		pnlDisplay.add(btnDStop);
		
		pnlMove = new JPanel();
		pnlMove.setBounds(10,  19,  200,  200);
		Border b21 = BorderFactory.createLineBorder(Color.black);
		pnlMove.setBorder(b21);
		pnlDisplay.add(pnlMove);
		// Then add this to main window
		frame.add(pnlDisplay);
		
		// The moving graphics outer panel
		JPanel pnlImage = new JPanel();
		Border b3 = BorderFactory.createTitledBorder("Image Bouncer Thread");
		pnlImage.setBorder(b3);
		pnlImage.setBounds(240, 12, 222, 269);
		pnlImage.setLayout(null);
		
		// Add buttons and drawing panel to this panel
		btnImage = new JButton("Start Bouncing");
		btnImage.setBounds(10, 226, 121, 23);
		btnImage.addActionListener(new ActionListener() { // Register action listener, event fired when pressed button
			@Override
			public void actionPerformed(ActionEvent e) {
				// Toggle thread execution
				imageBouncer.toggleRunning();
				
				btnImage.setEnabled(false);
				btnIStop.setEnabled(true);
			}
		});
		pnlImage.add(btnImage);
		
		btnIStop = new JButton("Stop");
		btnIStop.setBounds(135, 226, 75, 23);
		btnIStop.setEnabled(false);
		btnIStop.addActionListener(new ActionListener() { // Register action listener, event fired when pressed button
			@Override
			public void actionPerformed(ActionEvent e) {
				// Toggle thread execution
				imageBouncer.toggleRunning();
				
				btnImage.setEnabled(true);
				btnIStop.setEnabled(false);
			}
		});
		pnlImage.add(btnIStop);
		
		pnlBounce = new JPanel();
		pnlBounce.setBounds(10,  19,  200,  200);
		Border b31 = BorderFactory.createLineBorder(Color.black);
		pnlBounce.setBorder(b31);
		pnlImage.add(pnlBounce);
		// Add this to main window
		frame.add(pnlImage);
	}
}
