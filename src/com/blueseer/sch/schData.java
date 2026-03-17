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
package com.blueseer.sch;

import bsmf.MainFrame;
import static bsmf.MainFrame.bslog;
import static bsmf.MainFrame.db;
import static bsmf.MainFrame.defaultDecimalSeparator;
import static bsmf.MainFrame.ds;
import static bsmf.MainFrame.pass;
import static bsmf.MainFrame.url;
import static bsmf.MainFrame.user;
import static com.blueseer.hrm.hrmData.getEmpFormalNameByID;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.bsNumber;
import static com.blueseer.utl.BlueSeerUtils.currformat;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import static com.blueseer.utl.BlueSeerUtils.getDateDB;
import static com.blueseer.utl.BlueSeerUtils.getGlobalProgTag;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import static com.blueseer.utl.BlueSeerUtils.jsonToArrayListStringArray;
import static com.blueseer.utl.BlueSeerUtils.jsonToBoolean;
import static com.blueseer.utl.BlueSeerUtils.jsonToDouble;
import static com.blueseer.utl.BlueSeerUtils.jsonToStringArray;
import static com.blueseer.utl.BlueSeerUtils.sendServerPost;
import static com.blueseer.utl.BlueSeerUtils.xNull;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import org.json.JSONArray;

/**
 *
 * @author terryva
 */
public class schData {
    
    public static plan_mstr getPlanMstr(String[] x) {
        plan_mstr r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id","getPlanMstr"});
            list.add(new String[]{"param1",x[0]});
            if (x.length > 1) {
             list.add(new String[]{"param2",x[1]});
            } else {
             list.add(new String[]{"param2",""});   
            }
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServSCH");
                r = objectMapper.readValue(returnstring, plan_mstr.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
                r = new plan_mstr(m);
                return r;
            }
        }
        
        String sql = "select * from plan_mstr where plan_nbr = ? ;";
        if (x.length > 1 && ! x[1].isBlank()) {
            sql = "select * from plan_mstr where plan_nbr = ? and plan_type = ? ;";
        }
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());   
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, x[0]);
        if (x.length > 1 && ! x[1].isBlank()) {
            ps.setString(2, x[1]);
        }
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new plan_mstr(m);  // minimum return
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new plan_mstr(m, res.getInt("plan_nbr"), res.getString("plan_item"), res.getString("plan_site"),
                    res.getDouble("plan_qty_req"), res.getDouble("plan_qty_comp"), res.getDouble("plan_qty_sched"), 
                    res.getString("plan_date_create"), res.getString("plan_date_due"), res.getString("plan_date_sched"),
                    res.getInt("plan_status"), res.getString("plan_rmks"), res.getString("plan_order"), res.getString("plan_line"), 
                    res.getString("plan_type"), res.getString("plan_cell"), res.getInt("plan_is_sched")
        );
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getClassName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new plan_mstr(m);
        }
        return r;
    }
    
    public static String[] addPlanMstr(plan_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addPlanMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServSCH"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  plan_mstr where plan_nbr = ?";
        String sqlInsert = "insert into plan_mstr (plan_nbr, plan_item, plan_site, plan_qty_req, plan_qty_comp, "
                        + " plan_qty_sched, plan_date_create, plan_date_due, plan_date_sched, plan_status, " 
                        + " plan_rmks, plan_order, plan_line, plan_type, plan_cell, plan_is_sched ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); ";
       
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setInt(1, x.plan_nbr);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setInt(1, x.plan_nbr);
            psi.setString(2, x.plan_item);
            psi.setString(3, x.plan_site);
            psi.setDouble(4, x.plan_qty_req);
            psi.setDouble(5, x.plan_qty_comp);
            psi.setDouble(6, x.plan_qty_sched);
            psi.setString(7, x.plan_date_create);
            psi.setString(8, x.plan_date_due);
            psi.setString(9, x.plan_date_sched);
            psi.setInt(10, x.plan_status);
            psi.setString(11, x.plan_rmks);
            psi.setString(12, x.plan_order);
            psi.setString(13, x.plan_line);
            psi.setString(14, x.plan_type);
            psi.setString(15, x.plan_cell);
            psi.setInt(16, x.plan_is_sched);
            int rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordAlreadyExists};    
            }
          } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
          }
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] updatePlanMstr(plan_mstr x) {
       /* status must be defined as below...within x prior to call
       if (status.equals(getGlobalProgTag("open"))) { status = "0"; }
       if (status.equals(getGlobalProgTag("closed"))) { status = "1"; }
       if (status.equals(getGlobalProgTag("void"))) { status = "-1"; }
        */
       
        String[] m = new String[2];
        String sql = "update plan_mstr set plan_rmks = ?, plan_status = ? "
                        + " where plan_nbr = ? ;"; 
        
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(2, x.plan_rmks());
            ps.setInt(1, x.plan_status());
            int rows = ps.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.updateRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }

    
    public static String[] deletePlanMstr(plan_mstr x) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","deletePlanMstr"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServSCH"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m;
        String sqlDelete = "delete from plan_mstr where plan_nbr = ? ;"; 
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
             PreparedStatement ps = con.prepareStatement(sqlDelete);) {
             ps.setInt(1, x.plan_nbr());
             int rows = ps.executeUpdate();
             if (rows > 0) {
                m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.deleteRecordSuccess}; 
             } else {
                m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.deleteRecordError}; 
             }
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }
    
    public static String[] addPlanOperationTrans(ArrayList<plan_operation> plo) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addPlanOperationTrans"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(plo);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServSCH"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        Connection bscon = null;
        PreparedStatement ps = null;
        ResultSet res = null;
        try { 
            if (ds != null) {
              bscon = ds.getConnection();
            } else {
              bscon = DriverManager.getConnection(url + db, user, pass);  
            }
            bscon.setAutoCommit(false);
            for (plan_operation z : plo) {
                _addPlanOperation(z, bscon, ps, res);
            }
            bscon.commit();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
        } catch (SQLException s) {
             MainFrame.bslog(s);
             try {
                 bscon.rollback();
                 m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordError};
             } catch (SQLException rb) {
                 MainFrame.bslog(rb);
             }
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
            if (bscon != null) {
                try {
                    bscon.setAutoCommit(true);
                    bscon.close();
                } catch (SQLException ex) {
                    MainFrame.bslog(ex);
                }
            }
        }
    return m;
    }
    
    public static String[] addPlanOperation(plan_operation x ) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","addPlanOperation"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServSCH"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlSelect = "SELECT * FROM  plan_operation where plo_parent = ? and plo_op = ?";
        String sqlInsert = "insert into plan_operation (plo_parent, plo_op, plo_qty, plo_qty_comp, plo_cell, "
                        + " plo_operator, plo_operatorname, plo_date, plo_status, plo_userid, plo_desc, plo_notes ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?); ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlSelect);) {
             ps.setInt(1, x.plo_parent);
             ps.setInt(2, x.plo_op);
          try (ResultSet res = ps.executeQuery();
               PreparedStatement psi = con.prepareStatement(sqlInsert);) {  
            if (! res.isBeforeFirst()) {
            psi.setInt(1, x.plo_parent);
            psi.setInt(2, x.plo_op);
            psi.setDouble(3, x.plo_qty);
            psi.setDouble(4, x.plo_qty_comp);
            psi.setString(5, x.plo_cell);
            psi.setString(6, x.plo_operator);
            psi.setString(7, x.plo_operatorname);
            psi.setString(8, x.plo_date);
            psi.setString(9, x.plo_status);
            psi.setString(10, x.plo_userid);
            psi.setString(11, x.plo_desc);
            psi.setString(12, x.plo_notes);
            int rows = psi.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
            } else {
            m = new String[] {BlueSeerUtils.ErrorBit, BlueSeerUtils.addRecordAlreadyExists};    
            }
          } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
          }
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }

    public static String[] updatePlanOperation(plan_operation x ) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id","updatePlanOperation"});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String jsonString = objectMapper.writeValueAsString(x);
                return jsonToStringArray(sendServerPost(list, jsonString, null, "dataServSCH"));
            } catch (IOException ex) {
                bslog(ex);
                return new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())};
            }
        }
        String[] m = new String[2];
        String sqlUpdate = "update plan_operation set plo_qty = ?, plo_qty_comp = ?, plo_cell = ?, "
                        + " plo_operator = ?, plo_operatorname = ?, plo_date = ?, plo_status = ?  "
                        + " where plo_parent = ? and plo_op = ? ; ";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
             PreparedStatement ps = con.prepareStatement(sqlUpdate);) {
            ps.setDouble(1, x.plo_qty);
            ps.setDouble(2, x.plo_qty_comp);
            ps.setString(3, x.plo_cell);
            ps.setString(4, x.plo_operator);
            ps.setString(5, x.plo_operatorname);
            ps.setString(6, x.plo_date);
            ps.setString(7, x.plo_status);
            ps.setInt(8, x.plo_parent);
            ps.setInt(9, x.plo_op);
            int rows = ps.executeUpdate();
            m = new String[] {BlueSeerUtils.SuccessBit, BlueSeerUtils.addRecordSuccess};
        } catch (SQLException s) {
	       MainFrame.bslog(s);
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
        }
        return m;
    }

    
    public static int _addPlanOperation(plan_operation x, Connection con, PreparedStatement ps, ResultSet res ) throws SQLException {
        int rows = 0;
        String sqlSelect = "SELECT * FROM  plan_operation where plo_parent = ? and plo_op = ?";
        String sqlInsert = "insert into plan_operation (plo_parent, plo_op, plo_qty, plo_qty_comp, plo_cell, "
                        + " plo_operator, plo_operatorname, plo_date, plo_status, plo_userid, plo_desc, plo_notes ) "
                        + " values (?,?,?,?,?,?,?,?,?,?,?,?); ";
       
        ps = con.prepareStatement(sqlSelect);
             ps.setInt(1, x.plo_parent);
             ps.setInt(2, x.plo_op);
             res = ps.executeQuery();
             ps = con.prepareStatement(sqlInsert); 
            if (! res.isBeforeFirst()) {
            ps.setInt(1, x.plo_parent);
            ps.setInt(2, x.plo_op);
            ps.setDouble(3, x.plo_qty);
            ps.setDouble(4, x.plo_qty_comp);
            ps.setString(5, x.plo_cell);
            ps.setString(6, x.plo_operator);
            ps.setString(7, x.plo_operatorname);
            ps.setString(8, x.plo_date);
            ps.setString(9, x.plo_status);
            ps.setString(10, x.plo_userid);
            ps.setString(11, x.plo_desc);
            ps.setString(12, x.plo_notes);
            rows = ps.executeUpdate();
            }
        return rows;
    }

    
    public static ArrayList<plan_operation> getPlanOperation(String parent) {
        plan_operation r = null;
        String[] m = new String[2];
        ArrayList<plan_operation> list = new ArrayList<plan_operation>();
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> paramlist = new ArrayList<>();
            paramlist.add(new String[]{"id","getPlanOperationList"});
            paramlist.add(new String[]{"param1",parent});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(paramlist, "", null, "dataServSCH");
                list = objectMapper.readValue(returnstring, new TypeReference<ArrayList<plan_operation>>() {});
                return list;
            } catch (IOException ex) {
                bslog(ex);
                return list;
            }
        }
        
        String sql = "select * from plan_operation where plo_parent = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setString(1, parent);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new plan_operation(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new plan_operation(m, res.getInt("plo_id"), res.getInt("plo_parent"), res.getInt("plo_op"),
                            res.getDouble("plo_qty"), res.getDouble("plo_qty_comp"), res.getString("plo_cell"), res.getString("plo_operator"), res.getString("plo_operatorname"),
                            res.getString("plo_date"), res.getString("plo_status"), res.getString("plo_userid") , res.getString("plo_desc"), res.getString("plo_notes"));
                        list.add(r);
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new plan_operation(m);
               list.add(r);
        }
        return list;
    }
    
    public static plan_operation getPlanOperation(int parent, int op) {
        plan_operation r = null;
        String[] m = new String[2];
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getPlanOperation"});
            list.add(new String[]{"param1",  bsNumber(parent)});
            list.add(new String[]{"param2",  bsNumber(op)});
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                String returnstring = sendServerPost(list, "", null, "dataServSCH");
                r = objectMapper.readValue(returnstring, plan_operation.class); 
                return r;
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        String sql = "select * from plan_operation where plo_parent = ? and plo_op = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection()); 
	PreparedStatement ps = con.prepareStatement(sql);) {
        ps.setInt(1, parent);
        ps.setInt(2, op);
             try (ResultSet res = ps.executeQuery();) {
                if (! res.isBeforeFirst()) {
                m = new String[]{BlueSeerUtils.ErrorBit, BlueSeerUtils.noRecordFound};
                r = new plan_operation(m);
                } else {
                    while(res.next()) {
                        m = new String[]{BlueSeerUtils.SuccessBit, BlueSeerUtils.getRecordSuccess};
                        r = new plan_operation(m, res.getInt("plo_id"), res.getInt("plo_parent"), res.getInt("plo_op"),
                            res.getDouble("plo_qty"), res.getDouble("plo_qty_comp"), res.getString("plo_cell"), res.getString("plo_operator"), res.getString("plo_operatorname"),
                            res.getString("plo_date"), res.getString("plo_status"), res.getString("plo_userid"), res.getString("plo_desc"), res.getString("plo_notes"));
                    }
                }
            }
        } catch (SQLException s) {   
	       MainFrame.bslog(s);  
               m = new String[]{BlueSeerUtils.ErrorBit, getMessageTag(1016, Thread.currentThread().getStackTrace()[1].getMethodName())}; 
               r = new plan_operation(m);
        }
        return r;
    }
    
    
    // misc functions 
    public static String getSchRptPickerData(String[] keys) {
        JSONArray jsonarray = new JSONArray();
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
                if (keys[0].equals("planOrdersByItem")) {
                res = st.executeQuery("SELECT it_item, it_desc, it_code, it_site, plan_nbr, plan_type, plan_order, case plan_is_sched when '1' then 'yes' else 'no' end plan_is_sched , plan_cell, plan_qty_sched, plan_date_sched, case plan_status when '1' then 'complete' when '0' then 'open' else 'void' end plan_status  " +
                        " FROM  item_mstr left outer join plan_mstr on plan_item = it_item  " +
                        " where it_item  >= " + "'" + keys[1] + "'" +
                        " and it_item  <= " + "'" + keys[2] + "'" +
                        " order by plan_nbr ;");                
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put(res.getString("it_item"));
                            rowArray.put(res.getString("it_desc"));
                            rowArray.put(res.getString("it_code"));
                            rowArray.put(xNull(res.getString("plan_nbr")));
                            rowArray.put(xNull(res.getString("plan_type")));
                            rowArray.put(xNull(res.getString("plan_order")));
                            rowArray.put(xNull(res.getString("plan_is_sched")));
                            rowArray.put(xNull(res.getString("plan_cell")));
                            rowArray.put(xNull(res.getString("plan_qty_sched")));
                            rowArray.put(xNull(res.getString("plan_date_sched")));
                            rowArray.put(xNull(res.getString("plan_status")));
                            jsonarray.put(rowArray);

                    } 
                }
                
                if (keys[0].equals("planOrdersBySalesOrder")) {
                res = st.executeQuery("SELECT sod_item, sod_nbr, sod_line, it_desc, it_code, it_site, plan_nbr, plan_type, plan_order, case plan_is_sched when '1' then 'yes' else 'no' end plan_is_sched , plan_cell, plan_qty_sched, plan_date_sched, case plan_status when '1' then 'complete' when '0' then 'open' else 'void' end plan_status  " +
                        " FROM  sod_det inner join item_mstr on sod_item = it_item left outer join plan_mstr on plan_order = sod_nbr and plan_line = sod_line  " +
                        " where cast(sod_nbr as decimal) >= " + "'" + keys[1] + "'" +
                        " and cast(sod_nbr as decimal) <= " + "'" + keys[2] + "'" +
                        " order by sod_line ;");                
                    while (res.next()) {
                            i++;
                            JSONArray rowArray = new JSONArray(); 
                            rowArray.put(res.getString("sod_nbr"));
                            rowArray.put(res.getString("sod_line"));
                            rowArray.put(res.getString("sod_item"));
                            rowArray.put(res.getString("it_desc"));
                            rowArray.put(res.getString("it_code"));
                            rowArray.put(res.getString("plan_nbr"));
                            rowArray.put(res.getString("plan_is_sched"));
                            rowArray.put(res.getString("plan_cell"));
                            rowArray.put(res.getString("plan_qty_sched"));
                            rowArray.put(res.getString("plan_date_sched"));
                            rowArray.put(res.getString("plan_status"));
                            
                            jsonarray.put(rowArray);

                    } 
                }
                
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
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
        return jsonarray.toString(); 
    }
    
   
    public static String getSchedulerBrowseView(String[] keys) {
        JSONArray jsonarray = new JSONArray();
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
                double dol = 0;
                double qty = 0;
                 
                
                 // init for new cust
                i = 0;
                String status = "";
                
                                
                keys[2] = (keys[2].isBlank()) ? bsmf.MainFrame.lowchar : keys[2];
                keys[3] = (keys[3].isBlank()) ? bsmf.MainFrame.hichar : keys[3];
                keys[4] = (keys[4].isBlank()) ? bsmf.MainFrame.lowchar : keys[4];
                keys[5] = (keys[5].isBlank()) ? bsmf.MainFrame.hichar : keys[5];
                
                if (keys[6].equals("1")) {
                    res = st.executeQuery("SELECT plan_nbr, plan_type, plan_item, plan_qty_req, plan_qty_comp, "
                      //  + "( select coalesce(sum(pland_qty),0) as qtycomp from pland_mstr where pland_parent = plan_nbr) as qtycomp,"
                        + " plan_qty_sched, plan_date_due, plan_date_sched, plan_status, ifnull(plan_is_sched,0) plan_is_sched, plan_cell, plan_order, plan_line " +
                        " FROM  plan_mstr " +
                        " where plan_date_due >= " + "'" + keys[0] + "'" +
                        " AND plan_date_due <= " + "'" + keys[1] + "'" +
                        " AND plan_item >= " + "'" + keys[2] + "'" +
                        " AND plan_item <= " + "'" + keys[3] + "'" +
                        " AND plan_cell >= " + "'" + keys[4] + "'" +
                        " AND plan_cell <= " + "'" + keys[5] + "'" +
                        " AND plan_is_sched = " + "'0' "  +
                        " order by plan_item, plan_date_due;");
                } else {
                    res = st.executeQuery("SELECT plan_nbr, plan_item, plan_type, plan_qty_req, plan_qty_comp, "
                      //  + "( select coalesce(sum(pland_qty),0) as qtycomp from pland_mstr where pland_parent = plan_nbr) as qtycomp,"     
                        + " plan_qty_sched, plan_date_due, plan_date_sched, plan_status, ifnull(plan_is_sched,0) plan_is_sched, plan_cell, plan_order, plan_line " +
                        " FROM  plan_mstr " +
                        " where plan_date_due >= " + "'" + keys[0] + "'" +
                        " AND plan_date_due <= " + "'" + keys[1] + "'" +
                        " AND plan_item >= " + "'" + keys[2] + "'" +
                        " AND plan_item <= " + "'" + keys[3] + "'" +
                        " AND plan_cell >= " + "'" + keys[4] + "'" +
                        " AND plan_cell <= " + "'" + keys[5] + "'" +
                        " order by plan_item, plan_date_due ;");
                }
                
                while (res.next()) {
                        i++;
                        if (res.getString("plan_status").equals("0")) { status = getGlobalProgTag("open"); }
                        if (res.getString("plan_status").equals("1")) { status = getGlobalProgTag("closed"); }
                        if (res.getString("plan_status").equals("-1")) { status = getGlobalProgTag("void"); }
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put("select");
                        rowArray.put(res.getString("plan_nbr"));
                        rowArray.put(res.getString("plan_item"));
                        rowArray.put(res.getString("plan_date_due"));
                        rowArray.put(res.getString("plan_type"));
                        rowArray.put(res.getString("plan_is_sched"));
                        rowArray.put(res.getString("plan_cell"));
                        rowArray.put(bsNumber(res.getDouble("plan_qty_sched")));
                        rowArray.put(bsNumber(res.getDouble("plan_qty_req")));
                        rowArray.put(bsNumber(res.getDouble("plan_qty_comp")));
                        rowArray.put(res.getString("plan_date_sched"));
                        rowArray.put(res.getString("plan_order"));
                        rowArray.put(res.getString("plan_line"));
                        rowArray.put(status);
                        jsonarray.put(rowArray);
                        
                } 
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
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
        return jsonarray.toString(); 
    }
    
    public static String getSchedulerDetView(String[] keys) {
        JSONArray jsonarray = new JSONArray();
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
                
                 
                
                 // init for new cust
                i = 0;
                
                if (keys[1].equals("ALL")) {
                res = st.executeQuery("SELECT plan_nbr, plan_type, plan_item, plan_qty_req, plan_qty_comp, "
                        + " plan_qty_sched, plan_date_due, plan_date_sched, plan_status, ifnull(plan_is_sched,0) plan_is_sched, plan_cell, plan_order, plan_line " +
                        " FROM  plan_mstr " +
                        " where plan_date_sched = " + "'" + keys[0] + "'" +
                        " AND plan_is_sched = " + "'" + "1" + "'"  +
                        " AND plan_status <> " + "'" + "-1" + "'"  + // void
                        " order by plan_item, plan_cell;");
                } else {
                res = st.executeQuery("SELECT plan_nbr, plan_type, plan_item, plan_qty_req, plan_qty_comp, "
                        + " plan_qty_sched, plan_date_due, plan_date_sched, plan_status, ifnull(plan_is_sched,0) plan_is_sched, plan_cell, plan_order, plan_line " +
                        " FROM  plan_mstr " +
                        " where plan_date_sched = " + "'" + keys[0] + "'" +
                        " AND plan_cell = " + "'" + keys[1] + "'" +
                        " AND plan_is_sched = " + "'" + "1" + "'"  +
                        " AND plan_status <> " + "'" + "-1" + "'"  + // void
                        " order by plan_item, plan_cell;");    
                }
               
                while (res.next()) {
                        i++;
                       
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("plan_nbr"));
                        rowArray.put(res.getString("plan_item"));
                        rowArray.put(res.getString("plan_type"));
                        rowArray.put(res.getString("plan_cell"));
                        rowArray.put(bsNumber(res.getDouble("plan_qty_sched")));
                        rowArray.put(res.getString("plan_status"));
                        jsonarray.put(rowArray);
                        
                } 
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
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
        return jsonarray.toString(); 
    }
    
    public static String getSchedulerOpView(String[] keys) {
        JSONArray jsonarray = new JSONArray();
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
                
                 
                
                 // init for new cust
                i = 0;
                
                res = st.executeQuery("select plo_op, plo_cell, plo_qty, plo_operator, plo_desc, emp_lname, emp_fname " +
                         " from plan_operation " +
                         " inner join plan_mstr on plan_nbr = plo_parent " +
                         " inner join item_mstr on it_item = plan_item " +
                         " inner join wf_mstr on wf_id = it_wf and plo_op = wf_op " +
                         " left outer join emp_mstr on emp_nbr = plo_operator " +
                        " where plo_parent = " + "'" + keys[0] + "'" + " order by plo_op ;");   
               
                while (res.next()) {
                        i++;
                        JSONArray rowArray = new JSONArray(); 
                        rowArray.put(res.getString("plo_op"));
                        rowArray.put(res.getString("plo_desc"));
                        rowArray.put(res.getString("plo_cell"));
                        rowArray.put(res.getString("plo_qty"));
                        rowArray.put(res.getString("emp_lname") + ", " + res.getString("emp_fname"));
                        jsonarray.put(rowArray);
                        
                } 
                
            } catch (SQLException s) {
                MainFrame.bslog(s);
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
        return jsonarray.toString(); 
    }
    
    
    public static ArrayList<String[]> getSummaryByDate(String fromdate, String todate, String cell) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getSummaryByDate"});
            list.add(new String[]{"param1", fromdate});
            list.add(new String[]{"param2", todate});
            list.add(new String[]{"param3", cell});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServSCH"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
        ArrayList<String[]> arr = new ArrayList<String[]>();
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
                
                if (cell.equals("ALL")) {
                res = st.executeQuery("SELECT sum(plan_qty_sched) as 'sum', plan_date_sched, plan_cell " +
                        " FROM  plan_mstr " +
                        " where plan_date_sched >= " + "'" + fromdate + "'" +
                        " AND plan_date_sched <= " + "'" + todate + "'"  +
                        " AND plan_is_sched = " + "'" + "1" + "'"  +
                        " AND plan_status <> " + "'" + "-1" + "'"  + // void
                        " group by plan_date_sched, plan_cell order by plan_date_sched;");
                } else {
                   res = st.executeQuery("SELECT sum(plan_qty_sched) as 'sum', plan_date_sched, plan_cell " +
                        " FROM  plan_mstr " +
                        " where plan_date_sched >= " + "'" + fromdate + "'" +
                        " AND plan_date_sched <= " + "'" + todate + "'"  +
                        " AND plan_is_sched = " + "'" + "1" + "'"  +
                        " AND plan_status <> " + "'" + "-1" + "'"  + // void
                        " AND plan_cell = " + "'" + cell + "'" +
                        " group by plan_date_sched, plan_cell order by plan_date_sched;");  
                }
                while (res.next()) {
                    String[] s = new String[]{res.getString("plan_date_sched"), res.getString("plan_cell"),res.getString("sum") };
                    arr.add(s);
                }

            } catch (SQLException s) {
                bslog(s);
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

        return arr;
    }    
    
    
    public static String getPlanItem(String serialno) {

          // From perspective of "has it been scanned...or is there a 1 in lbl_scan which is set when label is scanned
          // assume it's false i.e. hasn't been scanned.
        String x = "";
        String sql = "select plan_item from plan_mstr where plan_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
            PreparedStatement ps = con.prepareStatement(sql);) {
            ps.setString(1, serialno);  
            try (ResultSet res = ps.executeQuery();) {
               while (res.next()) {
                   x = res.getString("plan_item");
               }
            }
        } catch (SQLException e) {
            MainFrame.bslog(e);
        } 
      return x;
  }
    
    public static String getPlanSrvOrderNumber(String serialno) {

          // From perspective of "has it been scanned...or is there a 1 in lbl_scan which is set when label is scanned
          // assume it's false i.e. hasn't been scanned.
        String x = "";
        String sql = "select plan_order from plan_mstr where plan_nbr = ? ;";
        try (Connection con = (ds == null ? DriverManager.getConnection(url + db, user, pass) : ds.getConnection());
            PreparedStatement ps = con.prepareStatement(sql);) {
            ps.setString(1, serialno);  
            try (ResultSet res = ps.executeQuery();) {
               while (res.next()) {
                   x = res.getString("plan_order");
               }
            }
        } catch (SQLException e) {
            MainFrame.bslog(e);
        } 
      return x;
  }
    
    
    public static double getPlanSchedQty(String serialno) {

      // From perspective of "has it been scanned...or is there a 1 in lbl_scan which is set when label is scanned
      // assume it's false i.e. hasn't been scanned.
      double myreturn = 0;

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
             res = st.executeQuery("select plan_qty_sched from plan_mstr where plan_nbr = " + "'" + serialno + "'" 
                     + " ;");
           while (res.next()) {
               myreturn = res.getDouble("plan_qty_sched");
           }

        } catch (SQLException s) {
            MainFrame.bslog(s);
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
      return myreturn;
  }

    public static double getPlanDetTotQtyByOp(String serialno, String op) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<>();
            list.add(new String[]{"id", "getPlanDetTotQtyByOp"});
            list.add(new String[]{"param1",  serialno});
            list.add(new String[]{"param2",  op});
            try {
                return jsonToDouble(sendServerPost(list, "", null, "dataServSCH")); 
            } catch (IOException ex) {
                bslog(ex);
                return 0.00;
            }
        }
      // From perspective of "has it been scanned...or is there a 1 in lbl_scan which is set when label is scanned
      // assume it's false i.e. hasn't been scanned.
      double x = 0;

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
             res = st.executeQuery("select sum(pland_qty) as 'mysum' from pland_mstr where pland_parent = " + "'" + serialno + "'" 
                     + " AND pland_op = " + "'" + op + "'"
                     + " ;");
           while (res.next()) {
               x = res.getDouble("mysum");
           }

        } catch (SQLException s) {
            MainFrame.bslog(s);
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
      return x;
  }
    
    public static ArrayList<String[]> getPlanDetHistory(String serialno) {

      // From perspective of "has it been scanned...or is there a 1 in lbl_scan which is set when label is scanned
      // assume it's false i.e. hasn't been scanned.
      if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "getPlanDetHistory"});
            list.add(new String[]{"param1",  serialno});
            try {
                return jsonToArrayListStringArray(sendServerPost(list, "", null, "dataServSCH"));
            } catch (IOException ex) {
                bslog(ex);
                return null;
            }
        }
      ArrayList<String[]> x = new ArrayList<String[]>();

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
             res = st.executeQuery("select * from pland_mstr where pland_parent = " + "'" + serialno + "'"
                     + " order by abs(pland_op) ;");
           while (res.next()) {
               String[] w = new String[]{
                   res.getString("pland_id"),
                    res.getString("pland_parent"),
                    res.getString("pland_item"),
                    res.getString("pland_op"),
                    res.getString("pland_cell"),
                    res.getString("pland_date"),
                    res.getString("pland_ref"),
                    res.getString("pland_qty"),
                    res.getString("pland_userid")
               };
               x.add(w);
           }

        } catch (SQLException s) {
            MainFrame.bslog(s);
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
      return x;
  }
    
    
    public static int getPlanStatus(String serialno) {
          
          // -1 plan_status is void
          // 0 plan_status is open
          // 1 plan_status is closed
          
          int myreturn = 0;
          
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
                 res = st.executeQuery("select plan_status from plan_mstr where plan_nbr = " + "'" + serialno + "'" 
                         + " ;");
               while (res.next()) {
                   myreturn = res.getInt("plan_status");
               }
              
            } catch (SQLException s) {
                MainFrame.bslog(s);
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
          return myreturn;
      }
    
    public static String getPlanStatusMnemonic(int status) {
          
          // -1 plan_status is void
          // 0 plan_status is open
          // 1 plan_status is closed/complete
          
          String x = "unknown";
          if (status == 0) {
              x = getGlobalProgTag("open");
          }
          if (status == -1) {
              x = getGlobalProgTag("void");
          }
          if (status == 1) {
              x = getGlobalProgTag("complete");
          }
          return x;
      }
     
    public static String getPlanIsSchedMnemonic(int issched) {
          
          // 
          // 0 plan_is_sched is no
          // 1 plan_is_sched is yes
          
          String x = "unknown";
          if (issched == 0) {
              x = "no";
          }
          if (issched == 1) {
              x = "yes";
          }
          return x;
      }
          
    public static void updatePlanStatus(String serialno, String value) {
        if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "updatePlanStatus"});
            list.add(new String[]{"param1", serialno});
            list.add(new String[]{"param2", value});
            try {
                sendServerPost(list, "", null, "dataServSCH");
                return; 
            } catch (IOException ex) {
                bslog(ex);
                return;
            }
        }   
        
        try {
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                 st.executeUpdate("update plan_mstr set plan_status = " + "'" + value + "'" + " where plan_nbr = " + "'" + serialno + "'" 
                         + " ;");
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
      }
      
    public static void updatePlanQty(String serialno, double qty) {
          try {
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                 st.executeUpdate("update plan_mstr set plan_qty_comp = " + "'" + qty + "'" + " where plan_nbr = " + "'" + serialno + "'" 
                         + " ;");
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
      }
      
    public static void updatePlanQtyByOp(String serialno, int qty, String op, String ref, String cell) {
          try {
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            try {
                 st.executeUpdate("update plan_mstr set plan_qty_comp = " + "'" + qty + "'" + " where plan_nbr = " + "'" + serialno + "'" 
                         + " ;");
            } catch (SQLException s) {
                MainFrame.bslog(s);
            } finally {
                if (st != null) {
                    st.close();
                }
                con.close();
            }
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
      }
          
    public static boolean isPlan(String serialno) {
          
          // From perspective of "does it exist"
          // assume it's false i.e. it doesnt exist.
          boolean myreturn = false;
          int i = 0;
          
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
                 res = st.executeQuery("select plan_nbr from plan_mstr where plan_nbr = " + "'" + serialno + "'" 
                         + " ;");
               while (res.next()) {
                 i++; 
               }
               if (i > 0) {
                   myreturn = true;
               }
              
            } catch (SQLException s) {
                MainFrame.bslog(s);
            }
            con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
          return myreturn;
      }
    
    public static boolean hasOperations(String serialno) {
          
          // From perspective of "does it exist"
          // assume it's false i.e. it doesnt exist.
          if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "hasOperations"});
            list.add(new String[]{"param1", serialno});
            try {
                return jsonToBoolean(sendServerPost(list, "", null, "dataServSCH"));
            } catch (IOException ex) {
                bslog(ex);
                return false;
            }
        } 
          boolean myreturn = false;
          int i = 0;
          
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
                 res = st.executeQuery("select * from plan_operation where plo_parent = " + "'" + serialno + "'" 
                         + " ;");
               while (res.next()) {
                 i++; 
               }
               if (i > 0) {
                   myreturn = true;
               }
              
            } catch (SQLException s) {
                MainFrame.bslog(s);
            }
            con.close();
        } catch (Exception e) {
            MainFrame.bslog(e);
        }
          return myreturn;
      }
    
    
    public static String orderPlanStatus(String order) {
      String x = "unknown";
      int summation = 0;
      int scheduled = 0;
      int linecount = 0;
      int nullcount = 0;
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
             boolean proceed = true;
             res = st.executeQuery("select sod_nbr, sod_line, plan_order, plan_line, plan_status, plan_is_sched " +
                     " from sod_det " +
                     " left outer join plan_mstr on plan_order = sod_nbr and plan_line = sod_line " +
                     " where sod_nbr = " + "'" + order + "'" 
                     + " ;");
           while (res.next()) {
               linecount++;
               if (res.getString("plan_status") == null) {
                   nullcount++;
               } else {
                   summation += res.getInt("plan_status");
                   scheduled += res.getInt("plan_is_sched");
               }


           }

           if (summation == linecount && scheduled == linecount && nullcount == 0) {
               x = "complete";
           }
           if (summation == 0 && nullcount == 0 && scheduled == linecount) {
               x = "planned";
           }
           if (summation == 0 && nullcount >= 0 && scheduled == 0) {
               x = "unplanned";
           }



        } catch (SQLException s) {
            MainFrame.bslog(s);
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
      return x;
  }

    public static boolean updatePlanOrder(String order, String schedqty, String cell, String scheddate, String status) {
      if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "updatePlanOrder"});
            list.add(new String[]{"param1", order});
            list.add(new String[]{"param2", schedqty});
            list.add(new String[]{"param3", cell});
            list.add(new String[]{"param4", scheddate});
            list.add(new String[]{"param5", status});
            try {
                return jsonToBoolean(sendServerPost(list, "", null, "dataServSCH"));
            } catch (IOException ex) {
                bslog(ex);
                return false;
            }
        } 
      boolean myreturn = false;  
      try {

           if (status.equals(getGlobalProgTag("open"))) { status = "0"; }
           if (status.equals(getGlobalProgTag("close"))) { status = "1"; }
           if (status.equals(getGlobalProgTag("void"))) { status = "-1"; }


            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
             boolean proceed = true;
             res = st.executeQuery("select plan_status from plan_mstr where plan_nbr = " + "'" + order + "'" 
                     + " ;");
           while (res.next()) {
                if (res.getInt("plan_status") > 0 || res.getInt("plan_status") < 0)
                    proceed = false;
           }

           if (proceed) {
                    st.executeUpdate("update plan_mstr set "
                        + "plan_cell =  " + "'" + cell + "'" + ","
                        + "plan_qty_sched =  " + "'" + schedqty.replace(defaultDecimalSeparator, '.') + "'" + ","
                        + "plan_date_sched =  " + "'" + scheddate + "'" + ","        
                        + "plan_status = " + "'" + status + "'"
                        + " where plan_nbr = " + "'" + order + "'" 
                        + ";");
                    myreturn = true;
           }
        } catch (SQLException s) {
            MainFrame.bslog(s);
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
      return myreturn;
  }

    public static boolean updatePlanOrderStatus(String order, String status) {
      if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "updatePlanOrderStatus"});
            list.add(new String[]{"param1", order});
            list.add(new String[]{"param2", status});
            try {
                return jsonToBoolean(sendServerPost(list, "", null, "dataServSCH"));
            } catch (IOException ex) {
                bslog(ex);
                return false;
            }
        } 
      boolean myreturn = false;  
      try {

           if (status.equals(getGlobalProgTag("open"))) { status = "0"; }
           if (status.equals(getGlobalProgTag("closed"))) { status = "1"; }
           if (status.equals(getGlobalProgTag("void"))) { status = "-1"; }


            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            try {
             boolean proceed = true;
             res = st.executeQuery("select plan_status from plan_mstr where plan_nbr = " + "'" + order + "'" 
                     + " ;");
           while (res.next()) {
                if (res.getInt("plan_status") > 0 || res.getInt("plan_status") < 0)
                    proceed = false;
           }

           if (proceed) {
                    st.executeUpdate("update plan_mstr set "       
                        + "plan_status = " + "'" + status + "'"
                        + " where plan_nbr = " + "'" + order + "'" 
                        + ";");
                    myreturn = true;
           }
        } catch (SQLException s) {
            MainFrame.bslog(s);
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
      return myreturn;
  }
    
    public static boolean updatePlanOrderRemarks(String order, String remarks) {
      if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "updatePlanOrderRemarks"});
            list.add(new String[]{"param1", order});
            list.add(new String[]{"param2", remarks});
            try {
                return jsonToBoolean(sendServerPost(list, "", null, "dataServSCH"));
            } catch (IOException ex) {
                bslog(ex);
                return false;
            }
        } 
      boolean r = false;
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
                st.executeUpdate("update plan_mstr set "       
                    + "plan_rmks = " + "'" + remarks + "'"
                    + " where plan_nbr = " + "'" + order + "'" 
                    + ";");
                r = true;
           
        } catch (SQLException s) {
            MainFrame.bslog(s);
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
        return r;
  }
    
    
    public static boolean updatePlanOperationStatusQty(String plan, String op, String status, double qty) {
      if (bsmf.MainFrame.remoteDB && ! bsmf.MainFrame.isSSHConnected) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(new String[]{"id", "updatePlanOperationStatusQty"});
            list.add(new String[]{"param1", plan});
            list.add(new String[]{"param2", op});
            list.add(new String[]{"param3", status});
            list.add(new String[]{"param4", bsNumber(qty)});
            try {
                return jsonToBoolean(sendServerPost(list, "", null, "dataServSCH"));
            } catch (IOException ex) {
                bslog(ex);
                return false;
            }
        } 
      boolean x = false;  
      try {
            Connection con = null;
            if (ds != null) {
              con = ds.getConnection();
            } else {
              con = DriverManager.getConnection(url + db, user, pass);  
            }
            Statement st = con.createStatement();
            ResultSet res = null;
            int i = 0;
            double qtycomp = 0;
            try {
             res = st.executeQuery("select plo_id, plo_qty_comp from plan_operation where plo_parent = " + "'" + plan + "'"
                     + " and plo_op = " + "'" + op + "'"
                     + " ;");
           while (res.next()) {
               i++;
               qtycomp += res.getDouble("plo_qty_comp");               
           }
           
           qtycomp += qty; // add incoming qty to stored qty

           if (i > 0) {
                    st.executeUpdate("update plan_operation set "
                        + "plo_qty_comp =  " + "'" + qtycomp + "'" + ","       
                        + "plo_status = " + "'" + status + "'"
                        + " where plo_parent = " + "'" + plan + "'" 
                        + " and plo_op = " + "'" + op + "'"
                        + ";");
                    x = true;
           }
        } catch (SQLException s) {
            MainFrame.bslog(s);
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
      return x;
  }


    
    
     public record plan_mstr(String[] m, int plan_nbr, String plan_item,
        String plan_site, double plan_qty_req, double plan_qty_comp, double plan_qty_sched,
        String plan_date_create, String plan_date_due, String plan_date_sched, int plan_status,
        String plan_rmks, String plan_order, String plan_line, String plan_type, String plan_cell,
        int plan_is_sched) {
         plan_mstr(String[] m) {
            this(m, 0, "", "", 0, 0, 0, "", "", "", 0,
                    "", "", "", "", "", 0);
        }
    }
    
     public record plan_operation(String[] m, int plo_id, int plo_parent, 
        int plo_op, double plo_qty, double plo_qty_comp, String plo_cell,
        String plo_operator, String plo_operatorname, String plo_date, 
        String plo_status, String plo_userid, String plo_desc, String plo_notes) {
        public plan_operation(String[] m) {
            this(m, 0, 0, 0, 0, 0, "", "", "", "", "", "", "", "");
        }
        public plan_operation update_plo_qty_comp(plan_operation po, double qty) {
            Objects.requireNonNull(po, "record plan_operation is required");
            double newqty = po.plo_qty_comp + qty;
            return new plan_operation(po.m, po.plo_id, po.plo_parent, po.plo_op, po.plo_qty, newqty, po.plo_cell, po.plo_operator, po.plo_operatorname, po.plo_date, po.plo_status, po.plo_userid, po.plo_desc, po.plo_notes);
        }
    }
    
    
}
