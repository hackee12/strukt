#!/bin/bash
#
# Required environment variables:
# - S21_LIB_DIR

LIB_DIR=$S21_LIB_DIR
FAT_JAR=$LIB_DIR/strukt-*-all.jar

java -jar $FAT_JAR "$@"
