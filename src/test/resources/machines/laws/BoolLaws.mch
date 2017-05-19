
MACHINE BoolLaws
 /* completely proven with AtelierB 4.0 */
CONSTANTS
   TT,FF
PROPERTIES
   FF:BOOL & FF=FALSE & TT:BOOL & TT=TRUE
VARIABLES
   PP, QQ, RR
INVARIANT
  PP:BOOL & QQ:BOOL & RR:BOOL &
  
  /* some laws about the BOOL datatype */
  (PP=TRUE or PP=FALSE) &
  not(PP=TRUE & PP=FALSE) &
  (PP=TRUE <=> PP/=FALSE) &
  (PP=FALSE <=> PP/=TRUE) &
  (PP/=QQ => (PP=TRUE <=> QQ=FALSE)) &

   /* Truth tables */
   ((TT=TT) <=> (TT=TT)) & ((TT=FF) <=> (TT=FF)) & not((TT=TT) <=> (TT=FF)) & not((TT=FF) <=> (TT=TT)) &
   ((TT=TT) => (TT=TT)) & ((TT=FF) => (TT=FF)) & not((TT=TT) => (TT=FF)) & ((TT=FF) => (TT=TT)) &
   ((TT=TT) or (TT=TT)) & not((TT=FF) or (TT=FF)) & ((TT=TT) or (TT=FF)) & ((TT=FF) or (TT=TT)) &
   ((TT=TT) & (TT=TT)) & not((TT=FF) & (TT=FF)) & not((TT=TT) & (TT=FF)) & not((TT=FF) & (TT=TT)) &

   /* Rules from Gallier Logic book, 2003, pages 54 & 55 */
   /* Laws of zero or one: */
   (((PP=TRUE) & (TT=FF)) <=> (TT=FF)) & 
   (((PP=TRUE) or (TT=FF)) <=> (PP=TRUE)) &  
   (((PP=TRUE) or (TT=TT)) <=> (TT=TT)) &  
   (((PP=TRUE) & (TT=TT)) <=> (PP=TRUE)) &  
   ((PP=TRUE) or not((PP=TRUE))) &
    not((PP=TRUE) & not((PP=TRUE))) & 

   /* Associativity rules */
   ((((PP=TRUE) or (QQ=TRUE)) or (RR=TRUE)) <=> ((PP=TRUE) or ((QQ=TRUE) or (RR=TRUE)))) &
   ((((PP=TRUE) & (QQ=TRUE)) & (RR=TRUE)) <=> ((PP=TRUE) & ((QQ=TRUE) & (RR=TRUE)))) &

   /* Commutativity rules */
   (((PP=TRUE) & (QQ=TRUE)) <=> ((QQ=TRUE) & (PP=TRUE))) &
   (((PP=TRUE) or (QQ=TRUE)) <=> ((QQ=TRUE) or (PP=TRUE))) &
   (((PP=TRUE) <=> (QQ=TRUE)) <=> ((QQ=TRUE) <=> (PP=TRUE))) &

   /* Double negation rule */
   (((PP=TRUE)) <=> (not(not((PP=TRUE))))) &

   /* Absorption rules */
   (((QQ=TRUE) or ((QQ=TRUE) & (PP=TRUE))) <=> (QQ=TRUE)) &
   (((QQ=TRUE) & ((QQ=TRUE) or (PP=TRUE))) <=> (QQ=TRUE)) &

   /* Idempotentcy rules */
   ( ((PP=TRUE) or (PP=TRUE)) <=> (PP=TRUE)) &
   ( ((PP=TRUE) & (PP=TRUE)) <=> (PP=TRUE)) &
    /* again using bool this time */
   ( bool((PP=TRUE) or (PP=TRUE)) = bool((PP=TRUE))) &
   ( bool((PP=TRUE) & (PP=TRUE)) = bool((PP=TRUE))) &

   /* Dis(TT=TT)ibutivity rules */
   ( ((PP=TRUE) or ((QQ=TRUE) & (RR=TRUE))) <=> ( ((PP=TRUE) or (QQ=TRUE)) & ((PP=TRUE) or (RR=TRUE)) ) ) &
   ( ((PP=TRUE) & ((QQ=TRUE) or (RR=TRUE))) <=> ( ((PP=TRUE) & (QQ=TRUE)) or ((PP=TRUE) & (RR=TRUE)) ) ) &
    /* again using bool this time */
   ( bool((PP=TRUE) or ((QQ=TRUE) & (RR=TRUE))) = bool( ((PP=TRUE) or (QQ=TRUE)) & ((PP=TRUE) or (RR=TRUE)) ) ) &
   ( bool((PP=TRUE) & ((QQ=TRUE) or (RR=TRUE))) = bool( ((PP=TRUE) & (QQ=TRUE)) or ((PP=TRUE) & (RR=TRUE)) ) ) &

   /* De Morgan's rules */ 
   (not((PP=TRUE) or (QQ=TRUE)) <=> (not((PP=TRUE)) & not((QQ=TRUE))) ) &
   (not((PP=TRUE)  & (QQ=TRUE)) <=> (not((PP=TRUE)) or not((QQ=TRUE))) ) &
    /* again using bool this time */
   bool(not((PP=TRUE) or (QQ=TRUE)) <=> (not((PP=TRUE)) & not((QQ=TRUE))) )=TRUE &
   bool(not((PP=TRUE) or (QQ=TRUE))) = bool(not((PP=TRUE)) & not((QQ=TRUE)))  &
   bool(not((PP=TRUE)  & (QQ=TRUE)) <=> (not((PP=TRUE)) or not((QQ=TRUE))) )=TRUE &
   bool(not((PP=TRUE)  & (QQ=TRUE))) = bool(not((PP=TRUE)) or not((QQ=TRUE)))  &

 
   /* some unamed rules about => and <=> */
   ( ((PP=TRUE) => (QQ=TRUE)) <=> (not((PP=TRUE)) or (QQ=TRUE)) ) &
   ( (PP=TRUE) <=> (PP=TRUE) ) &
   ((((PP=TRUE) => (QQ=TRUE)) & ((QQ=TRUE) => (RR=TRUE))) => ((PP=TRUE) => (RR=TRUE))) & /* Modus pones */
   ((((PP=TRUE) <=> (QQ=TRUE)) & ((QQ=TRUE) <=> (RR=TRUE))) => ((PP=TRUE) <=> (RR=TRUE))) &
   (((PP=TRUE) <=> (QQ=TRUE)) => ((PP=TRUE) => (QQ=TRUE))) &
   (((PP=TRUE) <=> (QQ=TRUE)) => ((QQ=TRUE) => (PP=TRUE))) &
   (((PP=TRUE) <=> (QQ=TRUE)) <=> (((PP=TRUE) => (QQ=TRUE)) & ((QQ=TRUE) => (PP=TRUE)))) &
   (bool((PP=TRUE) <=> (QQ=TRUE)) = bool(((PP=TRUE) => (QQ=TRUE)) & ((QQ=TRUE) => (PP=TRUE)))) &

   ( (PP=TRUE) => (QQ=TRUE) or (PP=TRUE) ) &
   ( ((PP=TRUE)=>(QQ=TRUE)) or (PP=TRUE) ) & ( (PP=TRUE) => ((QQ=TRUE) or (PP=TRUE)) ) &

   /* TRUE/False: */
   (TT=TT) &
   not((TT=FF)) &

   /* Some rules about bool */
   ( ((PP=TRUE) <=> (QQ=TRUE))  <=>  (bool((PP=TRUE)) = bool((QQ=TRUE))) ) &
   ( not((PP=TRUE) <=> (QQ=TRUE))  <=>  (bool((PP=TRUE)) /= bool((QQ=TRUE))) ) &
   (bool((PP=TRUE) or not((PP=TRUE))) = TRUE ) &
   (bool((PP=TRUE) & not((PP=TRUE))) = FALSE ) &
   (bool((PP=TRUE))=PP & bool((QQ=TRUE))=QQ ) &

   /* Some tautologies */
   ( (PP=TRUE) => ((QQ=TRUE) => (PP=TRUE)) ) &
   ( (PP=TRUE) => ((QQ=TRUE) => ((PP=TRUE) & (QQ=TRUE)))) &
   ( ((PP=TRUE) & (QQ=TRUE)) => (PP=TRUE) ) &  ( ((PP=TRUE) & (QQ=TRUE)) => (QQ=TRUE) ) &
   ( not(not((QQ=TRUE))) => (QQ=TRUE)) &
   ( ((PP=TRUE) => (QQ=TRUE)) => ( ((QQ=TRUE)=>(PP=TRUE)) => ((QQ=TRUE)<=>(PP=TRUE)) ) )
INITIALISATION

   PP,QQ,RR := FALSE,FALSE,FALSE

OPERATIONS
set_pp = BEGIN PP:=TRUE END;
set_qq = BEGIN QQ:=TRUE END;
set_rr = BEGIN RR:=TRUE END

END
