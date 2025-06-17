import com.blueseer.shp.shpData;
import com.blueseer.frt.frtData;
import com.blueseer.ctr.cusData;
import com.blueseer.utl.OVData;
import com.blueseer.utl.BlueSeerUtils;

     com.blueseer.edi.EDI edi = new com.blueseer.edi.EDI();
     String doctype = c[1];
     String key = doc.get(0).toString();
    
        
    String[] h = shpData.getShipperHeader(key);  // 13 elements...see declaration
    frtData.cfo_mstr cfo = frtData.getCFOMstr(new String[]{h[2],""});
    frtData.car_mstr car = frtData.getCarrierMstr(new String[]{"internal"});
    String revision = frtData.getCFODefaultRevision(h[2]);
    ArrayList<frtData.cfo_det> cfodet = frtData.getCFODet(h[2],revision);
    double total = shpData.getShipperTotal(key);
    String invoicestatus = shpData.getShipperChar1(key);
   

     /* Begin Mapping Segments */ 
    mapSegment("B3","e02",key);
    mapSegment("B3","e03",h[3]);
    mapSegment("B3","e04","PP");
    mapSegment("B3","e06",h[4].replace("-", ""));
    mapSegment("B3","e07",formatNumber((total * 100),"0"));
    if (invoicestatus.equals("1")) {
    mapSegment("B3","e08","BD");
    }
    if (invoicestatus.equals("2")) {
    mapSegment("B3","e08","CO");
    }
    mapSegment("B3","e11",car.car_scac().isBlank() ? h[12] : car.car_scac());
    commitSegment("B3");
    
    mapSegment("C3","e01",h[13]);
    commitSegment("C3");

    mapSegment("N9","e01","MB");
    mapSegment("N9","e02",key);
    commitSegment("N9");
    
    mapSegment("N1","e01","SH");
    mapSegment("N1","e02",h[16]);
    mapSegment("N1","e03","9");
    mapSegment("N1","e04",h[0]);
    commitSegment("N1");

    mapSegment("N3","e01",h[17]);
    commitSegment("N3");

    mapSegment("N4","e01",h[18]);
    mapSegment("N4","e02",h[19]);
    mapSegment("N4","e03",h[20]);
    mapSegment("N4","e04","US");
    commitSegment("N4");
    
// S5 addresses
int k = 0;
for (frtData.cfo_det cfod : cfodet) {
  k++;
 

    mapSegment("S5","e01",snum(k));
    mapSegment("S5","e02", cfod.cfod_type().equals("Load") ? "LD" : "UL");
    commitSegment("S5");

    mapSegment("G62","e01",cfod.cfod_type().equals("Load") ? "86" : "35");
    mapSegment("G62","e02",cfod.cfod_date().replace("-", ""));
    mapSegment("G62","e03",cfod.cfod_type().equals("Load") ? "8" : "9");
    mapSegment("G62","e04",cfod.cfod_time1().isBlank() ? "1200" : cfod.cfod_time1().substring(0,4) );
    mapSegment("G62","e05",cfod.cfod_timezone1());
    commitSegment("G62");

    mapSegment("N1","e01", cfod.cfod_type().equals("Load") ? "SF" : "ST");
    mapSegment("N1","e02",cfod.cfod_name());
    mapSegment("N1","e03","9");
    mapSegment("N1","e04",cfod.cfod_code());
    commitSegment("N1");

    mapSegment("N3","e01",cfod.cfod_line1());
    commitSegment("N3");

    mapSegment("N4","e01",cfod.cfod_city());
    mapSegment("N4","e02",cfod.cfod_state());
    mapSegment("N4","e03",cfod.cfod_zip());
    mapSegment("N4","e04","US");
    commitSegment("N4");
} // end S5 loop

                 
        // detail
         int i = 0;
         int sumqty = 0;
         double sumamt = 0;
         double sumlistamt = 0;
         double sumamtTDS = 0;
         String sku = "";
         // item, custitem, qty, po, cumqty, listprice, netprice, reference, sku, desc
         ArrayList<String[]> lines = shpData.getShipperLines(key);
              for (String[] d : lines) {
                  i++;
                                    
                  sumqty = sumqty + Integer.valueOf(d[2]);
                  sumamt = sumamt + (BlueSeerUtils.bsParseDouble(d[2]) * BlueSeerUtils.bsParseDouble(d[6]));
                  sumlistamt = sumlistamt + (BlueSeerUtils.bsParseDouble(d[2]) * BlueSeerUtils.bsParseDouble(d[5]));
              
                mapSegment("LX","e01",snum(i));
                commitSegment("LX");
 
                if (! d[1].equals("Flat Rate")) {
                   mapSegment("L0","e02",cfo.cfo_mileage());
                   mapSegment("L0","e03","DM");
                   mapSegment("L0","e04",cfo.cfo_weight());
                   mapSegment("L0","e05","G");
                   commitSegment("L0");
                }

                if (d[1].equals("Flat Rate")) {
                mapSegment("L1","e02",formatNumber(BlueSeerUtils.bsParseDouble(d[5]),"2"));
                mapSegment("L1","e03","FR");
                mapSegment("L1","e04",formatNumber(BlueSeerUtils.bsParseDouble(d[5]) * 100,"0"));
                mapSegment("L1","e08",d[0]);
                mapSegment("L1","e12",d[9]);
                commitSegment("L1");
                } else {
                mapSegment("L1","e02",formatNumber(BlueSeerUtils.bsParseDouble(d[5]),"2"));
                mapSegment("L1","e03","PM");
                mapSegment("L1","e04",formatNumber(BlueSeerUtils.bsParseDouble(d[5]) * 100,"0"));
                mapSegment("L1","e08",d[0]);
                mapSegment("L1","e12",d[9]);
                commitSegment("L1");
                }
                  
              }
            sumamtTDS = (sumamt * 100);
            
            // trailer
       
         mapSegment("L3","e01",snum(cfo.cfo_weight()));
         mapSegment("L3","e02","G");
         mapSegment("L3","e05",formatNumber(sumamtTDS,"0"));
         mapSegment("L3","e11","7");
         commitSegment("L3");
         
        
