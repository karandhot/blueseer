import com.blueseer.ctr.cusData;
import com.blueseer.shp.shpData;
import com.blueseer.adm.admData;
import com.blueseer.utl.BlueSeerUtils;
import com.blueseer.utl.OVData;
import java.util.HashSet;

GlobalDebug = true;

     com.blueseer.edi.EDI edi = new com.blueseer.edi.EDI();
     String doctype = c[1];
     String shipper = doc.get(0).toString();

		    String  now = now();
		    int i = 0;
		    int hlcounter = 0;
                    int packcounter = 0;
                    int ordercounter = 0;
		    int itemLoopCount = 0;
		    double totalqty = 0;    
    String[] h = shpData.getShipperHeader(shipper);  // 13 elements...see declaration
    String ponum = h[3];

     /* Begin Mapping Segments */ 
    mapSegment("asn","asnid",shipper);
    mapSegment("asn","site",shipper);
    mapSegment("asn","asndate",now);
mapSegment("asn","remarks",h[6]);
mapSegment("asn","reference",h[7]);
mapSegment("asn","doctype","810");
mapSegment("asn","asntype","");
mapSegment("asn","senderid",c[0]);
mapSegment("asn","receiverid",c[21]);
mapSegment("asn","currency",h[13]);
mapSegment("asn","shipvia",h[8]);
mapSegment("asn","transportmethod","");
mapSegment("asn","status","");
mapSegment("asn","termscode",h[29]);
mapSegment("asn","termsdescription",h[30]);
 commitSegment("asn");


mapSegment("references:reference","qualifier","vendcode");
if (! getMeta(ponum,"header","vendcode").isBlank()) {
mapSegment("references:reference","value",getMeta(ponum,"header","vendcode"));
} else {
mapSegment("references:reference","value",c[0]);
}
commitSegment("references:reference");


String[] bt = cusData.getCustAddressInfo(h[0]);
mapSegment("addresses:address","type","BT"); 
mapSegment("addresses:address","addrid",getMeta(ponum,"header","billcode"));    
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
mapSegment("addresses:address","addrid",getMeta(ponum,"header","shipcode"));    
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

  
	

        // HL Order Loop
        HashSet<String> pos = shpData.getShipperTreeUniquePOs(shipper);
        for (String po : pos) {
          
		mapSegment("orders:order","ponumber",po);
		mapSegment("orders:order","podate","");
		mapSegment("orders:order","orderreference","");
		mapSegment("orders:order","ordertype","");
		mapSegment("orders:order","orderaddrtype","");
                mapSegment("orders:order","orderaddrname","");
                commitSegment("orders:order");

	  

		HashSet<String> packing = shpData.getShipperTreePackOfPO(shipper, po);
              
                for (String pack : packing) {
                        ArrayList<String[]> lines = shpData.getShipperTreeLinesOfPack(pack, shipper);
                        for (String[] line : lines) {
			mapSegment("items:item","packline","");
                        mapSegment("items:item","packitem","");
			mapSegment("items:item","packaltname","");
			mapSegment("items:item","packtype","GM");
			mapSegment("items:item","packserial",pack);
			mapSegment("items:item","order",po);
                        mapSegment("items:item","itemnumber",line[0]);
                        mapSegment("items:item","line",line[4]);
			mapSegment("items:item","uom","EA");
			mapSegment("items:item","description",line[3]);
			mapSegment("items:item","skunumber",line[2]);
			mapSegment("items:item","asnquantity",formatNumber(Double.valueOf(line[1]),"0"));
			commitSegment("items:item");
                        } // end of item loop
                } // end pack loop

           } // end order loop    

      
 
       

