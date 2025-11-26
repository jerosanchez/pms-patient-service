#!/usr/bin/env bash
# jacoco-coverage.sh: Calculate Jacoco line coverage from CSV and check against a target.
# Usage: jacoco-coverage.sh <csv-file> [target-percent]

set -e
exec 2>/dev/null

# Fun message for target strictness
fun_target_message() {
  local target="$1"

  printf "Target: %s%% - " "$target"

  if (( $(echo "$target < 70" | bc -l) )); then
    echo "🛌 Coverage target is super chill. Did you even try? 😴"
  elif (( $(echo "$target < 80" | bc -l) )); then
    echo "😏 Coverage target is pretty easy. You can push harder!"
  elif (( $(echo "$target < 85" | bc -l) )); then
    echo "🙂 Coverage target is reasonable. Solid, but not heroic."
  elif (( $(echo "$target < 95" | bc -l) )); then
    echo "💪 Coverage target is tough! You're pushing for quality."
  else
    echo "🤯 Coverage target is INSANE. Are you even human?!"
  fi
}

# Fun message for relative distance from target
fun_coverage_message() {
  local delta="$1"

  printf "Coverage: %s%% - " "$PCT"

  if (( $(echo "$delta >= 0.20" | bc -l) )); then
    echo "🎉 Incredible! Coverage is way above target! 🏆"
  elif (( $(echo "$delta >= 0.10" | bc -l) )); then
    echo "😎 Awesome! Coverage is well above target."
  elif (( $(echo "$delta >= 0.03" | bc -l) )); then
    echo "👍 Great! Coverage is comfortably above target."
  elif (( $(echo "$delta > 0" | bc -l) )); then
    echo "✅ Nice, you just made it!"
  elif (( $(echo "$delta >= -0.03" | bc -l) )); then
    echo "🟡 So close! Just a bit more testing needed."
  elif (( $(echo "$delta >= -0.10" | bc -l) )); then
    echo "🟠 Coverage needs some love. Add more tests!"
  elif (( $(echo "$delta >= -0.20" | bc -l) )); then
    echo "🔴 Ouch! Coverage is well below target."
  else
    echo "💀 Yikes! Coverage is way below target. Time to write tests!"
  fi
}

check_minimum_target() {
  local target="$1"
  if (( $(echo "$target < 80" | bc -l) )); then
    echo "🙅 Sorry, coverage target too low! Even my grandma writes more tests than that. Set at least 80!"
    exit 42
  fi
}

check_empty_report() {
  local lines_count="$1"
  if [[ $lines_count -eq 0 ]]; then
    echo "No lines found in coverage report."
    exit 2
  fi
}

report_result() {
  local pct="$1"
  local target="$2"
  GREEN="\033[0;32m"
  RED="\033[0;31m"
  NC="\033[0m" # No Color
  if (( $(echo "$pct >= $target" | bc -l) )); then
    printf "${GREEN}PASSED${NC}\n"
    exit 0
  else
    printf "${RED}FAILED${NC}\n"
    exit 1
  fi
}

main() {
  CSV_FILE="$1"
  TARGET="${2:-80}"

  check_minimum_target "$TARGET"

  # Sum missed and covered lines
  read -r MISSED COVERED <<< $(awk -F, 'NR>1 {missed+=$8; covered+=$9} END {print missed, covered}' "$CSV_FILE")
  TOTAL=$((MISSED + COVERED))

  check_empty_report "$TOTAL"

  PCT=$(awk -v c=$COVERED -v t=$TOTAL 'BEGIN {printf "%.2f", (c/t)*100}')
  DELTA=$(awk -v pct=$PCT -v tgt=$TARGET 'BEGIN {printf "%.2f", (pct-tgt)/tgt}')

  fun_target_message "$TARGET"
  fun_coverage_message "$DELTA"
  
  report_result "$PCT" "$TARGET"
}

main "$@"
