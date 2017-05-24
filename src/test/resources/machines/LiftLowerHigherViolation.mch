MACHINE LiftHigherViolation
VARIABLES
  current_floor, moving, doors_open
INVARIANT
  current_floor : INTEGER & current_floor : 0..5
& moving : BOOL
& doors_open : BOOL
INITIALISATION
  current_floor := 1 || moving := FALSE || doors_open := FALSE
OPERATIONS
  CloseDoors = BEGIN doors_open := FALSE END;
  OpenDoors = SELECT moving = FALSE THEN doors_open := TRUE END;
  StartMoving = SELECT doors_open = FALSE THEN moving := TRUE END;
  StopMoving  = BEGIN moving := FALSE END;
  Move_up = SELECT moving = TRUE THEN current_floor := current_floor + 1 END;
  Move_down = SELECT moving = TRUE THEN current_floor := current_floor - 1 END
END
