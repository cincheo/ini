chan channels[3]=[10] of {byte}
boolean start=false
boolean end=false
active proctype main() {
  RUN p(channels[1], channels[2])
  RUN p(channels[2], channels[0])
  c1!1
  start = true
  START: if
    CONSUME1802598046:
    :: channels[0]?v ->
      end = true
  fi
  goto START
}
proctype p(byte in, byte out) {
  START: if
    CONSUME659748578:
    :: channels[in]?v ->
      out!v+1
  fi
  goto START
}
