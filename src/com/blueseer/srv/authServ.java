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


import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bouncycastle.util.encoders.Base64;


/**
 *
 * @author terryva
 */
public class authServ extends HttpServlet {
 
    public static HashMap<String, String> hmuser = new HashMap<String, String>();
        
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
    response.setContentType("text/plain");
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.getWriter().println(" get method not supported");
        
    }

 @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/plain");
    
        
    if (request.getHeader("id") == null || request.getHeader("id").isEmpty()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().println(HttpServletResponse.SC_BAD_REQUEST + ": missing id " + "\n" + getHeaders(request) );  
      return;
    }
    
    if (request.getHeader("id").equals("loginAPI")) {
        String user = request.getHeader("user");
        String pass = request.getHeader("pass");
        String sessionid = request.getHeader("sessionid");
        String ip = request.getRemoteAddr();
        
        System.out.println("HERE:  " + user + "/" + pass + "/" + sessionid + "/" + ip);
        
        if (sessionid.isBlank()) {  // must be login
            sessionid = Long.toHexString(System.currentTimeMillis());
            
            if (! bsmf.MainFrame.confirmServerLogin(request, sessionid)) {
                System.out.println("unauthorized...sending '0' " + " for user: " + user + "/" + pass + "/"  + sessionid + "/" + ip);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().print("0");
            } else { 
                System.out.println("authorized...adding " + user + " with sessionid: " + sessionid + " for ip: " + ip);
                hmuser.remove(user);                
                String b64string = Base64.toBase64String(sessionid.getBytes());
                hmuser.put(user, sessionid + "," + ip); 
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().print(b64string);
            }
        } 
    }
       
    } // doPost
     
    
    private String getHeaders(HttpServletRequest request) {
    
    StringBuilder requestHeaders = new StringBuilder();

            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String hd = headerNames.nextElement();
                requestHeaders.append("Header  ").append(hd).append("  Value  ").append(request.getHeader(hd)).append("\n");
            }
    return requestHeaders.toString();
}


}
