int _step_count = 0
int _step_max = 1000
bool start=false
bool end=false
chan c0=[10] of {byte}
chan c1=[10] of {byte}
chan c2=[10] of {byte}
active proctype main() {
  run p(c1, c2)
  run p(c2, c0)
  start = true
  c1!1
  byte v
  do
    :: c0?v ->
      _step_count++
      end = true
      break
    :: _step_count > _step_max -> break
  od
  END:
}
proctype p(chan in; chan out) {
  byte v
  do
    :: in?v ->
      _step_count++
      out!v+1
      break
    :: _step_count > _step_max -> break
  od
  END:
}
