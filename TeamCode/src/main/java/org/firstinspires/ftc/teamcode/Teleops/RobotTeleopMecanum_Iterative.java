/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode.Teleops;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.utils.AllianceColor;
import org.firstinspires.ftc.teamcode.utils.OpModeUtils;
import org.opencv.ml.EM;

import java.util.Locale;

public class RobotTeleopMecanum_Iterative extends OpMode {

    /* Declare OpMode members. */
    protected OpModeUtils utils; //motors and servos created in here

    /* Declare OpMode variables */
    protected boolean isPrecisionMode = false;
    protected boolean isIntaking = false;
    protected boolean isPollenShooting = false;
    protected boolean isSafetyMode = false;
    //protected boolean isNectarShooting = false;
    protected double normalDriveSpeed = 1.0;
    protected double precisionDriveSpeed = 0.05;
    protected boolean EMERGENCY = false;
    protected AllianceColor alliance = AllianceColor.UNAFFILIATED;
    protected ElapsedTime emergencyTimer = new ElapsedTime();

    // Code to run ONCE when the driver hits INIT
    @Override
    public void init() {
        //setup hardware in utils
        utils = new OpModeUtils(hardwareMap, alliance);

        // Send telemetry message to signify robot waiting;
        telemetry.addData(">", "Robot Ready.  Press START.");
    }

    // Main thing that runs continuously
    @Override
    public void loop() {

        //MOVEMENT -------------------------------------------------------------------------------------------------------------
        if (gamepad1.leftStickButtonWasPressed() && gamepad1.right_stick_button) {
            isSafetyMode = !isSafetyMode;
        }

        if (gamepad1.bWasPressed()) {
            isPrecisionMode = !isPrecisionMode;
        }

        double forward = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double rotate = gamepad1.right_stick_x;
        double speed = isSafetyMode ? 0 : isPollenShooting ? precisionDriveSpeed : normalDriveSpeed;

        if (gamepad1.a) {
            utils.aprilTagDrive(forward, strafe, rotate, speed);
        } else {
            utils.standardDrive(forward, strafe, rotate, speed);
            utils.resetAprilTagLastError();
        }

        utils.aprilTagLoop();



        //INTAKE ---------------------------------------------------------------------------------------------------------------
        if (gamepad1.xWasPressed()) {
            isIntaking = !isIntaking;
            if (!isIntaking) {
                utils.stopIntake();
            }
        }

        if (isIntaking && !EMERGENCY) {
            utils.intake();
        }

        if (gamepad1.y) {
            if (!EMERGENCY) {
                emergencyTimer.reset();
            }

            EMERGENCY = true;
            utils.emergencyEjectIntake(emergencyTimer.seconds() / 2);
        }

        if (EMERGENCY && !gamepad1.y) {
            if (!isIntaking) {
                utils.stopIntake();
            } else {
                utils.intake();
            }

            EMERGENCY = false;
        }


        //POLLEN SHOOTING ------------------------------------------------------------------------------------------------------
        if (gamepad1.leftBumperWasPressed() || gamepad1.rightBumperWasPressed()) {
            isPollenShooting = !isPollenShooting;
            if (isPollenShooting) {
                utils.startPollenShooting();
            } else {
                utils.endPollenShooting();
            }
        }

        if (isPollenShooting) {
            utils.shootPollen();
        }

        if (gamepad1.left_trigger_pressed || gamepad1.right_trigger_pressed) {
            utils.releasePollenFlowToShooter();
        } else {
            utils.blockPollenFlowToShooter();
        }

        //NECTAR SHOOTING --------------------------------------------------------------------------------------------------------
        /* not dealing with nectar for now
        if (gamepad1.rightBumperWasPressed()) {
            isNectarShooting = !isNectarShooting;
            if (isNectarShooting) {
                utils.startNectarShooting();
            }
        }

        if (isNectarShooting) {
            utils.shootNectar();
        }

        if (gamepad1.right_trigger_pressed) {
            utils.releaseNectarFlowToShooter();
        } else {
            utils.blockNectarFlowToShooter();
        }
         */


        //OTHER --------------------------------------------------------------------------------------------------------------
        /* ahhhhhhhhhhhh
        if (gamepad1.dpadDownWasPressed()) {
            utils.openFlowerDumper();
        } else {
            utils.closeFlowerDumper();
        }
         */


        //GAMEPAD 2 -----------------------------------------------------------------------------------------------------------
        if (gamepad2.dpadUpWasPressed()) {
            utils.changePollenSpeed(10);
        }

        if (gamepad2.dpadDownWasPressed()) {
            utils.changePollenSpeed(-10);
        }

        if (gamepad2.dpadRightWasPressed()) {
            utils.changeNectarSpeed(10);
        }

        if (gamepad2.dpadLeftWasPressed()) {
            utils.changeNectarSpeed(-10);
        }

        if (gamepad2.yWasPressed()) {
            utils.changeCameraDecimation(0.1f);
        }

        if (gamepad2.aWasPressed()) {
            utils.changeCameraDecimation(-0.1f);
        }

        if (gamepad2.bWasPressed()) {
            if (this.alliance == AllianceColor.BLUE) {
                this.alliance = AllianceColor.RED;
            } else if (this.alliance == AllianceColor.RED) {
                this.alliance = AllianceColor.BLUE;
            } else { //unaffiliated, sets to red, but can cycle
                this.alliance = AllianceColor.RED;
            }

            utils.setAlliance(alliance);
        }

        //TELEMETRY ------------------------------------------------------------------------------------------------------------
        if (utils.getIsTargetingAprilTag()) {
            telemetry.addData("Found April Tag", utils.getTargetedAprilTagName());
        } else {
            telemetry.addLine("No April Tag Targeted");
        }

        telemetry.addLine();
        telemetry.addData("Intaking", isIntaking ? "TRUE" : "FALSE");
        telemetry.addData("Precision Mode", isPrecisionMode ? "TRUE" : "FALSE");
        telemetry.addData("Safety Mode", isSafetyMode ? "TRUE" : "FALSE");
        telemetry.addData("AprilTag Tracking", gamepad1.a ? "TRUE" : "FALSE");
        telemetry.addData("Alliance", alliance);
        telemetry.addLine();
        telemetry.addData("Pollen Shooting", isPollenShooting ? "TRUE" : "FALSE");
        telemetry.addData("Pollen Speed", utils.getPollenSpeed());
        telemetry.addData("Pollen Target", utils.getPollenTargetSpeed());
        telemetry.addData("Pollen Stable", utils.getPollenPIDStable() ? "TRUE" : "FALSE");
        //telemetry.addLine();
        //telemetry.addData("Nectar Shooting", isIntaking ? "TRUE" : "FALSE");
        //telemetry.addData("Nectar Speed", utils.getNectarSpeed());
        //telemetry.addData("Nectar Target", utils.getNectarTargetSpeed());
        //telemetry.addData("Nectar Stable", utils.getNectarPIDStable() ? "TRUE" : "FALSE");
        telemetry.update();
    }

    // Code to run ONCE after the driver hits STOP
    @Override
    public void stop() {
        utils.stopMoving();
        utils.stopIntake();
        utils.endPollenShooting();
        //utils.endNectarShooting();
        utils.blockPollenFlowToShooter();
        //utils.blockNectarFlowToShooter();
        //utils.closeFlowerDumper();
    }
}
