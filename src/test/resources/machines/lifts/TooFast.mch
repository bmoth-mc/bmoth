MACHINE TooFast

VARIABLES
	floor, x, v, wantedAt
INVARIANT
	floor : INTEGER &
	floor: 0..5 &
	wantedAt: INTEGER &
	wantedAt: 0..5 &
	x:INTEGER &
/* x are microsteps between floors, here 10 steps each floor */
	v: INTEGER &
	v: -10..10
	
INITIALISATION
	floor:=0 ||
	x:=0 ||
	v := 0
OPERATIONS
	LiftTo0 = SELECT v=0 THEN wantedAt:=0 END;
	LiftTo1 = SELECT v=0 THEN wantedAt:=1 END;
	LiftTo2 = SELECT v=0 THEN wantedAt:=2 END;
	LiftTo3 = SELECT v=0 THEN wantedAt:=3 END;
	LiftTo4 = SELECT v=0 THEN wantedAt:=4 END;
	LiftTo5 = SELECT v=0 THEN wantedAt:=5 END;
	StartMovingUp = SELECT (wantedAt-floor>0 & v=0 & x=0) THEN v:=1 END;
//	StartMovingDown = SELECT wantedAt-floor<0 & v=0 & x=0 THEN  v := -1 END;
	Accelerate = SELECT v>0 & wantedAt>floor & ((wantedAt-floor)*10 -x-5*v) >0
		THEN v:=2*v || x:=x+v END; 
	Decelerate = SELECT v>1 & wantedAt>floor & ((wantedAt-floor)*10 -x-5*v) <=0
		 THEN v:=v/2 || x:=x+v END;
	SlowMove = SELECT v=1 & 10*(wantedAt-floor)-x /=0 & 10*(wantedAt-floor)-x <3
		THEN v:=1 || x:=x+v END;
	Halt = SELECT v=1 & 10*(wantedAt-floor)=0 THEN floor:=wantedAt || x:=0 || v:=0 END

END