package software_patterns.week4.smarthomeiot;

public class BulbAdapter implements SmartDevice {
    private final LegacyBulb bulb;

    public BulbAdapter(LegacyBulb bulb) {
        if (bulb == null) {
            throw new IllegalArgumentException("Bulb cant be null");
        }
        this.bulb = bulb;
    }

    @Override
    public void turnOn() {
        this.bulb.setBrightness(255);
    }

    @Override
    public void turnOff() {
        this.bulb.setBrightness(0);
    }

    @Override
    public boolean isOn() {
        return bulb.hasPower();
    }

    @Override
    public int getPowerPercent() {
        if (!bulb.hasPower()) {
            return 0;
        }
        return (bulb.readBrightness() * 100) / 255;
    }
}
