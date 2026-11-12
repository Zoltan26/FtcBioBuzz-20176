package org.firstinspires.ftc.teamcode.utils;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

public class PIDController {

    private final DcMotorEx motor;
    private double targetSpeed;        //tracks target speed in ticks/second (tps), set once at the start of the loop

    //gonna need to retune it when we have the right speed. frankly, these initial values won't really be used
    private static final double KP_DEFAULT = 0.0012;
    private static final double KF_DEFAULT = 0.000592;
    private static final double KS_DEFAULT = 0.066;

    private double Kp = KP_DEFAULT;  //how fast/slow the system moves towards 0 error
    private double Kf = KF_DEFAULT;  //estimates based on speed, predicts (more or less accurately) power requirement
    private double Ks = KS_DEFAULT;  //power needed to overcome static friction
    //target = 1210

    double error;
    boolean loopActive = false;

    protected final double STABILIZATION_TOLERANCE = 100;
    protected final double STABILIZATION_MINIMUM_THRESHOLD = 250;
    protected final double STABILIZATION_CHECKS_REQUIRED = 30;
    protected final double STABILIZATION_MISSES_ALLOWED = 15;
    protected double stabilizationChecks = 0;
    protected double stabilizationChecksMissed = 0;

    public PIDController(DcMotor motor, double targetSpeed) {
        this((DcMotorEx) motor, targetSpeed);
    }

    public PIDController(DcMotorEx motor, double targetSpeed) {
        this.motor = motor;
        this.targetSpeed = clamp(targetSpeed, 0, 2000);
    }

    public void startLoop() {
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        loopActive = true;
    }

    public void endLoop() {
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setPower(0);
        loopActive = false;
    }

    public void loop() {
        error = targetSpeed - motor.getVelocity();

        double output = (error * Kp) + (targetSpeed * Kf) + Ks;
        motor.setPower(clamp(output, 0, 1));
    }

    public double getError() {
        return error;
    }

    public double getCurrentSpeed() {
        return motor.getVelocity();
    }

    public double getTargetSpeed() {
        return this.targetSpeed;
    }

    public void tune(double Kp, double Kf, double Ks, double speed) {
        this.Kp = Kp;
        this.Kf = Kf;
        this.Ks = Ks;
        this.targetSpeed = speed;
    }

    public void resetDefaults() {
        this.Kp = KP_DEFAULT;
        this.Ks = KS_DEFAULT;
        this.Kf = KF_DEFAULT;
    }

    public void maxPowerMode() {
        this.Kp = 0;
        this.Ks = 0;
        this.Kf = 1;
    }

    public void changeTargetSpeed(double targetSpeed) {
        this.targetSpeed = clamp(targetSpeed, 0, 2000);
    }

    public static double clamp(double value, double lowBound, double highBound) {
        return value <= lowBound ? lowBound : Math.min(highBound, value);
    }

    public boolean isStable() {
        //returns if we are lingering close to target speed for a while
        //basically if abs(error) is within X for Y iterations
        //X and Y being the two constants, tolerance and checks required
        //also if we miss for 1 check, that's fine, but if we start missing a lot, not stable
        //if waaay off, then stable is def not good

        if (Math.abs(error) <= STABILIZATION_TOLERANCE) {
            stabilizationChecks++;
        } else if (Math.abs(error) >= STABILIZATION_MINIMUM_THRESHOLD) {
            stabilizationChecks = 0;
            stabilizationChecksMissed = 0;
        } else {
            stabilizationChecksMissed++;
            if (stabilizationChecksMissed > STABILIZATION_MISSES_ALLOWED) {
                stabilizationChecks = 0;
                stabilizationChecksMissed = 0;
            }
        }

        return stabilizationChecks >= STABILIZATION_CHECKS_REQUIRED;
    }

}
