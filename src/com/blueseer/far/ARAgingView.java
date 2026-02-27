/*
The MIT License (MIT)

Copyright (c) Terry Evans Vaughn 

All rights reserved.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package com.blueseer.far;

import bsmf.MainFrame;
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.checkperms;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.adm.admData;
import com.blueseer.ctr.cusData;
import com.blueseer.fgl.fglData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsFormatDouble;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsNumberToUS;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getDateDB;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.jsonToData;
import static com.blueseer.utl.BlueSeerUtils.sendServerPost;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import java.util.HashMap;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author vaughnte
 */
public class ARAgingView extends javax.swing.JPanel {
    
    String selectedCustomer = "";
    String selectedCustomerName = "";
    boolean isLoad = false;
    public String rsData; 
    Object[][] roData;
    ArrayList<String[]> initDataSets = new ArrayList<>();
    String defaultsite = "";
    String defaultCurrency = "";
 
     MyTableModel modelsummary = new ARAgingView.MyTableModel(new Object[][]{},
                        new String[]{
                            getGlobalColumnTag("detail"), 
                            getGlobalColumnTag("customer"), 
                            getGlobalColumnTag("name"), 
                            getGlobalColumnTag("0daysold"), 
                            getGlobalColumnTag("30daysold"), 
                            getGlobalColumnTag("60daysold"), 
                            getGlobalColumnTag("90daysold"), 
                            getGlobalColumnTag("90+daysold")})
             {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0  )       
                            return ImageIcon.class;
                        else if (col == 3 || col == 4 || col == 5 || col == 6 || col == 7)
                            return Double.class;
                        else return String.class;  //other columns accept String values  
                      } 
                      @Override
                      public boolean isCellEditable(int row, int column) {
                            return false;
                            //Only the first column
                            // return column == 1;
                      }
                      
                        };
    
    MyTableModel2 modeldetail = new ARAgingView.MyTableModel2(new Object[][]{},
                        new String[]{getGlobalColumnTag("select"), 
                            getGlobalColumnTag("id"), 
                            getGlobalColumnTag("description"), 
                            getGlobalColumnTag("type"), 
                            getGlobalColumnTag("effectivedate"), 
                            getGlobalColumnTag("duedate"), 
                            getGlobalColumnTag("0daysold"), 
                            getGlobalColumnTag("30daysold"), 
                            getGlobalColumnTag("60daysold"), 
                            getGlobalColumnTag("90daysold"), 
                            getGlobalColumnTag("90+daysold")})
            {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0)       
                            return ImageIcon.class;  
                        else if (col == 6 || col == 7 || col == 8 || col == 9 || col == 10)
                            return Double.class;
                        else return String.class;  //other columns accept String values  
                      }  
                        };
   
    
    MyTableModel3 modelpayment = new ARAgingView.MyTableModel3(new Object[][]{},
                        new String[]{
                            getGlobalColumnTag("id"), 
                            getGlobalColumnTag("invoice"), 
                            getGlobalColumnTag("effectivedate"), 
                            getGlobalColumnTag("duedate"), 
                            getGlobalColumnTag("type"), 
                            getGlobalColumnTag("checknbr"), 
                            getGlobalColumnTag("invoiceamt"), 
                            getGlobalColumnTag("checkamt")});
    
    /**
     * Creates new form ScrapReportPanel
     */
    
    
    
    class MyTableModel3 extends DefaultTableModel {  
      
        public MyTableModel3(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }  
         
        @Override  
          public Class getColumnClass(int col) {  
            if (col == 6 || col == 7 )       
                return Double.class;  
            else return String.class;  //other columns accept String values  
             
        }  
      @Override  
      public boolean isCellEditable(int row, int col) {  
        if (col == 0)       //first column will be uneditable  
            return false;  
        else return true;  
      }  
       
        }   
    
     class MyTableModel2 extends DefaultTableModel {  
      
        public MyTableModel2(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }  
         
        @Override  
          public Class getColumnClass(int col) {  
            if (col == 6 || col == 7 || col == 8 || col == 9 || col == 10)       
                return Double.class;  
            else return String.class;  //other columns accept String values  
             
        }  
      @Override  
      public boolean isCellEditable(int row, int col) {  
        if (col == 0)       //first column will be uneditable  
            return false;  
        else return true;  
      }  
       
        }   
                        
     class MyTableModel extends DefaultTableModel {  
      
        public MyTableModel(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }  
         
        @Override  
          public Class getColumnClass(int col) {  
            if (col == 3 || col == 4 || col == 5 || col == 6 || col == 7)       
                return Double.class;  
            else return String.class;  //other columns accept String values  
           
        }  
      @Override  
      public boolean isCellEditable(int row, int col) {  
        if (col == 0)       //first column will be uneditable  
            return false;  
        else return true;  
      }  
       
        }    
    
    class SomeRenderer extends DefaultTableCellRenderer {
        
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);
        
        String status = (String)table.getModel().getValueAt(table.convertRowIndexToModel(row), 5);  // 7 = status column
        
         if ("0".equals(status) || status.isEmpty()) {
            c.setBackground(Color.red);
            c.setForeground(Color.WHITE);
        } 
        else {
            c.setBackground(table.getBackground());
            c.setForeground(table.getForeground());
        }       
        
        //c.setBackground(row % 2 == 0 ? Color.LIGHT_GRAY : Color.WHITE);
      // c.setBackground(row % 2 == 0 ? Color.GREEN : Color.LIGHT_GRAY);
      // c.setBackground(row % 3 == 0 ? new Color(245,245,220) : Color.LIGHT_GRAY);
       /*
            if (column == 3)
            c.setForeground(Color.BLUE);
            else
                c.setBackground(table.getBackground());
       */
        return c;
    }
    }
      
     class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setForeground(table.getSelectionForeground());
                setBackground(table.getSelectionBackground());
            } else {
                setForeground(Color.blue);
                setBackground(UIManager.getColor("Button.background"));
            }
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
    
        
    public ARAgingView() {
        initComponents();
        setLanguageTags(this);
    }

    public void executeTask(String x, String[] y) { 
      
        class Task extends SwingWorker<String[], Void> {
         
          String action = "";
          String[] key = null;
          
          public Task(String action, String[] key) { 
              this.action = action;
              this.key = key;
          }     
            
        @Override
        public String[] doInBackground() throws Exception {
            String[] message = new String[2];
            message[0] = "";
            message[1] = "";
            
            rsData = "";
            
            
            switch(this.action) {
                case "dataInit":
                    message = getInitialization();
                    break;
                
                case "getBrowseView":
                    message = getBrowseView();
                    break; 
                    
                case "getBrowseDetView":
                    message = getBrowseDetView(key[0]);
                    break;  
                    
                case "getBrowsePaymentView":
                    message = getBrowsePaymentView(key[0]);
                    break;    
                    
                default:
                    message = new String[]{"1", "unknown action"};
            }
            
            
            
            
            return message;
        }
 
        
       public void done() {
            try {
            String[] message = get();
           
            BlueSeerUtils.endTask(message);
            
            
            if (this.action.equals("dataInit")) {
                    done_Initialization();
            }
            
            if (this.action.equals("getBrowseView")) {
                done_getBrowseView();
            }
            
            if (this.action.equals("getBrowseDetView")) {
                done_getBrowseDetView();
            }
            
            if (this.action.equals("getBrowsePaymentView")) {
                done_getBrowsePaymentView();
            }
            
            } catch (Exception e) {
                MainFrame.bslog(e);
            } 
           
        }
    }  
      
       BlueSeerUtils.startTask(new String[]{"","Running..."});
       Task z = new Task(x, y); 
       z.execute(); 
       
    }
    
    public void setLanguageTags(Object myobj) {
       JPanel panel = null;
        JTabbedPane tabpane = null;
        JScrollPane scrollpane = null;
        if (myobj instanceof JPanel) {
            panel = (JPanel) myobj;
        } else if (myobj instanceof JTabbedPane) {
           tabpane = (JTabbedPane) myobj; 
        } else if (myobj instanceof JScrollPane) {
           scrollpane = (JScrollPane) myobj;    
        } else {
            return;
        }
       Component[] components = panel.getComponents();
       for (Component component : components) {
           if (component instanceof JPanel) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".panel." + component.getName())) {
                       ((JPanel) component).setBorder(BorderFactory.createTitledBorder(tags.getString(this.getClass().getSimpleName() +".panel." + component.getName())));
                    } 
                    setLanguageTags((JPanel) component);
                }
                if (component instanceof JLabel ) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".label." + component.getName())) {
                       ((JLabel) component).setText(tags.getString(this.getClass().getSimpleName() +".label." + component.getName()));
                    }
                }
                if (component instanceof JButton ) {
                    if (tags.containsKey("global.button." + component.getName())) {
                       ((JButton) component).setText(tags.getString("global.button." + component.getName()));
                    }
                }
                if (component instanceof JCheckBox) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".label." + component.getName())) {
                       ((JCheckBox) component).setText(tags.getString(this.getClass().getSimpleName() +".label." + component.getName()));
                    } 
                }
                if (component instanceof JRadioButton) {
                    if (tags.containsKey(this.getClass().getSimpleName() + ".label." + component.getName())) {
                       ((JRadioButton) component).setText(tags.getString(this.getClass().getSimpleName() +".label." + component.getName()));
                    } 
                }
       }
    }
      
    public void initvars(String[] arg) {
        executeTask("dataInit", null); 
    }
    
    public String[] getInitialization() {
        initDataSets = admData.getInitMinimum(this.getClass().getName(), bsmf.MainFrame.userid, "customers");
        if (initDataSets.isEmpty()) {
           return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.dataInitError}; 
        } else {
           return new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess}; 
        }
    }  
    
    public void done_Initialization() {
        
        isLoad = true;
        
        modelsummary.setRowCount(0);
        java.util.Date now = new java.util.Date();
       
         
        modelsummary.setNumRows(0);
        modeldetail.setNumRows(0);
        tablesummary.setModel(modelsummary);
        tabledetail.setModel(modeldetail);
        tablepayment.setModel(modelpayment);
         
        tabledetail.getTableHeader().setReorderingAllowed(false);
        tablepayment.getTableHeader().setReorderingAllowed(false);
         
        detailpanel.setVisible(false);
        btdetail.setEnabled(false);
        btexport.setEnabled(false);
        btcsv.setEnabled(false);
        btpdf.setEnabled(false);
         
        cbpaymentpanel.setEnabled(false);
        cbpaymentpanel.setSelected(false);
        paymentpanel.setVisible(false);
         
        ddfromcust.removeAllItems();
        ddtocust.removeAllItems();
        
        
        
        ddsite.removeAllItems();
        
        
        for (String[] s : initDataSets) {
            
            if (s[0].equals("sites")) {
              ddsite.addItem(s[1]); 
            }
            if (s[0].equals("site")) {
              defaultsite = s[1]; 
            }
            if (s[0].equals("customers")) {
              ddfromcust.addItem(s[1]); 
              ddtocust.addItem(s[1]); 
            }
            if (s[0].equals("currency")) {
              defaultCurrency = s[1]; 
            }
        }
        if (ddsite.getItemCount() > 0) {
            ddsite.setSelectedItem(defaultsite);
        }
       
        ddfromcust.setSelectedIndex(0);
        ddtocust.setSelectedIndex(ddfromcust.getItemCount() - 1); 
        
        tablesummary.getColumnModel().getColumn(0).setMaxWidth(100);
        tablesummary.getTableHeader().setReorderingAllowed(false);
        tablesummary.getColumnModel().getColumn(0).setMaxWidth(100);
        tablesummary.getColumnModel().getColumn(3).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(defaultCurrency)));
        tablesummary.getColumnModel().getColumn(4).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(defaultCurrency)));
        tablesummary.getColumnModel().getColumn(5).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(defaultCurrency)));
        tablesummary.getColumnModel().getColumn(6).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(defaultCurrency)));
        tablesummary.getColumnModel().getColumn(7).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(defaultCurrency)));

        tabledetail.getColumnModel().getColumn(6).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(defaultCurrency)));
        tabledetail.getColumnModel().getColumn(7).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(defaultCurrency)));
        tabledetail.getColumnModel().getColumn(8).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(defaultCurrency)));
        tabledetail.getColumnModel().getColumn(9).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(defaultCurrency)));
        tabledetail.getColumnModel().getColumn(10).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(defaultCurrency)));
        
        isLoad = false;
        
    }
    
    public String[] getBrowseView() {
        String jsonString = null; 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) { 
        ArrayList<String[]> list = new ArrayList<>();
        list.add(new String[]{"id","getARAgingView"});
        list.add(new String[]{"param1",ddfromcust.getSelectedItem().toString()});
        list.add(new String[]{"param2",ddtocust.getSelectedItem().toString()});
        list.add(new String[]{"param3",ddsite.getSelectedItem().toString()});
        
        try {
                jsonString = sendServerPost(list, "", null, "dataServFAR"); 
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getMessageTag(1010, "getExpenseBrowseView")};
            }
        } else {
            jsonString = farData.getARAgingView(new String[]{
                ddfromcust.getSelectedItem().toString(),
                ddtocust.getSelectedItem().toString(),
                ddsite.getSelectedItem().toString()
            });
        }
      
      if (jsonString == null) {
          return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getMessageTag(1010, "getExpenseBrowseView return jsonString is null")};
      }
        
      roData = jsonToData(jsonString);
       
      return new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getMessageTag(1125)};
    }

    public void done_getBrowseView() {
       
        int i = 0;
        double amt = 0;
        modelsummary.setNumRows(0);
        if (roData != null) {
        for (Object[] rowData : roData) {
            roData[i][3] = bsParseDouble(roData[i][3].toString());
            roData[i][4] = bsParseDouble(roData[i][4].toString());
            roData[i][5] = bsParseDouble(roData[i][5].toString());
            roData[i][6] = bsParseDouble(roData[i][6].toString());
            roData[i][7] = bsParseDouble(roData[i][7].toString());
            amt = amt + (bsParseDouble(roData[i][3].toString()) + bsParseDouble(roData[i][4].toString()) + bsParseDouble(roData[i][5].toString()) + bsParseDouble(roData[i][6].toString()) + bsParseDouble(roData[i][7].toString()));
            i++;
            modelsummary.addRow(rowData);
        }
        }
        
        labelcount.setText(String.valueOf(i));
        labeldollar.setText(String.valueOf(currformatDouble(amt)));
        
        roData = null;
    }   
    
    public String[] getBrowseDetView(String cust) {
      
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getARAgingDetailView"});
            list.add(new String[]{"param1", cust});
            list.add(new String[]{"param2", ddsite.getSelectedItem().toString()});
            try {
                jsonString = sendServerPost(list, "", null, "dataServFAR"); 
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getMessageTag(1010, "getDetail")};
            }
        } else {
            jsonString = farData.getARAgingDetailView(new String[]{cust, ddsite.getSelectedItem().toString()}); 
        }        
        roData = jsonToData(jsonString);
        
        return new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getMessageTag(1125)};
      
    }
   
    public void done_getBrowseDetView() {
      modeldetail.setNumRows(0);
         double totalsales = 0;
         double totalqty = 0;
       int i = 0;  
       if (roData != null) {
        if (roData.length > 0) {
            for (Object[] rowData : roData) {
                roData[i][6] = bsParseDouble(roData[i][6].toString());
                roData[i][7] = bsParseDouble(roData[i][7].toString());
                roData[i][8] = bsParseDouble(roData[i][8].toString());
                roData[i][9] = bsParseDouble(roData[i][9].toString());
                roData[i][10] = bsParseDouble(roData[i][10].toString());
                modeldetail.addRow(rowData);
                i++;
            } 
        }
       }
       roData = null;
    }
    
    public String[] getBrowsePaymentView(String cust) {
      
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getARAgingPaymentView"});
            list.add(new String[]{"param1", cust});
            try {
                jsonString = sendServerPost(list, "", null, "dataServFAR"); 
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getMessageTag(1010, "getDetail")};
            }
        } else {
            jsonString = farData.getARAgingPaymentView(cust); 
        }        
        roData = jsonToData(jsonString);
        
        return new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getMessageTag(1125)};
      
    }
   
    public void done_getBrowsePaymentView() {
      modelpayment.setNumRows(0);
         double totalsales = 0;
         double totalqty = 0;
       int i = 0;  
       if (roData != null) {
        if (roData.length > 0) {
            for (Object[] rowData : roData) {
                roData[i][6] = bsParseDouble(roData[i][6].toString());
                roData[i][7] = bsParseDouble(roData[i][7].toString());
                modelpayment.addRow(rowData);
                i++;
            } 
        }
       }
       roData = null;
    }
    
    public void getExportView(String cust) {
        String jsonString = null; 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) { 
        ArrayList<String[]> list = new ArrayList<>();
        list.add(new String[]{"id","getARAgingExport"});
        list.add(new String[]{"param1",cust});
        
        try {
                jsonString = sendServerPost(list, "", null, "dataServFAR"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = farData.getARAgingExport(cust);
        }
      
      if (jsonString != null) {
        Object[][] expData = jsonToData(jsonString);
        FileDialog fDialog;
        fDialog = new FileDialog(new Frame(), "Save", FileDialog.SAVE);
        fDialog.setVisible(true);
        //fDialog.setFile("data.csv");
        String path = fDialog.getDirectory() + fDialog.getFile();
        File f = new File(path);
        BufferedWriter output;

            try {
                output = new BufferedWriter(new FileWriter(f));
                String myheader = "Cust,Inv,PO,Invdate,DueDate,0,30,60,90,90+";
                output.write(myheader + '\n');
                int i = 0;
                String newstring = "";
                if (expData.length > 0) {
                    for (Object[] rowData : expData) {
                    expData[i][6] = bsParseDouble(expData[i][6].toString());
                    expData[i][7] = bsParseDouble(expData[i][7].toString());
                    newstring = expData[i][0].toString() + "," + expData[i][1].toString() + "," + expData[i][2].toString() + "," +
                    expData[i][3].toString() + "," + expData[i][4].toString() + "," + expData[i][5].toString() + "," +
                    expData[i][6].toString() + "," + expData[i][7].toString() + "," + expData[i][8].toString() + "," + expData[i][9].toString() ;
                    output.write(newstring + '\n');
                    i++;
                    } 
                }    
                output.close();
                           
            } catch (IOException ex) {
                bslog(ex);
            }
                
                
      }
        
      
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        labelcount = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        labeldollar = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        btRun = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        btdetail = new javax.swing.JButton();
        ddfromcust = new javax.swing.JComboBox();
        ddtocust = new javax.swing.JComboBox();
        cbpaymentpanel = new javax.swing.JCheckBox();
        btexport = new javax.swing.JButton();
        btcsv = new javax.swing.JButton();
        btpdf = new javax.swing.JButton();
        ddsite = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        tablepanel = new javax.swing.JPanel();
        summarypanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablesummary = new javax.swing.JTable();
        detailpanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabledetail = new javax.swing.JTable();
        paymentpanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablepayment = new javax.swing.JTable();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setName("panelmain"); // NOI18N

        labelcount.setText("0");

        jLabel7.setText("Count");
        jLabel7.setName("lblcount"); // NOI18N

        jLabel8.setText("$");
        jLabel8.setName("lbamt"); // NOI18N

        labeldollar.setText("0");

        jLabel3.setText("To Cust");
        jLabel3.setName("lbltocust"); // NOI18N

        btRun.setText("Run");
        btRun.setName("btrun"); // NOI18N
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jLabel2.setText("From Cust");
        jLabel2.setName("lblfromcust"); // NOI18N

        btdetail.setText("Hide Detail");
        btdetail.setName("bthidedetail"); // NOI18N
        btdetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdetailActionPerformed(evt);
            }
        });

        cbpaymentpanel.setText("Payments");
        cbpaymentpanel.setName("cbpayments"); // NOI18N
        cbpaymentpanel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbpaymentpanelActionPerformed(evt);
            }
        });

        btexport.setText("Export Detail");
        btexport.setName("btexportdetail"); // NOI18N
        btexport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btexportActionPerformed(evt);
            }
        });

        btcsv.setText("CSV");
        btcsv.setName("btcsv"); // NOI18N
        btcsv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcsvActionPerformed(evt);
            }
        });

        btpdf.setText("PDF");
        btpdf.setName("btpdf"); // NOI18N
        btpdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btpdfActionPerformed(evt);
            }
        });

        jLabel1.setText("Site");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ddfromcust, 0, 169, Short.MAX_VALUE)
                    .addComponent(ddtocust, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btRun)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btdetail)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbpaymentpanel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btcsv)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btpdf)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btexport)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btRun)
                        .addComponent(btdetail)
                        .addComponent(cbpaymentpanel)
                        .addComponent(btcsv)
                        .addComponent(btpdf)
                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)
                        .addComponent(btexport))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddfromcust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ddtocust, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 435, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(labeldollar, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                    .addComponent(labelcount, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labeldollar, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        tablepanel.setLayout(new javax.swing.BoxLayout(tablepanel, javax.swing.BoxLayout.LINE_AXIS));

        summarypanel.setLayout(new java.awt.BorderLayout());

        tablesummary.setAutoCreateRowSorter(true);
        tablesummary.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tablesummary.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablesummaryMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablesummary);

        summarypanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        tablepanel.add(summarypanel);

        detailpanel.setLayout(new java.awt.BorderLayout());

        tabledetail.setAutoCreateRowSorter(true);
        tabledetail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabledetail.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabledetailMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tabledetail);

        detailpanel.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        tablepanel.add(detailpanel);

        paymentpanel.setLayout(new java.awt.BorderLayout());

        tablepayment.setAutoCreateRowSorter(true);
        tablepayment.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(tablepayment);

        paymentpanel.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        tablepanel.add(paymentpanel);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1467, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(613, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                    .addGap(106, 106, 106)
                    .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, 612, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRunActionPerformed
        executeTask("getBrowseView", null);
    }//GEN-LAST:event_btRunActionPerformed

    private void tablesummaryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablesummaryMouseClicked

        int row = tablesummary.rowAtPoint(evt.getPoint());
        int col = tablesummary.columnAtPoint(evt.getPoint());
         // select any field in a row grabs the vendor for that row...so open the possibility of payment for that row/vendor
        cbpaymentpanel.setEnabled(true);
        selectedCustomer = tablesummary.getValueAt(row, 1).toString();
        selectedCustomerName = tablesummary.getValueAt(row, 2).toString();
        
        if ( col == 0) {
            executeTask("getBrowseDetView", new String[]{selectedCustomer});
           // getdetail(selectedCustomer);
            btdetail.setEnabled(true);
            detailpanel.setVisible(true);
            btexport.setEnabled(true);
            btcsv.setEnabled(true);
            btpdf.setEnabled(true);
            paymentpanel.setVisible(false);
            cbpaymentpanel.setSelected(false);
        }
    }//GEN-LAST:event_tablesummaryMouseClicked

    private void btdetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdetailActionPerformed
        detailpanel.setVisible(false);
       btdetail.setEnabled(false);
       btexport.setEnabled(false);
       btcsv.setEnabled(false);
       btpdf.setEnabled(false);
       paymentpanel.setVisible(false);
       cbpaymentpanel.setSelected(false);
    }//GEN-LAST:event_btdetailActionPerformed

    private void cbpaymentpanelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbpaymentpanelActionPerformed
        if (cbpaymentpanel.isSelected()) {
           if (! selectedCustomer.isEmpty()) {
          // getpayment(selectedCustomer);
           executeTask("getBrowsePaymentView", new String[]{selectedCustomer});
            paymentpanel.setVisible(true);
           }
       } else {
           paymentpanel.setVisible(false);
       }
    }//GEN-LAST:event_cbpaymentpanelActionPerformed

    private void btexportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btexportActionPerformed
        getExportView(selectedCustomer);
    }//GEN-LAST:event_btexportActionPerformed

    private void btcsvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcsvActionPerformed
        if (tablesummary != null)
        OVData.exportCSV(tablesummary);
    }//GEN-LAST:event_btcsvActionPerformed

    private void btpdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btpdfActionPerformed
        if (tabledetail != null && modeldetail.getRowCount() > 0) {
        try {
                HashMap hm = new HashMap();
                hm.put("REPORT_RESOURCE_BUNDLE", bsmf.MainFrame.tags);
                hm.put("customercode", selectedCustomer);
                hm.put("customername", selectedCustomerName);
                //hm.put("imagepath", "images/avmlogo.png");
               // res = st.executeQuery("select shd_id, sh_cust, shd_po, shd_item, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, cm_city, cm_state, cm_zip, concat(cm_city, \" \", cm_state, \" \", cm_zip) as st_citystatezip, site_desc from ship_det inner join ship_mstr on sh_id = shd_id inner join cm_mstr on cm_code = sh_cust inner join site_mstr on site_site = sh_site where shd_id = '1848' ");
               // JRResultSetDataSource jasperReports = new JRResultSetDataSource(res);
                File mytemplate = new File("jasper/aragingdetail.jasper");
                
                JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, new JRTableModelDataSource(tabledetail.getModel()) );
               // JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/araging.pdf");
         
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setVisible(true);
        } catch (Exception e) {
            MainFrame.bslog(e);
            bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
        }
        }
    }//GEN-LAST:event_btpdfActionPerformed

    private void tabledetailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabledetailMouseClicked
         
        int row = tabledetail.rowAtPoint(evt.getPoint());
        int col = tabledetail.columnAtPoint(evt.getPoint());
           if ( col == 0) {     
               if (! checkperms("InvoiceMaint")) { return; }
               String[] args = new String[]{tabledetail.getValueAt(row, 1).toString()};
               reinitpanels("InvoiceMaint", true, args);
           }
    }//GEN-LAST:event_tabledetailMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRun;
    private javax.swing.JButton btcsv;
    private javax.swing.JButton btdetail;
    private javax.swing.JButton btexport;
    private javax.swing.JButton btpdf;
    private javax.swing.JCheckBox cbpaymentpanel;
    private javax.swing.JComboBox ddfromcust;
    private javax.swing.JComboBox<String> ddsite;
    private javax.swing.JComboBox ddtocust;
    private javax.swing.JPanel detailpanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel labelcount;
    private javax.swing.JLabel labeldollar;
    private javax.swing.JPanel paymentpanel;
    private javax.swing.JPanel summarypanel;
    private javax.swing.JTable tabledetail;
    private javax.swing.JPanel tablepanel;
    private javax.swing.JTable tablepayment;
    private javax.swing.JTable tablesummary;
    // End of variables declaration//GEN-END:variables
}
