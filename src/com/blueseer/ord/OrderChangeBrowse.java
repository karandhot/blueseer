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

package com.blueseer.ord;

import com.blueseer.pur.*;
import bsmf.MainFrame;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import static bsmf.MainFrame.checkperms;
import static bsmf.MainFrame.db;
import java.awt.Color;
import java.awt.Component;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.reinitpanels;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.ctr.cusData;
import static com.blueseer.edi.ediData.getEDIMetaValueDetail;
import static com.blueseer.edi.ediData.getEDIMetaValueHeader;
import static com.blueseer.ord.ordData._evaluateOrderChange;
import static com.blueseer.ord.ordData.applyOrderChange;
import static com.blueseer.ord.ordData.updateOrderChangeStatus;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.bsNumberToUS;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getDateDB;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.setDateDB;
import com.blueseer.vdr.venData;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Enumeration;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

/**
 *
 * @author vaughnte
 */
public class OrderChangeBrowse extends javax.swing.JPanel {
 
     public Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
     
                          
     
    javax.swing.table.DefaultTableModel mymodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("detail"), 
                            getGlobalColumnTag("id"),
                            getGlobalColumnTag("order"),
                            getGlobalColumnTag("po"),
                            getGlobalColumnTag("date"),
                            getGlobalColumnTag("name"),                              
                            "old Due Date",
                            "new Due Date",
                            "Changes",
                            getGlobalColumnTag("status"),
                            "commit",
                            getGlobalColumnTag("void"),
                            "view"})
            {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0 || col == 10 || col == 11 || col == 12)       
                            return ImageIcon.class;  
                        else return String.class;  //other columns accept String values  
                      }  
                      
                      @Override
                      public boolean isCellEditable(int row, int column) {
                            return false;
                            //Only the first column
                            // return column == 1;
                      }
                      
                        };
                
    javax.swing.table.DefaultTableModel modeldetail = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{getGlobalColumnTag("line"), 
                            getGlobalColumnTag("item"), 
                            "old price", 
                            "new price", 
                            "old qty", 
                            "new qty" });
    
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
    
    class SomeRenderer extends DefaultTableCellRenderer {
        
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);
        
        String change = (String)table.getModel().getValueAt(table.convertRowIndexToModel(row), 8);
        String status = (String)table.getModel().getValueAt(table.convertRowIndexToModel(row), 9);
        
         if (! "none".equals(change) && column == 8 && status.equals("open")) {
              c.setBackground(Color.ORANGE);
              c.setForeground(Color.WHITE);
        } else {
            c.setBackground(table.getBackground());
            c.setForeground(table.getBackground());
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

    class DetailRenderer extends DefaultTableCellRenderer {
        
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);
        
         double oldprice = Double.valueOf((String) table.getModel().getValueAt(table.convertRowIndexToModel(row), 2).toString());
         double newprice = Double.valueOf((String) table.getModel().getValueAt(table.convertRowIndexToModel(row), 3).toString());
         double oldqty = Double.valueOf((String) table.getModel().getValueAt(table.convertRowIndexToModel(row), 4).toString());
         double newqty = Double.valueOf((String) table.getModel().getValueAt(table.convertRowIndexToModel(row), 5).toString());
         
        c.setBackground(table.getBackground());
        c.setForeground(table.getBackground()); 
        
        if (oldprice != newprice) {
              if (column == 2 || column == 3) {
                c.setBackground(Color.ORANGE);
                c.setForeground(Color.WHITE);
              }
        }
         
        if (oldqty != newqty) {
          if (column == 4 || column == 5) {
            c.setBackground(Color.ORANGE);
            c.setForeground(Color.WHITE);
          }
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

    
    
    
    /**
     * Creates new form ScrapReportPanel
     */
    public OrderChangeBrowse() {
        initComponents();
        setLanguageTags(this);
    }

    public void getdetail(String id, String po) {
      
         modeldetail.setNumRows(0);
         double total = 0;
        
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
                String blanket = "";
                
                 Enumeration<TableColumn> en = tabledetail.getColumnModel().getColumns();
                 while (en.hasMoreElements()) {
                     TableColumn tc = en.nextElement();
                     if (modeldetail.getColumnClass(tc.getModelIndex()).getSimpleName().equals("ImageIcon")) {
                         continue;
                     }
                     tc.setCellRenderer(new OrderChangeBrowse.DetailRenderer());
                 }
                
                res = st.executeQuery("select sod_line, sod_item, sod_ord_qty, sod_listprice, sodc_qty, sodc_price from sod_chg " +
                        " inner join so_chg on soc_id = sodc_id " +
                        " inner join sod_det on sodc_po = sod_po and sodc_line = sod_line " +
                        " where sod_po = " + "'" + po + "'" + 
                        " and soc_id = " + "'" + id + "'" +
                        ";");
                while (res.next()) {
                   modeldetail.addRow(new Object[]{ 
                      bsNumber(res.getString("sod_line")), 
                       res.getString("sod_item"),
                       bsParseDouble(currformatDouble(res.getDouble("sod_listprice"))),
                       bsParseDouble(currformatDouble(res.getDouble("sodc_price"))),
                      bsNumber(res.getDouble("sod_ord_qty")), 
                      bsNumber(res.getDouble("sodc_qty"))});
                }
              
                tabledetail.setModel(modeldetail);
              //  tabledetail.getColumnModel().getColumn(2).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(OVData.getDefaultCurrency())));
              //  tabledetail.getColumnModel().getColumn(3).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(OVData.getDefaultCurrency())));
                 this.repaint();

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

    }
  
    public void commitChange(String po) {
        
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
    
    public void clearAll() {
        lblamttot.setText("0");
        lblqtytot.setText("0");
        labeldettotal.setText("");
        tbsearch.setText("");
        tbfromorder.setText("");
        tbtoorder.setText("");
        
        
         cbopen.setSelected(true);
         cbclose.setSelected(false);
        
        java.util.Date now = new java.util.Date();
        Calendar calfrom = Calendar.getInstance();
        Calendar calto = Calendar.getInstance();
        
        calfrom.add(Calendar.DATE, -30);
        dcfrom.setDate(calfrom.getTime()); 
        
        calto.add(Calendar.DATE, +30);
        dcto.setDate(calto.getTime());
         
        mymodel.setNumRows(0);
        modeldetail.setNumRows(0);
        tablereport.setModel(mymodel);
        tabledetail.setModel(modeldetail);
        
        btdetail.setEnabled(false);
        detailpanel.setVisible(false);
        
        ddsite.removeAllItems();
        ArrayList sites = OVData.getSiteList(bsmf.MainFrame.userid);
        for (Object site : sites) {
            ddsite.addItem(site);
        }
        
        ddcustfrom.removeAllItems();
        ArrayList custs = cusData.getcustmstrlist();
        for (Object cust : custs) {
            ddcustfrom.addItem(cust);
        }
        ddcustto.removeAllItems();
        for (Object cust : custs) {
            ddcustto.addItem(cust);
        }
        
        if (ddcustfrom.getItemCount() > 0)
        ddcustfrom.setSelectedIndex(0);
        
        if (ddcustto.getItemCount() > 0)
        ddcustto.setSelectedIndex(ddcustto.getItemCount() - 1);
    }
    
    public void initvars(String[] arg) {
        clearAll();          
    }
   
    public void showEDIKVHeader(String key) {
        javax.swing.JTextArea ta = new javax.swing.JTextArea();
        
        JScrollPane scroll = new JScrollPane(ta);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
                
        ArrayList<String[]> list = getEDIMetaValueHeader(key);
        
        if (list == null || list.isEmpty()) {
            bsmf.MainFrame.show("no header level edi kv data to show");
            return;
        }
        
        ta.setText("  " + "\n\n");
        for (String[] s : list) {
          ta.append("Key: " + s[2] + " \t\t  Value: " + s[3] + "  \n");
        }
        
        ta.setCaretPosition(0);
        ta.setEditable(false);
        
        JDialog dialog = new JDialog();
        dialog.setTitle("Item level Key/Value Pair Information : " + key);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints panelGBC = new GridBagConstraints();

        panelGBC.weightx = 1;                    //I want to fill whole panel with JTextArea
        panelGBC.weighty = 1;                    //so both weights =1
        panelGBC.fill = GridBagConstraints.BOTH; //and fill is set to BOTH
        
        panel.add(scroll, panelGBC);
        dialog.add(panel);
        dialog.setPreferredSize(new Dimension(500, 400));
        dialog.pack();
        dialog.setLocationRelativeTo( null );
        dialog.setResizable(false);
        dialog.setVisible(true);
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
        tablepanel = new javax.swing.JPanel();
        summarypanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablereport = new javax.swing.JTable();
        detailpanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabledetail = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        btdetail = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        btRun = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        ddcustto = new javax.swing.JComboBox();
        ddcustfrom = new javax.swing.JComboBox();
        ddsite = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        tbfromorder = new javax.swing.JTextField();
        tbtoorder = new javax.swing.JTextField();
        cbclose = new javax.swing.JCheckBox();
        cbopen = new javax.swing.JCheckBox();
        btprint = new javax.swing.JButton();
        cbapplied = new javax.swing.JCheckBox();
        btexport = new javax.swing.JButton();
        tbsearch = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        btclear = new javax.swing.JButton();
        dcfrom = new com.toedter.calendar.JDateChooser();
        jLabel7 = new javax.swing.JLabel();
        dcto = new com.toedter.calendar.JDateChooser();
        jLabel9 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        lblqtytot = new javax.swing.JLabel();
        lblamttot = new javax.swing.JLabel();
        EndBal = new javax.swing.JLabel();
        labeldettotal = new javax.swing.JLabel();

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("EDI Order Change Browse"));
        jPanel1.setName("panelmain"); // NOI18N

        tablepanel.setLayout(new javax.swing.BoxLayout(tablepanel, javax.swing.BoxLayout.LINE_AXIS));

        summarypanel.setLayout(new java.awt.BorderLayout());

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
        tablereport.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablereportMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tablereport);

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
        jScrollPane2.setViewportView(tabledetail);

        detailpanel.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        tablepanel.add(detailpanel);

        btdetail.setText("Hide Detail");
        btdetail.setName("bthidedetail"); // NOI18N
        btdetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdetailActionPerformed(evt);
            }
        });

        jLabel4.setText("To Customer");
        jLabel4.setName("lbltovend"); // NOI18N

        btRun.setText("Run");
        btRun.setName("btrun"); // NOI18N
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jLabel5.setText("Site");
        jLabel5.setName("lblsite"); // NOI18N

        jLabel1.setText("From Customer");
        jLabel1.setName("lblfromvend"); // NOI18N

        jLabel3.setText("To Order");
        jLabel3.setName("lbltopo"); // NOI18N

        jLabel6.setText("From Order");
        jLabel6.setName("lblfrompo"); // NOI18N

        cbclose.setText("Closed");
        cbclose.setName("cbclosed"); // NOI18N

        cbopen.setText("Open");
        cbopen.setName("cbopen"); // NOI18N

        btprint.setText("Print/PDF");
        btprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintActionPerformed(evt);
            }
        });

        cbapplied.setText("Applied");

        btexport.setText("CSV Export");
        btexport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btexportActionPerformed(evt);
            }
        });

        jLabel2.setText("PO Search");

        btclear.setText("Clear");

        dcfrom.setDateFormatString("yyyy-MM-dd");

        jLabel7.setText("From Date");

        dcto.setDateFormatString("yyyy-MM-dd");

        jLabel9.setText("To Date");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel3))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel6)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(tbtoorder, javax.swing.GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
                            .addComponent(tbfromorder))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ddcustfrom, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddcustto, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(165, 165, 165)
                                .addComponent(cbopen)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cbclose)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cbapplied))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(4, 4, 4)
                                .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btRun)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btclear)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btdetail)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btprint)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btexport))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(tbsearch, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(ddcustfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btRun)
                        .addComponent(btdetail)
                        .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(tbfromorder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6)
                        .addComponent(btprint)
                        .addComponent(btexport)
                        .addComponent(btclear))
                    .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(ddcustto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(tbtoorder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cbclose)
                        .addComponent(cbopen)
                        .addComponent(cbapplied))
                    .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbsearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap())
        );

        jLabel8.setText("Total Qty");
        jLabel8.setName("lbltotalqty"); // NOI18N

        lblqtytot.setText("0");

        lblamttot.setBackground(new java.awt.Color(195, 129, 129));
        lblamttot.setText("0");

        EndBal.setText("Total Amt");
        EndBal.setName("lbltotalamt"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(65, 65, 65)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(EndBal)
                    .addComponent(jLabel8))
                .addGap(27, 27, 27)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblamttot, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblqtytot, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblqtytot, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(EndBal)
                    .addComponent(lblamttot, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labeldettotal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labeldettotal, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE))
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
               mymodel.setNumRows(0);
        
              tablereport.setModel(mymodel);
              tablereport.getColumnModel().getColumn(0).setMaxWidth(100);
              tablereport.getColumnModel().getColumn(10).setMaxWidth(100);
              tablereport.getColumnModel().getColumn(11).setMaxWidth(100);
              tablereport.getColumnModel().getColumn(12).setMaxWidth(100);
                 DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
                
                 double totqty = 0;
                 double totamt = 0;
                 String change = "";
                 
                 String orderfrom = tbfromorder.getText();
                 String orderto = tbtoorder.getText();
                 
                 if (orderfrom.isEmpty()) {
                     orderfrom = bsmf.MainFrame.lownbr;
                 }
                  if (orderto.isEmpty()) {
                     orderto = bsmf.MainFrame.hinbr;
                 }
                 
                 String custfrom = "";
                 String custto = "";
                 
                 if (ddcustfrom.getSelectedItem() != null)
                     custfrom = ddcustfrom.getSelectedItem().toString();
                 
                 if (ddcustto.getSelectedItem() != null)
                     custto = ddcustto.getSelectedItem().toString();
                 
                 
                 
                  Enumeration<TableColumn> en = tablereport.getColumnModel().getColumns();
                 while (en.hasMoreElements()) {
                     TableColumn tc = en.nextElement();
                     if (mymodel.getColumnClass(tc.getModelIndex()).getSimpleName().equals("ImageIcon")) {
                         continue;
                     }
                     tc.setCellRenderer(new OrderChangeBrowse.SomeRenderer());
                 }
                // tablereport.getColumnModel().getColumn(8).setCellRenderer(BlueSeerUtils.NumberRenderer.getCurrencyRenderer(BlueSeerUtils.getCurrencyLocale(OVData.getDefaultCurrency())));
                 
             if (! tbsearch.getText().isBlank()) {
                 res = st.executeQuery("select cm_name, so_nbr, so_po, soc_id, soc_chgdate, so_due_date, soc_duedate, soc_status  " +
                     " from so_mstr inner join so_chg on soc_po = so_po inner join cm_mstr on cm_code = so_cust where " +
                        " so_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " so_po like " + "'%" + tbsearch.getText() + "%'" +
                        " order by so_nbr desc ;");
                 
             } else {
                 res = st.executeQuery("select cm_name, so_nbr, so_po, soc_id, soc_chgdate, so_due_date, soc_duedate, soc_status  " +
                     " from so_mstr inner join so_chg on soc_po = so_po inner join cm_mstr on cm_code = so_cust where " +
                        " so_site = " + "'" + ddsite.getSelectedItem().toString() + "'" + " AND " +
                        " so_cust >= " + "'" + custfrom + "'" + " AND " +        
                        " so_cust <= " + "'" + custto + "'" + " AND " +
                        " so_create_date >= " + "'" + setDateDB(dcfrom.getDate()) + "'" + " AND " +
                        " so_create_date <= " + "'" + setDateDB(dcto.getDate()) + "'" + " AND " +        
                        " so_nbr >= " + "'" + orderfrom + "'" + " AND " +
                        " so_nbr <= " + "'" + orderto + "'" + 
                        " order by so_nbr desc ;");
             }  
             
                     
                  
                
                       while (res.next()) {
                    
                        change = _evaluateOrderChange(res.getString("soc_id"), res.getString("so_po"), con);    
                           
                             if (! cbopen.isSelected() && res.getString("soc_status").equals("open"))
                             continue;
                             if (! cbclose.isSelected() && res.getString("soc_status").equals("closed"))
                             continue;
                             if (! cbapplied.isSelected() && res.getString("soc_status").equals("applied"))
                             continue;
                                              
                    mymodel.addRow(new Object[]{ BlueSeerUtils.clickbasket, 
                                res.getString("soc_id"),
                                res.getString("so_nbr"),
                                res.getString("so_po"),
                                res.getString("soc_chgdate"),
                                res.getString("cm_name"),
                                res.getString("so_due_date"),
                                res.getString("soc_duedate"),
                                change,
                                res.getString("soc_status"),
                                BlueSeerUtils.clickchange,
                                BlueSeerUtils.clickvoid,
                                BlueSeerUtils.clickgear
                            });
                   
                } // while   
                    
                 
               
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
       
    }//GEN-LAST:event_btRunActionPerformed

    private void btdetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdetailActionPerformed
       detailpanel.setVisible(false);
       labeldettotal.setText("");
       btdetail.setEnabled(false);
    }//GEN-LAST:event_btdetailActionPerformed

    private void tablereportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablereportMouseClicked
        
        int row = tablereport.rowAtPoint(evt.getPoint());
        int col = tablereport.columnAtPoint(evt.getPoint());
        if ( col == 0) {
                getdetail(tablereport.getValueAt(row, 1).toString(), tablereport.getValueAt(row, 3).toString());  // soc_id, soc_po
                btdetail.setEnabled(true);
                detailpanel.setVisible(true);
              
        }
        if ( col == 10 && ! tablereport.getValueAt(row, 9).toString().equals("closed") &&
                 ! tablereport.getValueAt(row, 9).toString().equals("applied")) {
                applyOrderChange(tablereport.getValueAt(row, 1).toString(), tablereport.getValueAt(row, 3).toString());
                bsmf.MainFrame.show("Order Change Committed");
        }
        if ( col == 11 && ! tablereport.getValueAt(row, 9).toString().equals("closed") &&
                ! tablereport.getValueAt(row, 9).toString().equals("applied")) {
                updateOrderChangeStatus(tablereport.getValueAt(row, 1).toString(), "closed");
                bsmf.MainFrame.show("Order Change Closed");
        }
        if ( col == 12 ) {
                showEDIKVHeader(tablereport.getValueAt(row, 1).toString());
        }
       
    }//GEN-LAST:event_tablereportMouseClicked

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed
        OVData.printJTableToJasper("Order Change Report", tablereport, "genericJTableL9.jasper" );
    }//GEN-LAST:event_btprintActionPerformed

    private void btexportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btexportActionPerformed
       if (tablereport != null && mymodel.getRowCount() > 0) {
        OVData.exportOrderChange(tablereport);
        bsmf.MainFrame.show("export file created");
       }
    }//GEN-LAST:event_btexportActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel EndBal;
    private javax.swing.JButton btRun;
    private javax.swing.JButton btclear;
    private javax.swing.JButton btdetail;
    private javax.swing.JButton btexport;
    private javax.swing.JButton btprint;
    private javax.swing.JCheckBox cbapplied;
    private javax.swing.JCheckBox cbclose;
    private javax.swing.JCheckBox cbopen;
    private com.toedter.calendar.JDateChooser dcfrom;
    private com.toedter.calendar.JDateChooser dcto;
    private javax.swing.JComboBox ddcustfrom;
    private javax.swing.JComboBox ddcustto;
    private javax.swing.JComboBox ddsite;
    private javax.swing.JPanel detailpanel;
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
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel labeldettotal;
    private javax.swing.JLabel lblamttot;
    private javax.swing.JLabel lblqtytot;
    private javax.swing.JPanel summarypanel;
    private javax.swing.JTable tabledetail;
    private javax.swing.JPanel tablepanel;
    private javax.swing.JTable tablereport;
    private javax.swing.JTextField tbfromorder;
    private javax.swing.JTextField tbsearch;
    private javax.swing.JTextField tbtoorder;
    // End of variables declaration//GEN-END:variables
}
