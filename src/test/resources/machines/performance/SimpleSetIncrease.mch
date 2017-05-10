MACHINE SimpleSetIncrease
VARIABLES myset, xx
INVARIANT myset <: 1..1000 & xx< 10000 
INITIALISATION myset := {} || xx:= 1
OPERATIONS
  incset = BEGIN myset := myset \/ { xx } || xx:= xx*2 END
END
