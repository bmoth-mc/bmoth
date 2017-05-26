MACHINE LiftStopsAtInvalidPosition
CONSTANTS
  MAX_FLOOR, MIN_FLOOR
PROPERTIES
  MAX_FLOOR = 5
& MIN_FLOOR = 0
VARIABLES
  current_floor
, moving
, doors_open
, betweenFloors
INVARIANT
  current_floor : INTEGER
& current_floor >= MIN_FLOOR
& current_floor <= MAX_FLOOR
& moving : BOOL
& doors_open : BOOL

& (doors_open = TRUE => moving = FALSE)
& betweenFloors : BOOL
& (moving = FALSE => betweenFloors = FALSE)
INITIALISATION
  current_floor := 1
|| moving := FALSE
|| doors_open := FALSE
|| betweenFloors := FALSE
OPERATIONS
  CloseDoors =
    BEGIN doors_open := FALSE
    END
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
  StopMoving =
    SELECT
      moving = TRUE
    THEN
      moving := FALSE
    END
;
  MoveBetweenFloors =
    SELECT
      moving = TRUE
      & betweenFloors = FALSE
    THEN
      betweenFloors := TRUE
    END
;
  Move =
    ANY p
    WHERE
      p : {current_floor + 1, current_floor -1}
      & p >= MIN_FLOOR
      & p <= MAX_FLOOR
      & moving = TRUE
      & betweenFloors = TRUE
    THEN
      current_floor := p
      || betweenFloors := FALSE
    END
END
