import com.blueseer.ctr.cusData;
import com.blueseer.shp.shpData;
import com.blueseer.edi.EDI;
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.OVData;

     
     	String doctype = c[1];
     	String shipper = doc.get(0).toString();
	EDI.edi856 e = EDI.init856DB(shipper); 

	String  now = now();
	int i = 0;
	int hlcounter = 0;
	int itemLoopCount = 0;
	double totalqty = 0;    
    
    
     /* Begin Mapping Segments */ 
    	mapSegment("BSN","e01","00");
    	mapSegment("BSN","e02",shipper);
    	mapSegment("BSN","e03",now.substring(0,8));
    	mapSegment("BSN","e04",now.substring(8,12));
    	commitSegment("BSN");
    
     hlcounter++;   
        mapSegment("HL","e01", snum(hlcounter));
        mapSegment("HL","e03","S");
        mapSegment("HL","e04","1");
        commitSegment("HL");

        mapSegment("TD1","e01", "PCS25");
        mapSegment("TD1","e02","0");
        mapSegment("TD1","e06","A3");
        mapSegment("TD1","e07","0");
        mapSegment("TD1","e08","LB");
        commitSegment("TD1");

        mapSegment("TD5","e02", "2");
        mapSegment("TD5","e03",e.shipvia());
        mapSegment("TD5","e05",e.shipvia());
        mapSegment("TD5","e06","CC");
        commitSegment("TD5");

        mapSegment("REF","e01", "BM");
        mapSegment("REF","e02",shipper);
        commitSegment("REF");

        if (! e.ref().isEmpty()) {
        mapSegment("REF","e01", "CN");
        mapSegment("REF","e02",e.ref());
        commitSegment("REF");
        }

        mapSegment("DTM","e01", "011");
        mapSegment("DTM","e02",e.confdate().replace("-",""));
        commitSegment("DTM");

        // addresses
        String[] shipaddr = cusData.getShipAddressInfo(e.bs_billto(), e.bs_shipto());
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
       
	

        // Item Loop 
         ArrayList<shpData.ship_det> lines = e.lines();
              for (shpData.ship_det d : lines) {
                itemLoopCount++;
                totalqty += d.shd_qty();

                // do PRF once...this map is one PRF only
                if (itemLoopCount == 1) {
                hlcounter++;
                mapSegment("HL","e01", snum(hlcounter));
                mapSegment("HL","e02","1");
                mapSegment("HL","e03","O");
                mapSegment("HL","e04","1");
                commitSegment("HL");
                mapSegment("PRF","e01", d.shd_po());
                commitSegment("PRF");
                }
                
                hlcounter++;
                mapSegment("HL","e01", snum(hlcounter));
                mapSegment("HL","e02","2");
                mapSegment("HL","e03","I");
                mapSegment("HL","e04","1");
                commitSegment("HL");


                mapSegment("LIN","e04","VN");
                mapSegment("LIN","e05",d.shd_item());
                if (! d.shd_custitem().isEmpty()) {
                  mapSegment("LIN","e06","BP");
                  mapSegment("LIN","e07",d.shd_custitem());
                } 
                commitSegment("LIN");


                
                mapSegment("SN1","e02",formatNumber(d.shd_qty(),"0"));
                mapSegment("SN1","e03","PC");
                commitSegment("SN1");

                mapSegment("PO4","e01","1");
                mapSegment("PO4","e02",formatNumber(d.shd_qty(),"0"));
                mapSegment("PO4","e03","PC");
                commitSegment("PO4");


                mapSegment("PID","e01","F");
                mapSegment("PID","e05",d.shd_desc());
                commitSegment("PID");

        }

            /* end of item loop */

        mapSegment("CTT","e01",snum(hlcounter));
        mapSegment("CTT","e02",formatNumber(totalqty,"0"));
        commitSegment("CTT");

