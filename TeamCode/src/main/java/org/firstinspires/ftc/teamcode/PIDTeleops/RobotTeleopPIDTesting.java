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
    private double Kp = 0.00005;
    private double Ks = 0.102;
    private double Kf = 0.0005368;
    private final double[] speed = {800, 1200, 1210, 1220, 1230, 1240, 1250, 1260, 1270, 1280, 1290, 1300};
    private int speedIndex = 9;
    private final double[] increments = {1.0, 0.1, 0.01, 0.001, 0.0001, 0.00001, 0.000001, 0.0000001, 0.00000001};
    private int incrementIndex = 4;
    private boolean reverseAdd = false;

    private DcMotor referenceMotor;
    private PIDController pid;

    double averageError = 0;
    double totalError = 0;
    double denominator = 0;


    @Override
    public void init() {
        referenceMotor = hardwareMap.get(DcMotor.class, "pollen_shooter");
        referenceMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        pid = new PIDController(referenceMotor, speed[speedIndex]);
        pid.tune(Kp, Kf, Ks, speed[speedIndex]);
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

        boolean reset = false;

        if (gamepad1.dpadLeftWasPressed()) {
            reverseAdd = !reverseAdd;
        }

        if (gamepad1.dpadUpWasPressed()) {
            Kp += increments[incrementIndex] * (reverseAdd ? -1 : 1);
            reset = true;
        }

        if (gamepad1.dpadRightWasPressed()) {
            Ks += increments[incrementIndex] * (reverseAdd ? -1 : 1);
            reset = true;
        }

        if (gamepad1.dpadDownWasPressed()) {
            Kf += increments[incrementIndex] * (reverseAdd ? -1 : 1);
            reset = true;
        }

        if (reset) {
            //reset to track error info at a target Kf
            totalError = 0;
            denominator = 0;
        }

        pid.tune(Kp, Kf, Ks, speed[speedIndex]);
        pid.loop();

        totalError += pid.getError();
        denominator++;
        //average error to know roughly how on-point it is
        //it will fluctuate a lot, but generally, is it centered on a point, ish?
        averageError = totalError / denominator;

        telemetry.addData("Power", referenceMotor.getPower());
        telemetry.addData("Target Speed", pid.getTargetSpeed());
        telemetry.addData("Current Speed", pid.getCurrentSpeed());
        telemetry.addData("Get Velocity", ((DcMotorEx) referenceMotor).getVelocity());
        telemetry.addData("Current Position", referenceMotor.getCurrentPosition());
        telemetry.addData("Error", pid.getError());
        telemetry.addData("Average Error", averageError);
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
