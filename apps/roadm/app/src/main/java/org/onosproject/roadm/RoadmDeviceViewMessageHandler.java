/*
 * Copyright 2016-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onosproject.roadm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableSet;
import org.onlab.osgi.ServiceDirectory;
import org.onosproject.mastership.MastershipService;
import org.onosproject.net.AnnotationKeys;
import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import org.onosproject.net.device.DeviceService;
import org.onosproject.ui.RequestHandler;
import org.onosproject.ui.UiConnection;
import org.onosproject.ui.UiMessageHandler;
import org.onosproject.ui.table.TableModel;
import org.onosproject.ui.table.TableRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.json.JSONObject;
//import org.json.JSONException;


import java.util.Collection;

import static org.onosproject.net.Device.Type;

/**
 * Table-View message handler for ROADM device view.
 */
public class RoadmDeviceViewMessageHandler extends UiMessageHandler {

    private static final String ROADM_DEVICE_DATA_REQ = "roadmDataRequest";
    private static final String ROADM_DEVICE_DATA_RESP = "roadmDataResponse";
    private static final String ROADM_DEVICES = "roadms";

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String MASTER = "master";
    private static final String PORTS = "ports";
    private static final String VENDOR = "vendor";
    private static final String HW_VERSION = "hwVersion";
    private static final String SW_VERSION = "swVersion";
    private static final String PROTOCOL = "protocol";

    //定义触发数据类型
    private static final String HI_SGP_RESP = "hiResponse";
    private static final String THERECEIVE = "receive message";

    private static final String HI_SGP_RESP_DCI = "yiResponse";
    private static final String HI_SGP_RESP_DCN = "dcnResponse";

    private static final String HI_SGP_QINGHUA = "alResponse";
    private static final String THERECEIVE_QINGHUA = "serviceList message";


    private static final String STATUS = "status";
    private static final String USER_NAME = "user_name";
    private static final String TENANT_NAME = "tenant_name";
    private static final String CREATE_TIME = "creat_time";

    //发送字符
    public String theFirst;
    public String theSecond;
    public String theThird;
    public String theFourth;

    public String theDCI_1;
    public String theDCI_2;
    public String theDCI_3;
    public String theDCI_4;
    public String theDCI_5;
    public String theDCI_6;

    private static final String HELLO_QINGHUA_REQ = "alRequest";
    private static final String THE_QINGHUA_LIST = "servicelist";
    //public Object THE_QINGHUA_LIST = "servicelist";
    //public String THE_QINGHUA_LIST = "servicelist";
    //private static final String THE_QINGHUA_LIST = "servicelist";
    public String theQINGHUA;

    private static final String HELLO_DCN_REQ = "dcnRequest";
    private static final String THE_DCN_STATUS = "get";
    public String theDCN;
    private static final String DEVICE = "device";
    private static final String PORT = "port";
    private static final String CONTROL = "control";



    private static final String HELLO_DCI_REQ = "dciRequest";
    private static final String THE_DCI_SRC_NEID = "srcNeId";
    private static final String THE_DCI_SRC_CLIENTEP = "srcClientEp";
    private static final String THE_DCI_SRC_PHYSICALCHANNEL = "srcPhysicalChannel";
    private static final String THE_DCI_DST_NEID = "dstNeId";
    private static final String THE_DCI_DST_CLIENTEP = "dstClientEp";
    private static final String THE_DCI_PHYSICALCHANNEL = "dstPhysicalChannel";
    private static final String VM_REQ = "vmCreateRequest";
    private static final String THE_NAME = "name";
    private static final String THE_IMAGE_REF = "imageRef";
    private static final String THE_UUID = "uuid";
    private static final String THE_FLAVOR_REF = "flavorRef";


    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String[] COLUMN_IDS = {
            ID, NAME, TYPE, MASTER, PORTS, VENDOR, HW_VERSION, SW_VERSION, PROTOCOL
    };

    private DeviceService deviceService;
    private MastershipService mastershipService;

    @Override
    public void init(UiConnection connection, ServiceDirectory directory) {
        super.init(connection, directory);
        deviceService = get(DeviceService.class);
        mastershipService = get(MastershipService.class);
    }

    @Override
    protected Collection<RequestHandler> createRequestHandlers() {
        return ImmutableSet.of(new DeviceTableDataRequestHandler(), new SayHelloWorld(), new SayHelloWorld1(), new SayHelloWorld2(), new SayHelloWorld3());
    }

    // Handler for sample table requests
    private final class DeviceTableDataRequestHandler extends TableRequestHandler {

        private DeviceTableDataRequestHandler() {
            super(ROADM_DEVICE_DATA_REQ, ROADM_DEVICE_DATA_RESP, ROADM_DEVICES);
        }

        @Override
        protected String[] getColumnIds() {
            return COLUMN_IDS;
        }

        @Override
        protected String noRowsMessage(ObjectNode payload) {
            return RoadmUtil.NO_ROWS_MESSAGE;
        }

        @Override
        protected void populateTable(TableModel tm, ObjectNode payload) {
            for (Device device : deviceService.getDevices()) {
                Type type = device.type();
                if (type == Type.ROADM || type == Type.TERMINAL_DEVICE
                        || type == Type.OPTICAL_AMPLIFIER || type == Type.FIBER_SWITCH
                        || type == Type.ROADM_OTN
                        || type == Type.OTN
                        || type == Type.OLS) {
                    populateRow(tm.addRow(), device);
                }
            }
        }

        private void populateRow(TableModel.Row row, Device device) {
            DeviceId devId = device.id();
            String id = devId.toString();
            row.cell(ID, id)
                    .cell(NAME, RoadmUtil.getAnnotation(device.annotations(), AnnotationKeys.NAME, id))
                    .cell(TYPE, RoadmUtil.objectToString(device.type(), RoadmUtil.UNKNOWN))
                    .cell(MASTER, mastershipService.getMasterFor(devId))
                    .cell(PORTS, deviceService.getPorts(devId).size())
                    .cell(VENDOR, device.manufacturer())
                    .cell(HW_VERSION, device.hwVersion())
                    .cell(SW_VERSION, device.swVersion())
                    .cell(PROTOCOL, RoadmUtil.getAnnotation(device.annotations(), PROTOCOL));
        }
    }

    private final class SayHelloWorld extends RequestHandler {
        private SayHelloWorld() {
            super(VM_REQ);
        }


        @Override
        public void process(ObjectNode payload) {
//            theFirst = string(payload, THE_FIRST_WORD);
//            log.info("The first word : {}",theFirst);
//            theSecond = string(payload, THE_SECOND_WORD);
//            log.info("The second word : {}",theSecond);

            theFirst = string(payload, THE_NAME);
            log.info("The first word : {}", theFirst);
            theSecond = string(payload, THE_IMAGE_REF);
            log.info("The second word : {}", theSecond);
            theThird = string(payload, THE_UUID);
            log.info("The Third word : {}", theThird);
            theFourth = string(payload, THE_FLAVOR_REF);
            log.info("The Fourth word : {}", theFourth);
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("{\n" +
                    "    \"servers\": [\n" +
                    "        {\n" +
                    "            \"OS-EXT-STS:task_state\": null,\n" +
                    "            \"addresses\": {\n" +
                    "                \"to-huake\": [\n" +
                    "                    {\n" +
                    "                        \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:a6:67:81\",\n" +
                    "                        \"version\": 4,\n" +
                    "                        \"addr\": \"200.1.1.121\",\n" +
                    "                        \"OS-EXT-IPS:type\": \"fixed\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"links\": [\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/v2.1/servers/d1538d50-9b43-40ae-a424-76c53f1c5f2a\",\n" +
                    "                    \"rel\": \"self\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/servers/d1538d50-9b43-40ae-a424-76c53f1c5f2a\",\n" +
                    "                    \"rel\": \"bookmark\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"image\": {\n" +
                    "                \"id\": \"8113da59-e608-40ab-a7e9-d4191127dc88\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/images/8113da59-e608-40ab-a7e9-d4191127dc88\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"OS-EXT-STS:vm_state\": \"active\",\n" +
                    "            \"OS-EXT-SRV-ATTR:instance_name\": \"instance-00000443\",\n" +
                    "            \"OS-SRV-USG:launched_at\": \"2023-02-22T08:38:01.000000\",\n" +
                    "            \"flavor\": {\n" +
                    "                \"id\": \"4U8G100G\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/flavors/4U8G100G\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"id\": \"d1538d50-9b43-40ae-a424-76c53f1c5f2a\",\n" +
                    "            \"security_groups\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"user_id\": \"c88ea64767af4106b9e88fae93c2ff88\",\n" +
                    "            \"OS-DCF:diskConfig\": \"AUTO\",\n" +
                    "            \"accessIPv4\": \"1.2.3.4\",\n" +
                    "            \"accessIPv6\": \"80fe::\",\n" +
                    "            \"progress\": 0,\n" +
                    "            \"OS-EXT-STS:power_state\": 1,\n" +
                    "            \"OS-EXT-AZ:availability_zone\": \"nova\",\n" +
                    "            \"config_drive\": \"\",\n" +
                    "            \"status\": \"ACTIVE\",\n" +
                    "            \"updated\": \"2023-02-22T08:38:01Z\",\n" +
                    "            \"hostId\": \"cd271688b71f9674f85e30498ad232c6aac8968b56a2be3c4053afca\",\n" +
                    "            \"OS-EXT-SRV-ATTR:host\": \"host-3\",\n" +
                    "            \"OS-SRV-USG:terminated_at\": null,\n" +
                    "            \"key_name\": null,\n" +
                    "            \"OS-EXT-SRV-ATTR:hypervisor_hostname\": \"host-3\",\n" +
                    "            \"name\": \"show-test2-2023-2-22\",\n" +
                    "            \"created\": \"2023-02-22T08:36:21Z\",\n" +
                    "            \"tenant_id\": \"0415bfa3cc324b65a60b28557f1a9bb1\",\n" +
                    "            \"os-extended-volumes:volumes_attached\": [],\n" +
                    "            \"metadata\": {\n" +
                    "                \"My Server Name\": \"Apache1\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"OS-EXT-STS:task_state\": null,\n" +
                    "            \"addresses\": {\n" +
                    "                \"to-huake\": [\n" +
                    "                    {\n" +
                    "                        \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:8c:c7:43\",\n" +
                    "                        \"version\": 4,\n" +
                    "                        \"addr\": \"200.1.1.115\",\n" +
                    "                        \"OS-EXT-IPS:type\": \"fixed\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"links\": [\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/v2.1/servers/97fa7251-1a2f-4fef-b738-b19d53d4437a\",\n" +
                    "                    \"rel\": \"self\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/servers/97fa7251-1a2f-4fef-b738-b19d53d4437a\",\n" +
                    "                    \"rel\": \"bookmark\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"image\": {\n" +
                    "                \"id\": \"8113da59-e608-40ab-a7e9-d4191127dc88\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/images/8113da59-e608-40ab-a7e9-d4191127dc88\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"OS-EXT-STS:vm_state\": \"active\",\n" +
                    "            \"OS-EXT-SRV-ATTR:instance_name\": \"instance-0000043f\",\n" +
                    "            \"OS-SRV-USG:launched_at\": \"2023-02-22T08:16:46.000000\",\n" +
                    "            \"flavor\": {\n" +
                    "                \"id\": \"4U8G100G\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/flavors/4U8G100G\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"id\": \"97fa7251-1a2f-4fef-b738-b19d53d4437a\",\n" +
                    "            \"security_groups\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"user_id\": \"c88ea64767af4106b9e88fae93c2ff88\",\n" +
                    "            \"OS-DCF:diskConfig\": \"AUTO\",\n" +
                    "            \"accessIPv4\": \"1.2.3.4\",\n" +
                    "            \"accessIPv6\": \"80fe::\",\n" +
                    "            \"progress\": 0,\n" +
                    "            \"OS-EXT-STS:power_state\": 1,\n" +
                    "            \"OS-EXT-AZ:availability_zone\": \"nova\",\n" +
                    "            \"config_drive\": \"\",\n" +
                    "            \"status\": \"ACTIVE\",\n" +
                    "            \"updated\": \"2023-02-22T08:16:46Z\",\n" +
                    "            \"hostId\": \"81e87e1fe9e78c3a26f626d9705464a890e380a92c20702adc76a91a\",\n" +
                    "            \"OS-EXT-SRV-ATTR:host\": \"host-6\",\n" +
                    "            \"OS-SRV-USG:terminated_at\": null,\n" +
                    "            \"key_name\": null,\n" +
                    "            \"OS-EXT-SRV-ATTR:hypervisor_hostname\": \"host-6\",\n" +
                    "            \"name\": \"test2-2023-2-22\",\n" +
                    "            \"created\": \"2023-02-22T08:15:13Z\",\n" +
                    "            \"tenant_id\": \"0415bfa3cc324b65a60b28557f1a9bb1\",\n" +
                    "            \"os-extended-volumes:volumes_attached\": [],\n" +
                    "            \"metadata\": {\n" +
                    "                \"My Server Name\": \"Apache1\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"OS-EXT-STS:task_state\": null,\n" +
                    "            \"addresses\": {\n" +
                    "                \"to-huake\": [\n" +
                    "                    {\n" +
                    "                        \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:5f:cc:38\",\n" +
                    "                        \"version\": 4,\n" +
                    "                        \"addr\": \"200.1.1.117\",\n" +
                    "                        \"OS-EXT-IPS:type\": \"fixed\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"links\": [\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/v2.1/servers/02ba251c-4705-4c98-8746-24f88e6c1113\",\n" +
                    "                    \"rel\": \"self\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/servers/02ba251c-4705-4c98-8746-24f88e6c1113\",\n" +
                    "                    \"rel\": \"bookmark\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"image\": {\n" +
                    "                \"id\": \"8113da59-e608-40ab-a7e9-d4191127dc88\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/images/8113da59-e608-40ab-a7e9-d4191127dc88\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"OS-EXT-STS:vm_state\": \"active\",\n" +
                    "            \"OS-EXT-SRV-ATTR:instance_name\": \"instance-0000043d\",\n" +
                    "            \"OS-SRV-USG:launched_at\": \"2023-02-22T02:59:41.000000\",\n" +
                    "            \"flavor\": {\n" +
                    "                \"id\": \"4U8G100G\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/flavors/4U8G100G\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"id\": \"02ba251c-4705-4c98-8746-24f88e6c1113\",\n" +
                    "            \"security_groups\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"user_id\": \"c88ea64767af4106b9e88fae93c2ff88\",\n" +
                    "            \"OS-DCF:diskConfig\": \"AUTO\",\n" +
                    "            \"accessIPv4\": \"1.2.3.4\",\n" +
                    "            \"accessIPv6\": \"80fe::\",\n" +
                    "            \"progress\": 0,\n" +
                    "            \"OS-EXT-STS:power_state\": 1,\n" +
                    "            \"OS-EXT-AZ:availability_zone\": \"nova\",\n" +
                    "            \"config_drive\": \"\",\n" +
                    "            \"status\": \"ACTIVE\",\n" +
                    "            \"updated\": \"2023-02-22T02:59:42Z\",\n" +
                    "            \"hostId\": \"8faf15a78c0d364d4b4783833922175d89d41d68b83e7c4434930b28\",\n" +
                    "            \"OS-EXT-SRV-ATTR:host\": \"host-2\",\n" +
                    "            \"OS-SRV-USG:terminated_at\": null,\n" +
                    "            \"key_name\": null,\n" +
                    "            \"OS-EXT-SRV-ATTR:hypervisor_hostname\": \"host-2\",\n" +
                    "            \"name\": \"test2-2023-2-22\",\n" +
                    "            \"created\": \"2023-02-22T02:51:36Z\",\n" +
                    "            \"tenant_id\": \"0415bfa3cc324b65a60b28557f1a9bb1\",\n" +
                    "            \"os-extended-volumes:volumes_attached\": [],\n" +
                    "            \"metadata\": {\n" +
                    "                \"My Server Name\": \"Apache1\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"OS-EXT-STS:task_state\": null,\n" +
                    "            \"addresses\": {\n" +
                    "                \"to-huake\": [\n" +
                    "                    {\n" +
                    "                        \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:4f:7f:c1\",\n" +
                    "                        \"version\": 4,\n" +
                    "                        \"addr\": \"200.1.1.110\",\n" +
                    "                        \"OS-EXT-IPS:type\": \"fixed\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"links\": [\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/v2.1/servers/1ab7a2b7-44b3-4d21-8fbb-4eb0a95695eb\",\n" +
                    "                    \"rel\": \"self\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/servers/1ab7a2b7-44b3-4d21-8fbb-4eb0a95695eb\",\n" +
                    "                    \"rel\": \"bookmark\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"image\": {\n" +
                    "                \"id\": \"8113da59-e608-40ab-a7e9-d4191127dc88\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/images/8113da59-e608-40ab-a7e9-d4191127dc88\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"OS-EXT-STS:vm_state\": \"active\",\n" +
                    "            \"OS-EXT-SRV-ATTR:instance_name\": \"instance-0000043c\",\n" +
                    "            \"OS-SRV-USG:launched_at\": \"2023-02-22T03:00:38.000000\",\n" +
                    "            \"flavor\": {\n" +
                    "                \"id\": \"4U8G100G\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/flavors/4U8G100G\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"id\": \"1ab7a2b7-44b3-4d21-8fbb-4eb0a95695eb\",\n" +
                    "            \"security_groups\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"user_id\": \"c88ea64767af4106b9e88fae93c2ff88\",\n" +
                    "            \"OS-DCF:diskConfig\": \"AUTO\",\n" +
                    "            \"accessIPv4\": \"1.2.3.4\",\n" +
                    "            \"accessIPv6\": \"80fe::\",\n" +
                    "            \"progress\": 0,\n" +
                    "            \"OS-EXT-STS:power_state\": 1,\n" +
                    "            \"OS-EXT-AZ:availability_zone\": \"nova\",\n" +
                    "            \"config_drive\": \"\",\n" +
                    "            \"status\": \"ACTIVE\",\n" +
                    "            \"updated\": \"2023-02-22T03:00:38Z\",\n" +
                    "            \"hostId\": \"91f4d3df8cba0106b9df7e3fb3a68b8d868446a9bfb43ec7622f8ce6\",\n" +
                    "            \"OS-EXT-SRV-ATTR:host\": \"host-4\",\n" +
                    "            \"OS-SRV-USG:terminated_at\": null,\n" +
                    "            \"key_name\": null,\n" +
                    "            \"OS-EXT-SRV-ATTR:hypervisor_hostname\": \"host-4\",\n" +
                    "            \"name\": \"test-2023-2-22\",\n" +
                    "            \"created\": \"2023-02-22T02:51:26Z\",\n" +
                    "            \"tenant_id\": \"0415bfa3cc324b65a60b28557f1a9bb1\",\n" +
                    "            \"os-extended-volumes:volumes_attached\": [],\n" +
                    "            \"metadata\": {\n" +
                    "                \"My Server Name\": \"Apache1\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"OS-EXT-STS:task_state\": null,\n" +
                    "            \"addresses\": {\n" +
                    "                \"aaa\": [\n" +
                    "                    {\n" +
                    "                        \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:94:0a:77\",\n" +
                    "                        \"version\": 4,\n" +
                    "                        \"addr\": \"77.1.1.5\",\n" +
                    "                        \"OS-EXT-IPS:type\": \"fixed\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"links\": [\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/v2.1/servers/2d1a3ea1-53b3-431c-a1a0-2568242ed32e\",\n" +
                    "                    \"rel\": \"self\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/servers/2d1a3ea1-53b3-431c-a1a0-2568242ed32e\",\n" +
                    "                    \"rel\": \"bookmark\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"image\": {\n" +
                    "                \"id\": \"8113da59-e608-40ab-a7e9-d4191127dc88\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/images/8113da59-e608-40ab-a7e9-d4191127dc88\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"OS-EXT-STS:vm_state\": \"active\",\n" +
                    "            \"OS-EXT-SRV-ATTR:instance_name\": \"instance-00000436\",\n" +
                    "            \"OS-SRV-USG:launched_at\": \"2023-02-17T07:50:01.000000\",\n" +
                    "            \"flavor\": {\n" +
                    "                \"id\": \"4U8G100G\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/flavors/4U8G100G\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"id\": \"2d1a3ea1-53b3-431c-a1a0-2568242ed32e\",\n" +
                    "            \"security_groups\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"user_id\": \"c88ea64767af4106b9e88fae93c2ff88\",\n" +
                    "            \"OS-DCF:diskConfig\": \"MANUAL\",\n" +
                    "            \"accessIPv4\": \"\",\n" +
                    "            \"accessIPv6\": \"\",\n" +
                    "            \"progress\": 0,\n" +
                    "            \"OS-EXT-STS:power_state\": 1,\n" +
                    "            \"OS-EXT-AZ:availability_zone\": \"nova\",\n" +
                    "            \"config_drive\": \"True\",\n" +
                    "            \"status\": \"ACTIVE\",\n" +
                    "            \"updated\": \"2023-02-17T07:50:01Z\",\n" +
                    "            \"hostId\": \"1224879040fecf46e67c71ae19b29785b6e48a7baebc9ce9a65d66a3\",\n" +
                    "            \"OS-EXT-SRV-ATTR:host\": \"host-7\",\n" +
                    "            \"OS-SRV-USG:terminated_at\": null,\n" +
                    "            \"key_name\": null,\n" +
                    "            \"OS-EXT-SRV-ATTR:hypervisor_hostname\": \"host-7\",\n" +
                    "            \"name\": \"11\",\n" +
                    "            \"created\": \"2023-02-17T07:44:07Z\",\n" +
                    "            \"tenant_id\": \"0415bfa3cc324b65a60b28557f1a9bb1\",\n" +
                    "            \"os-extended-volumes:volumes_attached\": [],\n" +
                    "            \"metadata\": {\n" +
                    "                \"ha\": \"False\",\n" +
                    "                \"flatten\": \"False\"\n" +
                    "            }\n" +
                    "        },");
            stringBuffer.append("{\n" +
                    "            \"OS-EXT-STS:task_state\": null,\n" +
                    "            \"addresses\": {\n" +
                    "                \"to-huake\": [\n" +
                    "                    {\n" +
                    "                        \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:4e:cf:6f\",\n" +
                    "                        \"version\": 4,\n" +
                    "                        \"addr\": \"200.1.1.112\",\n" +
                    "                        \"OS-EXT-IPS:type\": \"fixed\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"links\": [\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/v2.1/servers/7d5060cd-f703-4c14-b29a-87a0c2e74be0\",\n" +
                    "                    \"rel\": \"self\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/servers/7d5060cd-f703-4c14-b29a-87a0c2e74be0\",\n" +
                    "                    \"rel\": \"bookmark\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"image\": {\n" +
                    "                \"id\": \"8847719c-f78b-41ab-ad72-43d427534560\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/images/8847719c-f78b-41ab-ad72-43d427534560\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"OS-EXT-STS:vm_state\": \"active\",\n" +
                    "            \"OS-EXT-SRV-ATTR:instance_name\": \"instance-00000435\",\n" +
                    "            \"OS-SRV-USG:launched_at\": \"2023-02-16T08:04:23.000000\",\n" +
                    "            \"flavor\": {\n" +
                    "                \"id\": \"4U16G100G\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/flavors/4U16G100G\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"id\": \"7d5060cd-f703-4c14-b29a-87a0c2e74be0\",\n" +
                    "            \"security_groups\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"user_id\": \"c88ea64767af4106b9e88fae93c2ff88\",\n" +
                    "            \"OS-DCF:diskConfig\": \"MANUAL\",\n" +
                    "            \"accessIPv4\": \"\",\n" +
                    "            \"accessIPv6\": \"\",\n" +
                    "            \"progress\": 0,\n" +
                    "            \"OS-EXT-STS:power_state\": 1,\n" +
                    "            \"OS-EXT-AZ:availability_zone\": \"nova\",\n" +
                    "            \"config_drive\": \"True\",\n" +
                    "            \"status\": \"ACTIVE\",\n" +
                    "            \"updated\": \"2023-02-16T08:04:23Z\",\n" +
                    "            \"hostId\": \"8faf15a78c0d364d4b4783833922175d89d41d68b83e7c4434930b28\",\n" +
                    "            \"OS-EXT-SRV-ATTR:host\": \"host-2\",\n" +
                    "            \"OS-SRV-USG:terminated_at\": null,\n" +
                    "            \"key_name\": null,\n" +
                    "            \"OS-EXT-SRV-ATTR:hypervisor_hostname\": \"host-2\",\n" +
                    "            \"name\": \"hustest\",\n" +
                    "            \"created\": \"2023-02-16T08:04:10Z\",\n" +
                    "            \"tenant_id\": \"0415bfa3cc324b65a60b28557f1a9bb1\",\n" +
                    "            \"os-extended-volumes:volumes_attached\": [],\n" +
                    "            \"metadata\": {\n" +
                    "                \"ha\": \"False\",\n" +
                    "                \"flatten\": \"False\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"OS-EXT-STS:task_state\": null,\n" +
                    "            \"addresses\": {\n" +
                    "                \"to-huake\": [\n" +
                    "                    {\n" +
                    "                        \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:63:2f:7f\",\n" +
                    "                        \"version\": 4,\n" +
                    "                        \"addr\": \"200.1.1.104\",\n" +
                    "                        \"OS-EXT-IPS:type\": \"fixed\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"links\": [\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/v2.1/servers/04bf042e-d58b-476e-a63b-c221c724fc8a\",\n" +
                    "                    \"rel\": \"self\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/servers/04bf042e-d58b-476e-a63b-c221c724fc8a\",\n" +
                    "                    \"rel\": \"bookmark\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"image\": {\n" +
                    "                \"id\": \"8113da59-e608-40ab-a7e9-d4191127dc88\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/images/8113da59-e608-40ab-a7e9-d4191127dc88\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"OS-EXT-STS:vm_state\": \"active\",\n" +
                    "            \"OS-EXT-SRV-ATTR:instance_name\": \"instance-00000434\",\n" +
                    "            \"OS-SRV-USG:launched_at\": \"2023-02-16T06:56:44.000000\",\n" +
                    "            \"flavor\": {\n" +
                    "                \"id\": \"4U8G100G\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/flavors/4U8G100G\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"id\": \"04bf042e-d58b-476e-a63b-c221c724fc8a\",\n" +
                    "            \"security_groups\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"user_id\": \"c88ea64767af4106b9e88fae93c2ff88\",\n" +
                    "            \"OS-DCF:diskConfig\": \"MANUAL\",\n" +
                    "            \"accessIPv4\": \"\",\n" +
                    "            \"accessIPv6\": \"\",\n" +
                    "            \"progress\": 0,\n" +
                    "            \"OS-EXT-STS:power_state\": 1,\n" +
                    "            \"OS-EXT-AZ:availability_zone\": \"nova\",\n" +
                    "            \"config_drive\": \"True\",\n" +
                    "            \"status\": \"ACTIVE\",\n" +
                    "            \"updated\": \"2023-02-16T06:56:45Z\",\n" +
                    "            \"hostId\": \"d8605b766393c66482072ae942de9617f3951e8344c50d7eb3d3723f\",\n" +
                    "            \"OS-EXT-SRV-ATTR:host\": \"host-1\",\n" +
                    "            \"OS-SRV-USG:terminated_at\": null,\n" +
                    "            \"key_name\": null,\n" +
                    "            \"OS-EXT-SRV-ATTR:hypervisor_hostname\": \"host-1\",\n" +
                    "            \"name\": \"zzc\",\n" +
                    "            \"created\": \"2023-02-16T06:49:48Z\",\n" +
                    "            \"tenant_id\": \"0415bfa3cc324b65a60b28557f1a9bb1\",\n" +
                    "            \"os-extended-volumes:volumes_attached\": [],\n" +
                    "            \"metadata\": {\n" +
                    "                \"ha\": \"False\",\n" +
                    "                \"flatten\": \"False\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"OS-EXT-STS:task_state\": null,\n" +
                    "            \"addresses\": {\n" +
                    "                \"VmService1\": [\n" +
                    "                    {\n" +
                    "                        \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:19:6c:90\",\n" +
                    "                        \"version\": 4,\n" +
                    "                        \"addr\": \"100.1.1.111\",\n" +
                    "                        \"OS-EXT-IPS:type\": \"fixed\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"links\": [\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/v2.1/servers/dc569c4e-cd8c-44e8-a23a-6a25c26784c5\",\n" +
                    "                    \"rel\": \"self\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/servers/dc569c4e-cd8c-44e8-a23a-6a25c26784c5\",\n" +
                    "                    \"rel\": \"bookmark\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"image\": {\n" +
                    "                \"id\": \"20ec9dae-3a41-469d-bc6d-ef9abcbaf574\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/images/20ec9dae-3a41-469d-bc6d-ef9abcbaf574\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"OS-EXT-STS:vm_state\": \"active\",\n" +
                    "            \"OS-EXT-SRV-ATTR:instance_name\": \"instance-00000430\",\n" +
                    "            \"OS-SRV-USG:launched_at\": \"2023-01-29T02:59:07.000000\",\n" +
                    "            \"flavor\": {\n" +
                    "                \"id\": \"m1.small\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/flavors/m1.small\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"id\": \"dc569c4e-cd8c-44e8-a23a-6a25c26784c5\",\n" +
                    "            \"security_groups\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"user_id\": \"c88ea64767af4106b9e88fae93c2ff88\",\n" +
                    "            \"OS-DCF:diskConfig\": \"MANUAL\",\n" +
                    "            \"accessIPv4\": \"\",\n" +
                    "            \"accessIPv6\": \"\",\n" +
                    "            \"progress\": 0,\n" +
                    "            \"OS-EXT-STS:power_state\": 1,\n" +
                    "            \"OS-EXT-AZ:availability_zone\": \"nova\",\n" +
                    "            \"config_drive\": \"True\",\n" +
                    "            \"status\": \"ACTIVE\",\n" +
                    "            \"updated\": \"2023-01-29T02:59:07Z\",\n" +
                    "            \"hostId\": \"91f4d3df8cba0106b9df7e3fb3a68b8d868446a9bfb43ec7622f8ce6\",\n" +
                    "            \"OS-EXT-SRV-ATTR:host\": \"host-4\",\n" +
                    "            \"OS-SRV-USG:terminated_at\": null,\n" +
                    "            \"key_name\": null,\n" +
                    "            \"OS-EXT-SRV-ATTR:hypervisor_hostname\": \"host-4\",\n" +
                    "            \"name\": \"dddd-3\",\n" +
                    "            \"created\": \"2023-01-29T02:58:53Z\",\n" +
                    "            \"tenant_id\": \"0415bfa3cc324b65a60b28557f1a9bb1\",\n" +
                    "            \"os-extended-volumes:volumes_attached\": [],\n" +
                    "            \"metadata\": {\n" +
                    "                \"ha\": \"False\",\n" +
                    "                \"flatten\": \"False\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"OS-EXT-STS:task_state\": null,\n" +
                    "            \"addresses\": {\n" +
                    "                \"VmService1\": [\n" +
                    "                    {\n" +
                    "                        \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:29:4c:b1\",\n" +
                    "                        \"version\": 4,\n" +
                    "                        \"addr\": \"100.1.1.103\",\n" +
                    "                        \"OS-EXT-IPS:type\": \"fixed\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"links\": [\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/v2.1/servers/ad6f5168-f4da-4b70-9392-7da6427c4c80\",\n" +
                    "                    \"rel\": \"self\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/servers/ad6f5168-f4da-4b70-9392-7da6427c4c80\",\n" +
                    "                    \"rel\": \"bookmark\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"image\": {\n" +
                    "                \"id\": \"20ec9dae-3a41-469d-bc6d-ef9abcbaf574\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/images/20ec9dae-3a41-469d-bc6d-ef9abcbaf574\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"OS-EXT-STS:vm_state\": \"active\",\n" +
                    "            \"OS-EXT-SRV-ATTR:instance_name\": \"instance-0000042d\",\n" +
                    "            \"OS-SRV-USG:launched_at\": \"2023-01-29T02:59:06.000000\",\n" +
                    "            \"flavor\": {\n" +
                    "                \"id\": \"m1.small\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/flavors/m1.small\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"id\": \"ad6f5168-f4da-4b70-9392-7da6427c4c80\",\n" +
                    "            \"security_groups\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"user_id\": \"c88ea64767af4106b9e88fae93c2ff88\",\n" +
                    "            \"OS-DCF:diskConfig\": \"MANUAL\",\n" +
                    "            \"accessIPv4\": \"\",\n" +
                    "            \"accessIPv6\": \"\",\n" +
                    "            \"progress\": 0,\n" +
                    "            \"OS-EXT-STS:power_state\": 1,\n" +
                    "            \"OS-EXT-AZ:availability_zone\": \"nova\",\n" +
                    "            \"config_drive\": \"True\",\n" +
                    "            \"status\": \"ACTIVE\",\n" +
                    "            \"updated\": \"2023-01-29T02:59:06Z\",\n" +
                    "            \"hostId\": \"1224879040fecf46e67c71ae19b29785b6e48a7baebc9ce9a65d66a3\",\n" +
                    "            \"OS-EXT-SRV-ATTR:host\": \"host-7\",\n" +
                    "            \"OS-SRV-USG:terminated_at\": null,\n" +
                    "            \"key_name\": null,\n" +
                    "            \"OS-EXT-SRV-ATTR:hypervisor_hostname\": \"host-7\",\n" +
                    "            \"name\": \"dddd-2\",\n" +
                    "            \"created\": \"2023-01-29T02:58:53Z\",\n" +
                    "            \"tenant_id\": \"0415bfa3cc324b65a60b28557f1a9bb1\",\n" +
                    "            \"os-extended-volumes:volumes_attached\": [],\n" +
                    "            \"metadata\": {\n" +
                    "                \"ha\": \"False\",\n" +
                    "                \"flatten\": \"False\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"OS-EXT-STS:task_state\": null,\n" +
                    "            \"addresses\": {\n" +
                    "                \"VmService1\": [\n" +
                    "                    {\n" +
                    "                        \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:95:76:41\",\n" +
                    "                        \"version\": 4,\n" +
                    "                        \"addr\": \"100.1.1.125\",\n" +
                    "                        \"OS-EXT-IPS:type\": \"fixed\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"links\": [\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/v2.1/servers/db732a91-dae2-4660-be65-0b4a2cd7963f\",\n" +
                    "                    \"rel\": \"self\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/servers/db732a91-dae2-4660-be65-0b4a2cd7963f\",\n" +
                    "                    \"rel\": \"bookmark\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"image\": {\n" +
                    "                \"id\": \"20ec9dae-3a41-469d-bc6d-ef9abcbaf574\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/images/20ec9dae-3a41-469d-bc6d-ef9abcbaf574\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"OS-EXT-STS:vm_state\": \"active\",\n" +
                    "            \"OS-EXT-SRV-ATTR:instance_name\": \"instance-0000042a\",\n" +
                    "            \"OS-SRV-USG:launched_at\": \"2023-01-29T02:59:08.000000\",\n" +
                    "            \"flavor\": {\n" +
                    "                \"id\": \"m1.small\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/flavors/m1.small\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"id\": \"db732a91-dae2-4660-be65-0b4a2cd7963f\",\n" +
                    "            \"security_groups\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"user_id\": \"c88ea64767af4106b9e88fae93c2ff88\",\n" +
                    "            \"OS-DCF:diskConfig\": \"MANUAL\",\n" +
                    "            \"accessIPv4\": \"\",\n" +
                    "            \"accessIPv6\": \"\",\n" +
                    "            \"progress\": 0,\n" +
                    "            \"OS-EXT-STS:power_state\": 1,\n" +
                    "            \"OS-EXT-AZ:availability_zone\": \"nova\",\n" +
                    "            \"config_drive\": \"True\",\n" +
                    "            \"status\": \"ACTIVE\",\n" +
                    "            \"updated\": \"2023-01-29T02:59:08Z\",\n" +
                    "            \"hostId\": \"8faf15a78c0d364d4b4783833922175d89d41d68b83e7c4434930b28\",\n" +
                    "            \"OS-EXT-SRV-ATTR:host\": \"host-2\",\n" +
                    "            \"OS-SRV-USG:terminated_at\": null,\n" +
                    "            \"key_name\": null,\n" +
                    "            \"OS-EXT-SRV-ATTR:hypervisor_hostname\": \"host-2\",\n" +
                    "            \"name\": \"dddd-1\",\n" +
                    "            \"created\": \"2023-01-29T02:58:53Z\",\n" +
                    "            \"tenant_id\": \"0415bfa3cc324b65a60b28557f1a9bb1\",\n" +
                    "            \"os-extended-volumes:volumes_attached\": [],\n" +
                    "            \"metadata\": {\n" +
                    "                \"ha\": \"False\",\n" +
                    "                \"flatten\": \"False\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"OS-EXT-STS:task_state\": null,\n" +
                    "            \"addresses\": {},\n" +
                    "            \"links\": [\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/v2.1/servers/037e9401-520c-4ad5-81ba-8f01235c40eb\",\n" +
                    "                    \"rel\": \"self\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/servers/037e9401-520c-4ad5-81ba-8f01235c40eb\",\n" +
                    "                    \"rel\": \"bookmark\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"image\": {\n" +
                    "                \"id\": \"8847719c-f78b-41ab-ad72-43d427534560\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/images/8847719c-f78b-41ab-ad72-43d427534560\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"OS-EXT-STS:vm_state\": \"active\",\n" +
                    "            \"OS-EXT-SRV-ATTR:instance_name\": \"instance-00000426\",\n" +
                    "            \"OS-SRV-USG:launched_at\": \"2022-11-14T09:41:02.000000\",\n" +
                    "            \"flavor\": {\n" +
                    "                \"id\": \"4U8G100G\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/flavors/4U8G100G\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"id\": \"037e9401-520c-4ad5-81ba-8f01235c40eb\",\n" +
                    "            \"security_groups\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"user_id\": \"c88ea64767af4106b9e88fae93c2ff88\",\n" +
                    "            \"OS-DCF:diskConfig\": \"MANUAL\",\n" +
                    "            \"accessIPv4\": \"\",\n" +
                    "            \"accessIPv6\": \"\",\n" +
                    "            \"progress\": 0,\n" +
                    "            \"OS-EXT-STS:power_state\": 1,\n" +
                    "            \"OS-EXT-AZ:availability_zone\": \"nova\",\n" +
                    "            \"config_drive\": \"True\",\n" +
                    "            \"status\": \"ACTIVE\",\n" +
                    "            \"updated\": \"2022-11-14T11:42:06Z\",\n" +
                    "            \"hostId\": \"d8605b766393c66482072ae942de9617f3951e8344c50d7eb3d3723f\",\n" +
                    "            \"OS-EXT-SRV-ATTR:host\": \"host-1\",\n" +
                    "            \"OS-SRV-USG:terminated_at\": null,\n" +
                    "            \"key_name\": null,\n" +
                    "            \"OS-EXT-SRV-ATTR:hypervisor_hostname\": \"host-1\",\n" +
                    "            \"name\": \"aaa\",\n" +
                    "            \"created\": \"2022-11-14T09:40:46Z\",\n" +
                    "            \"tenant_id\": \"0415bfa3cc324b65a60b28557f1a9bb1\",\n" +
                    "            \"os-extended-volumes:volumes_attached\": [],\n" +
                    "            \"metadata\": {\n" +
                    "                \"ha\": \"False\",\n" +
                    "                \"flatten\": \"False\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"OS-EXT-STS:task_state\": null,\n" +
                    "            \"addresses\": {},\n" +
                    "            \"links\": [\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/v2.1/servers/7a741c89-1f49-4332-a4eb-23562989d8dc\",\n" +
                    "                    \"rel\": \"self\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/servers/7a741c89-1f49-4332-a4eb-23562989d8dc\",\n" +
                    "                    \"rel\": \"bookmark\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"image\": {\n" +
                    "                \"id\": \"8113da59-e608-40ab-a7e9-d4191127dc88\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/images/8113da59-e608-40ab-a7e9-d4191127dc88\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"OS-EXT-STS:vm_state\": \"active\",\n" +
                    "            \"OS-EXT-SRV-ATTR:instance_name\": \"instance-00000425\",\n" +
                    "            \"OS-SRV-USG:launched_at\": \"2022-11-14T09:28:29.000000\",\n" +
                    "            \"flavor\": {\n" +
                    "                \"id\": \"eb0eeb0e-6497-4938-aca3-e021310ab3df\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/flavors/eb0eeb0e-6497-4938-aca3-e021310ab3df\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"id\": \"7a741c89-1f49-4332-a4eb-23562989d8dc\",\n" +
                    "            \"security_groups\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"user_id\": \"c88ea64767af4106b9e88fae93c2ff88\",\n" +
                    "            \"OS-DCF:diskConfig\": \"MANUAL\",\n" +
                    "            \"accessIPv4\": \"\",\n" +
                    "            \"accessIPv6\": \"\",\n" +
                    "            \"progress\": 0,\n" +
                    "            \"OS-EXT-STS:power_state\": 1,\n" +
                    "            \"OS-EXT-AZ:availability_zone\": \"nova\",\n" +
                    "            \"config_drive\": \"True\",\n" +
                    "            \"status\": \"ACTIVE\",\n" +
                    "            \"updated\": \"2022-11-14T09:28:29Z\",\n" +
                    "            \"hostId\": \"91f4d3df8cba0106b9df7e3fb3a68b8d868446a9bfb43ec7622f8ce6\",\n" +
                    "            \"OS-EXT-SRV-ATTR:host\": \"host-4\",\n" +
                    "            \"OS-SRV-USG:terminated_at\": null,\n" +
                    "            \"key_name\": null,\n" +
                    "            \"OS-EXT-SRV-ATTR:hypervisor_hostname\": \"host-4\",\n" +
                    "            \"name\": \"hust-test2\",\n" +
                    "            \"created\": \"2022-11-14T09:21:06Z\",\n" +
                    "            \"tenant_id\": \"0415bfa3cc324b65a60b28557f1a9bb1\",\n" +
                    "            \"os-extended-volumes:volumes_attached\": [],\n" +
                    "            \"metadata\": {\n" +
                    "                \"ha\": \"False\",\n" +
                    "                \"flatten\": \"False\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"OS-EXT-STS:task_state\": null,\n" +
                    "            \"addresses\": {},\n" +
                    "            \"links\": [\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/v2.1/servers/e86868fb-6ea0-4927-b30f-f066f4d3c478\",\n" +
                    "                    \"rel\": \"self\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/servers/e86868fb-6ea0-4927-b30f-f066f4d3c478\",\n" +
                    "                    \"rel\": \"bookmark\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"image\": {\n" +
                    "                \"id\": \"8113da59-e608-40ab-a7e9-d4191127dc88\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/images/8113da59-e608-40ab-a7e9-d4191127dc88\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"OS-EXT-STS:vm_state\": \"active\",\n" +
                    "            \"OS-EXT-SRV-ATTR:instance_name\": \"instance-00000421\",\n" +
                    "            \"OS-SRV-USG:launched_at\": \"2022-09-20T07:17:23.000000\",\n" +
                    "            \"flavor\": {\n" +
                    "                \"id\": \"a64b97a3-f2e4-4245-8bef-cb2a38be13dd\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/flavors/a64b97a3-f2e4-4245-8bef-cb2a38be13dd\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"id\": \"e86868fb-6ea0-4927-b30f-f066f4d3c478\",\n" +
                    "            \"security_groups\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"user_id\": \"c88ea64767af4106b9e88fae93c2ff88\",\n" +
                    "            \"OS-DCF:diskConfig\": \"MANUAL\",\n" +
                    "            \"accessIPv4\": \"\",\n" +
                    "            \"accessIPv6\": \"\",\n" +
                    "            \"progress\": 0,\n" +
                    "            \"OS-EXT-STS:power_state\": 1,\n" +
                    "            \"OS-EXT-AZ:availability_zone\": \"nova\",\n" +
                    "            \"config_drive\": \"True\",\n" +
                    "            \"status\": \"ACTIVE\",\n" +
                    "            \"updated\": \"2022-09-20T07:17:23Z\",\n" +
                    "            \"hostId\": \"1a8c80f55de308c9600090b8100304f23ef72fe0e61ed16863f62110\",\n" +
                    "            \"OS-EXT-SRV-ATTR:host\": \"host-5\",\n" +
                    "            \"OS-SRV-USG:terminated_at\": null,\n" +
                    "            \"key_name\": null,\n" +
                    "            \"OS-EXT-SRV-ATTR:hypervisor_hostname\": \"host-5\",\n" +
                    "            \"name\": \"dist-vm2\",\n" +
                    "            \"created\": \"2022-09-20T07:12:00Z\",\n" +
                    "            \"tenant_id\": \"0415bfa3cc324b65a60b28557f1a9bb1\",\n" +
                    "            \"os-extended-volumes:volumes_attached\": [],\n" +
                    "            \"metadata\": {\n" +
                    "                \"ha\": \"False\",\n" +
                    "                \"flatten\": \"False\"\n" +
                    "            }\n" +
                    "        },");
            stringBuffer.append("{\n" +
                    "            \"OS-EXT-STS:task_state\": null,\n" +
                    "            \"addresses\": {\n" +
                    "                \"VmService1\": [\n" +
                    "                    {\n" +
                    "                        \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:02:18:b6\",\n" +
                    "                        \"version\": 4,\n" +
                    "                        \"addr\": \"100.1.1.102\",\n" +
                    "                        \"OS-EXT-IPS:type\": \"fixed\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"links\": [\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/v2.1/servers/b0579a8c-62aa-437c-a482-6824a8c5c533\",\n" +
                    "                    \"rel\": \"self\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/servers/b0579a8c-62aa-437c-a482-6824a8c5c533\",\n" +
                    "                    \"rel\": \"bookmark\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"image\": {\n" +
                    "                \"id\": \"8113da59-e608-40ab-a7e9-d4191127dc88\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/images/8113da59-e608-40ab-a7e9-d4191127dc88\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"OS-EXT-STS:vm_state\": \"active\",\n" +
                    "            \"OS-EXT-SRV-ATTR:instance_name\": \"instance-00000420\",\n" +
                    "            \"OS-SRV-USG:launched_at\": \"2022-09-20T07:04:23.000000\",\n" +
                    "            \"flavor\": {\n" +
                    "                \"id\": \"eb0eeb0e-6497-4938-aca3-e021310ab3df\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/flavors/eb0eeb0e-6497-4938-aca3-e021310ab3df\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"id\": \"b0579a8c-62aa-437c-a482-6824a8c5c533\",\n" +
                    "            \"security_groups\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"user_id\": \"c88ea64767af4106b9e88fae93c2ff88\",\n" +
                    "            \"OS-DCF:diskConfig\": \"MANUAL\",\n" +
                    "            \"accessIPv4\": \"\",\n" +
                    "            \"accessIPv6\": \"\",\n" +
                    "            \"progress\": 0,\n" +
                    "            \"OS-EXT-STS:power_state\": 1,\n" +
                    "            \"OS-EXT-AZ:availability_zone\": \"nova\",\n" +
                    "            \"config_drive\": \"True\",\n" +
                    "            \"status\": \"ACTIVE\",\n" +
                    "            \"updated\": \"2022-09-20T07:04:23Z\",\n" +
                    "            \"hostId\": \"1224879040fecf46e67c71ae19b29785b6e48a7baebc9ce9a65d66a3\",\n" +
                    "            \"OS-EXT-SRV-ATTR:host\": \"host-7\",\n" +
                    "            \"OS-SRV-USG:terminated_at\": null,\n" +
                    "            \"key_name\": null,\n" +
                    "            \"OS-EXT-SRV-ATTR:hypervisor_hostname\": \"host-7\",\n" +
                    "            \"name\": \"dist-vm1\",\n" +
                    "            \"created\": \"2022-09-20T06:58:22Z\",\n" +
                    "            \"tenant_id\": \"0415bfa3cc324b65a60b28557f1a9bb1\",\n" +
                    "            \"os-extended-volumes:volumes_attached\": [],\n" +
                    "            \"metadata\": {\n" +
                    "                \"ha\": \"False\",\n" +
                    "                \"flatten\": \"False\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"OS-EXT-STS:task_state\": null,\n" +
                    "            \"addresses\": {\n" +
                    "                \"VmService1\": [\n" +
                    "                    {\n" +
                    "                        \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:aa:ba:ce\",\n" +
                    "                        \"version\": 4,\n" +
                    "                        \"addr\": \"100.1.1.122\",\n" +
                    "                        \"OS-EXT-IPS:type\": \"fixed\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"links\": [\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/v2.1/servers/30e73426-aad5-4031-a3df-4df82a78a060\",\n" +
                    "                    \"rel\": \"self\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/servers/30e73426-aad5-4031-a3df-4df82a78a060\",\n" +
                    "                    \"rel\": \"bookmark\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"image\": {\n" +
                    "                \"id\": \"20ec9dae-3a41-469d-bc6d-ef9abcbaf574\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/images/20ec9dae-3a41-469d-bc6d-ef9abcbaf574\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"OS-EXT-STS:vm_state\": \"stopped\",\n" +
                    "            \"OS-EXT-SRV-ATTR:instance_name\": \"instance-00000415\",\n" +
                    "            \"OS-SRV-USG:launched_at\": \"2022-06-30T11:25:45.000000\",\n" +
                    "            \"flavor\": {\n" +
                    "                \"id\": \"m1.small\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/flavors/m1.small\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"id\": \"30e73426-aad5-4031-a3df-4df82a78a060\",\n" +
                    "            \"security_groups\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"user_id\": \"c88ea64767af4106b9e88fae93c2ff88\",\n" +
                    "            \"OS-DCF:diskConfig\": \"MANUAL\",\n" +
                    "            \"accessIPv4\": \"\",\n" +
                    "            \"accessIPv6\": \"\",\n" +
                    "            \"OS-EXT-STS:power_state\": 4,\n" +
                    "            \"OS-EXT-AZ:availability_zone\": \"nova\",\n" +
                    "            \"config_drive\": \"True\",\n" +
                    "            \"status\": \"SHUTOFF\",\n" +
                    "            \"updated\": \"2022-07-26T05:38:54Z\",\n" +
                    "            \"hostId\": \"1224879040fecf46e67c71ae19b29785b6e48a7baebc9ce9a65d66a3\",\n" +
                    "            \"OS-EXT-SRV-ATTR:host\": \"host-7\",\n" +
                    "            \"OS-SRV-USG:terminated_at\": null,\n" +
                    "            \"key_name\": null,\n" +
                    "            \"OS-EXT-SRV-ATTR:hypervisor_hostname\": \"host-7\",\n" +
                    "            \"name\": \"aaa\",\n" +
                    "            \"created\": \"2022-06-30T11:25:31Z\",\n" +
                    "            \"tenant_id\": \"0415bfa3cc324b65a60b28557f1a9bb1\",\n" +
                    "            \"os-extended-volumes:volumes_attached\": [],\n" +
                    "            \"metadata\": {\n" +
                    "                \"ha\": \"False\",\n" +
                    "                \"flatten\": \"False\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"OS-EXT-STS:task_state\": null,\n" +
                    "            \"addresses\": {\n" +
                    "                \"10-190-84-0-VmMgnt\": [\n" +
                    "                    {\n" +
                    "                        \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:e7:1e:1b\",\n" +
                    "                        \"version\": 4,\n" +
                    "                        \"addr\": \"10.190.84.211\",\n" +
                    "                        \"OS-EXT-IPS:type\": \"fixed\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"links\": [\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/v2.1/servers/269d61cb-6a0f-47e3-ae1b-7ec9a8b88006\",\n" +
                    "                    \"rel\": \"self\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/servers/269d61cb-6a0f-47e3-ae1b-7ec9a8b88006\",\n" +
                    "                    \"rel\": \"bookmark\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"image\": {\n" +
                    "                \"id\": \"e1e5f4c8-eaf4-4ec7-9653-d6fb5863945d\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/images/e1e5f4c8-eaf4-4ec7-9653-d6fb5863945d\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"OS-EXT-STS:vm_state\": \"stopped\",\n" +
                    "            \"OS-EXT-SRV-ATTR:instance_name\": \"instance-00000414\",\n" +
                    "            \"OS-SRV-USG:launched_at\": \"2022-06-30T06:14:58.000000\",\n" +
                    "            \"flavor\": {\n" +
                    "                \"id\": \"m1.medium\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/flavors/m1.medium\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"id\": \"269d61cb-6a0f-47e3-ae1b-7ec9a8b88006\",\n" +
                    "            \"security_groups\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"user_id\": \"c88ea64767af4106b9e88fae93c2ff88\",\n" +
                    "            \"OS-DCF:diskConfig\": \"MANUAL\",\n" +
                    "            \"accessIPv4\": \"\",\n" +
                    "            \"accessIPv6\": \"\",\n" +
                    "            \"OS-EXT-STS:power_state\": 4,\n" +
                    "            \"OS-EXT-AZ:availability_zone\": \"nova\",\n" +
                    "            \"config_drive\": \"True\",\n" +
                    "            \"status\": \"SHUTOFF\",\n" +
                    "            \"updated\": \"2022-07-26T05:39:09Z\",\n" +
                    "            \"hostId\": \"8faf15a78c0d364d4b4783833922175d89d41d68b83e7c4434930b28\",\n" +
                    "            \"OS-EXT-SRV-ATTR:host\": \"host-2\",\n" +
                    "            \"OS-SRV-USG:terminated_at\": null,\n" +
                    "            \"key_name\": null,\n" +
                    "            \"OS-EXT-SRV-ATTR:hypervisor_hostname\": \"host-2\",\n" +
                    "            \"name\": \"tftest-3\",\n" +
                    "            \"created\": \"2022-06-30T06:13:03Z\",\n" +
                    "            \"tenant_id\": \"0415bfa3cc324b65a60b28557f1a9bb1\",\n" +
                    "            \"os-extended-volumes:volumes_attached\": [],\n" +
                    "            \"metadata\": {\n" +
                    "                \"ha\": \"False\",\n" +
                    "                \"flatten\": \"False\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"OS-EXT-STS:task_state\": null,\n" +
                    "            \"addresses\": {\n" +
                    "                \"10-190-84-0-VmMgnt\": [\n" +
                    "                    {\n" +
                    "                        \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:bc:19:99\",\n" +
                    "                        \"version\": 4,\n" +
                    "                        \"addr\": \"10.190.84.100\",\n" +
                    "                        \"OS-EXT-IPS:type\": \"fixed\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"links\": [\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/v2.1/servers/cef16ffe-854e-4177-bfb9-58e30388b54f\",\n" +
                    "                    \"rel\": \"self\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/servers/cef16ffe-854e-4177-bfb9-58e30388b54f\",\n" +
                    "                    \"rel\": \"bookmark\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"image\": {\n" +
                    "                \"id\": \"e1e5f4c8-eaf4-4ec7-9653-d6fb5863945d\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/images/e1e5f4c8-eaf4-4ec7-9653-d6fb5863945d\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"OS-EXT-STS:vm_state\": \"stopped\",\n" +
                    "            \"OS-EXT-SRV-ATTR:instance_name\": \"instance-00000413\",\n" +
                    "            \"OS-SRV-USG:launched_at\": \"2022-06-30T06:14:39.000000\",\n" +
                    "            \"flavor\": {\n" +
                    "                \"id\": \"m1.medium\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/flavors/m1.medium\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"id\": \"cef16ffe-854e-4177-bfb9-58e30388b54f\",\n" +
                    "            \"security_groups\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"user_id\": \"c88ea64767af4106b9e88fae93c2ff88\",\n" +
                    "            \"OS-DCF:diskConfig\": \"MANUAL\",\n" +
                    "            \"accessIPv4\": \"\",\n" +
                    "            \"accessIPv6\": \"\",\n" +
                    "            \"OS-EXT-STS:power_state\": 4,\n" +
                    "            \"OS-EXT-AZ:availability_zone\": \"nova\",\n" +
                    "            \"config_drive\": \"True\",\n" +
                    "            \"status\": \"SHUTOFF\",\n" +
                    "            \"updated\": \"2022-07-26T05:39:18Z\",\n" +
                    "            \"hostId\": \"91f4d3df8cba0106b9df7e3fb3a68b8d868446a9bfb43ec7622f8ce6\",\n" +
                    "            \"OS-EXT-SRV-ATTR:host\": \"host-4\",\n" +
                    "            \"OS-SRV-USG:terminated_at\": null,\n" +
                    "            \"key_name\": null,\n" +
                    "            \"OS-EXT-SRV-ATTR:hypervisor_hostname\": \"host-4\",\n" +
                    "            \"name\": \"tftest-2\",\n" +
                    "            \"created\": \"2022-06-30T06:13:03Z\",\n" +
                    "            \"tenant_id\": \"0415bfa3cc324b65a60b28557f1a9bb1\",\n" +
                    "            \"os-extended-volumes:volumes_attached\": [],\n" +
                    "            \"metadata\": {\n" +
                    "                \"ha\": \"False\",\n" +
                    "                \"flatten\": \"False\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"OS-EXT-STS:task_state\": null,\n" +
                    "            \"addresses\": {\n" +
                    "                \"10-190-84-0-VmMgnt\": [\n" +
                    "                    {\n" +
                    "                        \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:ac:0c:dd\",\n" +
                    "                        \"version\": 4,\n" +
                    "                        \"addr\": \"10.190.84.240\",\n" +
                    "                        \"OS-EXT-IPS:type\": \"fixed\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"links\": [\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/v2.1/servers/2243bc9f-7799-4c1a-953b-3539bcffbbb6\",\n" +
                    "                    \"rel\": \"self\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/servers/2243bc9f-7799-4c1a-953b-3539bcffbbb6\",\n" +
                    "                    \"rel\": \"bookmark\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"image\": {\n" +
                    "                \"id\": \"e1e5f4c8-eaf4-4ec7-9653-d6fb5863945d\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/images/e1e5f4c8-eaf4-4ec7-9653-d6fb5863945d\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"OS-EXT-STS:vm_state\": \"stopped\",\n" +
                    "            \"OS-EXT-SRV-ATTR:instance_name\": \"instance-00000412\",\n" +
                    "            \"OS-SRV-USG:launched_at\": \"2022-06-30T06:14:49.000000\",\n" +
                    "            \"flavor\": {\n" +
                    "                \"id\": \"m1.medium\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/flavors/m1.medium\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"id\": \"2243bc9f-7799-4c1a-953b-3539bcffbbb6\",\n" +
                    "            \"security_groups\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"user_id\": \"c88ea64767af4106b9e88fae93c2ff88\",\n" +
                    "            \"OS-DCF:diskConfig\": \"MANUAL\",\n" +
                    "            \"accessIPv4\": \"\",\n" +
                    "            \"accessIPv6\": \"\",\n" +
                    "            \"OS-EXT-STS:power_state\": 4,\n" +
                    "            \"OS-EXT-AZ:availability_zone\": \"nova\",\n" +
                    "            \"config_drive\": \"True\",\n" +
                    "            \"status\": \"SHUTOFF\",\n" +
                    "            \"updated\": \"2022-07-26T05:39:51Z\",\n" +
                    "            \"hostId\": \"d8605b766393c66482072ae942de9617f3951e8344c50d7eb3d3723f\",\n" +
                    "            \"OS-EXT-SRV-ATTR:host\": \"host-1\",\n" +
                    "            \"OS-SRV-USG:terminated_at\": null,\n" +
                    "            \"key_name\": null,\n" +
                    "            \"OS-EXT-SRV-ATTR:hypervisor_hostname\": \"host-1\",\n" +
                    "            \"name\": \"tftest-1\",\n" +
                    "            \"created\": \"2022-06-30T06:13:03Z\",\n" +
                    "            \"tenant_id\": \"0415bfa3cc324b65a60b28557f1a9bb1\",\n" +
                    "            \"os-extended-volumes:volumes_attached\": [],\n" +
                    "            \"metadata\": {\n" +
                    "                \"ha\": \"False\",\n" +
                    "                \"flatten\": \"False\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"OS-EXT-STS:task_state\": null,\n" +
                    "            \"addresses\": {\n" +
                    "                \"10-190-84-0-VmMgnt\": [\n" +
                    "                    {\n" +
                    "                        \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:6a:54:c4\",\n" +
                    "                        \"version\": 4,\n" +
                    "                        \"addr\": \"10.190.84.79\",\n" +
                    "                        \"OS-EXT-IPS:type\": \"fixed\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"links\": [\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/v2.1/servers/bcb96b6e-97bd-4e1d-b5d6-6045f60ea88b\",\n" +
                    "                    \"rel\": \"self\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/servers/bcb96b6e-97bd-4e1d-b5d6-6045f60ea88b\",\n" +
                    "                    \"rel\": \"bookmark\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"image\": {\n" +
                    "                \"id\": \"e1e5f4c8-eaf4-4ec7-9653-d6fb5863945d\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/images/e1e5f4c8-eaf4-4ec7-9653-d6fb5863945d\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"OS-EXT-STS:vm_state\": \"stopped\",\n" +
                    "            \"OS-EXT-SRV-ATTR:instance_name\": \"instance-000003a7\",\n" +
                    "            \"OS-SRV-USG:launched_at\": \"2022-05-26T12:47:12.000000\",\n" +
                    "            \"flavor\": {\n" +
                    "                \"id\": \"725d34c1-7172-41a0-a3f7-bf0f8a9f1aae\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/flavors/725d34c1-7172-41a0-a3f7-bf0f8a9f1aae\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"id\": \"bcb96b6e-97bd-4e1d-b5d6-6045f60ea88b\",\n" +
                    "            \"security_groups\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"user_id\": \"c88ea64767af4106b9e88fae93c2ff88\",\n" +
                    "            \"OS-DCF:diskConfig\": \"MANUAL\",\n" +
                    "            \"accessIPv4\": \"\",\n" +
                    "            \"accessIPv6\": \"\",\n" +
                    "            \"OS-EXT-STS:power_state\": 4,\n" +
                    "            \"OS-EXT-AZ:availability_zone\": \"nova\",\n" +
                    "            \"config_drive\": \"True\",\n" +
                    "            \"status\": \"SHUTOFF\",\n" +
                    "            \"updated\": \"2022-12-12T01:14:01Z\",\n" +
                    "            \"hostId\": \"8faf15a78c0d364d4b4783833922175d89d41d68b83e7c4434930b28\",\n" +
                    "            \"OS-EXT-SRV-ATTR:host\": \"host-2\",\n" +
                    "            \"OS-SRV-USG:terminated_at\": null,\n" +
                    "            \"key_name\": null,\n" +
                    "            \"OS-EXT-SRV-ATTR:hypervisor_hostname\": \"host-2\",\n" +
                    "            \"name\": \"test\",\n" +
                    "            \"created\": \"2022-05-26T12:45:16Z\",\n" +
                    "            \"tenant_id\": \"0415bfa3cc324b65a60b28557f1a9bb1\",\n" +
                    "            \"os-extended-volumes:volumes_attached\": [],\n" +
                    "            \"metadata\": {\n" +
                    "                \"ha\": \"False\",\n" +
                    "                \"flatten\": \"False\"\n" +
                    "            }\n" +
                    "        },");
            stringBuffer.append("{\n" +
                    "            \"OS-EXT-STS:task_state\": null,\n" +
                    "            \"addresses\": {\n" +
                    "                \"10-190-84-0-VmMgnt\": [\n" +
                    "                    {\n" +
                    "                        \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:98:7a:39\",\n" +
                    "                        \"version\": 4,\n" +
                    "                        \"addr\": \"10.190.84.75\",\n" +
                    "                        \"OS-EXT-IPS:type\": \"fixed\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"links\": [\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/v2.1/servers/80562152-3f8e-4e69-9cb8-5eca43e474ec\",\n" +
                    "                    \"rel\": \"self\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/servers/80562152-3f8e-4e69-9cb8-5eca43e474ec\",\n" +
                    "                    \"rel\": \"bookmark\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"image\": \"\",\n" +
                    "            \"OS-EXT-STS:vm_state\": \"stopped\",\n" +
                    "            \"OS-EXT-SRV-ATTR:instance_name\": \"instance-000003a6\",\n" +
                    "            \"OS-SRV-USG:launched_at\": \"2022-05-26T09:13:27.000000\",\n" +
                    "            \"flavor\": {\n" +
                    "                \"id\": \"725d34c1-7172-41a0-a3f7-bf0f8a9f1aae\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/flavors/725d34c1-7172-41a0-a3f7-bf0f8a9f1aae\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"id\": \"80562152-3f8e-4e69-9cb8-5eca43e474ec\",\n" +
                    "            \"security_groups\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"user_id\": \"c88ea64767af4106b9e88fae93c2ff88\",\n" +
                    "            \"OS-DCF:diskConfig\": \"MANUAL\",\n" +
                    "            \"accessIPv4\": \"\",\n" +
                    "            \"accessIPv6\": \"\",\n" +
                    "            \"OS-EXT-STS:power_state\": 4,\n" +
                    "            \"OS-EXT-AZ:availability_zone\": \"nova\",\n" +
                    "            \"config_drive\": \"True\",\n" +
                    "            \"status\": \"SHUTOFF\",\n" +
                    "            \"updated\": \"2022-06-03T06:47:22Z\",\n" +
                    "            \"hostId\": \"1a8c80f55de308c9600090b8100304f23ef72fe0e61ed16863f62110\",\n" +
                    "            \"OS-EXT-SRV-ATTR:host\": \"host-5\",\n" +
                    "            \"OS-SRV-USG:terminated_at\": null,\n" +
                    "            \"key_name\": null,\n" +
                    "            \"OS-EXT-SRV-ATTR:hypervisor_hostname\": \"host-5\",\n" +
                    "            \"name\": \"ubuntu_test\",\n" +
                    "            \"created\": \"2022-05-26T09:13:10Z\",\n" +
                    "            \"tenant_id\": \"0415bfa3cc324b65a60b28557f1a9bb1\",\n" +
                    "            \"os-extended-volumes:volumes_attached\": [\n" +
                    "                {\n" +
                    "                    \"id\": \"bbd9d90a-9eb7-48a9-896d-66fd543b1419\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"metadata\": {\n" +
                    "                \"ha\": \"False\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"OS-EXT-STS:task_state\": null,\n" +
                    "            \"addresses\": {\n" +
                    "                \"10-190-84-0-VmMgnt\": [\n" +
                    "                    {\n" +
                    "                        \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:31:b5:88\",\n" +
                    "                        \"version\": 4,\n" +
                    "                        \"addr\": \"10.190.84.56\",\n" +
                    "                        \"OS-EXT-IPS:type\": \"fixed\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"links\": [\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/v2.1/servers/77bf6822-3254-4fd6-9dd7-e8865ba4417c\",\n" +
                    "                    \"rel\": \"self\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/servers/77bf6822-3254-4fd6-9dd7-e8865ba4417c\",\n" +
                    "                    \"rel\": \"bookmark\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"image\": \"\",\n" +
                    "            \"OS-EXT-STS:vm_state\": \"stopped\",\n" +
                    "            \"OS-EXT-SRV-ATTR:instance_name\": \"instance-0000039c\",\n" +
                    "            \"OS-SRV-USG:launched_at\": \"2022-04-29T03:42:31.000000\",\n" +
                    "            \"flavor\": {\n" +
                    "                \"id\": \"48f47482-8bc8-4b40-9c26-8876363f0a6f\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/flavors/48f47482-8bc8-4b40-9c26-8876363f0a6f\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"id\": \"77bf6822-3254-4fd6-9dd7-e8865ba4417c\",\n" +
                    "            \"security_groups\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"user_id\": \"c88ea64767af4106b9e88fae93c2ff88\",\n" +
                    "            \"OS-DCF:diskConfig\": \"MANUAL\",\n" +
                    "            \"accessIPv4\": \"\",\n" +
                    "            \"accessIPv6\": \"\",\n" +
                    "            \"OS-EXT-STS:power_state\": 4,\n" +
                    "            \"OS-EXT-AZ:availability_zone\": \"nova\",\n" +
                    "            \"config_drive\": \"True\",\n" +
                    "            \"status\": \"SHUTOFF\",\n" +
                    "            \"updated\": \"2022-11-24T17:51:53Z\",\n" +
                    "            \"hostId\": \"d8605b766393c66482072ae942de9617f3951e8344c50d7eb3d3723f\",\n" +
                    "            \"OS-EXT-SRV-ATTR:host\": \"host-1\",\n" +
                    "            \"OS-SRV-USG:terminated_at\": null,\n" +
                    "            \"key_name\": null,\n" +
                    "            \"OS-EXT-SRV-ATTR:hypervisor_hostname\": \"host-1\",\n" +
                    "            \"name\": \"GUOXIANGH\",\n" +
                    "            \"created\": \"2022-04-29T03:42:20Z\",\n" +
                    "            \"tenant_id\": \"0415bfa3cc324b65a60b28557f1a9bb1\",\n" +
                    "            \"os-extended-volumes:volumes_attached\": [\n" +
                    "                {\n" +
                    "                    \"id\": \"3b2dd136-8e85-4899-9075-1a1c95f93fe5\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"metadata\": {\n" +
                    "                \"ha\": \"False\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"OS-EXT-STS:task_state\": null,\n" +
                    "            \"addresses\": {\n" +
                    "                \"10-190-84-0-VmMgnt\": [\n" +
                    "                    {\n" +
                    "                        \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:b7:46:d6\",\n" +
                    "                        \"version\": 4,\n" +
                    "                        \"addr\": \"10.190.84.55\",\n" +
                    "                        \"OS-EXT-IPS:type\": \"fixed\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"links\": [\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/v2.1/servers/1b8b2d68-3009-4354-9ad6-8c4ae4823d14\",\n" +
                    "                    \"rel\": \"self\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/servers/1b8b2d68-3009-4354-9ad6-8c4ae4823d14\",\n" +
                    "                    \"rel\": \"bookmark\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"image\": {\n" +
                    "                \"id\": \"8847719c-f78b-41ab-ad72-43d427534560\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/images/8847719c-f78b-41ab-ad72-43d427534560\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"OS-EXT-STS:vm_state\": \"stopped\",\n" +
                    "            \"OS-EXT-SRV-ATTR:instance_name\": \"instance-0000036c\",\n" +
                    "            \"OS-SRV-USG:launched_at\": \"2022-04-19T02:27:25.000000\",\n" +
                    "            \"flavor\": {\n" +
                    "                \"id\": \"4U8G100G\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/flavors/4U8G100G\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"id\": \"1b8b2d68-3009-4354-9ad6-8c4ae4823d14\",\n" +
                    "            \"security_groups\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"user_id\": \"c88ea64767af4106b9e88fae93c2ff88\",\n" +
                    "            \"OS-DCF:diskConfig\": \"MANUAL\",\n" +
                    "            \"accessIPv4\": \"\",\n" +
                    "            \"accessIPv6\": \"\",\n" +
                    "            \"OS-EXT-STS:power_state\": 4,\n" +
                    "            \"OS-EXT-AZ:availability_zone\": \"nova\",\n" +
                    "            \"config_drive\": \"True\",\n" +
                    "            \"status\": \"SHUTOFF\",\n" +
                    "            \"updated\": \"2022-06-03T00:45:30Z\",\n" +
                    "            \"hostId\": \"91f4d3df8cba0106b9df7e3fb3a68b8d868446a9bfb43ec7622f8ce6\",\n" +
                    "            \"OS-EXT-SRV-ATTR:host\": \"host-4\",\n" +
                    "            \"OS-SRV-USG:terminated_at\": null,\n" +
                    "            \"key_name\": null,\n" +
                    "            \"OS-EXT-SRV-ATTR:hypervisor_hostname\": \"host-4\",\n" +
                    "            \"name\": \"centos1\",\n" +
                    "            \"created\": \"2022-04-19T02:27:07Z\",\n" +
                    "            \"tenant_id\": \"0415bfa3cc324b65a60b28557f1a9bb1\",\n" +
                    "            \"os-extended-volumes:volumes_attached\": [],\n" +
                    "            \"metadata\": {\n" +
                    "                \"ha\": \"False\",\n" +
                    "                \"flatten\": \"False\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"OS-EXT-STS:task_state\": null,\n" +
                    "            \"addresses\": {\n" +
                    "                \"10-190-84-0-VmMgnt\": [\n" +
                    "                    {\n" +
                    "                        \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:68:11:14\",\n" +
                    "                        \"version\": 4,\n" +
                    "                        \"addr\": \"10.190.84.74\",\n" +
                    "                        \"OS-EXT-IPS:type\": \"fixed\"\n" +
                    "                    }\n" +
                    "                ],\n" +
                    "                \"192-168-8-0-VmToInternet\": [\n" +
                    "                    {\n" +
                    "                        \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:8d:c4:49\",\n" +
                    "                        \"version\": 4,\n" +
                    "                        \"addr\": \"192.168.8.105\",\n" +
                    "                        \"OS-EXT-IPS:type\": \"fixed\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"links\": [\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/v2.1/servers/3adac46a-c205-42eb-bad6-6f037efa912f\",\n" +
                    "                    \"rel\": \"self\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/servers/3adac46a-c205-42eb-bad6-6f037efa912f\",\n" +
                    "                    \"rel\": \"bookmark\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"image\": {\n" +
                    "                \"id\": \"78c10293-1168-430a-aa6f-b20bfd1346a1\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/images/78c10293-1168-430a-aa6f-b20bfd1346a1\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"OS-EXT-STS:vm_state\": \"stopped\",\n" +
                    "            \"OS-EXT-SRV-ATTR:instance_name\": \"instance-00000366\",\n" +
                    "            \"OS-SRV-USG:launched_at\": \"2022-04-18T06:03:33.000000\",\n" +
                    "            \"flavor\": {\n" +
                    "                \"id\": \"m1.medium\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/flavors/m1.medium\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"id\": \"3adac46a-c205-42eb-bad6-6f037efa912f\",\n" +
                    "            \"security_groups\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"user_id\": \"c88ea64767af4106b9e88fae93c2ff88\",\n" +
                    "            \"OS-DCF:diskConfig\": \"MANUAL\",\n" +
                    "            \"accessIPv4\": \"\",\n" +
                    "            \"accessIPv6\": \"\",\n" +
                    "            \"OS-EXT-STS:power_state\": 4,\n" +
                    "            \"OS-EXT-AZ:availability_zone\": \"nova\",\n" +
                    "            \"config_drive\": \"True\",\n" +
                    "            \"status\": \"SHUTOFF\",\n" +
                    "            \"updated\": \"2022-06-03T00:44:46Z\",\n" +
                    "            \"hostId\": \"1a8c80f55de308c9600090b8100304f23ef72fe0e61ed16863f62110\",\n" +
                    "            \"OS-EXT-SRV-ATTR:host\": \"host-5\",\n" +
                    "            \"OS-SRV-USG:terminated_at\": null,\n" +
                    "            \"key_name\": null,\n" +
                    "            \"OS-EXT-SRV-ATTR:hypervisor_hostname\": \"host-5\",\n" +
                    "            \"name\": \"P2\",\n" +
                    "            \"created\": \"2022-04-18T06:03:18Z\",\n" +
                    "            \"tenant_id\": \"0415bfa3cc324b65a60b28557f1a9bb1\",\n" +
                    "            \"os-extended-volumes:volumes_attached\": [],\n" +
                    "            \"metadata\": {\n" +
                    "                \"ha\": \"False\",\n" +
                    "                \"flatten\": \"False\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"OS-EXT-STS:task_state\": null,\n" +
                    "            \"addresses\": {\n" +
                    "                \"10-190-84-0-VmMgnt\": [\n" +
                    "                    {\n" +
                    "                        \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:bf:78:12\",\n" +
                    "                        \"version\": 4,\n" +
                    "                        \"addr\": \"10.190.84.84\",\n" +
                    "                        \"OS-EXT-IPS:type\": \"fixed\"\n" +
                    "                    }\n" +
                    "                ],\n" +
                    "                \"192-168-8-0-VmToInternet\": [\n" +
                    "                    {\n" +
                    "                        \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:bb:79:9a\",\n" +
                    "                        \"version\": 4,\n" +
                    "                        \"addr\": \"192.168.8.118\",\n" +
                    "                        \"OS-EXT-IPS:type\": \"fixed\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"links\": [\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/v2.1/servers/6ae25742-e6ea-45a8-9f8d-b81043f23911\",\n" +
                    "                    \"rel\": \"self\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/servers/6ae25742-e6ea-45a8-9f8d-b81043f23911\",\n" +
                    "                    \"rel\": \"bookmark\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"image\": {\n" +
                    "                \"id\": \"78c10293-1168-430a-aa6f-b20bfd1346a1\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/images/78c10293-1168-430a-aa6f-b20bfd1346a1\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"OS-EXT-STS:vm_state\": \"stopped\",\n" +
                    "            \"OS-EXT-SRV-ATTR:instance_name\": \"instance-00000363\",\n" +
                    "            \"OS-SRV-USG:launched_at\": \"2022-04-18T06:02:43.000000\",\n" +
                    "            \"flavor\": {\n" +
                    "                \"id\": \"m1.medium\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/flavors/m1.medium\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"id\": \"6ae25742-e6ea-45a8-9f8d-b81043f23911\",\n" +
                    "            \"security_groups\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"user_id\": \"c88ea64767af4106b9e88fae93c2ff88\",\n" +
                    "            \"OS-DCF:diskConfig\": \"MANUAL\",\n" +
                    "            \"accessIPv4\": \"\",\n" +
                    "            \"accessIPv6\": \"\",\n" +
                    "            \"OS-EXT-STS:power_state\": 4,\n" +
                    "            \"OS-EXT-AZ:availability_zone\": \"nova\",\n" +
                    "            \"config_drive\": \"True\",\n" +
                    "            \"status\": \"SHUTOFF\",\n" +
                    "            \"updated\": \"2022-06-03T00:44:47Z\",\n" +
                    "            \"hostId\": \"8faf15a78c0d364d4b4783833922175d89d41d68b83e7c4434930b28\",\n" +
                    "            \"OS-EXT-SRV-ATTR:host\": \"host-2\",\n" +
                    "            \"OS-SRV-USG:terminated_at\": null,\n" +
                    "            \"key_name\": null,\n" +
                    "            \"OS-EXT-SRV-ATTR:hypervisor_hostname\": \"host-2\",\n" +
                    "            \"name\": \"P1\",\n" +
                    "            \"created\": \"2022-04-18T06:02:26Z\",\n" +
                    "            \"tenant_id\": \"0415bfa3cc324b65a60b28557f1a9bb1\",\n" +
                    "            \"os-extended-volumes:volumes_attached\": [],\n" +
                    "            \"metadata\": {\n" +
                    "                \"ha\": \"False\",\n" +
                    "                \"flatten\": \"False\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"OS-EXT-STS:task_state\": null,\n" +
                    "            \"addresses\": {\n" +
                    "                \"10-190-84-0-VmMgnt\": [\n" +
                    "                    {\n" +
                    "                        \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:d5:3c:13\",\n" +
                    "                        \"version\": 4,\n" +
                    "                        \"addr\": \"10.190.84.58\",\n" +
                    "                        \"OS-EXT-IPS:type\": \"fixed\"\n" +
                    "                    }\n" +
                    "                ],\n" +
                    "                \"192-168-8-0-VmToInternet\": [\n" +
                    "                    {\n" +
                    "                        \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:7e:c1:91\",\n" +
                    "                        \"version\": 4,\n" +
                    "                        \"addr\": \"192.168.8.101\",\n" +
                    "                        \"OS-EXT-IPS:type\": \"fixed\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"links\": [\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/v2.1/servers/ca9ac11e-be60-4855-8ca5-5c42ce37c605\",\n" +
                    "                    \"rel\": \"self\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/servers/ca9ac11e-be60-4855-8ca5-5c42ce37c605\",\n" +
                    "                    \"rel\": \"bookmark\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"image\": {\n" +
                    "                \"id\": \"bcabe449-8901-41ef-8331-06cb5cb6d492\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/images/bcabe449-8901-41ef-8331-06cb5cb6d492\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"OS-EXT-STS:vm_state\": \"stopped\",\n" +
                    "            \"OS-EXT-SRV-ATTR:instance_name\": \"instance-00000351\",\n" +
                    "            \"OS-SRV-USG:launched_at\": \"2022-04-15T01:05:31.000000\",\n" +
                    "            \"flavor\": {\n" +
                    "                \"id\": \"9b765211-d438-46e1-9334-04f517e98465\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/flavors/9b765211-d438-46e1-9334-04f517e98465\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"id\": \"ca9ac11e-be60-4855-8ca5-5c42ce37c605\",\n" +
                    "            \"security_groups\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"user_id\": \"c88ea64767af4106b9e88fae93c2ff88\",\n" +
                    "            \"OS-DCF:diskConfig\": \"MANUAL\",\n" +
                    "            \"accessIPv4\": \"\",\n" +
                    "            \"accessIPv6\": \"\",\n" +
                    "            \"OS-EXT-STS:power_state\": 4,\n" +
                    "            \"OS-EXT-AZ:availability_zone\": \"nova\",\n" +
                    "            \"config_drive\": \"True\",\n" +
                    "            \"status\": \"SHUTOFF\",\n" +
                    "            \"updated\": \"2022-06-03T00:44:33Z\",\n" +
                    "            \"hostId\": \"cd271688b71f9674f85e30498ad232c6aac8968b56a2be3c4053afca\",\n" +
                    "            \"OS-EXT-SRV-ATTR:host\": \"host-3\",\n" +
                    "            \"OS-SRV-USG:terminated_at\": null,\n" +
                    "            \"key_name\": null,\n" +
                    "            \"OS-EXT-SRV-ATTR:hypervisor_hostname\": \"host-3\",\n" +
                    "            \"name\": \"zjz_test\",\n" +
                    "            \"created\": \"2022-04-15T00:59:01Z\",\n" +
                    "            \"tenant_id\": \"0415bfa3cc324b65a60b28557f1a9bb1\",\n" +
                    "            \"os-extended-volumes:volumes_attached\": [],\n" +
                    "            \"metadata\": {\n" +
                    "                \"ha\": \"False\",\n" +
                    "                \"flatten\": \"False\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"OS-EXT-STS:task_state\": null,\n" +
                    "            \"addresses\": {\n" +
                    "                \"10-190-84-0-VmMgnt\": [\n" +
                    "                    {\n" +
                    "                        \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:c0:81:bc\",\n" +
                    "                        \"version\": 4,\n" +
                    "                        \"addr\": \"10.190.84.54\",\n" +
                    "                        \"OS-EXT-IPS:type\": \"fixed\"\n" +
                    "                    }\n" +
                    "                ],\n" +
                    "                \"192-168-8-0-VmToInternet\": [\n" +
                    "                    {\n" +
                    "                        \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:a7:8f:e3\",\n" +
                    "                        \"version\": 4,\n" +
                    "                        \"addr\": \"192.168.8.116\",\n" +
                    "                        \"OS-EXT-IPS:type\": \"fixed\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"links\": [\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/v2.1/servers/2eed6456-61dc-4b6f-9e8a-cbadbbb785bd\",\n" +
                    "                    \"rel\": \"self\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/servers/2eed6456-61dc-4b6f-9e8a-cbadbbb785bd\",\n" +
                    "                    \"rel\": \"bookmark\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"image\": {\n" +
                    "                \"id\": \"3da0d8de-35d3-481e-9573-0cc990620e20\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/images/3da0d8de-35d3-481e-9573-0cc990620e20\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"OS-EXT-STS:vm_state\": \"stopped\",\n" +
                    "            \"OS-EXT-SRV-ATTR:instance_name\": \"instance-0000034e\",\n" +
                    "            \"OS-SRV-USG:launched_at\": \"2022-04-15T01:12:07.000000\",\n" +
                    "            \"flavor\": {\n" +
                    "                \"id\": \"9b765211-d438-46e1-9334-04f517e98465\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/flavors/9b765211-d438-46e1-9334-04f517e98465\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"id\": \"2eed6456-61dc-4b6f-9e8a-cbadbbb785bd\",\n" +
                    "            \"security_groups\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"user_id\": \"c88ea64767af4106b9e88fae93c2ff88\",\n" +
                    "            \"OS-DCF:diskConfig\": \"MANUAL\",\n" +
                    "            \"accessIPv4\": \"\",\n" +
                    "            \"accessIPv6\": \"\",\n" +
                    "            \"OS-EXT-STS:power_state\": 4,\n" +
                    "            \"OS-EXT-AZ:availability_zone\": \"nova\",\n" +
                    "            \"config_drive\": \"True\",\n" +
                    "            \"status\": \"SHUTOFF\",\n" +
                    "            \"updated\": \"2022-06-03T00:44:38Z\",\n" +
                    "            \"hostId\": \"81e87e1fe9e78c3a26f626d9705464a890e380a92c20702adc76a91a\",\n" +
                    "            \"OS-EXT-SRV-ATTR:host\": \"host-6\",\n" +
                    "            \"OS-SRV-USG:terminated_at\": null,\n" +
                    "            \"key_name\": null,\n" +
                    "            \"OS-EXT-SRV-ATTR:hypervisor_hostname\": \"host-6\",\n" +
                    "            \"name\": \"zjz\",\n" +
                    "            \"created\": \"2022-04-15T00:52:04Z\",\n" +
                    "            \"tenant_id\": \"0415bfa3cc324b65a60b28557f1a9bb1\",\n" +
                    "            \"os-extended-volumes:volumes_attached\": [],\n" +
                    "            \"metadata\": {\n" +
                    "                \"ha\": \"False\",\n" +
                    "                \"flatten\": \"False\"\n" +
                    "            }\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"OS-EXT-STS:task_state\": null,\n" +
                    "            \"addresses\": {\n" +
                    "                \"192-168-8-0-VmToInternet\": [\n" +
                    "                    {\n" +
                    "                        \"OS-EXT-IPS-MAC:mac_addr\": \"fa:16:3e:57:86:80\",\n" +
                    "                        \"version\": 4,\n" +
                    "                        \"addr\": \"192.168.8.140\",\n" +
                    "                        \"OS-EXT-IPS:type\": \"fixed\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"links\": [\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/v2.1/servers/63f412bb-ed90-429a-8275-0efc30a52481\",\n" +
                    "                    \"rel\": \"self\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"href\": \"https://10.190.85.44:8774/servers/63f412bb-ed90-429a-8275-0efc30a52481\",\n" +
                    "                    \"rel\": \"bookmark\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"image\": {\n" +
                    "                \"id\": \"78c10293-1168-430a-aa6f-b20bfd1346a1\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/images/78c10293-1168-430a-aa6f-b20bfd1346a1\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"OS-EXT-STS:vm_state\": \"stopped\",\n" +
                    "            \"OS-EXT-SRV-ATTR:instance_name\": \"instance-00000342\",\n" +
                    "            \"OS-SRV-USG:launched_at\": \"2022-04-07T02:31:30.000000\",\n" +
                    "            \"flavor\": {\n" +
                    "                \"id\": \"4U8G100G\",\n" +
                    "                \"links\": [\n" +
                    "                    {\n" +
                    "                        \"href\": \"https://10.190.85.44:8774/flavors/4U8G100G\",\n" +
                    "                        \"rel\": \"bookmark\"\n" +
                    "                    }\n" +
                    "                ]\n" +
                    "            },\n" +
                    "            \"id\": \"63f412bb-ed90-429a-8275-0efc30a52481\",\n" +
                    "            \"security_groups\": [\n" +
                    "                {\n" +
                    "                    \"name\": \"default\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"user_id\": \"c88ea64767af4106b9e88fae93c2ff88\",\n" +
                    "            \"OS-DCF:diskConfig\": \"MANUAL\",\n" +
                    "            \"accessIPv4\": \"\",\n" +
                    "            \"accessIPv6\": \"\",\n" +
                    "            \"OS-EXT-STS:power_state\": 4,\n" +
                    "            \"OS-EXT-AZ:availability_zone\": \"nova\",\n" +
                    "            \"config_drive\": \"True\",\n" +
                    "            \"status\": \"SHUTOFF\",\n" +
                    "            \"updated\": \"2022-06-03T00:44:56Z\",\n" +
                    "            \"hostId\": \"1224879040fecf46e67c71ae19b29785b6e48a7baebc9ce9a65d66a3\",\n" +
                    "            \"OS-EXT-SRV-ATTR:host\": \"host-7\",\n" +
                    "            \"OS-SRV-USG:terminated_at\": null,\n" +
                    "            \"key_name\": null,\n" +
                    "            \"OS-EXT-SRV-ATTR:hypervisor_hostname\": \"host-7\",\n" +
                    "            \"name\": \"win7_1\",\n" +
                    "            \"created\": \"2022-04-07T02:31:19Z\",\n" +
                    "            \"tenant_id\": \"0415bfa3cc324b65a60b28557f1a9bb1\",\n" +
                    "            \"os-extended-volumes:volumes_attached\": [],\n" +
                    "            \"metadata\": {\n" +
                    "                \"ha\": \"False\",\n" +
                    "                \"flatten\": \"False\"\n" +
                    "            }\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}");
            String theFitos = stringBuffer.toString();

            ObjectNode test = objectNode();
////            test.put(STATUS, theSTATUS);
////            test.put(USER_NAME, theUSER_NAME);
////            test.put(TENANT_NAME, theTENANT_NAME);
////            test.put(CREATE_TIME, theCREATE_TIME);
////            test.put(THERECEIVE, theResponse);
////            test.put(THERECEIVE, theUSER_ID);
            test.put(THERECEIVE, theFitos);
//            test.put(THERECEIVE, theGet);
//            //riskLink.put(LINK_RISK_ID,linkId);
            sendMessage(HI_SGP_RESP, test);

        }
    }

    private final class SayHelloWorld1 extends RequestHandler {
        private SayHelloWorld1() {
            super(HELLO_DCI_REQ);
        }

        public void process(ObjectNode payload) {
            theDCI_1 = string(payload, THE_DCI_SRC_NEID);
            log.info("The DCI first word : {}", theDCI_1);
            theDCI_2 = string(payload, THE_DCI_SRC_CLIENTEP);
            log.info("The DCI second word : {}", theDCI_2);
            theDCI_3 = string(payload, THE_DCI_SRC_PHYSICALCHANNEL);
            log.info("The DCI third word : {}", theDCI_3);
            theDCI_4 = string(payload, THE_DCI_DST_NEID);
            log.info("The DCI fourth word : {}", theDCI_4);
            theDCI_5 = string(payload, THE_DCI_DST_CLIENTEP);
            log.info("The DCI fifth word : {}", theDCI_5);
            theDCI_6 = string(payload, THE_DCI_PHYSICALCHANNEL);
            log.info("The DCI sixth word : {}", theDCI_6);

            String theResponse_DCI = "{\n" +
                    "    \"code\": 0,\n" +
                    "    \"msg\": \"响应成功\",\n" +
                    "    \"data\": {\n" +
                    "        \"total\": 2,\n" +
                    "        \"neList\": [\n" +
                    "            {\n" +
                    "                \"id\": \"78\",\n" +
                    "                \"password\": \"fiberhome\",\n" +
                    "                \"username\": \"fiberhome\",\n" +
                    "                \"snId\": \"344b00000100\",\n" +
                    "                \"customName\": \"78\",\n" +
                    "                \"managerIp\": \"10.190.85.78/24\",\n" +
                    "                \"status\": \"connected\",\n" +
                    "                \"nodeId\": \"78\",\n" +
                    "                \"version\": \"V1R2-2021-10-19-21:57:28\",\n" +
                    "                \"mergeNe\": \"78\",\n" +
                    "                \"deviceType\": \"device\",\n" +
                    "                \"timezone\": \"Asia/Shanghai\"\n" +
                    "            },\n" +
                    "            {\n" +
                    "                \"id\": \"79\",\n" +
                    "                \"password\": \"fiberhome\",\n" +
                    "                \"username\": \"fiberhome\",\n" +
                    "                \"snId\": \"344b00000101\",\n" +
                    "                \"customName\": \"79\",\n" +
                    "                \"managerIp\": \"10.190.85.79/24\",\n" +
                    "                \"status\": \"connected\",\n" +
                    "                \"nodeId\": \"79\",\n" +
                    "                \"version\": \"V1R2-2021-10-19-21:57:28\",\n" +
                    "                \"mergeNe\": \"79\",\n" +
                    "                \"deviceType\": \"device\",\n" +
                    "                \"timezone\": \"Asia/Shanghai\"\n" +
                    "            }\n" +
                    "        ],\n" +
                    "        \"chassisList\": null,\n" +
                    "        \"cardList\": null,\n" +
                    "        \"portList\": null\n" +
                    "    }\n" +
                    "}\n";

            ObjectNode test_DCI = objectNode();
            test_DCI.put(THERECEIVE, theResponse_DCI);
            sendMessage(HI_SGP_RESP_DCI, test_DCI);

        }

    }

    private final class SayHelloWorld2 extends RequestHandler {
        private SayHelloWorld2() {
            super(HELLO_QINGHUA_REQ);
        }

        public void process(ObjectNode payload) {
            theQINGHUA = string(payload, THE_QINGHUA_LIST);
            log.info("The QINGHUA word : {}", theQINGHUA);

            String theResponse_qinghua =("[{\"tasks_routing\":[\"task1->task2: 11010-11001\",\n" +
                    "\"task1->task3: 11010-11002\",\n" +
                    "\"task2->task4: 11001-11005\",\n" +
                    "\"task2->task5: 11001-11006\",\n" +
                    "\"task3->task5: 11002-11006\",\n" +
                    "\"task3->task6: 11002-11003\",\n" +
                    "\"task4->task7: 11005-11004\",\n" +
                    "\"task5->task7: 11006-11002-11004\",\n" +
                    "\"task6->task7: 11003-11004\",\n" +
                    "\"data1->task2: 11002-11001\"],\n" +
                    "\"finish_time\":\"900us\"},\n" +
                    "{\"tasks_routing\":[\"task1->task2: 11005-11001\",\n" +
                    "\"task1->task3: 11005-11002\",\n" +
                    "\"task2->task4: 11001-11006\",\n" +
                    "\"task3->task4: 11002-11006\",\n" +
                    "\"data1->task1: 11001-11005\",\n" +
                    "\"data2->task3: 11091-11009-11002\"],\n" +
                    "\"finish_time\":\"700us\"}]");
            ObjectNode test_qinghua = objectNode();
            test_qinghua.put(THERECEIVE_QINGHUA, theResponse_qinghua);
            sendMessage(HI_SGP_QINGHUA, test_qinghua);

        }
    }

    private final class SayHelloWorld3 extends RequestHandler {
        private SayHelloWorld3() {
            super(HELLO_DCN_REQ);
        }

        public void process(ObjectNode payload) {
            theDCN = string(payload, THE_DCN_STATUS);
            log.info("The QINGHUA word : {}", theDCN);

            String theResponse_DCN_DEVICE =("{\"firewall\":1,\"gateway\":1,\"switchDevice\":2,\"server\":7,\"total\":4,\"moreGateway\":0,\"moreSwitch\":0}");
            String theResponse_DCN_PORT =("{\"ge\":0,\"tenGe\":144,\"fortyGe\":18,\"hundredGe\":0,\"total\":162,\"totalUsed\":30,\"totalFree\":132}");
            String theResponse_DCN_CONTROL =("[{\"member\":\"member-2\",\n" +
                    "\"ip\":\"10.10.10.12\",\n" +
                    "\"lastSeenTime\":1677222177426,\n" +
                    "\"bladeId\":168430092,\n" +
                    "\"memoryUseRatio\":{\"used\":\" 32289064K\",\"total\":\" 32779956K\",\"free\":\" 490892K\",\"ratio\":\"98.50246290751579%\"},\n" +
                    "\"cpuUseRatios\":[{\"name\":\"CPU-0-Perc\",\"user\":\" 3.4%\",\"system\":\" 2.4%\",\"nice\":\" 0.0%\",\"wait\":\" 0.0%\",\"idle\":\" 93.9%\"},\n" +
                    "\t           {\"name\":\"CPU-1-Perc\",\"user\":\" 4.8%\",\"system\":\" 2.2%\",\"nice\":\" 0.0%\",\"wait\":\" 0.0%\",\"idle\":\" 92.8%\"},\n" +
                    "                           {\"name\":\"CPU-2-Perc\",\"user\":\" 26.2%\",\"system\":\" 1.2%\",\"nice\":\" 0.0%\",\"wait\":\" 0.0%\",\"idle\":\" 72.4%\"},\n" +
                    "                           {\"name\":\"CPU-3-Perc\",\"user\":\" 5.1%\",\"system\":\" 2.4%\",\"nice\":\" 0.0%\",\"wait\":\" 0.0%\",\"idle\":\" 92.2%\"},\n" +
                    "                           {\"name\":\"CPU-4-Perc\",\"user\":\" 3.9%\",\"system\":\" 1.5%\",\"nice\":\" 0.0%\",\"wait\":\" 0.0%\",\"idle\":\" 94.3%\"},\n" +
                    "                           {\"name\":\"CPU-5-Perc\",\"user\":\" 5.7%\",\"system\":\" 1.7%\",\"nice\":\" 0.0%\",\"wait\":\" 0.0%\",\"idle\":\" 92.4%\"},\n" +
                    "                           {\"name\":\"CPU-6-Perc\",\"user\":\" 3.7%\",\"system\":\" 1.9%\",\"nice\":\" 0.0%\",\"wait\":\" 0.0%\",\"idle\":\" 94.1%\"},\n" +
                    "                           {\"name\":\"CPU-7-Perc\",\"user\":\" 4.5%\",\"system\":\" 2.2%\",\"nice\":\" 0.0%\",\"wait\":\" 0.0%\",\"idle\":\" 93.0%\"}],\n" +
                    "\"fileUseRatios\":[{\"name\":\"Disk-NONE-/\",\"path\":\"/\",\"avail\":\"2925592\",\"usePercent\":\"0.95\",\"diskQueue\":\"-1.0\",\"free\":\"5856356}\",\"used\":\"49477608\",\"total\":\"52403200\",\"files\":\"5856356}\",\"freeFiles\":\"5856356}\"}]},\n" +
                    "\n" +
                    "{\"member\":\"member-3\",\n" +
                    "\"ip\":\"10.10.10.13\",\n" +
                    "\"lastSeenTime\":1677222180258,\n" +
                    "\"bladeId\":168430093,\n" +
                    "\"memoryUseRatio\":{\"used\":\" 32400248K\",\"total\":\" 32779956K\",\"free\":\" 379708K\",\"ratio\":\"98.84164579110478%\"},\n" +
                    "\"cpuUseRatios\":[{\"name\":\"CPU-0-Perc\",\"user\":\" 4.6%\",\"system\":\" 2.4%\",\"nice\":\" 0.0%\",\"wait\":\" 0.0%\",\"idle\":\" 92.5%\"},\n" +
                    "                           {\"name\":\"CPU-1-Perc\",\"user\":\" 35.3%\",\"system\":\" 1.3%\",\"nice\":\" 0.0%\",\"wait\":\" 0.0%\",\"idle\":\" 62.9%\"},  \n" +
                    "                           {\"name\":\"CPU-2-Perc\",\"user\":\" 4.0%\",\"system\":\" 2.2%\",\"nice\":\" 0.0%\",\"wait\":\" 0.0%\",\"idle\":\" 93.4%\"},\n" +
                    "                           {\"name\":\"CPU-3-Perc\",\"user\":\" 5.6%\",\"system\":\" 2.2%\",\"nice\":\" 0.0%\",\"wait\":\" 0.0%\",\"idle\":\" 91.7%\"}, \n" +
                    "                           {\"name\":\"CPU-4-Perc\",\"user\":\" 5.2%\",\"system\":\" 3.2%\",\"nice\":\" 0.0%\",\"wait\":\" 0.0%\",\"idle\":\" 91.1%\"},\n" +
                    "                           {\"name\":\"CPU-5-Perc\",\"user\":\" 3.4%\",\"system\":\" 2.4%\",\"nice\":\" 0.0%\",\"wait\":\" 0.0%\",\"idle\":\" 93.7%\"}, \n" +
                    "                           {\"name\":\"CPU-6-Perc\",\"user\":\" 13.2%\",\"system\":\" 2.6%\",\"nice\":\" 0.0%\",\"wait\":\" 0.0%\",\"idle\":\" 83.8%\"},\n" +
                    "                           {\"name\":\"CPU-7-Perc\",\"user\":\" 3.4%\",\"system\":\" 2.2%\",\"nice\":\" 0.0%\",\"wait\":\" 0.0%\",\"idle\":\" 93.8%\"}],\n" +
                    "\"fileUseRatios\":[{\"name\":\"Disk-NONE-/\",\"path\":\"/\",\"avail\":\"4529156\",\"usePercent\":\"0.92\",\"diskQueue\":\"-1.0\",\"free\":\"9064562}\",\"used\":\"47874044\",\"total\":\"52403200\",\"files\":\"9064562}\",\"freeFiles\":\"9064562}\"}]},\n" +
                    "\n" +
                    "{\"member\":\"member-1\",\n" +
                    "\"ip\":\"10.10.10.11\",\n" +
                    "\"lastSeenTime\":1677222179512,\n" +
                    "\"bladeId\":168430091,\n" +
                    "\"memoryUseRatio\":{\"used\":\" 30432752K\",\"total\":\" 32779956K\",\"free\":\" 2347204K\",\"ratio\":\"92.83951448867106%\"},\n" +
                    "\"cpuUseRatios\":[{\"name\":\"CPU-0-Perc\",\"user\":\" 16.6%\",\"system\":\" 1.5%\",\"nice\":\" 0.0%\",\"wait\":\" 0.0%\",\"idle\":\" 81.1%\"},\n" +
                    "                           {\"name\":\"CPU-1-Perc\",\"user\":\" 24.4%\",\"system\":\" 1.6%\",\"nice\":\" 0.0%\",\"wait\":\" 0.0%\",\"idle\":\" 73.3%\"},\n" +
                    "                           {\"name\":\"CPU-2-Perc\",\"user\":\" 20.8%\",\"system\":\" 3.5%\",\"nice\":\" 0.0%\",\"wait\":\" 0.0%\",\"idle\":\" 75.2%\"},\n" +
                    "                           {\"name\":\"CPU-3-Perc\",\"user\":\" 16.5%\",\"system\":\" 2.4%\",\"nice\":\" 0.0%\",\"wait\":\" 0.0%\",\"idle\":\" 80.5%\"},\n" +
                    "                           {\"name\":\"CPU-4-Perc\",\"user\":\" 13.4%\",\"system\":\" 3.4%\",\"nice\":\" 0.0%\",\"wait\":\" 0.0%\",\"idle\":\" 82.8%\"},\n" +
                    "                           {\"name\":\"CPU-5-Perc\",\"user\":\" 17.3%\",\"system\":\" 2.7%\",\"nice\":\" 0.0%\",\"wait\":\" 0.0%\",\"idle\":\" 79.5%\"},\n" +
                    "                           {\"name\":\"CPU-6-Perc\",\"user\":\" 16.1%\",\"system\":\" 2.0%\",\"nice\":\" 0.0%\",\"wait\":\" 0.0%\",\"idle\":\" 81.3%\"},\n" +
                    "                           {\"name\":\"CPU-7-Perc\",\"user\":\" 22.8%\",\"system\":\" 1.7%\",\"nice\":\" 0.0%\",\"wait\":\" 0.0%\",\"idle\":\" 74.6%\"}],\n" +
                    "\"fileUseRatios\":[{\"name\":\"Disk-NONE-/\",\"path\":\"/\",\"avail\":\"3201440\",\"usePercent\":\"0.94\",\"diskQueue\":\"-1.0\",\"free\":\"6405396}\",\"used\":\"49201760\",\"total\":\"52403200\",\"files\":\"6405396}\",\"freeFiles\":\"6405396}\"}]}]");
            ObjectNode test_DCN = objectNode();
            test_DCN.put(DEVICE, theResponse_DCN_DEVICE);
            test_DCN.put(PORT, theResponse_DCN_PORT);
            test_DCN.put(CONTROL, theResponse_DCN_CONTROL);
            sendMessage(HI_SGP_RESP_DCN, test_DCN);



        }
    }
}