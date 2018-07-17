package assignment3;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.*;

/**
 * The GUI for assignment 3, Food Supply
 * 
 * @author Farid Naisan/Stig, edited and completed by Alexander Johansson (AF2015)
 */
public class GUIFoodSupply
{
	/**
	 * These are the components you need to handle.
	 * You have to add listeners and/or code
	 * Static controls are defined inline
	 */
	private JFrame frame;				// The Main window
	private JProgressBar bufferStatus;	// The progressbar, showing content in buffer
	
	// Data for Producer Scan
	private JButton btnStartS;			// Button start Scan
	private JButton btnStopS;			// Button stop Scan
	private JLabel lblStatusS;			// Status Scan
	// DAta for producer Arla
	private JButton btnStartA;			// Button start Arla
	private JButton btnStopA;			// Button stop Arla
	private JLabel lblStatusA;			// Status Arla
	//Data for producer AxFood
	private JButton btnStartX;			// Button start AxFood
	private JButton btnStopX;			// Button stop AxFood
	private JLabel lblStatusX;			// Status AxFood
	
	// Producers
	private Factory factoryS;			// Factory Scan
	private Factory factoryA;			// Factory Arla
	private Factory factoryX;			// Factory AxFood
	
	// Threads for producers
	private Thread factoryThreadS;		// Factory Thread Scan
	private Thread factoryThreadA;		// Factory Thread Arla
	private Thread factoryThreadX;		// Factory Thread AxFood
	
	// Consumers
	private Truck truckIca;				// Truck Ica
	private Truck truckCoop;			// Truck Coop
	private Truck truckCG;				// Truck CITY GROSS
	
	// Threads for consumers
	private Thread truckThreadIca;		// Truck Thread Ica
	private Thread truckThreadCoop;		// Truck Thread Coop
	private Thread truckThreadCG;		// Truck Thread CITY GROSS
	
	// Data for consumer ICA
	private JLabel lblIcaItems;			// Ica limits
	private JLabel lblIcaWeight;
	private JLabel lblIcaVolume;
	private JLabel lblIcaStatus;		// load status
	private JTextArea lstIca;			// The cargo list
	private JButton btnIcaStart;		// The buttons
	private JButton btnIcaStop;
	private JCheckBox chkIcaCont;		// Continue checkbox
	//Data for consumer COOP
	private JLabel lblCoopItems;
	private JLabel lblCoopWeight;
	private JLabel lblCoopVolume;
	private JLabel lblCoopStatus;		// load status
	private JTextArea lstCoop;			// The cargo list
	private JButton btnCoopStart;		// The buttons
	private JButton btnCoopStop;
	private JCheckBox chkCoopCont;		// Continue checkbox
	// Data for consumer CITY GROSS
	private JLabel lblCGItems;
	private JLabel lblCGWeight;
	private JLabel lblCGVolume;
	private JLabel lblCGStatus;			// load status
	private JTextArea lstCG;			// The cargo list
	private JButton btnCGStart;			// The buttons
	private JButton btnCGStop;
	private JCheckBox chkCGCont;		// Continue checkbox
	
	// FoodItem collection
	private FoodItem[] foodBuffer;
	private Random rand;
	
	// Storage
	private static int MAX_CAPACITY = 50;
	private Storage foodStorage;
	
	/**
	 * Constructor, creates the window
	 */
	public GUIFoodSupply()
	{
	}
	
	/**
	 * Starts the application
	 */
	public void Start()
	{
		frame = new JFrame();
		frame.setBounds(0, 0, 730, 526);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(null);
		frame.setTitle("Food Supply System");
		InitializeGUI();					// Fill in components
		frame.setVisible(true);
		frame.setResizable(false);			// Prevent user from change size
		frame.setLocationRelativeTo(null);	// Start middle screen
		
		setupThreadsAndStorage();
	}
	
	/**
	 * Inits storage, food buffer and starts threads
	 */
	public void setupThreadsAndStorage() {
		foodStorage = new Storage(MAX_CAPACITY, bufferStatus);
		rand = new Random();
		initFoodItems();
		
		factoryS = new Factory(foodStorage, foodBuffer, rand);
		factoryA = new Factory(foodStorage, foodBuffer, rand);
		factoryX = new Factory(foodStorage, foodBuffer, rand);
		
		factoryThreadS = new Thread(factoryS);
		factoryThreadA = new Thread(factoryA);
		factoryThreadX = new Thread(factoryX);
		
		factoryThreadS.start();
		factoryThreadA.start();
		factoryThreadX.start();
		
		truckIca = new Truck(foodStorage, lblIcaItems, lblIcaWeight, lblIcaVolume, lblIcaStatus, lstIca, chkIcaCont);
		truckCoop = new Truck(foodStorage, lblCoopItems, lblCoopWeight, lblCoopVolume, lblCoopStatus, lstCoop, chkCoopCont);
		truckCG = new Truck(foodStorage, lblCGItems, lblCGWeight, lblCGVolume, lblCGStatus, lstCG, chkCGCont);
		
		truckIca.setMaxLoad(50, 10.0, 100.0);
		truckCoop.setMaxLoad(50, 100.0, 10.0);
		truckCG.setMaxLoad(10, 100.0, 100.0);
		
		truckThreadIca = new Thread(truckIca);
		truckThreadCoop = new Thread(truckCoop);
		truckThreadCG = new Thread(truckCG);
		
		truckThreadIca.start();
		truckThreadCoop.start();
		truckThreadCG.start();
	}
	
	/**
	 * Sets up the GUI with components
	 */
	private void InitializeGUI()
	{
		// First create the three main panels
		JPanel pnlBuffer = new JPanel();
		pnlBuffer.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),"Storage"));
		pnlBuffer.setBounds(13, 403, 693, 82);
		pnlBuffer.setLayout(null);
		// Then create the progressbar, only component in buffer panel
		bufferStatus = new JProgressBar();
		bufferStatus.setBounds(155, 37, 500, 23);
		bufferStatus.setBorder(BorderFactory.createLineBorder(Color.black));
		bufferStatus.setForeground(Color.GREEN);
		bufferStatus.setMaximum(MAX_CAPACITY);
		pnlBuffer.add(bufferStatus);
		JLabel lblmax = new JLabel("Max capacity (" + MAX_CAPACITY + "):");
		lblmax.setBounds(10, 42, 126,13);
		pnlBuffer.add(lblmax);
		frame.add(pnlBuffer);
		
		JPanel pnlProd = new JPanel();
		pnlProd.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),"Producers"));
		pnlProd.setBounds(13, 13, 229, 379);
		pnlProd.setLayout(null);
		
		JPanel pnlCons = new JPanel();
		pnlCons.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),"Consumers"));
		pnlCons.setBounds(266, 13, 440, 379);
		pnlCons.setLayout(null);
		
		// Now add the three panels to producer panel
		JPanel pnlScan = new JPanel();
		pnlScan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),"Producer: Scan"));
		pnlScan.setBounds(6, 19, 217, 100);
		pnlScan.setLayout(null);
		
		// Content Scan panel
		btnStartS = new JButton("Start Producing");
		btnStartS.setBounds(10, 59, 125, 23);
		btnStartS.addActionListener(new ActionListener() { // Register action listener, event fired when pressed button
			@Override
			public void actionPerformed(ActionEvent e) {
				factoryS.toggleRunning();
				
				lblStatusS.setText("Status: Producing");
				
				btnStartS.setEnabled(false);
				btnStopS.setEnabled(true);
			}
		});
		pnlScan.add(btnStartS);
		btnStopS = new JButton("Stop");
		btnStopS.setBounds(140, 59, 65, 23);
		btnStopS.setEnabled(false);
		btnStopS.addActionListener(new ActionListener() { // Register action listener, event fired when pressed button
			@Override
			public void actionPerformed(ActionEvent e) {
				factoryS.toggleRunning();
				
				lblStatusS.setText("Status: Stopped");
				
				btnStartS.setEnabled(true);
				btnStopS.setEnabled(false);
			}
		});
		pnlScan.add(btnStopS);
		lblStatusS = new JLabel("Status: Stopped");
		lblStatusS.setBounds(10, 31, 200, 13);
		pnlScan.add(lblStatusS);
		// Add Scan panel to producers		
		pnlProd.add(pnlScan);
		
		// The Arla panel
		JPanel pnlArla = new JPanel();
		pnlArla.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),"Producer: Arla"));
		pnlArla.setBounds(6, 139, 217, 100);
		pnlArla.setLayout(null);
		
		// Content Arla panel
		btnStartA = new JButton("Start Producing");
		btnStartA.setBounds(10, 59, 125, 23);
		btnStartA.addActionListener(new ActionListener() { // Register action listener, event fired when pressed button
			@Override
			public void actionPerformed(ActionEvent e) {
				factoryA.toggleRunning();
				
				lblStatusA.setText("Status: Producing");
				
				btnStartA.setEnabled(false);
				btnStopA.setEnabled(true);
			}
		});
		pnlArla.add(btnStartA);
		btnStopA = new JButton("Stop");
		btnStopA.setBounds(140, 59, 65, 23);
		btnStopA.setEnabled(false);
		btnStopA.addActionListener(new ActionListener() { // Register action listener, event fired when pressed button
			@Override
			public void actionPerformed(ActionEvent e) {
				factoryA.toggleRunning();
				
				lblStatusA.setText("Status: Stopped");
				
				btnStartA.setEnabled(true);
				btnStopA.setEnabled(false);
			}
		});
		pnlArla.add(btnStopA);
		lblStatusA = new JLabel("Status: Stopped");
		lblStatusA.setBounds(10, 31, 200, 13);
		pnlArla.add(lblStatusA);
		// Add Arla panel to producers		
		pnlProd.add(pnlArla);
		
		// The AxFood Panel
		JPanel pnlAxfood = new JPanel();
		pnlAxfood.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),"Producer: AxFood"));
		pnlAxfood.setBounds(6, 262, 217, 100);
		pnlAxfood.setLayout(null);
		
		// Content AxFood Panel
		btnStartX = new JButton("Start Producing");
		btnStartX.setBounds(10, 59, 125, 23);
		btnStartX.addActionListener(new ActionListener() { // Register action listener, event fired when pressed button
			@Override
			public void actionPerformed(ActionEvent e) {
				factoryX.toggleRunning();
				
				lblStatusX.setText("Status: Producing");
				
				btnStartX.setEnabled(false);
				btnStopX.setEnabled(true);
			}
		});
		pnlAxfood.add(btnStartX);
		btnStopX = new JButton("Stop");
		btnStopX.setBounds(140, 59, 65, 23);
		btnStopX.setEnabled(false);
		btnStopX.addActionListener(new ActionListener() { // Register action listener, event fired when pressed button
			@Override
			public void actionPerformed(ActionEvent e) {
				factoryX.toggleRunning();
				
				lblStatusX.setText("Status: Stopped");
				
				btnStartX.setEnabled(true);
				btnStopX.setEnabled(false);
			}
		});
		pnlAxfood.add(btnStopX);
		lblStatusX = new JLabel("Status: Stopped");
		lblStatusX.setBounds(10, 31, 200, 13);
		pnlAxfood.add(lblStatusX);
		// Add Axfood panel to producers		
		pnlProd.add(pnlAxfood);
		// Producer panel done, add to frame		
		frame.add(pnlProd);
		
		// Next, add the three panels to Consumer panel
		JPanel pnlICA = new JPanel();
		pnlICA.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),"Consumer: ICA"));
		pnlICA.setBounds(19, 19,415, 100);
		pnlICA.setLayout(null);
		
		// Content ICA panel
		// First the limits panel
		JPanel pnlLim = new JPanel();
		pnlLim.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),"Limits"));
		pnlLim.setBounds(6, 19, 107, 75);
		pnlLim.setLayout(null);
		JLabel lblItems = new JLabel("Items:");
		lblItems.setBounds(7, 20, 50, 13);
		pnlLim.add(lblItems);
		JLabel lblWeight = new JLabel("Weight:");
		lblWeight.setBounds(7, 35, 50, 13);
		pnlLim.add(lblWeight);
		JLabel lblVolume = new JLabel("Volume:");
		lblVolume.setBounds(7, 50, 50, 13);
		pnlLim.add(lblVolume);
		lblIcaItems = new JLabel("0.0");
		lblIcaItems.setBounds(60, 20, 47, 13);
		pnlLim.add(lblIcaItems);
		lblIcaWeight = new JLabel("0.0");
		lblIcaWeight.setBounds(60, 35, 47, 13);
		pnlLim.add(lblIcaWeight);
		lblIcaVolume = new JLabel("0.0");
		lblIcaVolume.setBounds(60, 50, 47, 13);
		pnlLim.add(lblIcaVolume);
		pnlICA.add(pnlLim);
		// Then rest of controls
		lstIca = new JTextArea();
		lstIca.setEditable(false);
		JScrollPane spane = new JScrollPane(lstIca);		
		spane.setBounds(307, 16, 102, 69);
		spane.setBorder(BorderFactory.createLineBorder(Color.black));
		pnlICA.add(spane);
		btnIcaStart = new JButton("Start Loading");
		btnIcaStart.setBounds(118, 64, 120, 23);
		btnIcaStart.addActionListener(new ActionListener() { // Register action listener, event fired when pressed button
			@Override
			public void actionPerformed(ActionEvent e) {
				truckIca.toggleRunning();
				
				lblIcaStatus.setText("Status: Loading");
				
				btnIcaStart.setEnabled(false);
				btnIcaStop.setEnabled(true);
			}
		});
		pnlICA.add(btnIcaStart);
		btnIcaStop = new JButton("Stop");
		btnIcaStop.setBounds(240, 64, 60, 23);
		btnIcaStop.setEnabled(false);
		btnIcaStop.addActionListener(new ActionListener() { // Register action listener, event fired when pressed button
			@Override
			public void actionPerformed(ActionEvent e) {
				truckIca.toggleRunning();
				
				lblIcaStatus.setText("Status: Stopped");
				
				btnIcaStart.setEnabled(true);
				btnIcaStop.setEnabled(false);
			}
		});
		pnlICA.add(btnIcaStop);
		lblIcaStatus = new JLabel("Status: Stopped");
		lblIcaStatus.setBounds(118, 16, 150, 23);
		pnlICA.add(lblIcaStatus);
		chkIcaCont = new JCheckBox("Continue load");
		chkIcaCont.setBounds(118, 39, 130, 17);
		pnlICA.add(chkIcaCont);
		// All done, add to consumers panel
		pnlCons.add(pnlICA);
		
		JPanel pnlCOOP = new JPanel();
		pnlCOOP.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),"Consumer: COOP"));
		pnlCOOP.setBounds(19, 139, 415, 100);
		pnlCOOP.setLayout(null);
		pnlCons.add(pnlCOOP);
		
		// Content COOP panel
		// First the limits panel
		JPanel pnlLimC = new JPanel();
		pnlLimC.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),"Limits"));
		pnlLimC.setBounds(6, 19, 107, 75);
		pnlLimC.setLayout(null);
		JLabel lblItemsC = new JLabel("Items:");
		lblItemsC.setBounds(7, 20, 50, 13);
		pnlLimC.add(lblItemsC);
		JLabel lblWeightC = new JLabel("Weight:");
		lblWeightC.setBounds(7, 35, 50, 13);
		pnlLimC.add(lblWeightC);
		JLabel lblVolumeC = new JLabel("Volume:");
		lblVolumeC.setBounds(7, 50, 50, 13);
		pnlLimC.add(lblVolumeC);
		lblCoopItems = new JLabel("0.0");
		lblCoopItems.setBounds(60, 20, 47, 13);
		pnlLimC.add(lblCoopItems);
		lblCoopWeight = new JLabel("0.0");
		lblCoopWeight.setBounds(60, 35, 47, 13);
		pnlLimC.add(lblCoopWeight);
		lblCoopVolume = new JLabel("0.0");
		lblCoopVolume.setBounds(60, 50, 47, 13);
		pnlLimC.add(lblCoopVolume);
		pnlCOOP.add(pnlLimC);
		// Then rest of controls
		lstCoop = new JTextArea();
		lstCoop.setEditable(false);
		JScrollPane spaneC = new JScrollPane(lstCoop);		
		spaneC.setBounds(307, 16, 102, 69);
		spaneC.setBorder(BorderFactory.createLineBorder(Color.black));
		pnlCOOP.add(spaneC);
		btnCoopStart = new JButton("Start Loading");
		btnCoopStart.setBounds(118, 64, 120, 23);
		btnCoopStart.addActionListener(new ActionListener() { // Register action listener, event fired when pressed button
			@Override
			public void actionPerformed(ActionEvent e) {
				truckCoop.toggleRunning();
				
				lblCoopStatus.setText("Status: Loading");
				
				btnCoopStart.setEnabled(false);
				btnCoopStop.setEnabled(true);
			}
		});
		pnlCOOP.add(btnCoopStart);
		btnCoopStop = new JButton("Stop");
		btnCoopStop.setBounds(240, 64, 60, 23);
		btnCoopStop.setEnabled(false);
		btnCoopStop.addActionListener(new ActionListener() { // Register action listener, event fired when pressed button
			@Override
			public void actionPerformed(ActionEvent e) {
				truckCoop.toggleRunning();
				
				lblCoopStatus.setText("Status: Stopped");
				
				btnCoopStart.setEnabled(true);
				btnCoopStop.setEnabled(false);
			}
		});
		pnlCOOP.add(btnCoopStop);
		lblCoopStatus = new JLabel("Status: Stopped");
		lblCoopStatus.setBounds(118, 16, 150, 23);
		pnlCOOP.add(lblCoopStatus);
		chkCoopCont = new JCheckBox("Continue load");
		chkCoopCont.setBounds(118, 39, 130, 17);
		pnlCOOP.add(chkCoopCont);
		// All done, add to consumers panel
		pnlCons.add(pnlCOOP);
		
		JPanel pnlCG = new JPanel();
		pnlCG.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),"Consumer: CITY GROSS"));
		pnlCG.setBounds(19, 262, 415, 100);
		pnlCG.setLayout(null);
		pnlCons.add(pnlCG);
		
		// Content CITY GROSS panel
		// First the limits panel
		JPanel pnlLimG = new JPanel();
		pnlLimG.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black),"Limits"));
		pnlLimG.setBounds(6, 19, 107, 75);
		pnlLimG.setLayout(null);
		JLabel lblItemsG = new JLabel("Items:");
		lblItemsG.setBounds(7, 20, 50, 13);
		pnlLimG.add(lblItemsG);
		JLabel lblWeightG = new JLabel("Weight:");
		lblWeightG.setBounds(7, 35, 50, 13);
		pnlLimG.add(lblWeightG);
		JLabel lblVolumeG = new JLabel("Volume:");
		lblVolumeG.setBounds(7, 50, 50, 13);
		pnlLimG.add(lblVolumeG);
		lblCGItems = new JLabel("0.0");
		lblCGItems.setBounds(60, 20, 47, 13);
		pnlLimG.add(lblCGItems);
		lblCGWeight = new JLabel("0.0");
		lblCGWeight.setBounds(60, 35, 47, 13);
		pnlLimG.add(lblCGWeight);
		lblCGVolume = new JLabel("0.0");
		lblCGVolume.setBounds(60, 50, 47, 13);
		pnlLimG.add(lblCGVolume);
		pnlCG.add(pnlLimG);
		// Then rest of controls
		lstCG = new JTextArea();
		lstCG.setEditable(false);
		JScrollPane spaneG = new JScrollPane(lstCG);		
		spaneG.setBounds(307, 16, 102, 69);
		spaneG.setBorder(BorderFactory.createLineBorder(Color.black));
		pnlCG.add(spaneG);
		btnCGStart = new JButton("Start Loading");
		btnCGStart.setBounds(118, 64, 120, 23);
		btnCGStart.addActionListener(new ActionListener() { // Register action listener, event fired when pressed button
			@Override
			public void actionPerformed(ActionEvent e) {
				truckCG.toggleRunning();
				
				lblCGStatus.setText("Status: Loading");
				
				btnCGStart.setEnabled(false);
				btnCGStop.setEnabled(true);
			}
		});
		pnlCG.add(btnCGStart);
		btnCGStop = new JButton("Stop");
		btnCGStop.setBounds(240, 64, 60, 23);
		btnCGStop.setEnabled(false);
		btnCGStop.addActionListener(new ActionListener() { // Register action listener, event fired when pressed button
			@Override
			public void actionPerformed(ActionEvent e) {
				truckCG.toggleRunning();
				
				lblCGStatus.setText("Status: Stopped");
				
				btnCGStart.setEnabled(true);
				btnCGStop.setEnabled(false);
			}
		});
		pnlCG.add(btnCGStop);
		lblCGStatus = new JLabel("Status: Stopped");
		lblCGStatus.setBounds(118, 16, 150, 23);
		pnlCG.add(lblCGStatus);
		chkCGCont = new JCheckBox("Continue load");
		chkCGCont.setBounds(118, 39, 130, 17);
		pnlCG.add(chkCGCont);
		// All done, add to consumers panel
		pnlCons.add(pnlCOOP);
		
		// Add consumer panel to frame
		frame.add(pnlCons);
	}
	
	private void initFoodItems() {
		foodBuffer = new FoodItem[20];
		foodBuffer[0] = new FoodItem(1.1, 0.5, "Milk");
		foodBuffer[1] = new FoodItem(0.6, 0.1, "Cream");
		foodBuffer[2] = new FoodItem(1.1, 0.5, "Yogurt");
		foodBuffer[3] = new FoodItem(2.34, 0.66, "Butter");
		foodBuffer[4] = new FoodItem(2.4, 1.2, "Flour");
		foodBuffer[5] = new FoodItem(2.7, 1.8, "Sugar");
		foodBuffer[6] = new FoodItem(1.55, 0.27, "Salt");
		foodBuffer[7] = new FoodItem(0.6, 0.19, "Almonds");
		foodBuffer[8] = new FoodItem(1.98, 0.75, "Bread");
		foodBuffer[9] = new FoodItem(1.4, 0.5, "Donuts");
		foodBuffer[10] = new FoodItem(1.3, 1.5, "Jam");
		foodBuffer[11] = new FoodItem(4.1, 2.5, "Ham");
		foodBuffer[12] = new FoodItem(6.8, 3.9, "Chicken");
		foodBuffer[13] = new FoodItem(0.87, 0.55, "Salat");
		foodBuffer[14] = new FoodItem(2.46, 0.29, "Orange");
		foodBuffer[15] = new FoodItem(2.44, 0.4, "Apple");
		foodBuffer[16] = new FoodItem(1.2, 0.77, "Pear");
		foodBuffer[17] = new FoodItem(2.98, 2.0, "Soda");
		foodBuffer[18] = new FoodItem(3.74, 1.5, "Beer");
		foodBuffer[19] = new FoodItem(2.0, 1.35, "Hotdogs");
	}
}
