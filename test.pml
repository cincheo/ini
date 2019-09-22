chan channels[0]=[10] of {byte}
active proctype main() {
  RUN myProcess("yeah")
  RUN myProcess1("yeah")
  RUN myProcess2(2)
  START: if
  fi
  goto START
}
proctype myProcess(byte msg) {
  START: if
  fi
  goto START
}
proctype myProcess1(byte msg) {
  START: if
  fi
  goto START
}
proctype myProcess2(byte n) {
  START: if
  fi
  goto START
}
