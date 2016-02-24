package net.jertocvil.easydb;

import net.jertocvil.easydb.annotatioins.ColumnName;
import net.jertocvil.easydb.annotatioins.TableName;

@TableName("Tabla1")
public class Point extends DBTable<Point> {

    public int x;

    public int y;

    public int z;

    public String name;

}
