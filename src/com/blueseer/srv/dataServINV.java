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

import static com.blueseer.fgl.fglData.getAccountActivityYear;
import static com.blueseer.fgl.fglData.getAccountBalanceReport;
import com.blueseer.inv.invData;
import static com.blueseer.inv.invData.addItemMstr;
import static com.blueseer.inv.invData.addLocationMstr;
import static com.blueseer.inv.invData.addPLMstr;
import static com.blueseer.inv.invData.addRoutingMstr;
import static com.blueseer.inv.invData.addUOMMstr;
import static com.blueseer.inv.invData.addWareHouseMstr;
import static com.blueseer.inv.invData.addWorkCenterMstr;
import static com.blueseer.inv.invData.bind_tree_op;
import static com.blueseer.inv.invData.deleteItemMstr;
import static com.blueseer.inv.invData.deleteLocationMstr;
import static com.blueseer.inv.invData.deletePLMstr;
import static com.blueseer.inv.invData.deleteRoutingMstr;
import static com.blueseer.inv.invData.deleteUOMMstr;
import static com.blueseer.inv.invData.deleteWareHouseMstr;
import static com.blueseer.inv.invData.deleteWorkCenterMstr;
import static com.blueseer.inv.invData.getBOMsByItemSite_mg;
import static com.blueseer.inv.invData.getCurrentCost;
import static com.blueseer.inv.invData.getInvMaintInit;
import static com.blueseer.inv.invData.getInvMaintInit_min;
import static com.blueseer.inv.invData.getInventoryQtyByItem;
import static com.blueseer.inv.invData.getItemBrowseView;
import static com.blueseer.inv.invData.getItemCostElements;
import static com.blueseer.inv.invData.getItemDataInit;
import static com.blueseer.inv.invData.getItemImagesFile;
import static com.blueseer.inv.invData.getItemMaintInit;
import static com.blueseer.inv.invData.getItemMstr;
import static com.blueseer.inv.invData.getItemPrice;
import static com.blueseer.inv.invData.getItemQOHTotal;
import static com.blueseer.inv.invData.getItemQtyByWarehouseAndLocation;
import static com.blueseer.inv.invData.getLocationListByWarehouse;
import static com.blueseer.inv.invData.getLocationMaintInit;
import static com.blueseer.inv.invData.getLocationMstr;
import static com.blueseer.inv.invData.getOrderMaintDetailEvent;
import static com.blueseer.inv.invData.getPLMstr;
import static com.blueseer.inv.invData.getRecentTransByItem;
import static com.blueseer.inv.invData.getRoutingMstr;
import static com.blueseer.inv.invData.getUOMMstr;
import static com.blueseer.inv.invData.getWareHouseMaintInit;
import static com.blueseer.inv.invData.getWareHouseMstr;
import static com.blueseer.inv.invData.getWorkCenterMstr;
import static com.blueseer.inv.invData.rebaseCurrentCost;
import static com.blueseer.inv.invData.resetBOMDefault;
import static com.blueseer.inv.invData.updateCurrentItemCost;
import static com.blueseer.inv.invData.updateItemMstr;
import static com.blueseer.inv.invData.updateLocationMstr;
import static com.blueseer.inv.invData.updatePLMstr;
import static com.blueseer.inv.invData.updateRoutingMstr;
import static com.blueseer.inv.invData.updateUOMMstr;
import static com.blueseer.inv.invData.updateWareHouseMstr;
import static com.blueseer.inv.invData.updateWorkCenterMstr;
import static com.blueseer.ord.ordData.getOrderItemAllocatedQty;
import static com.blueseer.utl.BlueSeerUtils.ArrayListDoubleToJson;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringArrayToJson;
import static com.blueseer.utl.BlueSeerUtils.ArrayListStringToJson;
import static com.blueseer.utl.BlueSeerUtils.HashMapStringIntegerToJson;
import static com.blueseer.utl.BlueSeerUtils.HashMapStringStringToJson;
import static com.blueseer.utl.BlueSeerUtils.arrayToJson;
import static com.blueseer.utl.BlueSeerUtils.bsParseDouble;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuth;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuthAPI;
import static com.blueseer.utl.BlueSeerUtils.doubleToJson;
import static com.blueseer.utl.BlueSeerUtils.intToJson;
import static com.blueseer.utl.OVData.getCodeMstrValueList;
import static com.blueseer.utl.OVData.getNextNbr;
import static com.blueseer.utl.OVData.getSysMetaData;
import static com.blueseer.utl.OVData.getTableInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.tree.DefaultMutableTreeNode;


/**
 *
 * @author terryva
 */
public class dataServINV extends HttpServlet {
 
    
        
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
        
        case "addItemMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.item_mstr x = objectMapper.readValue(sb.toString(), invData.item_mstr.class);            
            response.getWriter().print(arrayToJson(addItemMstr(x)));
            break;
          }
        
        case "updateItemMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.item_mstr x = objectMapper.readValue(sb.toString(), invData.item_mstr.class);            
            response.getWriter().print(arrayToJson(updateItemMstr(x)));
            break;
          }
        
        case "deleteItemMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.item_mstr x = objectMapper.readValue(sb.toString(), invData.item_mstr.class);            
            response.getWriter().print(arrayToJson(deleteItemMstr(x)));
            break;
          }
        
        case "getItemMstr" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            invData.item_mstr x = getItemMstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "addPLMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.pl_mstr x = objectMapper.readValue(sb.toString(), invData.pl_mstr.class);            
            response.getWriter().print(arrayToJson(addPLMstr(x)));
            break;
          }
        
        case "updatePLMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.pl_mstr x = objectMapper.readValue(sb.toString(), invData.pl_mstr.class);            
            response.getWriter().print(arrayToJson(updatePLMstr(x)));
            break;
          }
        
        case "deletePLMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.pl_mstr x = objectMapper.readValue(sb.toString(), invData.pl_mstr.class);            
            response.getWriter().print(arrayToJson(deletePLMstr(x)));
            break;
          }
        
        case "getPLMstr" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            invData.pl_mstr x = getPLMstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "addWareHouseMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.wh_mstr x = objectMapper.readValue(sb.toString(), invData.wh_mstr.class);            
            response.getWriter().print(arrayToJson(addWareHouseMstr(x)));
            break;
          }
        
        case "updateWareHouseMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.wh_mstr x = objectMapper.readValue(sb.toString(), invData.wh_mstr.class);            
            response.getWriter().print(arrayToJson(updateWareHouseMstr(x)));
            break;
          }
        
        case "deleteWareHouseMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.wh_mstr x = objectMapper.readValue(sb.toString(), invData.wh_mstr.class);            
            response.getWriter().print(arrayToJson(deleteWareHouseMstr(x)));
            break;
          }
        
        case "getWareHouseMstr" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            invData.wh_mstr x = getWareHouseMstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "addLocationMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.loc_mstr x = objectMapper.readValue(sb.toString(), invData.loc_mstr.class);            
            response.getWriter().print(arrayToJson(addLocationMstr(x)));
            break;
          }
        
        case "updateLocationMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.loc_mstr x = objectMapper.readValue(sb.toString(), invData.loc_mstr.class);            
            response.getWriter().print(arrayToJson(updateLocationMstr(x)));
            break;
          }
        
        case "deleteLocationMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.loc_mstr x = objectMapper.readValue(sb.toString(), invData.loc_mstr.class);            
            response.getWriter().print(arrayToJson(deleteLocationMstr(x)));
            break;
          }
        
        case "getLocationMstr" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            invData.loc_mstr x = getLocationMstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "addUOMMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.uom_mstr x = objectMapper.readValue(sb.toString(), invData.uom_mstr.class);            
            response.getWriter().print(arrayToJson(addUOMMstr(x)));
            break;
          }
        
        case "updateUOMMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.uom_mstr x = objectMapper.readValue(sb.toString(), invData.uom_mstr.class);            
            response.getWriter().print(arrayToJson(updateUOMMstr(x)));
            break;
          }
        
        case "deleteUOMMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.uom_mstr x = objectMapper.readValue(sb.toString(), invData.uom_mstr.class);            
            response.getWriter().print(arrayToJson(deleteUOMMstr(x)));
            break;
          }
        
        case "getUOMMstr" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            invData.uom_mstr x = getUOMMstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "addRoutingMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.wf_mstr x = objectMapper.readValue(sb.toString(), invData.wf_mstr.class);            
            response.getWriter().print(arrayToJson(addRoutingMstr(x)));
            break;
          }
        
        case "updateRoutingMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.wf_mstr x = objectMapper.readValue(sb.toString(), invData.wf_mstr.class);            
            response.getWriter().print(arrayToJson(updateRoutingMstr(x)));
            break;
          }
        
        case "deleteRoutingMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.wf_mstr x = objectMapper.readValue(sb.toString(), invData.wf_mstr.class);            
            response.getWriter().print(arrayToJson(deleteRoutingMstr(x)));
            break;
          }
        
        case "getRoutingMstr" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            invData.wf_mstr x = getRoutingMstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        case "addWorkCenterMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.wc_mstr x = objectMapper.readValue(sb.toString(), invData.wc_mstr.class);            
            response.getWriter().print(arrayToJson(addWorkCenterMstr(x)));
            break;
          }
        
        case "updateWorkCenterMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.wc_mstr x = objectMapper.readValue(sb.toString(), invData.wc_mstr.class);            
            response.getWriter().print(arrayToJson(updateWorkCenterMstr(x)));
            break;
          }
        
        case "deleteWorkCenterMstr" : { 
            String line;
            StringBuilder sb = new StringBuilder();  
            BufferedReader reader = request.getReader();  // as string
            while ((line = reader.readLine()) != null) {  
            sb.append(line);
            } 
            ObjectMapper objectMapper = new ObjectMapper();
            invData.wc_mstr x = objectMapper.readValue(sb.toString(), invData.wc_mstr.class);            
            response.getWriter().print(arrayToJson(deleteWorkCenterMstr(x)));
            break;
          }
        
        case "getWorkCenterMstr" : { 
            String[] key = new String[]{request.getHeader("param1")}; 
            invData.wc_mstr x = getWorkCenterMstr(key);
            ObjectMapper objectMapper = new ObjectMapper();
            String r = objectMapper.writeValueAsString(x);
            response.getWriter().print(r);
            break;
          }
        
        
        
        case "getBOMsByItemSite_mg" : {       
            response.getWriter().print(ArrayListStringArrayToJson(getBOMsByItemSite_mg(request.getHeader("param1"))));
            break;
        }
            
        case "getLocationListByWarehouse" : {       
            response.getWriter().print(ArrayListStringToJson(getLocationListByWarehouse(request.getHeader("param1"))));
            break;    
        }
            
        case "getSysMetaData" :   {     
            response.getWriter().print(ArrayListStringToJson(getSysMetaData(request.getHeader("param1"),request.getHeader("param2"),request.getHeader("param3"))));
            break;    
        }
            
        case "getNextNbr" : {
            response.getWriter().print(intToJson(getNextNbr(request.getHeader("param1")))); 
            break;
        }
            
        case "getItemQtyByWarehouseAndLocation" : {
            response.getWriter().print(doubleToJson(getItemQtyByWarehouseAndLocation(request.getHeader("param1"),
                    request.getHeader("param2"),
                    request.getHeader("param3"),
                    request.getHeader("param4"))));  
            break;
        }
            
        case "getItemQOHTotal" : {
            response.getWriter().print(doubleToJson(getItemQOHTotal(request.getHeader("param1"),
                    request.getHeader("param2"))));  
            break; 
        }
            
        case "getOrderItemAllocatedQty" : {
            response.getWriter().print(doubleToJson(getOrderItemAllocatedQty(request.getHeader("param1"),
                    request.getHeader("param2"))));  
            break;    
        }
            
        case "getTableInfo" : {
            response.getWriter().print(HashMapStringIntegerToJson(getTableInfo(new String[]{request.getHeader("param1")})));
            break; 
        }
           
        case "getItemMaintInit" : { 
            response.getWriter().print(ArrayListStringArrayToJson(getItemMaintInit(request.getHeader("param1"), request.getHeader("param2"))));
            break;  
        }
        
        case "getLocationMaintInit" : { 
            response.getWriter().print(ArrayListStringArrayToJson(getLocationMaintInit(request.getHeader("param1"), request.getHeader("param2"))));
            break;  
        }
        
        case "getWareHouseMaintInit" : { 
            response.getWriter().print(ArrayListStringArrayToJson(getWareHouseMaintInit(request.getHeader("param1"), request.getHeader("param2"))));
            break;  
        }
        
        case "getInvMaintInit" : { 
            response.getWriter().print(ArrayListStringArrayToJson(getInvMaintInit(request.getHeader("param1"), request.getHeader("param2"))));
            break;  
        }
        
        case "getInvMaintInit_min" : { 
            response.getWriter().print(ArrayListStringArrayToJson(getInvMaintInit_min(request.getHeader("param1"), request.getHeader("param2"))));
            break;  
        }
        
        case "getItemBrowseView" : {
        String[] it = new String[]{
               request.getHeader("fromitem"), 
               request.getHeader("toitem"), 
               request.getHeader("fromclass"), 
               request.getHeader("toclass"), 
               request.getHeader("site")
               };     
        response.getWriter().print(getItemBrowseView(it));  
        break;
        }
        
        case "getItemDataInit" : {
            response.getWriter().print(HashMapStringStringToJson(getItemDataInit(
                request.getHeader("param1"),
                request.getHeader("param2"),
                request.getHeader("param3"),
                request.getHeader("param4"))));
            break;  
        }
            
        case "getItemPrice" : {
            response.getWriter().print(arrayToJson(getItemPrice(
                request.getHeader("param1"),
                request.getHeader("param2"),
                request.getHeader("param3"),
                request.getHeader("param4"),
                request.getHeader("param5"),
                request.getHeader("param6"))));
            break;
        }
            
        case "getOrderMaintDetailEvent" : {
            response.getWriter().print(arrayToJson(getOrderMaintDetailEvent(
                request.getHeader("param1"),
                request.getHeader("param2"),
                request.getHeader("param3"),
                request.getHeader("param4"),
                request.getHeader("param5"),
                request.getHeader("param6"))));
            break;    
        }
        
        case "getCurrentCost" :   {     
            response.getWriter().print(ArrayListDoubleToJson(getCurrentCost(request.getHeader("param1"))));
            break;    
        }
        
        case "getItemCostElements" :   {     
            response.getWriter().print(ArrayListDoubleToJson(getItemCostElements(request.getHeader("param1"), request.getHeader("param2"), request.getHeader("param3"))));
            break;    
        }
        
        case "getItemImagesFile" : { 
            response.getWriter().print(ArrayListStringToJson(getItemImagesFile(request.getHeader("param1"))));
            break;  
        }
          
        case "getRecentTransByItem" : { 
            response.getWriter().print(ArrayListStringArrayToJson(getRecentTransByItem(request.getHeader("param1"))));
            break;  
        }
        
        case "getInventoryQtyByItem" : { 
            response.getWriter().print(ArrayListStringArrayToJson(getInventoryQtyByItem(request.getHeader("param1"))));
            break;  
        }
        
        case "bind_tree_op" : { 
            DefaultMutableTreeNode x = bind_tree_op(request.getHeader("param1"));
            ObjectMapper objectMapper = new ObjectMapper(); 
            String r = "";
            if (x != null && x.getChildCount() > 0) {
            r = objectMapper.writeValueAsString(x);
            } 
            response.getWriter().print(r);
            break;
          }
        
        case "rebaseCurrentCost" : { 
            rebaseCurrentCost(request.getHeader("param1"),
                    bsParseDouble(request.getHeader("param2")),
                    bsParseDouble(request.getHeader("param3")),
                    bsParseDouble(request.getHeader("param4")));
            break;  
        }
        
        case "updateCurrentItemCost" : { 
            updateCurrentItemCost(request.getHeader("param1"));
            break;  
        }
        
        case "resetBOMDefault" : { 
            resetBOMDefault(request.getHeader("param1"));
            break;  
        }
        
        
        
        default:
        response.getWriter().print("no switch case exists in dataServINV for id: " + id);
        System.out.println("no switch case exists in dataServINV for id: " + id);    
            
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
