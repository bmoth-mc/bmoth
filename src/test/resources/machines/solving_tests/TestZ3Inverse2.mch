MACHINE TestZ3Inverse2
 /* check whether Z3 can invert f and apply it */
CONSTANTS
 f 
PROPERTIES
 f = {1|->10, 2|->20}
VARIABLES x
INVARIANT
  x : 1..2
INITIALISATION
  x := 1
OPERATIONS
  setx2(y) = PRE y:1..2 & y = f~(20) THEN x:= y END
END