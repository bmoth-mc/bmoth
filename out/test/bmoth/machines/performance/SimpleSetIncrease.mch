MACHINE SimpleSetIncrease
VARIABLES myset, xx
INVARIANT myset <: 1..5 & xx < 5
INITIALISATION myset := {} || xx:= 1
OPERATIONS
  incset = BEGIN myset := myset \/ { xx } || xx:= xx*2 END
END
