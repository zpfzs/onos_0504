import { Component, OnInit } from '@angular/core';
import { StorageService } from '../../../../roadm/services/storage.service';
import {
    FnService,
    LogService,
    WebSocketService,
    SortDir, TableBaseImpl, TableResponse
} from 'gui2-fw-lib';
@Component({
  selector: 'roadm-app-dci',
  templateUrl: './dci.component.html',
  styleUrls: ['./dci.component.css']
})
export class DciComponent implements OnInit {

  public service_type:any[]=['10GE-LAN'];
  public map_path:any[] = ['10GE-LAN->ODU2->ODU4->ODUC2->OTUC2'];
  public map_way:any[] = ['GFP6.2'];
  public ne_list:any[] = ['78','79'];
  public port78_list:any[] = ['78_PORT-1-1-C1-1','78_PORT-1-1-C1-2'];
  public port79_list:any[] = ['79_PORT-1-1-C1-1','79_PORT-1-1-C1-2'];
  public mf_list:any[] = ['16QAM'];
  public fec_list:any[] = ['超强'];
  public wl_list:any[] = ['CE30(193.10000THz-1552.52nm)'];
  public receive:string='';
  public recj:any;

  public servicePanel:any = {
    name:'',
    type:'',
    path:'',
    way:'',
    s_ne:'',
    d_ne:'',
    s_port:'',
    d_port:'',
    mf:'',
    fec:'',
    wl:''
  }
  public serviceElement:any = {
    name:'',
    s_ne:'',
    d_ne:'',
    s_port:'',
    d_port:'',
    status:'',
    create_time:new Date,
    manage_ip:''
  }
  public serviceList:any[]=[];
  public handlers:any[]=[];
  public delete_service:any;
  public delete_name_list:any[]=[];
  constructor(public storage:StorageService,
          protected fs: FnService,
          protected log: LogService,
          protected wss: WebSocketService,
  ) { }
  presub(){
    this.serviceElement.name=this.servicePanel.name;
    this.serviceElement.s_ne=this.servicePanel.s_ne;
    this.serviceElement.d_ne=this.servicePanel.d_ne;
    this.serviceElement.s_port=this.servicePanel.s_port;
    this.serviceElement.d_port=this.servicePanel.d_port;
    this.serviceElement.create_time=new Date;
    this.ReceiveMessageFromBackward();
  }
  sub() {
  if(this.recj){
    this.serviceElement.status=this.recj.data.neList[0].status;
    this.serviceElement.manage_ip=this.recj.data.neList[0].managerIp;
  }
  this.serviceList.push(JSON.parse(JSON.stringify(this.serviceElement)));
  this.storage.set('serv',this.serviceElement);
  this.storage.set('servlist',this.serviceList);
  this.delete_name_list.push(JSON.parse(JSON.stringify(this.serviceElement.name)));
  this.storage.set('delnamelist',this.delete_name_list);
  }
  judge(){
    let logo=0;
    for(let i=0;i<this.serviceList.length;i++){
        if(this.serviceList[i].name==this.servicePanel.name){
            logo=1;
        }
    }
    if(logo==1){
        alert("名称重复!");
    }else{this.presub();}
  }
  del() {
      let del_v=this.delete_service;
      console.log("shancuyuansu",this.delete_service);
      let index=-1
      for(let i=0;i<this.serviceList.length;i++){
          console.log(this.serviceList[i])
          if(this.serviceList[i].name==del_v){
              index=i
          }
      }
      console.log("index",index);
      if(index>-1){
          this.serviceList.splice(index,1);
          console.log(this.serviceList);
          this.delete_name_list.splice(index,1);
          this.storage.set('servlist',this.serviceList);
          this.storage.set('delnamelist',this.delete_name_list);
      }
      window.location.reload();

  }
  initialize(){
  this.serviceElement={
                          name:'',
                          s_ne:'',
                          d_ne:'',
                          s_port:'',
                          d_port:'',
                          status:'',
                          create_time:new Date,
                          manage_ip:''
                        }
  this.serviceList=[];
  this.delete_name_list=[];
    this.storage.set('serv',this.serviceElement);
    this.storage.set('servlist',this.serviceList);
    this.storage.set('delnamelist',this.delete_name_list);
  }
  reload(){
  window.location.reload();
  }
  test(){
  console.log('接收',this.receive);
  let aq = JSON.parse(this.receive);
  console.log(aq);
  this.recj = aq;
  this.sub();
  }
  SendMessageToBackward(){
              if(this.wss.isConnected){
                  this.wss.sendEvent('dciRequest',{
                  'srcNeId':this.serviceElement.s_ne,
                  'srcClientEp':this.serviceElement.s_port.slice(0,-2),
                  'srcPhysicalChannel':this.serviceElement.s_port[this.serviceElement.s_port.length-1],
                  'dstNeId':this.serviceElement.d_ne,
                  'dstClientEp':this.serviceElement.d_port.slice(0,-2),
                  'dstPhysicalChannel':this.serviceElement.d_port[this.serviceElement.d_port.length-1],
                  });
                  this.log.info('websocket发送helloworld成功');
              }
  }
  ReceiveMessageFromBackward(){
        this.wss.bindHandlers(new Map<string,(data)=>void>([
            ['yiResponse',(data)=>{
                this.log.info(data);
                this.receive = data['receive message'];
            }]
        ]));
        this.handlers.push('yiResponse');
        this.SendMessageToBackward();
        setTimeout(() => {this.test();},2000);
  }
  ngOnInit() {
      let list1=this.storage.get('servlist')//导出服务
      if(list1){
        this.serviceList=list1;
      }
        let name1=this.storage.get('delnamelist')
        if(name1){
          this.delete_name_list=name1;
        }
  }

}
