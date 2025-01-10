import com.blueseer.ctr.cusData;
import com.blueseer.adm.admData;
import com.blueseer.edi.EDI;
import static com.blueseer.ord.ordData.getSOMstrHeaderEDI;
import static com.blueseer.ord.ordData.getSOMstrdetailsEDI;
import com.blueseer.shp.shpData;
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.OVData;


     
     String doctype = c[1];
     String key = doc.get(0).toString();
    
        
        
    String[] h = getSOMstrHeaderEDI(key);  // 13 elements...see declaration 
    // so, po, cust, ship, site, type, orddate, duedate, shipvia, rmks, cur, status
    
    if (h[0] == null || h[0].isEmpty()) {
    setError("Cannot find order number:" + key);
    return error; 
    }

mapSegment("order","orderid",key);
mapSegment("order","po",h[1]);
mapSegment("order","orderdate",h[6]);
mapSegment("order","vendorcode","");
mapSegment("order","site",h[4]);
mapSegment("order","duedate",h[7]);
mapSegment("order","remarks",h[9]);
mapSegment("order","reference","");
mapSegment("order","doctype","bs-json-855");
mapSegment("order","ordertype","");
mapSegment("order","senderid",c[0]);
mapSegment("order","receiverid",c[21]);
mapSegment("order","currency",h[10]);
mapSegment("order","shipvia",h[8]);
mapSegment("order","transportmethod","");
mapSegment("order","status",h[11]);
mapSegment("order","vendcode",getMeta(h[1],"header","vendcode"));
mapSegment("order","isanumber","");
mapSegment("order","gsnumber","");
mapSegment("order","transdatetime","");
commitSegment("order");


mapSegment("references:reference","qualifier","vendcode");
if (! getMeta(h[1],"header","vendcode").isBlank()) {
mapSegment("references:reference","value",getMeta(h[1],"header","vendcode"));
} else {
mapSegment("references:reference","value",c[0]);
}
commitSegment("references:reference");

mapSegment("references:reference","qualifier","originalduedate");
mapSegment("references:reference","value",getMeta(h[1],"header","duedate"));
commitSegment("references:reference");

String[] bt = cusData.getCustAddressInfo(h[2]);
mapSegment("addresses:address","type","BT"); 
mapSegment("addresses:address","addrid",getMeta(h[1],"header","billcode"));    
mapSegment("addresses:address","name",bt[0]);
mapSegment("addresses:address","line1",bt[1]);
mapSegment("addresses:address","line2",bt[2]);
mapSegment("addresses:address","line3",bt[3]);
mapSegment("addresses:address","city",bt[4]);
mapSegment("addresses:address","state",bt[5]);
mapSegment("addresses:address","zip",bt[6]);
mapSegment("addresses:address","country",bt[7]);
mapSegment("addresses:address","email",bt[8]);
commitSegment("addresses:address");

String[] st = cusData.getShipAddressInfo(h[2],h[3]);
mapSegment("addresses:address","type","ST"); 
mapSegment("addresses:address","addrid",getMeta(h[1],"header","shipcode"));    
mapSegment("addresses:address","name",st[1]);
mapSegment("addresses:address","line1",st[2]);
mapSegment("addresses:address","line2",st[3]);
mapSegment("addresses:address","line3",st[4]);
mapSegment("addresses:address","city",st[5]);
mapSegment("addresses:address","state",st[6]);
mapSegment("addresses:address","zip",st[7]);
mapSegment("addresses:address","country",st[8]);
commitSegment("addresses:address");

String[] re = admData.getSiteAddressInfo(h[4]);
mapSegment("addresses:address","type","RE"); 
mapSegment("addresses:address","addrid",re[0]);    
mapSegment("addresses:address","name",re[1]);
mapSegment("addresses:address","line1",st[2]);
mapSegment("addresses:address","line2",st[3]);
mapSegment("addresses:address","line3",st[4]);
mapSegment("addresses:address","city",st[5]);
mapSegment("addresses:address","state",st[6]);
mapSegment("addresses:address","zip",st[7]);
mapSegment("addresses:address","country",st[8]);
commitSegment("addresses:address");
    
  
    
               
        // detail
         int i = 0;
         String detline;
         // line, item, custitem, qty, price, uom, desc, custline, custuom, custprice
         ArrayList<String[]> lines = getSOMstrdetailsEDI(key);
              for (String[] d : lines) {	
                i++;
                // get original info
                detline = "detail:" + i;
                mapSegment("items:item","originalquantity",getMeta(h[1],detline,"qty"));
		mapSegment("items:item","originalprice",formatNumber(BlueSeerUtils.bsParseDouble(getMeta(h[1],detline,"price")),"4"));

                mapSegment("items:item","line",d[7]);
		mapSegment("items:item","itemnumber",d[1]);
                mapSegment("items:item","orderquantity",d[3]);
		
                mapSegment("items:item","uom",d[5]);
                mapSegment("items:item","description",d[6]);
                mapSegment("items:item","listprice",formatNumber(BlueSeerUtils.bsParseDouble(d[4]),"4"));
                mapSegment("items:item","netprice",formatNumber(BlueSeerUtils.bsParseDouble(d[4]),"4"));
                
                mapSegment("items:item","skunumber",d[2]);
                mapSegment("items:item","upcnumber",getMeta(h[1],detline,"upc"));
                commitSegment("items:item");
              }
