MACHINE PathTestMachine
VARIABLES
  x
INVARIANT
  x : NATURAL & x > 0 & x < 11 & x mod 10 /= 0
INITIALISATION
  x := 5
OPERATIONS
  Inc = SELECT x < 10 THEN x := x + 1 END;
  Dec = SELECT x > 1 THEN x := x -1 END
END
