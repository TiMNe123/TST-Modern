package com.tstmodern.machine.logic;

public enum OperationalStatus {
    CAN_RUN(true, "tstmodern.machine.big_bro_array.status.can_run"),
    NO_MACHINE(false, "tstmodern.machine.big_bro_array.status.no_machine"),
    STALE_ID(false, "tstmodern.machine.big_bro_array.status.stale_id"),
    FRAME_TOO_LOW(false, "tstmodern.machine.big_bro_array.status.frame_too_low"),
    MISSING_INPUT_ENERGY(false, "tstmodern.machine.big_bro_array.status.missing_input_energy"),
    MISSING_OUTPUT_ENERGY(false, "tstmodern.machine.big_bro_array.status.missing_output_energy");

    private final boolean canRun;
    private final String messageKey;

    OperationalStatus(boolean canRun, String messageKey) {
        this.canRun = canRun;
        this.messageKey = messageKey;
    }

    public boolean canRun() {
        return canRun;
    }

    public String messageKey() {
        return messageKey;
    }
}
