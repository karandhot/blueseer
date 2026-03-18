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


import com.blueseer.far.farData;
import static com.blueseer.far.farData.getARMstrSet;
import static com.blueseer.far.farData.getFarRptPickerData;
import static com.blueseer.utl.BlueSeerUtils.arrayToJson;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuth;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuthAPI;
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
public class dataServFAR extends HttpServlet {
    
        
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
        case "addArTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            String artype = ca[0];
            farData.ard_mstr[] sdarray = om.readValue(ca[1], farData.ard_mstr[].class);
            ArrayList<farData.ard_mstr> ardlist = (sdarray == null) ? null :new ArrayList<farData.ard_mstr>(Arrays.asList(sdarray));  
            farData.ar_mstr ar = om.readValue(ca[2], farData.ar_mstr.class); 
            response.getWriter().print(arrayToJson(farData.addArTransaction(artype, ardlist, ar))); 
            break;
            }
                
        case "getARMstrSet" : {       
            farData.ARSet shset = getARMstrSet(new String[]{request.getHeader("param1")});
            ObjectMapper om_shset = new ObjectMapper(); 
            String r = om_shset.writeValueAsString(shset);
            response.getWriter().print(r);
            break; 
        }
        
        case "getARAgingView" : { 
        response.getWriter().print(farData.getARAgingView(new String[]{request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3")})); 
        break;
        } 
        
        case "getARAgingDetailView" : { 
        response.getWriter().print(farData.getARAgingDetailView(new String[]{request.getHeader("param1"), 
                    request.getHeader("param2")})); 
        break;
        } 
        
        case "getARAgingPaymentView" : { 
        response.getWriter().print(farData.getARAgingPaymentView(request.getHeader("param1"))); 
        break;
        } 
        
        case "getARReferencesView" : { 
        response.getWriter().print(farData.getARReferencesView(request.getHeader("param1"), request.getHeader("param2")));  
        break;
        }
        
        case "getARTransactionsView" : { 
        response.getWriter().print(farData.getARTransactionsView(new String[]{request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5")})); 
        break;
        } 
        
        case "getARTransactionsDetView" : { 
        response.getWriter().print(farData.getARTransactionsDetView(request.getHeader("param1"))); 
        break;
        }
        
        case "getARAgingExport" : { 
        response.getWriter().print(farData.getARAgingExport(request.getHeader("param1"))); 
        break;
        }
        
        case "getFarRptPickerData" : {
        String[] x = new String[]{
               request.getHeader("func"),
               request.getHeader("param1"), 
               request.getHeader("param2"),
               request.getHeader("param3"),
               request.getHeader("param4"),
               request.getHeader("param5"),
               request.getHeader("param6")
               };     
        response.getWriter().print(getFarRptPickerData(x));  
        break;
        }
        
        default:
        response.getWriter().print("");
        System.out.println("no switch case exists in dataServFAR for id: " + id);    
            
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
