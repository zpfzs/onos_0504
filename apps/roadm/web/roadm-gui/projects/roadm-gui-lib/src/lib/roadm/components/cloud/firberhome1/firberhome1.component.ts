import { Component, OnInit } from '@angular/core';
import { StorageService } from '../../../../roadm/services/storage.service';
import {
    FnService,
    LogService,
    WebSocketService,
    SortDir, TableBaseImpl, TableResponse
} from 'gui2-fw-lib';
declare var echarts:any;
@Component({
  selector: 'roadm-app-firberhome1',
  templateUrl: './firberhome1.component.html',
  styleUrls: ['./firberhome1.component.css']
})
export class Firberhome1Component implements OnInit {


  public option1:any;
  public option2:any;
  public option3:any;
  public adress:string="10.112.231.75";
  public mirror_image:any[]=["8113da59-e608-40ab-a7e9-d4191127dc88"];
  public segment:any[]=["8a98c674-bea1-423f-a671-b4a64da0b3a9"];
  public config:any[]=["4U8G100G"];
  public vm:any={
    name:"",
    id:"",
    region:"",
    create_time:"",
//     ip:"",
    image:"",
    status:""
  }
  public creat_form:any={
    name:"",
    image:"",
    seg:"",
    cfg:""
  }
  public vmlist:any[]=[];
  public delete_name_list:any[]=[];
  public delete_vm:any;
  public handlers:any[]=[];
  public receive:string='';
  public receiveData:any={
    status:"",
    u_name:"",
    T_name:"",
    time:""
  }
  public recj:any;
  public del_serial=-1;
  public del_id='';
  constructor(public storage:StorageService,
            protected fs: FnService,
            protected log: LogService,
            protected wss: WebSocketService,) {
  }
  choose(i:number){
  this.del_serial=i;
  console.log(this.del_serial);
  }
  reset(){
  this.vmlist=[];
  this.delete_name_list=[];
  if(this.recj){
    let carrier={
                  name:"",
                  id:"",
                  region:"",
                  create_time:"",
//                   ip:"",
                  image:"",
                  status:""
                };
    for(let i=0;i<this.recj.servers.length;i++){

        let uuu=this.recj.servers[i];
        carrier.name=uuu.name;
        carrier.id=uuu.id;
        carrier.region=uuu["OS-EXT-AZ:availability_zone"];
        carrier.create_time=uuu.created;
//         carrier.ip="乱了";
        carrier.image=uuu.image.id;
        carrier.status=uuu.status;
        console.log(carrier);
        this.vmlist.push(JSON.parse(JSON.stringify(carrier)));
        this.delete_name_list.push(JSON.parse(JSON.stringify(carrier.name)));
    }
  }
  this.storage.set('vmlist',this.vmlist);//装入服务
  this.storage.set('namelist',this.delete_name_list);

  }
  test(){
  console.log('接收',this.receive);
  console.log('类型',typeof this.receive);
  let aq = JSON.parse(this.receive);
   console.log(aq);
   this.recj = aq;
  console.log(this.recj.servers);
  this.setValue();//需要重置业务列表用this.reset()，正常添加用this.setValue()；注意这两个函数不同时运行
//   this.reset();
  }
  SendMessageToBackward(){
              if(this.wss.isConnected){
                  this.wss.sendEvent('vmCreateRequest',{
                  'name':this.creat_form.name,
                  'imageRef':this.creat_form.image,
                  'uuid':this.creat_form.seg,
                  'flavorRef':this.creat_form.cfg,
                  });
                  this.log.info('websocket发送helloworld成功');
              }
  }
  SendDelete(){
                if(this.wss.isConnected){
                    this.wss.sendEvent('vmDeleteRequest',{
                    'id':this.del_id,
                    });
                    this.log.info('websocket发送helloworld成功');
                }
  }
  receiveDelete(){
   this.wss.bindHandlers(new Map<string,(data)=>void>([
              ['vmDeleteResponse',(data)=>{
                  this.log.info(data);
  //                 this.receiveData.status = data['status'];
  //                 this.receiveData.u_name = data['user_name'];
  //                 this.receiveData.T_name = data['tenant_name'];
  //                 this.receiveData.time = data['creat_time'];
                  this.receive = data['receive message'];
              }]
          ]));
          this.handlers.push('vmDeleteResponse');
          this.SendDelete();


  }
  ReceiveMessageFromBackward(){
        this.wss.bindHandlers(new Map<string,(data)=>void>([
            ['hiResponse',(data)=>{
                this.log.info(data);
//                 this.receiveData.status = data['status'];
//                 this.receiveData.u_name = data['user_name'];
//                 this.receiveData.T_name = data['tenant_name'];
//                 this.receiveData.time = data['creat_time'];
                this.receive = data['receive message'];
            }]
        ]));
        this.handlers.push('hiResponse');
        this.SendMessageToBackward();
        setTimeout(() => {this.test();},2000);
  }

  vmpush(){
      this.vmlist.push(JSON.parse(JSON.stringify(this.vm)));
      this.storage.set('vmlist',this.vmlist);//装入服务
      this.delete_name_list.push(JSON.parse(JSON.stringify(this.vm.name)));
      this.storage.set('namelist',this.delete_name_list);

  }
  judge(){
    let logo=0;
    for(let i=0;i<this.vmlist.length;i++){
        if(this.vmlist[i].name==this.creat_form.name){
            logo=1;
        }
    }
    if(logo==1){
        alert("名称重复!");
    }else{this.submit();}
  }
  del() {
    let del_v=this.delete_vm
    console.log("shancuyuansu",this.delete_vm);
    let index=-1
    for(let i=0;i<this.vmlist.length;i++){
        console.log(this.vmlist[i])
        if(this.vmlist[i].name==del_v){
            index=i
        }
    }
    console.log("index",index);
    if(index>-1){
        this.vmlist.splice(index,1);
        console.log(this.vmlist);
        this.delete_name_list.splice(index,1);
        this.storage.set('vmlist',this.vmlist);
        this.storage.set('namelist',this.delete_name_list);
    }
    this.del_id=this.vmlist[this.del_serial].id;
    console.log(this.del_id);
    window.location.reload();

  }
  clear(){
  this.vmlist=[]
  this.delete_name_list=[]
  this.storage.set('vmlist',this.vmlist);
  this.storage.set('namelist',this.delete_name_list);

  }
  setValue() {
    if(this.recj){
        let uuu=this.recj.servers[this.recj.servers.length-1];
        this.vm.name=uuu.name;
            this.vm.id=uuu.id;
            this.vm.region=uuu["OS-EXT-AZ:availability_zone"];
            this.vm.create_time=uuu.created;
//             this.vm.ip="乱了";
            this.vm.image=uuu.image.id;
            this.vm.status=uuu.status;
            this.storage.set('vm',this.vm);
            this.vmpush();
            console.log("列表长度",this.vmlist.length);
    }

  }
  submit(){
//   this.SendMessageToBackward();
    this.ReceiveMessageFromBackward();

  }
  ngOnInit() {
    let myChart1=echarts.init(document.getElementById('bar1'));
                  this.option1 = {
                      tooltip: {
                        trigger: 'item'
                      },
                      legend: {
                        top: '5%',
                        left: 'center'
                      },
                      series: [
                        {
                          name: 'Access From',
                          type: 'pie',
                          radius: ['40%', '70%'],
                          avoidLabelOverlap: false,
                          label: {
                            show: false,
                            position: 'center'
                          },
                          emphasis: {
                            label: {
                              show: true,
                              fontSize: '40',
                              fontWeight: 'bold'
                            }
                          },
                          labelLine: {
                            show: false
                          },
                          data: [
                            { value: 700, name: '占用' },
                            { value: 300, name: '空闲' }
                          ]
                        }
                      ]
                  };
                  myChart1.setOption(this.option1);

    let myChart2=echarts.init(document.getElementById('bar2'));
                      this.option2 = {
                          tooltip: {
                            trigger: 'item'
                          },
                          legend: {
                            top: '5%',
                            left: 'center'
                          },
                          series: [
                            {
                              name: 'Access From',
                              type: 'pie',
                              radius: ['40%', '70%'],
                              avoidLabelOverlap: false,
                              label: {
                                show: false,
                                position: 'center'
                              },
                              emphasis: {
                                label: {
                                  show: true,
                                  fontSize: '40',
                                  fontWeight: 'bold'
                                }
                              },
                              labelLine: {
                                show: false
                              },
                              data: [
                                { value: 650, name: '占用' },
                                { value: 350, name: '空闲' }
                              ]
                            }
                          ]
                      };
                      myChart2.setOption(this.option2);

    let myChart3=echarts.init(document.getElementById('bar3'));
                      this.option3 = {
                          tooltip: {
                            trigger: 'item'
                          },
                          legend: {
                            top: '5%',
                            left: 'center'
                          },
                          series: [
                            {
                              name: 'Access From',
                              type: 'pie',
                              radius: ['40%', '70%'],
                              avoidLabelOverlap: false,
                              label: {
                                show: false,
                                position: 'center'
                              },
                              emphasis: {
                                label: {
                                  show: true,
                                  fontSize: '40',
                                  fontWeight: 'bold'
                                }
                              },
                              labelLine: {
                                show: false
                              },
                              data: [
                                { value: 600, name: '占用' },
                                { value: 400, name: '空闲' }
                              ]
                            }
                          ]
                      };
                      myChart3.setOption(this.option3);




    let list1=this.storage.get('vmlist')//导出服务
        if(list1){
          this.vmlist=list1;
        }
        let obj1=this.storage.get('vm')//导出服务
        if(obj1){
          this.vm=obj1;
        }
        let name1=this.storage.get('namelist')
        if(name1){
          this.delete_name_list=name1;
        }

  }

}
