package com.koios.game.arpg;
import java.util.Objects;
public final class Modifier {
 public enum Operation { FLAT, INCREASED, MORE }
 private final Stat stat;
 private final Operation operation;
 private final double value;
 public Modifier(Stat stat, Operation operation, double value) {
  this.stat=Objects.requireNonNull(stat); this.operation=Objects.requireNonNull(operation);
  if (!Double.isFinite(value) || (operation==Operation.MORE && value < -100)) throw new IllegalArgumentException("Invalid modifier");
  this.value=value;
 }
 public Stat getStat(){return stat;}
 public Operation getOperation(){return operation;}
 public double getValue(){return value;}
 public void validate(){new Modifier(stat,operation,value);}
 public String toString(){return stat+" "+operation+" "+value;}
}
