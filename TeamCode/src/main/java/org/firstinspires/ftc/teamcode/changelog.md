# VERSION 1.3.0 (11/15/2026):
    -Fixed PID tuning OpModes to further improve ease of tuning
    -Fixed PIDController class to remove outdated and unneeded code
        -This required an update to OpModeUtils and other OpMode classes
    -Tuned PID (mostly). Might need another tuning later when flywheel is fixed


-------------------------------------------------------------------------------------------------------------------------------------------------------------
# VERSION 1.2.0 (11/14/2026):
    -Remembered to add v1.1.1 to changelog
    -Fixed changelog for version 1.1.1 to more accurately represent what changed
    -Reordered the changelog so newer versions are on top
    -Added dates to changelog (commit dates)

    -Fixed auto pathing (tested on a carpet so it might not work, but should be generally accurate)
    -Added "GenericAuto" class that Autos can extend, which provides some helpful methods
        -Note that the move() method does not actually move the correct amount of tiles (slightly short)
        -However, it works, so this will only be fixed if other Auto paths conflict later on
        -Basically, this issue is noted and deemed not worthy of a fix quite yet
        -This could all change when we actually test on the field, who knows
    -Fixed shooting intervals in Auto so it starts moving as soon as it's done shooting
    -Fixed an issue that it wouldn't stop shooting until the end of the auto
    -Ensured that failing to detect AprilTags does in fact result in omitting the shooting step

    -Fixed intake power being too high and occasionally launching balls too far
    -Removed some useless telemetry output
    -Fixed a slight mistake that Precision mode did nothing because it was actually checking isPollenShooting (whoops lol)
    -Refactored some AprilTag detection stuff for code cleanliness's sake


-------------------------------------------------------------------------------------------------------------------------------------------------------------
# VERSION 1.1.1 (11/12/2026):
    -Fixed changelog for version 1.1.0 to more accurately represent what changed


-------------------------------------------------------------------------------------------------------------------------------------------------------------
# VERSION 1.1.0 (11/12/2026):
    -Improved PID tuning classes to show more info
    -Attempted to tune PID and failed (fairly untuned, will require more testing)
    -Moved valid tag detection to AprilTagWebcam and refactored code accordingly
    -Added Max Power Mode to shooting so we can shoot balls even if flywheel is worn down (plastic buildup from poor-quality balls)
    -Added a changelog so I don't have to write the changes in the commit message (also to keep track of my changes)


-------------------------------------------------------------------------------------------------------------------------------------------------------------
# VERSION 1.0.0 (11/09/2026) - INITIAL COMMIT:
    -Code from halfway through the season
    -Includes code to drive, intake, shoot (PID tuned), AprilTag detection, and some simple Autos
    -This is not at all the first version, but it is the first I've commited, so I will start version counter from here