// start mapping

// number of rows created equals number of line items
// header info is repeated for each item 

double totamt = 0.00;
var linecount = getGroupCount("IT1");
for (int i = 1; i <= linecount; i++) {
totamt = 0.00;
mapSegment("ROW","f1",getInput("BIG",2));
mapSegment("ROW","f2",getInput("BIG",4));
mapSegment("ROW","f3",getInput("BIG",1));
mapSegment("ROW","f4",getInput("BIG",3));
mapSegment("ROW","f5",getInput("N1","1:BT",4));
mapSegment("ROW","f6",getInput("N1","1:BT",2));
mapSegment("ROW","f7",getInput("N1","1:RE",4));
mapSegment("ROW","f8",getInput("N1","1:RE",2));
mapSegment("ROW","f9","");
mapSegment("ROW","f10",getInput("ITD",12));
mapSegment("ROW","f11",getInput("ITD",4));
mapSegment("ROW","f12",getInput("REF","1:MB",2));
mapSegment("ROW","f13",getInput(i,"IT1",7));
mapSegment("ROW","f14",getInput(i,"IT1",9));
mapSegment("ROW","f15",getInput(i,"IT1",2));
mapSegment("ROW","f16",getInput(i,"IT1",4));
totamt += ( Double.valueOf(getInput(i,"IT1",2)) * Double.valueOf(getInput(i,"IT1",4)));
mapSegment("ROW","f17",formatNumber(totamt,"2"));
commitSegment("ROW");
}

// end mapping
