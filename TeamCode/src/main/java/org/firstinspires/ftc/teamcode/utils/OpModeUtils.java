package org.firstinspires.ftc.teamcode.utils;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

public class OpModeUtils {

    //OP MODE MEMBERS
    protected DcMotor frontRight;
    protected DcMotor frontLeft;
    protected DcMotor backRight;
    protected DcMotor backLeft;
    protected DcMotor intakeMotor;
    protected DcMotor pollenShooter;
    protected DcMotor nectarShooter;
    protected Servo pollenServo;
    protected Servo nectarServo;
    //protected Servo flowerDumper;
    protected AprilTagWebcam webcam;


    //PIDs
    private final PIDController pollenPID;
    private final PIDController nectarPID;


    //CONSTANTS
    private final double POLLEN_SHOOTER_SPEED = 1450;
    private final double NECTAR_SHOOTER_SPEED = 1550; //this is guess, not real. must find
    private final double POLLEN_RELEASE_POSITION = 0.15;
    private final double POLLEN_STOPPER_POSITION = 0.375;
    private final double NECTAR_RELEASE_POSITION = 0.15; //make sure this is right
    private final double NECTAR_STOPPER_POSITION = 0.375; //make sure this is right
    //private final double FLOWER_DUMPER_OPEN_POSITION = 0.5;
    //private final double FLOWER_DUMPER_CLOSE_POSITION = 1;


    //APRIL TAG STUFF
    private AllianceColor alliance;
    private final double APRIL_TAG_TARGET = 0;
    private final double APRIL_TAG_TOLERANCE = 2.5;
    private final double APRIL_TAG_KP = 0.02;
    private final double APRIL_TAG_KD = 0.15;
    private double aprilTagLastError = 0;
    private final ElapsedTime aprilTagTimer = new ElapsedTime();



    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // CONSTRUCTOR ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    //function as an init, sets up hardware and whatnot
    public OpModeUtils(HardwareMap hardwareMap, AllianceColor color) {
        // Define and Initialize Motors/Servos
        frontLeft = hardwareMap.get(DcMotor.class, "front_left_drive");
        frontRight = hardwareMap.get(DcMotor.class, "front_right_drive");
        backLeft = hardwareMap.get(DcMotor.class, "back_left_drive");
        backRight = hardwareMap.get(DcMotor.class, "back_right_drive");
        intakeMotor = hardwareMap.get(DcMotor.class, "intake_motor");
        pollenShooter = hardwareMap.get(DcMotor.class, "pollen_shooter");
        //nectarShooter = hardwareMap.get(DcMotor.class, "nectar_shooter");
        pollenServo = hardwareMap.get(Servo.class, "pollen_servo");
        //nectarServo = hardwareMap.get(Servo.class, "nectar_servo");
        //flowerDumper = hardwareMap.get(Servo.class, "flower_dumper");

        //SET DIRECTION
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.REVERSE);
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        intakeMotor.setDirection(DcMotor.Direction.REVERSE);
        pollenShooter.setDirection(DcMotor.Direction.FORWARD);
        //nectarShooter.setDirection(DcMotor.Direction.FORWARD);

        //SET MODE
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        pollenShooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //nectarShooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //SET ZERO POWER BEHAVIOR
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        pollenShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        //nectarShooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        this.pollenPID = new PIDController(pollenShooter, POLLEN_SHOOTER_SPEED);
        this.nectarPID = new PIDController(nectarShooter, NECTAR_SHOOTER_SPEED);

        //add dedicated tunes if needed
        //format: pid.tune(Kp, Kd, Kf)
        //won't need it for intake (doesn't have to be precise)
        //hopefully won't need it for shooters, but might

        webcam = new AprilTagWebcam(hardwareMap);
        this.alliance = color;
    }



    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // MOVEMENT ------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void standardDrive(double forward, double strafe, double rotation, double speed) {
        double denominator = Math.max(Math.abs(forward) + Math.abs(strafe) + Math.abs(rotation), 1);
        double leftFrontMotorPower = (forward + strafe + rotation) / denominator;
        double leftBackMotorPower = (forward - strafe + rotation) / denominator; //maybe add more power via magic number
        double rightFrontMotorPower = (forward - strafe - rotation) / denominator;
        double rightBackMotorPower = (forward + strafe - rotation) / denominator; //maybe add more power via magic number

        frontLeft.setPower(leftFrontMotorPower * speed);
        backLeft.setPower(leftBackMotorPower * speed);
        frontRight.setPower(rightFrontMotorPower * speed);
        backRight.setPower(rightBackMotorPower * speed);
    }

    public void stopMoving() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backRight.setPower(0);
        backLeft.setPower(0);
    }

    public void aprilTagDrive(double forward, double strafe, double rotate, double speed) {
        webcam.update();

        boolean validFind = getValidTargetExists();
        String tagClusterFound;

        //if we didn't find a tag, we should move normally, but add some rotation
        //added rotation will allow us to spin until we find something
        if (!validFind) {
            standardDrive(forward, strafe, 0.15, speed);
            return;
        } else {
            tagClusterFound = getValidTag(); //returns the valid tag that was found
        }


        //camera is upside down, so bearing is negative, subtractions of negative bearing results in addition
        double error = APRIL_TAG_TARGET + webcam.getBearingForCluster(tagClusterFound);

        if (Math.abs(error) <= APRIL_TAG_TOLERANCE) {
            rotate = 0;
        } else {
            double pTerm = error * APRIL_TAG_KP;
            double dTerm = ((error - aprilTagLastError) / aprilTagTimer.seconds()) * APRIL_TAG_KD;

            rotate = Range.clip(pTerm + dTerm, -0.2, 0.2);
        }

        //all vals are negative because "forward" for the april tag is shooter side
        standardDrive(-forward, -strafe, -rotate, speed);

        aprilTagLastError = error;
    }

    public void aprilTagLoop() {
        aprilTagTimer.reset();
    }

    public void resetAprilTagLastError() {
        aprilTagLastError = 0;
    }

    public String getTargetedAprilTagName() {
        return webcam.getFoundCluster(0);
    }

    public boolean getIsTargetingAprilTag() {
        return webcam.aprilTagFound();
    }

    public boolean getValidTargetExists() {
        boolean validFind = false;
        int index = 0;
        String tagClusterFound = webcam.getFoundCluster(index);

        while (true) {
            if (alliance == AllianceColor.RED) {
                if (tagClusterFound.startsWith("R")) {
                    validFind = true;
                }
            } else if (alliance == AllianceColor.BLUE) {
                if (tagClusterFound.startsWith("B")) {
                    validFind = true;
                }
            } else {
                //if no alliance, track any tag
                if (!tagClusterFound.isEmpty()) {
                    validFind = true;
                }
            }

            if (!validFind) {
                index++;
                tagClusterFound = webcam.getFoundCluster(index);
                if (tagClusterFound.isEmpty()) {
                    return false; //index out of bounds
                    //a valid tag hasn't been detected, return false
                }
            } else {
                //validFind is true, we can leave the loop
                return true;
            }
        }
    }

    public String getValidTag() {
        boolean validFind = false;
        int index = 0;
        String tagClusterFound = webcam.getFoundCluster(index);

        while (true) {
            if (alliance == AllianceColor.RED) {
                if (tagClusterFound.startsWith("R")) {
                    validFind = true;
                }
            } else if (alliance == AllianceColor.BLUE) {
                if (tagClusterFound.startsWith("B")) {
                    validFind = true;
                }
            } else {
                //if no alliance, track any tag
                if (!tagClusterFound.isEmpty()) {
                    validFind = true;
                }
            }

            if (!validFind) {
                index++;
                tagClusterFound = webcam.getFoundCluster(index);
            } else {
                //validFind is true, we can leave the loop
                return tagClusterFound;
            }
        }
    }



    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // INTAKE --------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void intake() {
        intakeMotor.setPower(0.65);
    }

    public void stopIntake() {
        intakeMotor.setPower(0);
    }

    public void emergencyEjectIntake(double time) {
        intakeMotor.setPower(-0.5 - time);
    }

    /* later
    public void openFlowerDumper() {
        flowerDumper.setPosition(FLOWER_DUMPER_OPEN_POSITION);
    }

    public void closeFlowerDumper() {
        flowerDumper.setPosition(FLOWER_DUMPER_CLOSE_POSITION);
    }
     */



    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // POLLEN SHOOTING -----------------------------------------------------------------------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void startPollenShooting() {
        //preventing mishaps
        if (pollenServo.getPosition() != POLLEN_STOPPER_POSITION) {
            return;
        }

        pollenPID.startLoop();
    }

    public void shootPollen() {
        pollenPID.loop();
    }

    public void endPollenShooting() {
        //preventing mishaps
        if (pollenServo.getPosition() != POLLEN_STOPPER_POSITION) {
            return;
        }

        pollenPID.endLoop();
    }

    public void releasePollenFlowToShooter() {
        pollenServo.setPosition(POLLEN_RELEASE_POSITION);
    }

    public void blockPollenFlowToShooter() {
        pollenServo.setPosition(POLLEN_STOPPER_POSITION);
    }



    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // NECTAR SHOOTING -----------------------------------------------------------------------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void startNectarShooting() {
        //preventing mishaps
        /* yeah....
        if (nectarServo.getPosition() != NECTAR_STOPPER_POSITION) {
            return;
        }
         */

        nectarPID.startLoop();
    }

    public void shootNectar() {
        nectarPID.loop();
    }

    public void endNectarShooting() {
        //preventing mishaps
        /* yeah....
        if (nectarServo.getPosition() != NECTAR_STOPPER_POSITION) {
            return;
        }
         */

        nectarPID.endLoop();
    }

    public void releaseNectarFlowToShooter() {
        nectarServo.setPosition(NECTAR_RELEASE_POSITION);
    }

    public void blockNectarFlowToShooter() {
        nectarServo.setPosition(NECTAR_STOPPER_POSITION);
    }



    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // GETTERS AND SETTERS -------------------------------------------------------------------------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public double getPollenSpeed() {
        return pollenPID.getCurrentSpeed();
    }

    public double getNectarSpeed() {
        return nectarPID.getCurrentSpeed();
    }

    public double getPollenTargetSpeed() {
        return pollenPID.getTargetSpeed();
    }

    public double getNectarTargetSpeed() {
        return nectarPID.getTargetSpeed();
    }

    public void changePollenSpeed(int amount) {
        pollenPID.changeTargetSpeed(pollenPID.getTargetSpeed() + amount);
    }

    public void changeNectarSpeed(int amount) {
        nectarPID.changeTargetSpeed(nectarPID.getTargetSpeed() + amount);
    }

    public void changeCameraDecimation(float amount) {
        webcam.changeDecimation(amount);
    }

    public boolean getPollenPIDStable() {
        return pollenPID.isStable();
    }

    public boolean getNectarPIDStable() {
        return nectarPID.isStable();
    }

    public void setAlliance(AllianceColor alliance) {
        this.alliance = alliance;
    }

}
