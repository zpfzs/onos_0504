import {Component, OnInit} from '@angular/core';
declare var echarts:any;
@Component({
    selector: 'roadm-app-home',
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {


  constructor() { }

  ngOnInit() {
//     let myChart4=echarts.init(document.getElementById('bar2'));
//     this.option4 = {
//                      graphic: {
//                        elements: [
//                          {
//                            type: 'group',
//                            left: 'center',
//                            top: 'center',
//                            children: new Array(7).fill(0).map((val, i) => ({
//                              type: 'rect',
//                              x: i * 20,
//                              shape: {
//                                x: 0,
//                                y: -40,
//                                width: 10,
//                                height: 80
//                              },
//                              style: {
//                                fill: '#5470c6'
//                              },
//                              keyframeAnimation: {
//                                duration: 1000,
//                                delay: i * 200,
//                                loop: true,
//                                keyframes: [
//                                  {
//                                    percent: 0.5,
//                                    scaleY: 0.3,
//                                    easing: 'cubicIn'
//                                  },
//                                  {
//                                    percent: 1,
//                                    scaleY: 1,
//                                    easing: 'cubicOut'
//                                  }
//                                ]
//                              }
//                            }))
//                          }
//                        ]
//                      }
//                    };
//     myChart4.setOption(this.option4);
//     let myChart5=echarts.init(document.getElementById('bar1'));
//
//               this.option5 = {
//                                graphic: {
//                                  elements: [
//                                    {
//                                      type: 'text',
//                                      left: 'center',
//                                      top: 'center',
//                                      style: {
//                                        text: '重点研发',
//                                        fontSize: 80,
//                                        fontWeight: 'bold',
//                                        lineDash: [0, 200],
//                                        lineDashOffset: 0,
//                                        fill: 'transparent',
//                                        stroke: '#000',
//                                        lineWidth: 1
//                                      },
//                                      keyframeAnimation: {
//                                        duration: 3000,
//                                        loop: true,
//                                        keyframes: [
//                                          {
//                                            percent: 0.7,
//                                            style: {
//                                              fill: 'transparent',
//                                              lineDashOffset: 200,
//                                              lineDash: [200, 0]
//                                            }
//                                          },
//                                          {
//                                            // Stop for a while.
//                                            percent: 0.8,
//                                            style: {
//                                              fill: 'transparent'
//                                            }
//                                          },
//                                          {
//                                            percent: 1,
//                                            style: {
//                                              fill: 'black'
//                                            }
//                                          }
//                                        ]
//                                      }
//                                    }
//                                  ]
//                                }
//                              };
//               myChart5.setOption(this.option5);
  }


}

