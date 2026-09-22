package software_patterns.week4.smarthomeiot;

public interface SmartDevice {
    void turnOn();

    void turnOff();

    boolean isOn();

    int getPowerPercent(); // Standard range: 0 to 100
}
