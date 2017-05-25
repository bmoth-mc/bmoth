MACHINE TargetAndCurrentCorrespond
CONSTANTS
  MAX_FLOOR, MIN_FLOOR
PROPERTIES
  MAX_FLOOR = 3
& MIN_FLOOR = -1
VARIABLES
  current_floor
, target_floor
INVARIANT
  current_floor : INTEGER
& current_floor >= MIN_FLOOR 
& current_floor <= MAX_FLOOR
& target_floor >= MIN_FLOOR 
& target_floor <= MAX_FLOOR

& (((target_floor > 0) & (target_floor /= current_floor))  => (current_floor > 0))
INITIALISATION
  current_floor := 1
OPERATIONS
  SetTargetFloor=
    ANY newFloor
    WHERE
      newFloor >= -1
      & newFloor <= 3
    THEN
      target_floor := newFloor
    END
;
  MoveUp =
    ANY newFloor
    WHERE
      newFloor >= -1
      & newFloor <= 3
      & current_floor /= target_floor
      & newFloor-current_floor > 0
    THEN
      current_floor := newFloor
    END
; 
  MoveDown =
    ANY newFloor
    WHERE
      newFloor >= -1
      & newFloor <= 3
      & current_floor /= target_floor
      & current_floor-newFloor > 0
    THEN
      current_floor := newFloor
    END
END
