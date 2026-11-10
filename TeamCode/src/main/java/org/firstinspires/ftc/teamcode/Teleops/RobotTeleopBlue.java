package org.firstinspires.ftc.teamcode.Teleops;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.utils.AllianceColor;

@TeleOp(name="BioBuzz - Blue Alliance", group="Normal")
public class RobotTeleopBlue extends RobotTeleopMecanum_Iterative {

    @Override
    public void init() {
        this.alliance = AllianceColor.BLUE;
        super.init();
    }
}
