
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

@Autonomous(name="First-Tip Blue", group="Robot", preselectTeleOp = "BioBuzz - Blue Alliance")
public class FirstTipBlue extends GenericAuto {

    /* Declare OpMode members. */
    //done in utils

    @Override
    public void runOpMode() {

        // Define and Initialize Motors/Servos ---------------------------------------------------------------------------------------------------
        utils = new OpModeUtils(hardwareMap, AllianceColor.BLUE); //deals with hardware
        waitForStart(); //wait until driver hits "Start"
        ElapsedTime timer = new ElapsedTime(); //starts counting from "start" NOT "init"

        // Step through each leg of the path -----------------------------------------------------------------------------------------------------
        while (timer.seconds() < 5.2) {
            if (timer.seconds() <= 4.5) {
                utils.shootPollen();
                if (timer.seconds() > 2) {
                    utils.releasePollenFlowToShooter();
                }
            } else {
                //got to give it time to go back up before we stop shooting
                //this is to prevent a ball getting stuck in the shooter
                utils.blockPollenFlowToShooter();
            }
        }

        utils.blockPollenFlowToShooter();
        utils.endPollenShooting();

        rotate(-1, 45);
        move(-1, 1.42); //~sqrt(2), because we are going diagonally across a tile
        rotate(1, 45);
        move(-1, 1); //straight 1 tile
        rotate(-1, 45);
        move(-1, 1.42);
        rotate(1, 45);
        move(-1, 1);
        utils.stopMoving();

        // Finish --------------------------------------------------------------------------------------------------------------------------------
        telemetry.addData("Path Complete", "");
        telemetry.update();
        sleep(1000);  // pause to display final telemetry message.
    }


}