MACHINE AccMachine
CONSTANTS
  MAX_SPEED, MIN_SPEED, MAX_ACC, MIN_ACC
PROPERTIES
  MAX_SPEED = 5
& MIN_SPEED = -5
& MAX_ACC = 2
& MIN_ACC = -2
VARIABLES
  current_speed
, previous_speed
, current_acceleration
, changed_flag
INVARIANT
  current_speed : INTEGER
& previous_speed : INTEGER
& current_speed >= MIN_SPEED
& current_speed <= MAX_SPEED
& current_acceleration >= MIN_ACC
& current_acceleration <= MAX_ACC

& ((changed_flag = 1) => (current_acceleration < 1))
& ((changed_flag = 1) => (current_acceleration > -1))
INITIALISATION
   current_speed := 0
|| previous_speed := 0
|| current_acceleration := 0
|| changed_flag := 0
OPERATIONS
  SetChangedFlag =
    SELECT changed_flag = 1
    THEN
      changed_flag := 0
    END
;
  AccelerateUp =
    ANY newSpeed
    WHERE
      newSpeed >= current_speed
      & newSpeed-current_speed <= MAX_ACC
      & changed_flag = 0
    THEN
      previous_speed := current_speed
      || current_speed := newSpeed
      || current_acceleration := current_speed-previous_speed
      || changed_flag := 1
    END
;
  AccelerateDown =
    ANY newSpeed
    WHERE
      newSpeed <= current_speed
      & current_speed-newSpeed <= MIN_ACC
      & changed_flag = 0
    THEN
      previous_speed := current_speed
      || current_speed := newSpeed
      || current_acceleration := previous_speed-current_speed
      || changed_flag := 1
    END
END
