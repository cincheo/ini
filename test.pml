chan channels[3]=[10] of {byte}
proctype fac(byte n) {
result=0;
  facRequest[n]!n
  START: if
    CONSUME716083600:
    :: channels[facResult[n]]?r ->
result=r;
  fi
  goto START
}
proctype p(byte n) {
  START: if
    CONSUME991505714:
    :: channels[facRequest[n]]?n ->
do
:: (n==1) -> accept2083562754: progress2083562754:
      facResult[1]!1
:: (n>1) -> accept1239731077: progress1239731077:
      facRequest[n-1]!n-1
od;
    CONSUME344560770:
    :: channels[facResult[n-1]]?f ->
      facResult[n]!f*n
    CONSUME559450121:
    :: channels[facAdmin[n]]?msg ->
  fi
  goto START
}
