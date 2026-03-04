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
import com.blueseer.pur.purData;
import static com.blueseer.pur.purData.getPOMstrSet;
import com.blueseer.rcv.rcvData;
import static com.blueseer.rcv.rcvData.getReceiverLines;
import static com.blueseer.rcv.rcvData.getReceiverMstrSet;
import static com.blueseer.rcv.rcvData.getReceiversFromPO;
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
public class dataServRCV extends HttpServlet {
 
    
        
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
        
        case "addReceiverTransaction" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            rcvData.recv_det[] rdarray = om.readValue(ca[0], rcvData.recv_det[].class);
            ArrayList<rcvData.recv_det> rvdlist = new ArrayList<rcvData.recv_det>(Arrays.asList(rdarray)); 
            rcvData.recv_mstr rv = om.readValue(ca[1], rcvData.recv_mstr.class); 
            fapData.ap_mstr ap = om.readValue(ca[2], fapData.ap_mstr.class);
            fapData.vod_mstr[] sdarray = om.readValue(ca[3], fapData.vod_mstr[].class);
            ArrayList<fapData.vod_mstr> vodlist = new ArrayList<fapData.vod_mstr>(Arrays.asList(sdarray)); 
            response.getWriter().print(arrayToJson(rcvData.addReceiverTransaction(rvdlist, rv, ap, vodlist))); 
            break;
            }
        
        case "updateReceiverTransaction" : {
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
            rcvData.recv_det[] rdarray = om.readValue(ca[0], rcvData.recv_det[].class);
            ArrayList<rcvData.recv_det> rvdlist = new ArrayList<rcvData.recv_det>(Arrays.asList(rdarray)); 
            rcvData.recv_mstr rv = om.readValue(ca[1], rcvData.recv_mstr.class); 
            response.getWriter().print(arrayToJson(rcvData.updateReceiverTransaction(key, badlist, rvdlist, rv))); 
            break;
            }
        
        case "getReceiverMstrSet" : {       
        rcvData.Receiver rcv = getReceiverMstrSet(new String[]{request.getHeader("param1")});
        ObjectMapper objectMapper = new ObjectMapper();
        String r = objectMapper.writeValueAsString(rcv);
        response.getWriter().print(r);
        break;
        }
        
        case "getReceiversFromPO" : {
            response.getWriter().print(ArrayListStringToJson(getReceiversFromPO(request.getHeader("param1"), 
                    request.getHeader("param2"))));  
            break;
        }
        
        case "getReceiverLines" : {
            response.getWriter().print(ArrayListStringToJson(getReceiverLines(request.getHeader("param1"))));  
            break;
        }
        
        case "getReceiverBrowseView" : { 
        response.getWriter().print(rcvData.getReceiverBrowseView(new String[]{request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5")})); 
        break;
        } 
        
        case "getReceiverDetailView" : { 
        response.getWriter().print(rcvData.getReceiverDetailView(new String[]{request.getHeader("param1")})); 
        break;
        } 
        
        case "getReceiverByPOBrowseView" : { 
        response.getWriter().print(rcvData.getReceiverByPOBrowseView(new String[]{request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5")})); 
        break;
        } 
        
        case "getReceiverByPODetailView" : { 
        response.getWriter().print(rcvData.getReceiverByPODetailView(new String[]{request.getHeader("param1"), request.getHeader("param2")})); 
        break;
        } 
        
        case "getReceiverByItemBrowseView" : { 
        response.getWriter().print(rcvData.getReceiverByItemBrowseView(new String[]{
            request.getHeader("param1"), 
            request.getHeader("param2"),
            request.getHeader("param3"),
            request.getHeader("param4"),
            request.getHeader("param5"),
            request.getHeader("param6"),
            request.getHeader("param7"),
            request.getHeader("param8")})); 
        break;
        } 
        
        default:
        response.getWriter().print("");
        System.out.println("no switch case exists in dataServRCV for id: " + id);    
            
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
