package com.yiban.javaBase.dev.GOF.creator.factory.abstracted;

import com.yiban.javaBase.dev.GOF.creator.factory.simple.Circle;
import com.yiban.javaBase.dev.GOF.creator.factory.simple.Rectangle;
import com.yiban.javaBase.dev.GOF.creator.factory.simple.Shape;
import com.yiban.javaBase.dev.GOF.creator.factory.simple.Square;
import org.apache.commons.lang.StringUtils;

public class ShapeFactory extends AbstractFactory{
    @Override
    public Color getColor(String color) {
        return null;
    }

    @Override
    public Color getColorByReflect(String filePath, String color) {
        return null;
    }

    @Override
    public Shape getShape(String shape) {
        if(shape == null){
            return null;
        }
        if(shape.equalsIgnoreCase("CIRCLE")){
            return new Circle();
        } else if(shape.equalsIgnoreCase("RECTANGLE")){
            return new Rectangle();
        } else if(shape.equalsIgnoreCase("SQUARE")){
            return new Square();
        }
        return null;
    }

    @Override
    public Shape getShapeByReflect(String filePath, String key) {
        Shape shape = null;
        String classPath = null;
        try {
            classPath = PropertiesUtil.get(filePath, key);
            if (!StringUtils.isBlank(classPath)) {
                shape = (Shape) Class.forName(classPath).newInstance();
            }else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return shape;
    }
}
