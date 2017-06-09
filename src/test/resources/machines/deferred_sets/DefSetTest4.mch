MACHINE DefSetTest4
// here no counter-example should be found, as inc1 can only be executed if inc2 cannot and vice versa
SETS DS 
VARIABLES x, y, z
INVARIANT x<2 & y:{0,1} & z:{0,1}
INITIALISATION x:=0 || y:= 0 || z:=0
OPERATIONS
 inc1 = SELECT #(a,b).(a:DS & b:DS & a/=b) & y=0 THEN x:=x+1 || y := 1 END;
 inc2 = SELECT #(a).(a:DS & !b.(b:DS => a=b)) & z=0 THEN x:=x+1 || z := 1 END
END
