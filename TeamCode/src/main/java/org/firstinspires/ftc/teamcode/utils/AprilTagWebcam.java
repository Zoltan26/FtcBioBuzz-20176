package org.firstinspires.ftc.teamcode.utils;

import android.util.Size;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;

import java.util.ArrayList;

public class AprilTagWebcam {

    private final AprilTagProcessor processor;
    private final VisionPortal visionPortal;
    private ArrayList<AprilTagDetection> detections = new ArrayList<>();
    private float decimation = 2;


    public AprilTagWebcam(HardwareMap hardwareMap) {
        //most of this might not be needed, just the units and build methods (last 2)
        processor = new AprilTagProcessor.Builder().setDrawTagID(true).setDrawTagOutline(true).setDrawAxes(true).setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11).setTagLibrary(AprilTagGameDatabase.getBioBuzzTagLibrary()).setOutputUnits(DistanceUnit.CM, AngleUnit.DEGREES).build();

        VisionPortal.Builder builder = new VisionPortal.Builder();
        builder.setCamera(hardwareMap.get(WebcamName.class, "camera"));
        builder.setCameraResolution(new Size(640, 480));
        builder.addProcessor(processor);

        visionPortal = builder.build();

        //might need to modify exposure, we will see :)
    }

    public void update() {
        detections = processor.getDetections();
    }

    public ArrayList<AprilTagDetection> getDetections() {
        return detections;
    }

    /* NOT NEEDED AS THIS SEASON (BIOBUZZ) USES APRIL TAG CLUSTERS
       USE THESE METHODS IF SEASON USES SINGLE APRIL TAGS

    public AprilTagSingleDetection getDetectionByID(int id) {

        if (!detections.isEmpty() && id < 0) {
            return (AprilTagSingleDetection) detections.get(0);
        }

        for (AprilTagDetection detection : detections) {
            if (((AprilTagSingleDetection) detection).id == id) {
                return (AprilTagSingleDetection) detection;
            }
        }

        return null;
    }

    public AprilTagSingleDetection getDetectionByIDSet(int[] idSet) {

        if (!detections.isEmpty() && idSet.length == 0) {
            return null;
        }

        for (int id : idSet) {
            for (AprilTagDetection detection : detections) {
                if (((AprilTagSingleDetection) detection).id == id) {
                    return (AprilTagSingleDetection) detection;
                }
            }
        }

        return null;
    }
    */

    public String getFoundCluster(int index) {

        if (detections.isEmpty() || detections.size() <= index) {
            return "";
        }

        if (detections.get(index) instanceof AprilTagClusterDetection) {
            return ((AprilTagClusterDetection) detections.get(index)).metadata.name;
        } else {
            return "";
        }
    }

    public double getBearingForCluster(String id) {

        if (detections.isEmpty()) {
            return 0;
        }

        for (AprilTagDetection detection : detections) {
            if (detection instanceof AprilTagClusterDetection) {
                if (((AprilTagClusterDetection) detection).metadata.name.equals(id)) {
                    return detection.ftcPose.bearing;
                }
            }
        }

        return 0;
    }

    public boolean aprilTagFound() {
        update();
        return !detections.isEmpty();
    }

    public void stop() {
        if (visionPortal != null) {
            visionPortal.close();
        }
    }

    public void changeDecimation(float delta) {
        decimation += delta;
    }

    public void setDecimation() {
        processor.setDecimation(decimation);
    }

    public float getDecimation() {
        return decimation;
    }

}
