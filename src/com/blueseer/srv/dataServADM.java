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


import com.blueseer.adm.admData;
import static com.blueseer.adm.admData.addFTPMstr;
import static com.blueseer.adm.admData.deleteFTPAttrMstr;
import static com.blueseer.adm.admData.deleteFTPMstr;
import static com.blueseer.adm.admData.getFTPAttr;
import static com.blueseer.adm.admData.getFTPAttrHash;
import static com.blueseer.adm.admData.getFTPMstr;
import static com.blueseer.adm.admData.getSiteInit;
import static com.blueseer.adm.admData.updateFTPMstr;
import com.blueseer.edi.FTPMaint;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringArrayToJson;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringToJson;
import static com.blueseer.utl.BlueSeerUtils.arrayToJson;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuthAPI;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author terryva
 */
public class dataServADM extends HttpServlet {
 
    
        
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
        case "getAllPKSKeysExceptStore" : 
            response.getWriter().print(ArrayListStringToJson(admData.getAllPKSKeysExceptStore()));
            break;
     
        case "getSiteInit" : { 
            response.getWriter().print(ArrayListStringArrayToJson(getSiteInit(request.getHeader("param1"), request.getHeader("param2"))));
            break;  
        }    
        
        case "addFTPMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            admData.ftp_mstr x = objectMapper.readValue(sb.toString(), admData.ftp_mstr.class);            
            response.getWriter().print(arrayToJson(addFTPMstr(x)));
            break;
          }
        
        case "updateFTPMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            admData.ftp_mstr x = objectMapper.readValue(sb.toString(), admData.ftp_mstr.class);            
            response.getWriter().print(arrayToJson(updateFTPMstr(x)));
            break;
          }
        
        case "deleteFTPMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            admData.ftp_mstr x = objectMapper.readValue(sb.toString(), admData.ftp_mstr.class);            
            response.getWriter().print(arrayToJson(deleteFTPMstr(x)));
            break;
          }
        
        case "getFTPMstr" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            admData.ftp_mstr x = getFTPMstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "getFTPAttr" : { 
            ArrayList<admData.ftp_attr> x = getFTPAttr(new String[]{request.getHeader("param1")}); 
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "getFTPAttrHash" : { 
            HashMap<String, String> x = getFTPAttrHash(new String[]{request.getHeader("param1")}); 
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        
        case "addUpdateFTPAttr" : {
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            reader.close();
            ObjectMapper om = new ObjectMapper();
            String[] ca = sb.toString().split("=_=", -1);
            String x = ca[0];
            ArrayList<String[]> list = om.readValue(ca[1], new TypeReference<ArrayList<String[]>>() {});
            response.getWriter().print(arrayToJson(admData.addUpdateFTPAttr(x, list)));    
            break; 
            }
        
        case "deleteFTPAttrMstr" : {
            response.getWriter().print(arrayToJson(deleteFTPAttrMstr(request.getHeader("param1")
                    )));  
            break;
        }
        
        case "runFTPClient" :
            response.getWriter().print(ArrayListStringToJson(admData.runFTPClient(request.getHeader("param1"))));     
            break;
        
        default:
        response.getWriter().print("no switch case exists in dataServADM for id: " + id);
        System.out.println("no switch case exists in dataServADM for id: " + id);    
            
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
