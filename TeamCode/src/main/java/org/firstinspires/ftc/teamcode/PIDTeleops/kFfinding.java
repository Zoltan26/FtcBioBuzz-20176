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

@TeleOp(name="feedforward finding", group="PID")
public class kFfinding extends RobotTeleopMecanum_Iterative {

    //double Kf = 0.0004;
    double Kf = 0; //0.000592
    double Ks = 0.066;
    double targetSpeed = 1240;
    double averageError = 0;
    double totalError = 0;
    double denominator = 0;
    double highestFluxError = 0;
    double[] stepSizes = {10, 1, 0.1, 0.01, 0.001, 0.0001, 0.00001, 0.000001, 0.0000001, 0.00000001};
    int stepIndex = 5;

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

        boolean reset = false;

        if (gamepad1.dpadUpWasPressed()) {
            Kf += stepSizes[stepIndex];
            reset = true;
        }

        if (gamepad1.dpadDownWasPressed()) {
            Kf -= stepSizes[stepIndex];
            reset = true;
        }

        if (gamepad1.dpadRightWasPressed()) {
            targetSpeed += 10;
            reset = true;
        }

        if (gamepad1.dpadLeftWasPressed()) {
            targetSpeed -= 10;
            reset = true;
        }

        if (gamepad1.aWasPressed()) {
            stepIndex = (stepIndex + 1) % stepSizes.length;
        }

        if (reset) {
            //reset to track error info at a target Kf
            totalError = 0;
            denominator = 0;
            highestFluxError = 0;
            reset = false;
        }

        double error = targetSpeed - motor.getVelocity();
        totalError += error;
        denominator++;
        //average error to know roughly how on-point it is
        //it will fluctuate a lot, but generally, is it centered on a point, ish?
        averageError = totalError / denominator;

        if (Math.abs(error) > Math.abs(highestFluxError)) {
            //the magnitude is the only thing that's important in the check
            //we want to keep direction though
            //basically the most swing the error ever experienced (but we want to know if it was under or over shoot)
            highestFluxError = error;
        }

        motor.setPower(Ks + (targetSpeed * Kf));

        telemetry.addData("Power", motor.getPower());
        telemetry.addData("Increment", String.format("%.8f", stepSizes[stepIndex]));
        telemetry.addData("Ks", String.format("%.8f", Ks));
        telemetry.addData("Kf", String.format("%.8f", Kf));
        telemetry.addData("Target", targetSpeed);
        telemetry.addLine();
        telemetry.addData("Error", error);
        telemetry.addData("Most Error", highestFluxError);
        telemetry.addData("Average Error", averageError);
        telemetry.addData("Total Error", totalError);
        telemetry.addData("Error Checks", denominator);
        telemetry.update();

    }


    // Code to run ONCE after the driver hits STOP
    @Override
    public void stop() {
        motor.setPower(0);
    }
}
