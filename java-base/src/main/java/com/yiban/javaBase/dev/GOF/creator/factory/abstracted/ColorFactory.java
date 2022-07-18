package com.yiban.javaBase.dev.GOF.creator.factory.abstracted;

import com.yiban.javaBase.dev.GOF.creator.factory.simple.Shape;
import org.apache.commons.lang.StringUtils;

public class ColorFactory extends AbstractFactory{
    @Override
    public Color getColor(String color) {
        if(color == null){
            return null;
        }
        if(color.equalsIgnoreCase("RED")){
            return new Red();
        } else if(color.equalsIgnoreCase("GREEN")){
            return new Green();
        } else if(color.equalsIgnoreCase("BLUE")){
            return new Blue();
        }
        return null;
    }

    @Override
    public Color getColorByReflect(String filePath, String key) {
        Color color = null;
        String classPath = null;
        try {
            classPath = PropertiesUtil.get(filePath, key);
            if (!StringUtils.isBlank(classPath)) {
                color = (Color) Class.forName(classPath).newInstance();
            }else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return color;
    }

    @Override
    public Shape getShape(String shape) {
        return null;
    }

    @Override
    public Shape getShapeByReflect(String filePath, String shape) {
        return null;
    }
}
