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


import com.blueseer.fap.fapData;
import static com.blueseer.fap.fapData.getAPExpenseByAcct;
import static com.blueseer.fap.fapData.getAPExpenseByVendor;
import static com.blueseer.fap.fapData.getAPVoucherSet;
import static com.blueseer.fgl.fglData.getAccountActivityYear;
import com.blueseer.ord.ordData;
import static com.blueseer.ord.ordData.addUpdateORCtrl;
import static com.blueseer.ord.ordData.addUpdateSOMeta;
import static com.blueseer.ord.ordData.applyOrderChange;
import static com.blueseer.ord.ordData.billTransAll;
import static com.blueseer.ord.ordData.deleteSOMeta;
import static com.blueseer.ord.ordData.getBillBrowseView;
import static com.blueseer.ord.ordData.getBillDet;
import static com.blueseer.ord.ordData.getBillMstr;
import static com.blueseer.ord.ordData.getBillSAC;
import static com.blueseer.ord.ordData.getORCtrl;
import static com.blueseer.ord.ordData.getOrderBrowseView;
import static com.blueseer.ord.ordData.getOrderChangeBrowseDetail;
import static com.blueseer.ord.ordData.getOrderChangeBrowseView;
import static com.blueseer.ord.ordData.getOrderChangeExport;
import static com.blueseer.ord.ordData.getOrderChangeReportData;
import static com.blueseer.ord.ordData.getOrderDet;
import static com.blueseer.ord.ordData.getOrderDetailExport;
import static com.blueseer.ord.ordData.getOrderDetailExportNew;
import static com.blueseer.ord.ordData.getOrderItemBrowseView;
import static com.blueseer.ord.ordData.getOrderMstr;
import static com.blueseer.ord.ordData.getOrderMstrSet;
import static com.blueseer.ord.ordData.getOrderReportData;
import static com.blueseer.ord.ordData.getQuoteBrowseView;
import static com.blueseer.ord.ordData.getQuoteDet;
import static com.blueseer.ord.ordData.getQuoteMstr;
import static com.blueseer.ord.ordData.getQuoteSAC;
import static com.blueseer.ord.ordData.getSOMetaData;
import static com.blueseer.ord.ordData.getSVOrderTotalTax;
import static com.blueseer.ord.ordData.getServiceOrderBrowseView;
import static com.blueseer.ord.ordData.getServiceOrderDet;
import static com.blueseer.ord.ordData.getServiceOrderMstr;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringArrayToJson;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringToJson;
import static com.blueseer.utl.BlueSeerUtils.arrayToJson;
import static com.blueseer.utl.BlueSeerUtils.boolToJson;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuth;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuthAPI;
import static com.blueseer.utl.BlueSeerUtils.doubleToJson;
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
public class dataServFAP extends HttpServlet {
    
        
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
        case "VoucherTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            String ctype = ca[0];
            fapData.vod_mstr[] sdarray = om.readValue(ca[1], fapData.vod_mstr[].class);
            ArrayList<fapData.vod_mstr> vodlist = new ArrayList<fapData.vod_mstr>(Arrays.asList(sdarray)); 
            fapData.ap_mstr ap = om.readValue(ca[2], fapData.ap_mstr.class); 
            Boolean isvoid = Boolean.valueOf(ca[3]);
            response.getWriter().print(arrayToJson(fapData.VoucherTransaction(ctype, vodlist, ap, isvoid))); 
            break;
            }
        
        case "VouchAndPayTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            String ctype = ca[0];
            fapData.vod_mstr[] sdarray = om.readValue(ca[1], fapData.vod_mstr[].class);
            ArrayList<fapData.vod_mstr> vodlist = new ArrayList<fapData.vod_mstr>(Arrays.asList(sdarray)); 
            fapData.ap_mstr ap = om.readValue(ca[2], fapData.ap_mstr.class); 
            Boolean isvoid = Boolean.valueOf(ca[3]);
            response.getWriter().print(arrayToJson(fapData.VouchAndPayTransaction(ctype, vodlist, ap, isvoid))); 
            break;
            }
       
        case "getAPVoucherSet" : {       
            fapData.VoucherAP shset = getAPVoucherSet(new String[]{request.getHeader("param1")});
            ObjectMapper om_shset = new ObjectMapper(); 
            String r = om_shset.writeValueAsString(shset);
            response.getWriter().print(r);
            break; 
        }
         
        case "getAPExpenseByVendor" : {
            response.getWriter().print(ArrayListStringArrayToJson(getAPExpenseByVendor(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5"))));
            break;
        }
        
        case "getAPExpenseByAcct" : {
            response.getWriter().print(ArrayListStringArrayToJson(getAPExpenseByAcct(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5"))));
            break;
        }
        
        
        
        default:
        response.getWriter().print("no switch case exists in dataServFAP for id: " + id);
        System.out.println("no switch case exists in dataServFAP for id: " + id);    
            
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
