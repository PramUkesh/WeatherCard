package cn.fython.weathercard.support;

public class CityNotFoundException extends Exception {

    public CityNotFoundException() {
        super();
    }

    public CityNotFoundException(String msg) {
        super(msg);
    }

}
