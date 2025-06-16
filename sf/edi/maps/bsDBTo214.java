import com.blueseer.frt.frtData;
import com.blueseer.utl.EDData;


String cfonbr = doc.get(0).toString();
String uniquekey = doc.get(1).toString();
String defaultrev = "";
String shiptype = "";
String shipname = "";
String shipline = "";
String shipcity = "";
String shipstate = "";
String shipzip = "";

 // now lets get order header info 
        // fonbr, ref, site, wh, date, remarks, carrier, carrier_assigned, reasoncode, custfo, type
        frtData.cfo_mstr cfo = frtData.getCFOMstr(new String[]{cfonbr,""});
        frtData.car_mstr car = frtData.getCarrierMstr(new String[]{"internal"});
        frtData.cfo_status cfox = frtData.getCFOStatus(new String[]{cfonbr,uniquekey});
        String[] t = EDData.getEDIXrefOut(cfo.cfo_cust(),"QM");
        
        defaultrev = cfo.cfo_defaultrev();

        // need to determine stop address info for this particular 214 event code
        String eventcode = cfox.cfox_event();

        // IF PICKUP event codes
        if (eventcode.equals("AA") || eventcode.equals("AF") || eventcode.equals("X3") ) {
          shiptype = "SF";
          ArrayList<frtData.cfo_det> cfod = frtData.getCFODet(cfonbr, defaultrev);
          for (frtData.cfo_det cd : cfod) {
              if (cd.cfod_type().equals("Load")) {
              shipname = cd.cfod_name();
              shipline = cd.cfod_line1();
              shipcity = cd.cfod_city();
              shipstate = cd.cfod_state();
              shipzip = cd.cfod_zip();
              }
          }
        } else {  // else must be drop off event codes
          shiptype = "ST";
          ArrayList<frtData.cfo_det> cfod = frtData.getCFODet(cfonbr, defaultrev);
          for (frtData.cfo_det cd : cfod) {
              if (cd.cfod_type().equals("UnLoad")) {
              shipname = cd.cfod_name();
              shipline = cd.cfod_line1();
              shipcity = cd.cfod_city();
              shipstate = cd.cfod_state();
              shipzip = cd.cfod_zip();
              }
          }
        }

        mapSegment("B10","e01",cfonbr);
        mapSegment("B10","e02",cfo.cfo_custfonbr());
        mapSegment("B10","e03",car.car_scac());
        commitSegment("B10");

        mapSegment("L11","e01",cfo.cfo_custfonbr());
        mapSegment("L11","e02","OQ");
        commitSegment("L11");

        mapSegment("N1","e01","SH");
        mapSegment("N1","e02",cfo.cfo_cust());
        mapSegment("N1","e03","1");
        mapSegment("N1","e04",t[1]);
        commitSegment("N1");

        mapSegment("N1","e01",shiptype);
        mapSegment("N1","e02",shipname);
        mapSegment("N1","e03","9");
        mapSegment("N1","e04",t[1]);
        commitSegment("N1");

        mapSegment("N3","e01",shipline);
        commitSegment("N3");

        mapSegment("N4","e01",shipcity);
        mapSegment("N4","e02",shipstate);
        mapSegment("N4","e03",shipzip);
        commitSegment("N4");

        mapSegment("LX","e01","1");
        commitSegment("LX");

        mapSegment("AT7","e01",eventcode);
        mapSegment("AT7","e02",cfox.cfox_status());
        mapSegment("AT7","e03","");
        mapSegment("AT7","e04","");
        mapSegment("AT7","e05",cfox.cfox_eventdate());
        mapSegment("AT7","e06",cfox.cfox_eventtime());
        mapSegment("AT7","e07","LT");
        commitSegment("AT7");

        if (! cfox.cfox_city().isBlank()) {
        mapSegment("MS1","e01",shipcity);
        mapSegment("MS1","e02",shipstate);
        mapSegment("MS1","e03",shipzip);
        commitSegment("MS1");
        } else {
        mapSegment("MS1","e04",cfox.cfox_lat());
        mapSegment("MS1","e05",cfox.cfox_long());
        commitSegment("MS1");
        }

         mapSegment("MS2","e01",car.car_scac());
        mapSegment("MS2","e02",cfox.cfox_remarks());
        mapSegment("MS2","e03","TF");
        commitSegment("MS2");


       

       
