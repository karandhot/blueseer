import com.blueseer.shp.shpData;
import com.blueseer.ctr.cusData;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.edi.EDI;

 
    String doctype = c[1];
    String key = doc.get(0).toString();
    EDI.edi810 e = EDI.init810DB(key); 

     /* Begin Mapping Segments */ 
    mapSegment("BIG","e01",e.confdate().replace("-", ""));
    mapSegment("BIG","e02",key);
    mapSegment("BIG","e03",e.confdate().replace("-", ""));
    mapSegment("BIG","e04",e.po());
    commitSegment("BIG");
    
    mapSegment("REF","e01","ST");
    mapSegment("REF","e02",e.bs_shipto());
    commitSegment("REF");

    mapSegment("REF","e01","ON");
    mapSegment("REF","e02",e.so());
    commitSegment("REF");
    
    mapSegment("N1","e01","RE");
    mapSegment("N1","e02",e.site());
    mapSegment("N1","e03","92");
    mapSegment("N1","e04",e.site());
    commitSegment("N1");
    
    mapSegment("DTM","e01","011");
    mapSegment("DTM","e02",e.shipdate().replace("-", ""));
    commitSegment("DTM");    
    
               
        // detail
         int i = 0;
         double sumqty = 0;
         double sumamt = 0;
         double sumlistamt = 0;
         double sumamtTDS = 0;
         String sku = "";

         ArrayList<shpData.ship_det> lines = e.lines();

       
              for (shpData.ship_det d : lines) {
                  i++;
                  if (d.shd_custitem().isEmpty() && d.shd_custitem() != null) {
                      sku = cusData.getCustAltItem(e.bs_billto(), d.shd_item());
                  }

                  if (sku.isEmpty()) {
                   sku = "somevalue";
                  }
                                    
                  sumqty = sumqty + d.shd_qty();
                  sumamt = sumamt + (d.shd_qty() * d.shd_netprice());
                  sumlistamt = sumlistamt + (d.shd_qty() * d.shd_listprice());
                  
                mapSegment("IT1","e01",String.valueOf(i));
                mapSegment("IT1","e02",formatNumber(d.shd_qty(),"0"));
                mapSegment("IT1","e03","EA");
                mapSegment("IT1","e04",formatNumber(d.shd_listprice(),"2"));
                mapSegment("IT1","e06","IN");
                mapSegment("IT1","e07",sku.toUpperCase());
                mapSegment("IT1","e08","VP");
                mapSegment("IT1","e09",d.shd_item().toUpperCase());
                commitSegment("IT1");
                  
              }
            sumamtTDS = (sumamt * 100);
            
            // trailer
         mapSegment("TDS","e01",formatNumber(sumamtTDS,"0"));
         commitSegment("TDS");
         
         mapSegment("ISS","e01",snum(sumqty));
         mapSegment("ISS","e02","EA");
         mapSegment("ISS","e03",snum(sumqty));
         mapSegment("ISS","e04","LB");
         commitSegment("ISS");
         
         mapSegment("CTT","e01",snum(i));
         commitSegment("CTT");
        

