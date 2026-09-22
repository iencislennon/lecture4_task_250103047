package software_patterns.week4.smarthomeiot;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println(" OMNIHOME SMART CONTROLLER: SYSTEM STARTUP");
        System.out.println("============================================================");

        LegacyBulb legacyBulb = new LegacyBulb();
        LegacyThermostat legacyThermostat = new LegacyThermostat();
        System.out.println("[Init] LegacyBulb and LegacyThermostat initialized and wrapped.");

        BulbAdapter bulbAdapter = new BulbAdapter(legacyBulb);
        ThermostatAdapter thermostatAdapter = new ThermostatAdapter(legacyThermostat);
        List<SmartDevice> devices = List.of(bulbAdapter, thermostatAdapter);
        System.out.println("[Hub] Registering " + devices.size() + " adapted devices into ModernHub...");
        ModernHub hub = new ModernHub(devices);

        System.out.println("--- OPERATION: ACTIVATE ALL DEVICES ---");
        System.out.println("[Action] ModernHub.activateAll() invoked.");
        hub.activateAll();
        System.out.println(" -> BulbAdapter: Brightness set to " + legacyBulb.readBrightness() + ".");
        System.out.println(" -> ThermostatAdapter: Dial set to '" + legacyThermostat.checkDial() + "'.");

        boolean allActive = bulbAdapter.isOn() && thermostatAdapter.isOn();
        System.out.println("[Status] All devices reported active: " + allActive);

        System.out.printf("[Power] Fleet Average Power Usage: %.2f%% (Bulb: %d%%, Thermostat: %d%%)%n",
                hub.calculateAveragePowerUsage(), bulbAdapter.getPowerPercent(), thermostatAdapter.getPowerPercent());

        System.out.println("--- AUDIT: HARDWARE FAULT INJECTION (STAGE 4) ---");

        System.out.println("[Fault 1] Filament physically severed on LegacyBulb...");
        legacyBulb.breakFilament();
        check(" -> BulbAdapter.isOn(): " + bulbAdapter.isOn(), !bulbAdapter.isOn(), "Verified disconnected");
        check(" -> BulbAdapter.getPowerPercent(): " + bulbAdapter.getPowerPercent() + "%",
                bulbAdapter.getPowerPercent() == 0, "Inactive power confirmed");

        System.out.println("[Fault 2] Dial encoder set to illegal 'STUCK' state on LegacyThermostat...");
        legacyThermostat.rotateDial("STUCK");
        check(" -> ThermostatAdapter.isOn(): " + thermostatAdapter.isOn(), !thermostatAdapter.isOn(),
                "Inactive flag confirmed");
        check(" -> ThermostatAdapter.getPowerPercent(): " + thermostatAdapter.getPowerPercent(),
                thermostatAdapter.getPowerPercent() == -1, "Sensor fault sentinel returned");

        System.out.println("--- OPERATION: EMERGENCY SHUTDOWN ---");
        System.out.println("[Action] ModernHub.emergencyShutdown() invoked.");
        hub.emergencyShutdown();
        System.out.println(" -> BulbAdapter: Brightness set to " + legacyBulb.readBrightness() + ".");
        System.out.println(" -> ThermostatAdapter: Dial rotated to '" + legacyThermostat.checkDial() + "'.");

        System.out.printf("[Power] Fleet Average Power Usage: %.2f%%%n", hub.calculateAveragePowerUsage());

        System.out.println("============================================================");
        if (failures == 0) {
            System.out.println(" ALL INTEGRATION TESTS PASSED (100/100)");
        } else {
            System.out.println(" " + failures + " INTEGRATION TEST(S) FAILED");
        }
        System.out.println("============================================================");

        if (failures > 0) {
            System.exit(1);
        }
    }

    private static int failures = 0;

    private static void check(String label, boolean condition, String passMessage) {
        if (condition) {
            System.out.println(label + " [PASSED - " + passMessage + "]");
        } else {
            failures++;
            System.out.println(label + " [FAILED - expected " + passMessage + "]");
        }
    }
}
