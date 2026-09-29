// [PDF Section 2, Page 2 & Section 4 Stage 2, Page 5]
// Implements Target interface SmartDevice using Object Composition wrapping LegacyThermostat.
public class ThermostatAdapter implements SmartDevice {

    // [PDF Section 2, Page 2 & Section 4 Stage 2, Page 5]
    // Encapsulated Adaptee reference held as private final (Composition principle).
    private final LegacyThermostat thermostat;

    // [PDF Section 4 Stage 2 Constructor Specification, Page 5]
    // Constructor establishing dependency injection and rejecting null references.
    public ThermostatAdapter(LegacyThermostat thermostat) {
        // [PDF Section 4 Stage 2, Page 5] Validates non-null parameter before assignment.
        if (thermostat == null) {
            // Throws IllegalArgumentException as strictly mandated by specification.
            throw new IllegalArgumentException("Adaptee LegacyThermostat reference cannot be null.");
        }
        // Assigns validated adaptee instance.
        this.thermostat = thermostat;
    }

    // [PDF Listing 1, Page 2 & Section 4 Stage 2 Table, Page 5]
    // Implements turnon() with dial state inspection and operation idempotency.
    @Override
    public void turnon() {
        // Queries current dial position string from legacy hardware.
        String currentDial = this.thermostat.checkDial();

        // [PDF Section 4 Stage 2 Table: turnOn() Action, Page 5]
        // If currently in standby 'IDLE', rotate dial to baseline operational level 'LOW'.
        if ("IDLE".equalsIgnoreCase(currentDial)) {
            // Baseline operational heating engagement.
            this.thermostat.rotateDial("LOW");
        }
        // [PDF Section 4 Stage 2 Table: turnOn() Idempotency Rationalization, Page 5]
        // If dial is 'LOW', 'MEDIUM', or 'MAX', do nothing to preserve existing user heat setting.
    }

    // [PDF Section 4 Stage 2 Table Prose Reference, Page 5]
    // CamelCase alias method provided for API consistency across test harnesses.
    public void turnOn() {
        // Delegates directly to contract implementation.
        this.turnon();
    }

    // [PDF Listing 1, Page 2 & Section 4 Stage 2 Table, Page 5]
    // Implements turnoff() by returning hardware dial to safe standby 'IDLE' state.
    @Override
    public void turnoff() {
        // [PDF Section 4 Stage 2 Table: turnOff() Action, Page 5]
        // For any current state, rotates rotary dial back to safe standby 'IDLE'.
        this.thermostat.rotateDial("IDLE");
    }

    // [PDF Section 4 Stage 2 Table Prose Reference, Page 5]
    // CamelCase alias method provided for API consistency across test harnesses.
    public void turnOff() {
        // Delegates directly to contract implementation.
        this.turnoff();
    }

    // [PDF Listing 1, Page 2, Section 4 Stage 2 Table, Page 5 & Stage 4 Fault Scenario B, Page 6]
    // Evaluates active operational status across discrete states with defensive corruption guards.
    @Override
    public boolean ison() {
        // Polls rotary dial string from adaptee.
        String dial = this.thermostat.checkDial();

        // [PDF Section 4 Stage 4 Fault Scenario B Hardening Rule, Page 6]
        // Defensive check: null dial state must never throw NullPointerException; returns false.
        if (dial == null) {
            // Unmapped null state safely treated as inactive.
            return false;
        }

        // [PDF Section 4 Stage 2 Table & Stage 4 Fault Table, Pages 5-6]
        // Matches dial string against known operational heating positions.
        switch (dial.toUpperCase().trim()) {
            // Active operational heat stage: LOW.
            case "LOW":
            // Active operational heat stage: MEDIUM.
            case "MEDIUM":
            // Active operational heat stage: MAX.
            case "MAX":
                // Returns true when hardware is actively drawing power for heating.
                return true;

            // Safe standby state: IDLE.
            case "IDLE":
                // Returns false when unit is resting in standby mode.
                return false;

            // [PDF Section 4 Stage 4 Fault Scenario B, Page 6] Unmapped string (e.g. 'STUCK', 'OVERHEAT', "").
            default:
                // Sensor/encoder corruption safely defaults to inactive false without throwing exception.
                return false;
        }
    }

    // [PDF Section 4 Stage 2 Table Prose Reference, Page 5]
    // CamelCase alias method provided for API consistency across test harnesses.
    public boolean isOn() {
        // Delegates directly to contract implementation.
        return this.ison();
    }

    // [PDF Listing 1, Page 2, Section 4 Stage 2 Table, Page 5 & Stage 4 Fault Scenario B, Page 6]
    // Translates discrete dial strings into numerical percentages with fault sentinel protection.
    @Override
    public int getPowerPercent() {
        // Interrogates legacy rotary dial setting.
        String dial = this.thermostat.checkDial();

        // [PDF Section 4 Stage 4 Fault Scenario B Hardening Rule, Page 6]
        // Defensive check: null dial string must safely return -1 sentinel without throwing NullPointerException.
        if (dial == null) {
            // Sensor fault sentinel returned.
            return -1;
        }

        // [PDF Section 4 Stage 2 Table & Stage 4 Fault Table, Pages 5-6]
        // Translates valid discrete states to percentages or returns error sentinel for anomalies.
        switch (dial.toUpperCase().trim()) {
            // [PDF Section 4 Stage 2 Table, Page 5] Standby consumes 0% power.
            case "IDLE":
                return 0;

            // [PDF Section 4 Stage 2 Table, Page 5] Low power stage = 33%.
            case "LOW":
                return 33;

            // [PDF Section 4 Stage 2 Table, Page 5] Medium power stage = 66%.
            case "MEDIUM":
                return 66;

            // [PDF Section 4 Stage 2 Table, Page 5] Maximum power stage = 100%.
            case "MAX":
                return 100;

            // [PDF Section 4 Stage 4 Fault Scenario B Hardening Rule, Page 6]
            // Encoder corruption ('STUCK', 'OVERHEAT', "", etc.) must return error sentinel -1.
            default:
                // Safe failure sentinel indicating unmapped hardware telemetry.
                return -1;
        }
    }
}
