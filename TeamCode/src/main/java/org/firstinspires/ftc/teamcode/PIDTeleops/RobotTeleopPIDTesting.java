package org.firstinspires.ftc.teamcode.PIDTeleops;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.Teleops.RobotTeleopMecanum_Iterative;
import org.firstinspires.ftc.teamcode.utils.AllianceColor;
import org.firstinspires.ftc.teamcode.utils.PIDController;

@TeleOp(name="Manual PID Testing", group="PID")
public class RobotTeleopPIDTesting extends RobotTeleopMecanum_Iterative {

    private boolean testingActive = false;
    private double Kp = 0.0007;
    private double Ks = 0.28;
    private double Kf = 0.0006;
    private final double[] speed = {800, 1200, 1250, 1300, 1350, 1400, 1450, 1500, 1550, 1600};
    private int speedIndex = 6;
    private final double[] increments = {10, 1.0, 0.1, 0.01, 0.001, 0.0001, 0.00001, 0.000001, 0.0000001, 0.00000001};
    private int incrementIndex = 0;
    private boolean reverseAdd = false;

    private DcMotor referenceMotor;
    private PIDController pid;

    @Override
    public void init() {
        referenceMotor = hardwareMap.get(DcMotor.class, "pollen_shooter");
        referenceMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        pid = new PIDController(referenceMotor, speed[0]);
        this.alliance = AllianceColor.UNAFFILIATED;

        telemetry.addData(">", "Robot Ready.  Press START.");
    }


    @SuppressLint("DefaultLocale")
    @Override
    public void loop() {

        if (gamepad1.xWasPressed()) {
            speedIndex = (speedIndex + 1) % speed.length;
        }

        if (gamepad1.aWasPressed()) {
            incrementIndex = (incrementIndex + 1) % increments.length;
        }

        if (gamepad1.yWasPressed()) {
            incrementIndex--;
            if (incrementIndex < 0) {
                incrementIndex += increments.length;
            }
        }

        if (gamepad1.dpadLeftWasPressed()) {
            reverseAdd = !reverseAdd;
        }

        if (gamepad1.dpadUpWasPressed()) {
            Kp += increments[incrementIndex] * (reverseAdd ? -1 : 1);
        }

        if (gamepad1.dpadRightWasPressed()) {
            Ks += increments[incrementIndex] * (reverseAdd ? -1 : 1);
        }

        if (gamepad1.dpadDownWasPressed()) {
            Kf += increments[incrementIndex] * (reverseAdd ? -1 : 1);
        }

        if (gamepad1.bWasPressed()) {
            testingActive = !testingActive;
            if (testingActive) {
                pid.tune(Kp, Kf, Ks, speed[speedIndex]);
                pid.startLoop();
            }
        }

        if (testingActive) {
            pid.loop();
        } else {
            referenceMotor.setPower(0);
        }

        telemetry.addData("Power", referenceMotor.getPower());
        telemetry.addData("Target Speed", speed[speedIndex]);
        telemetry.addData("Current Speed", pid.getCurrentSpeed());
        telemetry.addData("Get Velocity", ((DcMotorEx) referenceMotor).getVelocity());
        telemetry.addData("Current Position", referenceMotor.getCurrentPosition());
        telemetry.addData("Error", pid.getError());
        telemetry.addData("Stable", pid.isStable());
        telemetry.addLine();
        telemetry.addData("testingActive", testingActive);
        telemetry.addData("Reverse Add", reverseAdd);
        telemetry.addData("Increment", String.format("%.8f", increments[incrementIndex]));
        telemetry.addData("Kp", String.format("%.8f", Kp));
        telemetry.addData("Ks", String.format("%.8f", Ks));
        telemetry.addData("Kf", String.format("%.8f", Kf));
        telemetry.update();

    }


    // Code to run ONCE after the driver hits STOP
    @Override
    public void stop() {
        referenceMotor.setPower(0);
    }
}
