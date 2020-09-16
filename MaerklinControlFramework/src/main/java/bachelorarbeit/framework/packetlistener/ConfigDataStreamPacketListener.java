package bachelorarbeit.framework.packetlistener;

import bachelorarbeit.framework.CANPacket;
import bachelorarbeit.framework.CS2CANCommands;
import bachelorarbeit.framework.FrameworkException;

/**
 * Created by ivo on 04.12.15.
 */
public abstract class ConfigDataStreamPacketListener implements PacketListener {
    private int compressedFileLength = -1;
    private int decompressedFileLength = -1;
    private int crc = -1;
    private boolean configDataRequestResponseReceived = false;
    private boolean firstDataPacketReceived = false;
    private byte[] decompressedBytes = null;
    private byte[] compressedBytes = null;
    private int bytesReceived = 0;

    @Override
    public void onPacketEvent(final PacketEvent packetEvent) {
        processConfigDataStreamPacket(packetEvent.getCANPacket());

        if (decompressedBytes != null) {
            compressedFileLength = decompressedFileLength = crc = -1;
            configDataRequestResponseReceived = firstDataPacketReceived = false;
            compressedBytes = null;
            bytesReceived = 0;
            onSuccess();

            decompressedBytes = null;
        }
    }

    @Override
    public abstract void onSuccess();

    @Override
    public abstract void onException(final FrameworkException frameworkException);

    public byte[] getDecompressedBytes() {
        return decompressedBytes;
    }

    private void processConfigDataStreamPacket(final CANPacket canPacket) {
        final byte[] dataBytes = canPacket.getData();

        if (canPacket.getCommand() == CS2CANCommands.GET_CONFIG_DATA_STREAM) {
            switch (canPacket.getDlc()) {

                case CS2CANCommands.GET_CONFIG_DATA_STREAM_REQUEST_RESPONSE_DLC:
                    configDataRequestResponseReceived = true;

                    final byte[] compressedFileLengthBytes = new byte[]{dataBytes[0], dataBytes[1], dataBytes[2], dataBytes[3]};
                    compressedFileLength = ((compressedFileLengthBytes[0] & 0xFF) << 24) | ((compressedFileLengthBytes[1] & 0xFF) << 16)
                            | ((compressedFileLengthBytes[2] & 0xFF) << 8) | ((compressedFileLengthBytes[3] & 0xFF) /*<<0*/);

                    final int remainder = compressedFileLength % 8;
                    if (remainder > 0) {
                        compressedFileLength += 8 - remainder; //fill up
                    }

                    compressedBytes = new byte[compressedFileLength];

                    final byte[] crcBytes = new byte[]{dataBytes[4], dataBytes[5]};
                    crc = (((crcBytes[0] & 0xFF) << 8) | (crcBytes[1] & 0xFF));

                    break;

                case CS2CANCommands.GET_CONFIG_DATA_STREAM_PACKET_DLC:
                    if (configDataRequestResponseReceived) {
                        for (int j = 0; bytesReceived < compressedBytes.length && j < dataBytes.length; bytesReceived++, j++) {
                            compressedBytes[bytesReceived] = dataBytes[j];
                        }

                        if (!firstDataPacketReceived) {
                            firstDataPacketReceived = true;

                            final byte[] decompressedFileLengthBytes = new byte[]{compressedBytes[0], compressedBytes[1], compressedBytes[2], compressedBytes[3]};
                            decompressedFileLength = ((decompressedFileLengthBytes[0] & 0xFF) << 24) | ((decompressedFileLengthBytes[1] & 0xFF) << 16)
                                    | ((decompressedFileLengthBytes[2] & 0xFF) << 8) | ((decompressedFileLengthBytes[3] & 0xFF) /*<<0*/);
                        }

                        if (bytesReceived == compressedBytes.length) {
                            final int calculatedCRC = ConfigDataHelper.calcCRC(compressedBytes); //calculate crc over ALL bytes

                            if (calculatedCRC != crc) {
                                onException(new ConfigDataCrcException(String.format("Incorrect crc value %d but expected %d", calculatedCRC, crc)));
                                return;
                            }

                            final byte[] tempBuffer = new byte[compressedFileLength - 4];
                            System.arraycopy(compressedBytes, 4, tempBuffer, 0, tempBuffer.length); //Skip decompressed file length bytes

                            try {
                                decompressedBytes = ConfigDataHelper.decompressBytes(tempBuffer, decompressedFileLength);
                            } catch (final ConfigDataDecompressException e) {
                                onException(e);
                            }
                        }
                    } else {
                        onException(new ConfigDataMissingRequestResponseException("Config data stream packet received without previous request response packet."));
                    }
                    break;

                default:
                    break;
            }
        }
    }
}
