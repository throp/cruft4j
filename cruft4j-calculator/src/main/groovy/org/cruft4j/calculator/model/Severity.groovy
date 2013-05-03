package org.cruft4j.calculator.model

enum Severity {
  Green(0, "green", "G"), Yellow(1, "yellow", "Y"), Orange(5, "orange", "O"), Red(10, "red", "R")

  String name

  String code

  int points

  Severity(int p, String n, String c) {
    name = n
    points = p
    code = c
  }
}
