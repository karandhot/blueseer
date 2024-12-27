import com.blueseer.ctr.cusData;
import com.blueseer.utl.OVData;
import com.blueseer.inv.invData;
import com.blueseer.utl.EDData;
import com.blueseer.ord.ordData;


    setReference(getInput("BCH","e03")); //optional...but must be ran after mappedInput
   
    isDBWrite(c);// optional...unless this map is writing to internal database tables (orders, etc)
    
    //since this is a DB entry map, create class object specific to inbound doctype (edi850, edi824, etc)
    edi860 e = new edi860(getInputISA(6), getInputISA(8), getInputGS(2), getInputGS(3), getInputISA(13), getInputISA(9), doctype, stctrl);  // mandatory class creation
    
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
     po = getInput("BCH","e03");
     e.setPO(po);
     

    // changeid must be set as it is used for unique ID for so_chg and sod_chg
     e.setChangeID(po + "-" + getInputGS(5));  

    // first try finding internal Billto with Original Purchase Order
    e.setOVBillTo(ordData.getSOOrderBilltoByPO(po)); 
    
    if (e.getOVBillTo().isEmpty()) {
     e.setOVBillTo(EDData.getEDIXrefIn(getInputGS(3), getInputGS(2), "BT", getInputGS(2))); 
    }

    if (e.getPO().isEmpty()) {
      setError("No internal PO found for this change: " + po);
      return error; 
    }
     
    

    if (segmentExists("DTM","1:002","e01")) {
    e.setDueDate(convertDate("yyyyMMdd", getInput("DTM","1:002","e02")));
    } else {
    e.setDueDate(now);    
    } 
    
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

   

       /* Now the Detail LOOP  */ 
       /* Item Loop */
    int itemcount = getGroupCount("POC");
    int itemLoopCount = 0;
    int totalqty = 0;
    String uom = "";
    String item = "";
    for (i = 1; i <= itemcount; i++) {
        e.addDetail();  // INITIATE An ArrayList for Each POC SEGMENT....variable i is set at bottom of loop as index  i == 0 is first PO1
        itemLoopCount++;
        totalqty += Integer.valueOf(getInput(i,"POC",4));
        e.setDetQty(i-1, getInput(i,"POC",4));
        if (getInput(i,"POC",8).equals("VP") || getInput(i,"POC",8).equals("VN")) {
         e.setDetItem(i-1,getInput(i,"POC",9));
        } else if (getInput(i,"POC",10).equals("BP") || getInput(i,"POC",10).equals("SK")) {
         e.setDetItem(i-1,getInput(i,"POC",11));   
        } else {
         e.setDetItem(i-1,"UNKNOWN");   
        }
        item = e.getDetItem(i-1);
        e.setDetPO(i-1,po);
        e.setDetLine(i-1,getInput(i,"POC",1));
       
        e.setDetDesc(i-1,getInput(i,"POC:PID",5));

        //override incoming UOM with what is available in UOM Maintenance
        if (getInput(i,"POC",5).equals("CS")) {
            uom = "CA";
        } else {
         uom = OVData.getUOMByItem(item);
        }

        if (uom.isBlank()) {
        uom = getInput(i,"POC",5);  // take whatever is there
        }

        e.setDetUOM(i-1,uom);

        if (useInternalPrice) {
        listprice = invData.getItemPriceFromCust(e.getOVBillTo(), item, uom, cusData.getCustCurrency(e.getOVBillTo()),"LIST",getInput(i,"POC",6));
        discount = invData.getItemDiscFromCust(e.getOVBillTo());
        netprice = listprice;
        if (discount != 0) {
        netprice = listprice - (listprice * (discount / 100));
        }
        e.setDetNetPrice(i-1,formatNumber(netprice,"2"));
        e.setDetListPrice(i-1,formatNumber(listprice,"2"));
        e.setDetDisc(i-1,formatNumber(discount,"2"));
        } else {
         if (isNumber(getInput(i,"POC",6))) {
            e.setDetNetPrice(i-1, formatNumber(getInput(i,"POC",6),"3"));
            e.setDetListPrice(i-1, formatNumber(getInput(i,"POC",6),"3"));
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
    processDB(c,com.blueseer.edi.EDI.createSOCFrom860(e, c), null);


