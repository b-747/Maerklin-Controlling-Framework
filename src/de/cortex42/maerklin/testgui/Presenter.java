package de.cortex42.maerklin.testgui;

import de.cortex42.maerklin.framework.*;

import java.io.IOException;
import java.math.BigInteger;
import java.net.SocketException;
import java.util.Objects;

/**
 * Created by ivo on 21.10.15.
 */
public class Presenter {
    private final View view;
    private SerialPortInterface serialPortInterface = SerialPortInterface.getInstance();
    private EthernetInterface ethernetInterface;
    private boolean isEthernet;
    private byte[] selectedLocId;

    //todo
    private static final byte[] MM_LOC_ID = new byte[]{0x00, 0x00, 0x00, 0x4E}; //78 = BR 81 (MM)
    private static final byte[] MFX_LOC_ID = new byte[]{0x00, 0x00, 0x40, 0x06}; //RE 460 076-3 (MFX)
    private static final byte[] MFX_LOC_ID2 = new byte[]{0x00, 0x00, 0x40, 0x07}; //AE 6/6 11424
    private static final byte LIGHT_FUNCTION = (byte)0x00; //Light function of MM loc
    private static final byte[] EQUIPMENT_0 = new byte[]{0x00, 0x00, 0x30, 0x00};
    private static final byte[] EQUIPMENT_1 = new byte[]{0x00, 0x00, 0x30, 0x05};
    private static final int PC_PORT = 15730;
    private static final int CS2_PORT = 15731;
    private static final String CS2_IP_ADDRESS = "192.168.16.2";

    private final PacketListener debugPacketListener = packetEvent -> {
        CANPacket canPacket = packetEvent.getCANPacket();

        DebugOutput.write(String.format("----Received: %s\n\t%s",
                canPacket.getString(),
                CANPacketInterpreter.interpretCANPacket(canPacket)));
    };

    /*
    Im System besitzt jeder adressierbare Teilnehmer eine eindeutige 32 Bit Adresse.
    Dabei werden folgende UID unterschieden:
    Geräte-UID Eindeutig vergebene Universal ID.
    Loc-ID (=Local ID, nicht Locomotive ID) Aus dem Protokoll und der Adresse berechnete Lokale ID.
    MFX-UID MFX Universal ID, eindeutige Kennung eines mfx Teilnehmers.
    (Loc-ID = SID!)
    Die Lage der Loc-ID (Local-ID, NICHT Locomotive-ID) im Adressraum bestimmt das Protokoll (S. 8)


    Beispiel (Hex):
    Märklin Motorola mit Adresse 2: Basis: 00 00 00 00 Plus Adresse: 00 00 00 02
    MM2 Zubehör mit Adresse 3: Basis: 00 00 30 00 Plus Adresse: 00 00 30 03
    */

    //todo Anfordern Config Data (S. 46), Format der Konfigurationsdateien der CS2
    //get config: https://github.com/GBert/railroad/blob/6c3519682aabf6c19438c8a5824db0f7e2f11dd4/can2udp/src/get-cs-config.c

    //todo check hashes (S. 6)

    //Device ID 0040 = S88 Link
    //Decoder m83 Weichen
    //m84 Gleisabschaltung

    //todo fahrstufen berechnen!

    //todo weiche an/aus und stromschalter: Zeit zwischen Schalten = 200ms

    public Presenter(View view){
        this.view = view;

        view.addLoc("");
        view.addLoc(Integer.toString(new BigInteger(MM_LOC_ID).intValue()));
        view.addLoc(Integer.toString(new BigInteger(MFX_LOC_ID).intValue()));
        view.addLoc(Integer.toString(new BigInteger(MFX_LOC_ID2).intValue()));

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
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }else{
            isEthernet = false;

            if(!serialPortInterface.openPort(selectedInterface)){
                DebugOutput.write("Could not open the port.");
            }
        }

        addPacketListener(debugPacketListener);

        //ugly..
        String selectedLoc = view.getSelectedLoc();
        String mmLoc = Integer.toString(new BigInteger(MM_LOC_ID).intValue());
        String mfxLoc1 = Integer.toString(new BigInteger(MFX_LOC_ID).intValue());

        if(Objects.equals(selectedLoc, mmLoc)){
            selectedLocId = MM_LOC_ID;
        }else if(Objects.equals(selectedLoc, mfxLoc1)){
            selectedLocId = MFX_LOC_ID;
        }else{
            selectedLocId = MFX_LOC_ID2;
        }
    }

    public void sendStart(){
        sendPacket(CS2CANCommands.newRegistration());
        sendPacket(CS2CANCommands.unlockRail());
        sendPacket(CS2CANCommands.go());
    }

    public void sendStop(){
        sendPacket(CS2CANCommands.stop());
    }

    public void sendBootloaderGo(){
        sendPacket(CS2CANCommands.bootloaderGo());
    }

    public void sendLight(boolean on){
        sendPacket(CS2CANCommands.toggleFunction(LIGHT_FUNCTION, selectedLocId, on? CS2CANCommands.FUNCTION_ON: CS2CANCommands.FUNCTION_OFF));
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
        pause(50);

        sendPacket(CS2CANCommands.setDirection(selectedLocId, CS2CANCommands.DIRECTION_TOGGLE)); //toggle direction
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
        sendStart();
        sendPacket(CS2CANCommands.queryDirection()); //query new direction
        //todo does not work...
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

                        if (command != CS2CANCommands.VELOCITY) {
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
                ethernetInterface.writeCANPacket(canPacket, CS2_IP_ADDRESS, PC_PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            serialPortInterface.writeCANPacket(canPacket);
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

    private void pause(long ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
