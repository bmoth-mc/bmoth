MACHINE EmergencyCallFail

VARIABLES
  moving
, doors_open
, emergency_state
, emergency_call
, changed_flag

INVARIANT
  moving : BOOL
& doors_open : BOOL

& (((emergency_state = 1) & (changed_flag = 0)) => (emergency_call = 1))
INITIALISATION
   moving := FALSE
|| doors_open := FALSE
|| emergency_state := 0
|| emergency_call := 0
|| changed_flag := 0
OPERATIONS
  SetChangedFlag =
    SELECT
      changed_flag = 1
    & emergency_state = 0
    THEN
      changed_flag := 0
    END
;
  EmergencyCall =
    SELECT
      emergency_state = 1
    THEN
       emergency_call := 0
    || changed_flag := 0
    END
;
  CloseDoors =
    SELECT
      changed_flag = 0
    & moving = FALSE
    THEN
       doors_open := FALSE
    || changed_flag := 1
    END
;
  OpenDoors =
    SELECT
      changed_flag = 0
    & moving = FALSE
    THEN
      doors_open := TRUE
    || changed_flag := 1
    END
;
  StartMoving =
    SELECT 
       changed_flag = 0
    THEN
       moving := TRUE
    || changed_flag := 1
    END
;
  StartMovingOpenDoors =
    SELECT 
      changed_flag = 0
    & doors_open = TRUE
    THEN
       moving := TRUE
    || emergency_state := 1
    || changed_flag := 1
    END

END