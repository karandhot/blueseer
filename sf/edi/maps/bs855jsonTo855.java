import com.blueseer.utl.BlueSeerUtils;


String itemstatus = "";
String headerstatus = "AD";  // originally assign accepted

// loop through items to see if any changes
for (int i = 1; i <= getLoopCount("order:items:item",2); i++) {
  if (BlueSeerUtils.bsParseDouble(getInput(i, "order:items:item","orderquantity")) != BlueSeerUtils.bsParseDouble(getInput(i, "order:items:item","originalquantity")) ) {
  headerstatus = "AC";
  }
  if (BlueSeerUtils.bsParseDouble(getInput(i, "order:items:item","listprice")) != BlueSeerUtils.bsParseDouble(getInput(i, "order:items:item","originalprice")) ) {
  headerstatus = "AC";
  }
} // item loop

//GlobalDebug = true;
mapSegment("BAK","e01","00");
mapSegment("BAK","e02",headerstatus);
mapSegment("BAK","e03",getInput("order","po"));
mapSegment("BAK","e04",getInput("order","orderdate").replace("-",""));
mapSegment("BAK","e09",getInput("order","orderdate").replace("-",""));
commitSegment("BAK");


mapSegment("REF","e01","IA");
mapSegment("REF","e02",getInput("order","vendcode"));
commitSegment("REF");

mapSegment("DTM","e01","068");
mapSegment("DTM","e02",getInput("order","duedate").replace("-",""));
commitSegment("DTM");

int addrcount = getLoopCount("order:addresses:address",2);
for (int i = 1; i <= addrcount; i++) {
/*
if (getInput(i,"order:addresses:address","type").equals("BT")) {
mapSegment("N1","e01","BT");
mapSegment("N1","e02",getInput(i,"order:addresses:address","name"));
mapSegment("N1","e03","92");
mapSegment("N1","e04",getInput(i,"order:addresses:address","addrid"));
commitSegment("N1");
mapSegment("N3:N1","e01",getInput(i,"order:addresses:address","line1"));
commitSegment("N3:N1");
mapSegment("N4:N1","e01",getInput(i,"order:addresses:address","city"));
mapSegment("N4:N1","e02",getInput(i,"order:addresses:address","state"));
mapSegment("N4:N1","e03",getInput(i,"order:addresses:address","zip"));
mapSegment("N4:N1","e04","US");
commitSegment("N4:N1");
} // if billto
*/

if (getInput(i,"order:addresses:address","type").equals("ST")) {
mapSegment("N1","e01","ST");
mapSegment("N1","e02",getInput(i,"order:addresses:address","name"));
mapSegment("N1","e03","92");
mapSegment("N1","e04",getInput(i,"order:addresses:address","addrid"));
commitSegment("N1");
mapSegment("N3:N1","e01",getInput(i,"order:addresses:address","line1"));
commitSegment("N3:N1");
mapSegment("N4:N1","e01",getInput(i,"order:addresses:address","city"));
mapSegment("N4:N1","e02",getInput(i,"order:addresses:address","state"));
mapSegment("N4:N1","e03",getInput(i,"order:addresses:address","zip"));
mapSegment("N4:N1","e04","US");
commitSegment("N4:N1");
} // if shipto

if (getInput(i,"order:addresses:address","type").equals("RE")) {
mapSegment("N1","e01","VN");
mapSegment("N1","e02",getInput(i,"order:addresses:address","name"));
mapSegment("N1","e03","92");
mapSegment("N1","e04",getInput(i,"order:addresses:address","addrid"));
commitSegment("N1");
mapSegment("N3:N1","e01",getInput(i,"order:addresses:address","line1"));
commitSegment("N3:N1");
mapSegment("N4:N1","e01",getInput(i,"order:addresses:address","city"));
mapSegment("N4:N1","e02",getInput(i,"order:addresses:address","state"));
mapSegment("N4:N1","e03",getInput(i,"order:addresses:address","zip"));
mapSegment("N4:N1","e04","US");
commitSegment("N4:N1");
} // if vendor

}

int itemcount = getLoopCount("order:items:item",2);
double lineamt = 0;
double totalamt = 0;
double lineqty = 0;
double lineprice = 0;
int totalqty = 0;
for (int i = 1; i <= itemcount; i++) {
  itemstatus = "IA";
  
  if (BlueSeerUtils.bsParseDouble(getInput(i, "order:items:item","orderquantity")) != BlueSeerUtils.bsParseDouble(getInput(i, "order:items:item","originalquantity")) ) {
  itemstatus = "IQ";
  }
  if (BlueSeerUtils.bsParseDouble(getInput(i, "order:items:item","orderquantity")) == 0 ) {
  itemstatus = "IR";
  }
  if (BlueSeerUtils.bsParseDouble(getInput(i, "order:items:item","listprice")) != BlueSeerUtils.bsParseDouble(getInput(i, "order:items:item","originalprice")) ) {
  itemstatus = "IP";
  }

lineqty = BlueSeerUtils.bsParseDouble(getInput(i, "order:items:item","orderquantity"));
lineprice = BlueSeerUtils.bsParseDouble(getInput(i, "order:items:item","listprice"));
totalqty += Double.valueOf(getInput(i, "order:items:item","orderquantity"));

//mapSegment("PO1","e01",getInput(i, "order:items:item","line"));
mapSegment("PO1","e02",getInput(i, "order:items:item","orderquantity"));
mapSegment("PO1","e03",getInput(i, "order:items:item","uom"));
mapSegment("PO1","e04",getInput(i, "order:items:item","listprice"));
mapSegment("PO1","e06","VN");
mapSegment("PO1","e07",getInput(i, "order:items:item","itemnumber"));
//mapSegment("PO1","e08","VN");
//mapSegment("PO1","e09",getInput(i, "order:items:item","skunumber"));
//mapSegment("PO1","e10","UP");
//mapSegment("PO1","e11",getInput(i, "order:items:item","upcnumber"));
commitSegment("PO1");

mapSegment("PID","e01","F");
mapSegment("PID","e02","08");
mapSegment("PID","e05",getInput(i, "order:items:item","description"));
commitSegment("PID");

mapSegment("ACK","e01",itemstatus);
mapSegment("ACK","e02",getInput(i, "order:items:item","orderquantity"));
mapSegment("ACK","e03",getInput(i, "order:items:item","uom"));
mapSegment("ACK","e04","068");
mapSegment("ACK","e05",getInput("order","duedate").replace("-",""));
commitSegment("ACK");


lineamt = lineqty * lineprice;
totalamt = totalamt + lineamt;

mapSegment("AMT","e01","1");
mapSegment("AMT","e02",formatNumber((lineamt * 100),"0"));
commitSegment("AMT");

} // end item loop

mapSegment("CTT","e01",snum(itemcount));
// mapSegment("CTT","e02",snum(total));
commitSegment("CTT");

mapSegment("AMT","e01","TT");
mapSegment("AMT","e02",snum(formatNumber((totalamt * 100),"0")));
commitSegment("AMT");

