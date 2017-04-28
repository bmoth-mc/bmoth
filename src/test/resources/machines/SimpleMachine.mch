MACHINE SimpleMachine
VARIABLES
  x
INVARIANT
  x : INTEGER & x : 1..10 & x /= 1
INITIALISATION
  x := 1
OPERATIONS
  Inc = SELECT x < 10 THEN x := x + 1 END;
  Dec = SELECT x > 1 THEN x := x -1 END
END