MACHINE SimpleMachine
VARIABLES
  x
INVARIANT
  x : NATURAL & x > 8 & x < 10
INITIALISATION
  x := 9
OPERATIONS
  Inc = SELECT x < 10 THEN x := x + 1 END;
  Dec = SELECT x > 1 THEN x := x -1 END
END