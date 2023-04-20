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
import com.google.common.collect.Range;
import org.onlab.osgi.ServiceDirectory;
import org.onlab.util.Frequency;
import org.onlab.util.Spectrum;
import org.onosproject.net.ChannelSpacing;
import org.onosproject.net.DeviceId;
import org.onosproject.net.OchSignal;
import org.onosproject.net.PortNumber;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.flow.FlowEntry;
import org.onosproject.net.flow.FlowId;
import org.onosproject.net.flow.FlowRuleService;
import org.onosproject.ui.RequestHandler;
import org.onosproject.ui.UiConnection;
import org.onosproject.ui.UiMessageHandler;
import org.onosproject.ui.table.TableModel;
import org.onosproject.ui.table.TableRequestHandler;
import org.onosproject.ui.table.cell.HexLongFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import static org.onosproject.net.Device.Type;
import static org.onosproject.ui.JsonUtils.node;

/**
 * Message handler for DAG
 */
public class DagMessageHandler extends UiMessageHandler {

    private static final String DAG_REQ = "alRequest";
    private static final String DAG_RESP = "alResponse";

    private static final String DAG_INPUT = "servicelist";
    private static final String DAG_OUTPUT = "serviceList message";

    private DeviceService deviceService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void init(UiConnection connection, ServiceDirectory directory) {
        super.init(connection, directory);
        deviceService = get(DeviceService.class);
    }

    @Override
    protected Collection<RequestHandler> createRequestHandlers() {
        return ImmutableSet.of(
                new DagRequestHandler()
        );
    }

    // Handler for Dag_input
    private final class DagRequestHandler extends RequestHandler {
        private DagRequestHandler() {
            super(DAG_REQ);
        }

        @Override
        public void process(ObjectNode payload) {
            String input = string(payload, DAG_INPUT);
            log.info("INPUT : {}",input);
            //执行DAG算法
            new InputUtils().writeToFile(input);
            log.info("InputUtils().writeToFile方法成功调用");
            try {
                new DAG().dagStart();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // Build response
            ObjectNode node = objectNode();
            String output = new OutputUtils().readFromFile();
            node.put(DAG_OUTPUT, output);
            log.info("OUTPUT : {}",output);
            sendMessage(DAG_RESP, node);

        }
    }
}
