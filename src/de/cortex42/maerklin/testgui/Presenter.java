package de.cortex42.maerklin.testgui;

import de.cortex42.maerklin.framework.*;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Created by ivo on 21.10.15.
 */
public class Presenter {
    private final View view;
    private Connection connection;
    private boolean isEthernet;
    private int selectedLocId;

    private final static int BAUD = 500000;
    private final static int DATA_BITS = 8;
    private final static int STOP_BITS = 1;
    private final static int PARITY_BIT = 0;

    private static final int MFX_RANGE = 16384; //new byte[]{0x00, 0x00, 0x40, 0x00};
    private static final int MM_RANGE = 0; //new byte[]{0x00, 0x00, 0x00, 0x00};
    private static final int MM2_EQUIPMENT_RANGE = 12288; //new byte[]{0x00, 0x00, 0x30, 0x00};

    private static final int MM_LOC_ID = 78; //new byte[]{0x00, 0x00, 0x00, 0x4E}; //78 = BR 81 (MM)
    private static final int MFX_LOC_ID = 6; //RE 460 076-3 (MFX)
    private static final int MFX_LOC_ID2 = 7; //AE 6/6 11424

    private static final int LIGHT_FUNCTION = 0;

    private static final int PC_PORT = 15730;
    private static final int CS2_PORT = 15731;
    private static final String CS2_IP_ADDRESS = "192.168.16.2";
    private static final long DEFAULT_DELAY = 250L;


    //todo check hashes (S. 6)
    /*
    // -----------------------------------------------------------------------------
// Calculation HASH:
// 16Bit High UID XOR 16Bit LOW UID
// 0x4347 XOR 0x5A6B = 0x192C
// Initial Hash for command 0x1B will be 0x192c
// For other commands swap HByte with LByte and add b110 at Bit 9,8,7
//    0x2C |  0x19
// 00101100|00011001
// 00101111|00011001 = 0x2F19
#define BRIDGE_HASH   0x2F19
#define INIT_HASH      0x192C
     */

    public Presenter(View view){
        this.view = view;

        view.addLoc("");
        view.addLoc(Integer.toString(MM_LOC_ID));
        view.addLoc(Integer.toString(MFX_LOC_ID));
        view.addLoc(Integer.toString(MFX_LOC_ID2));

        //fill combobox with available interfaces
        view.addInterface("");
        view.addInterface(CS2_IP_ADDRESS);
       /* for (String serialPortString : serialPortConnection.getAvailableSerialPorts()) {
            view.addInterface(serialPortString);
        }*/
    }

    public void initialize(){
        String selectedInterface = view.getSelectedInterface();

      /*  if(Objects.equals(selectedInterface, CS2_IP_ADDRESS)){
            isEthernet = true;

            try {
                ethernetConnection = new EthernetConnection(PC_PORT, CS2_PORT, CS2_IP_ADDRESS);
            } catch (FrameworkException e) {
                e.printStackTrace();
            }
        }else{
            isEthernet = false;

            if (!serialPortConnection.openPort(selectedInterface, BAUD, DATA_BITS, STOP_BITS, PARITY_BIT)) {
                DebugOutput.write("Could not open the port.");
            }else{
                DebugOutput.write("Port opened.");
            }
        }*/

        //todo ugly..
        String selectedLoc = view.getSelectedLoc();
        String mmLoc = Integer.toString(MM_LOC_ID);
        String mfxLoc1 = Integer.toString(MFX_LOC_ID);

        if(Objects.equals(selectedLoc, mmLoc)){
            selectedLocId = MM_RANGE | MM_LOC_ID;
        }else if(Objects.equals(selectedLoc, mfxLoc1)){
            selectedLocId = MFX_RANGE | MFX_LOC_ID;
        }else{
            selectedLocId = MFX_RANGE | MFX_LOC_ID2;
        }
    }

    public void sendStart(){
        sendPacket(CS2CANCommands.newRegistration());
        pause(DEFAULT_DELAY);
        sendPacket(CS2CANCommands.unlockRail());
        pause(DEFAULT_DELAY);

        sendPacket(CS2CANCommands.go());
    }

    public void sendStop(){
        sendPacket(CS2CANCommands.stop());
    }

    public void sendBootloaderGo(){
        sendPacket(CS2CANCommands.bootloaderGo());
        pause(400L); //pause 400ms after sending bootloader go command
    }

    public void sendLight(boolean on){
        sendPacket(CS2CANCommands.toggleFunction(selectedLocId, LIGHT_FUNCTION, on ? CS2CANCommands.FUNCTION_ON : CS2CANCommands.FUNCTION_OFF));
    }

    public void sendVelocity(int velocity){
        sendPacket(CS2CANCommands.setVelocity(selectedLocId, velocity));
    }

    public void sendToggleDirection(){
        final int[] velocity = new int[1];

        addPacketListener(
                new PacketListener() {
                    @Override
                    public void packetEvent(PacketEvent packetEvent) {
                        byte command = packetEvent.getCANPacket().getCommand();

                        if((command & CS2CANCommands.RESPONSE) == CS2CANCommands.RESPONSE){
                            //toggle reponse bit
                            command = (byte)(command & ~CS2CANCommands.RESPONSE);
                        }

                        if (command != CS2CANCommands.VELOCITY) {
                            return;
                        }

                        byte[] data = packetEvent.getCANPacket().getData();
                        byte[] velocityBytes = new byte[]{data[4], data[5]};
                        velocity[0] = new BigInteger(velocityBytes).intValue();

                        removePacketListener(this);
                    }
                }
        );

        sendPacket(CS2CANCommands.queryVelocity(selectedLocId));
        pause(DEFAULT_DELAY);


        sendPacket(CS2CANCommands.setDirection(selectedLocId, CS2CANCommands.DIRECTION_TOGGLE)); //toggle direction
        pause(DEFAULT_DELAY);

        addPacketListener(
                new PacketListener() {
                    @Override
                    public void packetEvent(PacketEvent packetEvent) {
                        byte command = packetEvent.getCANPacket().getCommand();

                        if((command & CS2CANCommands.RESPONSE) == CS2CANCommands.RESPONSE){
                            //toggle reponse bit
                            command = (byte)(command & ~CS2CANCommands.RESPONSE);
                        }

                        if(command != CS2CANCommands.DIRECTION){
                            return;
                        }

                        byte[] data = packetEvent.getCANPacket().getData();
                        byte direction= data[4];

                        switch (direction) {
                            case CS2CANCommands.DIRECTION_FORWARD:
                                view.setDirection("FORWARD");
                                break;

                            case CS2CANCommands.DIRECTION_BACKWARD:
                                view.setDirection("BACKWARD");
                                break;
                        }

                        removePacketListener(this);

                    }
                }
        );
        sendVelocity(velocity[0]);
        pause(DEFAULT_DELAY);

        sendStart();
        pause(DEFAULT_DELAY);

        sendPacket(CS2CANCommands.queryDirection(selectedLocId)); //query new direction
    }

    public void cleanUp(){
       /* if(isEthernet){
            if (ethernetConnection != null) {
                ethernetConnection.cleanUp();
            }
        }else{
            serialPortConnection.closePort();
        }

        DebugOutput.write("Cleaned up.");*/
    }

    private void sendQueryVelocity(){
        addPacketListener(
                new PacketListener() {
                    @Override
                    public void packetEvent(PacketEvent packetEvent) {
                        byte command = packetEvent.getCANPacket().getCommand();

                        if((command & CS2CANCommands.RESPONSE) == CS2CANCommands.RESPONSE){
                            //toggle reponse bit
                            command = (byte)(command & ~CS2CANCommands.RESPONSE);
                        }

                        if (command != CS2CANCommands.VELOCITY
                                && packetEvent.getCANPacket().getDlc() != CS2CANCommands.VELOCITY_SET_DLC) {
                            return;
                        }

                        byte[] data = packetEvent.getCANPacket().getData();
                        byte[] velocityBytes = new byte[]{data[4], data[5]};
                        int velocity = new BigInteger(velocityBytes).intValue();
                        view.setVelocity(velocity);

                        removePacketListener(this);
                    }
                }
        );

        sendPacket(CS2CANCommands.queryVelocity(selectedLocId));
    }

    private void sendPacket(CANPacket canPacket){
       /* if(isEthernet){
            try {
                ethernetConnection.writeCANPacket(canPacket);
            } catch (FrameworkException e) {
                e.printStackTrace();
            }
        }else{
            try {
                serialPortConnection.writeCANPacket(canPacket);
            } catch (FrameworkException e) {
                e.printStackTrace();
            }
        }*/
    }

    private void addPacketListener(PacketListener packetListener){
     /*   if(isEthernet){
            ethernetConnection.addPacketListener(packetListener);
        }else{
            serialPortConnection.addPacketListener(packetListener);
        }*/
    }

    private void removePacketListener(PacketListener packetListener){
     /*   if(isEthernet){
            ethernetConnection.removePacketListener(packetListener);
        }else{
            serialPortConnection.removePacketListener(packetListener);
        }*/
    }

    private void pause(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
