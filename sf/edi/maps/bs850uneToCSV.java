// start mapping

// number of rows created equals number of line items
// header info is repeated for each item 

var linecount = getGroupCount("LIN");
for (int i = 1; i <= linecount; i++) {
mapSegment("ROW","f1",getInput("BGM",2));
mapSegment("ROW","f2",getInputComp("NAD","1:ST",2,1));
mapSegment("ROW","f3",getInput("NAD","1:ST",4));
mapSegment("ROW","f4",getInputComp("NAD","1:BY",2,1));
mapSegment("ROW","f5",getInput("NAD","1:BY",4));
mapSegment("ROW","f6",getInputComp(i,"LIN",3,1));
mapSegment("ROW","f7","");
mapSegment("ROW","f8",getInputComp(i,"LIN:QTY",1,2));
mapSegment("ROW","f9",getInputComp(i,"LIN:QTY",1,3));
mapSegment("ROW","f10",getInputComp(i,"LIN:PRI",1,2));
mapSegment("ROW","f11",getInputComp(i,"LIN:IMD",3,4));
commitSegment("ROW");
}

// end mapping



