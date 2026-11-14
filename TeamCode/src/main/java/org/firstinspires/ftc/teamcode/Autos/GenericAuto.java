package org.firstinspires.ftc.teamcode.Autos;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.utils.OpModeUtils;

public class GenericAuto extends LinearOpMode {

    protected OpModeUtils utils;

    @Override
    public void runOpMode() {
        //does nothing, this is to add useful methods all Autos can use
        //this is not meant to be run
    }

    public void move(int direction, double tiles) {
        direction = getDirection(direction);

        //takes time to accelerate
        utils.standardDrive(direction, 0, 0, 0.3);
        sleep(850);
        //first part it to accelerate and cover 1 tile
        //afterward, it's at speed and so time to cover a tile will be less
        utils.standardDrive(direction, 0, 0, 0.3);
        sleep(Math.round(500 * (tiles - 1)));
        utils.stopMoving();
        sleep(250);
    }

    public void rotate(int direction, int degrees) {
        direction = getDirection(direction);

        utils.standardDrive(0, 0, 0.4 * direction, 0.3);
        sleep((long) 18 * degrees);
        utils.stopMoving();
        sleep(250);
    }

    private int getDirection(int direction) {
        //used to ensure direction is always either 1 or -1
        if (direction > 0) {
            direction = 1;
        } else {
            direction = -1;
        }

        return direction;
    }
}
