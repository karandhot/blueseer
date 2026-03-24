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


import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.adm.admData;
import com.blueseer.prd.ProdDetRpt;
import com.blueseer.sch.schData;
import static com.blueseer.sch.schData.getPlanMstr;
import com.blueseer.sch.schData.plan_mstr;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.TableColumn;

/**
 *
 * @author vaughnte
 */
public class JobPostScan extends javax.swing.JPanel {

String partnumber = "";
String custpart = "";
int serialno = 0;
String serialno_str = "";
String quantity = "";

boolean isLoad = false;
ArrayList<String[]> initDataSets = new ArrayList<>();
String defaultSite = "";
String defaultCurrency = "";

plan_mstr pm = null;
    
    
    /**
     * Creates new form CarrierMaintPanel
     */
    public JobPostScan() {
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
            
            
            
            
            switch(this.action) {
                case "dataInit":
                    message = getInitialization();
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
    
    public void setPanelComponentState(Object myobj, boolean b) {
        JPanel panel = null;
        JTabbedPane tabpane = null;
        if (myobj instanceof JPanel) {
            panel = (JPanel) myobj;
        } else if (myobj instanceof JTabbedPane) {
           tabpane = (JTabbedPane) myobj; 
        } else {
            return;
        }
        
        if (panel != null) {
        panel.setEnabled(b);
        Component[] components = panel.getComponents();
        
            for (Component component : components) {
                 // start reset background colors
                if (component instanceof JTextField) {
                    if (((JTextField) component).isEditable()) {
                     component.setBackground(Color.WHITE);
                    } else {
                     component.setBackground(bsmf.MainFrame.nonEditableColor);   
                    }
                }
                if (component instanceof JComboBox) {
                     component.setBackground(bsmf.MainFrame.ddbgcolor);
                }
                // end reset background colors
                if (component instanceof JLabel || component instanceof JTable ) {
                    continue;
                }
                if (component instanceof JPanel) {
                    setPanelComponentState((JPanel) component, b);
                }
                if (component instanceof JTabbedPane) {
                    setPanelComponentState((JTabbedPane) component, b);
                }
                
                component.setEnabled(b);
            }
        }
            if (tabpane != null) {
                tabpane.setEnabled(b);
                Component[] componentspane = tabpane.getComponents();
                for (Component component : componentspane) {
                    if (component instanceof JLabel || component instanceof JTable ) {
                        continue;
                    }
                    if (component instanceof JPanel) {
                        setPanelComponentState((JPanel) component, b);
                    }
                    component.setEnabled(b);
                }
            }
    } 
    
    
    public void initvars(String[] arg) {
        executeTask("dataInit", null); 
    }
    
    public String[] getInitialization() {
        initDataSets = admData.getInitMinimum(this.getClass().getName(), bsmf.MainFrame.userid, "cells");
        if (initDataSets.isEmpty()) {
           return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.dataInitError}; 
        } else {
           return new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess}; 
        }
    }  
    
    public void done_Initialization() {
        setPanelComponentState(this, true);
        java.util.Date now = new java.util.Date();        
        tbqty.setText("");
        tbscan.setText("");
        ddcell.removeAllItems(); 
        
        for (String[] s : initDataSets) {
            if (s[0].equals("site")) {
              defaultSite = s[1]; 
            }
           
            if (s[0].equals("currency")) {
              defaultCurrency = s[1]; 
            }
            if (s[0].equals("cells")) {
              ddcell.addItem(s[1]); 
            }
        }        
        
        
       tbscan.requestFocus();

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
        ddcell = new javax.swing.JComboBox();
        tbqty = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        tbscan = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btcommit = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Job Scan"));
        jPanel1.setName("panelmain"); // NOI18N

        tbqty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbqtyFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbqtyFocusLost(evt);
            }
        });

        jLabel4.setText("Quantity");
        jLabel4.setName("lblqty"); // NOI18N

        tbscan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tbscanActionPerformed(evt);
            }
        });
        tbscan.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tbscanFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbscanFocusLost(evt);
            }
        });

        jLabel5.setText("Scan");
        jLabel5.setName("lblscan"); // NOI18N

        btcommit.setText("Commit");
        btcommit.setName("btcommit"); // NOI18N
        btcommit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btcommitActionPerformed(evt);
            }
        });

        jLabel6.setText("Cell:");
        jLabel6.setName("lblcell"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btcommit)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tbscan)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ddcell, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 109, Short.MAX_VALUE)))
                        .addGap(18, 18, 18))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbscan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(tbqty, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddcell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btcommit)
                .addContainerGap(107, Short.MAX_VALUE))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btcommitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcommitActionPerformed
       
        
        double qty = 0;
        
        Pattern p = Pattern.compile("^[0-9]\\d*$");
        Matcher m = p.matcher(tbqty.getText());
        if (!m.find() || tbqty.getText() == null) {
            bsmf.MainFrame.show(getMessageTag(1028));
            tbqty.requestFocus();
            return;
        } else {
            qty = bsParseDouble(tbqty.getText());
        }
        
        if (pm == null) {
            bsmf.MainFrame.show(getMessageTag(1070, tbscan.getText()));
            initvars(null);
            return;
        } 
        
        
        if (pm.m()[0].equals("1")) {
            bsmf.MainFrame.show(getMessageTag(1070, tbscan.getText()));
            initvars(null);
            return;
        } 
        
        
        if (pm.plan_status() > 0 ) {
            bsmf.MainFrame.show(getMessageTag(1071, tbscan.getText()));
            initvars(null);
            return;
        }
        if (pm.plan_status() < 0 ) {
            bsmf.MainFrame.show(getMessageTag(1072, tbscan.getText()));
            initvars(null);
            return;
        }
        if (pm.plan_status() == 0 ) {
               
               schData.updatePlanQty(tbscan.getText(), qty);
               schData.updatePlanStatus(tbscan.getText(), "1");
               bsmf.MainFrame.show(getMessageTag(1073));
               initvars(null);
           
           
       } 
    }//GEN-LAST:event_btcommitActionPerformed

    private void tbqtyFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusGained
       tbqty.setBackground(Color.yellow);
    }//GEN-LAST:event_tbqtyFocusGained

    private void tbqtyFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbqtyFocusLost
        tbqty.setBackground(Color.white);
    }//GEN-LAST:event_tbqtyFocusLost

    private void tbscanFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbscanFocusGained
       tbscan.setBackground(Color.yellow);
    }//GEN-LAST:event_tbscanFocusGained

    private void tbscanFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbscanFocusLost
        tbscan.setBackground(Color.white);
        
    }//GEN-LAST:event_tbscanFocusLost

    private void tbscanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tbscanActionPerformed
       pm = null;
       pm = getPlanMstr(new String[]{tbscan.getText()});
       tbqty.setText(bsNumber(pm.plan_qty_sched()));
       tbqty.requestFocus();
    }//GEN-LAST:event_tbscanActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btcommit;
    private javax.swing.JComboBox ddcell;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField tbqty;
    private javax.swing.JTextField tbscan;
    // End of variables declaration//GEN-END:variables
}
