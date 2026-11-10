package org.firstinspires.ftc.teamcode.PIDTeleops;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

import org.firstinspires.ftc.teamcode.Teleops.RobotTeleopMecanum_Iterative;
import org.firstinspires.ftc.teamcode.utils.AllianceColor;
import org.firstinspires.ftc.teamcode.utils.OpModeUtils;

@TeleOp(name="Flywheel Built-In PID Testing", group="PID")
public class RobotTeleopBuiltInPIDTesting extends RobotTeleopMecanum_Iterative {

    //start by getting Kf to get as close to hitting target
    //it's not gonna be perfect, but close and error is relatively accurate
    //increase Kp until its faster to hit target, but DOESN'T OVERSHOOT
    //increase Kd until oscillation is reduced, if not gone

    //1450 is a good target it seems, just need to make it more consistent, oscillate less


    double[] velocity = {1600, 1550, 1500, 1450, 1400, 1350, 800};
    int velocityIndex = 3;
    double targetSpeed = velocity[velocityIndex];
    double Kp = 0;
    double Kd = 0;
    double Kf = 0;
    //saves store values we find, just so it doesn't auto start revving up
    //we can set it, but might not work well
    double KpSave = 0.0005;
    double KdSave = 0;
    double KfSave = 0.000395;
    double[] stepSizes = {10, 1, 0.1, 0.01, 0.001, 0.0001, 0.00001, 0.000001, 0.0000001, 0.00000001};
    int stepIndex = 0;

    private DcMotorEx motor;
    private OpModeUtils utils;

    @Override
    public void init() {
        motor = hardwareMap.get(DcMotorEx.class, "pollen_shooter");
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setDirection(DcMotorSimple.Direction.FORWARD);

        utils = new OpModeUtils(hardwareMap, AllianceColor.UNAFFILIATED);

        telemetry.addData(">", "Robot Ready.  Press START.");
    }


    @SuppressLint("DefaultLocale")
    @Override
    public void loop() {

        if (gamepad1.yWasPressed()) {
            velocityIndex = (velocityIndex + 1) % velocity.length;
            targetSpeed = velocity[velocityIndex];
        }

        if (gamepad1.bWasPressed()) {
            stepIndex = (stepIndex + 1) % stepSizes.length;
        }

        if (gamepad1.dpadUpWasPressed()) {
            Kp += stepSizes[stepIndex];
        }

        if (gamepad1.dpadDownWasPressed()) {
            Kp -= stepSizes[stepIndex];
        }

        if (gamepad1.dpadRightWasPressed()) {
            Kf += stepSizes[stepIndex];
        }

        if (gamepad1.dpadLeftWasPressed()) {
            Kf -= stepSizes[stepIndex];
        }

        if (gamepad1.rightBumperWasPressed()) {
            Kd += stepSizes[stepIndex];
        }

        if (gamepad1.leftBumperWasPressed()) {
            Kd -= stepSizes[stepIndex];
        }

        if (gamepad1.a) {
            utils.releasePollenFlowToShooter();
        } else {
            utils.blockPollenFlowToShooter();
        }

        if (gamepad1.leftTriggerWasPressed()) {
            KpSave = Kp;
            KdSave = Kd;
            KfSave = Kf;
            Kp = 0;
            Kd = 0;
            Kf = 0;
        }

        if (gamepad1.rightTriggerWasPressed()) {
            Kp = KpSave;
            Kd = KdSave;
            Kf = KfSave;
        }

        motor.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(Kp, 0, Kd, Kf));

        motor.setVelocity(targetSpeed);
        double currentSpeed = motor.getVelocity();
        double error = targetSpeed - currentSpeed;

        telemetry.addData("Power", motor.getPower());
        telemetry.addData("Target Speed", targetSpeed);
        telemetry.addData("Current Speed", currentSpeed);
        telemetry.addData("Current Position", motor.getCurrentPosition());
        telemetry.addData("Error", error);
        telemetry.addLine();
        telemetry.addData("Increment", String.format("%.8f", stepSizes[stepIndex]));
        telemetry.addData("Kp", String.format("%.8f", Kp));
        telemetry.addData("Kd", String.format("%.8f", Kd));
        telemetry.addData("Kf", String.format("%.8f", Kf));
        telemetry.update();

    }


    // Code to run ONCE after the driver hits STOP
    @Override
    public void stop() {
        motor.setPower(0);
        telemetry.addData("Final KP", Kp);
        telemetry.addData("Final KD", Kd);
        telemetry.addData("Final KF", Kf);
        telemetry.update();
    }
}


// tps  at P and F
// 1550 at 5 and 20
// 1450 at 6 and 23