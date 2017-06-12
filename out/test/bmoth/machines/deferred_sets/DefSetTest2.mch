MACHINE DefSetTest2
// check that no counter example
SETS DS
VARIABLES x, y, z
INVARIANT x<2 & y:{0,1} & z:{0,1}
INITIALISATION x:=0 || y:= 0 || z:=0
OPERATIONS
 inc1 = SELECT card(DS)=1 & y=0 THEN x:=x+1 || y := 1 END;
 inc2 = SELECT card(DS)=2 & z=0 THEN x:=x+1 || z := 1 END
END
