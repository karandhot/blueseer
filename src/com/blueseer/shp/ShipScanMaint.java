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
package com.blueseer.shp;


import bsmf.MainFrame;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.JTable;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.inv.invData;
import static com.blueseer.inv.invData.getWHLOCfromSerialNumber;
import com.blueseer.inv.invData.inv_ctrl;
import static com.blueseer.lbl.lblData.getLabelMstrByStrID;
import static com.blueseer.lbl.lblData.getLabelTableRecs;
import com.blueseer.lbl.lblData.label_mstr;
import com.blueseer.sch.schData;
import static com.blueseer.sch.schData.getPlanDetHistory;
import com.blueseer.sch.schData.plan_mstr;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.OVData.getSysMetaValue;
import static com.blueseer.utl.OVData.getpsmstrcompSerialized;
import java.awt.Component;
import java.sql.Connection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import net.sf.jasperreports.view.JasperViewer;
import java.util.HashSet;

/**
 *
 * @author vaughnte
 */
public class ShipScanMaint extends javax.swing.JPanel {

 // global variable declarations
                boolean isLoad = false;
                String terms = "";
                String aracct = "";
                String arcc = "";
                String arbank = "";
                double actamt = 0.00;
                double baseamt = 0.00;
                double rcvamt = 0.00;
                String curr = "";
                String basecurr = "";
                int j = 0;
                HashSet<String> assignedlabels = new HashSet<String>();
                HashSet<String> assigneditems = new HashSet<String>();
    
    // global datatablemodel declarations 
    javax.swing.table.DefaultTableModel serialmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("label"),
                getGlobalColumnTag("order"),
                getGlobalColumnTag("line"),
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("description"), 
                getGlobalColumnTag("custitem"),
                getGlobalColumnTag("warehouse"), 
                getGlobalColumnTag("location"),
                getGlobalColumnTag("qty"),
                getGlobalColumnTag("uom"),
                getGlobalColumnTag("price"),
                getGlobalColumnTag("po")});
    
    javax.swing.table.DefaultTableModel itemmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
            new String[]{
                getGlobalColumnTag("item"), 
                getGlobalColumnTag("description"), 
                getGlobalColumnTag("qty")});
  
    
    
    /**
     * Creates new form CarrierMaintPanel
     */
    public ShipScanMaint() {
        initComponents();
        setLanguageTags(this);
    }

    public void executeTask(BlueSeerUtils.dbaction x, String[] y) { 
      
        class Task extends SwingWorker<String[], Void> {
       
          String type = "";
          String[] key = null;
          
          public Task(BlueSeerUtils.dbaction type, String[] key) { 
              this.type = type.name();
              this.key = key;
          } 
           
        @Override
        public String[] doInBackground() throws Exception {
            String[] message = new String[2];
            message[0] = "";
            message[1] = "";
            
            
             switch(this.type) {
                case "run":
                    message = postShip();    
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
            initvars(null);  
            } catch (Exception e) {
                MainFrame.bslog(e);
            } 
           
        }
    }  
      
       BlueSeerUtils.startTask(new String[]{"","Running..."});
       Task z = new Task(x, y); 
       z.execute(); 
       
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
    
    public void setComponentDefaultValues() {
        
      tbscan.setText("");
       
      serialmodel.setRowCount(0);
      serialdet.setModel(serialmodel);
      serialdet.getTableHeader().setReorderingAllowed(false);
      
      itemmodel.setRowCount(0);
      itemdet.setModel(itemmodel);
      itemdet.getTableHeader().setReorderingAllowed(false);
       
       
      btcommit.setEnabled(false);
        
      tbscan.requestFocusInWindow();
    }
     
    public String sumQtyByItem() {
        String x = "";
        double qty = 0;
        boolean doesExist = false;
        for (String s : assigneditems) {
          qty = 0;
          for (int j = 0; j < serialdet.getRowCount(); j++) {
            if (serialdet.getModel().getValueAt(j, 3).equals(s)) {
                qty += bsParseDouble(serialdet.getModel().getValueAt(j, 8).toString());
            }             
          }
          for (int j = 0; j < itemdet.getRowCount(); j++) {
            if (itemdet.getModel().getValueAt(j, 0).equals(s)) {
                doesExist = true;
                itemdet.getModel().setValueAt(String.valueOf(qty), j, 2);
                break;
            }             
          }
          if (! doesExist) {
              itemmodel.addRow(new Object[] { 
                s, // item
                "", // desc
                qty // qty
                }); 
          }
        }
        return x;
    }
    
   
        
    public boolean isDuplicate(String serial_id_str) {
        for (int j = 0; j < serialdet.getRowCount(); j++) {
            if (serialdet.getValueAt(j, 0).toString().equals(serial_id_str)) {
                return true;
            }
        }
        return false;
    }
    
    public String[] postShip() {
        
        return null;
    }
    
    public void validateScan(String scan) {
               
        
        lblmessage.setText("");
        if (scan.isEmpty()) {
            return;
        }
        
        label_mstr label = getLabelMstrByStrID(scan);
        if (label.m()[0].equals("0")) {
            
        
            if (! isDuplicate(label.lbl_id_str())) {
                serialmodel.addRow(new Object[] { 
                    label.lbl_id_str(), // serial
                    label.lbl_order(), // order
                    label.lbl_line(), // orderline
                    label.lbl_item(), // item
                    label.lbl_item(), // desc
                    label.lbl_custitem(), // custitem
                    "", // wh
                    label.lbl_loc(), // loc
                    label.lbl_qty(), // qty
                    "", // uom
                    "0.00", // price
                    label.lbl_po() // po
                    });

                if (! assigneditems.contains(label.lbl_item())) {
                  assigneditems.add(label.lbl_item());
                }

                sumQtyByItem();

              tbscan.setText("");
              lblmessage.setText("scanned: " + scan);
              lblmessage.setForeground(Color.black);
              tbscan.requestFocusInWindow();
              return;  
            } else {
              tbscan.setText("");
              lblmessage.setText("Duplicate Serial Number: " + scan);
              lblmessage.setForeground(Color.red);
              tbscan.requestFocusInWindow();
              return;  
            }
        } else {
          tbscan.setText("");
          lblmessage.setText("Bad / Unknown Serial Number: " + scan);
          lblmessage.setForeground(Color.red);
          tbscan.requestFocusInWindow();
          return;  
        }
      
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
       setPanelComponentState(this, true); 
       setComponentDefaultValues();
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
        tbscan = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btcommit = new javax.swing.JButton();
        lblmessage = new javax.swing.JLabel();
        btdeleteitem = new javax.swing.JButton();
        btclear = new javax.swing.JButton();
        labelPanel = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        serialdet = new javax.swing.JTable();
        itemPanel = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        itemdet = new javax.swing.JTable();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Shipper Scan Menu"));
        jPanel1.setName("panelmain"); // NOI18N

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

        btdeleteitem.setText("Remove Label");
        btdeleteitem.setName("btdeleteitem"); // NOI18N
        btdeleteitem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteitemActionPerformed(evt);
            }
        });

        btclear.setText("Clear");
        btclear.setName("btclear"); // NOI18N
        btclear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearActionPerformed(evt);
            }
        });

        labelPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Scanned Labels"));

        serialdet.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane8.setViewportView(serialdet);

        javax.swing.GroupLayout labelPanelLayout = new javax.swing.GroupLayout(labelPanel);
        labelPanel.setLayout(labelPanelLayout);
        labelPanelLayout.setHorizontalGroup(
            labelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(labelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
                .addContainerGap())
        );
        labelPanelLayout.setVerticalGroup(
            labelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(labelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        itemPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Summarized Items"));

        itemdet.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane9.setViewportView(itemdet);

        javax.swing.GroupLayout itemPanelLayout = new javax.swing.GroupLayout(itemPanel);
        itemPanel.setLayout(itemPanelLayout);
        itemPanelLayout.setHorizontalGroup(
            itemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(itemPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
                .addContainerGap())
        );
        itemPanelLayout.setVerticalGroup(
            itemPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btdeleteitem, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btcommit, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(itemPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(tbscan, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(btclear)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(lblmessage, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                .addContainerGap())
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(labelPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbscan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(btclear))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblmessage, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21)
                .addComponent(btdeleteitem)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 212, Short.MAX_VALUE)
                .addComponent(itemPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btcommit)
                .addGap(94, 94, 94))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(126, 126, 126)
                    .addComponent(labelPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(304, Short.MAX_VALUE)))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void btcommitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btcommitActionPerformed
       setPanelComponentState(this, false);
        executeTask(BlueSeerUtils.dbaction.run, new String[]{""});
    }//GEN-LAST:event_btcommitActionPerformed

    private void tbscanFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbscanFocusGained
       tbscan.setBackground(Color.yellow);
       
    }//GEN-LAST:event_tbscanFocusGained

    private void tbscanFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbscanFocusLost
        tbscan.setBackground(Color.white);
        validateScan(tbscan.getText());        
    }//GEN-LAST:event_tbscanFocusLost

    private void btdeleteitemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteitemActionPerformed
        int[] rows = serialdet.getSelectedRows();
        String targetlabel = "";
        for (int i : rows) {
            targetlabel = serialdet.getModel().getValueAt(i, 0).toString();
        }

        ArrayList<Integer> rowsToDelete = new ArrayList<Integer>();
        for (int i = 0; i < serialdet.getRowCount(); i++) {
            if (serialdet.getModel().getValueAt(i, 0).toString().equals(targetlabel)) {
                rowsToDelete.add(i);

            }
        }
        Collections.reverse(rowsToDelete);
        for (int j : rowsToDelete) {
            ((javax.swing.table.DefaultTableModel) serialdet.getModel()).removeRow(j);
        }

        assignedlabels.remove(targetlabel);
        sumQtyByItem();

    }//GEN-LAST:event_btdeleteitemActionPerformed

    private void btclearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearActionPerformed
        BlueSeerUtils.messagereset();
        initvars(null);
    }//GEN-LAST:event_btclearActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btclear;
    private javax.swing.JButton btcommit;
    private javax.swing.JButton btdeleteitem;
    private javax.swing.JPanel itemPanel;
    private javax.swing.JTable itemdet;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JPanel labelPanel;
    private javax.swing.JLabel lblmessage;
    private javax.swing.JTable serialdet;
    private javax.swing.JTextField tbscan;
    // End of variables declaration//GEN-END:variables
}
