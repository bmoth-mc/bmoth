MACHINE SetVarToConstantNoViolation
CONSTANTS
  c
PROPERTIES
  c = 1
VARIABLES
  x, y
INVARIANT
  x : {0,1}
& y = 1
INITIALISATION
  x := 0
|| y := 1
OPERATIONS
  copy_from_constant = BEGIN x:=c || y:=1 END
END
