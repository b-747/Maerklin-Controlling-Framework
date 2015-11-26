package de.cortex42.maerklin.testgui;

import de.cortex42.maerklin.framework.*;

import java.math.BigInteger;
import java.util.Objects;

/**
 * Created by ivo on 21.10.15.
 */
public class Presenter {
    private final View view;
    private final SerialPortInterface serialPortInterface = SerialPortInterface.getInstance();
    private EthernetInterface ethernetInterface;
    private boolean isEthernet;
    private int selectedLocId;

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
    private static final long DELAY = 250L;

    private final PacketListener debugPacketListener = new PacketListener() {
        @Override
        public void packetEvent(PacketEvent packetEvent) {
            CANPacket canPacket = packetEvent.getCANPacket();

            DebugOutput.write(String.format("----Received: %s\n\t%s",
                    canPacket.getString(),
                    CANPacketInterpreter.interpretCANPacket(canPacket)));
        }
    };


    //todo check hashes (S. 6)

    public Presenter(View view){
        this.view = view;

        view.addLoc("");
        view.addLoc(Integer.toString(MM_LOC_ID));
        view.addLoc(Integer.toString(MFX_LOC_ID));
        view.addLoc(Integer.toString(MFX_LOC_ID2));

        //fill combobox with available interfaces
        view.addInterface("");
        view.addInterface(CS2_IP_ADDRESS);
        for (String serialPortString : serialPortInterface.getAvailableSerialPorts()) {
            view.addInterface(serialPortString);
        }
    }

    public void initialize(){
        String selectedInterface = view.getSelectedInterface();

        if(Objects.equals(selectedInterface, CS2_IP_ADDRESS)){
            isEthernet = true;

            try {
                ethernetInterface = EthernetInterface.getInstance(CS2_PORT);
            } catch (FrameworkException e) {
                e.printStackTrace();
            }
        }else{
            isEthernet = false;

            if(!serialPortInterface.openPort(selectedInterface)){
                DebugOutput.write("Could not open the port.");
            }else{
                DebugOutput.write("Port opened.");
            }
        }

        addPacketListener(debugPacketListener);

        //ugly..
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
        pause();
        sendPacket(CS2CANCommands.unlockRail());
        pause();

        sendPacket(CS2CANCommands.go());
    }

    public void sendStop(){
        sendPacket(CS2CANCommands.stop());
    }

    public void sendBootloaderGo(){
        sendPacket(CS2CANCommands.bootloaderGo());
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
        pause();


        sendPacket(CS2CANCommands.setDirection(selectedLocId, CS2CANCommands.DIRECTION_TOGGLE)); //toggle direction
        pause();

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
        pause();

        sendStart();
        pause();

        sendPacket(CS2CANCommands.queryDirection(selectedLocId)); //query new direction
    }

    public void cleanUp(){
        if(isEthernet){
            if(ethernetInterface != null) {
                ethernetInterface.cleanUp();
            }
        }else{
            serialPortInterface.closePort();
        }

        DebugOutput.write("Cleaned up.");
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
        if(isEthernet){
            try {
                ethernetInterface.writeCANPacket(canPacket, CS2_IP_ADDRESS, CS2_PORT);
            } catch (FrameworkException e) {
                e.printStackTrace();
            }
        }else{
            try {
                serialPortInterface.writeCANPacket(canPacket);
            } catch (FrameworkException e) {
                e.printStackTrace();
            }
        }
    }

    private void addPacketListener(PacketListener packetListener){
        if(isEthernet){
            ethernetInterface.addPacketListener(packetListener);
        }else{
            serialPortInterface.addPacketListener(packetListener);
        }
    }

    private void removePacketListener(PacketListener packetListener){
        if(isEthernet){
            ethernetInterface.removePacketListener(packetListener);
        }else{
            serialPortInterface.removePacketListener(packetListener);
        }
    }

    private void pause() {
        try {
            Thread.sleep(DELAY);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
