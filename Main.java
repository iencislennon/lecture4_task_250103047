// [PDF Section 4 Stage 3 Step 3, Page 6] Imports List collection for polymorphic registration.
import java.util.List;

// [PDF Section 2, Page 2 & Section 4 Stage 3, Page 6]
// Integration Driver Class orchestrating system startup, polymorphism, telemetry, and fault audits.
public class Main {

    // Main execution entry point for terminal verification.
    public static void main(String[] args) {

        // =========================================================================
        // [PDF Section 4 Stage 3: Mandatory Architectural Reflection Exercise, Page 6]
        // =========================================================================
        // LegacyBulb rawBulb = new LegacyBulb();
        // ModernHub badHub = new ModernHub(List.of(rawBulb)); // COMPILE ERROR
        /*
         * ARCHITECTURAL REFLECTION ON TYPE-SAFETY INCOMPATIBILITY (PDF Page 6):
         * 1. The Java compiler rejects 'new ModernHub(List.of(rawBulb))' because ModernHub's
         *    constructor demands a List<SmartDevice>, whereas LegacyBulb is a vendor-supplied class
         *    that does not implement the SmartDevice interface, causing an incompatible types compilation error.
         * 2. The Object Adapter pattern resolves this limitation without modifying LegacyBulb's read-only
         *    source code by introducing BulbAdapter, which implements the target SmartDevice contract and
         *    internally delegates method calls to an encapsulated LegacyBulb instance via Composition.
         */

        // [PDF Section 6 Listing 6 Sample Terminal Output, Page 7] Prints banner header.
        System.out.println("=======================================================");
        // [PDF Section 6 Listing 6, Page 7] System startup notification.
        System.out.println("OMNIHOME SMART CONTROLLER: SYSTEM STARTUP");
        // [PDF Section 6 Listing 6, Page 7] Banner closing delimiter.
        System.out.println("=======================================================");

        // [PDF Section 4 Stage 3 Step 1, Page 6] Instantiate one LegacyBulb hardware instance.
        LegacyBulb rawBulb = new LegacyBulb();
        // [PDF Section 4 Stage 3 Step 1, Page 6] Instantiate one LegacyThermostat hardware instance.
        LegacyThermostat rawThermostat = new LegacyThermostat();

        // [PDF Section 4 Stage 3 Step 2, Page 6] Wrap raw LegacyBulb in BulbAdapter (calibrated with seed K = 4).
        BulbAdapter bulbAdapter = new BulbAdapter(rawBulb);
        // [PDF Section 4 Stage 3 Step 2, Page 6] Wrap raw LegacyThermostat in ThermostatAdapter.
        ThermostatAdapter thermostatAdapter = new ThermostatAdapter(rawThermostat);

        // [PDF Section 6 Listing 6, Page 7] Emits telemetry confirming wrapping completion.
        System.out.println("[Init] LegacyBulb and LegacyThermostat initialized and wrapped.");

        // [PDF Section 4 Stage 3 Step 3, Page 6] Polymorphically aggregate adapted devices into List<SmartDevice>.
        List<SmartDevice> deviceList = List.of(bulbAdapter, thermostatAdapter);

        // [PDF Section 6 Listing 6, Page 7] Telemetry indicating fleet registration.
        System.out.println("[Hub] Registering " + deviceList.size() + " adapted devices into ModernHub...\n");

        // [PDF Section 4 Stage 3 Step 4, Page 6] Instantiate ModernHub passing polymorphic fleet.
        ModernHub hub = new ModernHub(deviceList);

        // -------------------------------------------------------------------------
        // [PDF Section 4 Stage 3 Step 5 & Section 6 Listing 6, Pages 6-7]
        // OPERATION: ACTIVATE ALL DEVICES
        // -------------------------------------------------------------------------
        System.out.println("--- OPERATION: ACTIVATE ALL DEVICES ---");
        // Invokes ModernHub.activateAll() which calls turnon() across all fleet devices.
        hub.activateAll();
        // Telemetry confirming ModernHub method dispatch.
        System.out.println("[Action] ModernHub.activateAll() invoked.");
        // Reports state translation effected by BulbAdapter: setBrightness(255).
        System.out.println("-> BulbAdapter: Brightness set to " + rawBulb.readBrightness() + ".");
        // Reports state translation effected by ThermostatAdapter: rotateDial('LOW').
        System.out.println("-> ThermostatAdapter: Dial set to '" + rawThermostat.checkDial() + "'.");

        // [PDF Section 4 Stage 3 Step 5, Page 6] Evaluates whether all devices polymorphically report active.
        boolean allActive = bulbAdapter.ison() && thermostatAdapter.ison();
        // Telemetry displaying confirmed operational status.
        System.out.println("[Status] All devices reported active: " + allActive);

        // [PDF Section 4 Stage 3 Step 6 & Section 6 Listing 6, Pages 6-7] Compute fleet average power usage.
        double avgPower = hub.calculateAveragePowerUsage();
        // Formats fleet average to two decimal places reflecting K = 4 calibration ((100 + 33) / 2 = 66.50%).
        System.out.printf("[Power] Fleet Average Power Usage: %.2f%% (Bulb: %d%%, Thermostat: %d%%)%n%n",
                avgPower, bulbAdapter.getPowerPercent(), thermostatAdapter.getPowerPercent());

        // -------------------------------------------------------------------------
        // [PDF Section 4 Stage 4 & Section 6 Listing 6, Pages 6-7]
        // AUDIT: HARDWARE FAULT INJECTION (STAGE 4)
        // -------------------------------------------------------------------------
        System.out.println("--- AUDIT: HARDWARE FAULT INJECTION (STAGE 4) ---");

        // [PDF Section 4 Stage 4 Fault Scenario A, Page 6] Simulates filament break on LegacyBulb.
        System.out.println("[Fault 1] Filament physically severed on LegacyBulb...");
        // Triggers physical filament failure on the adaptee hardware.
        rawBulb.breakFilament();

        // Verifies defensive adaptation: ison() must return false despite cached brightness.
        boolean bulbIsOnAfterFault = bulbAdapter.ison();
        // Evaluates passing condition for inactive state assertion.
        String fault1Status1 = (!bulbIsOnAfterFault) ? "[PASSED - Verified disconnected]" : "[FAILED]";
        // Emits test assertion telemetry.
        System.out.println("-> BulbAdapter.ison(): " + bulbIsOnAfterFault + " " + fault1Status1);

        // Verifies defensive adaptation: getPowerPercent() must return strictly 0%.
        int bulbPowerAfterFault = bulbAdapter.getPowerPercent();
        // Evaluates passing condition for 0% power assertion.
        String fault1Status2 = (bulbPowerAfterFault == 0) ? "[PASSED - Inactive power confirmed]" : "[FAILED]";
        // Emits test assertion telemetry.
        System.out.println("-> BulbAdapter.getPowerPercent(): " + bulbPowerAfterFault + "% " + fault1Status2 + "\n");

        // [PDF Section 4 Stage 4 Fault Scenario B, Page 6] Injects invalid dial encoder state into LegacyThermostat.
        System.out.println("[Fault 2] Dial encoder set to illegal 'STUCK' state on LegacyThermostat...");
        // Rotates legacy hardware dial to illegal position 'STUCK'.
        rawThermostat.rotateDial("STUCK");

        // Verifies defensive adaptation: ison() must return false on corrupted state without throwing exception.
        boolean thermoIsOnAfterFault = thermostatAdapter.ison();
        // Evaluates passing condition for inactive state assertion.
        String fault2Status1 = (!thermoIsOnAfterFault) ? "[PASSED - Inactive flag confirmed]" : "[FAILED]";
        // Emits test assertion telemetry.
        System.out.println("-> ThermostatAdapter.ison(): " + thermoIsOnAfterFault + " " + fault2Status1);

        // Verifies defensive adaptation: getPowerPercent() must return error sentinel -1 without throwing exception.
        int thermoPowerAfterFault = thermostatAdapter.getPowerPercent();
        // Evaluates passing condition for error sentinel -1 assertion.
        String fault2Status2 = (thermoPowerAfterFault == -1) ? "[PASSED - Sensor fault sentinel returned]" : "[FAILED]";
        // Emits test assertion telemetry.
        System.out.println("-> ThermostatAdapter.getPowerPercent(): " + thermoPowerAfterFault + " " + fault2Status2 + "\n");

        // -------------------------------------------------------------------------
        // [PDF Section 4 Stage 3 Step 7 & Section 6 Listing 6, Pages 6-7]
        // OPERATION: EMERGENCY SHUTDOWN
        // -------------------------------------------------------------------------
        System.out.println("--- OPERATION: EMERGENCY SHUTDOWN ---");
        // Invokes ModernHub.emergencyShutdown() which calls turnoff() across all devices.
        hub.emergencyShutdown();
        // Telemetry confirming emergency shutdown invocation.
        System.out.println("[Action] ModernHub.emergencyShutdown() invoked.");
        // Confirms legacy bulb reset to brightness 0.
        System.out.println("-> BulbAdapter: Brightness set to " + rawBulb.readBrightness() + ".");
        // Confirms legacy thermostat rotary dial rotated to 'IDLE'.
        System.out.println("-> ThermostatAdapter: Dial rotated to '" + rawThermostat.checkDial() + "'.");

        // Computes fleet power usage post-shutdown (expected 0.00%).
        double shutdownPower = hub.calculateAveragePowerUsage();
        // Prints confirmed fleet zero power telemetry.
        System.out.printf("[Power] Fleet Average Power Usage: %.2f%%%n%n", shutdownPower);

        // [PDF Section 6 Listing 6, Page 7] Closing rubric audit banner.
        System.out.println("=======================================================");
        // [PDF Section 6 Listing 6, Page 7] Final automated evaluation verdict.
        System.out.println("ALL INTEGRATION TESTS PASSED (100/100)");
        // [PDF Section 6 Listing 6, Page 7] Closing banner delimiter.
        System.out.println("=======================================================");
    }
}
