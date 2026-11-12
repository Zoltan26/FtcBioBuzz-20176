package org.firstinspires.ftc.teamcode.PIDTeleops;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Teleops.RobotTeleopMecanum_Iterative;
import org.firstinspires.ftc.teamcode.utils.AllianceColor;
import org.firstinspires.ftc.teamcode.utils.OpModeUtils;

@TeleOp(name="feedforward bounds", group="PID")
public class kFBoundsfinding extends RobotTeleopMecanum_Iterative {

    double Kf = 0.0004;
    double Ks = 0.28;
    double lowBound = 0;
    double highBound = 0;
    double midpoint = 0;
    boolean stop = false;
    double targetSpeed = 1200;
    double[] stepSizes = {100, 10};
    int stepIndex = 0;
    ElapsedTime timer = new ElapsedTime();

    private DcMotorEx motor;

    @Override
    public void init() {
        motor = hardwareMap.get(DcMotorEx.class, "pollen_shooter");
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor.setDirection(DcMotorSimple.Direction.FORWARD);

        utils = new OpModeUtils(hardwareMap, AllianceColor.UNAFFILIATED);

        telemetry.addData(">", "Robot Ready.  Press START.");
    }


    @SuppressLint("DefaultLocale")
    @Override
    public void loop() {

        if (timer.seconds() > 0.15) {
            Kf += 0.000001;
            timer.reset();
        }

        double error = targetSpeed - motor.getVelocity();
        if (error < 0 && lowBound == 0) {
            lowBound = Kf;
        }

        if (error > 0) {
            highBound = Kf;
        }

        if (gamepad1.aWasPressed()) {
            stepIndex++;
            stepIndex %= stepSizes.length;
        }

        if (gamepad1.dpadUpWasPressed()) {
            targetSpeed += stepSizes[stepIndex];
        }

        if (gamepad1.dpadDownWasPressed()) {
            targetSpeed -= stepSizes[stepIndex];
        }

        //stop the motor speeding up so you can record the low and high bounds in peace
        if (gamepad1.bWasPressed()) {
            stop = !stop;
        }

        if (!stop) {
            motor.setPower(Ks + (targetSpeed * Kf));
        } else {
            motor.setPower(0);
            midpoint = (lowBound + highBound) / 2;
            Kf = 0.0003;
        }

        telemetry.addData("Increment", String.format("%.8f", stepSizes[stepIndex]));
        telemetry.addData("Power", motor.getPower());
        telemetry.addData("Ks", String.format("%.8f", Ks));
        telemetry.addData("Kf", String.format("%.8f", Kf));
        telemetry.addData("Target", targetSpeed);
        telemetry.addData("Current", motor.getVelocity());
        telemetry.addData("Error", error);
        telemetry.addData("High bound", "%.8f", highBound);
        telemetry.addData("Low bound", "%.8f", lowBound);
        telemetry.addData("Midpoint", "%.8f", midpoint);
        telemetry.update();

    }


    // Code to run ONCE after the driver hits STOP
    @Override
    public void stop() {
        motor.setPower(0);
    }
}
