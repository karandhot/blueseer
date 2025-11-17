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
package com.blueseer.srv;

import static bsmf.MainFrame.bslog;
import static com.blueseer.adm.admData.getLoginInit;
import com.blueseer.fgl.fglData.AcctMstr;
import static com.blueseer.fgl.fglData.addAcctMstr;
import static com.blueseer.fgl.fglData.getAccountActivityYear;
import static com.blueseer.fgl.fglData.getAccountBalanceReport;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringToJson;
import static com.blueseer.utl.BlueSeerUtils.DefaultTableModelToJson;
import static com.blueseer.utl.BlueSeerUtils.HashMapStringIntegerToJson;
import static com.blueseer.utl.BlueSeerUtils.arrayToJson;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuth;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuthAPI;
import static com.blueseer.utl.BlueSeerUtils.intToJson;
import static com.blueseer.utl.BlueSeerUtils.jsonToDefaultTableModel;
import static com.blueseer.utl.BlueSeerUtils.sendServerPost;
import static com.blueseer.utl.DTData.getAcctBrowseUtil;
import static com.blueseer.utl.DTData.getAcctBrowseUtilData;
import static com.blueseer.utl.DTData.getBankBrowseUtil;
import static com.blueseer.utl.DTData.getBankBrowseUtilData;
import static com.blueseer.utl.DTData.getCurrencyBrowseUtil;
import static com.blueseer.utl.DTData.getCurrencyBrowseUtilData;
import static com.blueseer.utl.DTData.getDeptCCBrowseUtil;
import static com.blueseer.utl.DTData.getDeptCCBrowseUtilData;
import static com.blueseer.utl.OVData.getCodeMstrValueList;
import static com.blueseer.utl.OVData.getNextNbr;
import static com.blueseer.utl.OVData.getTableInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author terryva
 */
public class dataServDT extends HttpServlet {
 
    
        
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
    response.setContentType("text/plain");
        
    if (! confirmServerAuth(request)) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println("br549 authorization failed");
        return;
    }
    
        
    if (request.getParameter("id") == null || request.getParameter("id").isEmpty()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().println(HttpServletResponse.SC_BAD_REQUEST + ": missing id");  
      return;
    }
        
        String id = request.getParameter("id");
        
        response.setStatus(HttpServletResponse.SC_OK);
        
        
        
        if (id.equals("getAccountBalanceReport")) {
           String[] keys = new String[]{
               request.getParameter("year"), 
               request.getParameter("period"), 
               request.getParameter("site"), 
               request.getParameter("iscc"), 
               request.getParameter("intype"), 
               request.getParameter("fromacct"), 
               request.getParameter("toacct")  
           }; 
           
           for (String k : keys) {
               if (k == null) {
                   response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                   response.getWriter().println(HttpServletResponse.SC_BAD_REQUEST + ": missing param");  
                   return;
               }
           }
           
           String r = getAccountBalanceReport(keys);
           
           if (r == null || r.isBlank()) {
             response.getWriter().println("no return for: " + String.join(",",keys));   
           } else {
             response.getWriter().println(r);   
           }
        } 
        
        if (id.equals("getAccountActivityYear")) {
           String[] keys = new String[]{
               request.getParameter("year"), 
               request.getParameter("site"), 
               request.getParameter("fromacct"), 
               request.getParameter("toacct")  
           }; 
           
           for (String k : keys) {
               if (k == null) {
                   response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                   response.getWriter().println(HttpServletResponse.SC_BAD_REQUEST + ": missing param");  
                   return;
               }
           }
           
           String r = getAccountActivityYear(keys);
           
           if (r == null || r.isBlank()) {
             response.getWriter().println("no return for: " + String.join(",",keys));   
           } else {
             response.getWriter().println(r);   
           }
        } 
        
        if (id.equals("setStandardCosts")) {
           String[] keys = new String[]{
               request.getParameter("site"), 
               request.getParameter("item") 
           }; 
           
           for (String k : keys) {
               if (k == null) {
                   response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                   response.getWriter().println(HttpServletResponse.SC_BAD_REQUEST + ": missing param");  
                   return;
               }
           }
           
           String r = getAccountActivityYear(keys);
           
           if (r == null || r.isBlank()) {
             response.getWriter().println("no return for: " + String.join(",",keys));   
           } else {
             response.getWriter().println(r);   
           }
        } 
        
        
    }

 @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/plain");
      
        
    
    
        
    if (request.getHeader("id") == null || request.getHeader("id").isEmpty()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().println(HttpServletResponse.SC_BAD_REQUEST + ": missing id " + "\n" + getHeaders(request) );  
      return;
    }

    String id = request.getHeader("id");
    
    
    if (! confirmServerAuthAPI(request, authServ.hmuser)) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println(" br549finpost authorization failed");
        return;
    }
       
    
        
    if (id.equals("getAcctBrowseUtilData")) { 
      String param1 = request.getHeader("param1"); 
      int param2 = Integer.parseInt(request.getHeader("param2"));
      String param3 = request.getHeader("param3");
      response.getWriter().print(getAcctBrowseUtilData(param1, param2, param3));
    }
    
    if (id.equals("getBankBrowseUtilData")) { 
      String param1 = request.getHeader("param1"); 
      int param2 = Integer.parseInt(request.getHeader("param2"));
      String param3 = request.getHeader("param3");
      response.getWriter().print(getBankBrowseUtilData(param1, param2, param3));
    }
    
    if (id.equals("getCurrencyBrowseUtilData")) { 
      String param1 = request.getHeader("param1"); 
      int param2 = Integer.parseInt(request.getHeader("param2"));
      String param3 = request.getHeader("param3");
      response.getWriter().print(getCurrencyBrowseUtilData(param1, param2, param3));
    }
    
    if (id.equals("getDeptCCBrowseUtilData")) { 
      String param1 = request.getHeader("param1"); 
      int param2 = Integer.parseInt(request.getHeader("param2"));
      String param3 = request.getHeader("param3");
      response.getWriter().print(getDeptCCBrowseUtilData(param1, param2, param3));
    }
    
   
      
       
    } // doPost
     
    
    private String getHeaders(HttpServletRequest request) {
    
    StringBuilder requestHeaders = new StringBuilder();

            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String hd = headerNames.nextElement();
                requestHeaders.append("Header  " + hd).append("  Value  " + request.getHeader(hd)).append("\n");
            }
    return requestHeaders.toString();
}


}
