MACHINE FastClosingDoors

CONSTANTS
  MAX_SPEED
PROPERTIES
  MAX_SPEED = 10
VARIABLES
  doors_open
, doors_speed

INVARIANT
  doors_open : BOOL
& doors_speed <= MAX_SPEED

INITIALISATION
   doors_open := FALSE
|| doors_speed := 5

OPERATIONS
  CloseDoors =
    BEGIN
      doors_open := FALSE
    END
;
  OpenDoors =
    BEGIN
      doors_open := TRUE
    END
;
  SlowDownDoors =
    SELECT
      doors_speed > 0
    THEN
      doors_speed := doors_speed - 1
    END
;    
  SpeedUpDoors =
    BEGIN
      doors_speed := doors_speed +1
    END

END
