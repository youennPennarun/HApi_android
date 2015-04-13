package com.example.nolitsou.hapi;

import android.test.ActivityInstrumentationTestCase2;

import com.example.nolitsou.hapi.MainActivity;
import com.example.nolitsou.hapi.data.Settings;
import com.example.nolitsou.hapi.server.SocketService;


//IMPORTANT: All test cases MUST have a suffix "Test" at the end
//
//THAN:
//Define this in your manifest outside your application tag:
//< instrumentation 
// android:name="android.test.InstrumentationTestRunner"
// android:targetPackage="com.treslines.ponto" 
/// >
//
//AND:
//Define this inside your application tag:
//< uses-library android:name="android.test.runner" / >
//
//The activity you want to test will be the "T" type of ActivityInstrumentationTestCase2
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;

    public MainActivityTest() {
        // create a default constructor and pass the activity class
        // you want to test to the super() constructor
        super(MainActivity.class);
    }

    @Override
    // here is the place to setup the var types you want to test
    protected void setUp() throws Exception {
        super.setUp();

        // because i want to test the UI in the method testAlertasOff()
        // i must set this attribute to true
        setActivityInitialTouchMode(true);

        // init variables
        mainActivity = getActivity();
    }

    // usually we test some pre-conditions. This method is provided
    // by the test framework and is called after setUp()
    public void testPreconditions() {
        assertNotNull("alertaActivity is null", mainActivity);
    }

    public void testSocket() {
        Settings.loadSettings(mainActivity);
        SocketService socketService = new SocketService();
        assertEquals(false, socketService.connect(Settings.host, "totallyWrongToken"));


    }
}