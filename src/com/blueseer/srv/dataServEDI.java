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


import com.blueseer.edi.ediData;
import com.blueseer.edi.ediData.edi_ctrl;
import static com.blueseer.edi.ediData.getEDICtrl;
import static com.blueseer.edi.ediData.addupdateEDICtrl;
import com.blueseer.utl.BlueSeerUtils;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringArrayToJson;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringToJson;
import static com.blueseer.utl.BlueSeerUtils.arrayToJson;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuthAPI;
import com.blueseer.utl.EDData;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
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
public class dataServEDI extends HttpServlet {
 
    
        
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
        case "getEDIInit" :
            response.getWriter().print(ArrayListStringArrayToJson(ediData.getEDIInit(request.getHeader("param1"), request.getHeader("param2"))));
            break;
            
        case "readEDIRawFile" :
            response.getWriter().print(ArrayListStringToJson(EDData.readEDIRawFile(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    BlueSeerUtils.ConvertStringToBool(request.getHeader("param3")),
                    request.getHeader("param4"),
                    request.getHeader("param5"),
                    request.getHeader("param6") )));
            break;
            
        case "getEDIRawFileByFile" :
            response.getWriter().print(ArrayListStringToJson(EDData.getEDIRawFileByFile(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4")))); 
            break;     
            
        case "getEDIAckFile" :
            response.getWriter().print(ArrayListStringToJson(EDData.getEDIAckFile(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3") )));
            break;    
            
        case "getEDIBatchFromedi_file" :
            response.getWriter().print(EDData.getEDIBatchFromedi_file(request.getHeader("param1")));
            break;  
            
        case "getEDITransBrowseDocView" :
            response.getWriter().print(ediData.getEDITransBrowseDocView(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5"),
                    request.getHeader("param6"),
                    request.getHeader("param7") )); 
            break; 
            
        case "getEDITransBrowseFileView" :
            response.getWriter().print(ediData.getEDITransBrowseFileView(request.getHeader("param1"), 
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"),
                    request.getHeader("param5"),
                    request.getHeader("param6"),
                    request.getHeader("param7") )); 
            break;  
            
        case "getEDITransBrowseDetail" :
            response.getWriter().print(ediData.getEDITransBrowseDetail(request.getHeader("param1"), 
                    request.getHeader("param2") )); 
            break;
            
        case "getEDICtrl" : 
            edi_ctrl ec = getEDICtrl();
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(ec);
            response.getWriter().print(r);
            break;
            
        case "addupdateEDICtrl" : 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper omEDICtrl = new ObjectMapper();
            edi_ctrl ecvar = omEDICtrl.readValue(sb.toString(), edi_ctrl.class);            
            response.getWriter().print(arrayToJson(addupdateEDICtrl(ecvar)));
            break;
            
        default:
        response.getWriter().print("no switch case exists in dataServEDI for id: " + id);
        System.out.println("no switch case exists in dataServEDI for id: " + id);    
            
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
