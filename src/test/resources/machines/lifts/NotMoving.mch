MACHINE NotMoving
CONSTANTS
  MAX_FLOOR, MIN_FLOOR
PROPERTIES
  MAX_FLOOR = 3
& MIN_FLOOR = -1
VARIABLES
  current_floor
, target_floor
, next_floor
, changed_flag
INVARIANT
  current_floor : INTEGER
& current_floor >= MIN_FLOOR 
& current_floor <= MAX_FLOOR
& target_floor : INTEGER
& target_floor >= MIN_FLOOR 
& target_floor <= MAX_FLOOR

& (((target_floor /= current_floor) & (changed_flag = 1)) => (next_floor /= current_floor))
INITIALISATION
   current_floor := 1
|| changed_flag := 0
OPERATIONS
  SetChangedFlag =
    SELECT changed_flag = 0
    THEN
      changed_flag := 1
    END
;
  SetTargetFloor =
    ANY newFloor
    WHERE
      newFloor >= -1
      & newFloor <= 3
    THEN
      target_floor := newFloor
    END
;
  PrepareMoveUp =
    ANY newFloor
    WHERE
      newFloor >= -1
      & newFloor <= 3
      & newFloor = current_floor
    THEN
      current_floor := newFloor
      || next_floor := newFloor
    END
; 
  PrepareMoveDown =
    ANY newFloor
    WHERE
      newFloor >= -1
      & newFloor <= 3
      & newFloor = current_floor
    THEN
      current_floor := newFloor
      || next_floor := newFloor
    END
;
  Move = 
      current_floor := next_floor
      || changed_flag := 1
END
