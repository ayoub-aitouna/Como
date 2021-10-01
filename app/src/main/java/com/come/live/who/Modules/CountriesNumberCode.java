package com.come.live.who.Modules;

public class CountriesNumberCode {
    String name;
    String dial_code;
    String code;


    public CountriesNumberCode() {
    }

    public CountriesNumberCode(String name, String dial_code, String code) {
        this.name = name;
        this.dial_code = dial_code;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDial_code() {
        return dial_code;
    }

    public void setDial_code(String dial_code) {
        this.dial_code = dial_code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
