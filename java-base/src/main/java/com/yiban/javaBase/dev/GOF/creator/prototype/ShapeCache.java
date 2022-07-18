package com.yiban.javaBase.dev.GOF.creator.prototype;

import java.util.Hashtable;

public class ShapeCache {
    private static Hashtable<String, Shape> shapeMap
            = new Hashtable<String, Shape>();

    public static Shape getShape(String shapeId) {
        Shape cachedShape = shapeMap.get(shapeId);
        return (Shape) cachedShape.clone();
    }

    // 假设对每种形状都运行数据库查询，并创建该形状(创建对象的代价较大，因为要访问数据库)
    // shapeMap.put(shapeKey, shape);
    // 例如，我们要添加Circle
    public static void loadCache() {
        Circle circle = new Circle();
        circle.setId("1");
        shapeMap.put(circle.getId(),circle);
    }
}
