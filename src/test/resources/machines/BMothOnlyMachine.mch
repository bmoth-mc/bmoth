MACHINE BMothOnly
VARIABLES
    v
INVARIANT
    not(v = 5)
INITIALISATION
    v := 1
OPERATIONS
    op = ANY y,x WHERE x < y & y < x THEN v := 5 END
END