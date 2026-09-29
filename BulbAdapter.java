// [PDF Section 2, Page 2 & Section 4 Stage 1, Page 4]
// Implements Target interface SmartDevice using Object Composition wrapping LegacyBulb.
public class BulbAdapter implements SmartDevice {

    // [PDF Section 2, Page 2 & Section 4 Stage 1, Page 4]
    // Private final reference to the wrapped Adaptee instance (Composition principle).
    private final LegacyBulb bulb;

    // [PDF Section 1 Rule 2, Page 1 & Section 4 Stage 1, Page 4-5]
    // Personal Student-ID Calibration Seed K (last single digit of ID).
    // Configured to default K = 4 matching Tasksheet concrete example and transcript.
    private final int calibrationSeedK;

    // [PDF Section 4 Stage 1, Page 4-5] Default calibration constant matching the lab specification example.
    public static final int DEFAULT_K = 4;

    // [PDF Section 4 Stage 1, Page 4]
    // Standard lab constructor accepting the legacy adaptee and applying default K = 4.
    public BulbAdapter(LegacyBulb bulb) {
        // Delegates to parameterized constructor using default calibration seed K = 4.
        this(bulb, DEFAULT_K);
    }

    // [PDF Section 1 Rule 2, Page 1 & Section 4 Stage 1, Page 4-5]
    // Overloaded constructor supporting dynamic evaluation of any Student ID digit K.
    public BulbAdapter(LegacyBulb bulb, int k) {
        // [PDF Section 4 Stage 1 Constructor Specification, Page 4] Throws IllegalArgumentException if null.
        if (bulb == null) {
            // Fast-fail diagnostic defense preventing NullPointerException during delegation.
            throw new IllegalArgumentException("Adaptee LegacyBulb reference cannot be null.");
        }
        // Stores immutable reference to wrapped adaptee instance.
        this.bulb = bulb;
        // Binds student calibration seed parameter K for mathematical conversions.
        this.calibrationSeedK = k;
    }

    // [PDF Listing 1, Page 2 & Section 4 Stage 1, Page 4]
    // Implements SmartDevice.turnon() by delegating to legacy setBrightness(255).
    @Override
    public void turnon() {
        // [PDF Section 4 Stage 1 Method turnOn(), Page 4] Sets legacy hardware to peak brightness (255).
        this.bulb.setBrightness(255);
    }

    // [PDF Section 4 Stage 1 Prose Reference, Page 4]
    // CamelCase alias method provided for API consistency across test harnesses.
    public void turnOn() {
        // Delegates directly to contract implementation.
        this.turnon();
    }

    // [PDF Listing 1, Page 2 & Section 4 Stage 1, Page 4]
    // Implements SmartDevice.turnoff() by delegating to legacy setBrightness(0).
    @Override
    public void turnoff() {
        // [PDF Section 4 Stage 1 Method turnOff(), Page 4] Sets legacy hardware to zero output (0).
        this.bulb.setBrightness(0);
    }

    // [PDF Section 4 Stage 1 Prose Reference, Page 4]
    // CamelCase alias method provided for API consistency across test harnesses.
    public void turnOff() {
        // Delegates directly to contract implementation.
        this.turnoff();
    }

    // [PDF Listing 1, Page 2 & Section 4 Stage 1, Page 4 & Stage 4 Fault Scenario A, Page 6]
    // Evaluates active operational state with physical filament failure defense.
    @Override
    public boolean ison() {
        // [PDF Section 4 Stage 4 Fault Scenario A Hardening Rule, Page 6]
        // If filament is severed, bulb.hasPower() returns false; must immediately report false.
        if (!this.bulb.hasPower()) {
            // Hardware safety check: dead circuit cannot be active regardless of register cache.
            return false;
        }
        // [PDF Section 4 Stage 1 Method isOn(), Page 4] Returns true if power holds and brightness > 0.
        return this.bulb.readBrightness() > 0;
    }

    // [PDF Section 4 Stage 1 Prose Reference, Page 4]
    // CamelCase alias method provided for API consistency across test harnesses.
    public boolean isOn() {
        // Delegates directly to contract implementation.
        return this.ison();
    }

    // [PDF Listing 1, Page 2, Section 4 Stage 1, Page 4-5 & Stage 4 Fault Scenario A, Page 6]
    // Computes normalized power percentage incorporating calibration seed K and fault guards.
    @Override
    public int getPowerPercent() {
        // [PDF Section 4 Stage 4 Fault Scenario A Hardening Rule, Page 6]
        // Broken Filament defense: if circuit is open, power MUST be 0% regardless of cached register.
        if (!this.bulb.hasPower()) {
            // Returns 0% power immediately when filament continuity is broken.
            return 0;
        }

        // Reads raw hardware register integer value (range 0 to 255).
        int rawBrightness = this.bulb.readBrightness();

        // [PDF Section 4 Stage 1 Boundary Invariant 2: Zero Rule, Page 5]
        // If rawBrightness == 0, the method must return strictly 0 (no K offset added).
        if (rawBrightness == 0) {
            // Safe standby power invariant.
            return 0;
        }

        // [PDF Section 4 Stage 1 Mathematical Formula, Page 4]
        // Raw Percent = floor((rawBrightness * 100) / 255) using integer division truncation.
        int rawPercent = (rawBrightness * 100) / 255;

        // [PDF Section 4 Stage 1 Calibrated Percent Formula, Page 5]
        // Calibrated Percent = Raw Percent + K.
        int calibratedPercent = rawPercent + this.calibrationSeedK;

        // [PDF Section 4 Stage 1 Boundary Invariant 1: Capping Rule, Page 5]
        // Clamps output to ensure power never exceeds maximum 100%.
        return Math.min(100, calibratedPercent);
    }
}
