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

import static com.blueseer.adm.admData.getLoginInit;
import com.blueseer.far.farData;
import static com.blueseer.far.farData.getARMstr;
import com.blueseer.fgl.fglData;
import com.blueseer.fgl.fglData.AcctMstr;
import static com.blueseer.fgl.fglData.addAcctMstr;
import static com.blueseer.fgl.fglData.deleteAcctMstr;
import com.blueseer.fgl.fglData.exc_mstr;
import static com.blueseer.fgl.fglData.getAccountActivityYear;
import static com.blueseer.fgl.fglData.getAccountBalanceReport;
import static com.blueseer.fgl.fglData.getAcctMstr;
import static com.blueseer.fgl.fglData.getBankMstr;
import static com.blueseer.fgl.fglData.getCurrMstr;
import static com.blueseer.fgl.fglData.getDeptMstr;
import static com.blueseer.fgl.fglData.getExcMstr;
import static com.blueseer.fgl.fglData.getFINInit;
import static com.blueseer.fgl.fglData.updateAcctMstr;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringArrayToJson;
import static com.blueseer.utl.BlueSeerUtils.arrayToJson;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuth;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuthAPI;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
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
public class dataServFIN extends HttpServlet {
 
    
        
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
        response.getWriter().println(" br549finpost authorization failed");
        return;
    }
    
    if (request.getHeader("id") == null || request.getHeader("id").isEmpty()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().println(HttpServletResponse.SC_BAD_REQUEST + ": missing id " + "\n" + getHeaders(request) );  
      return;
    }
    
    String id = request.getHeader("id");
    
    switch (id) {
     
    case "getLoginInit" : { 
      String user = request.getHeader("user");          
      response.getWriter().print(ArrayListStringArrayToJson(getLoginInit(user)));
      break;
    }
          
    case "addAcctMstr" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      AcctMstr am = objectMapper.readValue(sb.toString(), AcctMstr.class);            
      response.getWriter().print(arrayToJson(addAcctMstr(am)));
      break;
    }
    
    case "updateAcctMstr" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      AcctMstr am = objectMapper.readValue(sb.toString(), AcctMstr.class);            
      response.getWriter().print(arrayToJson(updateAcctMstr(am)));
      break;
    }
    
    case "deleteAcctMstr" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      AcctMstr am = objectMapper.readValue(sb.toString(), AcctMstr.class);            
      response.getWriter().print(arrayToJson(deleteAcctMstr(am)));
      break;
    }
    
    case "getAcctMstr" : {
      String[] key = new String[]{request.getHeader("key")}; 
      AcctMstr am = getAcctMstr(key);
      ObjectMapper objectMapper = new ObjectMapper();
      String r = objectMapper.writeValueAsString(am);
      response.getWriter().print(r);
      break;
    }
    
    case "getDeptMstr" : {
      String[] key = new String[]{request.getHeader("key")}; 
      fglData.dept_mstr am = getDeptMstr(key);
      ObjectMapper objectMapper = new ObjectMapper();
      String r = objectMapper.writeValueAsString(am);
      response.getWriter().print(r);
      break;
    }
    
    case "getBankMstr" : {
      String[] key = new String[]{request.getHeader("key")}; 
      fglData.BankMstr am = getBankMstr(key);
      ObjectMapper objectMapper = new ObjectMapper();
      String r = objectMapper.writeValueAsString(am);
      response.getWriter().print(r);
      break;
    }
    
    case "getCurrMstr" : {
      String[] key = new String[]{request.getHeader("key")}; 
      fglData.CurrMstr am = getCurrMstr(key);
      ObjectMapper objectMapper = new ObjectMapper();
      String r = objectMapper.writeValueAsString(am);
      response.getWriter().print(r);
      break;
    }
    
    case "getExcMstr" : {
      String base = request.getHeader("base"); 
      ArrayList<exc_mstr> emlist = getExcMstr(base);
      ObjectMapper objectMapper = new ObjectMapper();
      String r = objectMapper.writeValueAsString(emlist);
      response.getWriter().print(r);
      break;
    }
    
    case "getARMstr" : { 
      String[] key = new String[]{request.getHeader("param1"), request.getHeader("param2")}; 
      farData.ar_mstr am = getARMstr(key);
      ObjectMapper objectMapper = new ObjectMapper();
      String r = objectMapper.writeValueAsString(am);
      response.getWriter().print(r);
      break;
    }
    
    case "getInvoiceBrowseView" : { 
      response.getWriter().print(fglData.getInvoiceBrowseView(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5"),
                    request.getHeader("param6") ));
      break;
    } 
    
    case "getInvoiceBrowseDetail" : { 
      response.getWriter().print(fglData.getInvoiceBrowseDetail(request.getHeader("param1"))); 
      break;
    }     
    
    case "getFINInit" : {
      String param1 = request.getHeader("param1"); 
      String user = request.getHeader("param2");
      response.getWriter().print(ArrayListStringArrayToJson(getFINInit(param1, user)));
      break;
    }     
    
    case "PostGL" : { 
      fglData.PostGL();
      response.getWriter().print(arrayToJson(new String[]{BlueSeerUtils.SuccessBit, getMessageTag(1125)}));
      break;
    }
    
    default:
        response.getWriter().print("no switch case exists in dataServFIN for id: " + id);
        System.out.println("no switch case exists in dataServFIN for id: " + id);    
            
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
