MACHINE DecOneOfTwoViolation
VARIABLES
  x, y
INVARIANT
  x : NATURAL 
  & y : NATURAL & y > x
INITIALISATION
  x := 7
  || y := 8
OPERATIONS
  Dec = BEGIN y := y - 1 || x := x END
END
