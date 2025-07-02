import com.blueseer.ctr.cusData;
import java.util.ArrayList;
import com.blueseer.edi.EDI;
import com.blueseer.ord.ordData;
import com.blueseer.pur.purData;
import com.blueseer.shp.shpData;
import static com.blueseer.utl.BlueSeerUtils.currformatDouble;
import com.blueseer.utl.OVData;
import java.io.IOException;
import java.text.DecimalFormat;


     String doctype = c[1];
     String key = doc.get(0).toString();
     EDI.edi855 e = EDI.init855DB(key); 
   
    
    if (e == null || e.so_nbr().isEmpty()) {
    setError("Cannot find order number:" + key);
    return error; 
    }
    
     /* Begin Mapping Segments */ 
    String status = "AD";  // accept by default
    String itemstatus = "IA"; // accept by default
    if (e.so_status().equals("rejected")) {
        status = "RJ";
        itemstatus = "IR";  // if one then all
    }
    mapSegment("BAK","e01","00");
    mapSegment("BAK","e02",status);
    mapSegment("BAK","e03",e.so_po());
    mapSegment("BAK","e05",e.so_ord_date().replace("-", ""));
    commitSegment("BAK");
    
    mapSegment("REF","e01","OR");
    mapSegment("REF","e02",e.so_nbr());
    commitSegment("REF");
    
    String[] shipaddr = cusData.getShipAddressInfo(e.so_cust(), e.so_ship());
    mapSegment("N1","e01", "ST");
    mapSegment("N1","e02",shipaddr[1]);
    mapSegment("N1","e03","92");
    mapSegment("N1","e04",shipaddr[0]);
    commitSegment("N1");
    mapSegment("N3","e01",shipaddr[2]);
    commitSegment("N3");
    mapSegment("N4","e01",shipaddr[5]);
    mapSegment("N4","e02",shipaddr[6]);
    mapSegment("N4","e03",shipaddr[7]);
    commitSegment("N4");
    
    mapSegment("N1","e01","VN");
    mapSegment("N1","e02",e.so_site());
    mapSegment("N1","e03","92");
    mapSegment("N1","e04",e.so_site());
    commitSegment("N1");
    
    mapSegment("DTM","e01","067");
    mapSegment("DTM","e02",e.so_due_date().replace("-", ""));
    commitSegment("DTM");    
    
               
        // detail
         int i = 0;
         double sumqty = 0;
         double sumamt = 0;
         // line, item, custitem, qty, price, uom, desc, custline, custuom, custprice
         ArrayList<ordData.sod_det> lines = e.lines();
              for (ordData.sod_det d : lines) {
                  i++;
                                    
                  sumqty = sumqty + d.sod_ord_qty();
                  sumamt = sumamt + (d.sod_ord_qty() * d.sod_netprice());
                  
                mapSegment("PO1","e01",snum(d.sod_line()));
                mapSegment("PO1","e02",formatNumber(d.sod_ord_qty(),"0"));
                mapSegment("PO1","e03",d.sod_uom());
                mapSegment("PO1","e04",currformatDouble(d.sod_netprice()));
                mapSegment("PO1","e06","VN");
                mapSegment("PO1","e07",d.sod_item());
                if (! d.sod_custitem().isEmpty()) {
                mapSegment("PO1","e08","BP");
                mapSegment("PO1","e09",d.sod_custitem());
                }
                commitSegment("PO1");
                
                mapSegment("PID","e01","F");
                mapSegment("PID","e05",d.sod_desc());
                commitSegment("PID");
                
                mapSegment("ACK","e01",itemstatus);
                mapSegment("ACK","e02",formatNumber(d.sod_ord_qty(),"0"));
                mapSegment("ACK","e03",d.sod_uom());
                mapSegment("ACK","e04","076");
                mapSegment("ACK","e05",e.so_due_date().replace("-", ""));
                commitSegment("ACK");
              }
         
         mapSegment("CTT","e01",String.valueOf(i));
         commitSegment("CTT");
        
