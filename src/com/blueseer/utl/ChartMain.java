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

package com.blueseer.utl;


import bsmf.MainFrame;
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.db;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import javax.imageio.ImageIO;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import static javax.swing.text.StyleConstants.Orientation;
import static bsmf.MainFrame.driver;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.tags;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import com.blueseer.adm.admData;
import com.blueseer.far.farData;
import com.blueseer.fgl.fglData;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.currformatDoubleWithSymbol;
import static com.blueseer.utl.BlueSeerUtils.getGlobalColumnTag;
import static com.blueseer.utl.BlueSeerUtils.getTitleTag;
import static com.blueseer.utl.BlueSeerUtils.isParsableToDouble;
import static com.blueseer.utl.BlueSeerUtils.isValidDateStr;
import static com.blueseer.utl.BlueSeerUtils.jsonToData;
import static com.blueseer.utl.BlueSeerUtils.sendServerPost;
import static com.blueseer.utl.ReportPanel.TableReport;
import java.awt.Component;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author vaughnte
 */
public class ChartMain extends javax.swing.JPanel {
 String whichreport = "";
 ArrayList<String> ml = new ArrayList<String>();
 LinkedHashMap<String,String> lhm = new LinkedHashMap<String,String>();
 BufferedImage myimage = null;
 
 boolean isLoad = false;
boolean canUpdate = false;
boolean isAutoPost = false;
ArrayList<String[]> initDataSets = null;
String defaultSite = "";
String defaultCurrency = "";
String defaultCC = "";
String tempdir = "";
String chartfilepath = "";
String symbol = "";

 
    /**
     * Creates new form CCChartView
     */
    public ChartMain() {
        initComponents();
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
                    done_Initialization(key[0]);
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
    


    public void initvars(String[] arg) {
       executeTask("dataInit", arg); 
    }
    
    public String[] getInitialization() {
        initDataSets  = admData.getInitMinimum(this.getClass().getName(), bsmf.MainFrame.userid, "");
        if (initDataSets.isEmpty()) {
           return new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.dataInitError}; 
        } else {
           return new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess}; 
        }
    }  
    
    public void done_Initialization(String rpt) {
      isLoad = true;
      setLanguageTags(this);
        ChartPanel.setVisible(false);
        CodePanel.setVisible(false);
        mainPanel.setBorder(BorderFactory.createTitledBorder(rpt));
        
        ml.clear();
        
         for (String[] s : initDataSets) {
            if (s[0].equals("currency")) {
              defaultCurrency = s[1];  
            }
            if (s[0].equals("site")) {
              defaultSite = s[1];  
            }
            if (s[0].equals("tempdir")) {
              tempdir = s[1];  
            }
            if (s[0].equals("canupdate")) {
              canUpdate = BlueSeerUtils.ConvertStringToBool(s[1]);  
            }
            
        }
        
        if (rpt.equals("chart_scrap")) {
        ml.add("Scrap -- per week");
        ml.add("Scrap -- quantity by code");
        ml.add("Scrap -- dollars by code");
        ml.add("Scrap -- quantity by item");
        ml.add("Scrap -- dollars by item");
        ml.add("Scrap -- quantity by department");
        ml.add("Scrap -- dollars by department");
        }
        
        if (rpt.equals("chart_clock")) {
        ml.add("Clock -- by department");
        ml.add("Clock -- by code");
        ml.add("Clock -- by employee");
        ml.add("Clock -- by week");        
        }
        
        if (rpt.equals("chart_requisition")) {
        ml.add("Requisition -- amount by account");
        ml.add("Requisition -- amount by department");
        ml.add("Requisition -- frequency per userid");
        }
        
        if (rpt.equals("chart_shipping")) {
        ml.add("Shipping -- units per week");
        ml.add("Shipping -- dollars per week");
        }
        
        if (rpt.equals("chart_production")) {
        ml.add("Production -- total units per week");
        ml.add("Production -- total cost per week");
        }
        if (rpt.equals("chart_sales")) {
        ml.add("Sales -- total sales by date");
        ml.add("Sales -- current accounts receivable");
        }
        
        if (rpt.equals("chart_finance")) {
        ml.add("Finance -- income versus expense");
        ml.add("Finance -- expense by account");
        ml.add("Finance -- income by account");
        }
        if (rpt.equals("chart_order")) {
        ml.add("Order -- open orders");
        ml.add("Order -- orders per week total units");
        ml.add("Order -- orders per week total dollars");
        }
        if (rpt.equals("ChartMain")) {
        ml.add("Clock -- by department");
        lhm.put("Clock -- by department", "1");
        ml.add("Clock -- by code");
        lhm.put("Clock -- by code", "1");
        ml.add("Clock -- by employee");
        lhm.put("Clock -- by employee", "1");
        ml.add("Clock -- by week");   
        lhm.put("Clock -- by week", "1");
        ml.add("Finance -- income versus expense");
        lhm.put("Finance -- income versus expense", "0");
        ml.add("Finance -- expense by account");
        lhm.put("Finance -- expense by account", "0");
        ml.add("Finance -- income by account");
        lhm.put("Finance -- income by account", "0");
        ml.add("Order -- open orders");
        lhm.put("Order -- open orders", "0");
        ml.add("Order -- orders per week total units");
        lhm.put("Order -- orders per week total units", "1");
        ml.add("Order -- orders per week total dollars");
        lhm.put("Order -- orders per week total dollars", "1");
        ml.add("Production -- total units per week");
        lhm.put("Production -- total units per week", "1");
        ml.add("Production -- total cost per week");
        lhm.put("Production -- total cost per week", "1");
        ml.add("Inventory -- inventory value by item");
        lhm.put("Inventory -- inventory value by item", "1");
        ml.add("Requisition -- amount by account");
        lhm.put("Requisition -- amount by account", "1");
        ml.add("Requisition -- amount by department");
        lhm.put("Requisition -- amount by department", "1");
        ml.add("Requisition -- frequency per userid");
        lhm.put("Requisition -- frequency per userid", "1");
        ml.add("Sales -- total sales by date");
        lhm.put("Sales -- total sales by date", "1");
        ml.add("Sales -- current accounts receivable");
        lhm.put("Sales -- current accounts receivable", "1");
        ml.add("Scrap -- per week");
        lhm.put("Scrap -- per week", "1");
        ml.add("Scrap -- quantity by code");
        lhm.put("Scrap -- quantity by code", "1");
        ml.add("Scrap -- dollars by code");
        lhm.put("Scrap -- dollars by code", "1");
        ml.add("Scrap -- quantity by item");
        lhm.put("Scrap -- quantity by item", "1");
        ml.add("Scrap -- dollars by item");
        lhm.put("Scrap -- dollars by item", "1");
        ml.add("Scrap -- quantity by department");
        lhm.put("Scrap -- quantity by department", "1");
        ml.add("Scrap -- dollars by department");
        lhm.put("Scrap -- dollars by department", "1");
        ml.add("Shipping -- units per week");
        lhm.put("Shipping -- units per week", "1");
        ml.add("Shipping -- dollars per week");
        lhm.put("Shipping -- dollars per week", "1");
        ml.add("Generic -- xy plot <date,number>");
        lhm.put("Generic -- xy plot <date,number>", "1");
        ml.add("Generic -- xy plot <number,number>");
        lhm.put("Generic -- xy plot <number,number>", "1");
        
        }
        
        ddreport.removeAllItems();
        for (String key : ml) {
         ddreport.addItem(key);
        }
        
        
         int year = Calendar.getInstance().get(Calendar.YEAR);
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.DAY_OF_YEAR, 1);    
                Date start = cal.getTime();

                //set date to last day of 2014
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, 11); // 11 = december
                cal.set(Calendar.DAY_OF_MONTH, 31); // new years eve

                Date end = cal.getTime();
                
                dcFrom.setDate(start);
                dcTo.setDate(end);
        
       
        Currency currency = Currency.getInstance(defaultCurrency);
        symbol = currency.getSymbol(Locale.getDefault()); 
        chartfilepath = tempdir + "/" + "chart.jpg";
       isLoad = false;
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
    
   
     
     
     class CustomRenderer extends BarRenderer
{

   public CustomRenderer()
   {
   }

   public Paint getItemPaint(final int row, final int column)
   {
       Color mycolor = null;
     CategoryDataset cd = getPlot().getDataset();
    if(cd != null)
    {
      String l_rowKey = (String)cd.getRowKey(row);
      String l_colKey = (String)cd.getColumnKey(column);
      int l_value  = cd.getValue(l_rowKey, l_colKey).intValue();
      
      
   //   if (l_value > 20 )
      if (l_colKey.toString().matches("(.*)Mon(.*)"))
      {
          mycolor = Color.GREEN;
      } else {
          mycolor = Color.RED;
      }
    }
       return mycolor;
       
   }
}
     
     
    public void cleanUpOldChartFile() {
        myimage = null;
        ChartPanel.setVisible(false);
        CodePanel.setVisible(false);
        chartlabel.setIcon(null);
        /*
        File f = new File(chartfilepath);
        if(f.exists()) { 
            f.delete();
        }
        */
     }
     
     
     // finance
    public void piechart_expensebyaccount() {
      
        cleanUpOldChartFile();
        ChartPanel.setVisible(true);
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String jsonString = null; 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) { 
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"id","getChartRptPickerData"});
        list.add(new String[]{"func","piechart_expensebyaccount"});
        list.add(new String[]{"param1",dfdate.format(dcFrom.getDate())});
        list.add(new String[]{"param2",dfdate.format(dcTo.getDate())});        
        try {
                jsonString = sendServerPost(list, "", null, "dataServOV"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = OVData.getChartRptPickerData(new String[]{
                "piechart_expensebyaccount",
                dfdate.format(dcFrom.getDate()),
                dfdate.format(dcTo.getDate())
            });
        }
        Object[][] roData = jsonToData(jsonString);
        DefaultPieDataset dataset = new DefaultPieDataset();
        double total = 0.00;
        double displayed = 0.00;
        int i = 0;
        if (roData != null) {
            for (Object[] roData1 : roData) {
                total += bsParseDouble(roData1[1].toString());
                Double amt = bsParseDouble(roData1[1].toString());
                if (i <= Integer.parseInt(ddlimit.getSelectedItem().toString())) {
                   displayed += bsParseDouble(roData1[1].toString()); 
                   dataset.setValue(roData1[0].toString(), amt);
                }
            }
            // other
            if (total > displayed) {
                dataset.setValue("other", (total - displayed));
            }
        }  
        JFreeChart chart = ChartFactory.createPieChart(getTitleTag(5000), dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(("{1} ({2})"), NumberFormat.getCurrencyInstance(), new DecimalFormat("0.00%"));
        plot.setLabelGenerator(gen);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());                
                chartlabel.setIcon(new ImageIcon(ImageIO.read(bais)));
                bais.close();
                baos.close();
                } catch (IOException e) {
                MainFrame.bslog(e);
                }
 }
        
    public void piechart_incomebyaccountcc() {
      
        cleanUpOldChartFile();
        ChartPanel.setVisible(true);
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String jsonString = null; 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) { 
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"id","getChartRptPickerData"});
        list.add(new String[]{"func","piechart_incomebyaccountcc"});
        list.add(new String[]{"param1",dfdate.format(dcFrom.getDate())});
        list.add(new String[]{"param2",dfdate.format(dcTo.getDate())});        
        try {
                jsonString = sendServerPost(list, "", null, "dataServOV"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = OVData.getChartRptPickerData(new String[]{
                "piechart_incomebyaccountcc",
                dfdate.format(dcFrom.getDate()),
                dfdate.format(dcTo.getDate())
            });
        }
        Object[][] roData = jsonToData(jsonString);
        DefaultPieDataset dataset = new DefaultPieDataset();
        double total = 0.00;
        double displayed = 0.00;
        int i = 0;
        if (roData != null) {
            for (Object[] roData1 : roData) {
                total += bsParseDouble(roData1[1].toString());
                Double amt = bsParseDouble(roData1[1].toString());
                if (i <= Integer.parseInt(ddlimit.getSelectedItem().toString())) {
                   displayed += bsParseDouble(roData1[1].toString()); 
                   dataset.setValue(roData1[0].toString(), amt);
                }
            }
            // other
            if (total > displayed) {
                dataset.setValue("other", (total - displayed));
            }
        }  
        JFreeChart chart = ChartFactory.createPieChart(getTitleTag(5001), dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(("{1} ({2})"), NumberFormat.getCurrencyInstance(), new DecimalFormat("0.00%"));
        plot.setLabelGenerator(gen);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());                
                chartlabel.setIcon(new ImageIcon(ImageIO.read(bais)));
                bais.close();
                baos.close();
                } catch (IOException e) {
                MainFrame.bslog(e);
                }
 }
        
    public void piechart_profitandloss() {
      
        cleanUpOldChartFile();
        ChartPanel.setVisible(true);
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String jsonString = null; 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) { 
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"id","getChartRptPickerData"});
        list.add(new String[]{"func","piechart_profitandloss"});
        list.add(new String[]{"param1",dfdate.format(dcFrom.getDate())});
        list.add(new String[]{"param2",dfdate.format(dcTo.getDate())});        
        try {
                jsonString = sendServerPost(list, "", null, "dataServOV"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = OVData.getChartRptPickerData(new String[]{
                "piechart_profitandloss",
                dfdate.format(dcFrom.getDate()),
                dfdate.format(dcTo.getDate())
            });
        }
        Object[][] roData = jsonToData(jsonString);
        DefaultPieDataset dataset = new DefaultPieDataset();
        double total = 0.00;
        double displayed = 0.00;
        int i = 0;
        String type = "";
        if (roData != null) {
            for (Object[] roData1 : roData) {
                total += bsParseDouble(roData1[1].toString());
                Double amt = bsParseDouble(roData1[1].toString());
                if (roData1[0].toString().equals("E")) {
                        type = "Expense";
                } else {
                        type = "Income";
                }
                if (i <= Integer.parseInt(ddlimit.getSelectedItem().toString())) {
                   displayed += bsParseDouble(roData1[1].toString()); 
                   dataset.setValue(type, amt);
                }
            }
            // other
            if (total > displayed) {
                dataset.setValue("other", (total - displayed));
            }
        }  
        JFreeChart chart = ChartFactory.createPieChart(getTitleTag(5026), dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(("{1} ({2})"), NumberFormat.getCurrencyInstance(), new DecimalFormat("0.00%"));
        plot.setLabelGenerator(gen);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());                
                chartlabel.setIcon(new ImageIcon(ImageIO.read(bais)));
                bais.close();
                baos.close();
                } catch (IOException e) {
                MainFrame.bslog(e);
                }
 }
    

    
     // shipments
    public void ShipPerWeekDollarsChart() {
      
        cleanUpOldChartFile();
        ChartPanel.setVisible(true);
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String jsonString = null; 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) { 
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"id","getChartRptPickerData"});
        list.add(new String[]{"func","ShipPerWeekDollarsChart"});
        list.add(new String[]{"param1",dfdate.format(dcFrom.getDate())});
        list.add(new String[]{"param2",dfdate.format(dcTo.getDate())});        
        try {
                jsonString = sendServerPost(list, "", null, "dataServOV"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = OVData.getChartRptPickerData(new String[]{
                "ShipPerWeekDollarsChart",
                dfdate.format(dcFrom.getDate()),
                dfdate.format(dcTo.getDate())
            });
        }
        Object[][] roData = jsonToData(jsonString);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (roData != null) {
            for (Object[] roData1 : roData) {
                dataset.setValue(bsParseDouble(roData1[1].toString()), "Dollars", roData1[0].toString());
            }
        }  
        JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5002), getGlobalColumnTag("week"), getGlobalColumnTag("total"), dataset, PlotOrientation.VERTICAL, true, true, false);
        CategoryItemRenderer renderer = new CustomRenderer();

        Font font = new Font("Dialog", Font.PLAIN, 30);
        CategoryPlot p = chart.getCategoryPlot();

        CategoryAxis axis = p.getDomainAxis();
         ValueAxis axisv = p.getRangeAxis();
         axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
         axisv.setVerticalTickLabels(false);

         p.setRenderer(renderer);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());                
                chartlabel.setIcon(new ImageIcon(ImageIO.read(bais)));
                bais.close();
                baos.close();
                } catch (IOException e) {
                MainFrame.bslog(e);
                }
 }
         
    public void ShipPerWeekUnitsChart() {
      
        cleanUpOldChartFile();
        ChartPanel.setVisible(true);
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String jsonString = null; 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) { 
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"id","getChartRptPickerData"});
        list.add(new String[]{"func","ShipPerWeekUnitsChart"});
        list.add(new String[]{"param1",dfdate.format(dcFrom.getDate())});
        list.add(new String[]{"param2",dfdate.format(dcTo.getDate())});        
        try {
                jsonString = sendServerPost(list, "", null, "dataServOV"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = OVData.getChartRptPickerData(new String[]{
                "ShipPerWeekUnitsChart",
                dfdate.format(dcFrom.getDate()),
                dfdate.format(dcTo.getDate())
            });
        }
        Object[][] roData = jsonToData(jsonString);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (roData != null) {
            for (Object[] roData1 : roData) {
                dataset.setValue(bsParseDouble(roData1[1].toString()), "Sum", roData1[0].toString());
            }
        }  
        JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5003), getGlobalColumnTag("week"), getGlobalColumnTag("total"), dataset, PlotOrientation.VERTICAL, true, true, false);
        CategoryItemRenderer renderer = new CustomRenderer();

        Font font = new Font("Dialog", Font.PLAIN, 30);
        CategoryPlot p = chart.getCategoryPlot();

        CategoryAxis axis = p.getDomainAxis();
         ValueAxis axisv = p.getRangeAxis();
         axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
         axisv.setVerticalTickLabels(false);

         p.setRenderer(renderer);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());                
                chartlabel.setIcon(new ImageIcon(ImageIO.read(bais)));
                bais.close();
                baos.close();
                } catch (IOException e) {
                MainFrame.bslog(e);
                }
 }
    
     
     // production
    public void ProdByWeekFGUnits() {
      
        cleanUpOldChartFile();
        ChartPanel.setVisible(true);
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String jsonString = null; 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) { 
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"id","getChartRptPickerData"});
        list.add(new String[]{"func","ProdByWeekFGUnits"});
        list.add(new String[]{"param1",dfdate.format(dcFrom.getDate())});
        list.add(new String[]{"param2",dfdate.format(dcTo.getDate())});        
        try {
                jsonString = sendServerPost(list, "", null, "dataServOV"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = OVData.getChartRptPickerData(new String[]{
                "ProdByWeekFGUnits",
                dfdate.format(dcFrom.getDate()),
                dfdate.format(dcTo.getDate())
            });
        }
        Object[][] roData = jsonToData(jsonString);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (roData != null) {
            for (Object[] roData1 : roData) {
                dataset.setValue(bsParseDouble(roData1[1].toString()), "Sum", roData1[0].toString());
            }
        }  
        JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5004), getGlobalColumnTag("week"), getGlobalColumnTag("totalqty"), dataset, PlotOrientation.VERTICAL, true, true, false);
        CategoryItemRenderer renderer = new CustomRenderer();

        Font font = new Font("Dialog", Font.PLAIN, 30);
        CategoryPlot p = chart.getCategoryPlot();

        CategoryAxis axis = p.getDomainAxis();
         ValueAxis axisv = p.getRangeAxis();
         axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
         axisv.setVerticalTickLabels(false);

         p.setRenderer(renderer);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());                
                chartlabel.setIcon(new ImageIcon(ImageIO.read(bais)));
                bais.close();
                baos.close();
                } catch (IOException e) {
                MainFrame.bslog(e);
                }
 }

    public void ProdByWeekFGDollars() {
      
        cleanUpOldChartFile();
        ChartPanel.setVisible(true);
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String jsonString = null; 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) { 
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"id","getChartRptPickerData"});
        list.add(new String[]{"func","ProdByWeekFGDollars"});
        list.add(new String[]{"param1",dfdate.format(dcFrom.getDate())});
        list.add(new String[]{"param2",dfdate.format(dcTo.getDate())});        
        try {
                jsonString = sendServerPost(list, "", null, "dataServOV"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = OVData.getChartRptPickerData(new String[]{
                "ProdByWeekFGDollars",
                dfdate.format(dcFrom.getDate()),
                dfdate.format(dcTo.getDate())
            });
        }
        Object[][] roData = jsonToData(jsonString);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (roData != null) {
            for (Object[] roData1 : roData) {
                dataset.setValue(bsParseDouble(roData1[1].toString()), "Dollars", roData1[0].toString());
            }
        }  
        JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5005), getGlobalColumnTag("week"), getGlobalColumnTag("total"), dataset, PlotOrientation.VERTICAL, true, true, false);
        CategoryItemRenderer renderer = new CustomRenderer();

        Font font = new Font("Dialog", Font.PLAIN, 30);
        CategoryPlot p = chart.getCategoryPlot();

        CategoryAxis axis = p.getDomainAxis();
         ValueAxis axisv = p.getRangeAxis();
         axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
         axisv.setVerticalTickLabels(false);

         p.setRenderer(renderer);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());                
                chartlabel.setIcon(new ImageIcon(ImageIO.read(bais)));
                bais.close();
                baos.close();
                } catch (IOException e) {
                MainFrame.bslog(e);
                }
 }
    
      
     // order    
    public void DiscreteOrderPerWeekUnits() {
     cleanUpOldChartFile();
        ChartPanel.setVisible(true);
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String jsonString = null; 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) { 
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"id","getChartRptPickerData"});
        list.add(new String[]{"func","DiscreteOrderPerWeekUnits"});
        list.add(new String[]{"param1",dfdate.format(dcFrom.getDate())});
        list.add(new String[]{"param2",dfdate.format(dcTo.getDate())});        
        try {
                jsonString = sendServerPost(list, "", null, "dataServOV"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = OVData.getChartRptPickerData(new String[]{
                "DiscreteOrderPerWeekUnits",
                dfdate.format(dcFrom.getDate()),
                dfdate.format(dcTo.getDate())
            });
        }
        Object[][] roData = jsonToData(jsonString);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (roData != null) {
            for (Object[] roData1 : roData) {
                dataset.setValue(bsParseDouble(roData1[1].toString()), "Sum", roData1[0].toString());
            }
        }  
        JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5006), getGlobalColumnTag("week"), getGlobalColumnTag("totalqty"), dataset, PlotOrientation.VERTICAL, true, true, false);
        CategoryItemRenderer renderer = new CustomRenderer();

        Font font = new Font("Dialog", Font.PLAIN, 30);
        CategoryPlot p = chart.getCategoryPlot();

        CategoryAxis axis = p.getDomainAxis();
         ValueAxis axisv = p.getRangeAxis();
         axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
         axisv.setVerticalTickLabels(false);

         p.setRenderer(renderer);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());                
                chartlabel.setIcon(new ImageIcon(ImageIO.read(bais)));
                bais.close();
                baos.close();
                } catch (IOException e) {
                MainFrame.bslog(e);
                }
 }
    
    public void DiscreteOrderPerWeekDollars() {
      
        cleanUpOldChartFile();
        ChartPanel.setVisible(true);
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String jsonString = null; 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) { 
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"id","getChartRptPickerData"});
        list.add(new String[]{"func","DiscreteOrderPerWeekDollars"});
        list.add(new String[]{"param1",dfdate.format(dcFrom.getDate())});
        list.add(new String[]{"param2",dfdate.format(dcTo.getDate())});        
        try {
                jsonString = sendServerPost(list, "", null, "dataServOV"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = OVData.getChartRptPickerData(new String[]{
                "DiscreteOrderPerWeekDollars",
                dfdate.format(dcFrom.getDate()),
                dfdate.format(dcTo.getDate())
            });
        }
        Object[][] roData = jsonToData(jsonString);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (roData != null) {
            for (Object[] roData1 : roData) {
                dataset.setValue(bsParseDouble(roData1[1].toString()), "Dollars", roData1[0].toString());
            }
        }  
        JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5007), getGlobalColumnTag("week"), getGlobalColumnTag("total"), dataset, PlotOrientation.VERTICAL, true, true, false);
        CategoryItemRenderer renderer = new CustomRenderer();

        Font font = new Font("Dialog", Font.PLAIN, 30);
        CategoryPlot p = chart.getCategoryPlot();

        CategoryAxis axis = p.getDomainAxis();
         ValueAxis axisv = p.getRangeAxis();
         axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
         axisv.setVerticalTickLabels(false);

         p.setRenderer(renderer);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());                
                chartlabel.setIcon(new ImageIcon(ImageIO.read(bais)));
                bais.close();
                baos.close();
                } catch (IOException e) {
                MainFrame.bslog(e);
                }
 }
    
    public void pcOpenOrdersByCust() {
      
        cleanUpOldChartFile();
        ChartPanel.setVisible(true);
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String jsonString = null; 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) { 
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"id","getChartRptPickerData"});
        list.add(new String[]{"func","pcOpenOrdersByCust"});
        list.add(new String[]{"param1",dfdate.format(dcFrom.getDate())});
        list.add(new String[]{"param2",dfdate.format(dcTo.getDate())});        
        try {
                jsonString = sendServerPost(list, "", null, "dataServOV"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = OVData.getChartRptPickerData(new String[]{
                "pcOpenOrdersByCust",
                dfdate.format(dcFrom.getDate()),
                dfdate.format(dcTo.getDate())
            });
        }
        Object[][] roData = jsonToData(jsonString);
        DefaultPieDataset dataset = new DefaultPieDataset();
        double total = 0.00;
        double displayed = 0.00;
        int i = 0;
        if (roData != null) {
            for (Object[] roData1 : roData) {
                total += bsParseDouble(roData1[1].toString());
                Double amt = bsParseDouble(roData1[1].toString());
                if (i <= Integer.parseInt(ddlimit.getSelectedItem().toString())) {
                   displayed += bsParseDouble(roData1[1].toString()); 
                   dataset.setValue(roData1[0].toString(), amt);
                }
            }
            // other
            if (total > displayed) {
                dataset.setValue("other", (total - displayed));
            }
        }  
        JFreeChart chart = ChartFactory.createPieChart(getTitleTag(5008) + " -- Total: " + currformatDoubleWithSymbol(total,defaultCurrency), dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(("{1} ({2})"), NumberFormat.getCurrencyInstance(), new DecimalFormat("0.00%"));
        plot.setLabelGenerator(gen);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());                
                chartlabel.setIcon(new ImageIcon(ImageIO.read(bais)));
                bais.close();
                baos.close();
                } catch (IOException e) {
                MainFrame.bslog(e);
                }
 }
    

   
    // inventory
    public void piechart_inventorybyitem() {
      
        cleanUpOldChartFile();
        ChartPanel.setVisible(true);
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String jsonString = null; 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) { 
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"id","getChartRptPickerData"});
        list.add(new String[]{"func","piechart_inventorybyitem"});
        list.add(new String[]{"param1",dfdate.format(dcFrom.getDate())});
        list.add(new String[]{"param2",dfdate.format(dcTo.getDate())});        
        try {
                jsonString = sendServerPost(list, "", null, "dataServOV"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = OVData.getChartRptPickerData(new String[]{
                "piechart_inventorybyitem",
                dfdate.format(dcFrom.getDate()),
                dfdate.format(dcTo.getDate())
            });
        }
        Object[][] roData = jsonToData(jsonString);
        DefaultPieDataset dataset = new DefaultPieDataset();
        double total = 0.00;
        double displayed = 0.00;
        int i = 0;
        if (roData != null) {
            for (Object[] roData1 : roData) {
                total += bsParseDouble(roData1[1].toString());
                Double amt = bsParseDouble(roData1[1].toString());
                if (i <= Integer.parseInt(ddlimit.getSelectedItem().toString())) {
                   displayed += bsParseDouble(roData1[1].toString()); 
                   dataset.setValue(roData1[0].toString(), amt);
                }
            }
            // other
            if (total > displayed) {
                dataset.setValue("other", (total - displayed));
            }
        }  
        JFreeChart chart = ChartFactory.createPieChart(getTitleTag(5029) + " -- Total: " + currformatDoubleWithSymbol(total,defaultCurrency), dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(("{1} ({2})"), NumberFormat.getCurrencyInstance(), new DecimalFormat("0.00%"));
        plot.setLabelGenerator(gen);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());                
                chartlabel.setIcon(new ImageIcon(ImageIO.read(bais)));
                bais.close();
                baos.close();
                } catch (IOException e) {
                MainFrame.bslog(e);
                }
 }
     
    
     // sales
    public void piechart_salesbycust() {
      
        cleanUpOldChartFile();
        ChartPanel.setVisible(true);
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String jsonString = null; 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) { 
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"id","getChartRptPickerData"});
        list.add(new String[]{"func","piechart_salesbycust"});
        list.add(new String[]{"param1",dfdate.format(dcFrom.getDate())});
        list.add(new String[]{"param2",dfdate.format(dcTo.getDate())});        
        try {
                jsonString = sendServerPost(list, "", null, "dataServOV"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = OVData.getChartRptPickerData(new String[]{
                "piechart_salesbycust",
                dfdate.format(dcFrom.getDate()),
                dfdate.format(dcTo.getDate())
            });
        }
        Object[][] roData = jsonToData(jsonString);
        DefaultPieDataset dataset = new DefaultPieDataset();
        double total = 0.00;
        double displayed = 0.00;
        int i = 0;
        if (roData != null) {
            for (Object[] roData1 : roData) {
                total += bsParseDouble(roData1[1].toString());
                Double amt = bsParseDouble(roData1[1].toString());
                if (i <= Integer.parseInt(ddlimit.getSelectedItem().toString())) {
                   displayed += bsParseDouble(roData1[1].toString()); 
                   dataset.setValue(roData1[0].toString(), amt);
                }
            }
            // other
            if (total > displayed) {
                dataset.setValue("other", (total - displayed));
            }
        }  
        JFreeChart chart = ChartFactory.createPieChart(getTitleTag(5009) + " -- Total: " + currformatDoubleWithSymbol(total,defaultCurrency), dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(("{1} ({2})"), NumberFormat.getCurrencyInstance(), new DecimalFormat("0.00%"));
        plot.setLabelGenerator(gen);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());                
                chartlabel.setIcon(new ImageIcon(ImageIO.read(bais)));
                bais.close();
                baos.close();
                } catch (IOException e) {
                MainFrame.bslog(e);
                }
 }
        
    public void piechart_custAR() {
      
        cleanUpOldChartFile();
        ChartPanel.setVisible(true);
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String jsonString = null; 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) { 
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"id","getChartRptPickerData"});
        list.add(new String[]{"func","piechart_custAR"});
        list.add(new String[]{"param1",dfdate.format(dcFrom.getDate())});
        list.add(new String[]{"param2",dfdate.format(dcTo.getDate())});        
        try {
                jsonString = sendServerPost(list, "", null, "dataServOV"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = OVData.getChartRptPickerData(new String[]{
                "piechart_custAR",
                dfdate.format(dcFrom.getDate()),
                dfdate.format(dcTo.getDate())
            });
        }
        Object[][] roData = jsonToData(jsonString);
        DefaultPieDataset dataset = new DefaultPieDataset();
        double total = 0.00;
        double displayed = 0.00;
        int i = 0;
        if (roData != null) {
            for (Object[] roData1 : roData) {
                total += bsParseDouble(roData1[1].toString());
                Double amt = bsParseDouble(roData1[1].toString());
                if (i <= Integer.parseInt(ddlimit.getSelectedItem().toString())) {
                   displayed += bsParseDouble(roData1[1].toString()); 
                   dataset.setValue(roData1[0].toString(), amt);
                }
            }
            // other
            if (total > displayed) {
                dataset.setValue("other", (total - displayed));
            }
        }  
        JFreeChart chart = ChartFactory.createPieChart(getTitleTag(5010) + " -- Total: " + currformatDoubleWithSymbol(total,defaultCurrency), dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(("{1} ({2})"), NumberFormat.getCurrencyInstance(), new DecimalFormat("0.00%"));
        plot.setLabelGenerator(gen);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());                
                chartlabel.setIcon(new ImageIcon(ImageIO.read(bais)));
                bais.close();
                baos.close();
                } catch (IOException e) {
                MainFrame.bslog(e);
                }
 }
    
       // Clock 
    public void ClockChartByDept() {
     cleanUpOldChartFile();
        ChartPanel.setVisible(true);
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String jsonString = null; 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) { 
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"id","ClockChartByDept"});
        list.add(new String[]{"func","DiscreteOrderPerWeekUnits"});
        list.add(new String[]{"param1",dfdate.format(dcFrom.getDate())});
        list.add(new String[]{"param2",dfdate.format(dcTo.getDate())});        
        try {
                jsonString = sendServerPost(list, "", null, "dataServOV"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = OVData.getChartRptPickerData(new String[]{
                "ClockChartByDept",
                dfdate.format(dcFrom.getDate()),
                dfdate.format(dcTo.getDate())
            });
        }
        Object[][] roData = jsonToData(jsonString);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (roData != null) {
            for (Object[] roData1 : roData) {
                dataset.setValue(bsParseDouble(roData1[1].toString()), "Sum", roData1[0].toString());
            }
        }  
        JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5011), getGlobalColumnTag("dept"), getGlobalColumnTag("totalqty"), dataset, PlotOrientation.VERTICAL, true, true, false);
        CategoryItemRenderer renderer = new CustomRenderer();

        Font font = new Font("Dialog", Font.PLAIN, 30);
        CategoryPlot p = chart.getCategoryPlot();

        CategoryAxis axis = p.getDomainAxis();
         ValueAxis axisv = p.getRangeAxis();
         axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
         axisv.setVerticalTickLabels(false);

         p.setRenderer(renderer);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());                
                chartlabel.setIcon(new ImageIcon(ImageIO.read(bais)));
                bais.close();
                baos.close();
                } catch (IOException e) {
                MainFrame.bslog(e);
                }
 }
        
    public void ClockChartByCode() {
     cleanUpOldChartFile();
        ChartPanel.setVisible(true);
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String jsonString = null; 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) { 
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"id","ClockChartByCode"});
        list.add(new String[]{"func","DiscreteOrderPerWeekUnits"});
        list.add(new String[]{"param1",dfdate.format(dcFrom.getDate())});
        list.add(new String[]{"param2",dfdate.format(dcTo.getDate())});        
        try {
                jsonString = sendServerPost(list, "", null, "dataServOV"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = OVData.getChartRptPickerData(new String[]{
                "ClockChartByCode",
                dfdate.format(dcFrom.getDate()),
                dfdate.format(dcTo.getDate())
            });
        }
        Object[][] roData = jsonToData(jsonString);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (roData != null) {
            for (Object[] roData1 : roData) {
                dataset.setValue(bsParseDouble(roData1[1].toString()), "Sum", roData1[0].toString());
            }
        }  
        JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5012), getGlobalColumnTag("code"), getGlobalColumnTag("totalqty"), dataset, PlotOrientation.VERTICAL, true, true, false);
        CategoryItemRenderer renderer = new CustomRenderer();

        Font font = new Font("Dialog", Font.PLAIN, 30);
        CategoryPlot p = chart.getCategoryPlot();

        CategoryAxis axis = p.getDomainAxis();
         ValueAxis axisv = p.getRangeAxis();
         axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
         axisv.setVerticalTickLabels(false);

         p.setRenderer(renderer);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());                
                chartlabel.setIcon(new ImageIcon(ImageIO.read(bais)));
                bais.close();
                baos.close();
                } catch (IOException e) {
                MainFrame.bslog(e);
                }
 }
        
    public void ClockChartByEmp() {
     cleanUpOldChartFile();
        ChartPanel.setVisible(true);
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String jsonString = null; 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) { 
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"id","ClockChartByEmp"});
        list.add(new String[]{"func","DiscreteOrderPerWeekUnits"});
        list.add(new String[]{"param1",dfdate.format(dcFrom.getDate())});
        list.add(new String[]{"param2",dfdate.format(dcTo.getDate())});        
        try {
                jsonString = sendServerPost(list, "", null, "dataServOV"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = OVData.getChartRptPickerData(new String[]{
                "ClockChartByEmp",
                dfdate.format(dcFrom.getDate()),
                dfdate.format(dcTo.getDate())
            });
        }
        Object[][] roData = jsonToData(jsonString);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (roData != null) {
            for (Object[] roData1 : roData) {
                dataset.setValue(bsParseDouble(roData1[1].toString()), "Sum", roData1[0].toString());
            }
        }  
        JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5013), getGlobalColumnTag("employee"), getGlobalColumnTag("totalqty"), dataset, PlotOrientation.VERTICAL, true, true, false);
        CategoryItemRenderer renderer = new CustomRenderer();

        Font font = new Font("Dialog", Font.PLAIN, 30);
        CategoryPlot p = chart.getCategoryPlot();

        CategoryAxis axis = p.getDomainAxis();
         ValueAxis axisv = p.getRangeAxis();
         axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
         axisv.setVerticalTickLabels(false);

         p.setRenderer(renderer);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());                
                chartlabel.setIcon(new ImageIcon(ImageIO.read(bais)));
                bais.close();
                baos.close();
                } catch (IOException e) {
                MainFrame.bslog(e);
                }
 }
        
    public void HoursPerWeek() {
     cleanUpOldChartFile();
        ChartPanel.setVisible(true);
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String jsonString = null; 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) { 
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"id","ClockChartByEmp"});
        list.add(new String[]{"func","HoursPerWeek"});
        list.add(new String[]{"param1",dfdate.format(dcFrom.getDate())});
        list.add(new String[]{"param2",dfdate.format(dcTo.getDate())});        
        try {
                jsonString = sendServerPost(list, "", null, "dataServOV"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = OVData.getChartRptPickerData(new String[]{
                "HoursPerWeek",
                dfdate.format(dcFrom.getDate()),
                dfdate.format(dcTo.getDate())
            });
        }
        Object[][] roData = jsonToData(jsonString);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (roData != null) {
            for (Object[] roData1 : roData) {
                dataset.setValue(bsParseDouble(roData1[1].toString()), "Sum", roData1[0].toString());
            }
        }  
        JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5014), getGlobalColumnTag("week"), getGlobalColumnTag("totalqty"), dataset, PlotOrientation.VERTICAL, true, true, false);
        CategoryItemRenderer renderer = new CustomRenderer();

        Font font = new Font("Dialog", Font.PLAIN, 30);
        CategoryPlot p = chart.getCategoryPlot();

        CategoryAxis axis = p.getDomainAxis();
         ValueAxis axisv = p.getRangeAxis();
         axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
         axisv.setVerticalTickLabels(false);

         p.setRenderer(renderer);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());                
                chartlabel.setIcon(new ImageIcon(ImageIO.read(bais)));
                bais.close();
                baos.close();
                } catch (IOException e) {
                MainFrame.bslog(e);
                }
 }
    
        
    // Requisition charts
    public void ReqDollarsByAcct() {
      
        cleanUpOldChartFile();
        ChartPanel.setVisible(true);
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String jsonString = null; 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) { 
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"id","getChartRptPickerData"});
        list.add(new String[]{"func","ReqDollarsByAcct"});
        list.add(new String[]{"param1",dfdate.format(dcFrom.getDate())});
        list.add(new String[]{"param2",dfdate.format(dcTo.getDate())});        
        try {
                jsonString = sendServerPost(list, "", null, "dataServOV"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = OVData.getChartRptPickerData(new String[]{
                "ReqDollarsByAcct",
                dfdate.format(dcFrom.getDate()),
                dfdate.format(dcTo.getDate())
            });
        }
        Object[][] roData = jsonToData(jsonString);
        DefaultPieDataset dataset = new DefaultPieDataset();
        double total = 0.00;
        double displayed = 0.00;
        int i = 0;
        if (roData != null) {
            for (Object[] roData1 : roData) {
                total += bsParseDouble(roData1[1].toString());
                Double amt = bsParseDouble(roData1[1].toString());
                if (i <= Integer.parseInt(ddlimit.getSelectedItem().toString())) {
                   displayed += bsParseDouble(roData1[1].toString()); 
                   dataset.setValue(roData1[0].toString(), amt);
                }
            }
            // other
            if (total > displayed) {
                dataset.setValue("other", (total - displayed));
            }
        }  
        JFreeChart chart = ChartFactory.createPieChart(getTitleTag(5015), dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(("{1} ({2})"), NumberFormat.getCurrencyInstance(), new DecimalFormat("0.00%"));
        plot.setLabelGenerator(gen);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());                
                chartlabel.setIcon(new ImageIcon(ImageIO.read(bais)));
                bais.close();
                baos.close();
                } catch (IOException e) {
                MainFrame.bslog(e);
                }
 }
        
    public void ReqDollarsByDept() {
      
        cleanUpOldChartFile();
        ChartPanel.setVisible(true);
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String jsonString = null; 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) { 
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"id","getChartRptPickerData"});
        list.add(new String[]{"func","ReqDollarsByDept"});
        list.add(new String[]{"param1",dfdate.format(dcFrom.getDate())});
        list.add(new String[]{"param2",dfdate.format(dcTo.getDate())});        
        try {
                jsonString = sendServerPost(list, "", null, "dataServOV"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = OVData.getChartRptPickerData(new String[]{
                "ReqDollarsByDept",
                dfdate.format(dcFrom.getDate()),
                dfdate.format(dcTo.getDate())
            });
        }
        Object[][] roData = jsonToData(jsonString);
        DefaultPieDataset dataset = new DefaultPieDataset();
        double total = 0.00;
        double displayed = 0.00;
        int i = 0;
        if (roData != null) {
            for (Object[] roData1 : roData) {
                total += bsParseDouble(roData1[1].toString());
                Double amt = bsParseDouble(roData1[1].toString());
                if (i <= Integer.parseInt(ddlimit.getSelectedItem().toString())) {
                   displayed += bsParseDouble(roData1[1].toString()); 
                   dataset.setValue(roData1[0].toString(), amt);
                }
            }
            // other
            if (total > displayed) {
                dataset.setValue("other", (total - displayed));
            }
        }  
        JFreeChart chart = ChartFactory.createPieChart(getTitleTag(5016), dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(("{1} ({2})"), NumberFormat.getCurrencyInstance(), new DecimalFormat("0.00%"));
        plot.setLabelGenerator(gen);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());                
                chartlabel.setIcon(new ImageIcon(ImageIO.read(bais)));
                bais.close();
                baos.close();
                } catch (IOException e) {
                MainFrame.bslog(e);
                }
 }
    
    public void ReqDollarsByUser() {
      
        cleanUpOldChartFile();
        ChartPanel.setVisible(true);
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String jsonString = null; 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) { 
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"id","getChartRptPickerData"});
        list.add(new String[]{"func","ReqDollarsByUser"});
        list.add(new String[]{"param1",dfdate.format(dcFrom.getDate())});
        list.add(new String[]{"param2",dfdate.format(dcTo.getDate())});        
        try {
                jsonString = sendServerPost(list, "", null, "dataServOV"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = OVData.getChartRptPickerData(new String[]{
                "ReqDollarsByUser",
                dfdate.format(dcFrom.getDate()),
                dfdate.format(dcTo.getDate())
            });
        }
        Object[][] roData = jsonToData(jsonString);
        DefaultPieDataset dataset = new DefaultPieDataset();
        double total = 0.00;
        double displayed = 0.00;
        int i = 0;
        if (roData != null) {
            for (Object[] roData1 : roData) {
                total += bsParseDouble(roData1[1].toString());
                Double amt = bsParseDouble(roData1[1].toString());
                if (i <= Integer.parseInt(ddlimit.getSelectedItem().toString())) {
                   displayed += bsParseDouble(roData1[1].toString()); 
                   dataset.setValue(roData1[0].toString(), amt);
                }
            }
            // other
            if (total > displayed) {
                dataset.setValue("other", (total - displayed));
            }
        }  
        JFreeChart chart = ChartFactory.createPieChart(getTitleTag(5017), dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator(("{1} ({2})"), NumberFormat.getCurrencyInstance(), new DecimalFormat("0.00%"));
        plot.setLabelGenerator(gen);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());                
                chartlabel.setIcon(new ImageIcon(ImageIO.read(bais)));
                bais.close();
                baos.close();
                } catch (IOException e) {
                MainFrame.bslog(e);
                }
 }
    
        
    // Scrap charts
    public void ScrapPerWeek() {
     cleanUpOldChartFile();
        ChartPanel.setVisible(true);
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String jsonString = null; 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) { 
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"id","ClockChartByEmp"});
        list.add(new String[]{"func","ScrapPerWeek"});
        list.add(new String[]{"param1",dfdate.format(dcFrom.getDate())});
        list.add(new String[]{"param2",dfdate.format(dcTo.getDate())});        
        try {
                jsonString = sendServerPost(list, "", null, "dataServOV"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = OVData.getChartRptPickerData(new String[]{
                "ScrapPerWeek",
                dfdate.format(dcFrom.getDate()),
                dfdate.format(dcTo.getDate())
            });
        }
        Object[][] roData = jsonToData(jsonString);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (roData != null) {
            for (Object[] roData1 : roData) {
                dataset.setValue(bsParseDouble(roData1[1].toString()), "Sum", roData1[0].toString());
            }
        }  
        JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5018), getGlobalColumnTag("week"), getGlobalColumnTag("totalqty"), dataset, PlotOrientation.VERTICAL, true, true, false);
        CategoryItemRenderer renderer = new CustomRenderer();

        Font font = new Font("Dialog", Font.PLAIN, 30);
        CategoryPlot p = chart.getCategoryPlot();

        CategoryAxis axis = p.getDomainAxis();
         ValueAxis axisv = p.getRangeAxis();
         axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
         axisv.setVerticalTickLabels(false);

         p.setRenderer(renderer);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());                
                chartlabel.setIcon(new ImageIcon(ImageIO.read(bais)));
                bais.close();
                baos.close();
                } catch (IOException e) {
                MainFrame.bslog(e);
                }
 }
        
    public void PartAccumQty() {
     cleanUpOldChartFile();
        ChartPanel.setVisible(true);
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String jsonString = null; 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) { 
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"id","ClockChartByEmp"});
        list.add(new String[]{"func","PartAccumQty"});
        list.add(new String[]{"param1",dfdate.format(dcFrom.getDate())});
        list.add(new String[]{"param2",dfdate.format(dcTo.getDate())});        
        try {
                jsonString = sendServerPost(list, "", null, "dataServOV"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = OVData.getChartRptPickerData(new String[]{
                "PartAccumQty",
                dfdate.format(dcFrom.getDate()),
                dfdate.format(dcTo.getDate())
            });
        }
        Object[][] roData = jsonToData(jsonString);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (roData != null) {
            for (Object[] roData1 : roData) {
                dataset.setValue(bsParseDouble(roData1[1].toString()), "Sum", roData1[0].toString());
            }
        }  
        JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5020), getGlobalColumnTag("item"), getGlobalColumnTag("totalqty"), dataset, PlotOrientation.VERTICAL, true, true, false);
        CategoryItemRenderer renderer = new CustomRenderer();

        Font font = new Font("Dialog", Font.PLAIN, 30);
        CategoryPlot p = chart.getCategoryPlot();

        CategoryAxis axis = p.getDomainAxis();
         ValueAxis axisv = p.getRangeAxis();
         axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
         axisv.setVerticalTickLabels(false);

         p.setRenderer(renderer);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());                
                chartlabel.setIcon(new ImageIcon(ImageIO.read(bais)));
                bais.close();
                baos.close();
                } catch (IOException e) {
                MainFrame.bslog(e);
                }
 }
    
    public void PartAccumDollar() {
     cleanUpOldChartFile();
        ChartPanel.setVisible(true);
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        String jsonString = null; 
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) { 
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{"id","ClockChartByEmp"});
        list.add(new String[]{"func","PartAccumDollar"});
        list.add(new String[]{"param1",dfdate.format(dcFrom.getDate())});
        list.add(new String[]{"param2",dfdate.format(dcTo.getDate())});        
        try {
                jsonString = sendServerPost(list, "", null, "dataServOV"); 
            } catch (IOException ex) {
                bslog(ex);
            }
        } else {
            jsonString = OVData.getChartRptPickerData(new String[]{
                "PartAccumDollar",
                dfdate.format(dcFrom.getDate()),
                dfdate.format(dcTo.getDate())
            });
        }
        Object[][] roData = jsonToData(jsonString);
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (roData != null) {
            for (Object[] roData1 : roData) {
                dataset.setValue(bsParseDouble(roData1[1].toString()), "Sum", roData1[0].toString());
            }
        }  
        JFreeChart chart = ChartFactory.createBarChart(getTitleTag(5022), getGlobalColumnTag("item"), getGlobalColumnTag("total"), dataset, PlotOrientation.VERTICAL, true, true, false);
        CategoryItemRenderer renderer = new CustomRenderer();

        Font font = new Font("Dialog", Font.PLAIN, 30);
        CategoryPlot p = chart.getCategoryPlot();

        CategoryAxis axis = p.getDomainAxis();
         ValueAxis axisv = p.getRangeAxis();
         axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
         axisv.setVerticalTickLabels(false);

         p.setRenderer(renderer);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());                
                chartlabel.setIcon(new ImageIcon(ImageIO.read(bais)));
                bais.close();
                baos.close();
                } catch (IOException e) {
                MainFrame.bslog(e);
                }
 }
    
    
    
    public void GenericDataXY(String reporttitle) {
    try {
          List<String> filecontent = getInput();
          cleanUpOldChartFile();
          ChartPanel.setVisible(true);
          JFreeChart chart;      
          boolean error = false;     

                if (reporttitle.equals("Generic -- xy plot <date,number>")) {
                TimeSeriesCollection dataset = new TimeSeriesCollection(); 
                TimeSeries s2 = new TimeSeries("XY Data");
                String[] arr;
                    for (String s : filecontent) {
                        if (s.isBlank()) {
                            continue;
                        }
                        arr = s.split(",");
                        if (arr == null || arr.length != 2 || ! isValidDateStr(arr[0])) {
                            error = true;
                            break;
                        }
                        String[] date = arr[0].split("-");
                        int year = Integer.parseInt(date[0]);
                        int month = Integer.parseInt(date[1]);
                        int day = Integer.parseInt(date[2]);
                        s2.add(new Day(day, month, year),Double.parseDouble(arr[1]));
                    }
                  if (error) {
                      bsmf.MainFrame.show("Invalid date format");
                      return;
                  }  
                  dataset.addSeries(s2);
                  chart = ChartFactory.createXYLineChart("Generic XY plot" + "\n", getGlobalColumnTag("x"), getGlobalColumnTag("y"), dataset, PlotOrientation.VERTICAL, true, true, false);
                
                } else {
                   XYSeriesCollection dataset = new XYSeriesCollection();
                   XYSeries s1 = new XYSeries("XY Data");
                   String[] arr;
                    for (String s : filecontent) {
                        
                        arr = s.split(",");
                        if (arr == null || arr.length != 2 || ! isParsableToDouble(arr[0]) || ! isParsableToDouble(arr[1])) {
                            error = true;
                            break;
                        }
                        s1.add(Double.valueOf(arr[0]),Double.valueOf(arr[1]));
                    }
                    if (error) {
                      bsmf.MainFrame.show("Invalid number format");
                      return;
                  } 
                    dataset.addSeries(s1);
                  chart = ChartFactory.createXYLineChart("Generic XY plot" + "\n", getGlobalColumnTag("x"), getGlobalColumnTag("y"), dataset, PlotOrientation.VERTICAL, true, true, false);
                                   
                }
               // JFreeChart chart = ChartFactory.createBarChart("Generic XY plot" + "\n", getGlobalColumnTag("x"), getGlobalColumnTag("y"), dataset, PlotOrientation.VERTICAL, true, true, false);
                /*
                CategoryItemRenderer renderer = new CustomRenderer();
                CategoryPlot p = chart.getCategoryPlot();
                CategoryAxis axis = p.getDomainAxis();
                ValueAxis axisv = p.getRangeAxis();
                axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
                axisv.setVerticalTickLabels(false);
                p.setRenderer(renderer);
                */
                XYPlot plot = (XYPlot) chart.getPlot();
                
                if (reporttitle.equals("Generic -- xy plot <date,number>")) {
                DateAxis dateAxis = new DateAxis();
                dateAxis.setDateFormatOverride(new SimpleDateFormat("dd-MM-yyyy"));
                plot.setDomainAxis(dateAxis);
                }
                
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
                    ChartUtilities.writeChartAsJPEG(baos, chart, jPanel2.getWidth(), this.getHeight() - 150);
                    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                    myimage = ImageIO.read(bais);
                    ImageIcon myicon = new ImageIcon(myimage);
                    myicon.getImage().flush();   
                    chartlabel.setIcon(myicon);
                    bais.close();
                    baos.close();
                    } catch (IOException e) {
                    MainFrame.bslog(e);
                    }
            
        } catch (Exception e) {
            MainFrame.bslog(e);
        } 
 }

    public File getfile(String title) {
        
        File file = null;
        JFileChooser jfc = new JFileChooser(FileSystems.getDefault().getPath("edi").toFile());
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setDialogTitle(title);
        int returnVal = jfc.showOpenDialog(this);
       

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
            file = jfc.getSelectedFile();
            String SourceDir = file.getAbsolutePath();
            file = new File(SourceDir);
               if (! file.exists()) {
                 return null;
               }
            }
            catch (Exception ex) {
            ex.printStackTrace();
            }
        } 
        return file;
    }
        
    public List<String> getInput() {
        List<String> lines = new ArrayList<String>();
        File infile = getfile("Open Input Data File");
        if (infile != null) {
            try {   
                lines = Files.readAllLines(infile.toPath());
                
            } catch (IOException ex) {
                bslog(ex);
            }   
        } 
        return lines;
    }
    
     
    class MyPrintable implements Printable {
  ImageIcon printImage = new javax.swing.ImageIcon(chartfilepath);

  
  
  public int print(Graphics g, PageFormat pf, int pageIndex) {
    Graphics2D g2d = (Graphics2D) g;
   
    g.translate((int) (pf.getImageableX()), (int) (pf.getImageableY()));
    if (pageIndex == 0) {
    
        double pageWidth = pf.getImageableWidth();
      double pageHeight = pf.getImageableHeight();
      double imageWidth = printImage.getIconWidth();
      double imageHeight = printImage.getIconHeight();
      double scaleX = pageWidth / imageWidth;
      double scaleY = pageHeight / imageHeight;
      double scaleFactor = Math.min(scaleX, scaleY);
      g2d.scale(scaleFactor, scaleFactor);
    // pf.setOrientation(PageFormat.LANDSCAPE);
        g.drawImage(printImage.getImage(), 0, 0, null);
      return Printable.PAGE_EXISTS;
    }
    return Printable.NO_SUCH_PAGE;
  }
}
      
      
    public class ImagePrintable implements Printable {

        private double          x, y, width;

        private int             orientation;

        private BufferedImage   image;

        public ImagePrintable(PrinterJob printJob, BufferedImage image) {
            PageFormat pageFormat = printJob.defaultPage();
            this.x = pageFormat.getImageableX();
            this.y = pageFormat.getImageableY();
            this.width = pageFormat.getImageableWidth();
            this.orientation = pageFormat.getOrientation();
            this.image = image;
            
        }

        @Override
        public int print(Graphics g, PageFormat pageFormat, int pageIndex)
                throws PrinterException {
            if (pageIndex == 0) {
                int pWidth = 0;
                int pHeight = 0;
                if (orientation == PageFormat.PORTRAIT) {
                    pWidth = (int) Math.min(width, (double) image.getWidth());
                    pHeight = pWidth * image.getHeight() / image.getWidth();
                } else {
                    pHeight = (int) Math.min(width, (double) image.getHeight());
                    pWidth = pHeight * image.getWidth() / image.getHeight();
                }
                pWidth = 600;
                pHeight = 400;
                g.drawImage(image, (int) x, (int) y, pWidth, pHeight, null);
                return PAGE_EXISTS;
            } else {
                return NO_SUCH_PAGE;
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        mainPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        dcFrom = new com.toedter.calendar.JDateChooser();
        dcTo = new com.toedter.calendar.JDateChooser();
        jLabel3 = new javax.swing.JLabel();
        btChart = new javax.swing.JButton();
        btprint = new javax.swing.JButton();
        ddlimit = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        ddreport = new javax.swing.JComboBox<>();
        cbcodes = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        ChartPanel = new javax.swing.JPanel();
        chartlabel = new javax.swing.JLabel();
        CodePanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tacodes = new javax.swing.JTextArea();

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        setBackground(new java.awt.Color(0, 102, 204));

        mainPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Chart View"));
        mainPanel.setName("panelmain"); // NOI18N

        jLabel2.setText("From Date");
        jLabel2.setName("lblfromdate"); // NOI18N

        dcFrom.setDateFormatString("yyyy-MM-dd");

        dcTo.setDateFormatString("yyyy-MM-dd");

        jLabel3.setText("To Date");
        jLabel3.setName("lbltodate"); // NOI18N

        btChart.setText("Chart");
        btChart.setName("btchart"); // NOI18N
        btChart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btChartActionPerformed(evt);
            }
        });

        btprint.setText("Print");
        btprint.setName("btprint"); // NOI18N
        btprint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btprintActionPerformed(evt);
            }
        });

        ddlimit.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "3", "5", "10", "25" }));

        jLabel1.setText("Limit");

        cbcodes.setText("codes");
        cbcodes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbcodesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ddreport, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dcFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dcTo, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btChart)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btprint)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ddlimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbcodes)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btChart)
                        .addComponent(btprint)
                        .addComponent(ddlimit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)
                        .addComponent(cbcodes))
                    .addComponent(ddreport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel3)
                        .addComponent(dcTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jLabel2)
                        .addComponent(dcFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel2.setPreferredSize(new java.awt.Dimension(900, 376));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        ChartPanel.setPreferredSize(new java.awt.Dimension(450, 376));

        javax.swing.GroupLayout ChartPanelLayout = new javax.swing.GroupLayout(ChartPanel);
        ChartPanel.setLayout(ChartPanelLayout);
        ChartPanelLayout.setHorizontalGroup(
            ChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(chartlabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 485, Short.MAX_VALUE)
        );
        ChartPanelLayout.setVerticalGroup(
            ChartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(chartlabel, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
        );

        jPanel2.add(ChartPanel);

        CodePanel.setPreferredSize(new java.awt.Dimension(450, 376));

        tacodes.setColumns(20);
        tacodes.setRows(5);
        jScrollPane2.setViewportView(tacodes);

        javax.swing.GroupLayout CodePanelLayout = new javax.swing.GroupLayout(CodePanel);
        CodePanel.setLayout(CodePanelLayout);
        CodePanelLayout.setHorizontalGroup(
            CodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(CodePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
                .addContainerGap())
        );
        CodePanelLayout.setVerticalGroup(
            CodePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, CodePanelLayout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 365, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.add(CodePanel);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        add(mainPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void btChartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btChartActionPerformed
        DateFormat dfdate = new SimpleDateFormat("yyyy-MM-dd");
        whichreport = ddreport.getSelectedItem().toString();
        isLoad = true;
         cbcodes.setSelected(false);
        isLoad = false;
        
        if (dcTo.getDate() == null || dcFrom.getDate() == null) {
                bsmf.MainFrame.show("Must choose a date for both From and To");
                return;
            }
        
        int days = (int)( (dcTo.getDate().getTime() - dcFrom.getDate().getTime()) / (1000 * 60 * 60 * 24) );
        
        tacodes.setText("");
        ArrayList weekcodes = OVData.getWeekNbrByDate(dfdate.format(dcFrom.getDate()), String.valueOf(days));
        String weeknumbers = "";

        for (int i = 0; i < weekcodes.size(); i++) {
          weeknumbers += (weekcodes.get(i)) + "\n";
        }
               
        if (whichreport.equals("")) {
            bsmf.MainFrame.show("whichreport is empty");
        }
        
       
         if (whichreport.equals("Requisition -- amount by account")) {
            ReqDollarsByAcct();
        }
        
        if (whichreport.equals("Requisition -- amount by department")) {
            ReqDollarsByDept();
        }
        
        if (whichreport.equals("Requisition -- frequency per userid")) {
            ReqDollarsByUser();
        }
        
        
         if (whichreport.equals("Shipping -- units per week")) {
            ShipPerWeekUnitsChart();
            tacodes.setText(weeknumbers);
        //    CodePanel.setVisible(true);
        } // if whichreport
        
        if (whichreport.equals("Shipping -- dollars per week")) {
            ShipPerWeekDollarsChart();
            tacodes.setText(weeknumbers);
         //   CodePanel.setVisible(true);
        } // if whichreport
        
          if (whichreport.equals("Production -- total units per week")) {
            ProdByWeekFGUnits();
            tacodes.setText(weeknumbers);
        //    CodePanel.setVisible(true);
        } // if whichreport
        
        if (whichreport.equals("Production -- total cost per week")) {
            
            ProdByWeekFGDollars();
            tacodes.setText(weeknumbers);
         //   CodePanel.setVisible(true);
        } // if whichreport
        
       
        
        if (whichreport.equals("Order -- orders per week total units")) {
            DiscreteOrderPerWeekUnits();
            tacodes.setText(weeknumbers);
          //  CodePanel.setVisible(true);
        } // if whichreport
        
        if (whichreport.equals("Order -- open orders")) {
            pcOpenOrdersByCust();
        } // if whichreport
        
        if (whichreport.equals("Order -- orders per week total dollars")) {
            DiscreteOrderPerWeekDollars();
            tacodes.setText(weeknumbers);
           // CodePanel.setVisible(true);
        } // if whichreport
                
        if (whichreport.equals("Sales -- total sales by date")) {
            piechart_salesbycust();
        }
        
        if (whichreport.equals("Inventory -- inventory value by item")) {
            piechart_inventorybyitem();
        }
        
        if (whichreport.equals("Finance -- income versus expense")) {
            piechart_profitandloss();
        }
        
        if (whichreport.equals("Finance -- expense by account")) {
            piechart_expensebyaccount();
        }
        if (whichreport.equals("Finance -- income by account")) {
            piechart_incomebyaccountcc();
        }
        
        if (whichreport.equals("Sales -- current accounts receivable")) {
            piechart_custAR();
        }
        
         if (whichreport.equals("Clock -- by department")) {
            ClockChartByDept();
            tacodes.setText("");
            ArrayList codes = OVData.getdeptanddesclist();
            String str = "";
            
            for (int i = 0; i < codes.size(); i++) {
                str += (codes.get(i)) + "\n";
            }
            tacodes.setText(str);
           // CodePanel.setVisible(true);
        } // if whichreport
        
        if (whichreport.equals("Clock -- by code")) {
            ClockChartByCode();
            tacodes.setText("");
            ArrayList codes = OVData.getClockCodesAndDesc();
            String str = "";
            for (int i = 0; i < codes.size(); i++) {
            String[] element = codes.get(i).toString().split(",");
                str += (element[0] + " = " + element[1]) + "\n";
            }
            tacodes.setText(str);
          //  CodePanel.setVisible(true);
        } // if whichreport
        
        if (whichreport.equals("Clock -- by employee")) {
            ClockChartByEmp();
            tacodes.setText("");
            ArrayList codes = OVData.getEmployeeIDAndName();
            String str = "";
            for (int i = 0; i < codes.size(); i++) {
            String[] element = codes.get(i).toString().split(",");
                str += (element[0] + " = " + element[1]) + "\n";
            }
            tacodes.setText(str);
         //   CodePanel.setVisible(true);
        } // if whichreport
                
        if (whichreport.equals("Clock -- by week")) {
            HoursPerWeek();
            tacodes.setText(weeknumbers);
          //  CodePanel.setVisible(true);
        } // if whichreport
   
        
        if (whichreport.equals("Scrap -- per week")) {
            ScrapPerWeek();
            tacodes.setText(weeknumbers);
           // CodePanel.setVisible(true);
        } // if whichreport
                
        if (whichreport.equals("Scrap -- quantity by item")) {
            PartAccumQty();
        }
        
        if (whichreport.equals("Scrap -- dollars by item")) {
            PartAccumDollar();
        }
        
        if (whichreport.equals("Generic -- xy plot <date,number>")) {
            GenericDataXY(whichreport);
        } 
        if (whichreport.equals("Generic -- xy plot <number,number>")) {
            GenericDataXY(whichreport);
        }
        
        
    }//GEN-LAST:event_btChartActionPerformed

    private void btprintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btprintActionPerformed
   /*      
    PrintService service = PrintServiceLookup.lookupDefaultPrintService();
    DocPrintJob job = service.createPrintJob();
    DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
    SimpleDoc doc = new SimpleDoc(new MyPrintable(), flavor, null);
    try {
         job.print(doc, null);
     } catch (PrintException ex) {
         Logger.getLogger(ScrapChartView.class.getName()).log(Level.SEVERE, null, ex);
     }
     */
        /*
        BufferedImage image = null;
        try {
        image = ImageIO.read(new File(bsmf.MainFrame.temp + "/" + "chart.jpg"));
        } catch (IOException e) {
        }
        */
        if (myimage != null) {
            PrinterJob printJob = PrinterJob.getPrinterJob();
            printJob.setPrintable(new ImagePrintable(printJob, myimage));
            PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
            aset.add(OrientationRequested.LANDSCAPE);
            if (printJob.printDialog()) {
                try {
                    printJob.print(aset);
                } catch (PrinterException prt) {
                    prt.printStackTrace();
                }
            }
        }
    }//GEN-LAST:event_btprintActionPerformed

    private void cbcodesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbcodesActionPerformed
        if (chartlabel.getIcon() != null && lhm.get(ddreport.getSelectedItem().toString()) != null && lhm.get(ddreport.getSelectedItem().toString()).equals("1")) {
            if (cbcodes.isSelected()) {
                CodePanel.setVisible(true);
                Image newimg = myimage.getScaledInstance(jPanel2.getWidth() / 2, this.getHeight() - 150,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
                ImageIcon imageIcon = new ImageIcon(newimg);
                imageIcon.getImage().flush();
                chartlabel.setIcon(imageIcon); 
                this.repaint();
            } else {  
                CodePanel.setVisible(false);
                Image newimg = myimage.getScaledInstance(jPanel2.getWidth(), this.getHeight() - 150,  java.awt.Image.SCALE_REPLICATE); // scale it the smooth way  
                ImageIcon imageIcon = new ImageIcon(newimg);
                imageIcon.getImage().flush();
                chartlabel.setIcon(imageIcon);
                this.repaint();
            }
        }
    }//GEN-LAST:event_cbcodesActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel ChartPanel;
    private javax.swing.JPanel CodePanel;
    private javax.swing.JButton btChart;
    private javax.swing.JButton btprint;
    private javax.swing.JCheckBox cbcodes;
    private javax.swing.JLabel chartlabel;
    private com.toedter.calendar.JDateChooser dcFrom;
    private com.toedter.calendar.JDateChooser dcTo;
    private javax.swing.JComboBox<String> ddlimit;
    private javax.swing.JComboBox<String> ddreport;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextArea tacodes;
    // End of variables declaration//GEN-END:variables
}
