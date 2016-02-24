package net.jertocvil.easydb;

import net.jertocvil.easydb.annotatioins.TableName;

@TableName("Usuarios")
public class User extends DBTable<User> {

    public String name, apellido;
    public int edad;

    public Location location;

}
