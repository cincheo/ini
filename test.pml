int _var_step = 0
int _step_max = 1000
bool _var_start=false
bool _var_end=false
chan c0=[10] of {byte}
chan c1=[10] of {byte}
chan c2=[10] of {byte}
active proctype main() {
  run p(c1, c2)
  run p(c2, c0)
  _var_start = true
  c1!1
  byte v
  do
    :: c0?v ->
      _var_step++
      _var_end = true
      break
    :: _var_step > _step_max -> break
  od
}
proctype p(chan in; chan out) {
  byte v
  do
    :: in?v ->
      _var_step++
      out!v+1
      break
    :: _var_step > _step_max -> break
  od
}
ltl p1 { (<>(_var_start)-><>(_var_end)) } 