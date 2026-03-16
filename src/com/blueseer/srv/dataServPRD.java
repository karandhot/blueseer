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


import static com.blueseer.prd.prdData.addPlanOpDet;
import static com.blueseer.prd.prdData.deletePlanOpDet;
import static com.blueseer.prd.prdData.getJobClockDetail;
import static com.blueseer.prd.prdData.getJobClockHistory;
import static com.blueseer.prd.prdData.getJobClockInTime;
import static com.blueseer.prd.prdData.getPlanOpDet;
import static com.blueseer.prd.prdData.getPlanOpLastOp;
import static com.blueseer.prd.prdData.getPrdRptPickerData;
import static com.blueseer.prd.prdData.getSerialBrowseView;
import static com.blueseer.prd.prdData.getSerialBrowseViewDet;
import static com.blueseer.prd.prdData.getTransBrowseView;
import static com.blueseer.prd.prdData.updatePlanOPDate;
import static com.blueseer.prd.prdData.updatePlanOPDesc;
import static com.blueseer.prd.prdData.updatePlanOPNotes;
import static com.blueseer.prd.prdData.updatePlanOPOperator;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringArrayToJson;
import static com.blueseer.utl.BlueSeerUtils.arrayToJson;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuthAPI;
import static com.blueseer.utl.BlueSeerUtils.intToJson;
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
public class dataServPRD extends HttpServlet {
 
    
        
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
        
    case "getSerialBrowseView" : {
        String[] it = new String[]{
               request.getHeader("fromdate"), 
               request.getHeader("todate"), 
               request.getHeader("fromserial"), 
               request.getHeader("toserial"), 
               request.getHeader("fromitem"), 
               request.getHeader("toitem"),
               request.getHeader("type")
               };     
        response.getWriter().print(getSerialBrowseView(it));  
        break;
        } 
       
    case "getSerialBrowseViewDet" : {
        response.getWriter().print(getSerialBrowseViewDet(request.getHeader("param1")));  
        break;
    } 
    
    case "getTransBrowseView" : {
        String[] it = new String[]{
               request.getHeader("fromdate"), 
               request.getHeader("todate"), 
               request.getHeader("fromitem"), 
               request.getHeader("toitem"),
               request.getHeader("type")
               };     
        response.getWriter().print(getTransBrowseView(it));  
        break;
        } 
    
    case "getPrdRptPickerData" : {
        String[] x = new String[]{
               request.getHeader("func"),
               request.getHeader("param1"), 
               request.getHeader("param2"),
               request.getHeader("param3"),
               request.getHeader("param4"),
               request.getHeader("param5"),
               request.getHeader("param6")
               };     
        response.getWriter().print(getPrdRptPickerData(x));  
        break;
        }
    
    case "addPlanOpDet" : {
            response.getWriter().print(intToJson(addPlanOpDet(request.getHeader("param1"),
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5"),
                    bsParseDouble(request.getHeader("param6")),
                    bsParseDouble(request.getHeader("param7")),
                    request.getHeader("param8"),
                    bsParseInt(request.getHeader("param9"))))); 
            break;    
    }
        
    case "getPlanOpLastOp" : {
            response.getWriter().print(intToJson(getPlanOpLastOp(request.getHeader("param1")))); 
            break;    
    }
    
    case "updatePlanOPNotes" : {
            response.getWriter().print(intToJson(updatePlanOPNotes(request.getHeader("param1"),
                    request.getHeader("param2"),
                    request.getHeader("param3")))); 
            break;    
    }
    
    case "updatePlanOPOperator" : {
            response.getWriter().print(intToJson(updatePlanOPOperator(request.getHeader("param1"),
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4")))); 
            break;    
    }
    
    case "updatePlanOPDate" : {
            response.getWriter().print(intToJson(updatePlanOPDate(request.getHeader("param1"),
                    request.getHeader("param2"),
                    request.getHeader("param3")))); 
            break;    
    }
    
    case "updatePlanOPDesc" : {
            response.getWriter().print(intToJson(updatePlanOPDesc(request.getHeader("param1"),
                    request.getHeader("param2"),
                    request.getHeader("param3")))); 
            break;    
    }
    
    
    case "getPlanOpDet" : {       
            response.getWriter().print(ArrayListStringArrayToJson(getPlanOpDet(request.getHeader("param1"))));
            break;  
    }
    
    case "getPlanOpDetX" : {       
            response.getWriter().print(ArrayListStringArrayToJson(getPlanOpDet(request.getHeader("param1"), request.getHeader("param2"))));
            break;  
    }
    
    case "getJobClockHistory" : {       
            response.getWriter().print(ArrayListStringArrayToJson(getJobClockHistory(request.getHeader("param1"))));
            break;  
    }
    
    case "getJobClockDetail" : {       
            response.getWriter().print(ArrayListStringArrayToJson(getJobClockDetail(bsParseInt(request.getHeader("param1")))));
            break;  
    }
    
    case "getJobClockInTime" : {       
            response.getWriter().print(arrayToJson(getJobClockInTime(bsParseInt(request.getHeader("param1")),
                    bsParseInt(request.getHeader("param2")),
                    request.getHeader("param3"))));
            break;  
    }
    
    case "deletePlanOpDet" : {       
            deletePlanOpDet(request.getHeader("param1"));
            break;  
    }
    
        default:
        response.getWriter().print("");
        System.out.println("no switch case exists in dataServPRD for id: " + id);    
            
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
