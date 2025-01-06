import com.blueseer.utl.BlueSeerUtils;

//GlobalDebug = true;
var now = now();
var hlcounter = 0;
var ponumber = "";
var itemLoopCount = 0.0;

// get totals
var totalqty = 0.0;
for (int i = 1; i <= getLoopCount("asn:items:item",2); i++) {
totalqty += Double.valueOf(getInput(i, "asn:items:item","asnquantity"));
ponumber = getInput(i, "asn:items:item","order");
}

mapSegment("BSN","e01","00");
mapSegment("BSN","e02",getInput("asn","asnid"));
mapSegment("BSN","e03",getInput("asn","asndate").replace("-",""));
mapSegment("BSN","e04",now.substring(8,12));
commitSegment("BSN");

hlcounter++;   
mapSegment("HL","e01", snum(hlcounter));
mapSegment("HL","e03","S");
commitSegment("HL");

mapSegment("TD1","e01", "PLT");
        mapSegment("TD1","e02",formatNumber(totalqty,"0"));
        mapSegment("TD1","e06","G");
        mapSegment("TD1","e07","100");
        mapSegment("TD1","e08","LB");
        commitSegment("TD1");

mapSegment("REF","e01","BM");
mapSegment("REF","e02",getInput("asn","asnid"));
commitSegment("REF");

mapSegment("REF","e01","CN");
mapSegment("REF","e02",getInput("asn","ordernumber"));
commitSegment("REF");

mapSegment("DTM","e01","011");
mapSegment("DTM","e02",getInput("asn","asndate").replace("-",""));
commitSegment("DTM");

int addrcount = getLoopCount("asn:addresses:address",2);
for (int i = 1; i <= addrcount; i++) {
/*
if (getInput(i,"asn:addresses:address","type").equals("BT")) {
mapSegment("N1","e01","BT");
mapSegment("N1","e02",getInput(i,"asn:addresses:address","name"));
mapSegment("N1","e03","92");
mapSegment("N1","e04",getInput(i,"asn:addresses:address","addrid"));
commitSegment("N1");
mapSegment("N3:N1","e01",getInput(i,"asn:addresses:address","line1"));
commitSegment("N3:N1");
mapSegment("N4:N1","e01",getInput(i,"asn:addresses:address","city"));
mapSegment("N4:N1","e02",getInput(i,"asn:addresses:address","state"));
mapSegment("N4:N1","e03",getInput(i,"asn:addresses:address","zip"));
mapSegment("N4:N1","e04","US");
commitSegment("N4:N1");
} // if billto
*/

if (getInput(i,"asn:addresses:address","type").equals("ST")) {
mapSegment("N1","e01","ST");
mapSegment("N1","e02",getInput(i,"asn:addresses:address","name"));
mapSegment("N1","e03","92");
mapSegment("N1","e04",getInput(i,"asn:addresses:address","addrid"));
commitSegment("N1");
mapSegment("N3:N1","e01",getInput(i,"asn:addresses:address","line1"));
commitSegment("N3:N1");
mapSegment("N4:N1","e01",getInput(i,"asn:addresses:address","city"));
mapSegment("N4:N1","e02",getInput(i,"asn:addresses:address","state"));
mapSegment("N4:N1","e03",getInput(i,"asn:addresses:address","zip"));
mapSegment("N4:N1","e04","US");
commitSegment("N4:N1");
} // if shipto

/*
if (getInput(i,"asn:addresses:address","type").equals("RE")) {
mapSegment("N1","e01","VN");
mapSegment("N1","e02",getInput(i,"asn:addresses:address","name"));
mapSegment("N1","e03","92");
mapSegment("N1","e04",getInput(i,"asn:addresses:address","addrid"));
commitSegment("N1");
mapSegment("N3:N1","e01",getInput(i,"asn:addresses:address","line1"));
commitSegment("N3:N1");
mapSegment("N4:N1","e01",getInput(i,"asn:addresses:address","city"));
mapSegment("N4:N1","e02",getInput(i,"asn:addresses:address","state"));
mapSegment("N4:N1","e03",getInput(i,"asn:addresses:address","zip"));
mapSegment("N4:N1","e04","US");
commitSegment("N4:N1");
} // if vendor
*/
} // loop addresses



int itemcount = getLoopCount("asn:items:item",2);
double lineamt = 0;
double totalamt = 0;
double lineqty = 0;
double lineprice = 0;
for (int i = 1; i <= itemcount; i++) {
 itemLoopCount++;
// do PRF once...this map is one PRF only
                if (itemLoopCount == 1) {
                hlcounter++;
                mapSegment("HL","e01", snum(hlcounter));
                mapSegment("HL","e02","1");
                mapSegment("HL","e03","O");
               // mapSegment("HL","e04","1");
                commitSegment("HL");
                mapSegment("PRF","e01", ponumber);
                commitSegment("PRF");
                
                mapSegment("REF","e01", "IA");
                mapSegment("REF","e02", getInput("asn","vendorcode"));
                commitSegment("REF");

                // pack level if needed
                hlcounter++;
                mapSegment("HL","e01", snum(hlcounter));
                mapSegment("HL","e02","2");
                mapSegment("HL","e03","P");
                commitSegment("HL");

                mapSegment("MAN","e01", "GM");
                mapSegment("MAN","e02","99999999999999999999");
                commitSegment("MAN");
                }

hlcounter++;
                mapSegment("HL","e01", snum(hlcounter));
                mapSegment("HL","e02","3");
                mapSegment("HL","e03","I");
               // mapSegment("HL","e04","1");
                commitSegment("HL");

                mapSegment("LIN","e01",snum(getInput(i, "asn:items:item","line")));
                mapSegment("LIN","e02","VN");
                mapSegment("LIN","e03",getInput(i, "asn:items:item","itemnumber"));
                if (! getInput(i, "asn:items:item","skunumber").isEmpty()) {
                  mapSegment("LIN","e04","BP");
                  mapSegment("LIN","e05",getInput(i, "asn:items:item","skunumber"));
                } 
                commitSegment("LIN");

                mapSegment("SN1","e01",snum(getInput(i, "asn:items:item","line")));
                if (BlueSeerUtils.isParsableToDouble(getInput(i, "asn:items:item","asnquantity"))) {
                    mapSegment("SN1","e02",formatNumber(Double.valueOf(getInput(i, "asn:items:item","asnquantity")),"0"));
                } else {
                    mapSegment("SN1","e02","0");	
                }
                mapSegment("SN1","e03","EA");
                commitSegment("SN1");

                mapSegment("PID","e01","F");
                mapSegment("PID","e02","08");
                mapSegment("PID","e05",getInput(i, "asn:items:item","description"));
                commitSegment("PID");

//lineqty = BlueSeerUtils.bsParseDouble(getInput(i, "asn:items:item","asnquantity"));
//lineprice = BlueSeerUtils.bsParseDouble(getInput(i, "asn:items:item","listprice"));


} // end item loop


mapSegment("CTT","e01",snum(hlcounter));
// mapSegment("CTT","e02",snum(total));
commitSegment("CTT");




