MACHINE DefSetTest3
// here a counter-example can be found if DS has at least two elements
SETS DS 
VARIABLES x, y, z
INVARIANT x<2 & y:{0,1} & z:{0,1}
INITIALISATION x:=0 || y:= 0 || z:=0
OPERATIONS
 inc1 = SELECT #(a,b).(a:DS & b:DS & a/=b) & y=0 THEN x:=x+1 || y := 1 END;
 inc2 = SELECT #(a).(a:DS) & z=0 THEN x:=x+1 || z := 1 END
END
