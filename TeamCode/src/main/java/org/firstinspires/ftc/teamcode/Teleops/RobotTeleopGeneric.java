package org.firstinspires.ftc.teamcode.Teleops;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.utils.AllianceColor;

@TeleOp(name="BioBuzz - Generic", group="Normal")
public class RobotTeleopGeneric extends RobotTeleopMecanum_Iterative {

    @Override
    public void init() {
        this.alliance = AllianceColor.UNAFFILIATED;
        super.init();
    }
}
