package system;

import Collections.Vehicle;

import java.io.Serializable;

public class Request implements Serializable {
    private static final long serialVersionUID = 5266595875730820760L;
    private String message = null;
    private Vehicle vehicle = null;

    private String key = null;

    public Request(String message, Vehicle vehicle, String key) {
        this.message = message;
        this.vehicle = vehicle;
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
