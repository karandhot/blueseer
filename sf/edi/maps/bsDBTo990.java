import com.blueseer.frt.frtData;


String key = doc.get(0).toString();
 // now lets get order header info 
        // fonbr, ref, site, wh, date, remarks, carrier, carrier_assigned, reasoncode, custfo, type
        frtData.cfo_mstr cfo = frtData.getCFOMstr(new String[]{key,""});
        frtData.car_mstr car = frtData.getCarrierMstr(new String[]{"internal"});

        String status = "";
	String rejectcode = "";
        String rejection = "";
        
        if (cfo.cfo_orderstatus().equals("accepted") || cfo.cfo_orderstatus().equals("scheduled")) {
             status = "A";
         } else {
             status = "D";
	     rejectcode = cfo.cfo_rejectcode();
             rejection = cfo.cfo_rejection();
         }
        
        mapSegment("B1","e01",car.car_scac());
       // mapSegment("B1","e02",key);
        mapSegment("B1","e02",cfo.cfo_custfonbr());
        mapSegment("B1","e03",now());
        mapSegment("B1","e04",status);
      //  mapSegment("B1","e06",rejectcode);
        commitSegment("B1");

        mapSegment("N9","e01","TN");
        mapSegment("N9","e02",cfo.cfo_custfonbr());
        mapSegment("N9","e07","CN");
        mapSegment("N9","e08",cfo.cfo_nbr());
        commitSegment("N9");

        if (status.equals("D")) {
           mapSegment("K1","e01",rejectcode);
           mapSegment("K1","e02",rejection);
           commitSegment("K1");
        }

        /*
        mapSegment("L11","e01",cfo.cfo_custfonbr());
        mapSegment("L11","e02","DO");
        mapSegment("L11","e03",cfo.cfo_nbr());
        commitSegment("L11");
        */
