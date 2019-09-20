chan c[3] = [10] of { byte }
//chan c1 = [10] of { byte }
//chan c = [10] of { byte }
bool recieved_c
bool sent_c1
	
	active proctype main()
   {
   		run p(1,2)
   		run p(2,0)
   		byte i = 1
	c[1]!i
	sent_c1 = true
   	byte v
S1:	if
 	:: c[0]?v  -> goto SEXIT
 	fi
 	goto S1
 	
 SEXIT:	
 	recieved_c = true
 }
 
	proctype p(byte cin, cout)
   {
   	byte v
S1:	if
 	:: c[cin]?v  -> c[cout]!v+1; goto SEXIT
 	fi
 	goto S1
 	SEXIT:
 }
 
 ltl p1 { (<> recieved_c) }