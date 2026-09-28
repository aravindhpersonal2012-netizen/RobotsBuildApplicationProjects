package org.firstinspires.ftc.teamcode;

import android.icu.text.Transliterator;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.configuration.annotations.ServoType;
import com.qualcomm.robotcore.util.ElapsedTime;



/**
 * Example OpMode. Demonstrates use of gyro, color sensor, encoders, and telemetry.
 */

@TeleOp(name = "Robot 1.6", group = "Drivetrain")

public class Robot extends LinearOpMode {

    ElapsedTime runtime = new ElapsedTime();


    DcMotor bl, fl, fr, br, intake, turretSpin, turretShooter;
    CRServo intakeServo;
    Servo angleServo;
    double power = 0;

    double shooterPower = 0;
    double position = 0;


    public void runOpMode() {
        bl = hardwareMap.dcMotor.get("bl");
        fl = hardwareMap.dcMotor.get("fl");
        fr = hardwareMap.dcMotor.get("fr");
        br = hardwareMap.dcMotor.get("br");

        intake = hardwareMap.dcMotor.get("intake");
        turretSpin = hardwareMap.dcMotor.get("tSpin");
        turretShooter = hardwareMap.dcMotor.get("tShooter");
        intakeServo = hardwareMap.get(CRServo.class, "iServo");
        angleServo = hardwareMap.get(Servo.class, "aServo");




        // fl.setDirection(DcMotor.Direction.REVERSE);
        bl.setDirection(DcMotor.Direction.REVERSE);


        waitForStart();
        while (opModeIsActive()) {
            double max;

            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
            double axial = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
            double lateral = gamepad1.left_stick_x;
            double yaw = gamepad1.right_stick_x;

            if(gamepad2.right_stick_y>0.25) {
                intakein();
                intakeServoPushIn();
            } else if (gamepad2.right_stick_y<-0.25) {
                intakeout();
                intakeServoPushOut();
            } else {
                intakestop();
                intakeServoStop();
            }

            if (gamepad2.left_stick_x > 0.5) {

                turretSpinRight();

            } else if (gamepad2.left_stick_x > - 0.5) {

                turretSpinLeft();

            } else {

                turretSpinStop();

            }


            if(gamepad2.dpad_up){
                angleUP();
            } else if (gamepad2.dpad_down) {
                angleDown();
            } else {
                angleServo.setPosition(position);
            }

            if (gamepad2.left_bumper){
                shooterPower -= 0.1;
            } else if (gamepad2.left_trigger>0.1){

                shooterPower += 0.1;

            }


            if (gamepad2.right_trigger > 0.1) {
                shoot();
            } else  {
                shootOff();
            }



            // Combine the joystick requests for each axis-motion to determine each wheel's power.
            // Set up a variable for each drive wheel to save the power level for telemetry.
            double frontLeftPower = axial + lateral + yaw;
            double frontRightPower = axial - lateral - yaw;
            double backLeftPower = axial - lateral + yaw;
            double backRightPower = axial + lateral - yaw;

            // Normalize the values so no wheel power exceeds 100%
            // This ensures that the robot maintains the desired motion.
            max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
            max = Math.max(max, Math.abs(backLeftPower));
            max = Math.max(max, Math.abs(backRightPower));

            if (max > 1.0) {
                frontLeftPower /= max;
                frontRightPower /= max;
                backLeftPower /= max;
                backRightPower /= max;
            }



            // Send calculated power to wheels
            fl.setPower(frontLeftPower);
            fr.setPower(frontRightPower);
            bl.setPower(backLeftPower);
            br.setPower(backRightPower);

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", frontLeftPower, frontRightPower);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", backLeftPower, backRightPower);
            telemetry.update();

        }
    }

    public void forward() {

        bl.setPower(1.0);
        //   fl.setPower(1.0);
        fr.setPower(1.0);
        br.setPower(1.0);

    }

    public void back() {

        bl.setPower(-1.0);
        //      fl.setPower(-1.0);
        fr.setPower(-1.0);
        br.setPower(-1.0);

    }
    public void right() {

        bl.setPower(-1.0);
        fl.setPower(1.0);
        fr.setPower(-1.0);
        br.setPower(1.0);

    }
    public void left() {

        bl.setPower(1.0);
        fl.setPower(-1.0);
        fr.setPower(1.0);
        br.setPower(-1.0);

    }
    public void stopmoving() {

        bl.setPower(0);
        fl.setPower(0);
        fr.setPower(0);
        br.setPower(0);

    }
    public void turnleft(){
        bl.setPower(-1.0);
        fl.setPower(-1.0);
        fr.setPower(1.0);
        br.setPower(1.0);
    }
    public void turnright(){
        bl.setPower(1.0);
        fl.setPower(1.0);
        fr.setPower(-1.0);
        br.setPower(-1.0);
    }
    public void intakein(){
        intake.setPower(-1);
    }
    public void intakeout(){
        intake.setPower(1);
    }
    public void intakestop(){
        intake.setPower(0);
    }
    public void intakeServoPushOut() {
        intakeServo.setPower(-1.0);
    }
    public void intakeServoPushIn() {
        intakeServo.setPower(1.0);
    }

    public void intakeServoStop() {
        intakeServo.setPower(0.0);
    }

    public void turretSpinLeft()   {

        turretSpin.setPower(-0.5);

    }
    public void turretSpinRight(){

        turretSpin.setPower(0.5);

    }
    public void turretSpinStop(){

        turretSpin.setPower(0);

    }

    public void angleUP(){

        position += 0.1;
        angleServo.setPosition(position);

    }

    public void angleDown(){

        position -= 0.1;
        angleServo.setPosition(position);

    }

    public void shoot (){

        turretShooter.setPower(shooterPower);

    }

    public void shootOff (){

        turretShooter.setPower(0.0);

    }


}




