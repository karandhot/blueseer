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

import static com.blueseer.utl.BlueSeerUtils.confirmServerAuth;
import static com.blueseer.utl.BlueSeerUtils.confirmServerAuthAPI;
import com.blueseer.utl.DTData;
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
public class dataServDT extends HttpServlet {
 
    
        
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
      
        
    
    
        
    if (request.getHeader("id") == null || request.getHeader("id").isEmpty()) {
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().println(HttpServletResponse.SC_BAD_REQUEST + ": missing id " + "\n" + getHeaders(request) );  
      return;
    }

    String id = request.getHeader("id");
    
    
    if (! confirmServerAuthAPI(request, authServ.hmuser)) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().println(" br549finpost authorization failed");
        return;
    }
       
    // all methods called in DTData must have a counterpart condition instance below
    
    switch (id) {
        case "getAcctBrowseUtilData" :
            response.getWriter().print(DTData.getAcctBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;
            
        case "getBankBrowseUtilData" :
            response.getWriter().print(DTData.getBankBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;
                
        case "getDeptCCBrowseUtilData" :
            response.getWriter().print(DTData.getDeptCCBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;            
        
        case "getCurrencyBrowseUtilData" :
            response.getWriter().print(DTData.getCurrencyBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;
        
        case "getGLTranBrowseUtilData" :
            response.getWriter().print(DTData.getGLTranBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;
        
case "getGLTranBrowseUtil2Data" :
            response.getWriter().print(DTData.getGLTranBrowseUtil2Data(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;
        
case "getItemBrowseUtilData" :
            response.getWriter().print(DTData.getItemBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getItemMClassBrowseUtilData" :
            response.getWriter().print(DTData.getItemMClassBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getBomBrowseUtilData" :
            response.getWriter().print(DTData.getBomBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3"), request.getHeader("param4"), request.getHeader("param5"))); 
            break; 

case "getVendBrowseUtilData" :
            response.getWriter().print(DTData.getVendBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getPOAddrBrowseUtilData" :
            response.getWriter().print(DTData.getPOAddrBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getMapBrowseUtilData" :
            response.getWriter().print(DTData.getMapBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3"), request.getHeader("param4")));
            break;

case "getMapStructBrowseData" :
            response.getWriter().print(DTData.getMapStructBrowseData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getCronBrowseUtilData" :
            response.getWriter().print(DTData.getCronBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getLabelFileUtilData" :
            response.getWriter().print(DTData.getLabelFileUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getPanelBrowseUtilData" :
            response.getWriter().print(DTData.getPanelBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getKeyBrowseUtilData" :
            response.getWriter().print(DTData.getKeyBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getPksBrowseUtilData" :
            response.getWriter().print(DTData.getPksBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getSiteBrowseUtilData" :
            response.getWriter().print(DTData.getSiteBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getCustBrowseUtilData" :
            response.getWriter().print(DTData.getCustBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getRoutingBrowseUtilData" :
            response.getWriter().print(DTData.getRoutingBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getWorkCenterBrowseUtilData" :
            response.getWriter().print(DTData.getWorkCenterBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getShiftBrowseUtilData" :
            response.getWriter().print(DTData.getShiftBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getUOMBrowseUtilData" :
            response.getWriter().print(DTData.getUOMBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getJobBrowseUtilData" :
            response.getWriter().print(DTData.getJobBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getJobSRVCBrowseUtilData" :
            response.getWriter().print(DTData.getJobSRVCBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getQPRBrowseUtilData" :
            response.getWriter().print(DTData.getQPRBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getPrinterBrowseUtilData" :
            response.getWriter().print(DTData.getPrinterBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getExchangeBrowseUtilData" :
            response.getWriter().print(DTData.getExchangeBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getUOMConvBrowseUtilData" :
            response.getWriter().print(DTData.getUOMConvBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getECNBrowseUtilData" :
            response.getWriter().print(DTData.getECNBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getTaskBrowseUtilData" :
            response.getWriter().print(DTData.getTaskBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getAPIBrowseUtilData" :
            response.getWriter().print(DTData.getAPIBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getAS2BrowseUtilData" :
            response.getWriter().print(DTData.getAS2BrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getFreightBrowseUtilData" :
            response.getWriter().print(DTData.getFreightBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getVehicleBrowseUtilData" :
            response.getWriter().print(DTData.getVehicleBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getDriverBrowseUtilData" :
            response.getWriter().print(DTData.getDriverBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getBrokerBrowseUtilData" :
            response.getWriter().print(DTData.getBrokerBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getWkfMstrBrowseUtilData" :
            response.getWriter().print(DTData.getWkfMstrBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getTaxBrowseUtilData" :
            response.getWriter().print(DTData.getTaxBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getDocRulesBrowseUtilData" :
            response.getWriter().print(DTData.getDocRulesBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getGenCodeBrowseUtilData" :
            response.getWriter().print(DTData.getGenCodeBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getFreightCodeBrowseUtilData" :
            response.getWriter().print(DTData.getFreightCodeBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getEDIXrefBrowseUtilData" :
            response.getWriter().print(DTData.getEDIXrefBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getJaspRptBrowseUtilData" :
            response.getWriter().print(DTData.getJaspRptBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getFctMstrBrowseUtilData" :
            response.getWriter().print(DTData.getFctMstrBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getCustXrefBrowseUtilData" :
            response.getWriter().print(DTData.getCustXrefBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getVendXrefBrowseUtilData" :
            response.getWriter().print(DTData.getVendXrefBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getVendPriceBrowseUtilData" :
            response.getWriter().print(DTData.getVendPriceBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getCustPriceBrowseUtilData" :
            response.getWriter().print(DTData.getCustPriceBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getPayProfileBrowseUtilData" :
            response.getWriter().print(DTData.getPayProfileBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getPayRollBrowseUtilData" :
            response.getWriter().print(DTData.getPayRollBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getEDITPBrowseUtilData" :
            response.getWriter().print(DTData.getEDITPBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getEDITPDOCBrowseUtilData" :
            response.getWriter().print(DTData.getEDITPDOCBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getTermBrowseUtilData" :
            response.getWriter().print(DTData.getTermBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getOrderBrowseUtilData" :
            response.getWriter().print(DTData.getOrderBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getOpenOrderBrowseUtilData" :
            response.getWriter().print(DTData.getOpenOrderBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getPOBrowseUtilData" :
            response.getWriter().print(DTData.getPOBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getDOBrowseUtilData" :
            response.getWriter().print(DTData.getDOBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getSVBrowseUtilData" :
            response.getWriter().print(DTData.getSVBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getFOBrowseUtilData" :
            response.getWriter().print(DTData.getFOBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getCFOBrowseUtilData" :
            response.getWriter().print(DTData.getCFOBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getShipperBrowseUtilData" :
            response.getWriter().print(DTData.getShipperBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getInvoiceBrowseUtilData" :
            response.getWriter().print(DTData.getInvoiceBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getProdCodeBrowseUtilData" :
            response.getWriter().print(DTData.getProdCodeBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getLocationBrowseUtilData" :
            response.getWriter().print(DTData.getLocationBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getWareHouseBrowseUtilData" :
            response.getWriter().print(DTData.getWareHouseBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getQuoteBrowseUtilData" :
            response.getWriter().print(DTData.getQuoteBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getBillingBrowseUtilData" :
            response.getWriter().print(DTData.getBillingBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getVoucherBrowseUtilData" :
            response.getWriter().print(DTData.getVoucherBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getARPaymentBrowseUtilData" :
            response.getWriter().print(DTData.getARPaymentBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getARMemoBrowseUtilData" :
            response.getWriter().print(DTData.getARMemoBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getExpenseBrowseUtilData" :
            response.getWriter().print(DTData.getExpenseBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getReceiverBrowseUtilData" :
            response.getWriter().print(DTData.getReceiverBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getCalendarBrowseUtilData" :
            response.getWriter().print(DTData.getCalendarBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getEmpBrowseUtilData" :
            response.getWriter().print(DTData.getEmpBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getJobClockUtilData" :
            response.getWriter().print(DTData.getJobClockUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getClockRecBrowseUtilData" :
            response.getWriter().print(DTData.getClockRecBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getUserBrowseUtilData" :
            response.getWriter().print(DTData.getUserBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getMenuBrowseUtilData" :
            response.getWriter().print(DTData.getMenuBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getReqBrowseUtilData" :
            response.getWriter().print(DTData.getReqBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getGLHistBrowseUtilData" :
            response.getWriter().print(DTData.getGLHistBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3")));
            break;

case "getVendShipToBrowseUtilData" :
            response.getWriter().print(DTData.getVendShipToBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3"), request.getHeader("param4")));
            break;

case "getShipToBrowseUtilData" :
            response.getWriter().print(DTData.getShipToBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3"), request.getHeader("param4")));
            break;

case "getEDIPartnerBrowseUtilData" :
            response.getWriter().print(DTData.getEDIPartnerBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3"), request.getHeader("param4")));
            break;


case "getGenCodeBrowseUtilByCodeData" :
            response.getWriter().print(DTData.getGenCodeBrowseUtilByCodeData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3"), request.getHeader("param4")));
            break;


case "getFreightCodeBrowseUtilByCodeData" :
            response.getWriter().print(DTData.getFreightCodeBrowseUtilByCodeData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3"), request.getHeader("param4")));
            break;


case "getCustXrefBrowseUtil2Data" :
            response.getWriter().print(DTData.getCustXrefBrowseUtil2Data(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3"), request.getHeader("param4")));
            break;


case "getVendXrefBrowseUtil2Data" :
            response.getWriter().print(DTData.getVendXrefBrowseUtil2Data(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3"), request.getHeader("param4")));
            break;


case "getFTPBrowseUtilData" :
            response.getWriter().print(DTData.getFTPBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3"), request.getHeader("param4")));
            break;


case "getOrderDetailBrowseUtilData" :
            response.getWriter().print(DTData.getOrderDetailBrowseUtilData(request.getHeader("param1"), request.getHeader("param2"), request.getHeader("param3"), request.getHeader("param4")));
            break;


case "getOrderLineBrowseUtilData" :
            response.getWriter().print(DTData.getOrderLineBrowseUtilData(request.getHeader("param1"), request.getHeader("param2"), request.getHeader("param3"))); 
            break;


case "getEDICustBrowseUtilData" :
            response.getWriter().print(DTData.getEDICustBrowseUtilData(request.getHeader("param1"), Integer.parseInt(request.getHeader("param2")), request.getHeader("param3"), request.getHeader("param4")));
            break;


case "getItemDescBrowseData" :
            response.getWriter().print(DTData.getItemDescBrowseData(request.getHeader("param1"), request.getHeader("param2")));
            break;


case "getItemDescBrowseBySiteData" :
            response.getWriter().print(DTData.getItemDescBrowseBySiteData(request.getHeader("param1"), request.getHeader("param2"), request.getHeader("param3")));
            break;


case "getItemDescBrowseBySite2Data" :
            response.getWriter().print(DTData.getItemDescBrowseBySite2Data(request.getHeader("param1"), request.getHeader("param2"), request.getHeader("param3"), request.getHeader("param4")));
            break;


case "getFreightOrderQuotesTableData" :
            response.getWriter().print(DTData.getFreightOrderQuotesTableData(request.getHeader("param1")));
            break;

case "getFreightOrderTendersTableData" :
            response.getWriter().print(DTData.getFreightOrderTendersTableData(request.getHeader("param1")));
            break;

case "getFreightOrderStatusTableData" :
            response.getWriter().print(DTData.getFreightOrderStatusTableData(request.getHeader("param1")));
            break;

case "getPayRollHoursData" :
            response.getWriter().print(DTData.getPayRollHoursData(request.getHeader("param1"), request.getHeader("param2")));
            break;

case "getForecast13weeksData" :
            response.getWriter().print(DTData.getForecast13weeksData(Integer.parseInt(request.getHeader("param1"))));
            break;

case "getForecast4weeksAndSecData" :
            response.getWriter().print(DTData.getForecast4weeksAndSecData(Integer.parseInt(request.getHeader("param1"))));
            break;

case "getForecast13weeksByPartData" :
            response.getWriter().print(DTData.getForecast13weeksByPartData(request.getHeader("param1"), request.getHeader("param2"), Integer.parseInt(request.getHeader("param3"))));
            break;

case "getEDITPAllData" :
            response.getWriter().print(DTData.getEDITPAllData(request.getHeader("param1")));
            break;

case "getEDITPDOCAllData" :
            response.getWriter().print(DTData.getEDITPDOCAllData(request.getHeader("param1")));
            break;

case "getEDIXrefAllData" :
            response.getWriter().print(DTData.getEDIXrefAllData(request.getHeader("param1")));
            break;

case "getEDIPartnerDocAllData" :
            response.getWriter().print(DTData.getEDIPartnerDocAllData(request.getHeader("param1")));
            break;

case "getReqByApproverData" :
            response.getWriter().print(DTData.getReqByApproverData(request.getHeader("param1")));
            break;

case "getPrintersAllData" :
            response.getWriter().print(DTData.getPrintersAllData());
            break;

case "getLabelFileAllData" :
            response.getWriter().print(DTData.getLabelFileAllData());
            break;

case "getForecastAllData" :
            response.getWriter().print(DTData.getForecastAllData());
            break;


case "getPlantDirectoryData" :
            response.getWriter().print(DTData.getPlantDirectoryData());
            break;


case "getNavCodeListData" :
            response.getWriter().print(DTData.getNavCodeListData());
            break;


case "getReqAllData" :
            response.getWriter().print(DTData.getReqAllData());
            break;


case "getGLAcctAllData" :
            response.getWriter().print(DTData.getGLAcctAllData());
            break;


case "getItemRoutingAllData" :
            response.getWriter().print(DTData.getItemRoutingAllData());
            break;


case "getItemBrowseData" :
            response.getWriter().print(DTData.getItemBrowseData());
            break;


case "getEmployeeAllData" :
            response.getWriter().print(DTData.getEmployeeAllData());
            break;


case "getGenCodeAllData" :
            response.getWriter().print(DTData.getGenCodeAllData());
            break;


case "getFreightCodeAllData" :
            response.getWriter().print(DTData.getFreightCodeAllData());
            break;


case "getWorkCellAllData" :
            response.getWriter().print(DTData.getWorkCellAllData());
            break;


case "getReqPendingData" :
            response.getWriter().print(DTData.getReqPendingData());
            break;


case "getReqApprovedData" :
            response.getWriter().print(DTData.getReqApprovedData());
            break;


case "getUserAllData" :
            response.getWriter().print(DTData.getUserAllData());
            break;


case "getProdCodeAllData" :
            response.getWriter().print(DTData.getProdCodeAllData());
            break;


case "getQPRAllData" :
            response.getWriter().print(DTData.getQPRAllData());
            break;


case "getShipperAllData" :
            response.getWriter().print(DTData.getShipperAllData());
            break;


case "getOrderOpenData" :
            response.getWriter().print(DTData.getOrderOpenData());
            break;


case "getDBSchemaData" :
            response.getWriter().print(DTData.getDBSchemaData());
            break;


case "getRoutingsAllData" :
            response.getWriter().print(DTData.getRoutingsAllData());
            break;


case "getLocationsAllData" :
            response.getWriter().print(DTData.getLocationsAllData());
            break;


case "getWareHousesAllData" :
            response.getWriter().print(DTData.getWareHousesAllData());
            break;


case "getDeptsAllData" :
            response.getWriter().print(DTData.getDeptsAllData());
            break;


case "getBankAllData" :
            response.getWriter().print(DTData.getBankAllData());
            break;


case "getUnPostedGLTransData" :
            response.getWriter().print(DTData.getUnPostedGLTransData());
            break;


case "getARPaymentBrowseData" :
            response.getWriter().print(DTData.getARPaymentBrowseData());
            break;

case "getMenusAllData" :
            response.getWriter().print(DTData.getMenusAllData());
            break;

case "getPanelsAllData" :
            response.getWriter().print(DTData.getPanelsAllData());
            break;

case "getTermsAllData" :
            response.getWriter().print(DTData.getTermsAllData());
            break;

case "getWorkFlowAllData" :
            response.getWriter().print(DTData.getWorkFlowAllData());
            break;

case "getAS2AllData" :
            response.getWriter().print(DTData.getAS2AllData());
            break;

case "getCronAllData" :
            response.getWriter().print(DTData.getCronAllData());
            break;

case "getPKSAllData" :
            response.getWriter().print(DTData.getPKSAllData());
            break;

case "getFreightAllData" :
            response.getWriter().print(DTData.getFreightAllData());
            break;

case "getCarrierAllData" :
            response.getWriter().print(DTData.getCarrierAllData());
            break;

case "getVehicleAllData" :
            response.getWriter().print(DTData.getVehicleAllData());
            break;

case "getDriverAllData" :
            response.getWriter().print(DTData.getDriverAllData());
            break;

case "getBrokerAllData" :
            response.getWriter().print(DTData.getBrokerAllData());
            break;

case "getTaxAllData" :
            response.getWriter().print(DTData.getTaxAllData());
            break;

case "getPayProfileAllData" :
            response.getWriter().print(DTData.getPayProfileAllData());
            break;

case "getSitesAllData" :
            response.getWriter().print(DTData.getSitesAllData());
            break;

case "getGLCalendarData" :
            response.getWriter().print(DTData.getGLCalendarData());
            break;

case "getNoStdCostItemsData" :
            response.getWriter().print(DTData.getNoStdCostItemsData());
            break;

case "getCustAddrInfoAllData" :
            response.getWriter().print(DTData.getCustAddrInfoAllData());
            break;

case "getVendorAllData" :
            response.getWriter().print(DTData.getVendorAllData());
            break;

case "getShiftAllData" :
            response.getWriter().print(DTData.getShiftAllData());
            break;

case "getClockCodesAllData" :
            response.getWriter().print(DTData.getClockCodesAllData());
            break;

case "getClockRecords66AllData" :
            response.getWriter().print(DTData.getClockRecords66AllData());
            break;

case "getQOHvsSSAllData" :
            response.getWriter().print(DTData.getQOHvsSSAllData());
            break;

case "getItemInfoAllData" :
            response.getWriter().print(DTData.getItemInfoAllData());
            break;

case "getFreightRejectionCodeDTData" :
            response.getWriter().print(DTData.getFreightRejectionCodeDTData());
            break;
            
        default:
        response.getWriter().print("");
        System.out.println("error no switch case exists in dataServDT for id: " + id);
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
