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

import static com.blueseer.fgl.fglData.getAccountActivityYear;
import static com.blueseer.fgl.fglData.getAccountBalanceReport;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringArrayToJson;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringToJson;
import static com.blueseer.utl.BlueSeerUtils.HashMapStringIntegerToJson;
import static com.blueseer.utl.BlueSeerUtils.boolToJson;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuth;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuthAPI;
import static com.blueseer.utl.BlueSeerUtils.doubleToJson;
import static com.blueseer.utl.BlueSeerUtils.intToJson;
import com.blueseer.utl.OVData;
import static com.blueseer.utl.OVData.addSysMetaDataNoUnique;
import static com.blueseer.utl.OVData.getCodeMstrValueList;
import static com.blueseer.utl.OVData.getExchangeRate;
import static com.blueseer.utl.OVData.getNextNbr;
import static com.blueseer.utl.OVData.getSysMetaData;
import static com.blueseer.utl.OVData.getSysMetaValue;
import static com.blueseer.utl.OVData.getSystemAttachmentDirectory;
import static com.blueseer.utl.OVData.getTableInfo;
import static com.blueseer.utl.OVData.getTaxAmtApplicableByItem;
import static com.blueseer.utl.OVData.getTaxPercentElementsApplicableByItem;
import static com.blueseer.utl.OVData.isValidCustShipTo;
import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author terryva
 */
public class dataServOV extends HttpServlet {
 
    
        
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
    
    if (! confirmServerAuthAPI(request, authServ.hmuser)) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println(" br549 authorization failed");
        return;
    }
    
    if (request.getHeader("id") == null || request.getHeader("id").isEmpty()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().println(HttpServletResponse.SC_BAD_REQUEST + ": missing id " + "\n" + getHeaders(request) );  
      return;
    }
    
    String id = request.getHeader("id"); 
    
    switch (id) {
        case "getCodeMstrValueList" :        
            response.getWriter().print(ArrayListStringToJson(getCodeMstrValueList(request.getHeader("code"))));
            break;
            
        case "getTaxPercentElementsApplicableByItem" :        
            response.getWriter().print(ArrayListStringArrayToJson(getTaxPercentElementsApplicableByItem(request.getHeader("param1"))));
            break;    
            
        case "getSysMetaData" :        
            response.getWriter().print(ArrayListStringToJson(getSysMetaData(request.getHeader("param1"),request.getHeader("param2"),request.getHeader("param3"))));
            break; 
            
        case "getSysMetaValue" :        
            response.getWriter().print(getSysMetaValue(request.getHeader("param1"),request.getHeader("param2"),request.getHeader("param3")));
            break; 
            
        case "getExchangeRate" :        
            response.getWriter().print(getExchangeRate(request.getHeader("param1"),request.getHeader("param2")));
            break;   
            
        case "getSystemAttachmentDirectory" :        
            response.getWriter().print(getSystemAttachmentDirectory());
            break;    
            
        case "getNextNbr" : 
            response.getWriter().print(intToJson(getNextNbr(request.getHeader("param1")))); 
            break;
            
        case "isValidCustShipTo" : 
            response.getWriter().print(boolToJson(isValidCustShipTo(request.getHeader("param1"), request.getHeader("param2")))); 
            break;    
            
        case "getTaxAmtApplicableByItem" : 
            response.getWriter().print(doubleToJson(getTaxAmtApplicableByItem(request.getHeader("param1"),
                    bsParseDouble(request.getHeader("param2")))));   
            break;    
            
        case "getTableInfo" : 
            response.getWriter().print(HashMapStringIntegerToJson(getTableInfo(new String[]{request.getHeader("param1")})));
            break;  
            
        case "getTaxPercentElementsApplicableByTaxCode" :        
            response.getWriter().print(ArrayListStringArrayToJson(OVData.getTaxPercentElementsApplicableByTaxCode(request.getHeader("param1"))));
            break;   
            
        case "addSysMetaDataNoUnique" : {
        response.getWriter().println(boolToJson(addSysMetaDataNoUnique(request.getHeader("param1"), 
                request.getHeader("param2"),
                request.getHeader("param3"),
                request.getHeader("param4")))); 
        break;   
        }
                     
        default:
        response.getWriter().print("no switch case exists in dataServOV for id: " + id);
        System.out.println("no switch case exists in dataServOV for id: " + id);    
            
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
