import com.blueseer.shp.shpData;
import com.blueseer.ctr.cusData;
import com.blueseer.adm.admData;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;

     String doctype = c[1];
     String key = doc.get(0).toString();
//GlobalDebug = true;    
        
    String[] h = shpData.getShipperHeader(key);  // 13 elements...see declaration
    String po = h[3];
    
     /* Begin Mapping Segments */ 
mapSegment("invoice","invoiceid",key);
mapSegment("invoice","ponumber",h[3]);
mapSegment("invoice","podate",h[4]);
mapSegment("invoice","vendorcode",getMeta(po,"header","vendcode"));
mapSegment("invoice","site",h[28]);
mapSegment("invoice","invoicedate",h[34]);
mapSegment("invoice","shipdate",h[34]);
mapSegment("invoice","remarks",h[6]);
mapSegment("invoice","reference",h[7]);
mapSegment("invoice","doctype","bs-json-810");
mapSegment("invoice","invoicetype","DR");
mapSegment("invoice","senderid",c[0]);
mapSegment("invoice","receiverid",c[21]);
mapSegment("invoice","currency",h[13]);
mapSegment("invoice","shipvia",h[8]);
mapSegment("invoice","transportmethod","");
mapSegment("invoice","servicetype","");
mapSegment("invoice","status","");
mapSegment("invoice","trackingnumber",h[11]);
mapSegment("invoice","termscode",h[29]);
mapSegment("invoice","termsdiscpct",h[31]);
mapSegment("invoice","termsdescription",h[30]);
mapSegment("invoice","termsnetduedate",h[33]);
commitSegment("invoice");
    
mapSegment("references:reference","qualifier","vendcode");
if (! getMeta(po,"header","vendcode").isBlank()) {
mapSegment("references:reference","value",getMeta(po,"header","vendcode"));
} else {
mapSegment("references:reference","value",c[0]);
}
commitSegment("references:reference");

ArrayList<String[]> reflist = getMetaAll(po,"reference");
for (String[] s : reflist) {
mapSegment("references:reference","qualifier",s[0]);
mapSegment("references:reference","value",s[1]);
commitSegment("references:reference");
}


String[] bt = cusData.getCustAddressInfo(h[0]);
mapSegment("addresses:address","type","BT"); 
mapSegment("addresses:address","addrid",getMeta(po,"header","billcode"));    
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

String[] st = cusData.getShipAddressInfo(h[0],h[1]);
mapSegment("addresses:address","type","ST"); 
mapSegment("addresses:address","addrid",getMeta(po,"header","shipcode"));    
mapSegment("addresses:address","name",st[1]);
mapSegment("addresses:address","line1",st[2]);
mapSegment("addresses:address","line2",st[3]);
mapSegment("addresses:address","line3",st[4]);
mapSegment("addresses:address","city",st[5]);
mapSegment("addresses:address","state",st[6]);
mapSegment("addresses:address","zip",st[7]);
mapSegment("addresses:address","country",st[8]);
commitSegment("addresses:address");

String[] re = admData.getSiteAddressInfo(h[12]);
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
         String sku = "";
         String detline = "";
         // item, custitem, qty, po, cumqty, listprice, netprice, reference, sku, desc
         ArrayList<String[]> lines = shpData.getShipperLines(key);
              for (String[] d : lines) {
                  i++;
                  detline = "detail:" + i; // getMeta usage
                  if (d[8].isEmpty() && d[8] != null) {
                      sku = cusData.getCustAltItem(h[0], d[0]);
                  }
                 
                mapSegment("items:item","itemnumber",d[0]);
                mapSegment("items:item","line",String.valueOf(i));
                mapSegment("items:item","invoicequantity",d[2]);
                mapSegment("items:item","uom",d[12]);
                mapSegment("items:item","description",d[9]);
                mapSegment("items:item","listprice",formatNumber(BlueSeerUtils.bsParseDouble(d[5]),"4"));
                mapSegment("items:item","netprice",formatNumber(BlueSeerUtils.bsParseDouble(d[5]),"4"));
                mapSegment("items:item","skunumber",sku);
                mapSegment("items:item","upcnumber",getMeta(po,detline,"upc"));
                
                commitSegment("items:item");
                  
              }
  
// summary info
//mapSegment("summarysacs:sac","sactype","A");
//mapSegment("summarysacs:sac","saccode","");
//mapSegment("summarysacs:sac","sacamt","0");
//mapSegment("summarysacs:sac","sacdesc","");      
//commitSegment("summarysacs:sac");  

