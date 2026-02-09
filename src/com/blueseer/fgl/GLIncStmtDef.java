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
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.fgl.fglData.addUpdateGLICMeta;
import static com.blueseer.fgl.fglData.deleteGLICMeta;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.callDialog;
import static com.blueseer.utl.BlueSeerUtils.getClassLabelTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.luModel;
import static com.blueseer.utl.BlueSeerUtils.luTable;
import static com.blueseer.utl.BlueSeerUtils.lual;
import static com.blueseer.utl.BlueSeerUtils.ludialog;
import static com.blueseer.utl.BlueSeerUtils.luinput;
import static com.blueseer.utl.BlueSeerUtils.luml;
import static com.blueseer.utl.BlueSeerUtils.lurb1;
import com.blueseer.utl.DTData;
import com.blueseer.utl.OVData;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListModel;

/**
 *
 * @author vaughnte
 */
public class GLIncStmtDef extends javax.swing.JPanel {

    DefaultListModel mymodel = new DefaultListModel() ;
    DefaultListModel mymodelex = new DefaultListModel() ;
    boolean isLoad = false;
    
    /**
     * Creates new form GLIncStmtDef
     */
    public GLIncStmtDef() {
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
           //bsmf.MainFrame.show(component.getClass().getTypeName() + "/" + component.getAccessibleContext().getAccessibleName() + "/" + component.getName());
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
    
    
    public void initvars(String[] vars) {
      /*  
        ddcategory.removeAllItems();
        ArrayList mylist = fglData.getGLICDefsList();
        for (int i = 0; i < mylist.size(); i++) {
            ddcategory.addItem(mylist.get(i));
        }
    */
        ddacct.removeAllItems();
        ArrayList accts = fglData.getGLAcctList();
        for (int i = 0; i < accts.size(); i++) {
            ddacct.addItem(accts.get(i));
        }
    // assignedlist.setModel(mymodel);
    assignlist.setModel(mymodel);
    excludelist.setModel(mymodelex);
    }
    
    public void lookUpFrame() {
        
        luinput.removeActionListener(lual);
        lual = new ActionListener() {
        public void actionPerformed(ActionEvent event) {
        if (lurb1.isSelected()) {  
         luModel = DTData.getGLICBrowseUtil(luinput.getText(),0, "glic_profile");
        } else {
         luModel = DTData.getGLICBrowseUtil(luinput.getText(),0, "glic_name");   
        }
        luTable.setModel(luModel);
        luTable.getColumnModel().getColumn(0).setMaxWidth(50);
        if (luModel.getRowCount() < 1) {
            ludialog.setTitle(getMessageTag(1001));
        } else {
            ludialog.setTitle(getMessageTag(1002, String.valueOf(luModel.getRowCount())));
        }
        }
        };
        luinput.addActionListener(lual);
        
        luTable.removeMouseListener(luml);
        luml = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable)e.getSource();
                int row = target.getSelectedRow();
                int column = target.getSelectedColumn();
                if ( column == 0) {
                ludialog.dispose();
                tbprofile.setText(target.getValueAt(row,1).toString());
                ArrayList<String> cats = fglData.getGLICCategoryList(target.getValueAt(row,1).toString());
                isLoad = true;
                for (String cat : cats) {
                ddcategory.addItem(cat);
                }
                if (ddcategory.getItemCount() > 0) {
                    ddcategory.setSelectedIndex(0);
                }
                isLoad = false;
                //initvars(new String[]{target.getValueAt(row,1).toString()});
                }
            }
        };
        luTable.addMouseListener(luml);
      
        callDialog(getClassLabelTag("lblid", this.getClass().getSimpleName()), getClassLabelTag("lbldesc", this.getClass().getSimpleName())); 
        
    }

    public void clearAll() {
        tbprofile.setText("");
        tbdesc.setText("");
        ddcategory.setSelectedIndex(0);
        ddtype.setSelectedIndex(0);
        tbsequence.setText("");
        tbfrom.setText("");
        tbto.setText("");
        cbsummarize.setSelected(false);
        cbflipsign.setSelected(false);
        assignlist.removeAll();
        excludelist.removeAll();
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
        ddcategory = new javax.swing.JComboBox();
        btupdate = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        tbfrom = new javax.swing.JTextField();
        tbto = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        tbprofile = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        btaddassign = new javax.swing.JButton();
        btdeleteexclude = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        btaddexclude = new javax.swing.JButton();
        btdeleteassigned = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        excludelist = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        assignlist = new javax.swing.JList();
        acctname = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        ddacct = new javax.swing.JComboBox();
        tbdesc = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        tbsequence = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        btlookup = new javax.swing.JButton();
        btclear = new javax.swing.JButton();
        btaddcat = new javax.swing.JButton();
        btdeletecat = new javax.swing.JButton();
        cbsummarize = new javax.swing.JCheckBox();
        cbflipsign = new javax.swing.JCheckBox();
        ddtype = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setName("panelmain"); // NOI18N

        ddcategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddcategoryActionPerformed(evt);
            }
        });

        btupdate.setText("Update");
        btupdate.setName("btupdate"); // NOI18N
        btupdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btupdateActionPerformed(evt);
            }
        });

        jLabel1.setText("Category");
        jLabel1.setName("lblcategory"); // NOI18N

        jLabel5.setText("From");
        jLabel5.setName("lblfrom"); // NOI18N

        jLabel6.setText("To");
        jLabel6.setName("lblto"); // NOI18N

        jLabel2.setText("Profile");

        jLabel3.setText("Account:");
        jLabel3.setName("lblaccounts"); // NOI18N

        btaddassign.setText("Add");
        btaddassign.setName("btadd"); // NOI18N
        btaddassign.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddassignActionPerformed(evt);
            }
        });

        btdeleteexclude.setText("Delete");
        btdeleteexclude.setName("btdelete"); // NOI18N
        btdeleteexclude.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteexcludeActionPerformed(evt);
            }
        });

        jLabel4.setText("Excluded:");
        jLabel4.setName("lblexcluded"); // NOI18N

        btaddexclude.setText("Add");
        btaddexclude.setName("btadd"); // NOI18N
        btaddexclude.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddexcludeActionPerformed(evt);
            }
        });

        btdeleteassigned.setText("Delete");
        btdeleteassigned.setName("btdelete"); // NOI18N
        btdeleteassigned.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeleteassignedActionPerformed(evt);
            }
        });

        jScrollPane3.setViewportView(excludelist);

        jScrollPane2.setViewportView(assignlist);

        jLabel7.setText("Assigned:");
        jLabel7.setName("lblassigned"); // NOI18N

        ddacct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ddacctActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel7)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(btdeleteexclude)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btaddexclude))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(ddacct, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btdeleteassigned)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btaddassign)))
                        .addGap(18, 18, 18)
                        .addComponent(acctname, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                    .addComponent(jScrollPane3))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(ddacct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3))
                    .addComponent(acctname, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btaddassign)
                    .addComponent(btdeleteassigned))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btaddexclude)
                    .addComponent(btdeleteexclude))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel8.setText("Description");

        jLabel9.setText("Sequence");

        btlookup.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/lookup.png"))); // NOI18N
        btlookup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btlookupActionPerformed(evt);
            }
        });

        btclear.setText("Clear");

        btaddcat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/add.png"))); // NOI18N
        btaddcat.setToolTipText("Add Category");
        btaddcat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btaddcatActionPerformed(evt);
            }
        });

        btdeletecat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/delete.png"))); // NOI18N
        btdeletecat.setToolTipText("Delete Category");
        btdeletecat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdeletecatActionPerformed(evt);
            }
        });

        cbsummarize.setText("Summarize");

        cbflipsign.setText("Flip Sign");

        ddtype.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "detail", "summation", "spacer", "dashline" }));

        jLabel10.setText("Type");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btupdate))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(80, 80, 80)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(tbprofile, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btlookup, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btclear))
                            .addComponent(tbfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbto, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, 265, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tbsequence, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(ddcategory, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btaddcat, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btdeletecat, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(cbsummarize)
                            .addComponent(cbflipsign)
                            .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tbprofile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addComponent(btlookup)
                    .addComponent(btclear))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddcategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(btaddcat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbdesc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbsequence, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tbto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddtype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbsummarize)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbflipsign)
                        .addGap(13, 13, 13)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btupdate))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btdeletecat)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        add(jPanel1);
    }// </editor-fold>//GEN-END:initComponents

    private void ddcategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddcategoryActionPerformed
       if (! isLoad && ddcategory.getItemCount() > 0) {
        mymodelex.removeAllElements();
        mymodel.removeAllElements();
        tbfrom.setText(fglData.getGLICDefsStart(ddcategory.getSelectedItem().toString()));
        tbto.setText(fglData.getGLICDefsEnd(ddcategory.getSelectedItem().toString()));
        
        ArrayList mylistin = fglData.getGLICAccts(ddcategory.getSelectedItem().toString(),"in");
        for (int i = 0; i < mylistin.size(); i++) {
            mymodel.addElement(mylistin.get(i));
        }
        
        ArrayList mylist = fglData.getGLICAccts(ddcategory.getSelectedItem().toString(),"out");
        for (int i = 0; i < mylist.size(); i++) {
            mymodelex.addElement(mylist.get(i));
        }
       }
    }//GEN-LAST:event_ddcategoryActionPerformed

    private void ddacctActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ddacctActionPerformed
       acctname.setText(fglData.getGLAcctDesc(ddacct.getSelectedItem().toString()));
    }//GEN-LAST:event_ddacctActionPerformed

    private void btupdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btupdateActionPerformed
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
                
                int i = 0;
                res = st.executeQuery("SELECT glic_profile FROM glic_def where glic_profile = " + "'" + tbprofile.getText() + "'"
                        + " AND glic_name = " + "'" + ddcategory.getSelectedItem().toString() + "'"
                        + " ;");
                while (res.next()) {
                    i++;
                }
                if (i == 0) {
                    st.executeUpdate("insert into glic_def (glic_profile, glic_name, glic_desc, glic_seq, glic_type, glic_start, glic_end) values ( "
                            + "'" + tbprofile.getText() + "'" + ","
                            + "'" + ddcategory.getSelectedItem().toString() + "'" + ","
                            + "'" + tbdesc.getText() + "'" + ","
                            + "'" + tbsequence.getText() + "'" + ","
                            + "'" + ddtype.getSelectedItem().toString() + "'" + "," // type unused
                            + "'" + tbfrom.getText() + "'" + ","
                            + "'" + tbto.getText() + "'" + ","
                            + "'" + BlueSeerUtils.boolToString(cbsummarize.isSelected()) + "'" + ","
                            + "'" + BlueSeerUtils.boolToString(cbflipsign.isSelected()) + "'"         
                            + ")"
                            + ";");
                    
                } else {
                    st.executeUpdate("update glic_def set " +
                            " glic_desc = " + "'" + tbdesc.getText() + "'" + "," +
                            " glic_seq = " + "'" + tbsequence.getText() + "'" + "," +        
                            " glic_start = " + "'" + tbfrom.getText() + "'" + "," +
                            " glic_end = " + "'" + tbto.getText() + "'" + "," +
                            " glic_summarize = " + "'" + BlueSeerUtils.boolToString(cbsummarize.isSelected()) + "'" + "," +
                            " glic_flipsign = " + "'" + BlueSeerUtils.boolToString(cbflipsign.isSelected()) + "'" +         
                            " where glic_name = " + "'" + ddcategory.getSelectedItem().toString() + "'" + 
                            " and glic_profile = " + "'" + tbprofile.getText() + "'" +
                            ";");
                } // else record exists
                
                 // erase all assigned accounts and refill with current assign and exclude list
                    st.executeUpdate("delete from glic_accts where glicd_name = " + "'" + ddcategory.getSelectedItem().toString() + "'" + ";");
                    
                       for (int j = 0; j < mymodel.getSize(); j++) {
                        st.executeUpdate("insert into glic_accts "
                            + "(glicd_profile, glicd_name, glicd_acct, glicd_type ) "
                            + " values ( " 
                            + "'" + tbprofile.getText() + "'" + ","
                            + "'" + ddcategory.getSelectedItem().toString() + "'" + ","
                            + "'" + mymodel.getElementAt(j) + "'" + ","
                            + "'in'"
                            + ")"
                            + ";");
                       }
                       
                       for (int j = 0; j < mymodelex.getSize(); j++) {
                        st.executeUpdate("insert into glic_accts "
                            + "(glicd_profile, glicd_name, glicd_acct, glicd_type ) "
                            + " values ( " 
                            + "'" + tbprofile.getText() + "'" + ","
                            + "'" + ddcategory.getSelectedItem().toString() + "'" + ","
                            + "'" + mymodelex.getElementAt(j) + "'" + ","
                            + "'out'"
                            + ")"
                            + ";");
                       }
                
               bsmf.MainFrame.show(getMessageTag(1008)); 
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
                bsmf.MainFrame.show(getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName()));
            } finally {
                if (res != null) {
                    res.close();
                }
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
    }//GEN-LAST:event_btupdateActionPerformed

    private void btaddassignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddassignActionPerformed
        mymodel.addElement(ddacct.getSelectedItem().toString());
    }//GEN-LAST:event_btaddassignActionPerformed

    private void btdeleteassignedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteassignedActionPerformed
         mymodel.removeElement(assignlist.getSelectedValue());
    }//GEN-LAST:event_btdeleteassignedActionPerformed

    private void btaddexcludeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddexcludeActionPerformed
       mymodelex.addElement(ddacct.getSelectedItem().toString());
    }//GEN-LAST:event_btaddexcludeActionPerformed

    private void btdeleteexcludeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeleteexcludeActionPerformed
        mymodelex.removeElement(excludelist.getSelectedValue());
    }//GEN-LAST:event_btdeleteexcludeActionPerformed

    private void btlookupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btlookupActionPerformed
        lookUpFrame();
    }//GEN-LAST:event_btlookupActionPerformed

    private void btaddcatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btaddcatActionPerformed
        if (! tbprofile.getText().isBlank()) {
            String input = bsmf.MainFrame.input("Enter new category");
            boolean proceed = true;
            for (int i = 0; i < ddcategory.getItemCount(); i++) {
                  if (ddcategory.getItemAt(i).toString().toLowerCase().equals(input.toLowerCase())) {
                     proceed = false;
                   }
            }
            if (proceed) {
            addUpdateGLICMeta("glic", "category", tbprofile.getText(), input);
            ddcategory.addItem(input);
            ddcategory.requestFocus();
            }
        }
    }//GEN-LAST:event_btaddcatActionPerformed

    private void btdeletecatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdeletecatActionPerformed
        if (! tbprofile.getText().isBlank()) {
            int i = ddcategory.getSelectedIndex();
            String item = ddcategory.getSelectedItem().toString();
            deleteGLICMeta("glic", "category", tbprofile.getText(), item);
            ddcategory.remove(i);
        }
    }//GEN-LAST:event_btdeletecatActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel acctname;
    private javax.swing.JList assignlist;
    private javax.swing.JButton btaddassign;
    private javax.swing.JButton btaddcat;
    private javax.swing.JButton btaddexclude;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdeleteassigned;
    private javax.swing.JButton btdeletecat;
    private javax.swing.JButton btdeleteexclude;
    private javax.swing.JButton btlookup;
    private javax.swing.JButton btupdate;
    private javax.swing.JCheckBox cbflipsign;
    private javax.swing.JCheckBox cbsummarize;
    private javax.swing.JComboBox ddacct;
    private javax.swing.JComboBox ddcategory;
    private javax.swing.JComboBox<String> ddtype;
    private javax.swing.JList excludelist;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextField tbdesc;
    private javax.swing.JTextField tbfrom;
    private javax.swing.JTextField tbprofile;
    private javax.swing.JTextField tbsequence;
    private javax.swing.JTextField tbto;
    // End of variables declaration//GEN-END:variables
}
