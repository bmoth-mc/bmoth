MACHINE FastDoors
CONSTANTS
	MAXPOS, MOVEMENT_STEP, TICK
PROPERTIES
	MAXPOS = 10
	& MOVEMENT_STEP = 5
	& TICK = 1
VARIABLES
	doorPos, clock, isOpen, isClosing, isClosed
INVARIANT
	doorPos : 0..MAXPOS & clock : NAT & isOpen : BOOL & isClosing : BOOL & isClosed : BOOL
	// prevent problems for clock > 10
	& MAXINT = 20
	// ensure we can reach 0 or MAXPOS in integral steps
	& MAXPOS mod MOVEMENT_STEP = 0
	// only one phase at a time is allowed
	& (	(isOpen & not(isClosing or isClosed))
		or (isClosing & not(isOpen or isClosed))
		or (isClosed & not(isOpen or isClosing)))
	// timing constraint
	& (	(isClosed & clock > 4)
		or not(isClosed))
INITIALISATION
	doorPos := MAXPOS
	|| isOpen := TRUE
	|| isClosing := FALSE
	|| isClosed := FALSE
OPERATIONS
closeStart = SELECT isOpen = TRUE THEN
		// unlock door
		isOpen := FALSE
		// initiate movement
		|| isClosing := TRUE
		// reset clock
	END;
close = SELECT isClosing = TRUE & doorPos > 0 THEN
		// move door
		doorPos := doorPos - MOVEMENT_STEP
		// time goes by...
		|| clock := clock + TICK
	END;
closeEnd = SELECT isClosing = TRUE & doorPos = 0 THEN
		// stop movement
		isClosing := FALSE
		// lock door
		|| isClosed := TRUE
	END
END
