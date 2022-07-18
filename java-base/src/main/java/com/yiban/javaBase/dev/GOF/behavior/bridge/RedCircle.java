package com.yiban.javaBase.dev.GOF.behavior.bridge;

public class RedCircle implements DrawAPI{
    @Override
    public void drawCircle(int x, int y, int radius) {
        System.out.println("\"Drawing Circle[ color: red, radius: \"\n" +
                "         + radius +\", x: \" +x+\", \"+ y +\"]\"");
    }
}
