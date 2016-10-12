# Maerklin Controlling Framework

This framework implements some parts of the [CS2 CAN Protocol](https://www.maerklin.de/fileadmin/media/service/software-updates/cs2CAN-Protokoll-2_0.pdf) and can be used to communicate with a Maerklin Central Station 2. It also provides classes which can be used to write [scripts](https://github.com/cortex42/Maerklin-Controlling-Framework/blob/master/src/bachelorarbeit/testscript/TestScript.java).

Supported connections:
- [PC] <---- Ethernet ----> [CS2]
- [PC] <----> [CC-Schnitte](http://can-digital-bahn.com/modul.php?system=sys5&modul=54) <----> [Digital-Anschlussbox]

Uses [jSerialComm](http://fazecast.github.io/jSerialComm/) for serial port access.

https://github.com/cortex42/Maerklin-Controlling-Framework/tree/master/src/bachelorarbeit/testgui provides an example on how to use the framework.
