package com.yiban.javaBase.dev.GOF.behavior.bridge;

public class GreenCircle implements DrawAPI{
    @Override
    public void drawCircle(int x, int y, int radius) {
        System.out.println("Drawing Circle[ color: green, radius: \"\n" +
                "         + radius +\", x: \" +x+\", \"+ y +\"]\"");
    }
}
