/*
 * Copyright 2019-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { RoadmDeviceComponent } from './roadm/roadm.component';
import { RoadmPortComponent } from './port/port.component';
import { HomeComponent } from './roadm/components/home/home.component';
import { TopologyComponent } from './roadm/components/topology/topology.component';
import { ApplicationComponent } from './roadm/components/application/application.component';
import { Firberhome1Component } from './roadm/components/cloud/firberhome1/firberhome1.component';
import { AuxiliaryComponent } from './roadm/components/topology/auxiliary/auxiliary.component';
import { VirtualComponent } from './roadm/components/topology/virtual/virtual.component';
import { XinjiangComponent } from './roadm/components/topology/xinjiang/xinjiang.component';
import { Firberhome2Component } from './roadm/components/cloud/firberhome2/firberhome2.component';
import { SimuComponent } from './roadm/components/topology/simu/simu.component';
import { DciComponent } from './roadm/components/network/dci/dci.component';
import { DcnComponent } from './roadm/components/network/dcn/dcn.component';
import { HustComponent } from './roadm/components/cloud/hust/hust.component';

const appRoutes: Routes = [
    {
        path: 'roadm-port-gui',
        component: RoadmPortComponent
    },
    {
        path: '',
        component: RoadmDeviceComponent,
        children:[
                     {
                         path: 'home',
                         component: HomeComponent
                     },
                     {
                         path: 'topology',
                         component: TopologyComponent,
                         children:[
                            {
                                path: 'virtual',
                                component: VirtualComponent
                            },
                            {
                                path: 'auxiliary',
                                component: AuxiliaryComponent
                            },
                            {
                                path: 'xinjiang',
                                component: XinjiangComponent
                            },
                            {
                                path: 'simu',
                                component: SimuComponent
                            },
                            {
                                path: '',
                                redirectTo: 'virtual'
                            }
                         ]
                     },
                     {
                         path: 'application',
                         component: ApplicationComponent
                     },
                     {
                         path: '',
                         redirectTo: 'home'
                     },
                     {
                         path: 'fiberhome1',
                         component: Firberhome1Component
                     },
                     {
                         path: 'fiberhome2',
                         component: Firberhome2Component
                     },
                     {
                         path: 'hust',
                         component: HustComponent
                     },
                     {
                         path: 'dci',
                         component: DciComponent
                     },
                     {
                         path: 'dcn',
                         component: DcnComponent
                     }
        ]
    },

];

/**
 * ONOS GUI -- Roadm View Feature Routing Module - allows it to be lazy loaded
 *
 * See https://angular.io/guide/lazy-loading-ngmodules
 */
@NgModule({
    imports: [RouterModule.forChild(appRoutes)],
    exports: [RouterModule]
})
export class RoadmRoutingModule { }
