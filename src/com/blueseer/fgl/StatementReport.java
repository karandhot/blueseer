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

package com.blueseer.fgl;

import bsmf.MainFrame;
import static bsmf.MainFrame.bslog;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
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
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.view.JasperViewer;
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
import com.blueseer.adm.admData;
import static com.blueseer.fgl.fglData.getGLCalYearsRange;
import static com.blueseer.utl.BlueSeerUtils.bsFormatInt;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.jsonToData;
import static com.blueseer.utl.BlueSeerUtils.sendServerPost;
import static com.blueseer.utl.OVData.getSiteLogo;
import java.sql.Connection;
import java.text.DecimalFormatSymbols;
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

/**
 *
 * @author vaughnte
 */
public class StatementReport extends javax.swing.JPanel {
 
    Object[][] rData;
    ArrayList<String[]> initDataSets = new ArrayList<>();
    String defaultcurrency = "";
    String[] glCalDateArray;
    boolean isLoad = false;
    double activetotal = 0;
    
     MyTableModel mymodel = new StatementReport.MyTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("description"), 
                            getGlobalColumnTag("definition"),  
                            getGlobalColumnTag("amount")});
    
    
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
        
        String status = (String)table.getModel().getValueAt(table.convertRowIndexToModel(row), 8);  // 8 = status column
        
         if ("error".equals(status)) {
            c.setBackground(Color.red);
            c.setForeground(Color.WHITE);
        } else if ("close".equals(status)) {
            c.setBackground(Color.blue);
            c.setForeground(Color.WHITE);
        } else if ("backorder".equals(status)) {
            c.setBackground(Color.yellow);
            c.setForeground(Color.BLACK);
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
        
        
    public StatementReport() {
        initComponents();
        setLanguageTags(this);
    }

    public void setLanguageTags(Object myobj) {
      // lblaccount.setText(labels.getString("LedgerAcctMstrPanel.labels.lblaccount"));
      
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
    
    public void executeTask(BlueSeerUtils.dbaction x, String[] y) { 
      
        class Task extends SwingWorker<String[], Void> {
          String type = "";
          String[] key = null; 
          /*
          String key = "";
          int row = 0;
          String site = "";
          */
          public Task(BlueSeerUtils.dbaction type, String[] key) { 
              this.type = type.name();
              this.key = key;
          } 
           
        @Override
        public String[] doInBackground() throws Exception {
            String[] message = new String[2];
            
            switch(this.type) {
                case "init":
                    message = getInitialization();
                    break;
                    
                case "run":
                    if (this.key[0].equals("getGLICBrowseView")) {
                      message = getGLICBrowseView();
                    } 
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
            if (this.type.equals("init")) {
                    done_Initialization();
            }
            if (this.type.equals("run")) {
                    if (this.key != null && this.key[0].equals("getGLICBrowseView")) {
                      done_getGLICBrowseView();
                    } 
                    
            } 
           
            } catch (Exception e) {
                MainFrame.bslog(e);
            } 
           
        }
    }  
      
       BlueSeerUtils.startTask(new String[]{"", getMessageTag(1189)});
       Task z = new Task(x, y);  
       z.execute(); 
       
    }
    
    
    public void initvars(String[] arg) {
        
        isLoad = true;
        mymodel.setRowCount(0);
        
        mytable.getColumnModel().getColumn(2).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(OVData.getDefaultCurrency())));
        
        java.util.Date now = new java.util.Date();
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat dfyear = new SimpleDateFormat("yyyy");
        DateFormat dfperiod = new SimpleDateFormat("M");
         
        ddyear.removeAllItems();
        ArrayList<String> years = getGLCalYearsRange();
        for (String y : years) {
            ddyear.addItem(y);
        }
        ddyear.setSelectedItem(bsNumber(dfyear.format(now)));
            
        ddperfrom.removeAllItems();
        for (int i = 1 ; i <= 12; i++) {
            ddperfrom.addItem(bsFormatInt(i));
        }
        
        ddperto.removeAllItems();
        for (int i = 1 ; i <= 12; i++) {
            ddperto.addItem(bsFormatInt(i));
        }
       
        glCalDateArray = fglData.getGLCalForDate(now);
        ddperfrom.setSelectedItem(bsNumber(glCalDateArray[1]));
        ddperto.setSelectedItem(bsNumber(glCalDateArray[1]));
        ArrayList<String> list = fglData.getGLCalForPeriodRange(bsParseInt(ddyear.getSelectedItem().toString()), bsParseInt(ddperfrom.getSelectedItem().toString()), bsParseInt(ddperto.getSelectedItem().toString()));
        datelabel.setText(list.get(0) + " To " + list.get(1));
        
        isLoad = false;
        
        executeTask(BlueSeerUtils.dbaction.init, null);
        
        
    }
    
    public String[] getInitialization() {
        initDataSets = admData.getInitMinimum(this.getClass().getName(), bsmf.MainFrame.userid, "icprofiles");
        if (initDataSets.isEmpty()) {
           return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.dataInitError}; 
        } else {
           return new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess}; 
        }
        
    }    
    
    public void done_Initialization() {
        
        ddsite.removeAllItems();
        ddprofile.removeAllItems();
        String defaultsite = "";
        for (String[] s : initDataSets) {
            if (s[0].equals("site")) {
              defaultsite = s[1];  
            }
                      
            if (s[0].equals("sites")) {
              ddsite.addItem(s[1]); 
            }
            
            if (s[0].equals("icprofiles")) {
              ddprofile.addItem(s[1]); 
            }
            
            if (s[0].equals("currency")) {
              defaultcurrency = s[1];  
            }
        }
        
        if (ddsite.getItemCount() > 0) {
            ddsite.setSelectedItem(defaultsite);
        }
        
        mymodel.setNumRows(0);
           
        mytable.setModel(mymodel);
        mytable.getTableHeader().setReorderingAllowed(false);
        mytable.getColumnModel().getColumn(2).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(defaultcurrency)));
       
       
    }

    public String[] getGLICBrowseView() {
       
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");        
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getGLICBrowseView"});
            list.add(new String[]{"param1", ddprofile.getSelectedItem().toString()});
            list.add(new String[]{"param2", ddsite.getSelectedItem().toString()});
            list.add(new String[]{"param3", ddyear.getSelectedItem().toString()});
            list.add(new String[]{"param4", ddperfrom.getSelectedItem().toString()});
            list.add(new String[]{"param5", ddperto.getSelectedItem().toString()});
            try {
                jsonString = sendServerPost(list, "", null, "dataServFIN"); 
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.getMessageTag(1010, "getInvoiceBrowseView")};
            }
        } else {
            jsonString = fglData.getGLICBrowseView(ddprofile.getSelectedItem().toString(), 
                    ddsite.getSelectedItem().toString(), 
                    ddyear.getSelectedItem().toString(), 
                    ddperfrom.getSelectedItem().toString(),
                    ddperto.getSelectedItem().toString());
        }
         rData = jsonToData(jsonString);
       
      return new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getMessageTag(1125)};
   }
    
    public void done_getGLICBrowseView() {
        double totsales = 0;
        double totopen = 0;
        mymodel.setNumRows(0);
        mytable.setModel(mymodel);
        if (rData != null) {
            
        
            int i = 0;
            if (rData.length > 0) {
                for (Object[] rowData : rData) {
                 rowData[2] = bsParseDouble(rowData[2].toString());    
                 totsales = totsales + bsParseDouble(rowData[2].toString()); 
                 if (rowData[0].equals("ACTIVE CATEGORIES")) {
                     activetotal = bsParseDouble(rowData[2].toString());
                     continue;
                 }
                 mymodel.addRow(rowData);  
                 i++;
                } 
            }
          labelcount.setText(String.valueOf(i));
          labeltotal.setText(currformatDouble(totsales));
        }
        rData = null;
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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btRun = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        mytable = new javax.swing.JTable();
        labelcount = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        labeltotal = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        ddyear = new javax.swing.JComboBox();
        ddperfrom = new javax.swing.JComboBox();
        datelabel = new javax.swing.JLabel();
        ddsite = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        btprint = new javax.swing.JButton();
        ddprofile = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        ddperto = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setName("panelmain"); // NOI18N

        jLabel2.setText("Year");
        jLabel2.setName("lblyear"); // NOI18N

        jLabel3.setText("From Period:");
        jLabel3.setName("lblperfrom"); // NOI18N

        btRun.setText("Run");
        btRun.setName("btrun"); // NOI18N
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        mytable.setAutoCreateRowSorter(true);
        mytable.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(mytable);

        labelcount.setText("0");

        jLabel7.setText("Lines:");
        jLabel7.setName("lbloperating"); // NOI18N

        labeltotal.setBackground(new java.awt.Color(195, 129, 129));
        labeltotal.setText("0");

        jLabel9.setText("Total:");
        jLabel9.setName("lblebitda"); // NOI18N

        ddyear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddyearActionPerformed(evt);
            }
        });

        ddperfrom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddperfromActionPerformed(evt);
            }
        });

        jLabel4.setText("Site");
        jLabel4.setName("lblsite"); // NOI18N

        btprint.setText("Print");
        btprint.setName("btprint"); // NOI18N
        btprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintActionPerformed(evt);
            }
        });

        jLabel5.setText("Profile:");
        jLabel5.setName("lblprofile"); // NOI18N

        ddperto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddpertoActionPerformed(evt);
            }
        });

        jLabel6.setText("To Period:");
        jLabel6.setName("lblperto"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ddsite, 0, 86, Short.MAX_VALUE)
                    .addComponent(ddyear, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(48, 48, 48)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ddperfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddperto, 0, 1, Short.MAX_VALUE))
                .addGap(20, 20, 20)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ddprofile, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btRun)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btprint)
                .addGap(28, 28, 28)
                .addComponent(datelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 277, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(labeltotal, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(36, 36, 36))
            .addComponent(jScrollPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddyear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(ddperfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ddperto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel6))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel7)
                                .addComponent(labelcount, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(datelabel, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labeltotal, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btRun)
                        .addComponent(btprint)
                        .addComponent(ddprofile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 548, Short.MAX_VALUE))
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
    
    executeTask(BlueSeerUtils.dbaction.run, new String[]{"getGLICBrowseView",""});
    
    /*
       try {
            Connection con = null;
            if (ds != null) {
            con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
                int qty = 0;
                double dol = 0;
                int i = 0;
               
                
               
               
                 
               //  ScrapReportPanel.MyTableModel mymodel = new ScrapReportPanel.MyTableModel(new Object[][]{},
               //         new String[]{"Acct", "Description", "Amt"});
               // tablescrap.setModel(mymodel);
               
                   
                 mymodel.setNumRows(0);
                   
               
                mytable.setModel(mymodel);
                mytable.getColumnModel().getColumn(2).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(OVData.getDefaultCurrency())));
             
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");

                 
                 double sales = 0;
                 double cogs = 0;
                 double stdmargin  = 0;
                 double mtlvar = 0;
                 double lbrvar = 0;
                 double bdnvar = 0;
                 double mfggrossmargin = 0;
                 double prodeng = 0;
                 double marketingandsales = 0;
                 double grossmargin = 0;
                 double generalandadmin = 0;
                 double profitbeforealloc = 0;
                 double interest = 0;
                 double alloc = 0;
                 double mgtfees = 0;
                 double bankfees = 0;
                 double other = 0;
                 double opprofitbeforetaxes = 0;
                 double ebitda = 0;
                 double depreciation = 0;
                 
                 String startacct = "";
                 String endacct = "";
                 ArrayList excludeaccts;
                 ArrayList includeaccts;
                 
                 
                 
                 
                 
                 // Sales
                 res = st.executeQuery("select glic_start, glic_end from glic_def where glic_name = 'sales';");
                 while (res.next()) {
                     startacct = res.getString("glic_start");
                     endacct = res.getString("glic_end");
                 }
                 res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct >= " + "'" + startacct + "'" + " AND " +
                        " acb_acct <= " + "'" + endacct + "'" + " AND " +
                        " acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " acb_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + 
                        " AND acb_per = " + "'" + ddper.getSelectedItem().toString() + "'" +
                        ";");
                
                       while (res.next()) {
                          sales += res.getDouble("sum");
                       }
                       
                        // now lets back out accts excluded
                      excludeaccts = fglData.getGLICAccts("sales", "out");
                       for (int k = 0; k < excludeaccts.size(); k++) {
                           sales = fglData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), sales);
                       }
                       // now add accts that are included
                       includeaccts = fglData.getGLICAccts("sales", "in");
                       for (int k = 0; k < includeaccts.size(); k++) {
                           sales = fglData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), sales);
                       }
                       
                       
                       sales = (-1 * sales);
                       
                       
                       
                     mymodel.addRow(new Object[] { "Sales", "", sales});
                 
                     
                     
                     // COGS
                     res = st.executeQuery("select glic_start, glic_end from glic_def where glic_name = 'cogs';");
                 while (res.next()) {
                     startacct = res.getString("glic_start");
                     endacct = res.getString("glic_end");
                 }
                     
                   res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct >= " + "'" + startacct + "'" + " AND " +
                        " acb_acct <= " + "'" + endacct + "'" + " AND " +
                        " acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " acb_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + 
                        " AND acb_per = " + "'" + ddper.getSelectedItem().toString() + "'" +
                        ";");
                
                       while (res.next()) {
                          cogs += res.getDouble("sum");
                       }
                          // now lets back out accts excluded
                      excludeaccts = fglData.getGLICAccts("cogs", "out");
                       for (int k = 0; k < excludeaccts.size(); k++) {
                           cogs = fglData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), cogs);
                       }
                       // now add accts that are included
                       includeaccts = fglData.getGLICAccts("cogs", "in");
                       for (int k = 0; k < includeaccts.size(); k++) {
                           cogs = fglData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), cogs);
                       }
                     mymodel.addRow(new Object[] { "Cost Of Goods", "", cogs});  
                  
                     // Standard Margin = Sales - Cogs
                     stdmargin = sales - cogs;
                     mymodel.addRow(new Object[] { "Standard Margin", "Sales less COGS", stdmargin}); 
                   
                     
                         // Mtl Variance
                     res = st.executeQuery("select glic_start, glic_end from glic_def where glic_name = 'mtlvar';");
                 while (res.next()) {
                     startacct = res.getString("glic_start");
                     endacct = res.getString("glic_end");
                 }
                   res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct >= " + "'" + startacct + "'" + " AND " +
                        " acb_acct <= " + "'" + endacct + "'" + " AND " +
                        " acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " acb_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + 
                        " AND acb_per = " + "'" + ddper.getSelectedItem().toString() + "'" +
                        ";");
                
                       while (res.next()) {
                          mtlvar += res.getDouble("sum");
                       }
                       // now lets back out accts excluded
                       excludeaccts = fglData.getGLICAccts("mtlvar", "out");
                       for (int k = 0; k < excludeaccts.size(); k++) {
                           mtlvar = fglData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), mtlvar);
                       }
                       // now add accts that are included
                       includeaccts = fglData.getGLICAccts("mtlvar", "in");
                       for (int k = 0; k < includeaccts.size(); k++) {
                           mtlvar = fglData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), mtlvar);
                       }
                     mymodel.addRow(new Object[] { "Matl Variance", "", mtlvar});   
                     
                 


                    // Labor Variance
                     res = st.executeQuery("select glic_start, glic_end from glic_def where glic_name = 'lbrvar';");
                 while (res.next()) {
                     startacct = res.getString("glic_start");
                     endacct = res.getString("glic_end");
                 }
                   res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct >= " + "'" + startacct + "'" + " AND " +
                        " acb_acct <= " + "'" + endacct + "'" + " AND " +
                        " acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " acb_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + 
                        " AND acb_per = " + "'" + ddper.getSelectedItem().toString() + "'" +
                        ";");
                
                       while (res.next()) {
                          lbrvar += res.getDouble("sum");
                       }
                      // now lets back out accts excluded
                       excludeaccts = fglData.getGLICAccts("lbrvar", "out");
                       for (int k = 0; k < excludeaccts.size(); k++) {
                           lbrvar = fglData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), lbrvar);
                       }
                       // now add accts that are included
                       includeaccts = fglData.getGLICAccts("lbrvar", "in");
                       for (int k = 0; k < includeaccts.size(); k++) {
                           lbrvar = fglData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), lbrvar);
                       }
                  mymodel.addRow(new Object[] { "Labor Variance", "", lbrvar});  
                     
                 
                  

                      // Burden Variance
                     res = st.executeQuery("select glic_start, glic_end from glic_def where glic_name = 'bdnvar';");
                 while (res.next()) {
                     startacct = res.getString("glic_start");
                     endacct = res.getString("glic_end");
                 }
                   res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct >= " + "'" + startacct + "'" + " AND " +
                        " acb_acct <= " + "'" + endacct + "'" + " AND " +
                        " acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " acb_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + 
                        " AND acb_per = " + "'" + ddper.getSelectedItem().toString() + "'" +
                        ";");
                
                       while (res.next()) {
                          bdnvar += res.getDouble("sum");
                       }
                        // now lets back out accts excluded
                       excludeaccts = fglData.getGLICAccts("bdnvar", "out");
                       for (int k = 0; k < excludeaccts.size(); k++) {
                           bdnvar = fglData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), bdnvar);
                       }
                       // now add accts that are included
                       includeaccts = fglData.getGLICAccts("bdnvar", "in");
                       for (int k = 0; k < includeaccts.size(); k++) {
                           bdnvar = fglData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), bdnvar);
                       }
                     mymodel.addRow(new Object[] { "Burden Variance", "", bdnvar});   
                     
                     
                     
                     
                     
                     
                      // MFG Gross Margin
                     mfggrossmargin = stdmargin - mtlvar - lbrvar - bdnvar;
                     mymodel.addRow(new Object[] { "MFG Gross Margin", "Standard Margin Less Variance", mfggrossmargin}); 
                     
                     
                     //ProdEng
                          res = st.executeQuery("select glic_start, glic_end from glic_def where glic_name = 'prodeng';");
                 while (res.next()) {
                     startacct = res.getString("glic_start");
                     endacct = res.getString("glic_end");
                 }
                   res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct >= " + "'" + startacct + "'" + " AND " +
                        " acb_acct <= " + "'" + endacct + "'" + " AND " +
                        " acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " acb_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + 
                        " AND acb_per = " + "'" + ddper.getSelectedItem().toString() + "'" +
                        ";");
                
                
                       while (res.next()) {
                          prodeng += res.getDouble("sum");
                       }
                         // now lets back out accts excluded
                       excludeaccts = fglData.getGLICAccts("prodeng", "out");
                       for (int k = 0; k < excludeaccts.size(); k++) {
                           prodeng = fglData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), prodeng);
                       }
                       // now add accts that are included
                       includeaccts = fglData.getGLICAccts("prodeng", "in");
                       for (int k = 0; k < includeaccts.size(); k++) {
                           prodeng = fglData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), prodeng);
                       }
                     mymodel.addRow(new Object[] { "Product Engineering", "", prodeng});  
                     
                     
                       // Gross Margin
                     grossmargin = mfggrossmargin - prodeng;
                     mymodel.addRow(new Object[] { "Gross Margin", "MFG Gross Margin less Prod Eng Expense", grossmargin}); 
                     
                       //Marketing and sales
                          res = st.executeQuery("select glic_start, glic_end from glic_def where glic_name = 'mktsales';");
                 while (res.next()) {
                     startacct = res.getString("glic_start");
                     endacct = res.getString("glic_end");
                 }
                   res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct >= " + "'" + startacct + "'" + " AND " +
                        " acb_acct <= " + "'" + endacct + "'" + " AND " +
                        " acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " acb_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + 
                        " AND acb_per = " + "'" + ddper.getSelectedItem().toString() + "'" +
                        ";");
                
                
                       while (res.next()) {
                          marketingandsales += res.getDouble("sum");
                       }
                         // now lets back out accts excluded
                       excludeaccts = fglData.getGLICAccts("mktsales", "out");
                       for (int k = 0; k < excludeaccts.size(); k++) {
                           marketingandsales = fglData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), marketingandsales);
                       }
                       // now add accts that are included
                       includeaccts = fglData.getGLICAccts("mktsales", "in");
                       for (int k = 0; k < includeaccts.size(); k++) {
                           marketingandsales = fglData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), marketingandsales);
                       }
                     mymodel.addRow(new Object[] { "Sales and Marketing", "", marketingandsales});  
                     
                     
                     
                       //Gen and Admin
                          res = st.executeQuery("select glic_start, glic_end from glic_def where glic_name = 'g&a';");
                 while (res.next()) {
                     startacct = res.getString("glic_start");
                     endacct = res.getString("glic_end");
                 }
                   res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct >= " + "'" + startacct + "'" + " AND " +
                        " acb_acct <= " + "'" + endacct + "'" + " AND " +
                        " acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " acb_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + 
                        " AND acb_per = " + "'" + ddper.getSelectedItem().toString() + "'" +
                        ";");
                       while (res.next()) {
                          generalandadmin += res.getDouble("sum");
                       }
                         // now lets back out accts excluded
                       excludeaccts = fglData.getGLICAccts("g&a", "out");
                       for (int k = 0; k < excludeaccts.size(); k++) {
                           generalandadmin = fglData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), generalandadmin);
                       }
                       // now add accts that are included
                       includeaccts = fglData.getGLICAccts("g&a", "in");
                       for (int k = 0; k < includeaccts.size(); k++) {
                           generalandadmin = fglData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), generalandadmin);
                       }
                     mymodel.addRow(new Object[] { "General and Admin", "", generalandadmin});  
                     
                     
                        // Profit Before Allocation
                     profitbeforealloc = grossmargin - marketingandsales - generalandadmin;
                     mymodel.addRow(new Object[] { "Profit Before Allocations", "Gross Margin less SMG&A Expenses",  profitbeforealloc}); 
                     
                   

                        //Interest
                          res = st.executeQuery("select glic_start, glic_end from glic_def where glic_name = 'interest';");
                 while (res.next()) {
                     startacct = res.getString("glic_start");
                     endacct = res.getString("glic_end");
                 }
                   res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct >= " + "'" + startacct + "'" + " AND " +
                        " acb_acct <= " + "'" + endacct + "'" + " AND " +
                        " acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " acb_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + 
                        " AND acb_per = " + "'" + ddper.getSelectedItem().toString() + "'" +
                        ";");
                       while (res.next()) {
                          interest += res.getDouble("sum");
                       }
                          // now lets back out accts excluded
                       excludeaccts = fglData.getGLICAccts("interest", "out");
                       for (int k = 0; k < excludeaccts.size(); k++) {
                           interest = fglData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), interest);
                       }
                       // now add accts that are included
                       includeaccts = fglData.getGLICAccts("interest", "in");
                       for (int k = 0; k < includeaccts.size(); k++) {
                           interest = fglData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), interest);
                       }
                     mymodel.addRow(new Object[] { "Interest", "", interest});  
                     
                         //Allocations
                          res = st.executeQuery("select glic_start, glic_end from glic_def where glic_name = 'alloc';");
                 while (res.next()) {
                     startacct = res.getString("glic_start");
                     endacct = res.getString("glic_end");
                 }
                   res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct >= " + "'" + startacct + "'" + " AND " +
                        " acb_acct <= " + "'" + endacct + "'" + " AND " +
                        " acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " acb_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + 
                        " AND acb_per = " + "'" + ddper.getSelectedItem().toString() + "'" +
                        ";");
                       while (res.next()) {
                          alloc += res.getDouble("sum");
                       }
                          // now lets back out accts excluded
                       excludeaccts = fglData.getGLICAccts("alloc", "out");
                       for (int k = 0; k < excludeaccts.size(); k++) {
                           alloc = fglData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), alloc);
                       }
                       // now add accts that are included
                       includeaccts = fglData.getGLICAccts("alloc", "in");
                       for (int k = 0; k < includeaccts.size(); k++) {
                           alloc = fglData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), alloc);
                       }
                     mymodel.addRow(new Object[] { "Corporate Allocations", "", alloc}); 
                     
                            //Management Fees
                          res = st.executeQuery("select glic_start, glic_end from glic_def where glic_name = 'mgtfee';");
                 while (res.next()) {
                     startacct = res.getString("glic_start");
                     endacct = res.getString("glic_end");
                 }
                   res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct >= " + "'" + startacct + "'" + " AND " +
                        " acb_acct <= " + "'" + endacct + "'" + " AND " +
                        " acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " acb_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + 
                        " AND acb_per = " + "'" + ddper.getSelectedItem().toString() + "'" +
                        ";");
                       while (res.next()) {
                          mgtfees += res.getDouble("sum");
                       }
                           // now lets back out accts excluded
                       excludeaccts = fglData.getGLICAccts("mgtfee", "out");
                       for (int k = 0; k < excludeaccts.size(); k++) {
                           mgtfees = fglData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), mgtfees);
                       }
                       // now add accts that are included
                       includeaccts = fglData.getGLICAccts("mgtfee", "in");
                       for (int k = 0; k < includeaccts.size(); k++) {
                           mgtfees = fglData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), mgtfees);
                       }
                     mymodel.addRow(new Object[] { "Management Fees", "", mgtfees});  
                     
                     
                     
                               //Bank Fees
                          res = st.executeQuery("select glic_start, glic_end from glic_def where glic_name = 'bankfee';");
                 while (res.next()) {
                     startacct = res.getString("glic_start");
                     endacct = res.getString("glic_end");
                 }
                   res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct >= " + "'" + startacct + "'" + " AND " +
                        " acb_acct <= " + "'" + endacct + "'" + " AND " +
                        " acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " acb_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + 
                        " AND acb_per = " + "'" + ddper.getSelectedItem().toString() + "'" +
                        ";");
                       while (res.next()) {
                          bankfees += res.getDouble("sum");
                       }
                            // now lets back out accts excluded
                       excludeaccts = fglData.getGLICAccts("bankfee", "out");
                       for (int k = 0; k < excludeaccts.size(); k++) {
                           bankfees = fglData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), bankfees);
                       }
                       // now add accts that are included
                       includeaccts = fglData.getGLICAccts("bankfee", "in");
                       for (int k = 0; k < includeaccts.size(); k++) {
                           bankfees = fglData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), bankfees);
                       }
                     mymodel.addRow(new Object[] { "Bank Fees", "", bankfees});  
                     
                     
                                 //Other income/expense
                          res = st.executeQuery("select glic_start, glic_end from glic_def where glic_name = 'otherie';");
                 while (res.next()) {
                     startacct = res.getString("glic_start");
                     endacct = res.getString("glic_end");
                 }
                   res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct >= " + "'" + startacct + "'" + " AND " +
                        " acb_acct <= " + "'" + endacct + "'" + " AND " +
                        " acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " acb_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + 
                        " AND acb_per = " + "'" + ddper.getSelectedItem().toString() + "'" +
                        ";");
                   while (res.next()) {
                          other += res.getDouble("sum");
                       }
                         // now lets back out accts excluded
                       excludeaccts = fglData.getGLICAccts("otherie", "out");
                       for (int k = 0; k < excludeaccts.size(); k++) {
                           other = fglData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), other);
                       }
                       // now add accts that are included
                       includeaccts = fglData.getGLICAccts("otherie", "in");
                       for (int k = 0; k < includeaccts.size(); k++) {
                           other = fglData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), other);
                       }
                     mymodel.addRow(new Object[] { "Other Income/Expense", "", other});  
                     
                     
                     
                     
                       // Operational Profit before taxes
                     opprofitbeforetaxes = profitbeforealloc -interest - alloc - mgtfees - bankfees - other;
                     mymodel.addRow(new Object[] { "Operating Profit Before Taxes", "Op Profit  less Int&Alloc&Fees&Other", opprofitbeforetaxes});  
                     
                                   //depreciation
                          res = st.executeQuery("select glic_start, glic_end from glic_def where glic_name = 'depreciation';");
                 while (res.next()) {
                     startacct = res.getString("glic_start");
                     endacct = res.getString("glic_end");
                 }
                   res = st.executeQuery("select sum(acb_amt) as sum from acb_mstr where " +
                        " acb_acct >= " + "'" + startacct + "'" + " AND " +
                        " acb_acct <= " + "'" + endacct + "'" + " AND " +
                        " acb_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " acb_year = " + "'" + ddyear.getSelectedItem().toString() + "'" + 
                        " AND acb_per = " + "'" + ddper.getSelectedItem().toString() + "'" +
                        ";");
                       while (res.next()) {
                          depreciation += res.getDouble("sum");
                       }
                         // now lets back out accts excluded
                       excludeaccts = fglData.getGLICAccts("depreciation", "out");
                       for (int k = 0; k < excludeaccts.size(); k++) {
                           depreciation = fglData.getGLICBackOut(excludeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), depreciation);
                       }
                       // now add accts that are included
                       includeaccts = fglData.getGLICAccts("depreciation", "in");
                       for (int k = 0; k < includeaccts.size(); k++) {
                           depreciation = fglData.getGLICAddIn(includeaccts.get(k).toString(), ddsite.getSelectedItem().toString(), ddyear.getSelectedItem().toString(), ddper.getSelectedItem().toString(), depreciation);
                       }
                     mymodel.addRow(new Object[] { "Depreciation", "", depreciation});
                     
                     
                     
                       // EBITDA
                     ebitda = opprofitbeforetaxes + depreciation + interest + alloc + mgtfees + bankfees + other;
                     mymodel.addRow(new Object[] { "EBITDA","", ebitda});  
                     
                     
                labeldollar.setText(String.valueOf(currformatDouble(ebitda)));
                labelcount.setText(String.valueOf(currformatDouble(opprofitbeforetaxes)));
               
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            }
            con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
       */
    }//GEN-LAST:event_btRunActionPerformed

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed
       try {
                DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                HashMap hm = new HashMap();
                String logo = getSiteLogo(ddsite.getSelectedItem().toString());
                hm.put("REPORT_RESOURCE_BUNDLE", bsmf.MainFrame.tags);
                hm.put("imagepath", "images/" + logo);
                hm.put("ReportTitle", ddprofile.getSelectedItem().toString());
                hm.put("daterange", datelabel.getText());
                hm.put("yearandperiod", ddyear.getSelectedItem().toString() + "   " + ddperfrom.getSelectedItem().toString() + " to " + ddperto.getSelectedItem().toString());
                hm.put("activetotal", activetotal);
               // res = st.executeQuery("select shd_id, sh_cust, shd_po, shd_item, shd_qty, shd_netprice, cm_code, cm_name, cm_line1, cm_line2, cm_city, cm_state, cm_zip, concat(cm_city, \" \", cm_state, \" \", cm_zip) as st_citystatezip, site_desc from ship_det inner join ship_mstr on sh_id = shd_id inner join cm_mstr on cm_code = sh_cust inner join site_mstr on site_site = sh_site where shd_id = '1848' ");
               // JRResultSetDataSource jasperReports = new JRResultSetDataSource(res);
                File mytemplate = new File("jasper/incomestatement.jasper");
                 
                JasperPrint jasperPrint = JasperFillManager.fillReport(mytemplate.getPath(), hm, new JRTableModelDataSource(mytable.getModel()) );
               // JasperExportManager.exportReportToPdfFile(jasperPrint,"temp/is.pdf");
         
            JasperViewer jasperViewer = new JasperViewer(jasperPrint, false);
            jasperViewer.setVisible(true);
          
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btprintActionPerformed

    private void ddperfromActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddperfromActionPerformed
        if (! isLoad) {
            if (! isLoad) {
            if (ddperfrom.getItemCount() > 0 && ddperto.getItemCount() > 0 && ddyear.getItemCount() > 0) {
                ArrayList<String> list = fglData.getGLCalForPeriodRange(bsParseInt(ddyear.getSelectedItem().toString()), bsParseInt(ddperfrom.getSelectedItem().toString()), bsParseInt(ddperto.getSelectedItem().toString()));
                if (list.isEmpty()) {
                    bsmf.MainFrame.show("No GL Calendar records for that year and period range");
                    return;
                }
                if (list != null && list.size() == 2) {
                datelabel.setText(list.get(0) + " To " + list.get(1));
                }
            }
        }
        }
    }//GEN-LAST:event_ddperfromActionPerformed

    private void ddpertoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddpertoActionPerformed
        if (! isLoad) {
            if (! isLoad) {
            if (ddperfrom.getItemCount() > 0 && ddperto.getItemCount() > 0 && ddyear.getItemCount() > 0) {
                ArrayList<String> list = fglData.getGLCalForPeriodRange(bsParseInt(ddyear.getSelectedItem().toString()), bsParseInt(ddperfrom.getSelectedItem().toString()), bsParseInt(ddperto.getSelectedItem().toString()));
                if (list.isEmpty()) {
                    bsmf.MainFrame.show("No GL Calendar records for that year and period range");
                    return;
                }
                if (list != null && list.size() == 2) {
                datelabel.setText(list.get(0) + " To " + list.get(1));
                }
            }
        }
        }
    }//GEN-LAST:event_ddpertoActionPerformed

    private void ddyearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddyearActionPerformed
        if (! isLoad) {
            if (ddperfrom.getItemCount() > 0 && ddperto.getItemCount() > 0 && ddyear.getItemCount() > 0) {
                ArrayList<String> list = fglData.getGLCalForPeriodRange(bsParseInt(ddyear.getSelectedItem().toString()), bsParseInt(ddperfrom.getSelectedItem().toString()), bsParseInt(ddperto.getSelectedItem().toString()));
                if (list.isEmpty()) {
                    bsmf.MainFrame.show("No GL Calendar records for that year and period range");
                    return;
                }
                if (list != null && list.size() == 2) {
                datelabel.setText(list.get(0) + " To " + list.get(1));
                }
            }
        }
    }//GEN-LAST:event_ddyearActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRun;
    private javax.swing.JButton btprint;
    private javax.swing.JLabel datelabel;
    private javax.swing.JComboBox ddperfrom;
    private javax.swing.JComboBox<String> ddperto;
    private javax.swing.JComboBox<String> ddprofile;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JComboBox ddyear;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel labelcount;
    private javax.swing.JLabel labeltotal;
    private javax.swing.JTable mytable;
    // End of variables declaration//GEN-END:variables
}
