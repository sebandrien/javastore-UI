package p1;

import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.DateFormat;
import java.util.Date;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class Store extends JFrame {

  private static int WIDTH = 700;
  private static int HEIGHT = 300;
  
  int index = 0;
  int counter = 1;
  int quantityCount = 0;
  
  double subtotal = 0;
  
  static String[] itemIDArray = new String[10];
  static String[] itemTitleArray = new String[10];
  static String[] itemInStockArray = new String[10];
  static String[] itemPriceArray = new String[10];
  static String[] itemQuantityArray = new String[10];
  static String[] itemDiscountArray = new String[10];
  static String[] itemSubtotalArray = new String[10];

  private JLabel idLabel, qtyLabel, itemLabel, totalLabel;
  private JTextField idTextField, qtyTextField, itemTextField, totalTextField;
  private JButton processB, confirmB, viewB, finishB, newB, exitB;

  private ProcessButtonHandler procbHandler;
  private ConfirmButtonHandler confbHandler;
  private ViewButtonHandler viewbHandler;
  private FinishButtonHandler finbHandler;
  private NewButtonHandler newbHandler;
  private ExitButtonHandler exitbHandler;
  
  static NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
  static NumberFormat percentFormatter = NumberFormat.getPercentInstance();
  
  static DecimalFormat decimalFormatter = (DecimalFormat) percentFormatter;

  static String itemID = "", itemTitle = "", outputStr = "", maxArraySizeStr = "",
    itemPriceStr = "", itemInStock = "", itemQuantityStr = "", itemSubtotalStr = "",
    taxRateStr, discountRateStr, orderSubtotalStr;

  static double itemPrice = 0, itemSubtotal = 0, orderSubtotal = 0, orderTotal = 0, itemDiscount = 0, orderTaxAmount = 0;
  static int itemQuantity = 0, itemCount = 0, maxArraySize = 0;
  final static double taxRate = 0.060, disc5 = .10, disc10 = .15, disc15 = .20;

  public Store() {

    setTitle("Store");
    setSize(WIDTH, HEIGHT);

    idLabel = new JLabel("Enter the Item ID for Item #" + (itemCount + 1) + ":", SwingConstants.RIGHT);
    qtyLabel = new JLabel("Enter the quantity for Item #" + (itemCount + 1) + ":", SwingConstants.RIGHT);
    itemLabel = new JLabel("Details of Item #" + (itemCount + 1) + ":", SwingConstants.RIGHT);
    totalLabel = new JLabel("Order subtotal for " + itemCount + " item(s):", SwingConstants.RIGHT);

    idTextField = new JTextField();
    qtyTextField = new JTextField();
    itemTextField = new JTextField();
    totalTextField = new JTextField();

    itemTextField.setEditable(false);
    totalTextField.setEditable(false);

    processB = new JButton("Find Item #" + (itemCount + 1));
    procbHandler = new ProcessButtonHandler();
    processB.addActionListener(procbHandler);

    confirmB = new JButton("Purchase Item #" + (itemCount + 1));
    confbHandler = new ConfirmButtonHandler();
    confirmB.addActionListener(confbHandler);

    viewB = new JButton("View Cart");
    viewbHandler = new ViewButtonHandler();
    viewB.addActionListener(viewbHandler);

    finishB = new JButton("Complete Order - Check Out");
    finbHandler = new FinishButtonHandler();
    finishB.addActionListener(finbHandler);

    newB = new JButton("Start A New Order");
    newbHandler = new NewButtonHandler();
    newB.addActionListener(newbHandler);

    exitB = new JButton("Exit (Close App)");
    exitbHandler = new ExitButtonHandler();
    exitB.addActionListener(exitbHandler);

    Container pane = getContentPane();
    
    GridLayout grid6by2 = new GridLayout(6, 2, 8, 4);
    GridLayout grid5by2 = new GridLayout(5, 2, 8, 4);
    GridLayout grid4by2 = new GridLayout(4, 2, 8, 3);

    JPanel northPanel = new JPanel();
    JPanel southPanel = new JPanel();
    JPanel centerPanel = new JPanel();

    northPanel.setLayout(grid6by2);
    centerPanel.setLayout(grid5by2);
    southPanel.setLayout(grid4by2);

    idLabel.setForeground(Color.BLACK);
    northPanel.add(idLabel);
    northPanel.add(idTextField);
    qtyLabel.setForeground(Color.BLACK);
    northPanel.add(qtyLabel);
    northPanel.add(qtyTextField);
    itemLabel.setForeground(Color.BLACK);
    northPanel.add(itemLabel);
    northPanel.add(itemTextField);
    totalLabel.setForeground(Color.BLACK);
    northPanel.add(totalLabel);
    northPanel.add(totalTextField);

    centerPanel.setBackground(Color.LIGHT_GRAY);

    southPanel.add(processB);
    southPanel.add(confirmB);
    southPanel.add(viewB);
    southPanel.add(finishB);
    southPanel.add(newB);
    southPanel.add(exitB);

    pane.add(northPanel, BorderLayout.NORTH);
    pane.add(centerPanel, BorderLayout.CENTER);
    pane.add(southPanel, BorderLayout.SOUTH);

    centerFrame(WIDTH, HEIGHT);
    pane.setBackground(Color.WHITE);
    northPanel.setBackground(Color.WHITE);
    southPanel.setBackground(Color.LIGHT_GRAY);

    confirmB.setEnabled(false);
    finishB.setEnabled(false);
    viewB.setEnabled(false);

  }

  public void centerFrame(int frameWidth, int frameHeight) {

    Toolkit aToolkit = Toolkit.getDefaultToolkit();
    Dimension screen = aToolkit.getScreenSize();

    int xPostionOfFrame = (screen.width - frameWidth) / 2;
    int yPostionOfFrame = (screen.height - frameHeight) / 2;

    setBounds(xPostionOfFrame, yPostionOfFrame, frameWidth, frameHeight);

  }

  private class ProcessButtonHandler implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
	    	
	        String inventoryLine;
	        String itemIDFromFile;
	        String itemID = idTextField.getText();
	        String quantity = qtyTextField.getText();

	        boolean found = false;
	        
            File inputFile = new File("inventory.txt");
	        
	        FileReader inputFileReader = null;
	        BufferedReader inputBuffReader = null;
	        
	        Scanner aScanner = null;
	        
	        
	        if (idTextField.getText().isEmpty()) {
            	
                JOptionPane.showMessageDialog(null, "Please enter an Item ID.", "Message", JOptionPane.INFORMATION_MESSAGE);
                
                return;
            }
	       
	        try {
	        	
	            inputFileReader = new FileReader(inputFile);
	            inputBuffReader = new BufferedReader(inputFileReader);
	            inventoryLine = inputBuffReader.readLine();

	            whileloop: while (inventoryLine != null) {
	            	
	                aScanner = new Scanner(inventoryLine).useDelimiter("\\s*,\\s*");
	                
	                itemIDFromFile = aScanner.next();

	                String name = aScanner.next();
	                String stock = aScanner.next();
	                String price = aScanner.next();
	                
	                if (itemIDFromFile.equals(itemID)) {
	                	
	                    if (stock.equals("false")) {
	                    	
	                        idTextField.setText("");
	                        qtyTextField.setText("");
	                        
	                        JOptionPane.showMessageDialog(null, "Item not in stock!", "Message", JOptionPane.INFORMATION_MESSAGE);
	                        
	                        found = true;
	                        
	                        break whileloop;
	                        
	                    }

	                    if (quantity.equals("")) {
	                    	
	                        JOptionPane.showMessageDialog(null, "Please enter a quantity.", "Error", JOptionPane.ERROR_MESSAGE);
	                        break whileloop;
	                        
	                    }

	                    itemIDArray[index] = itemID;
	                    itemTitleArray[index] = name;
	                    itemInStockArray[index] = stock;
	                    itemPriceArray[index] = price;
	                    itemQuantityArray[index] = quantity;
	                    
	                    double x = Double.parseDouble(price);
	                    double i = Double.parseDouble(quantity);
	                    
	                    quantityCount = (int) (quantityCount + i);

	                    String discount = null;

	                    if (i < 5) {  //Discount is dependent on item quantity
	                        discount = "0%";
	                    } else if (i >= 5 && i < 10) {
	                        discount = "10%";
	                    } else if (i >= 10 && i < 15) {
	                        discount = "15%";
	                    } else if (i >= 15) {
	                        discount = "20%";
	                    }

	                    itemDiscountArray[index] = discount;

	                    subtotal = subtotal + i * x;

	                    String n = String.valueOf(subtotal);

	                    itemSubtotalArray[index] = n;

	                    index++;

	                    confirmB.setEnabled(true);
	                    processB.setEnabled(false);
	                    
	                    JOptionPane.showMessageDialog(null, "Item found! Added to your cart.", "Message", JOptionPane.INFORMATION_MESSAGE);
	                    
	                    found = true;

	                    itemTextField.setText(itemID + " " + name + " $" + price + " " + quantity + " " + discount + " $" + price);

	                    break whileloop;
	                    
	                } 
	                
	                else {
	                	
	                    inventoryLine = inputBuffReader.readLine();
	                    
	                }
	            }

	            if (!found) {
	            	
	                JOptionPane.showMessageDialog(null, "Item not found!", "Message", JOptionPane.INFORMATION_MESSAGE);
	                
	                idTextField.setText("");
	                qtyTextField.setText("");
	                
	            }

	        } 
	        
	        catch (FileNotFoundException fileNotFoundException) {
	        	
	            JOptionPane.showMessageDialog(null, "Error: File not found!", "Message - ERROR", JOptionPane.ERROR_MESSAGE);
	            
	        } 
	        
	        catch (IOException ioException) {
	        	
	            JOptionPane.showMessageDialog(null, "Error: Problem reading from file!", "Message - ERROR", JOptionPane.ERROR_MESSAGE);
	            
	        }
	        
	    }
	}


  private class ConfirmButtonHandler implements ActionListener {
    public void actionPerformed(ActionEvent e) {

      itemCount++;

      idTextField.setText("");
      qtyTextField.setText("");

      idLabel.setText("Enter Item ID for Item #" + (itemCount + 1) + ":");
      qtyLabel.setText("Enter quantity for Item #" + (itemCount + 1) + ":");
      itemLabel.setText("Details of Item #" + (itemCount + 1) + ":");
      totalLabel.setText("Order subtotal for" + itemCount + " item(s):");
      processB.setText("Find Item #" + (itemCount + 1));
      confirmB.setText("Purchase Item #" + (itemCount + 1));

      viewB.setEnabled(true);
      finishB.setEnabled(true);
      processB.setEnabled(true);

      confirmB.setEnabled(false);

      totalTextField.setText(String.valueOf("$" + subtotal));
      
      itemTextField.setText("");

      String outputMessage;
      outputMessage = "Item purchased!";
      JOptionPane.showMessageDialog(null, outputMessage, "Message", JOptionPane.INFORMATION_MESSAGE);

    }
  }

  private class ViewButtonHandler implements ActionListener {
    public void actionPerformed(ActionEvent e) {

      String Message = "";

      counter = 1;

      for (int i = 0; i < itemCount; i++) {
    	  
        Message = Message + counter + ". " + itemIDArray[i] + "  " + itemTitleArray[i] + "$" + itemPriceArray[i] + "  " + itemQuantityArray[i] + "  " + itemDiscountArray[i] + "$" + itemSubtotalArray[i];
        Message = Message + "\n\n";
        
        counter++;
        
      }

      JOptionPane.showMessageDialog(null, Message, "Message - Cart", JOptionPane.INFORMATION_MESSAGE);

    }
  }

  private class ExitButtonHandler implements ActionListener {
    public void actionPerformed(ActionEvent e) {

      String outputMessage;
      
      outputMessage = "Exiting application...";
      
      JOptionPane.showMessageDialog(null, outputMessage, "Message", JOptionPane.INFORMATION_MESSAGE);
      
      Store.super.dispose();

    }
  }

  private class FinishButtonHandler implements ActionListener {
    public void actionPerformed(ActionEvent e) {

      Date date = new Date();
      Date dateN = new Date();

      DateFormat formatter = new SimpleDateFormat("dd/MM/yy HH:mm:ss z");
      DateFormat formatterN = new SimpleDateFormat("DDMMYYYYHHMM");

      formatter.setTimeZone(TimeZone.getTimeZone("EST"));

      double taxrate = 0.06;
      double fsubtotal = Math.round((subtotal * taxRate) * 100.0) / 100.0;
      double ftotal = Math.round((subtotal + fsubtotal) * 100.0) / 100.0;
      
      DecimalFormat decimalFormat = new DecimalFormat("#0.00");
      
      String Message = "Date: " + formatter.format(date);

      Message = Message + "\n\n";
      Message = Message + "Number of unique Items: " + itemCount;
      Message = Message + "\n\n";
      Message = Message + "Item ID / Title / Price / Quantity / Discount % / Subtotal:\n\n";

      for (int i = 0; i < itemCount; i++) {	
    	  
        Message = Message + itemIDArray[i] + "  " + itemTitleArray[i] + "$" + itemPriceArray[i] + "  " + itemQuantityArray[i] + "  " + itemDiscountArray[i] + "$" + itemSubtotalArray[i];
        Message = Message + "\n\n";
        
      }

      Message = Message + "Order Subtotal: $" + subtotal;
      Message = Message + "\n\n";
      Message = Message + "Tax rate: 6%";
      Message = Message + "\n";
      Message = Message + "Tax amount: $" + fsubtotal;
      Message = Message + "\n\n";
      Message = Message + "Order Total: $" + ftotal;
      Message = Message + "\n\n";
      Message = Message + "Thanks for shopping!";

      try {
    	  
        FileWriter myWriter = new FileWriter("transactions.txt");

        for (int i = 0; i < itemCount; i++) {
        	
          myWriter.write(formatterN.format(dateN) + itemIDArray[i] + "  " + itemTitleArray[i] + "$" + itemPriceArray[i] + "  " + itemQuantityArray[i] + "  " + itemDiscountArray[i] + "$" + itemSubtotalArray[i] + "  " + formatter.format(date));
          myWriter.write("\n");
          
        }
        
        myWriter.close();

      } 
      
      catch (IOException e1) {
    	  
        System.out.println("An error occurred.");
        e1.printStackTrace();
        
      }

      JOptionPane.showMessageDialog(null, Message, "Store - Final Invoice", JOptionPane.INFORMATION_MESSAGE);
      Store.super.dispose();

    }
  }

  private class NewButtonHandler implements ActionListener {
    public void actionPerformed(ActionEvent e) {

      String outputMessage;
      
      outputMessage = "Starting a new order...";
      JOptionPane.showMessageDialog(null, outputMessage, "Message", JOptionPane.INFORMATION_MESSAGE);

      itemCount = 0;

      idLabel.setText("Enter the Item ID for Item #" + (itemCount + 1) + ":");
      qtyLabel.setText("Enter the quantity for Item #" + (itemCount + 1) + ":");
      itemLabel.setText("Details for Item #" + (itemCount + 1) + ":");
      totalLabel.setText("Order subtotal for " + (itemCount + 1) + " item(s):");
      processB.setText("Find Item #" + (itemCount + 1));
      confirmB.setText("Purchase Item #" + (itemCount + 1));

      idTextField.setText("");
      qtyTextField.setText("");
      totalTextField.setText("");
      itemTextField.setText("");

      viewB.setEnabled(false);
      confirmB.setEnabled(false);
      finishB.setEnabled(false);
      processB.setEnabled(true);

    }
  }

  public static void main(String[] args) {

    JFrame store = new Store();
    
    store.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    store.setVisible(true);
    store.setResizable(false);

  }
}
