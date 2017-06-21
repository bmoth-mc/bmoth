MACHINE LiftNotMoving
VARIABLES
  current_floor, moving, doors_open, requested, requested_floor
INVARIANT
  current_floor : INTEGER & current_floor : 0..5
& requested_floor : 0..5
& requested : BOOL
& moving : BOOL
& doors_open : BOOL
& (requested = TRUE & doors_open = FALSE => moving = TRUE)
INITIALISATION
  current_floor := 1 || moving := FALSE || doors_open := TRUE || requested := FALSE || requested_floor := 1
OPERATIONS
  StartMoving = SELECT moving = FALSE & requested = TRUE
                THEN doors_open := FALSE END;
  StopMoving  = SELECT moving = TRUE & current_floor = requested_floor
                THEN moving := FALSE || doors_open := TRUE END;
  Request = ANY floor WHERE floor : 0..5 & moving = FALSE
            THEN requested_floor := floor || requested := TRUE END;
  Move = SELECT moving = TRUE
         THEN current_floor := requested_floor || requested := FALSE END
END
