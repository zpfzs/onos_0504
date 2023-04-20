import { Component, OnInit, AfterViewInit } from '@angular/core';
declare var echarts:any;
import {
    FnService,
    LogService,
    WebSocketService,
    SortDir, TableBaseImpl, TableResponse
} from 'gui2-fw-lib';
@Component({
  selector: 'roadm-app-dcn',
  templateUrl: './dcn.component.html',
  styleUrls: ['./dcn.component.css']
})
export class DcnComponent implements OnInit {

  public defender:number=0;
  public wlan:number=0;
  public switch:number=0;
  public server:number=0;
  public option1:any;
  public option2:any;
  public option3:any;
  public option4:any;
  public handlers:any[]=[];
  public device:any;
  public port:any;
  public control:any;
  public gateway_50:number=0;
  public switch_50:number=0;
  public GE:number=0;
  public GE_10:number=0;
  public GE_40:number=0;
  public GE_100:number=0;
  public used:number=0;
  public free:number=0;
  public control_count={
    member2:{
        cpu:0,
        mem:0,
        disk:0
    },
    member3:{
        cpu:0,
        mem:0,
        disk:0
    },
    member1:{
        cpu:0,
        mem:0,
        disk:0
    }
  }
  constructor(          protected fs: FnService,
                        protected log: LogService,
                        protected wss: WebSocketService,) { }
  test(){
    console.log(this.control);
    let Device=JSON.parse(this.device);
    this.defender=Device.firewall;
    this.wlan=Device.gateway;
    this.switch=Device.switchDevice;
    this.server=Device.server;
    this.gateway_50=Device.moreGateway;
    this.switch_50=Device.moreSwitch;
    let Port=JSON.parse(this.port);
    this.GE=Port.ge;
    this.GE_10=Port.tenGe;
    this.GE_40=Port.fortyGe;
    this.GE_100=Port.hundredGe;
    this.used=Port.totalUsed;
    this.free=Port.totalFree;
    let Control=JSON.parse(this.control)
    this.control_count.member2.mem=Number(Control[0].memoryUseRatio.ratio.slice(0,-1));
    this.control_count.member3.mem=Number(Control[1].memoryUseRatio.ratio.slice(0,-1));
    this.control_count.member1.mem=Number(Control[2].memoryUseRatio.ratio.slice(0,-1));
    console.log(this.control_count.member2.mem);
    console.log(this.control_count.member3.mem);
    console.log(this.control_count.member1.mem);
    this.control_count.member2.disk=Number(Control[0].fileUseRatios[0].usePercent)*100;
    this.control_count.member3.disk=Number(Control[1].fileUseRatios[0].usePercent)*100;
    this.control_count.member1.disk=Number(Control[2].fileUseRatios[0].usePercent)*100;
    console.log(this.control_count.member2.disk);
    console.log(this.control_count.member3.disk);
    console.log(this.control_count.member1.disk);
    let cpu1=0;
    for(let i=0;i<Control[0].cpuUseRatios.length;i++){
        cpu1 += Number(Control[0].cpuUseRatios[i].user.slice(0,-1));
        cpu1 += Number(Control[0].cpuUseRatios[i].system.slice(0,-1));
    }
    cpu1 /= Control[0].cpuUseRatios.length;
    let trans1=cpu1.toFixed(1);
    this.control_count.member2.cpu=Number(trans1);
    console.log(this.control_count.member2.cpu);
    let cpu2=0;
    for(let i=0;i<Control[1].cpuUseRatios.length;i++){
        cpu2 += Number(Control[1].cpuUseRatios[i].user.slice(0,-1));
        cpu2 += Number(Control[1].cpuUseRatios[i].system.slice(0,-1));
    }
    cpu2 /= Control[1].cpuUseRatios.length;
    let trans2=cpu2.toFixed(1);
    this.control_count.member3.cpu=Number(trans2);
    console.log(this.control_count.member3.cpu);
    let cpu3=0;
    for(let i=0;i<Control[2].cpuUseRatios.length;i++){
        cpu3 += Number(Control[2].cpuUseRatios[i].user.slice(0,-1));
        cpu3 += Number(Control[2].cpuUseRatios[i].system.slice(0,-1));
    }
    cpu3 /= Control[2].cpuUseRatios.length;
    let trans3=cpu3.toFixed(1);
    this.control_count.member1.cpu=Number(trans3);
    console.log(this.control_count.member1.cpu);
//     字符串转数字
  }
  SendMessageToBackward(){
                if(this.wss.isConnected){
                    this.wss.sendEvent('dcnRequest',{
                    'dcnStatus':'get',
                    });
                    this.log.info('websocket发送helloworld成功');
                }
  }
  ReceiveMessageFromBackward(){
          this.wss.bindHandlers(new Map<string,(data)=>void>([
              ['dcnResponse',(data)=>{
                  this.log.info(data);
                  this.device = data['device'];
                  this.port = data['port'];
                  this.control = data['control'];
              }],
          ]));
          this.handlers.push('dcnResponse1');
          this.SendMessageToBackward();
          setTimeout(() => {this.test();},100);
  }
  ngOnInit(){
  }
  dom() {
  let myChart1=echarts.init(document.getElementById('chart1'));
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
            fontSize: 40,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          { value: this.switch_50, name: '交换机' },
          { value: this.gateway_50, name: '网关' }
        ]
      }
    ]
  };
  myChart1.setOption(this.option1);
  let myChart2=echarts.init(document.getElementById('part21'));
  let datas = [
    [
      { name: '占用', value: this.used },
      { name: '空闲', value: this.free }
    ]
  ];
  this.option2 = {
    title: {
      text: '使用情况',
      left: 'center',
      textStyle: {
        color: 'black',
        fontWeight: 'normal',
        fontSize: 20
      }
    },
    series: datas.map(function (data, idx) {
      var top = idx * 33.3;
      return {
        type: 'pie',
        radius: [20, 60],
        top: top + '%',
        height: '100%',
        left: 'center',
        width: 300,
        itemStyle: {
          borderColor: '#fff',
          borderWidth: 1
        },
        label: {
          alignTo: 'edge',
          formatter: '{name|{b}}\n{time|{c} 个}',
          minMargin: 5,
          edgeDistance: 10,
          lineHeight: 15,
          rich: {
            time: {
              fontSize: 10,
              color: '#999'
            }
          }
        },
        labelLine: {
          length: 15,
          length2: 0,
          maxSurfaceAngle: 80
        },
        labelLayout: function (params) {
          const isLeft = params.labelRect.x < myChart2.getWidth() / 2;
          const points = params.labelLinePoints as number[][];
          // Update the end point.
          points[2][0] = isLeft
            ? params.labelRect.x
            : params.labelRect.x + params.labelRect.width;

          return {
            labelLinePoints: points
          };
        },
        data: data
      };
    })
  };
  myChart2.setOption(this.option2);
  let myChart3=echarts.init(document.getElementById('part22'));
  let datas1 = [
    [
      { name: 'GE', value: this.GE },
      { name: '10GE', value: this.GE_10 },
      { name: '40GE', value: this.GE_40 },
      { name: '100GE', value: this.GE_100 }
    ]
  ];
  this.option3 = {
    title: {
      text: '带宽容量',
      left: 'center',
      textStyle: {
        color: 'black',
        fontWeight: 'normal',
        fontSize: 20
      }
    },
    series: datas1.map(function (data, idx) {
      var top = idx * 33.3;
      return {
        type: 'pie',
        radius: [20, 60],
        top: top + '%',
        height: '100%',
        left: 'center',
        width: 300,
        itemStyle: {
          borderColor: '#fff',
          borderWidth: 1
        },
        label: {
          alignTo: 'edge',
          formatter: '{name|{b}}\n{time|{c} 个}',
          minMargin: 5,
          edgeDistance: 10,
          lineHeight: 15,
          rich: {
            time: {
              fontSize: 10,
              color: '#999'
            }
          }
        },
        labelLine: {
          length: 15,
          length2: 0,
          maxSurfaceAngle: 80
        },
        labelLayout: function (params) {
          const isLeft = params.labelRect.x < myChart3.getWidth() / 2;
          const points = params.labelLinePoints as number[][];
          // Update the end point.
          points[2][0] = isLeft
            ? params.labelRect.x
            : params.labelRect.x + params.labelRect.width;

          return {
            labelLinePoints: points
          };
        },
        data: data
      };
    })
  };
  myChart3.setOption(this.option3);
  let myChart4=echarts.init(document.getElementById('part3'));
  this.option4 = {
    legend: {},
    tooltip: {},
    dataset: {
      source: [
        ['product', 'CPU使用率', 'MEM使用率', 'DISK使用率'],
        ['member2', this.control_count.member2.cpu, this.control_count.member2.mem, this.control_count.member2.disk],
        ['member3', this.control_count.member3.cpu, this.control_count.member3.mem, this.control_count.member3.disk],
        ['member1', this.control_count.member1.cpu, this.control_count.member1.mem, this.control_count.member1.disk]
      ]
    },
    xAxis: { type: 'category' },
    yAxis: { name:'使用率%' },
    // Declare several bar series, each will be mapped
    // to a column of dataset.source by default.
    series: [{ type: 'bar' ,itemStyle:{normal:{ barBorderRadius:[30, 30, 0, 0]}}}, { type: 'bar' ,itemStyle:{normal:{ barBorderRadius:[30, 30, 0, 0]}}}, { type: 'bar',itemStyle:{normal:{ barBorderRadius:[30, 30, 0, 0]}} }]
  };
  myChart4.setOption(this.option4);
  }
  ngAfterViewInit(){
  this.ReceiveMessageFromBackward();
  setTimeout(() => {this.dom();},100);
  }
}
