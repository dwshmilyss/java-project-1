package com.yiban.javaBase.dev.GOF.behavior.visitor;

public interface ComputerPart {
    void accept(ComputerPartVisitor computerPartVisitor);
}
