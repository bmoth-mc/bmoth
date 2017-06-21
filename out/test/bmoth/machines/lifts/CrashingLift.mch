MACHINE CrashingLift
VARIABLES
  current_time, old_time,
  current_floor, old_floor
INVARIANT
  current_time : INTEGER & current_floor : 0..3 &
  old_time : INTEGER & old_floor : 0..3 &
  current_time - old_time >= old_floor - current_floor
INITIALISATION
  current_time := 0  || old_time := 0 ||
  current_floor := 0 || old_floor := 0
OPERATIONS
  move_up =
    SELECT current_floor < 3
    THEN old_floor := current_floor ||
         old_time := current_time ||
         current_time := current_time + 1 ||
         current_floor := current_floor + 1
    END;
  move_down =
    SELECT current_floor > 0
    THEN old_floor := current_floor ||
         old_time := current_time ||
         current_time := current_time + 1 ||
         current_floor := current_floor - 1
    END;
  crash =
    SELECT current_floor = 3
    THEN old_floor := current_floor ||
         old_time := current_time ||
         current_time := current_time + 1 ||
         current_floor := 0
    END
END