MACHINE OpenDoorWhileMoving

VARIABLES
  moving
, doors_open

INVARIANT
  moving : BOOL
& doors_open : BOOL
& (doors_open = TRUE => moving = FALSE)

INITIALISATION
   moving := FALSE
|| doors_open := FALSE
OPERATIONS
  CloseDoors =
  	SELECT
      moving = FALSE
    THEN
      doors_open := FALSE
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
    BEGIN
      moving := TRUE
    END

END
