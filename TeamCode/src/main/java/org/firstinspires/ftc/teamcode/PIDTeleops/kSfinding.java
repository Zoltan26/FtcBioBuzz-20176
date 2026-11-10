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

@TeleOp(name="static finding", group="PID")
public class kSfinding extends RobotTeleopMecanum_Iterative {

    double Ks = 0;
    double[] stepSizes = {10, 1, 0.1, 0.01, 0.001, 0.0001, 0.00001};
    int stepIndex = 0;

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

        if (gamepad1.aWasPressed()) {
            stepIndex = (stepIndex + 1) % stepSizes.length;
        }

        if (gamepad1.dpadUpWasPressed()) {
            Ks += stepSizes[stepIndex];
        }

        if (gamepad1.dpadDownWasPressed()) {
            Ks -= stepSizes[stepIndex];
        }


        motor.setPower(Ks);

        telemetry.addData("Increment", String.format("%.5f", stepSizes[stepIndex]));
        telemetry.addData("Kf", String.format("%.5f", Ks));
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