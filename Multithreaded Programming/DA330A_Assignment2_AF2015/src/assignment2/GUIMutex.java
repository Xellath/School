package assignment2;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Random;

import javax.swing.*;

/**
 * The GUI for assignment 2
 * 
 * @author Farid Naisan/Stig, edited and completed by Alexander Johansson (AF2015)
 */
public class GUIMutex 
{
	/**
	 * These are the components you need to handle.
	 * You have to add listeners and/or code
	 */
	private JFrame frame;			// The Main window
	private JLabel lblTrans;		// The transmitted text
	private JLabel lblRec;			// The received text
	private JRadioButton bSync;		// The sync radiobutton
	private JRadioButton bAsync;	// The async radiobutton
	private JTextField txtTrans;	// The input field for string to transfer
	private JButton btnRun;         // The run button
	private JButton btnClear;		// The clear button
	private JPanel pnlRes;			// The colored result area
	private JLabel lblStatus;		// The status of the transmission
	private JTextArea listW;		// The write logger pane
	private JTextArea listR;		// The read logger pane
	
	/*
	 * Writer & Reader, Buffer
	 */
	private Writer writer;
	private Reader reader;
	private CharacterBuffer characterBuffer;
	
	private Thread writerThread;
	private Thread readerThread;
	
	/**
	 * Constructor
	 */
	public GUIMutex()
	{
	}
	
	/**
	 * Starts the application
	 */
	public void Start()
	{
		frame = new JFrame();
		frame.setBounds(0, 0, 601, 482);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null);
		frame.setTitle("Concurrent Read/Write");
		InitializeGUI();					// Fill in components
		frame.setVisible(true);
		frame.setResizable(false);			// Prevent user from change size
		frame.setLocationRelativeTo(null);	// Start middle screen
	}
	
	/**
	 * Sets up the GUI with components
	 */
	private void InitializeGUI()
	{
		// First, create the static components
		// First the 4 static texts
		JLabel lab1 = new JLabel("Writer Thread Logger");
		lab1.setBounds(18, 29, 128, 13);
		frame.add(lab1);
		JLabel lab2 = new JLabel("Reader Thread Logger");
		lab2.setBounds(388, 29, 128, 13);
		frame.add(lab2);
		JLabel lab3 = new JLabel("Transmitted:");
		lab3.setBounds(13, 394, 100, 13);
		frame.add(lab3);
		JLabel lab4 = new JLabel("Received:");
		lab4.setBounds(383, 394, 100, 13);
		frame.add(lab4);
		// Then add the two lists (of string) for logging transfer
		listW = new JTextArea();
		listW.setBounds(13, 45, 197, 342);
		listW.setBorder(BorderFactory.createLineBorder(Color.black));
		listW.setEditable(false);
		frame.add(listW);
		listR = new JTextArea();
		listR.setBounds(386, 45, 183, 342);
		listR.setBorder(BorderFactory.createLineBorder(Color.black));
		listR.setEditable(false);
		frame.add(listR);
		// Next the panel that holds the "running" part
		JPanel pnlTest = new JPanel();
		pnlTest.setBorder(BorderFactory.createTitledBorder("Concurrent Tester"));
		pnlTest.setBounds(220, 45, 155, 342);
		pnlTest.setLayout(null);
		frame.add(pnlTest);
		lblTrans = new JLabel("");	// Replace with sent string
		lblTrans.setBounds(13, 415, 200, 13);
		frame.add(lblTrans);
		lblRec = new JLabel("");		// Replace with received string
		lblRec.setBounds(383, 415, 200, 13);
		lblRec.addPropertyChangeListener(new PropertyChangeListener() { // Add property change listener, event fires when text is changed in element
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				// Sleep to avoid not having incorrect results (due to the other text not being changed yet, as the timings of Writer/Reader threads are random)
				// Only applicable when in asynchronous mode, synchronous mode should wait for reader at all times, regardless of random delays
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				// Announce
				announceResult();
			}
		});
		frame.add(lblRec);
		
		// These are the controls on the user panel, first the radiobuttons
		bSync = new JRadioButton("Syncronous Mode", false);
		bSync.setBounds(8, 37, 131, 17);
		pnlTest.add(bSync);
		bAsync = new JRadioButton("Asyncronous Mode", true);
		bAsync.setBounds(8, 61, 141, 17);
		pnlTest.add(bAsync);
		ButtonGroup grp = new ButtonGroup();
		grp.add(bSync);
		grp.add(bAsync);
		// then the label and textbox to input string to transfer
		JLabel lab5 = new JLabel("String to Transfer:");
		lab5.setBounds(6, 99, 141, 13);
		pnlTest.add(lab5);
		txtTrans = new JTextField();
		txtTrans.setBounds(6, 124, 123, 20);
		pnlTest.add(txtTrans);
		// The run button
		btnRun = new JButton("Run");
		btnRun.setBounds(26, 150, 75, 23);
		btnRun.addActionListener(new ActionListener() { // Register action listener, event fired when pressed button
			@Override
			public void actionPerformed(ActionEvent e) {
				// See if you should synchronize or not
				boolean synchronize = getSynchronized();
				// Start writer/reader
				startWriterReader(synchronize);
				
				lblStatus.setText("WR - RD");
				
				btnRun.setEnabled(false);
				btnClear.setEnabled(true);
			}
		});
		pnlTest.add(btnRun);
		JLabel lab6 = new JLabel("Running status:");
		lab6.setBounds(23, 199, 110, 13);
		pnlTest.add(lab6);
		// The colored rectangle holding result status
		pnlRes = new JPanel();
		pnlRes.setBorder(BorderFactory.createLineBorder(Color.black));
		pnlRes.setBounds(26, 225, 75, 47);
		pnlRes.setBackground(Color.ORANGE);
		pnlTest.add(pnlRes);
		// also to this text
		lblStatus = new JLabel("READY");
		lblStatus.setBounds(23, 275, 100, 13);
		pnlTest.add(lblStatus);
		// The clear input button, starts disabled
		btnClear = new JButton("Clear");
		btnClear.setBounds(26, 303, 75, 23);
		btnClear.setEnabled(false);
		btnClear.addActionListener(new ActionListener() { // Register action listener, event fired when pressed button
			@Override
			public void actionPerformed(ActionEvent e) {
				// Reset everything
				listW.setText("");
				listR.setText("");
				
				txtTrans.setText("");
				
				lblTrans.setText("");
				lblRec.setText("");
				
				pnlRes.setBackground(Color.ORANGE);
				
				lblStatus.setText("READY");
				
				btnRun.setEnabled(true);
				btnClear.setEnabled(false);
			}
		});
		pnlTest.add(btnClear);
	}
	
	/**
	 * Method checks whether radio buttons are selected or not, Sync or Async mode
	 * @return boolean true if sync is selected, false if not (async)
	 */
	private boolean getSynchronized() {
		if(bSync.isSelected()) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Defines a buffer from input string, initializes writer & reader, starts two threads that - one that writes, one that reads
	 * @param synchronize boolean
	 */
	private void startWriterReader(boolean synchronize) {
		// Init buffer from input
		String textToRun = txtTrans.getText().toString();
		characterBuffer = new CharacterBuffer(listW, listR);
		
		Random rand = new Random();
		// Init Writer/Reader
		writer = new Writer(characterBuffer, textToRun, synchronize, lblTrans, rand);
		reader = new Reader(characterBuffer, textToRun, synchronize, lblRec, rand);
		
		// Start threads
		writerThread = new Thread(writer);
		readerThread = new Thread(reader);
		writerThread.start();
		readerThread.start();
	}
	
	/**
	 * Announces result upon changing text in lblRec (see addPropertyChangedListener)
	 */
	private void announceResult() {
		// If labels are empty
		if(lblTrans.getText().isEmpty() && lblRec.getText().isEmpty()) {
			pnlRes.setBackground(Color.ORANGE);
			lblStatus.setText("READY");
		} else if(lblTrans.getText().equals(lblRec.getText())) { // If strings match
			pnlRes.setBackground(Color.GREEN);
			lblStatus.setText("MATCH");
		} else { // No match
			pnlRes.setBackground(Color.RED);
			lblStatus.setText("NO MATCH");
		}
	}
}
