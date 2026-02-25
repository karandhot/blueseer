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
import com.blueseer.fap.fapData;
import static com.blueseer.fap.fapData.addUpdateAPCtrl;
import static com.blueseer.fap.fapData.getAPCtrl;
import com.blueseer.far.farData;
import static com.blueseer.far.farData.addUpdateARCtrl;
import static com.blueseer.far.farData.getARCtrl;
import static com.blueseer.far.farData.getARMstr;
import com.blueseer.fgl.fglData;
import com.blueseer.fgl.fglData.AcctMstr;
import static com.blueseer.fgl.fglData.addAcctMstr;
import static com.blueseer.fgl.fglData.addGL;
import static com.blueseer.fgl.fglData.deleteAcctMstr;
import static com.blueseer.fgl.fglData.deleteGL;
import com.blueseer.fgl.fglData.exc_mstr;
import static com.blueseer.fgl.fglData.getAccountActivityYear;
import static com.blueseer.fgl.fglData.getAcctMstr;
import static com.blueseer.fgl.fglData.getBankMstr;
import static com.blueseer.fgl.fglData.getCurrMstr;
import static com.blueseer.fgl.fglData.getDeptMstr;
import static com.blueseer.fgl.fglData.getExcMstr;
import static com.blueseer.fgl.fglData.getFINInit;
import static com.blueseer.fgl.fglData.getGLAcctListRangeWCurrTypeDesc;
import static com.blueseer.fgl.fglData.getGLCalForPeriod;
import static com.blueseer.fgl.fglData.getGLCalForPeriodRange;
import static com.blueseer.fgl.fglData.getGLCalYearsRange;
import static com.blueseer.fgl.fglData.getGLCtrl;
import static com.blueseer.fgl.fglData.getGLHist;
import static com.blueseer.fgl.fglData.getGLTran;
import static com.blueseer.fgl.fglData.getTaxDet;
import static com.blueseer.fgl.fglData.updateAcctMstr;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringArrayToJson;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringToJson;
import static com.blueseer.utl.BlueSeerUtils.arrayToJson;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuth;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuthAPI;
import static com.blueseer.utl.BlueSeerUtils.getMessageTag;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
        
        
        /*
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
        */
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
    
    case "getTaxDet" : {
      String param1 = request.getHeader("param1"); 
      ArrayList<fglData.taxd_mstr> emlist = getTaxDet(param1);
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
    
    case "addGLpair" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      fglData.gl_pair x = objectMapper.readValue(sb.toString(), fglData.gl_pair.class);            
      response.getWriter().print(arrayToJson(addGL(x)));
      break;
    }
    
    case "addGLtran" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      fglData.gl_tran x = objectMapper.readValue(sb.toString(), fglData.gl_tran.class);            
      response.getWriter().print(arrayToJson(addGL(x)));
      break;
    }
    
    case "addGLtrans" : {
      String line;
      StringBuilder sb = new StringBuilder();  
      BufferedReader reader = request.getReader();  // as string
      while ((line = reader.readLine()) != null) {  
      sb.append(line);
      } 
      ObjectMapper objectMapper = new ObjectMapper();
      fglData.gl_tran[] x = objectMapper.readValue(sb.toString(), fglData.gl_tran[].class);   
      ArrayList<fglData.gl_tran> list = new ArrayList<fglData.gl_tran>(Arrays.asList(x));       
      response.getWriter().print(arrayToJson(addGL(list)));
      break;
    }
    
    case "getGLTran" : {       
        ArrayList<fglData.gl_tran> xd = getGLTran(new String[]{request.getHeader("param1")});
        ObjectMapper omsd = new ObjectMapper(); 
        String rsd = omsd.writeValueAsString(xd);
        response.getWriter().print(rsd);
        break;
        } 
    
    case "getGLHist" : {       
        ArrayList<fglData.gl_hist> xd = getGLHist(new String[]{request.getHeader("param1")});
        ObjectMapper omsd = new ObjectMapper(); 
        String rsd = omsd.writeValueAsString(xd);
        response.getWriter().print(rsd);
        break;
        } 
    
    case "deleteGL" : {
            response.getWriter().print(arrayToJson(deleteGL(request.getHeader("param1"))));  
            break;
        }
    
    case "getExpenseBrowseView" : { 
      response.getWriter().print(fglData.getExpenseBrowseView(new String[]{request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5")})); 
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
    
    case "getAccountBalanceView" : { 
      response.getWriter().print(fglData.getAccountBalanceView(new String[]{request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5"),
                    request.getHeader("param6"),
                    request.getHeader("param7"),
                    request.getHeader("param8")} ));
      break;
    } 
    
    case "getAccountBalanceDetView" : { 
      response.getWriter().print(fglData.getAccountBalanceDetView(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    bsParseInt(request.getHeader("param4")),
                    bsParseInt(request.getHeader("param5")),
                    BlueSeerUtils.ConvertStringToBool(request.getHeader("param6"))));
      break;
    } 
    
    case "getInvoiceBrowseDetail" : { 
      response.getWriter().print(fglData.getInvoiceBrowseDetail(request.getHeader("param1"))); 
      break;
    }     
    
    case "getFINInit" : {
      response.getWriter().print(ArrayListStringArrayToJson(getFINInit(request.getHeader("param1"), request.getHeader("param2"))));
      break;
    }  
    
    case "getGLAcctListRangeWCurrTypeDesc" : {
      response.getWriter().print(ArrayListStringArrayToJson(getGLAcctListRangeWCurrTypeDesc(request.getHeader("param1"), request.getHeader("param2"))));
      break;
    } 
    
    case "PostGL" : { 
      fglData.PostGL();
      response.getWriter().print(arrayToJson(new String[]{BlueSeerUtils.SuccessBit, getMessageTag(1125)}));
      break;
    }
    
    case "getGLCtrl" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            fglData.gl_ctrl x = getGLCtrl(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
    }
    
    case "getARCtrl" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            farData.ar_ctrl x = getARCtrl(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
    }
    
    case "getAPCtrl" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            fapData.ap_ctrl x = getAPCtrl(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
    }
    
    case "addUpdateAPCtrl" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            fapData.ap_ctrl x = objectMapper.readValue(sb.toString(), fapData.ap_ctrl.class);            
            response.getWriter().print(arrayToJson(addUpdateAPCtrl(x)));
            break;
          }
    
    case "addUpdateARCtrl" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            farData.ar_ctrl x = objectMapper.readValue(sb.toString(), farData.ar_ctrl.class);            
            response.getWriter().print(arrayToJson(addUpdateARCtrl(x)));
            break;
          }
    
    case "getGLCalForDate" :
            response.getWriter().print(arrayToJson(fglData.getGLCalForDate(BlueSeerUtils.parseDate(request.getHeader("param1")))));   
            break;
            
    case "getGLAcctDescType" : 
            response.getWriter().print(arrayToJson(fglData.getGLAcctDescType(request.getHeader("param1"))));   
            break; 
            
    case "deleteGLIC" : 
            response.getWriter().print(arrayToJson(fglData.deleteGLIC(request.getHeader("param1"))));   
            break;        
            
    case "getGLAcctDesc" : 
            response.getWriter().print(fglData.getGLAcctDesc(request.getHeader("param1")));   
            break;    
            
   case "getGLCCDesc" : 
            response.getWriter().print(fglData.getGLCCDesc(request.getHeader("param1")));   
            break;             
            
    case "getGLCalForPeriod" : {
      response.getWriter().print(ArrayListStringToJson(getGLCalForPeriod(bsParseInt(request.getHeader("param1")), bsParseInt(request.getHeader("param2")))));
      break;
    }         
    
    case "getGLCalForPeriodRange" : {
      response.getWriter().print(ArrayListStringToJson(getGLCalForPeriodRange(bsParseInt(request.getHeader("param1")), bsParseInt(request.getHeader("param2")),
              bsParseInt(request.getHeader("param3")))));
      break;
    }  
    
    case "getGLCalYearsRange" : {
      response.getWriter().print(ArrayListStringToJson(getGLCalYearsRange()));
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
