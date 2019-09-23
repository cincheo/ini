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
 
 
 /////////////////
 
 chan c0 = [10] of { byte }
chan c1 = [10] of { byte }
chan c2 = [10] of { byte }
//chan c1 = [10] of { byte }
//chan c = [10] of { byte }
bool recieved_c
bool sent_c1
	
	active proctype main()
   {
   		run p(c1,c2)
   		run p(c2,c0)
   		byte i = 1
	c1!i
	sent_c1 = true
   	byte v
S1:	if
 	:: c0?v  -> goto SEXIT
 	fi
 	goto S1
 	
 SEXIT:	
 	recieved_c = true
 }
 
	proctype p(chan cin, cout)
   {
   	byte v
S1:	if
 	:: cin?v  -> cout!v+1 //; goto SEXIT
 	fi
 	goto S1
 	SEXIT:
 }
 
ltl p1 { (<> recieved_c) }
 
 
// perfect version

bool start=false
bool end=false
chan c0=[10] of {byte}
chan c1=[10] of {byte}
chan c2=[10] of {byte}
byte count = 0
byte max =100
active proctype main() {
  run p(c1, c2)
  run p(c2, c0)
  start = true
  c1!1
  byte v
  do
    :: c0?v ->
      count++
      end = true
      //break
    :: count >= max -> break
  od
}
proctype p(chan in; chan out) {
  byte v
  do
    :: in?v ->
      count++
      out!v+1
      //break
    :: count >= max -> break
  od
}

ltl p1 { (<> end) }
 
 
 