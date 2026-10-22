package darkrpg.tweaks.compat;

/** Captures the real Mixin onLoad result until Minecraft's logger is available. */
public final class BridgeDiagnostics {
    public enum State { NOT_EVALUATED, ACTIVE, INACTIVE, ERROR }
    public record Snapshot(State state, String reason) { }
    private static volatile Snapshot snapshot =
            new Snapshot(State.NOT_EVALUATED, "Mixin onLoad callback has not reported a result");

    private BridgeDiagnostics() { }
    public static void active(String reason) { publish(State.ACTIVE, reason); }
    public static void inactive(String reason) { publish(State.INACTIVE, reason); }
    public static void error(String reason) { publish(State.ERROR, reason); }
    private static synchronized void publish(State state, String reason) {
        snapshot = new Snapshot(state, reason == null ? "unspecified" : reason);
    }
    public static Snapshot read() { return snapshot; }
}
