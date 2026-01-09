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

package com.blueseer.inv;

import com.blueseer.prd.*;
import bsmf.MainFrame;
import static bsmf.MainFrame.bslog;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import static bsmf.MainFrame.checkperms;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.menumap;
import static bsmf.MainFrame.mydialog;
import static bsmf.MainFrame.panelmap;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.cleanDirString;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.jsonToData;
import static com.blueseer.utl.BlueSeerUtils.sendServerPost;
import static com.blueseer.utl.OVData.getSystemJasperDirectory;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.Connection;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.ListOfArrayDataSource;
import net.sf.jasperreports.view.JasperViewer;



/**
 *
 * @author vaughnte
 */
public class InventoryBrowse extends javax.swing.JPanel {
 
    public String rsData; 
     Object[][] roData;
    ArrayList<String[]> initDataSets = new ArrayList<>();
    String defaultsite = "";
    String defaultcurrency = "";
    
     MyTableModel mymodel = new InventoryBrowse.MyTableModel(new Object[][]{},
                        new String[]{
                            getGlobalColumnTag("item"),                             
                            getGlobalColumnTag("description"),
                            getGlobalColumnTag("class"),
                            getGlobalColumnTag("warehouse"),
                            getGlobalColumnTag("location"),
                            getGlobalColumnTag("serial"),
                            getGlobalColumnTag("expiredate"),
                            getGlobalColumnTag("qty")});
    
    // tr_id, tr_item, tr_type, tr_qty, tr_eff_date, tr_timestamp, tr_ref, tr_serial, tr_program , tr_userid
    /**
     * Creates new form ScrapReportPanel
     */
    
     class MyTableModel extends DefaultTableModel {  
      
        public MyTableModel(Object rowData[][], Object columnNames[]) {  
             super(rowData, columnNames);  
          }  
         
        @Override  
          public Class getColumnClass(int col) {  
          //  if (col == 2 || col == 3 || col == 4)       
          //      return Double.class;  
          //  else return String.class;  //other columns accept String values  
              return String.class;
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
            c.setBackground(table.getBackground());
            c.setForeground(table.getForeground());
               
        return c;
    }
    }
     
            
    public InventoryBrowse() {
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
                
                case "getInvBrowseView":
                    message = getInvBrowseView();
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
            
            if (this.action.equals("getInvBrowseView")) {
                done_getInvBrowseView();
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
    
    public String[] getInitialization() {
        initDataSets = invData.getInvMaintInit(this.getClass().getName(), bsmf.MainFrame.userid);
        if (initDataSets.isEmpty()) {
           return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.dataInitError}; 
        } else {
           return new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess}; 
        }
    }  
    
    public void done_Initialization() {
        Calendar calfrom = Calendar.getInstance();
        Calendar calto = Calendar.getInstance();
        ddsite.removeAllItems();
        ddfromitem.removeAllItems();
        ddtoitem.removeAllItems();
        ddfromwh.removeAllItems();
        ddfromloc.removeAllItems();
        String defaultsite = "";
        
        mymodel.setRowCount(0);
        
        ddclass.removeAllItems();
        ddclass.addItem("");
        ddclass.addItem("A");
        ddclass.addItem("M");
        ddclass.addItem("P");
        ddclass.setSelectedIndex(0);
        cbzero.setSelected(false);
        tbserial.setText("");
        
        for (String[] s : initDataSets) {
            
            if (s[0].equals("sites")) {
              ddsite.addItem(s[1]); 
            }
            if (s[0].equals("site")) {
              defaultsite = s[1]; 
            }
            if (s[0].equals("currency")) {
              defaultcurrency = s[1]; 
            }
            if (s[0].equals("items")) {
              ddfromitem.addItem(s[1]); 
              ddtoitem.addItem(s[1]);
            }
            if (s[0].equals("locations")) {
              ddfromloc.addItem(s[1]); 
            }
            if (s[0].equals("warehouses")) {
              ddfromwh.addItem(s[1]); 
            }
        }
        if (ddsite.getItemCount() > 0) {
            ddsite.setSelectedItem(defaultsite);
        }
        
        ddfromwh.insertItemAt("", 0);
        ddfromwh.setSelectedIndex(0);
        ddfromloc.insertItemAt("", 0);
        ddfromloc.setSelectedIndex(0);
        
        mymodel.setRowCount(0);
           tablereport.setModel(mymodel);
              Enumeration<TableColumn> en = tablereport.getColumnModel().getColumns();
                 while (en.hasMoreElements()) {
                     TableColumn tc = en.nextElement();
                     if (mymodel.getColumnClass(tc.getModelIndex()).getSimpleName().equals("ImageIcon")) {
                         continue;
                     }
                     tc.setCellRenderer(new InventoryBrowse.SomeRenderer());
                 }
                // tablereport.getColumnModel().getColumn(0).setMaxWidth(100);
                // tablereport.getColumnModel().getColumn(6).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(defaultcurrency)));
                // tablereport.getColumnModel().getColumn(7).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(defaultcurrency)));
                
       
    }
    
    public String[] getInvBrowseView() {
        String[] x = new String[2];
        String fromitem = "";
        String toitem = "";
        
        if (ddfromitem.getSelectedItem() == null || ddfromitem.getSelectedItem().toString().isEmpty()) {
                    fromitem = bsmf.MainFrame.lowchar;
        } else {
            fromitem = ddfromitem.getSelectedItem().toString();
        }
         if (ddtoitem.getSelectedItem() == null || ddtoitem.getSelectedItem().toString().isEmpty()) {
            toitem = bsmf.MainFrame.hichar;
        } else {
            toitem = ddtoitem.getSelectedItem().toString();
        }
        
        String jsonString = null; 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) { 
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"id","getItemBrowseView"});
        list.add(new String[]{"fromitem",fromitem});
        list.add(new String[]{"toitem",toitem});
        list.add(new String[]{"site",ddsite.getSelectedItem().toString()});
        list.add(new String[]{"serial",tbserial.getText()});
        try {
                jsonString = sendServerPost(list, "", null, "dataServINV"); 
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getMessageTag(1010, "getOrderBrowseView")};
            }
        } else {
            jsonString = invData.getInvBrowseView(new String[]{fromitem, 
                toitem,
                ddsite.getSelectedItem().toString(),
                tbserial.getText()
            });
        }
        
        
        roData = jsonToData(jsonString);
       
      return new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getMessageTag(1125)};
    }

    public void done_getInvBrowseView() {
        
        int i = 0;
        double qty = 0.0;
        String wh = "";
        String loc = "";
        if (ddfromwh.getSelectedItem() != null) {
         wh = ddfromwh.getSelectedItem().toString();
        }
        if (ddfromloc.getSelectedItem() != null) {
         loc = ddfromloc.getSelectedItem().toString();
        }
        
        mymodel.setNumRows(0);
        
        if (roData != null) {
        
        
        
        for (Object[] rowData : roData) {
            
            if (! rowData[3].toString().equals(wh) && ! wh.isBlank()) {
                continue;
            }
            if (! rowData[4].toString().equals(loc) && ! loc.isBlank()) {
                continue;
            }
            if (cbzero.isSelected() && bsParseDouble(rowData[7].toString()) == 0) {
                continue;
            }
            qty = qty + bsParseDouble(rowData[7].toString());
            i++;
            mymodel.addRow(rowData); 
        }
        labelcount.setText(String.valueOf(i));
        labelcount.setText(String.valueOf(i));
        labelqty.setText(String.valueOf(qty));
        
        }          
        roData = null;
    }   
       
    public void clearAll() {
        mymodel.setRowCount(0);
        ddclass.setSelectedIndex(0);
        cbzero.setSelected(false);
        tbserial.setText("");
        ddfromitem.setSelectedIndex(0);
        ddtoitem.setSelectedIndex(ddtoitem.getItemCount() - 1);
        ddfromwh.setSelectedIndex(0);
        ddfromloc.setSelectedIndex(0);
    }
    
    public void initvars(String[] arg) {
       executeTask("dataInit", null);
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
        labelcount = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        labelqty = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        tablepanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablereport = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        cbzero = new javax.swing.JCheckBox();
        btprint = new javax.swing.JButton();
        ddclass = new javax.swing.JComboBox<>();
        ddtoitem = new javax.swing.JComboBox<>();
        btRun = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        ddfromitem = new javax.swing.JComboBox<>();
        ddfromwh = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox<>();
        ddfromloc = new javax.swing.JComboBox<>();
        tbcsv = new javax.swing.JButton();
        tbserial = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        btclear = new javax.swing.JButton();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setName("panelmain"); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(1211, 539));

        labelcount.setText("0");

        jLabel7.setText("Count");
        jLabel7.setName("lblcount"); // NOI18N

        labelqty.setText("0");

        jLabel8.setText("Qty");
        jLabel8.setName("lblqty"); // NOI18N

        tablepanel.setLayout(new javax.swing.BoxLayout(tablepanel, javax.swing.BoxLayout.LINE_AXIS));

        tablereport.setAutoCreateRowSorter(true);
        tablereport.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tablereport);

        tablepanel.add(jScrollPane1);

        jLabel2.setText("Warehouse");

        jLabel4.setText("To Item");
        jLabel4.setName("lbltoitem"); // NOI18N

        jLabel1.setText("From Item");
        jLabel1.setName("lblfromitem"); // NOI18N

        cbzero.setText("Suppress Zero Inventory");

        btprint.setText("Print/PDF");
        btprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintActionPerformed(evt);
            }
        });

        ddclass.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "A", "M", "P" }));

        btRun.setText("Run");
        btRun.setName("btrun"); // NOI18N
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jLabel5.setText("Location");

        jLabel6.setText("Class");

        jLabel3.setText("Site");

        tbcsv.setText("CSV");
        tbcsv.setName("btcsv"); // NOI18N
        tbcsv.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbcsvActionPerformed(evt);
            }
        });

        jLabel9.setText("Serial Number");

        btclear.setText("Clear");
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ddsite, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ddclass, 0, 96, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tbserial)
                    .addComponent(ddfromitem, 0, 128, Short.MAX_VALUE)
                    .addComponent(ddtoitem, 0, 128, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ddfromwh, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddfromloc, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btRun)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btclear)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbcsv)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btprint))
                    .addComponent(cbzero))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(ddfromwh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(ddfromitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btRun)
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(tbcsv)
                    .addComponent(btprint)
                    .addComponent(btclear))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(ddtoitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddfromloc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(ddclass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(cbzero))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbserial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(283, 283, 283)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelqty, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7)
                            .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(labelqty, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(29, 29, 29))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 1214, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btRunActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btRunActionPerformed
      executeTask("getInvBrowseView", null);
    }//GEN-LAST:event_btRunActionPerformed

    private void tbcsvActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbcsvActionPerformed
        if (tablereport != null)
        OVData.exportCSV(tablereport);
    }//GEN-LAST:event_tbcsvActionPerformed

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed
        //OVData.printJTableToJasper("Quote Report", tablereport, "genericJTableL8B.jasper" );
         if (tablereport != null && mymodel.getRowCount() > 0) {
            String[] rec;
            String[] columnnames = new String[12];
            List<Object[]> list = new ArrayList<>();
            for (int j = 0; j < tablereport.getRowCount(); j++) {
                 rec = new String[]{tablereport.getValueAt(j, 0).toString(),
                   tablereport.getValueAt(j, 1).toString(),
                   tablereport.getValueAt(j, 2).toString(),
                   tablereport.getValueAt(j, 3).toString(),
                   tablereport.getValueAt(j, 4).toString(),
                   tablereport.getValueAt(j, 5).toString(),
                   tablereport.getValueAt(j, 6).toString(),
                   tablereport.getValueAt(j, 7).toString()}; 
                 list.add(rec);
             }
            HashMap hm = new HashMap();
            hm.put("REPORT_TITLE", "Quote Report");
            hm.put("REPORT_RESOURCE_BUNDLE", bsmf.MainFrame.tags);
            for (int j = 0; j < tablereport.getColumnCount(); j++) {
               hm.put("d" + j,  tablereport.getColumnName(j));
               columnnames[j] = "COLUMN_" + (j);
            }
            JRDataSource datasource = new ListOfArrayDataSource(list, columnnames);
            // assumes explicit jasper file name is larger than 3 chars.....if 3 chars or less...then must be key based L8, L8C, etc
            // type = "L8C";  ...or type = genericJTableL8.jasper
            // String jasperfile = (type.length() > 3) ? jasperfile = type  : OVData.getCodeValueByCodeKey("jasper", type)  ;
            Path template = FileSystems.getDefault().getPath(cleanDirString(getSystemJasperDirectory()) + "genericJTableL8B.jasper");
            JasperPrint jasperPrint; 
            try {
             jasperPrint = JasperFillManager.fillReport(template.toString(), hm, datasource );
             JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
               jasperViewer.setVisible(true);
                    jasperViewer.setTitle("Viewer");
                    jasperViewer.setIconImage(null);
                    jasperViewer.setFitPageZoomRatio();
               //  JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/ivprt.pdf");
           } catch (JRException ex) {
               MainFrame.bslog(ex);
           }
        }
       
    }//GEN-LAST:event_btprintActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        clearAll();
    }//GEN-LAST:event_btclearActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRun;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btprint;
    private javax.swing.JCheckBox cbzero;
    private javax.swing.JComboBox<String> ddclass;
    private javax.swing.JComboBox<String> ddfromitem;
    private javax.swing.JComboBox<String> ddfromloc;
    private javax.swing.JComboBox<String> ddfromwh;
    private javax.swing.JComboBox<String> ddsite;
    private javax.swing.JComboBox<String> ddtoitem;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelcount;
    private javax.swing.JLabel labelqty;
    private javax.swing.JPanel tablepanel;
    private javax.swing.JTable tablereport;
    private javax.swing.JButton tbcsv;
    private javax.swing.JTextField tbserial;
    // End of variables declaration//GEN-END:variables
}
