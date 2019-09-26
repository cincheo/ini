int _step_count = 0
int _step_max = 1000
chan test_channel=[10] of {byte}
proctype p() {
  byte p
  do
    :: test_channel?p ->
      _step_count++
do
:: (p of Person | !p.name||!p.firstName||!p.age) -> accept93122545: progress93122545:
:: (p~Person[name,firstName,age]) -> accept2083562754: progress2083562754:
:: else ->od;
    :: _step_count > _step_max -> break
  od
}
