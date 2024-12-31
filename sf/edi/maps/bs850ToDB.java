import com.blueseer.ctr.cusData;
import com.blueseer.utl.OVData;
import com.blueseer.inv.invData;
import com.blueseer.utl.EDData;


    setReference(getInput("BEG","e03")); //optional...but must be ran after mappedInput
   
    isDBWrite(c);// optional...unless this map is writing to internal database tables (orders, etc)
    
    //since this is a DB entry map, create class object specific to inbound doctype (edi850, edi824, etc)
    edi850 e = new edi850(getInputISA(6), getInputISA(8), getInputGS(2), getInputGS(3), getInputISA(13), getInputISA(9), doctype, stctrl);  // mandatory class creation
    
    //optional...set some global variables as necessary
    String  now = now();
    int i = 0; 
    String po;
    double discount;
    double listprice;
    double netprice;
    boolean useInternalPrice = false;
    ArrayList<String[]> ta = new ArrayList<String[]>();

    // begin mapping
    
    // first try finding internal Billto with N1 BT ....then fall back to ISA receiver
    e.setOVBillTo(EDData.getEDIXrefIn(getInputGS(3), getInputGS(2), "BT", getInput("N1","1:BT","e04"))); 
    
    if (e.getOVBillTo().isEmpty()) {
     e.setOVBillTo(EDData.getEDIXrefIn(getInputGS(3), getInputGS(2), "BT", getInputGS(2))); 
    }
    po = getInput("BEG","e03");
    e.setPO(po);  
    e.setPODate(convertDate("yyyyMMdd", getInput("BEG","e05")));

    if (segmentExists("DTM","1:002","e01")) {
    e.setDueDate(convertDate("yyyyMMdd", getInput("DTM","1:002","e02")));
    } else {
    e.setDueDate(now);    
    }   

    // store turnaround
    ta.add(new String[]{po,"header","vendcode", getInput("REF","1:IA","e02")});
    ta.add(new String[]{po,"header","shipcode", getInput("N1","1:ST","e04")});
    ta.add(new String[]{po,"header","billcode", getInput("N1","1:BT","e04")});
    ta.add(new String[]{po,"header","duedate", getInput("DTM","1:002","e02")});
    
    int n1count = getGroupCount("N1");
    boolean isN1ST = false;
    for (i = 1; i <= n1count; i++) {
        if (getInput(i,"N1",1).equals("ST")) {
        isN1ST = true;
        } else {
        isN1ST = false;
        }
        if (isN1ST) {
            e.setShipTo(getInput(i,"N1",4));
            e.setShipToName(getInput(i,"N1",2));
            e.setShipToLine1(getInput(i,"N1:N3",1));
            e.setShipToCity(getInput(i,"N1:N4",1));
            e.setShipToState(getInput(i,"N1:N4",2));
            e.setShipToZip(getInput(i,"N1:N4",3));
            e.setOVShipTo(EDData.getEDIXrefIn(getInputISA(6), getInputGS(2), "ST", getInput(i,"N1",4)));
        }
    }  // shipto loop

   if (! e.getOVShipTo().isEmpty()) {
   e.setOVBillTo(cusData.getcustBillTo(e.getOVShipTo()));
   } 
   // NOTE: it's imperative that we have an internal billto code assign for pricing and discounts look up during the detail loop
   // if here and we have a blank billto...then error out
   if (e.getOVBillTo().isEmpty()) {
   setError("No internal Billto Found PO: " + po);
   return error; 
   }

       /* Now the Detail LOOP  */ 
       /* Item Loop */
    int itemcount = getGroupCount("PO1");
    int itemLoopCount = 0;
    int totalqty = 0;
    String uom = "";
    String item = "";
    for (i = 1; i <= itemcount; i++) {
        e.addDetail();  // INITIATE An ArrayList for Each PO1 SEGMENT....variable i is set at bottom of loop as index  i == 0 is first PO1
        itemLoopCount++;
        totalqty += Integer.valueOf(getInput(i,"PO1",2));
        e.setDetQty(i-1, getInput(i,"PO1",2));
        if (getInput(i,"PO1",6).equals("VP") || getInput(i,"PO1",6).equals("VN")) {
         e.setDetItem(i-1,getInput(i,"PO1",7));
         ta.add(new String[]{po,("detail:"+snum(i)),"item", getInput(i,"PO1",7)});
        } else if (getInput(i,"PO1",8).equals("BP") || getInput(i,"PO1",8).equals("SK")) {
         e.setDetItem(i-1,getInput(i,"PO1",9));   
        } else {
         e.setDetItem(i-1,"UNKNOWN");   
        }
        item = e.getDetItem(i-1);
       // e.setDetCustItem(i,getInput(i,"PO1",9));
        e.setDetPO(i-1,po);
        e.setDetLine(i-1,getInput(i,"PO1",1));

        // detail turn around (ta)
        ta.add(new String[]{po,("detail:"+snum(i)),"custline", getInput(i,"PO1",1)});
        ta.add(new String[]{po,("detail:"+snum(i)),"qty", getInput(i,"PO1",2)});
        ta.add(new String[]{po,("detail:"+snum(i)),"price", getInput(i,"PO1",4)});
        
        if (getInput(i,"PO1",6).equals("BP") || getInput(i,"PO1",6).equals("SK")) {
         ta.add(new String[]{po,("detail:"+snum(i)),"sku", getInput(i,"PO1",7)});
        }
        if (getInput(i,"PO1",8).equals("BP") || getInput(i,"PO1",8).equals("SK")) {
         ta.add(new String[]{po,("detail:"+snum(i)),"sku", getInput(i,"PO1",9)});
        }
        if (getInput(i,"PO1",10).equals("UP")) {
         ta.add(new String[]{po,("detail:"+snum(i)),"upc", getInput(i,"PO1",11)});
        }

        e.setDetDesc(i-1,getInput(i,"PO1:PID",5));

        //override incoming UOM with what is available in UOM Maintenance
        if (getInput(i,"PO1",3).equals("CS")) {
            uom = "CA";
        } else {
         uom = OVData.getUOMByItem(item);
        }

        if (uom.isBlank()) {
        uom = getInput(i,"PO1",3);  // take whatever is there
        }

        e.setDetUOM(i-1,uom);

        if (useInternalPrice) {
        listprice = invData.getItemPriceFromCust(e.getOVBillTo(), item, uom, cusData.getCustCurrency(e.getOVBillTo()),"LIST",getInput(i,"PO1",2));
        discount = invData.getItemDiscFromCust(e.getOVBillTo());
        netprice = listprice;
        if (discount != 0) {
        netprice = listprice - (listprice * (discount / 100));
        }
        e.setDetNetPrice(i-1,formatNumber(netprice,"2"));
        e.setDetListPrice(i-1,formatNumber(listprice,"2"));
        e.setDetDisc(i-1,formatNumber(discount,"2"));
        } else {
         if (isNumber(getInput(i,"PO1",4))) {
            e.setDetNetPrice(i-1, formatNumber(getInput(i,"PO1",4),"3"));
            e.setDetListPrice(i-1, formatNumber(getInput(i,"PO1",4),"3"));
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

