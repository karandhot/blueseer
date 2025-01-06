import com.blueseer.utl.BlueSeerUtils;

//GlobalDebug = true;
mapSegment("BIG","e01",getInput("invoice","invoicedate").replace("-",""));
mapSegment("BIG","e02",getInput("invoice","invoiceid"));
mapSegment("BIG","e03",getInput("invoice","podate").replace("-",""));
mapSegment("BIG","e04",getInput("invoice","ponumber"));
commitSegment("BIG");


mapSegment("REF","e01","IA");
mapSegment("REF","e02",getInput("invoice","vendorcode"));
commitSegment("REF");

mapSegment("REF","e01","CN");
mapSegment("REF","e02",getInput("invoice","invoiceid"));
commitSegment("REF");



int addrcount = getLoopCount("invoice:addresses:address",2);
for (int i = 1; i <= addrcount; i++) {
/*
if (getInput(i,"invoice:addresses:address","type").equals("BT")) {
mapSegment("N1","e01","BT");
mapSegment("N1","e02",getInput(i,"invoice:addresses:address","name"));
mapSegment("N1","e03","92");
mapSegment("N1","e04",getInput(i,"invoice:addresses:address","addrid"));
commitSegment("N1");
mapSegment("N3:N1","e01",getInput(i,"invoice:addresses:address","line1"));
commitSegment("N3:N1");
mapSegment("N4:N1","e01",getInput(i,"invoice:addresses:address","city"));
mapSegment("N4:N1","e02",getInput(i,"invoice:addresses:address","state"));
mapSegment("N4:N1","e03",getInput(i,"invoice:addresses:address","zip"));
mapSegment("N4:N1","e04","US");
commitSegment("N4:N1");
} // if billto
*/

if (getInput(i,"invoice:addresses:address","type").equals("ST")) {
mapSegment("N1","e01","ST");
mapSegment("N1","e02",getInput(i,"invoice:addresses:address","name"));
mapSegment("N1","e03","92");
mapSegment("N1","e04",getInput(i,"invoice:addresses:address","addrid"));
commitSegment("N1");
mapSegment("N3:N1","e01",getInput(i,"invoice:addresses:address","line1"));
commitSegment("N3:N1");
mapSegment("N4:N1","e01",getInput(i,"invoice:addresses:address","city"));
mapSegment("N4:N1","e02",getInput(i,"invoice:addresses:address","state"));
mapSegment("N4:N1","e03",getInput(i,"invoice:addresses:address","zip"));
mapSegment("N4:N1","e04","US");
commitSegment("N4:N1");
} // if shipto

/*
if (getInput(i,"invoice:addresses:address","type").equals("RE")) {
mapSegment("N1","e01","VN");
mapSegment("N1","e02",getInput(i,"invoice:addresses:address","name"));
mapSegment("N1","e03","92");
mapSegment("N1","e04",getInput(i,"invoice:addresses:address","addrid"));
commitSegment("N1");
mapSegment("N3:N1","e01",getInput(i,"invoice:addresses:address","line1"));
commitSegment("N3:N1");
mapSegment("N4:N1","e01",getInput(i,"invoice:addresses:address","city"));
mapSegment("N4:N1","e02",getInput(i,"invoice:addresses:address","state"));
mapSegment("N4:N1","e03",getInput(i,"invoice:addresses:address","zip"));
mapSegment("N4:N1","e04","US");
commitSegment("N4:N1");
} // if vendor
*/
} // loop addresses

mapSegment("DTM","e01","011");
mapSegment("DTM","e02",getInput("invoice","shipdate").replace("-",""));
commitSegment("DTM");

int itemcount = getLoopCount("invoice:items:item",2);
double lineamt = 0;
double totalamt = 0;
double lineqty = 0;
double lineprice = 0;
int totalqty = 0;
for (int i = 1; i <= itemcount; i++) {
 
lineqty = BlueSeerUtils.bsParseDouble(getInput(i, "invoice:items:item","invoicequantity"));
lineprice = BlueSeerUtils.bsParseDouble(getInput(i, "invoice:items:item","listprice"));
totalqty += Double.valueOf(getInput(i, "invoice:items:item","invoicequantity"));

mapSegment("IT1","e01",getInput(i, "invoice:items:item","line"));
mapSegment("IT1","e02",getInput(i, "invoice:items:item","invoicequantity"));
mapSegment("IT1","e03",getInput(i, "invoice:items:item","uom"));
mapSegment("IT1","e04",getInput(i, "invoice:items:item","listprice"));
mapSegment("IT1","e06","VN");
mapSegment("IT1","e07",getInput(i, "invoice:items:item","itemnumber"));
//mapSegment("IT1","e08","VN");
//mapSegment("IT1","e09",getInput(i, "invoice:items:item","skunumber"));
//mapSegment("IT1","e10","UP");
//mapSegment("IT1","e11",getInput(i, "invoice:items:item","upcnumber"));
commitSegment("IT1");

mapSegment("PID","e01","F");
mapSegment("PID","e02","08");
mapSegment("PID","e05",getInput(i, "invoice:items:item","description"));
commitSegment("PID");

lineamt = lineqty * lineprice;
totalamt = totalamt + lineamt;

} // end item loop

mapSegment("TDS","e01",formatNumber((totalamt * 100),"0"));
mapSegment("TDS","e02",formatNumber((totalamt * 100),"0"));
commitSegment("TDS");

mapSegment("ISS","e01",snum(totalqty));
mapSegment("ISS","e02","EA");
mapSegment("ISS","e03",snum(totalqty));
mapSegment("ISS","e04","LB");
commitSegment("ISS");

mapSegment("CTT","e01",snum(itemcount));
// mapSegment("CTT","e02",snum(total));
commitSegment("CTT");



