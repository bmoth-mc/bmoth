MACHINE CounterErr2
// this still seems to take very long for xx<5000 (taking a few minutes on MacBook Air);
// for xx < 3000 it is still quite fast (a few seconds)
VARIABLES xx
INVARIANT xx>=0 &
          xx < 5000 /* will be violated */
INITIALISATION xx:=0
OPERATIONS
 inc = BEGIN xx:=xx+1 END;
 inc2 = BEGIN xx:=xx+2 END;
 dec = SELECT xx>0 THEN xx:= xx-1 END;
 dec2 = SELECT xx>1 THEN xx:= xx-2 END
END
