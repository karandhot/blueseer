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


import com.blueseer.inv.invData;
import static com.blueseer.inv.invData.addWorkCenterMstr;
import com.blueseer.ord.ordData;
import com.blueseer.sch.schData;
import static com.blueseer.sch.schData.addPlanMstr;
import static com.blueseer.sch.schData.addPlanOperationTrans;
import static com.blueseer.sch.schData.getPlanDetHistory;
import static com.blueseer.sch.schData.getPlanDetTotQtyByOp;
import static com.blueseer.sch.schData.getPlanMstr;
import static com.blueseer.sch.schData.getPlanOperation;
import static com.blueseer.sch.schData.getSchRptPickerData;
import static com.blueseer.sch.schData.getSummaryByDate;
import static com.blueseer.sch.schData.updatePlanOperation;
import static com.blueseer.sch.schData.updatePlanOrder;
import static com.blueseer.sch.schData.updatePlanStatus;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringArrayToJson;
import static com.blueseer.utl.BlueSeerUtils.arrayToJson;
import static com.blueseer.utl.BlueSeerUtils.boolToJson;
import static com.blueseer.utl.BlueSeerUtils.bsParseInt;
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
public class dataServSCH extends HttpServlet {
 
    
        
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
        
        case "getPlanDetHistory" :  {      
            response.getWriter().print(ArrayListStringArrayToJson(getPlanDetHistory(request.getHeader("param1"))));
            break;
        }
        
        case "getPlanDetTotQtyByOp" :  {      
            response.getWriter().print(doubleToJson(getPlanDetTotQtyByOp(request.getHeader("param1"), request.getHeader("param2"))));
            break;
        }
        
        case "getPlanMstr" : { 
            String[] key = new String[]{request.getHeader("param1"), request.getHeader("param2")}; 
            schData.plan_mstr x = getPlanMstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "addPlanMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            schData.plan_mstr x = objectMapper.readValue(sb.toString(), schData.plan_mstr.class);            
            response.getWriter().print(arrayToJson(addPlanMstr(x)));
            break;
          }
        
        case "addPlanOperationTrans" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            schData.plan_operation[] sdarray = objectMapper.readValue(sb.toString(), schData.plan_operation[].class);
            ArrayList<schData.plan_operation> sdlist = new ArrayList<schData.plan_operation>(Arrays.asList(sdarray));           
            response.getWriter().print(arrayToJson(addPlanOperationTrans(sdlist)));
            break;
          }
        
        case "getSummaryByDate" :  {      
            response.getWriter().print(ArrayListStringArrayToJson(getSummaryByDate(request.getHeader("param1"),
                    request.getHeader("param2"),
                    request.getHeader("param3"))));
            break;
        }
        
        case "getSchedulerBrowseView" : { 
        response.getWriter().print(schData.getSchedulerBrowseView(new String[]{request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5"),
                    request.getHeader("param6"),
                    request.getHeader("param7")})); 
        break;
        } 
        
        case "getSchedulerDetView" : { 
        response.getWriter().print(schData.getSchedulerDetView(new String[]{request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5"),
                    request.getHeader("param6"),
                    request.getHeader("param7"),
                    request.getHeader("param8")})); 
        break;
        } 
        
        case "getSchedulerOpView" : { 
        response.getWriter().print(schData.getSchedulerOpView(new String[]{request.getHeader("param1")})); 
        break;
        } 
        
        case "updatePlanStatus" : { 
            updatePlanStatus(request.getHeader("param1"), request.getHeader("param2"));
            break;  
        }
        
        case "updatePlanOrder" : {
            response.getWriter().print(boolToJson(updatePlanOrder(request.getHeader("param1"), 
                    request.getHeader("param2"), 
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5")))); 
            break;    
        }
        
        case "updatePlanOperation" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            schData.plan_operation x = objectMapper.readValue(sb.toString(), schData.plan_operation.class);            
            response.getWriter().print(arrayToJson(updatePlanOperation(x)));
            break;
          }
        
        case "getPlanOperation" : {       
        schData.plan_operation po = getPlanOperation(bsParseInt(request.getHeader("param1")), bsParseInt(request.getHeader("param2")));
        ObjectMapper objectMapper = new ObjectMapper();
        String r = objectMapper.writeValueAsString(po);
        response.getWriter().print(r);
        break;
        }
        
        case "getSchRptPickerData" : {
        String[] x = new String[]{
               request.getHeader("func"),
               request.getHeader("param1"), 
               request.getHeader("param2"),
               request.getHeader("param3"),
               request.getHeader("param4"),
               request.getHeader("param5"),
               request.getHeader("param6")
               };     
        response.getWriter().print(getSchRptPickerData(x));  
        break;
        }
        
        
        default:
        response.getWriter().print("");
        System.out.println("no switch case exists in dataServSCH for id: " + id);    
            
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
