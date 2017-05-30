MACHINE SimpleMachine
VARIABLES
  x,y
INVARIANT
  x : NATURAL & x < 899 &
  y : NATURAL & y < 899
INITIALISATION
  x := 9 
  y := 1
OPERATIONS
  Inc = SELECT x < 1000 THEN x := x + 1 END;
  Dec = SELECT x > 1 THEN x := x -1 END;
  IncY = SELECT y < 1000 THEN y := y + 1 END;
  DecY = SELECT y > 1 THEN y := y -1 END
END
