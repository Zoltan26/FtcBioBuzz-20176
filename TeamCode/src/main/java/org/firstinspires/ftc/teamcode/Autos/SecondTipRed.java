
package org.firstinspires.ftc.teamcode.Autos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.utils.AllianceColor;
import org.firstinspires.ftc.teamcode.utils.OpModeUtils;


/*
 * This OpMode illustrates the concept of driving a path based on encoder counts.
 * The code is structured as a LinearOpMode
 *
 * The code REQUIRES that you DO have encoders on the wheels,
 *   otherwise you would use: RobotAutoDriveByTime;
 *
 *  This code ALSO requires that the drive Motors have been configured such that a positive
 *  power command moves them forward, and causes the encoders to count UP.
 *
 *  The code is written using a method called: encoderDrive(speed, leftInches, rightInches, timeouts)
 *  that performs the actual movement.
 *  This method assumes that each movement is relative to the last stopping place.
 *  There are other ways to perform encoder based moves, but this method is probably the simplest.
 *  This code uses the RUN_TO_POSITION mode to enable the Motor controllers to generate the run profile
 *
 */

@Autonomous(name="Second-Tip Red", group="Robot", preselectTeleOp = "BioBuzz - Red Alliance")

public class SecondTipRed extends LinearOpMode {

    /* Declare OpMode members. */
    //done in utils

    protected OpModeUtils utils;

    @Override
    public void runOpMode() {

        // Define and Initialize Motors/Servos ---------------------------------------------------------------------------------------------------
        utils = new OpModeUtils(hardwareMap, AllianceColor.RED); //deals with hardware
        waitForStart(); //wait until driver hits "Start"
        ElapsedTime timer = new ElapsedTime(); //starts counting from "start" NOT "init"
        while (timer.seconds() < 20) {
            utils.stopMoving(); //wait for tip
        }
        timer.reset();

        // Step through each leg of the path -----------------------------------------------------------------------------------------------------
        if (utils.getValidTargetExists()) {
            //make sure tip was successful by reading the AprilTag
            //if not, this will fail, and we will just move into position
            utils.startPollenShooting();
            while (timer.seconds() < 5) {
                utils.shootPollen();
                if (timer.seconds() > 1.5) {
                    utils.releasePollenFlowToShooter();
                }
            }

            utils.endPollenShooting();
            utils.blockPollenFlowToShooter();
        }

        //sleep to allow movements, then go to next movement
        utils.standardDrive(-1, 0, 0, 0.5);
        sleep(400);
        utils.standardDrive(0, -1, 0, 0.5);
        sleep(1600);
        utils.stopMoving();

        // Finish --------------------------------------------------------------------------------------------------------------------------------
        telemetry.addData("Path Complete", "");
        telemetry.update();
        sleep(1000);  // pause to display final telemetry message.
    }
}
