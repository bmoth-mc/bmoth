MACHINE CounterErr
VARIABLES xx
INVARIANT xx>=0 &
          xx < 500 /* will be violated */
INITIALISATION xx:=0
OPERATIONS
 inc = BEGIN xx:=xx+1 END;
 dec = SELECT xx>0 THEN xx:= xx-1 END
END
