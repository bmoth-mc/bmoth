MACHINE LiftDoesNotMoveTowardsFirstPressedButton
SETS MOVING_DIRECTION = {UP, DOWN, HOLD}
CONSTANTS
  MAX_FLOOR, MIN_FLOOR
PROPERTIES
  MAX_FLOOR = 5
& MIN_FLOOR = 0
VARIABLES
  current_floor
, moving
, pressed_buttons
INVARIANT
  current_floor : INTEGER
& current_floor >= MIN_FLOOR
& current_floor <= MAX_FLOOR
& moving : MOVING_DIRECTION
& pressed_buttons : iseq(MIN_FLOOR..MAX_FLOOR)
& (pressed_buttons /= [] & first(pressed_buttons) > current_floor => moving /= DOWN)
& (pressed_buttons /= [] & first(pressed_buttons) < current_floor => moving /= UP)
INITIALISATION
  current_floor := 1
|| moving := HOLD
|| pressed_buttons := []
OPERATIONS
  PressButton =
    ANY button
    WHERE
      button : MIN_FLOOR..MAX_FLOOR
      & button /: ran(pressed_buttons)
    THEN
      pressed_buttons := pressed_buttons <- button
    END
;
  StartMovingUp =
    SELECT
      moving = HOLD
      & pressed_buttons /= []
    THEN
      moving := UP
    END
;
  StartMovingDown =
    SELECT
      moving = HOLD
      & pressed_buttons /= []
    THEN
      moving := DOWN
    END
;
  StopMoving =
    SELECT
      moving : {UP, DOWN}
    THEN
      moving := HOLD
    END
;
  MoveUp =
    SELECT
      moving = UP
      & current_floor + 1 <= MAX_FLOOR
    THEN
      current_floor := current_floor + 1
    END
;
  MoveDown =
    SELECT
      moving = DOWN
      & current_floor - 1 >= MIN_FLOOR
    THEN
      current_floor := current_floor - 1
    END

END
