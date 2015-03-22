package net.jertocvil.easydb;

import net.jertocvil.easydb.annotatioins.ColumnName;
import net.jertocvil.easydb.annotatioins.TableName;

@TableName("Tabla1")
public class Point extends DBTable<Point> {

    public int x;

    @ColumnName("col_y")
    public int y;

    public String name;

}
