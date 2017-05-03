MACHINE TestMachine
CONSTANTS
  MAX_FLOOR, MIN_FLOOR
, FLOORS
PROPERTIES
  MAX_FLOOR = 5
& MIN_FLOOR = 0
& FLOORS = MIN_FLOOR..MAX_FLOOR
VARIABLES
  current_floor
, moving
, doors_open
INVARIANT
  current_floor : INTEGER
& current_floor : MIN_FLOOR..MAX_FLOOR
& moving : BOOL
& doors_open : BOOL

& (doors_open = TRUE => moving = FALSE)
INITIALISATION
  current_floor := 1
|| moving := FALSE
|| doors_open := FALSE
OPERATIONS
  CloseDoors =
    BEGIN doors_open := FALSE END
;
  OpenDoors =
    SELECT
      moving = FALSE
    THEN
      doors_open := TRUE
    END
;
  StartMoving =
    SELECT
      doors_open = FALSE
    THEN
      moving := TRUE
    END
;
  Move =
    ANY newFloor
    WHERE
      newFloor : 0..5
      & moving = TRUE
    THEN
      current_floor := newFloor
    END
END
