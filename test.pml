int _step_count = 0
int _step_max = 1000
chan test_channel=[10] of {byte}
proctype p() {
  byte p
  do
    :: test_channel?p ->
      _step_count++
do
:: (p of Person | !p.name||!p.firstName||!p.age) -> accept488970385: progress488970385:
:: (p~Person[name,firstName,age]) -> accept1209271652: progress1209271652:
od;
    :: _step_count > _step_max -> break
  od
}
