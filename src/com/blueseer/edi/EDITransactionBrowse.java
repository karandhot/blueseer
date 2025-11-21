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

package com.blueseer.edi;

import bsmf.MainFrame;
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.edi.ediData.getDocViewData;
import static com.blueseer.edi.ediData.getEDITransBrowseDetail;
import static com.blueseer.edi.ediData.getFileViewData;
import com.blueseer.utl.EDData;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.cleanDirString;
import static com.blueseer.utl.BlueSeerUtils.dropColumn;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.jsonToData;
import static com.blueseer.utl.BlueSeerUtils.sendServerPost;
import static com.blueseer.utl.EDData.getEDIAckFile;
import static com.blueseer.utl.EDData.getEDIStds;
import static com.blueseer.utl.EDData.updateEDIFileLogStatusManual;
import com.blueseer.utl.OVData;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import java.net.MalformedURLException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import jcifs.smb.SmbException;

/**
 *
 * @author vaughnte
 */
public class EDITransactionBrowse extends javax.swing.JPanel {
 
    
    String indir = "";
    String outdir = "";
    String inarch = "";
    String outarch = "";
    String batchdir = "";
    String errordir = "";
    String mapdir = "";
    HashMap<String, String> hm = new HashMap<>();
    
    Highlighter.HighlightPainter myHighlightPainter = new MyHighlightPainter(Color.YELLOW);
    
    javax.swing.table.DefaultTableModel docmodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"Select", "IdxNbr", "ComKey", "SenderID", "ReceiverID", "TimeStamp", "InFileType", "InDocType", "InBatch", "Reference", "OutFileType", "OutDocType", "OutBatch", "InView", "OutView",  "Status"})
            {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0 || col == 13 || col == 14 || col == 15)  {     
                            return ImageIcon.class; 
                        } else if (col == 1 || col == 2) {
                            return Integer.class;
                        } else {
                            return String.class;
                        }  //other columns accept String values  
                      }
                      
                    @Override
                    public boolean isCellEditable(int row, int column)
                    {
                       // make read only fields except column 0,13,14
                        if (column == 0 || column == 13 || column == 14 || column == 15) {                            
                           return false;
                        } else {
                           return true; 
                        }
                    }
                        };
                
    javax.swing.table.DefaultTableModel filemodel = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"Select", "LogID", "ComKey", "Partner", "FileType", "DocType", "TimeStamp", "File", "Dir", "View", "Status"})
            {
                      @Override  
                      public Class getColumnClass(int col) {  
                        if (col == 0 || col == 9 || col == 10)  {     
                            return ImageIcon.class; 
                        } else if (col == 1 || col == 2) {
                            return Integer.class;
                        } else {
                            return String.class;
                        }  //other columns accept String values  
                      }  
                      @Override
                    public boolean isCellEditable(int row, int column)
                    {
                        // make read only fields except column 0,13,14
                        if (column == 0 || column == 9 || column == 10) {                            
                           return false;
                        } else {
                           return true; 
                        }
                    }
                        };
    
    javax.swing.table.DefaultTableModel modeldetail = new javax.swing.table.DefaultTableModel(new Object[][]{},
                        new String[]{"LogID", "ComKey", "Severity", "Desc", "TimeStamp"});
    
   
    
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
    
     public class myPopupHandler implements ActionListener,
                              PopupMenuListener {
        
        JTextArea ta; 
        JPopupMenu popup;
       
        
        public myPopupHandler(JTextArea ta) {
        this.ta = ta;
        this.ta.setName(ta.getName());
        popup = new JPopupMenu();
        popup.setInvoker(ta);
       
        if (ta.getName().equals("tafile")) {
            popup.add(setMenuItem("Search"));
            popup.add(setMenuItem("Hex Replace"));
            popup.add(setMenuItem("Raw To Newline"));
        }
        /*
        if (ta.getName().equals("tainput")) {
            popup.add(setMenuItem("Search"));
            popup.add(setMenuItem("Clear"));
            popup.add(setMenuItem("Hex Replace"));
            popup.add(setMenuItem("Input"));
            popup.add(setMenuItem("Identify"));
            popup.add(setMenuItem("Download"));
            popup.add(setMenuItem("Clear Highlights"));
            popup.add(setMenuItem("Hide Panel"));
        }
        if (ta.getName().equals("tainstruct")) {
            popup.add(setMenuItem("Search"));
            popup.add(setMenuItem("Clear"));
            popup.add(setMenuItem("Structure"));
            popup.add(setMenuItem("Overlay"));
            popup.add(setMenuItem("Clear Highlights"));
        }
        if (ta.getName().equals("taoutput")) {
            popup.add(setMenuItem("Search"));
            popup.add(setMenuItem("Clear"));
            popup.add(setMenuItem("Hex Replace"));
            popup.add(setMenuItem("Download"));
            popup.add(setMenuItem("Clear Highlights"));
            popup.add(setMenuItem("Hide Panel"));
        }
        if (ta.getName().equals("taoutstruct")) {
            popup.add(setMenuItem("Search"));
            popup.add(setMenuItem("Clear"));
            popup.add(setMenuItem("Structure"));
            popup.add(setMenuItem("Overlay"));
            popup.add(setMenuItem("Clear Highlights"));
        }
        */
        ta.addMouseListener(ma);
        popup.addPopupMenuListener(this);
    }
 
        public JPopupMenu getPopup() {
        return popup;
        }
        
        private JMenuItem setMenuItem(String s) {
        JMenuItem menuItem = new JMenuItem(s);
        menuItem.setActionCommand(s);
        menuItem.setName(this.ta.getName());
        menuItem.addActionListener(this);
        return menuItem;
    }
        
        
        private MouseListener ma = new MouseAdapter() {
            private void checkForPopup(MouseEvent e) {
               if (SwingUtilities.isRightMouseButton(e)) {
                  if (! ta.isEnabled()) {
                      return;
                  } 
                popup.show(ta, e.getX(), e.getY());
                } 
            }
            public void mousePressed(MouseEvent e)  { checkForPopup(e); }
            
        };

        @Override
        public void actionPerformed(ActionEvent e) { 
             String ac = e.getActionCommand();
             JMenuItem parentname = (JMenuItem) e.getSource();
             switch (ac) {
                case "Hex Replace" :
                    hexReplace(parentname.getName());
                    break;    
                    
                case "Search" :
                    searchTextArea(parentname.getName());
                    break; 
                    
                case "Raw To Newline" :
                    hexReplaceUnderScore(parentname.getName());
                    break;    
                    
                default:
                    System.out.println("unknown action: " + ac);
                    
             }
             
        }

        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
          //  throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
           // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
           // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
      
        
    }
    
     
    class DocViewRenderer extends DefaultTableCellRenderer {
        
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);

       
            if (column == 8 || column == 12)
            c.setForeground(Color.BLUE);
            else
                c.setBackground(table.getBackground());
       
        return c;
    }
    }
        
    class FileViewRenderer extends DefaultTableCellRenderer {
        
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row,
            int column) {

        Component c = super.getTableCellRendererComponent(table,
                value, isSelected, hasFocus, row, column);

       
            if (column == 7)
            c.setForeground(Color.BLUE);
            else
                c.setBackground(table.getBackground());
       
        return c;
    }
    }
        
    public void getDocLogView() {
     
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");        
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getDocViewData"});
            list.add(new String[]{"param1", ddtradeid.getSelectedItem().toString()});
            list.add(new String[]{"param2", dddoc.getSelectedItem().toString()});
            list.add(new String[]{"param3", ddoutdoctype.getSelectedItem().toString()});
            list.add(new String[]{"param4", tbref.getText()});
            list.add(new String[]{"param5", ddsite.getSelectedItem().toString()});
            list.add(new String[]{"param6", dfdate.format(dcfrom.getDate())});
            list.add(new String[]{"param7", dfdate.format(dcto.getDate())});
            try {
                jsonString = sendServerPost(list, "", null, "dataServEDI"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getDocViewData(ddtradeid.getSelectedItem().toString(), dddoc.getSelectedItem().toString(), ddoutdoctype.getSelectedItem().toString(), tbref.getText(), ddsite.getSelectedItem().toString(), dfdate.format(dcfrom.getDate()), dfdate.format(dcto.getDate()));
        }
        
        Object[][] data = jsonToData(jsonString);
        
        
        docmodel.setNumRows(0);
        tafile.setText("");
        tablereport.setModel(docmodel);
        tablereport.getColumnModel().getColumn(13).setMaxWidth(50);
        tablereport.getColumnModel().getColumn(14).setMaxWidth(50);
        tablereport.getColumnModel().getColumn(15).setMaxWidth(50);
        

            for (int j = 0; j < data.length; j++) { // adjust column 15 status only
                if (data[j][15].equals("success")) { 
                    data[j][15] = BlueSeerUtils.clickcheck;
                } else {
                    data[j][15] = BlueSeerUtils.clicknocheck;
                }
                if (data[j][16].equals("1")) { 
                    data[j][15] = BlueSeerUtils.clickcheckblue;
                }
           
                if (hm.containsKey(data[j][7].toString())) {
                    data[j][7] = hm.get(data[j][7].toString());
                } 
           
                if (hm.containsKey(data[j][11].toString())) {
                    data[j][11] = hm.get(data[j][11].toString());
                }
                
                
                data[j][13] = BlueSeerUtils.clickleftdoc;
                data[j][14] = BlueSeerUtils.clickrightdoc;
                
            }
            
            
      //  }
       
      // drop column 16
      Object[][] newdata = dropColumn(data, 16);
        
      int i = 0;
      if (newdata != null && newdata.length > 0) {
        for (Object[] rowData : newdata) {
         docmodel.addRow(rowData);
         i++;
        } 
      }
      tbtot.setText(String.valueOf(i));
      
       
   }
    
    public void getFileLogView() {
     
       DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");        
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getFileViewData"});
            list.add(new String[]{"param1", ddtradeid.getSelectedItem().toString()});
            list.add(new String[]{"param2", dddoc.getSelectedItem().toString()});
            list.add(new String[]{"param3", ddoutdoctype.getSelectedItem().toString()});
            list.add(new String[]{"param4", tbref.getText()});
            list.add(new String[]{"param5", ddsite.getSelectedItem().toString()});
            list.add(new String[]{"param6", dfdate.format(dcfrom.getDate())});
            list.add(new String[]{"param7", dfdate.format(dcto.getDate())});
            try {
                jsonString = sendServerPost(list, "", null, "dataServEDI"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getFileViewData(ddtradeid.getSelectedItem().toString(), dddoc.getSelectedItem().toString(), ddoutdoctype.getSelectedItem().toString(), tbref.getText(), ddsite.getSelectedItem().toString(), dfdate.format(dcfrom.getDate()), dfdate.format(dcto.getDate()));
        }
        
        Object[][] data = jsonToData(jsonString);
        
        
        filemodel.setNumRows(0);
        tafile.setText("");
        tablereport.setModel(filemodel);
        
        for (int j = 0; j < data.length; j++) { // 
                if (data[j][10].equals("success")) { 
                    data[j][10] = BlueSeerUtils.clickcheck;
                } else {
                    data[j][10] = BlueSeerUtils.clicknocheck;
                }
           
                if (hm.containsKey(data[j][5].toString())) {
                    data[j][5] = hm.get(data[j][5].toString());
                } 
                data[j][9] = BlueSeerUtils.clickfind;
            }
        
        int i = 0;
      if (data.length > 0) {
        for (Object[] rowData : data) {
         filemodel.addRow(rowData);
         i++;
        } 
      }
      tbtot.setText(String.valueOf(i));
        
   }
    
    
     
   
    /**
     * Creates new form ScrapReportPanel
     */
    

    public EDITransactionBrowse() {
        initComponents();
        setLanguageTags(this);
        
        EDITransactionBrowse.myPopupHandler handler3 = new EDITransactionBrowse.myPopupHandler(tafile);
        tafile.add(handler3.getPopup());
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
    
    public void getdetail(String comkey, String idxkey) {
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");        
        String jsonString = null;
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getEDITransBrowseDetail"});
            list.add(new String[]{"param1", comkey});
            list.add(new String[]{"param2", idxkey});
            try {
                jsonString = sendServerPost(list, "", null, "dataServEDI"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = getEDITransBrowseDetail(comkey, idxkey);
        }
        
        modeldetail.setNumRows(0);
        Object[][] data = jsonToData(jsonString);
        if (data.length > 0) {
            for (Object[] rowData : data) {
             modeldetail.addRow(rowData);
            } 
        }
        
        this.repaint();
        
    }
    
    public void hexReplace(String taname) {
        JTextComponent ta = null;
        char toHex = 0;
        if (taname.equals("tafile")) {
            ta = tafile;
        } 
       
        String text = bsmf.MainFrame.input("Hex Chars: ");
        if (text == null || text.isBlank()) {
            return;
        }
        String[] replacehex = text.split("\\|",-1);
        if (replacehex == null || replacehex.length != 2 || replacehex[0].isBlank()) {
            return;
        }
        char fromHex = (char) Integer.parseInt(replacehex[0], 16);
        if (! replacehex[1].isBlank()) { 
        toHex = (char) Integer.parseInt(replacehex[1], 16);
        }
        
        Document d = ta.getDocument();
        int count = 0;
         try {
             String data = d.getText(0, d.getLength());
             char[] carray = data.toCharArray();
             StringBuilder sb = new StringBuilder();
             
             for (int i = 0; i < carray.length; i++) {
                 if (carray[i] == fromHex) {
                    if (toHex > 0) {
                        sb.append(toHex);  // skip is toHex is blank (0)...removes character
                    } 
                    count++;
                 } else {
                     sb.append(carray[i]);
                 }
             }
                         
             if (taname.equals("tafile")) {
             tafile.setText("");
             tafile.setText(sb.toString());
             }
             bsmf.MainFrame.show("Occurences: " + count);
         } catch (BadLocationException ex) {
             bslog(ex);
             bsmf.MainFrame.show(ex.getMessage());
         }
        
    }
    
    public void hexReplaceUnderScore(String taname) {
        JTextComponent ta = null;
        char toHex = 0;
        if (taname.equals("tafile")) {
            ta = tafile;
        } 
       
        String text = "5f|0a";
        if (text == null || text.isBlank()) {
            return;
        }
        String[] replacehex = text.split("\\|",-1);
        if (replacehex == null || replacehex.length != 2 || replacehex[0].isBlank()) {
            return;
        }
        char fromHex = (char) Integer.parseInt(replacehex[0], 16);
        if (! replacehex[1].isBlank()) { 
        toHex = (char) Integer.parseInt(replacehex[1], 16);
        }
        
        Document d = ta.getDocument();
        int count = 0;
         try {
             String data = d.getText(0, d.getLength());
             char[] carray = data.toCharArray();
             StringBuilder sb = new StringBuilder();
             
             for (int i = 0; i < carray.length; i++) {
                 if (carray[i] == fromHex) {
                    if (toHex > 0) {
                        sb.append(toHex);  // skip is toHex is blank (0)...removes character
                    } 
                    count++;
                 } else {
                     sb.append(carray[i]);
                 }
             }
                         
             if (taname.equals("tafile")) {
             tafile.setText("");
             tafile.setText(sb.toString());
             }
            // bsmf.MainFrame.show("Occurences: " + count);
         } catch (BadLocationException ex) {
             bslog(ex);
             bsmf.MainFrame.show(ex.getMessage());
         }
        
    }
    
    
    public void searchTextArea(String taname) {
        if (taname.equals("tafile")) {
            cleanHighlights(taname);
            String text = bsmf.MainFrame.input("Text: ");
            highlightSearch(tafile, text);            
        }
       
    }
    
    class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
        public MyHighlightPainter(Color color) {
            super(color);
        }
    }
    
    public void cleanHighlights(String taname) {
               
        if (taname.equals("tafile")) {
            Highlighter h = tafile.getHighlighter();
            Highlighter.Highlight[] hl = h.getHighlights();
            for (int i = 0; i < hl.length; i++) {
                if (hl[i].getPainter() instanceof MyHighlightPainter) {
                    
                    h.removeHighlight(hl[i]);
                }
            }
        }
        
    }
    
    public void highlightSearch(JTextComponent ta, String phrase) {
        if (phrase == null || phrase.isBlank()) {
            return;
        }
        Highlighter h = ta.getHighlighter();
        Document d = ta.getDocument();
        int pos = 0;
        int count = 0;
        String text;
         try {
            text = d.getText(0, d.getLength());
            while ((pos = text.toUpperCase().indexOf(phrase.toUpperCase(),pos)) >= 0) {
                h.addHighlight(pos, pos + phrase.length(), myHighlightPainter);
                pos += phrase.length();
                count++;
            }
            bsmf.MainFrame.show("Occurences: " + count);
            
         } catch (BadLocationException ex) {
             bslog(ex);
         }
    }
    
    public void highlightNext(JTextComponent ta) {
       int current = 0; 
       
       if (ta.getName().equals("tafile")) {
            current = tafile.getCaretPosition();
            Highlighter h = tafile.getHighlighter();
            Highlighter.Highlight[] hl = h.getHighlights();
            for (int i = 0; i < hl.length; i++) {
                if (hl[i].getPainter() instanceof MyHighlightPainter) {
                    if (hl[i].getStartOffset() > current) {
                      tafile.setCaretPosition(hl[i].getStartOffset());
                      break;
                    }
                }
            }
        }
      
    }
    
    
    public void initvars(String[] arg) {
       
        buttonGroup1.add(rbDocLog);
        buttonGroup1.add(rbFileLog);
        rbDocLog.setSelected(true);
        
        
        dddoc.removeAllItems();
        ddoutdoctype.removeAllItems();
        ddtradeid.removeAllItems();
        ddsite.removeAllItems();
        
        String defaultsite = "";
        
        ArrayList<String[]> initDataSets = ediData.getEDIInit(this.getClass().getName(), bsmf.MainFrame.userid);
        
        for (String[] s : initDataSets) {
            if (s[0].equals("site")) {
              defaultsite = s[1];  
            }
                      
            if (s[0].equals("sites")) {
              ddsite.addItem(s[1]); 
            }
            
            if (s[0].equals("aliases")) {
              ddtradeid.addItem(s[1]); 
            }
            
            if (s[0].equals("doctypes")) {
              dddoc.addItem(s[1]); 
              ddoutdoctype.addItem(s[1]); 
            }
            
            if (s[0].equals("directories")) {
              String[] dirs = s[1].split(",", -1);
              indir = dirs[0];
              outdir = dirs[1];
              inarch = dirs[2];
              outarch = dirs[3];
              batchdir = dirs[4];
              errordir = dirs[5];
              mapdir = dirs[6];
            }
            
            if (s[0].equals("stds")) {
              String[] k = s[1].split(",", -1);
              hm.put(k[0], k[1]);
            }
            
        }
        
        if (ddsite.getItemCount() > 0) {
            ddsite.setSelectedItem(defaultsite);
        }
        
        dddoc.insertItemAt("", 0);
        dddoc.setSelectedIndex(0);
        ddoutdoctype.insertItemAt("", 0);
        ddoutdoctype.setSelectedIndex(0);
        ddtradeid.insertItemAt("", 0);
        ddtradeid.setSelectedIndex(0);
        
        tbtoterrors.setText("0");
        tbtot.setText("0");
       
        
        java.util.Date now = new java.util.Date();
       
        
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.DAY_OF_YEAR, 1);
        java.util.Date firstday = cal.getTime();
        
       // dcfrom.setDate(firstday);
       dcfrom.setDate(now);
        dcto.setDate(now);
               
        docmodel.setNumRows(0);
        modeldetail.setNumRows(0);
        tablereport.setModel(filemodel);
        tabledetail.setModel(modeldetail);
        
        tablereport.getTableHeader().setReorderingAllowed(false);
        tabledetail.getTableHeader().setReorderingAllowed(false);
        
        // tablereport.getColumnModel().getColumn(0).setCellRenderer(new ButtonRenderer());
         tablereport.getColumnModel().getColumn(0).setMaxWidth(100);
         tabledetail.getColumnModel().getColumn(0).setMaxWidth(100);
         tabledetail.getColumnModel().getColumn(1).setMaxWidth(100);
         tabledetail.getColumnModel().getColumn(2).setMaxWidth(100);
         tabledetail.getColumnModel().getColumn(4).setMaxWidth(200);
       
        
        btdetail.setEnabled(false);
        bthidetext.setEnabled(false);
        detailpanel.setVisible(false);
        textpanel.setVisible(false);
        btreprocess.setEnabled(false);
        btclearstatus.setEnabled(false);
        tafile.setText("");
        tafile.setFont(new Font("monospaced", Font.PLAIN, 12));
        
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        tablepanel = new javax.swing.JPanel();
        summarypanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablereport = new javax.swing.JTable();
        detailpanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabledetail = new javax.swing.JTable();
        textpanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tafile = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        btdetail = new javax.swing.JButton();
        btRun = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        dcfrom = new com.toedter.calendar.JDateChooser();
        dcto = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        bthidetext = new javax.swing.JButton();
        rbFileLog = new javax.swing.JRadioButton();
        rbDocLog = new javax.swing.JRadioButton();
        tbsegdelim = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        lbsegdelim = new javax.swing.JLabel();
        tbref = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        btreprocess = new javax.swing.JButton();
        btclearstatus = new javax.swing.JButton();
        ddtradeid = new javax.swing.JComboBox<>();
        dddoc = new javax.swing.JComboBox<>();
        ddoutdoctype = new javax.swing.JComboBox<>();
        ddsite = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        tbtoterrors = new javax.swing.JLabel();
        tbtot = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();

        jLabel2.setText("jLabel2");

        setBackground(new java.awt.Color(0, 102, 204));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("EDITranBrowse"));
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

        textpanel.setMinimumSize(new java.awt.Dimension(23, 23));
        textpanel.setName(""); // NOI18N
        textpanel.setPreferredSize(new java.awt.Dimension(452, 402));
        textpanel.setLayout(new java.awt.BorderLayout());

        tafile.setColumns(20);
        tafile.setRows(5);
        tafile.setName("tafile"); // NOI18N
        jScrollPane3.setViewportView(tafile);

        textpanel.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        tablepanel.add(textpanel);

        btdetail.setText("Hide Detail");
        btdetail.setName("bthidedetail"); // NOI18N
        btdetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btdetailActionPerformed(evt);
            }
        });

        btRun.setText("Run");
        btRun.setName("btrun"); // NOI18N
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btRunActionPerformed(evt);
            }
        });

        jLabel5.setText("From Date:");
        jLabel5.setName("lblfromdate"); // NOI18N

        jLabel6.setText("To Date:");
        jLabel6.setName("lbltodate"); // NOI18N

        dcfrom.setDateFormatString("yyyy-MM-dd");

        dcto.setDateFormatString("yyyy-MM-dd");

        jLabel3.setText("SenderID");
        jLabel3.setName("lbltpid"); // NOI18N

        jLabel4.setText("DocType");
        jLabel4.setName("lbldoctype"); // NOI18N

        bthidetext.setText("Hide Text");
        bthidetext.setName("bthidetext"); // NOI18N
        bthidetext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bthidetextActionPerformed(evt);
            }
        });

        rbFileLog.setText("FileLogView");
        rbFileLog.setName("cbfilelog"); // NOI18N
        rbFileLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbFileLogActionPerformed(evt);
            }
        });

        rbDocLog.setText("DocLogView");
        rbDocLog.setName("cbdoclog"); // NOI18N
        rbDocLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbDocLogActionPerformed(evt);
            }
        });

        tbsegdelim.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                tbsegdelimFocusLost(evt);
            }
        });

        jLabel1.setText("SegDelim (int)");
        jLabel1.setName("lblsegdelim"); // NOI18N

        jLabel7.setText("Reference");
        jLabel7.setName("lblref"); // NOI18N

        btreprocess.setText("Reprocess");
        btreprocess.setName("btreprocess"); // NOI18N
        btreprocess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btreprocessActionPerformed(evt);
            }
        });

        btclearstatus.setText("Clear Status");
        btclearstatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btclearstatusActionPerformed(evt);
            }
        });

        jLabel10.setText("Out DocType");

        jLabel9.setText("Site:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(jLabel6))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel5))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(dcfrom, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                            .addComponent(dcto, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE))
                        .addGap(31, 31, 31)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(rbFileLog)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(rbDocLog))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btRun)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btdetail)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(bthidetext)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btreprocess)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btclearstatus)
                                .addGap(69, 69, 69)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tbsegdelim, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lbsegdelim, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ddtradeid, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(27, 27, 27)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ddoutdoctype, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dddoc, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btRun)
                        .addComponent(btdetail)
                        .addComponent(bthidetext)
                        .addComponent(tbsegdelim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)
                        .addComponent(lbsegdelim, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btreprocess)
                        .addComponent(btclearstatus))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel5)
                        .addComponent(dcfrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(rbFileLog)
                        .addComponent(rbDocLog))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel6)
                        .addComponent(dcto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel4)
                    .addComponent(dddoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(tbref, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddsite, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3)
                    .addComponent(ddtradeid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ddoutdoctype, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel8.setText("Total Errors:");
        jLabel8.setName("lbltotalerrors"); // NOI18N

        tbtoterrors.setText("0");

        tbtot.setText("0");

        jLabel11.setText("Total Transactions:");
        jLabel11.setName("lbltotaltrans"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(tbtoterrors, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                    .addComponent(tbtot, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtot, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tbtoterrors, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(66, 66, 66))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(tablepanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1279, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(tablepanel, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE))
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
      if (rbDocLog.isSelected()) {
        getDocLogView();
      } else {
        getFileLogView();  
      }
    }//GEN-LAST:event_btRunActionPerformed

    private void btdetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btdetailActionPerformed
       detailpanel.setVisible(false);
       btdetail.setEnabled(false);
    }//GEN-LAST:event_btdetailActionPerformed

    private void tablereportMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablereportMouseClicked
        
        int row = tablereport.rowAtPoint(evt.getPoint());
        int col = tablereport.columnAtPoint(evt.getPoint());
        if ( col == 0 && rbDocLog.isSelected()) {
                getdetail(tablereport.getValueAt(row, 2).toString(), tablereport.getValueAt(row, 1).toString());
                btdetail.setEnabled(true);
                detailpanel.setVisible(true);
        }
        if ( col == 0 && rbFileLog.isSelected()) {
                getdetail(tablereport.getValueAt(row, 2).toString(), "0");
                btdetail.setEnabled(true);
                detailpanel.setVisible(true);
        }
        if ( col == 15) {
                int k = 10;
                      if (BlueSeerUtils.isParsableToInt(tbsegdelim.getText())) {
                      k = Integer.parseInt(tbsegdelim.getText());
                      }
                     try {
                         tafile.setText("");
                         if (! tablereport.getValueAt(row, 7).toString().isEmpty()) {
                             ArrayList<String> segments = getEDIAckFile(tablereport.getValueAt(row, 1).toString(), cleanDirString(batchdir), String.valueOf(k));
                            for (String segment : segments ) {
                                tafile.append(segment);
                                tafile.append("\n");
                            }
                         }
                     } catch (MalformedURLException ex) {
                         MainFrame.bslog(ex);
                     } catch (SmbException ex) {
                         MainFrame.bslog(ex);
                     } catch (IOException ex) {
                         MainFrame.bslog(ex);
                     }
                     tafile.setCaretPosition(0);
                     textpanel.setVisible(true);
                     bthidetext.setEnabled(true);
                
        }
        
        if ( col == 9 && rbFileLog.isSelected()) {
             try {
                 tafile.setText("");
                 if (! tablereport.getValueAt(row, 7).toString().isEmpty()) {
                 ArrayList<String> segments = EDData.getEDIRawFileByFile(tablereport.getValueAt(row, 7).toString(), 
                         tablereport.getValueAt(row, 2).toString(),
                         "edx_comkey",
                         cleanDirString(inarch));  
                    for (String segment : segments ) {
                        tafile.append(segment);
                        tafile.append("\n");
                    }
                 }
             } catch (MalformedURLException ex) {
                 MainFrame.bslog(ex);
             } catch (SmbException ex) {
                 MainFrame.bslog(ex);
             } catch (IOException ex) {
                 MainFrame.bslog(ex);
             }
             tafile.setCaretPosition(0);
             textpanel.setVisible(true);
             bthidetext.setEnabled(true);
             
        }
        
        if ( (col == 13) && rbDocLog.isSelected()) {
              if (tablereport.getValueAt(row, 7).toString().equals("DB")) {
                  return;
              }
              
             try {
                 tafile.setText("");
                 if (! tablereport.getValueAt(row, 8).toString().isEmpty()) {
                 ArrayList<String> segments = EDData.getEDIRawFileByFile(tablereport.getValueAt(row, 8).toString(), 
                         tablereport.getValueAt(row, 2).toString(),
                         "edx_comkey",
                         cleanDirString(batchdir));
                    for (String segment : segments ) {
                        tafile.append(segment);
                        tafile.append("\n");
                    }
                 }
             } catch (MalformedURLException ex) {
                 MainFrame.bslog(ex);
             } catch (SmbException ex) {
                 MainFrame.bslog(ex);
             } catch (IOException ex) {
                 MainFrame.bslog(ex);
             }
             tafile.setCaretPosition(0);
             textpanel.setVisible(true);
             bthidetext.setEnabled(true);
             
        }
          
        if ( (col == 14) && rbDocLog.isSelected()) {
             if (tablereport.getValueAt(row, 11).toString().equals("DB")) {
                  return;
              }
             try {
                 tafile.setText("");
                 if (! tablereport.getValueAt(row, 12).toString().isEmpty()) {
                 ArrayList<String> segments = EDData.getEDIRawFileByFile(tablereport.getValueAt(row, 12).toString(), 
                         tablereport.getValueAt(row, 2).toString(),
                         "edx_comkey",
                         cleanDirString(batchdir));
                    for (String segment : segments ) {
                        tafile.append(segment);
                        tafile.append("\n");
                    }
                 }
             } catch (MalformedURLException ex) {
                 MainFrame.bslog(ex);
             } catch (SmbException ex) {
                 MainFrame.bslog(ex);
             } catch (IOException ex) {
                 MainFrame.bslog(ex);
             }
             tafile.setCaretPosition(0);
             textpanel.setVisible(true);
             bthidetext.setEnabled(true);
             
        }  
      
    }//GEN-LAST:event_tablereportMouseClicked

    private void bthidetextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bthidetextActionPerformed
        textpanel.setVisible(false);
       bthidetext.setEnabled(false);
    }//GEN-LAST:event_bthidetextActionPerformed

    private void tbsegdelimFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tbsegdelimFocusLost
        if (BlueSeerUtils.isParsableToInt(tbsegdelim.getText())) {
        int x = Integer.valueOf(tbsegdelim.getText());
        lbsegdelim.setText(String.valueOf((char) x));
        } else {
            tbsegdelim.setText("10");
        }
    }//GEN-LAST:event_tbsegdelimFocusLost

    private void rbDocLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbDocLogActionPerformed
        detailpanel.setVisible(false);
        btdetail.setEnabled(false);
        if (rbDocLog.isSelected()) {
            tbref.setEnabled(true);
            btreprocess.setEnabled(true);
            btclearstatus.setEnabled(false);
            ddoutdoctype.setEnabled(true);
           // getDocLogView();
        } else {
            tbref.setEnabled(false);
            btreprocess.setEnabled(true);
            btclearstatus.setEnabled(true);
            ddoutdoctype.setEnabled(false);
           // getFileLogView();
        }
    }//GEN-LAST:event_rbDocLogActionPerformed

    private void rbFileLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbFileLogActionPerformed
        detailpanel.setVisible(false);
        btdetail.setEnabled(false);
        if (rbDocLog.isSelected()) {
            tbref.setEnabled(true);
            btreprocess.setEnabled(true);
            btclearstatus.setEnabled(false);
            ddoutdoctype.setEnabled(true);
           // getDocLogView();
        } else {
            tbref.setEnabled(false);
            btreprocess.setEnabled(true);
            btclearstatus.setEnabled(true);
            ddoutdoctype.setEnabled(false);
         //   getFileLogView();
        }
    }//GEN-LAST:event_rbFileLogActionPerformed

    private void btreprocessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btreprocessActionPerformed
        //bsmf.MainFrame.show("This functionality is not available yet");
       // return;
        String r = "unable to reprocess file";
        int[] rows = tablereport.getSelectedRows();
        if (rows.length > 1) {
            bsmf.MainFrame.show(getMessageTag(1162));
            return;
        }
        for (int i : rows) {
            if (tablereport.getValueAt(i, 1) != null && tablereport.getValueAt(i, 2) != null ) {
                String batch = EDData.getEDIBatchFromedi_file(tablereport.getValueAt(i,2).toString());
                if (! batch.isEmpty())
                    try {
                        if (bsmf.MainFrame.remoteDB) {
                        ArrayList<String[]> arrx = new ArrayList<String[]>();
                        arrx.add(new String[]{"id","ediReprocessFile"});
                        arrx.add(new String[]{"batchfilename", batch});
                        r = sendServerPost(arrx, "", null);
                        } else {
                          Path sourcepath = FileSystems.getDefault().getPath(cleanDirString(batchdir) + batch);
                          Path destinationpath = FileSystems.getDefault().getPath(cleanDirString(indir) + "reproc." + batch + "." + Long.toHexString(System.currentTimeMillis()));
                          Files.copy(sourcepath, destinationpath, StandardCopyOption.REPLACE_EXISTING); 
                          r = "file requeued for processing";
                        }
                 
                       bsmf.MainFrame.show(r);
                    } catch (IOException ex) {
                        MainFrame.bslog(ex);
                        bsmf.MainFrame.show(r);
                    } 
            }
        }
        
    }//GEN-LAST:event_btreprocessActionPerformed

    private void btclearstatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btclearstatusActionPerformed
         int[] rows = tablereport.getSelectedRows();
        for (int i : rows) {
            updateEDIFileLogStatusManual(tablereport.getValueAt(i,2).toString());
        }
        if (rows.length >= 1) {
        getFileLogView();
        }
    }//GEN-LAST:event_btclearstatusActionPerformed

    private void tabledetailMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabledetailMouseClicked
        int row = tabledetail.rowAtPoint(evt.getPoint());
        int col = tabledetail.columnAtPoint(evt.getPoint());
        if (col == 3) {
            StringBuilder sb = new StringBuilder();
            int chunkSize = 60;
            for (int i = 0; i < tabledetail.getValueAt(row, 3).toString().length(); i += chunkSize) {
              sb.append(tabledetail.getValueAt(row, 3).toString().substring(i, Math.min(i + chunkSize, tabledetail.getValueAt(row, 3).toString().length()))).append("\n");
            }
            
            tafile.setText(sb.toString());
            
            
            tafile.setCaretPosition(0);
            textpanel.setVisible(true);
            bthidetext.setEnabled(true);
        }
    }//GEN-LAST:event_tabledetailMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btRun;
    private javax.swing.JButton btclearstatus;
    private javax.swing.JButton btdetail;
    private javax.swing.JButton bthidetext;
    private javax.swing.JButton btreprocess;
    private javax.swing.ButtonGroup buttonGroup1;
    private com.toedter.calendar.JDateChooser dcfrom;
    private com.toedter.calendar.JDateChooser dcto;
    private javax.swing.JComboBox<String> dddoc;
    private javax.swing.JComboBox<String> ddoutdoctype;
    private javax.swing.JComboBox<String> ddsite;
    private javax.swing.JComboBox<String> ddtradeid;
    private javax.swing.JPanel detailpanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
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
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lbsegdelim;
    private javax.swing.JRadioButton rbDocLog;
    private javax.swing.JRadioButton rbFileLog;
    private javax.swing.JPanel summarypanel;
    private javax.swing.JTable tabledetail;
    private javax.swing.JPanel tablepanel;
    private javax.swing.JTable tablereport;
    private javax.swing.JTextArea tafile;
    private javax.swing.JTextField tbref;
    private javax.swing.JTextField tbsegdelim;
    private javax.swing.JLabel tbtot;
    private javax.swing.JLabel tbtoterrors;
    private javax.swing.JPanel textpanel;
    // End of variables declaration//GEN-END:variables
}
