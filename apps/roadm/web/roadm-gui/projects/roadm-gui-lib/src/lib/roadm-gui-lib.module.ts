/*
 * Copyright 2019-present Open Networking Foundation
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

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Gui2FwLibModule } from 'gui2-fw-lib';
import { RoadmDeviceComponent } from './roadm/roadm.component';
import { RoadmRoutingModule } from './roadm-routing.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RoadmPortComponent } from './port/port.component';
import { HomeComponent } from './roadm/components/home/home.component';
import { TopologyComponent } from './roadm/components/topology/topology.component';
import { ApplicationComponent } from './roadm/components/application/application.component';
import { StorageService } from './roadm/services/storage.service';
import { TopoService } from './roadm/services/topo.service';
import { HttpClientModule } from '@angular/common/http';
import { AuxiliaryComponent } from './roadm/components/topology/auxiliary/auxiliary.component';
import { VirtualComponent } from './roadm/components/topology/virtual/virtual.component';
import { XinjiangComponent } from './roadm/components/topology/xinjiang/xinjiang.component';
import { XinjiangdataService } from './roadm/services/xinjiangdata.service';
import { Firberhome1Component } from './roadm/components/cloud/firberhome1/firberhome1.component';
import { Firberhome2Component } from './roadm/components/cloud/firberhome2/firberhome2.component';
import { SimuComponent } from './roadm/components/topology/simu/simu.component';
import { DciComponent } from './roadm/components/network/dci/dci.component';
import { DcnComponent } from './roadm/components/network/dcn/dcn.component';
import { HustComponent } from './roadm/components/cloud/hust/hust.component';

@NgModule({
    declarations: [
        RoadmDeviceComponent,
        RoadmPortComponent,
        HomeComponent,
        TopologyComponent,
        ApplicationComponent,
        AuxiliaryComponent,
        VirtualComponent,
        XinjiangComponent,
        Firberhome1Component,
        Firberhome2Component,
        SimuComponent,
        DciComponent,
        DcnComponent,
        HustComponent,
    ],
    imports: [
        RoadmRoutingModule,
        Gui2FwLibModule,
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        HttpClientModule,
    ],
    exports: [
        RoadmDeviceComponent,
        RoadmPortComponent,
    ],
    providers: [
        StorageService,
        TopoService,
        XinjiangdataService,
    ],
})
export class RoadmGuiLibModule { }
