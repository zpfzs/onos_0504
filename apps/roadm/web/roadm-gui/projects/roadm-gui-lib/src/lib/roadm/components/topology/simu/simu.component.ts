import { Component, OnInit } from '@angular/core';
import {
    FnService,
    LogService,
    WebSocketService,
    SortDir, TableBaseImpl, TableResponse
} from 'gui2-fw-lib';
@Component({
  selector: 'roadm-app-simu',
  templateUrl: './simu.component.html',
  styleUrls: ['./simu.component.css']
})
export class SimuComponent implements OnInit {

  public sig = true;
  public raw_index = false;
  public use_index = false;
  public connect_index = false;
  public service:any={
  raw_data:[],
  use:[],
  connect:[]
  }
  public rawData:any={
  node:'',
  task:'',
  resource:''
  }
  public Use:any={
  serial:'',
  calculate:'',
  transport:''
  }
  public Connect:any={
  serial:'',
  connection:'',
  }
  public service_list:any[]=[];
  public raw_data_detail:any[]=[];
  public use_detail:any[]=[];
  public connect_detail:any[]=[];
  public standard_service:any={
    task_num:0,
    raw_data_num:0,
    raw_data_info:[],
    task_info:[],
    task_relation:[]
  }
  public final_submit:any={
    services_num:0,
    services_list:[]
  }
  public submit_back:string='';
  public handlers:any[]=[];
  public receive:string='';
//   public finish_time:any[]=['900us','700us'];
  public output:any[]=[];
  public output_service={
  finish_time:'',
  tasks_routing:[]
  }
  public output_unit={
  service_serial:0,
  source_node:'',
  source_task:'',
  end_node:'',
  end_task:'',
  path:''
  }

  constructor(protected fs: FnService,
              protected log: LogService,
              protected wss: WebSocketService,) { }
  submit1(){
  this.service.raw_data.push(JSON.parse(JSON.stringify(this.rawData)));
  this.rawData={
                node:'',
                task:'',
                resource:''
                }
  }
  submit2(){
  this.service.use.push(JSON.parse(JSON.stringify(this.Use)));
  this.Use={
             serial:'',
             calculate:'',
             transport:''
             }
  }
  submit3(){
  this.service.connect.push(JSON.parse(JSON.stringify(this.Connect)));
  this.Connect={
                 serial:'',
                 connection:'',
                 }
  }
  submit4(){
  this.service_list.push(JSON.parse(JSON.stringify(this.service)));
  this.service={
                 raw_data:[],
                 use:[],
                 connect:[]
                 }
  }
  clear1(){
  this.service.raw_data=[];
  }
  clear2(){
  this.service.use=[];
  }
  clear3(){
  this.service.connect=[];
  }
  detail1(x:number){
  this.raw_data_detail=this.service_list[x].raw_data;
//   console.log(this.raw_data_detail);
  this.raw_index=true;
  }
  detail2(x:number){
  this.use_detail=this.service_list[x].use;
//   console.log(this.use_detail);
  this.use_index=true;
  }
  detail3(x:number){
  this.connect_detail=this.service_list[x].connect;
//   console.log(this.connect_detail);
  this.connect_index=true;
  }
  vanish1(){
  this.raw_index=false;
  }
  vanish2(){
  this.use_index=false;
  }
  vanish3(){
  this.connect_index=false;
  }
  reset(){
  this.service_list=[];
  }
  comeOut(){
  this.sig=!this.sig;
  }
  finalSub(){
  if(this.service_list){
    this.final_submit={
                         services_num:0,
                         services_list:[]
                       }
    for(let i=0;i<this.service_list.length;i++){
        this.standard_service.task_num=this.service_list[i].use.length;
        this.standard_service.raw_data_num=this.service_list[i].raw_data.length;
        this.standard_service.raw_data_info=this.service_list[i].raw_data;
        this.standard_service.task_info=this.service_list[i].use;
        this.standard_service.task_relation=this.service_list[i].connect;
        this.final_submit.services_list.push(JSON.parse(JSON.stringify(this.standard_service)));
    }
    this.final_submit.services_num=this.service_list.length;
  }
  console.log(this.final_submit);
  this.submit_back='业务数：\n'+String(this.final_submit.services_num);
  for(let i=0;i<this.final_submit.services_num;i++){
    this.submit_back = this.submit_back+'\n'+'\n业务'+String(i+1);
    this.submit_back = this.submit_back+'\n任务数：\n'+String(this.final_submit.services_list[i].task_num);
    this.submit_back = this.submit_back+'\n原始数据数：\n'+String(this.final_submit.services_list[i].raw_data_num);
    this.submit_back = this.submit_back+'\n原始数据信息：';
    let Service=this.final_submit.services_list[i];
    let index_raw=1;
    for(let item0 of Service.raw_data_info){
        this.submit_back = this.submit_back+'\n'+String(index_raw)+': ['+String(item0.node)+','+String(item0.task)+','+String(item0.resource)+']';
        index_raw += 1;
    }
    this.submit_back = this.submit_back+'\n'+'资源消耗：';
    for(let item1 of Service.task_info){
        this.submit_back = this.submit_back+'\n'+String(item1.serial)+': ['+String(item1.calculate)+','+String(item1.transport)+']';
    }
    this.submit_back = this.submit_back+'\n'+'连接关系：';
    for(let item2 of Service.task_relation){
        this.submit_back = this.submit_back+'\n'+String(item2.serial)+': '+String(item2.connection);
    }
  }

  console.log(this.submit_back);
  }
  SendMessageToBackward(){
                this.finalSub();
                if(this.wss.isConnected){
                    this.wss.sendEvent('alRequest',{
                    'servicelist':this.submit_back,
                    });
                    this.log.info('websocket发送helloworld成功');
                }
  }
  test(){
  console.log(this.receive);
    let deal_data=JSON.parse(this.receive);
    console.log(deal_data);
    let index=1;
    for(let item of deal_data.serviceList){
        this.output_service.finish_time=item.finish_time;
        this.output_unit.service_serial=index;
        index += 1;
        for(let unit of item.tasks_routing){
            this.output_unit.source_task=unit.slice(0,5);
            this.output_unit.end_task=unit.slice(7,12);
            this.output_unit.source_node=unit.slice(14,19);
            this.output_unit.end_node=unit.slice(unit.length-5,unit.length);
            this.output_unit.path=unit.slice(14,unit.length);
            this.output_service.tasks_routing.push(JSON.parse(JSON.stringify(this.output_unit)));
        }
        this.output.push(JSON.parse(JSON.stringify(this.output_service)));
        this.output_service.tasks_routing=[];
    }
    console.log('输出',this.output);
  }

  ReceiveMessageFromBackward(){
            this.wss.bindHandlers(new Map<string,(data)=>void>([
                ['alResponse',(data)=>{
                    this.log.info(data);
                    this.receive=data['serviceList message'];
                }],
            ]));
            this.handlers.push('alResponse');
            this.SendMessageToBackward();
            setTimeout(() => {this.test();},100);
  }
  ngOnInit() {
  }

}
