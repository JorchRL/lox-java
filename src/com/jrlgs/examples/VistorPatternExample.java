package com.jrlgs.examples;

/*
Any new functionality "detached" from any particular class may be implemented
as a PastryVisitor. To avoid scattering the functionality's code inside the
individual classes.
 */
interface PastryVisitor {
 void visitBeignet(Beignet beignet);

 void visitCruller(Cruller cruller);
}

abstract class Pastry {
 abstract void accept(PastryVisitor visitor);
}

class Beignet extends Pastry {

 @Override
 void accept(PastryVisitor visitor) {
  visitor.visitBeignet(this);
 }
}

class Cruller extends Pastry {
 @Override
 void accept(PastryVisitor visitor) {
  visitor.visitCruller(this);
 }
}