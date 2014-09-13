package org.onlab.onos.net;

import static org.onlab.onos.net.DeviceId.deviceId;

import java.util.Set;

import org.onlab.onos.net.provider.ProviderId;
import org.onlab.packet.IPAddress;
import org.onlab.packet.MACAddress;
import org.onlab.packet.VLANID;

import com.google.common.collect.Sets;

/**
 * Provides a set of test DefaultDevice parameters for use with Host-
 * related tests.
 */
public abstract class TestDeviceParams {

    protected static final ProviderId PID = new ProviderId("foo");
    protected static final DeviceId DID1 = deviceId("of:foo");
    protected static final DeviceId DID2 = deviceId("of:bar");
    protected static final MACAddress MAC1 = MACAddress.valueOf("00:11:00:00:00:01");
    protected static final MACAddress MAC2 = MACAddress.valueOf("00:22:00:00:00:02");
    protected static final VLANID VLAN1 = VLANID.vlanId((short) 11);
    protected static final VLANID VLAN2 = VLANID.vlanId((short) 22);
    protected static final IPAddress IP1 = IPAddress.valueOf("10.0.0.1");
    protected static final IPAddress IP2 = IPAddress.valueOf("10.0.0.2");
    protected static final IPAddress IP3 = IPAddress.valueOf("10.0.0.3");

    protected static final PortNumber P1 = PortNumber.portNumber(100);
    protected static final PortNumber P2 = PortNumber.portNumber(200);
    protected static final HostId HID1 = HostId.hostId(MAC1, VLAN1);
    protected static final HostId HID2 = HostId.hostId(MAC2, VLAN2);
    protected static final HostLocation LOC1 = new HostLocation(DID1, P1, 123L);
    protected static final HostLocation LOC2 = new HostLocation(DID2, P2, 123L);
    protected static final Set<IPAddress> IPSET1 = Sets.newHashSet(IP1, IP2);
    protected static final Set<IPAddress> IPSET2 = Sets.newHashSet(IP1, IP3);

}
