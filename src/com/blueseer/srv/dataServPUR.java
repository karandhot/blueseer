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


import com.blueseer.pur.purData;
import static com.blueseer.pur.purData.addUpdatePOCtrl;
import static com.blueseer.pur.purData.getPOCtrl;
import static com.blueseer.pur.purData.getPODet;
import static com.blueseer.pur.purData.getPOListByVend;
import static com.blueseer.pur.purData.getPOMstr;
import static com.blueseer.pur.purData.getPOMstrSet;
import static com.blueseer.pur.purData.getPurchaseOrderInit;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringArrayToJson;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringToJson;
import static com.blueseer.utl.BlueSeerUtils.arrayToJson;
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
public class dataServPUR extends HttpServlet {
 
    
        
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
        response.getWriter().println(" br549edipost authorization failed");
        return;
    }
    
    if (request.getHeader("id") == null || request.getHeader("id").isEmpty()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().println(HttpServletResponse.SC_BAD_REQUEST + ": missing id " + "\n" + getHeaders(request) );  
      return;
    }
    
    String id = request.getHeader("id");
    
    switch (id) {
        
        case "addPOTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            purData.pod_mstr[] sdarray = om.readValue(ca[0],  purData.pod_mstr[].class);
            ArrayList<purData.pod_mstr> sdlist = new ArrayList<purData.pod_mstr>(Arrays.asList(sdarray)); 
            purData.po_addr poa = om.readValue(ca[1], purData.po_addr.class); 
            purData.po_mstr po = om.readValue(ca[2], purData.po_mstr.class); 
            purData.po_tax[] starray = om.readValue(ca[3], purData.po_tax[].class);
            ArrayList<purData.po_tax> potlist = new ArrayList<purData.po_tax>(Arrays.asList(starray));
            purData.pod_tax[] sodtarray = om.readValue(ca[4], purData.pod_tax[].class);
            ArrayList<purData.pod_tax> podtlist = new ArrayList<purData.pod_tax>(Arrays.asList(sodtarray));   
            purData.po_meta[] sosdarray = om.readValue(ca[5], purData.po_meta[].class);
            ArrayList<purData.po_meta> pomlist = new ArrayList<purData.po_meta>(Arrays.asList(sosdarray)); 
            response.getWriter().print(arrayToJson(purData.addPOTransaction(sdlist, poa, po,potlist, podtlist, pomlist))); 
            break;
            }
        
        case "updatePOTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            String key = ca[0];
            ArrayList<String> badlist = om.readValue(ca[1], ArrayList.class);
            purData.pod_mstr[] sdarray = om.readValue(ca[2],  purData.pod_mstr[].class);
            ArrayList<purData.pod_mstr> sdlist = new ArrayList<purData.pod_mstr>(Arrays.asList(sdarray)); 
            purData.po_addr poa = om.readValue(ca[3], purData.po_addr.class); 
            purData.po_mstr po = om.readValue(ca[4], purData.po_mstr.class); 
            purData.po_tax[] starray = om.readValue(ca[5], purData.po_tax[].class);
            ArrayList<purData.po_tax> potlist = new ArrayList<purData.po_tax>(Arrays.asList(starray));
            purData.pod_tax[] sodtarray = om.readValue(ca[6], purData.pod_tax[].class);
            ArrayList<purData.pod_tax> podtlist = new ArrayList<purData.pod_tax>(Arrays.asList(sodtarray));   
            purData.po_meta[] sosdarray = om.readValue(ca[7], purData.po_meta[].class);
            ArrayList<purData.po_meta> pomlist = new ArrayList<purData.po_meta>(Arrays.asList(sosdarray)); 
            response.getWriter().print(arrayToJson(purData.updatePOTransaction(key, badlist, sdlist, poa, po,potlist, podtlist, pomlist))); 
            break;
            }
        
        case "getPOListByVend" : {       
            response.getWriter().print(ArrayListStringToJson(getPOListByVend(request.getHeader("param1"))));
            break; 
        }
         
        case "deletePOMstr" : {
            response.getWriter().print(arrayToJson(purData.deletePOMstr(request.getHeader("param1")
                    )));  
            break; 
        }
         
        case "getPOMstr" : {       
        purData.po_mstr po = getPOMstr(new String[]{request.getHeader("param1")});
        ObjectMapper objectMapper = new ObjectMapper();
        String r = objectMapper.writeValueAsString(po);
        response.getWriter().print(r);
        break;
        }
        
        case "getPODet" : { 
            ArrayList<purData.pod_mstr> x = getPODet(new String[]{request.getHeader("param1")}); 
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "getPOMstrSet" : {       
        purData.purchaseOrder pos = getPOMstrSet(new String[]{request.getHeader("param1")});
        ObjectMapper objectMapper = new ObjectMapper();
        String r = objectMapper.writeValueAsString(pos);
        response.getWriter().print(r);
        break;
        }
        
        case "getPOBrowseView" : { 
        response.getWriter().print(purData.getPOBrowseView(new String[]{request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5")})); 
        break;
        } 
        
        case "getPODetailView" : { 
        response.getWriter().print(purData.getPODetailView(new String[]{request.getHeader("param1")})); 
        break;
        } 
        
        case "getPOItemBrowseView" : { 
        response.getWriter().print(purData.getPOItemBrowseView(new String[]{request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5"),
                    request.getHeader("param6"),
                    request.getHeader("param7"),
                    request.getHeader("param8")})); 
        break;
        } 
        
        case "getPurchaseOrderInit" : { 
            response.getWriter().print(ArrayListStringArrayToJson(getPurchaseOrderInit(request.getHeader("param1"), request.getHeader("param2"))));
            break;  
        }
        
        case "validatePODetail" : {
            response.getWriter().print(arrayToJson(purData.validatePODetail(request.getHeader("param1"),
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5"),
                    request.getHeader("param6"),
                    request.getHeader("param7")
                    )));  
            break;
        }
        
        case "getPOCtrl" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            purData.po_ctrl x = getPOCtrl(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
        }
        
        case "addUpdatePOCtrl" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            purData.po_ctrl x = objectMapper.readValue(sb.toString(), purData.po_ctrl.class);            
            response.getWriter().print(arrayToJson(addUpdatePOCtrl(x)));
            break;
        }
        
        default:
        response.getWriter().print("");
        System.out.println("no switch case exists in dataServPUR for id: " + id);    
            
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
