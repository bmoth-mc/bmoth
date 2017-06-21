MACHINE SetVarToConstantViolation
CONSTANTS
  c
PROPERTIES
  c = 1
VARIABLES
  x
INVARIANT
  x = 0
INITIALISATION
  x := 0
OPERATIONS
  copy_from_constant = BEGIN x:=c END
END