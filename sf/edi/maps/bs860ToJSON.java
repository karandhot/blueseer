import com.blueseer.utl.BlueSeerUtils;

String  now = now();
setReference(getInput("BEG","e03"));

mapSegment("order","po",getInput("BCH",3));
mapSegment("order","site",c[39]);
mapSegment("order","orderdate",BlueSeerUtils.convertDateFormat("yyyyMMdd",getInput("BCH",5)));
mapSegment("order","ordertype","EDI");
mapSegment("order","doctype","bs-json-860");
mapSegment("order","currency","USD");
mapSegment("order","senderid",c[0]);
mapSegment("order","receiverid",c[21]);
mapSegment("order","duedate",BlueSeerUtils.convertDateFormat("yyyyMMdd",getInput("DTM","1:002",2)));
mapSegment("order","vendcode",getInput("REF","1:IA",2));
mapSegment("order","transdatetime",now);
mapSegment("order","isanumber",c[4]);
mapSegment("order","gsnumber",c[5]);
mapSegment("order","stnumber",c[6]);
commitSegment("order");

mapSegment("references:reference","qualifier","IA");
mapSegment("references:reference","value",getInput("REF","1:IA",2));
commitSegment("references:reference");

int addrcount = getGroupCount("N1");
for (int i = 1; i <= addrcount; i++) {
  if (getInput(i,"N1",1).equals("ST")) {
  mapSegment("addresses:address","type","ship-to");
  mapSegment("addresses:address","name",getInput(i,"N1",2));
  mapSegment("addresses:address","addrid",getInput(i,"N1",4));
  mapSegment("addresses:address","line1",getInput(i,"N1:N3",1));
  mapSegment("addresses:address","city",getInput(i,"N1:N4",1));
  mapSegment("addresses:address","state",getInput(i,"N1:N4",2));
  mapSegment("addresses:address","zip",getInput(i,"N1:N4",3));
  commitSegment("addresses:address");
  }
if (getInput(i,"N1",1).equals("BT")) {
  mapSegment("addresses:address","type","bill-to");
  mapSegment("addresses:address","name",getInput(i,"N1",2));
  mapSegment("addresses:address","addrid",getInput(i,"N1",4));
  mapSegment("addresses:address","line1",getInput(i,"N1:N3",1));
  mapSegment("addresses:address","city",getInput(i,"N1:N4",1));
  mapSegment("addresses:address","state",getInput(i,"N1:N4",2));
  mapSegment("addresses:address","zip",getInput(i,"N1:N4",3));
  commitSegment("addresses:address");
  }
}

int count = getGroupCount("POC");
for (int i = 1; i <= count; i++) {
mapSegment("items:item","line",getInput(i,"POC",1));
mapSegment("items:item","itemnumber",getInput(i,"POC",7));
mapSegment("items:item","uom",getInput(i,"POC",3));
mapSegment("items:item","description",getInput(i,"POC",5));
mapSegment("items:item","skunumber",getInput(i,"POC",9));
mapSegment("items:item","orderquantity",getInput(i,"POC",2));
mapSegment("items:item","listprice",getInput(i,"POC",4));
mapSegment("items:item","netprice",getInput(i,"POC",4));
mapSegment("items:item","discount","0");
mapSegment("items:item","upcnumber",getInput(i,"POC",11));
commitSegment("items:item");
}

// tack on raw file as tag in JSON
StringBuilder sb = new StringBuilder();
sb.append("_").append(c[13]).append("_").append(c[14]).append("_");
for (Object f : doc) {
sb.append((String) f).append("_");
}
mapSegment("order:file","contents",sb.toString());
commitSegment("order:file");



