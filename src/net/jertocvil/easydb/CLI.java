package net.jertocvil.easydb;

public class CLI {

    public static void main(String[] args) {

        Point point = new Point();

        point.name = "Punto";
        point.x = 1;
        point.y = 2;


        point.createTable();

    }



}
