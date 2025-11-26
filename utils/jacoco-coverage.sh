#!/usr/bin/env bash
# jacoco-coverage.sh: Calculate Jacoco line coverage from CSV and check against a target.
# Usage: jacoco-coverage.sh <csv-file> [target-percent]

set -e
exec 2>/dev/null

CSV_FILE="$1"
TARGET="${2:-80}"

if [[ ! -f "$CSV_FILE" ]]; then
  echo "Error: CSV file not found: $CSV_FILE" >&2
  exit 2
fi


# Count total classes (number of rows minus header)
TOTAL_CLASSES=$(awk 'END {print NR-1}' "$CSV_FILE")

# Sum missed and covered lines
read -r MISSED COVERED <<< $(awk -F, 'NR>1 {missed+=$8; covered+=$9} END {print missed, covered}' "$CSV_FILE")
TOTAL=$((MISSED + COVERED))


if [[ $TOTAL -eq 0 ]]; then
  echo "No lines found in coverage report."
  exit 2
fi

# Print stats in one row
PCT=$(awk -v c=$COVERED -v t=$TOTAL 'BEGIN {printf "%.2f", (c/t)*100}')

# ANSI color codes
GREEN="\033[0;32m"
RED="\033[0;31m"
NC="\033[0m" # No Color

printf "Classes: %s | Lines: %s | Coverage: %s%%\n" "$TOTAL_CLASSES" "$TOTAL" "$PCT"
printf "Target: %s%%\n" "$TARGET"

if (( $(echo "$PCT >= $TARGET" | bc -l) )); then
  printf "${GREEN}OK${NC}\n"
  exit 0
else
  printf "${RED}BELOW TARGET!${NC}\n"
  exit 1
fi
