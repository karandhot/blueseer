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
import com.blueseer.ord.ordData;
import static com.blueseer.ord.ordData.getOrderBrowseView;
import static com.blueseer.ord.ordData.getOrderChangeExport;
import static com.blueseer.ord.ordData.getOrderChangeReportData;
import static com.blueseer.ord.ordData.getOrderDet;
import static com.blueseer.ord.ordData.getOrderDetailExport;
import static com.blueseer.ord.ordData.getOrderDetailExportNew;
import static com.blueseer.ord.ordData.getOrderMstrSet;
import static com.blueseer.ord.ordData.getOrderReportData;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringArrayToJson;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuth;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuthAPI;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class dataServORD extends HttpServlet {
    
        
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
        case "getOrderBrowseInit" :
            response.getWriter().print(ArrayListStringArrayToJson(ordData.getOrderBrowseInit(request.getHeader("param1"), request.getHeader("param2"))));
            break;
            
        case "getSalesOrderInit" :
            response.getWriter().print(ArrayListStringArrayToJson(ordData.getSalesOrderInit(request.getHeader("param1"), request.getHeader("param2"))));
            break;    
            
        case "exportOrderDetail" : 
        response.getWriter().println(getOrderDetailExportNew(request.getHeader("fromdate"), 
                request.getHeader("todate"), 
                request.getHeader("fromcust"), 
                request.getHeader("tocust"), 
                request.getHeader("site"))); 
        break;

        case "exportOrderChange" :
        response.getWriter().println(getOrderChangeExport(request.getHeader("fromdate"), 
                request.getHeader("todate"), 
                request.getHeader("fromcust"), 
                request.getHeader("tocust"), 
                request.getHeader("site"))); 
        break;

        case "getOrderBrowseView" :
        String[] or = new String[]{
               request.getHeader("fromdate"), 
               request.getHeader("todate"), 
               request.getHeader("fromcust"), 
               request.getHeader("tocust"), 
               request.getHeader("site"), 
               request.getHeader("datetype")
               };     
        response.getWriter().print(getOrderBrowseView(or));  
        break;

        case "orderChangeReport" :
        String[] ocr = new String[]{
               request.getHeader("fromdate"), 
               request.getHeader("todate"), 
               request.getHeader("fromcust"), 
               request.getHeader("tocust"), 
               request.getHeader("site"), 
               request.getHeader("datetype")
               };     
        response.getWriter().println(getOrderChangeReportData(ocr)); 
        break;

        case "getOrderMstrSet" :        
        ordData.salesOrder cs = getOrderMstrSet(new String[]{request.getHeader("param1")});
        ObjectMapper objectMapper = new ObjectMapper();
        String r = objectMapper.writeValueAsString(cs);
        response.getWriter().print(r);
        break; 
        
        case "getOrderDet" :        
        ordData.sod_det sd = getOrderDet(request.getHeader("param1"), request.getHeader("param2"));
        ObjectMapper omsd = new ObjectMapper(); 
        String rsd = omsd.writeValueAsString(sd);
        response.getWriter().print(rsd);
        break; 

        default:
        response.getWriter().print("no switch case exists in dataServORD for id: " + id);
        System.out.println("no switch case exists in dataServORD for id: " + id);    
            
    }   
    
       
    }
   
    
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
