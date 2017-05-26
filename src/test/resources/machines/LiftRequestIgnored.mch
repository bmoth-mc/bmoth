MACHINE LiftRequestIgnored
VARIABLES
  current_floor, moving, doors_open, requested, requested_floor
INVARIANT
  current_floor : INTEGER & current_floor : 0..5
& requested_floor : 0..5
& requested : BOOL
& moving : BOOL
& doors_open : BOOL
& (requested = FALSE => current_floor = requested_floor)
INITIALISATION
  current_floor := 1 || moving := FALSE || doors_open := FALSE || requested := FALSE || requested_floor := 1
OPERATIONS
  CloseDoors = BEGIN doors_open := FALSE END;
  OpenDoors = SELECT moving = FALSE THEN doors_open := TRUE END;
  StartMoving = SELECT doors_open = FALSE THEN moving := TRUE END;
  StopMoving  = BEGIN moving := FALSE END;
  Request = ANY floor WHERE floor : 0..5 & moving = TRUE
            THEN requested_floor := floor || requested := TRUE END;
  Move = ANY floor WHERE floor : 0..5 & moving = TRUE & requested = TRUE
         THEN current_floor := floor || requested := FALSE || moving := FALSE END
END
