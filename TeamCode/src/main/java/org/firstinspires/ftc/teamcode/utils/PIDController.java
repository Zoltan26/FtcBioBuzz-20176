package org.firstinspires.ftc.teamcode.utils;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

public class PIDController {

    private final DcMotorEx motor;
    private double targetSpeed;        //tracks target speed in ticks/second (tps), set once at the start of the loop

    //gonna need to retune it when we have the right speed. frankly, these initial values won't really be used
    private double Kp = 0.0007;        //how fast/slow the system moves towards 0 error
    //private double Kd = 0.0000305;   //makes sure the system doesn't tend towards 0 error too quickly
    private double Kf = 0.0006;      //estimates based on speed, predicts (more or less accurately) power requirement
    private double Ks = 0.011;         //power needed to overcome static friction

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
