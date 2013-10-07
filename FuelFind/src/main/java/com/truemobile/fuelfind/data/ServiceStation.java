package com.truemobile.fuelfind.data;

/**
 * Created by Werner on 2013/10/07.
 */
public class ServiceStation {
    public int id;
    public String name;
    public double Lat;
    public double Lng;
    public String field_one;
    public String field_two;
    public String field_three;

    public String GetDescription() {
        return field_one + '\n' + field_two + '\n' + field_three;
    }
}
