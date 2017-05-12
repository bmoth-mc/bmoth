MACHINE DecOneOfTwoNoViolation
VARIABLES
  x, y
INVARIANT
  x : NATURAL & x > 5 & x < 10
  & y : NATURAL & y > 0 & y < 15 & y >= x
INITIALISATION
  x := 7
  || y := 8
OPERATIONS
  Dec = SELECT y > x THEN y := y - 1 || x := x END;
  Inc = SELECT y < 14 THEN y := y + 1 || x := x END
END
