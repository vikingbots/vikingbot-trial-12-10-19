/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Timer;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  
  private final int LEFT_STICK_USB_PORT = 0;
  private final int RIGHT_STICK_USB_PORT = 1;

  private final int RightMotor1Port = 0;
  private final int RightMotor2Port = 1;
  private final int LeftMotor1Port = 2;
  private final int LeftMotor2Port = 3;
  private final int VerticalMotorPort = 4;

  private final double Auton_Forward_Time_Limit = 3.0;
  private final double Auton_Motor_Forward_Power = 0.5;

  private Boolean pressureSwitch;
  private final double creepModeMultiplier = 0.5;
  private double accelResult;

  private Talon LeftMotor1, RightMotor1, LeftMotor2, RightMotor2, VerticalMotor;
  private Joystick leftStick, rightStick;
  private Solenoid solenoid1;
  private Compressor compressor1;
  private Timer Timer01;

  private double left,right,vertical;

  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    LeftMotor1 = new Talon(LeftMotor1Port);
    LeftMotor2 = new Talon(LeftMotor2Port);
    RightMotor1 = new Talon(RightMotor1Port);
    RightMotor2 = new Talon(RightMotor2Port);
    VerticalMotor = new Talon(VerticalMotorPort);

    LeftMotor1.setInverted (true);
    LeftMotor2.setInverted (true);

    solenoid1 = new Solenoid(2);

    compressor1 = new Compressor(0);

    compressor1.setClosedLoopControl(true);

    leftStick = new Joystick(LEFT_STICK_USB_PORT);
    rightStick = new Joystick(RIGHT_STICK_USB_PORT);

     Timer01 = new Timer();

    CameraServer.getInstance().startAutomaticCapture();
  }

  /**
   * @return the leftMotor1Port
   */
  public int getLeftMotor1Port() {
    return LeftMotor1Port;
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() 
  // This is actually Tele-Op
  {

  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);

    Timer01.reset();
    Timer01.start();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    /** switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        //left = 0.3;
        //right = 0.3;
       break; */
       if (Timer01.get() < Auton_Forward_Time_Limit) 
       {
         LeftMotor1.set(Auton_Motor_Forward_Power);
         LeftMotor2.set(Auton_Motor_Forward_Power);
         RightMotor1.set(Auton_Motor_Forward_Power);
         RightMotor2.set(Auton_Motor_Forward_Power);
       }
       else 
       {
         LeftMotor1.set(0.0);
         LeftMotor2.set(0.0);
         RightMotor1.set(0.0);
         RightMotor2.set(0.0);
       }
  }
  // an acceleration function, this is used to change a number by speedInterval (acceleration) to reach speedGoal each time it runs
  public double acceleratenum (double speedCurrent, double speedGoal, double speedInterval) {
    if (speedCurrent > speedGoal) {
      if (speedCurrent - speedInterval < speedGoal){
        accelResult = speedGoal;
      }
      else {accelResult = speedCurrent - speedInterval;}
    }
    else if (speedCurrent < speedGoal) {
      if (speedCurrent + speedInterval > speedGoal){
        accelResult = speedGoal;
      }
      else {accelResult = speedCurrent + speedInterval;}
    } 
    else {
    accelResult = speedCurrent;
    }
    return accelResult;
  }
  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    
    //this says if creepmode is active by pressing right trigger
    
    
    //this statement is so that when right trigger is pushed, it enters "creepMode"
    if(rightStick.getTrigger())
    {
    left = leftStick.getY()*creepModeMultiplier;
    right = rightStick.getY()*creepModeMultiplier;
    } else 
    {
    left  = leftStick.getY();
    right = rightStick.getY();
    }
    LeftMotor1.set(left);
    LeftMotor2.set(left);
    RightMotor1.set(right);
    RightMotor2.set(right);

    if(leftStick.getTrigger())
    {
      solenoid1.set(true);
    }
    else
    {
      solenoid1.set(false);
    }
    
    switch(rightStick.getPOV()) {
      case 0: VerticalMotor.set(1); break;
      case 180: VerticalMotor.set(-1); break;
      default: VerticalMotor.set(0); break;
    } 

    //console output
    //System.out.
    
    System.out.println("Left motor: " + left + "     Right motor: " + right);
  }
 
  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
}
