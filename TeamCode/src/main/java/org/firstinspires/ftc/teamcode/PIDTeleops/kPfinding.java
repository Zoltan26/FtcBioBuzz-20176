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

@TeleOp(name="proportional finding", group="PID")
public class kPfinding extends RobotTeleopMecanum_Iterative {

    //double Kf = 0.0004;
    double Kf = 0.000395;
    double Ks = 0.011;
    double Kp = 0.0005;
    double targetSpeed = 1450;
    double highSpeed = 1450;
    double lowSpeed = 800;
    double[] stepSizes = {0.1, 0.01, 0.001, 0.0001, 0.00001, 0.000001, 0.0000001, 0.00000001};
    int stepIndex = 4;
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

        if (gamepad1.dpadUpWasPressed()) {
            Kp += stepSizes[stepIndex];
        }

        if (gamepad1.dpadDownWasPressed()) {
            Kp -= stepSizes[stepIndex];
        }

        if (gamepad1.aWasPressed()) {
            if (targetSpeed != lowSpeed) {
                targetSpeed = lowSpeed;
            } else {
                targetSpeed = highSpeed;
            }
        }

        if (gamepad1.x) {
            utils.releasePollenFlowToShooter();
        } else {
            utils.blockPollenFlowToShooter();
        }

        if (gamepad1.bWasPressed()) {
            stepIndex = (stepIndex + 1) % stepSizes.length;
        }

        double error = targetSpeed - motor.getVelocity();

        motor.setPower((error * Kp) + Ks + (targetSpeed * Kf));

        telemetry.addData("Increment", String.format("%.8f", stepSizes[stepIndex]));
        telemetry.addData("Ks", String.format("%.8f", Ks));
        telemetry.addData("Kf", String.format("%.8f", Kf));
        telemetry.addData("Kp", String.format("%.8f", Kp));
        telemetry.addData("Target", targetSpeed);
        telemetry.addData("Error", error);
        telemetry.update();

    }


    // Code to run ONCE after the driver hits STOP
    @Override
    public void stop() {
        motor.setPower(0);
    }
}


// tps  at P and F
// 1550 at 5 and 20
// 1450 at 6 and 23