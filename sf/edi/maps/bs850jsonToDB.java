import com.blueseer.ctr.cusData;
import com.blueseer.utl.OVData;
import com.blueseer.inv.invData;
import com.blueseer.utl.EDData;
import com.blueseer.utl.BlueSeerUtils;

setReference(getInput("order","po")); //optional...but must be ran after mappedInput
 

  
    isDBWrite(c);// optional...unless this map is writing to internal database tables (orders, etc)
    
    //since this is a DB entry map, create class object specific to inbound doctype (edi850, edi824, etc)
    edi850 e = new edi850(getInput("order","senderid"), getInput("order","receiverid"), getInput("order","senderid"), getInput("order","receiverid"), "", "", doctype, stctrl);  // mandatory class creation
    
    //optional...set some global variables as necessary
    String  now = now();
    String po;
    
    ArrayList<String[]> ta = new ArrayList<String[]>();

    // begin mapping

    // first try finding internal Billto with N1 BT ....then fall back to ISA receiver
    e.setOVBillTo(EDData.getEDIXrefIn(getInput("order","receiverid"), getInput("order","senderid"), "BT", getTag("addresses:address","type:billto","addrid"))); 
    
    if (e.getOVBillTo().isEmpty()) {
     e.setOVBillTo(EDData.getEDIXrefIn(getInput("order","receiverid"), getInput("order","senderid"), "BT", getInput("order","senderid"))); 
    }
    po = getInput("order","po");
    e.setPO(po);  
    
    e.setPODate(getInput("order","orderdate"));
    e.setDueDate(getInput("order","duedate"));
    ta.add(new String[]{po,"header","duedate", getInput("order","duedate")}); 

   
    ta.add(new String[]{po,"header","vendcode", getInput("order","vendcode")});
   
    
    
int addrcount = getLoopCount("order:addresses:address",2);
for (int i = 1; i <= addrcount; i++) {
if (getInput(i,"order:addresses:address","type").equals("billto")) {
    ta.add(new String[]{po,"header","billcode", getInput(i,"order:addresses:address","addrid")});
} // if billto

if (getInput(i,"order:addresses:address","type").equals("shipto")) {
	    e.setShipTo(getInput(i,"order:addresses:address","addrid"));
            e.setShipToName(getInput(i,"order:addresses:address","name"));
            e.setShipToLine1(getInput(i,"order:addresses:address","line1"));
            e.setShipToCity(getInput(i,"order:addresses:address","city"));
            e.setShipToState(getInput(i,"order:addresses:address","state"));
            e.setShipToZip(getInput(i,"order:addresses:address","zip"));
            e.setOVShipTo(EDData.getEDIXrefIn(getInput("order","receiverid"), getInput("order","senderid"), "ST", getInput(i,"order:addresses:address","addrid")));
            ta.add(new String[]{po,"header","shipcode", getInput(i,"order:addresses:address","addrid")});   
} // if shipto

} // loop addresses

  
   if (! e.getOVShipTo().isEmpty()) {
   e.setOVBillTo(cusData.getcustBillTo(e.getOVShipTo()));
   } 
   // NOTE: it's imperative that we have an internal billto code assign for pricing and discounts look up during the detail loop
   // if here and we have a blank billto...then error out
   if (e.getOVBillTo().isEmpty()) {
   setError("No internal Billto Found PO: " + po);
   return error; 
   }

/* Now the Detail Item LOOP  */ 
int itemcount = getLoopCount("order:items:item",2);
int total = 0;
int totalqty = 0;
String uom = "";
String item = "";
double discount;
double listprice;
double netprice;
boolean useInternalPrice = false;

for (int i = 1; i <= itemcount; i++) {
	e.addDetail();  // INITIATE An ArrayList
        totalqty += Double.valueOf(getInput(i, "order:items:item","orderquantity"));

        e.setDetQty(i-1, getInput(i, "order:items:item","orderquantity"));
        e.setDetItem(i-1,getInput(i, "order:items:item","itemnumber"));
        ta.add(new String[]{po,("detail:"+snum(i)),"item", getInput(i, "order:items:item","itemnumber")});
        e.setDetCustItem(i-1,getInput(i, "order:items:item","skunumber"));
        item = e.getDetItem(i-1);
        e.setDetPO(i-1,po);
        e.setDetLine(i-1,getInput(i, "order:items:item","line"));
        e.setDetDesc(i-1,getInput(i, "order:items:item","description"));
        uom = getInput(i, "order:items:item","uom");
        if (uom.isBlank()) {
        uom = OVData.getUOMByItem(item);
        }
        e.setDetUOM(i-1,uom);

        // detail turn around (ta)
        ta.add(new String[]{po,("detail:"+snum(i)),"custline", getInput(i, "order:items:item","line")});
        ta.add(new String[]{po,("detail:"+snum(i)),"qty", getInput(i, "order:items:item","orderquantity")});
        ta.add(new String[]{po,("detail:"+snum(i)),"price", getInput(i, "order:items:item","listprice")});
        ta.add(new String[]{po,("detail:"+snum(i)),"sku", getInput(i, "order:items:item","skunumber")});
        ta.add(new String[]{po,("detail:"+snum(i)),"sku", getInput(i, "order:items:item","upcnumber")});
        

        if (useInternalPrice) {
        listprice = invData.getItemPriceFromCust(e.getOVBillTo(), item, uom, cusData.getCustCurrency(e.getOVBillTo()),"LIST",getInput(i, "order:items:item","orderquantity"));
        discount = invData.getItemDiscFromCust(e.getOVBillTo());
        netprice = listprice;
        if (discount != 0) {
        netprice = listprice - (listprice * (discount / 100));
        }
        e.setDetNetPrice(i-1,formatNumber(netprice,"2"));
        e.setDetListPrice(i-1,formatNumber(listprice,"2"));
        e.setDetDisc(i-1,formatNumber(discount,"2"));
        } else {
         if (isNumber(getInput(i, "order:items:item","listprice"))) {
            e.setDetNetPrice(i-1, formatNumber(getInput(i, "order:items:item","listprice"),"3"));
            e.setDetListPrice(i-1, formatNumber(getInput(i, "order:items:item","netprice"),"3"));
         } else {
            e.setDetNetPrice(i-1, "0");
            e.setDetListPrice(i-1, "0");	
         }   
        }

}
   
    /* end of item loop */

    // mapping end
    
    mappedInput.clear();

     /* Load Sales Order */
     /* call processDB ONLY if the output is database write */
    processDB(c,com.blueseer.edi.EDI.createSOFrom850(e, c), ta);

