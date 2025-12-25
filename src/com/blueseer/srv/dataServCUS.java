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

import com.blueseer.ctr.cusData;
import static com.blueseer.ctr.cusData.deleteCustMstr;
import static com.blueseer.ctr.cusData.getCustLabel;
import static com.blueseer.ctr.cusData.getCustMstr;
import static com.blueseer.ctr.cusData.getCustShipSet;
import static com.blueseer.ctr.cusData.getDiscountRecsByCust;
import static com.blueseer.ctr.cusData.getcustshipmstrlist;
import static com.blueseer.ctr.cusData.updateCustMstr;
import static com.blueseer.inv.invData.getBOMsByItemSite_mg;
import static com.blueseer.inv.invData.getLocationListByWarehouse;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringArrayToJson;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringToJson;
import static com.blueseer.utl.BlueSeerUtils.HashMapStringIntegerToJson;
import static com.blueseer.utl.BlueSeerUtils.arrayToJson;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuthAPI;
import static com.blueseer.utl.BlueSeerUtils.intToJson;
import static com.blueseer.utl.OVData.getNextNbr;
import static com.blueseer.utl.OVData.getSysMetaData;
import static com.blueseer.utl.OVData.getTableInfo;
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
public class dataServCUS extends HttpServlet {
 
    
        
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
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
        
        case "addCustomerTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            cusData.cm_mstr cm = om.readValue(ca[0], cusData.cm_mstr.class);
            ArrayList<String[]> list = om.readValue(ca[1], ArrayList.class); 
            cusData.cms_det cms = om.readValue(ca[2], cusData.cms_det.class);
            response.getWriter().print(arrayToJson(cusData.addCustomerTransaction(cm, list, cms)));     
            break; 
            }    
        
        case "updateCustMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            cusData.cm_mstr x = objectMapper.readValue(sb.toString(), cusData.cm_mstr.class);            
            response.getWriter().print(arrayToJson(updateCustMstr(x)));
            break;
          }
        
        case "deleteCustMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            cusData.cm_mstr x = objectMapper.readValue(sb.toString(), cusData.cm_mstr.class);            
            response.getWriter().print(arrayToJson(deleteCustMstr(x)));
            break;
          }
        
        case "getCustMstr" :  {      
            cusData.cm_mstr cm = getCustMstr(new String[]{request.getHeader("param1")});
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(cm);
            response.getWriter().print(r);
            break;  
        }
        
        case "getcustshipmstrlist" :   {     
            response.getWriter().print(ArrayListStringToJson(getcustshipmstrlist(request.getHeader("param1"))));
            break;
        }
            
        case "getCustShipSet" :  {      
            cusData.CustShipSet cs = getCustShipSet(new String[]{request.getHeader("param1"), request.getHeader("param2")});
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(cs);
            response.getWriter().print(r);
            break;  
        }
            
        case "getDiscountRecsByCust" :    {    
            response.getWriter().print(ArrayListStringArrayToJson(cusData.getDiscountRecsByCust(request.getHeader("param1"))));
            break;  
        }
        
        case "getCustLabel" :  {       
            response.getWriter().print(getCustLabel(request.getHeader("param1")));
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
