package net.jertocvil.easydb;

public class CLI {

    public static void main(String[] args) {

        User usuario = new User();

        usuario.name = "Juan";
        usuario.apellido = "Perez";
        usuario.edad = 20;

        usuario.location = new Location();
        usuario.location.lat = 38.2345f;
        usuario.location.lon = -0.0123f;

        usuario.location.punto = new Point();
        usuario.location.punto.x = 1;
        usuario.location.punto.y = 2;
        usuario.location.punto.z = 3;
        usuario.location.punto.name = "Punto1";

        usuario.createTable();

        usuario.select(3);

    }



}

