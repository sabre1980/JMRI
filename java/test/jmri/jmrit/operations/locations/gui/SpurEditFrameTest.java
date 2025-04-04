
package jmri.jmrit.operations.locations.gui;

import java.awt.GraphicsEnvironment;
import java.text.MessageFormat;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jmri.InstanceManager;
import jmri.jmrit.operations.OperationsTestCase;
import jmri.jmrit.operations.locations.*;
import jmri.jmrit.operations.routes.Route;
import jmri.jmrit.operations.routes.RouteLocation;
import jmri.jmrit.operations.trains.Train;
import jmri.jmrit.operations.trains.TrainManager;
import jmri.util.*;
import jmri.util.swing.JemmyUtil;

/**
 * Tests for the Operations Locations GUI class
 *
 * @author Dan Boudreau Copyright (C) 2009
 */
public class SpurEditFrameTest extends OperationsTestCase {

    final static int ALL = Track.EAST + Track.WEST + Track.NORTH + Track.SOUTH;
    private LocationManager lManager = null;
    private Location l = null;
    private Train trainA = null;
    
    @Test
    public void testCTor() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        SpurEditFrame t = new SpurEditFrame();
        Assert.assertNotNull("exists",t);
        JUnitUtil.dispose(t);
    }

    @Test
    public void testAddSpurDefaults() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        LocationManager lManager = InstanceManager.getDefault(LocationManager.class);
        Location l = lManager.getLocationByName("Test Loc C");
        SpurEditFrame f = new SpurEditFrame();
        f.setTitle("Test Spur Add Frame");
        f.setLocation(0, 0); // entire panel must be visible for tests to work properly
        f.initComponents(l, null);

        // create one spur tracks
        f.trackNameTextField.setText("new spur track");
        f.trackLengthTextField.setText("1223");
        JemmyUtil.enterClickAndLeave(f.addTrackButton);

        Track t = l.getTrackByName("new spur track", null);
        Assert.assertNotNull("new spur track", t);
        Assert.assertEquals("spur track length", 1223, t.getLength());
        // check that the defaults are correct
        Assert.assertEquals("all directions", ALL, t.getTrainDirections());
        Assert.assertEquals("all roads", Track.ALL_ROADS, t.getRoadOption());

        // create a second spur
        f.trackNameTextField.setText("2nd spur track");
        f.trackLengthTextField.setText("9999");
        JemmyUtil.enterClickAndLeave(f.addTrackButton);

        t = l.getTrackByName("2nd spur track", null);
        Assert.assertNotNull("2nd spur track", t);
        Assert.assertEquals("2nd spur track length", 9999, t.getLength());
        // check that the defaults are correct
        Assert.assertEquals("all directions", ALL, t.getTrainDirections());
        Assert.assertEquals("all roads", Track.ALL_ROADS, t.getRoadOption());

        // test error, try to create track with same name
        JemmyUtil.enterClickAndLeaveThreadSafe(f.addTrackButton);

        // error dialog should have appeared
        JemmyUtil.pressDialogButton(f, MessageFormat.format(Bundle
                .getMessage("CanNotTrack"),
                new Object[]{Bundle
                        .getMessage("add")}),
                Bundle.getMessage("ButtonOK"));
        JemmyUtil.waitFor(f);
        // kill all frames
        JUnitUtil.dispose(f);
    }

    @Test
    public void testSetDirectionUsingCheckbox() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        SpurEditFrame f = new SpurEditFrame();
        f.setTitle("Test Spur Add Frame");
        f.setLocation(0, 0); // entire panel must be visible for tests to work properly
        f.initComponents(l, null);

        f.trackNameTextField.setText("3rd spur track");
        f.trackLengthTextField.setText("1010");
        JemmyUtil.enterClickAndLeave(f.addTrackButton);

        Track t = l.getTrackByName("3rd spur track", null);
        Assert.assertNotNull("3rd spur track", t);
        Assert.assertEquals("3rd spur track length", 1010, t.getLength());
        Assert.assertEquals("Direction All before change", ALL, t.getTrainDirections());

        // deselect east, west and north check boxes
        JemmyUtil.enterClickAndLeave(f.eastCheckBox);
        JemmyUtil.enterClickAndLeave(f.westCheckBox);
        JemmyUtil.enterClickAndLeave(f.northCheckBox);

        JemmyUtil.enterClickAndLeave(f.saveTrackButton);

        Assert.assertEquals("only south", Track.SOUTH, t.getTrainDirections());

        // kill all frames
        JUnitUtil.dispose(f);
    }

    @Test
    public void testAddScheduleButton() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        SpurEditFrame f = new SpurEditFrame();
        f.setTitle("Test Spur Add Frame");
        f.setLocation(0, 0); // entire panel must be visible for tests to work properly
        f.initComponents(l, null);

        f.trackNameTextField.setText("3rd spur track");
        f.trackLengthTextField.setText("1010");
        JemmyUtil.enterClickAndLeave(f.addTrackButton);

        // create the schedule edit frame
        JemmyUtil.enterClickAndLeave(f.editScheduleButton);

        // confirm schedule add frame creation
        JmriJFrame sef = JmriJFrame.getFrame(Bundle.getMessage("TitleScheduleAdd", "3rd spur track"));
        Assert.assertNotNull(sef);

        // kill all frames
        JUnitUtil.dispose(f);
        JUnitUtil.dispose(sef);
    }

    @Test
    public void testAddCloseAndRestore() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        SpurEditFrame f = new SpurEditFrame();
        f.setTitle("Test Spur Add Frame");
        f.setLocation(0, 0); // entire panel must be visible for tests to work properly
        f.initComponents(l, null);

        // create three spur tracks
        f.trackNameTextField.setText("new spur track");
        f.trackLengthTextField.setText("1223");
        JemmyUtil.enterClickAndLeave(f.addTrackButton);

        f.trackNameTextField.setText("2nd spur track");
        f.trackLengthTextField.setText("9999");
        JemmyUtil.enterClickAndLeave(f.addTrackButton);

        f.trackNameTextField.setText("3rd spur track");
        f.trackLengthTextField.setText("1010");
        JemmyUtil.enterClickAndLeave(f.addTrackButton);

        // deselect east, west and north check boxes
        JemmyUtil.enterClickAndLeave(f.eastCheckBox);
        JemmyUtil.enterClickAndLeave(f.westCheckBox);
        JemmyUtil.enterClickAndLeave(f.northCheckBox);

        JemmyUtil.enterClickAndLeave(f.saveTrackButton);

        // create the schedule edit frame
        JemmyUtil.enterClickAndLeave(f.editScheduleButton);

        // confirm schedule add frame creation
        JmriJFrame sef = JmriJFrame.getFrame(Bundle.getMessage("TitleScheduleAdd", "3rd spur track"));
        Assert.assertNotNull(sef);

        // kill all frames
        JUnitUtil.dispose(f);
        JUnitUtil.dispose(sef);

        // now reload
        Location l2 = lManager.getLocationByName("Test Loc C");
        Assert.assertNotNull("Location Test Loc C", l2);

        LocationEditFrame fl = new LocationEditFrame(l2);
        fl.setTitle("Test Edit Location Frame");

        // check location name
        Assert.assertEquals("name", "Test Loc C", fl.locationNameTextField.getText());

        Assert.assertEquals("number of spurs", 3, fl.spurModel.getRowCount());
        Assert.assertEquals("number of staging tracks", 0, fl.stagingModel.getRowCount());

        JUnitUtil.dispose(fl);
    }

    @Test
    public void testTrainServicesTrack() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        SpurEditFrame f = new SpurEditFrame();
        f.setTitle("Test Spur Frame");
        f.setLocation(0, 0); // entire panel must be visible for tests to work properly    
        f.initComponents(l, null);
        f.setSize(650, 800); // need to see save button

        // create track
        f.trackNameTextField.setText("Train test spur track");
        f.trackLengthTextField.setText("1234");
        JemmyUtil.enterClickAndLeave(f.addTrackButton);

        // Don't allow train to service car type "Boxcar"
        trainA.deleteTypeName("Boxcar");

        // save button
        JemmyUtil.enterClickAndLeave(f.saveTrackButton);
        
        // confirm no error dialog
        Assert.assertTrue(f.isActive());
        
        // specify train pickups using the exclude option
        JemmyUtil.enterClickAndLeave(f.excludeTrainPickup);
        JemmyUtil.enterClickAndLeaveThreadSafe(f.saveTrackButton);
        // error dialog should have appeared
        JemmyUtil.pressDialogButton(f, Bundle.getMessage("ErrorStrandedCar"), Bundle.getMessage("ButtonOK"));
        JemmyUtil.waitFor(f);
        
        trainA.addTypeName("Boxcar");

        // save button
        JemmyUtil.enterClickAndLeave(f.saveTrackButton);
        // confirm no error dialog
        Assert.assertTrue(f.isActive());

        // disable pick ups by train
        Route route = trainA.getRoute();
        RouteLocation rloc = route.getLastLocationByName(l.getName());
        rloc.setPickUpAllowed(false);

        // save button
        JemmyUtil.enterClickAndLeaveThreadSafe(f.saveTrackButton);
        // error dialog should have appeared
        JemmyUtil.pressDialogButton(f, Bundle.getMessage("ErrorStrandedCar"), Bundle.getMessage("ButtonOK"));
        JemmyUtil.waitFor(f);
        
        // restore pick ups
        rloc.setPickUpAllowed(true);

        // deselect east, west, north check boxes
        JemmyUtil.enterClickAndLeave(f.eastCheckBox);
        JemmyUtil.enterClickAndLeave(f.westCheckBox);
        JemmyUtil.enterClickAndLeave(f.northCheckBox);
        JemmyUtil.enterClickAndLeave(f.southCheckBox);

        // save button
        JemmyUtil.enterClickAndLeave(f.saveTrackButton);

        // confirm no error dialog
        Assert.assertTrue(f.isActive());

        // Train had only one location in its route, a switcher, now make it a train with two locations
        route.addLocation(lManager.getLocationByName("Test Loc A"));

        // save button
        JemmyUtil.enterClickAndLeaveThreadSafe(f.saveTrackButton);
        // error dialog should have appeared
        JemmyUtil.pressDialogButton(f, Bundle.getMessage("ErrorStrandedCar"), Bundle.getMessage("ButtonOK"));
        JemmyUtil.waitFor(f);
        
        // train direction default when creating a route is north 
        JemmyUtil.enterClickAndLeave(f.northCheckBox);
        JemmyUtil.enterClickAndLeave(f.saveTrackButton);
        // confirm no error dialog
        Assert.assertTrue(f.isActive());

        // try 0 moves
        rloc.setMaxCarMoves(0);
        JemmyUtil.enterClickAndLeaveThreadSafe(f.saveTrackButton);
        // error dialog should have appeared
        JemmyUtil.pressDialogButton(f, Bundle.getMessage("ErrorStrandedCar"), Bundle.getMessage("ButtonOK"));
        JemmyUtil.waitFor(f);
        
        // restore move count
        rloc.setMaxCarMoves(5);
        JemmyUtil.enterClickAndLeave(f.saveTrackButton);
        Assert.assertTrue(f.isActive());

        // try having the train skip the location
        trainA.addTrainSkipsLocation(rloc);

        JemmyUtil.enterClickAndLeaveThreadSafe(f.saveTrackButton);
        // error dialog should have appeared
        JemmyUtil.pressDialogButton(f, Bundle.getMessage("ErrorStrandedCar"), Bundle.getMessage("ButtonOK"));
        JemmyUtil.waitFor(f);
        // kill all frames
        JUnitUtil.dispose(f);
    }
    
    @Test
    public void testCloseWindowOnSave() {
        Assume.assumeFalse(GraphicsEnvironment.isHeadless());
        Track t = l.addTrack("Test Close", Track.SPUR);
        SpurEditFrame f = new SpurEditFrame();
        f.initComponents(l, t);
        JUnitOperationsUtil.testCloseWindowOnSave(f.getTitle());
    }

    // Ensure minimal setup for log4J
    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        JUnitOperationsUtil.loadFiveLocations();
        lManager = InstanceManager.getDefault(LocationManager.class);
        l = lManager.getLocationByName("Test Loc C");

        JUnitOperationsUtil.loadTrain(l);
        TrainManager trainManager = InstanceManager.getDefault(TrainManager.class);
        trainA = trainManager.getTrainByName("Test Train A"); 
    }
}
