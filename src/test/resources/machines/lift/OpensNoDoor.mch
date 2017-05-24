MACHINE OpensNoDoor

VARIABLES
	doorsOpen, x, doorsBeenOpen
INVARIANT
	doorsOpen: BOOL &
	doorsBeenOpen: BOOL &
	x:INTEGER &
	x<60 &
	(x>50 => doorsBeenOpen=TRUE)
INITIALISATION
	doorsOpen, doorsBeenOpen := FALSE, FALSE ||
	x:=0
OPERATIONS
	Close = SELECT x<59 THEN doorsOpen := FALSE || x:=x+1 END

END