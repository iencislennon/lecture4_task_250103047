package software_patterns.week4.smarthomeiot;

public class ThermostatAdapter implements SmartDevice {
    private final LegacyThermostat thermostat;

    public ThermostatAdapter(LegacyThermostat thermostat) {
        if (thermostat == null) {
            throw new IllegalArgumentException("Thermostat cant be null");
        }
        this.thermostat = thermostat;
    }

    @Override
    public void turnOn() {
        if (this.thermostat.checkDial().equals("IDLE")) {
            this.thermostat.rotateDial("LOW");
        } else {
            System.out.println("do nothng");
        }
    }

    @Override
    public void turnOff() {
        this.thermostat.rotateDial("IDLE");
    }

    @Override
    public boolean isOn() {
        String dial = this.thermostat.checkDial();
        return dial.equals("LOW") || dial.equals("MEDIUM") || dial.equals("MAX");
    }

    @Override
    public int getPowerPercent() {
        if (this.thermostat.checkDial().equals("IDLE")) {
            return 0;
        } else if (this.thermostat.checkDial().equals("LOW")) {
            return 33;
        } else if (this.thermostat.checkDial().equals("MEDIUM")) {
            return 66;
        } else if (this.thermostat.checkDial().equals("MAX")) {
            return 100;
        }
        return -1;
    }

}
