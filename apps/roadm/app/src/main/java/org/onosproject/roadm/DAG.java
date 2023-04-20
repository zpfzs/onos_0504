package org.onosproject.roadm;

import java.nio.file.Path;
import java.util.regex.*;
import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

enum Status {  // Dijkstra节点对象的状态
    // 未被发现, 已被遍历
    UNDISCOVERD, VISITED
}

enum Occupation {  // 计算资源的状态
    // 未分配, 已分配
    UNOCCUPIED, OCCUPIED
}

class JobInfo {
    static final int N = 40;

    int taskNum;
    int dataNum;
    String[] dataPort;
    int[][] dataInfo;
    int[][] resource; // 0列-计算资源，1列-传输资源
    String[] link;

    public int[] delay; // 保存每个任务完成时的延时

    public JobInfo() {
        this.taskNum = 0;
        this.dataNum = 0;
        this.dataPort = new String[N];
        this.dataInfo = new int[N][2];
        this.resource = new int[N][2];
        this.link = new String[N];
        this.delay = new int[N];
    }

    public JobInfo(int taskNum, int dataNum) {
        this.taskNum = taskNum;
        this.dataNum = dataNum;
        this.dataPort = new String[dataNum];
        this.dataInfo = new int[dataNum][2];
        this.resource = new int[taskNum][2];
        this.link = new String[taskNum];
        this.delay = new int[taskNum];
    }
}

public class DAG {
    static final int N = 40; // 拓扑节点数量
    static final String IP = "127.0.0.1"; // 各节点的前缀IP
    static final int IP_LENGTH = IP.length(); // IP长度，用于格式化读取拓扑信息
    static final int COMDELAY = 100; // 单位任务的计算时延，单位us
    static final int TRANSDELAY = 100; // 单位数据的传输时延，单位us
    static final int MAX_CPU = 100; // 单个节点计算资源总量
    static final int MAX_STORAGE = 100; // 单个节点存储资源总量
    static final int MAX_SLICE = 100; // 单条链路传输资源总量

    public int[][] matrix;  // 邻接矩阵
    private Status[] statuses;  // 保存每个节点的状态
    private Occupation[] occupation; // 保存每个节点的计算资源状态
    private String[] port; // 保存每个节点的端口号

    static int[] cpu; // 计算资源,单位?
    static int[] storage; // 存储资源,单位?
    static int[][] slice; // 传输资源,以光时片表示,参考ljl论文,OTSS帧长度为10ms,一帧包含100个光时片

    public DAG() {
        matrix = new int[N][N];
        statuses = new Status[N];
        occupation = new Occupation[N];
        port = new String[N];

        cpu = new int[N];
        storage = new int[N];
        slice = new int[N][N];

        initStatuses();
        initOccupation();
    }

//    public static void main(String[] args) throws IOException
//    {
//        DAG dag = new DAG();
//
//        dag.readPort(".\\apps/roadm/app/src/main/java/org/onosproject/roadm/device.json", dag.port);
//        dag.readLink(".\\apps/roadm/app/src/main/java/org/onosproject/roadm/link.json", dag.port, dag.matrix);
//        dag.test(".\\apps/roadm/app/src/main/java/org/onosproject/roadm/input.txt");
//    }

    public void dagStart() throws IOException {
        //DAG dag = new DAG();
//        try {
//            readPort("device.json", port);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        try {
//            readLink("link.json", port, matrix);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        readPort(port);
        readLink(port, matrix);

        try {
            test("input.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 输出读入的业务信息(测试用)
     *
     * @param jobInfo 包含一个业务的所有信息
     * @return void
     */
    public void outputJob(JobInfo jobInfo) {
        System.out.println("任务数：\n" + jobInfo.taskNum);
        System.out.println("原始数据数：\n" + jobInfo.dataNum);

        System.out.println("原始数据信息：");
        for (int i = 0; i < jobInfo.dataNum; i++)
            System.out.println((i + 1) + ": [" + jobInfo.dataPort[i] + "," + jobInfo.dataInfo[i][0] + "," + jobInfo.dataInfo[i][1] + "]");

        System.out.println("资源消耗：");
        for (int i = 0; i < jobInfo.taskNum; i++)
            System.out.println((i + 1) + ": [" + jobInfo.resource[i][0] + "," + jobInfo.resource[i][1] + "]");

        System.out.println("连接关系：");
        for (int i = 0; i < jobInfo.taskNum; i++)
            System.out.println((i + 1) + ": " + jobInfo.link[i]);
    }

    /**
     * 读取业务信息并进行资源融合的路由计算
     *
     * @param path input.txt路径
     * @return void
     */
    public void test(String path) throws IOException {
        // 打开业务信息文件
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader br = new BufferedReader(isr);
        String str = new String();
        Random random = new Random();
        int taskNum = 0;
        int dataNum = 0;

        // 打开输出文件
        File f = new File("output.txt");
        FileOutputStream fos = new FileOutputStream(f);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        BufferedWriter bw = new BufferedWriter(osw);

        ArrayList<String> portList = new ArrayList<>(); // 用于查找端口号对应数组下标
        for (int i = 0; i < N; i++) {
            portList.add(port[i]);
        }

        bw.write("{\"serviceList\":[");

        br.readLine();
        int jobNum = Integer.parseInt(br.readLine()); // 业务数
        // 依次读取业务信息，每读取一条业务进行一次计算
        for (int i = 0; i < jobNum; i++) {
            initOccupation();
            for (int j = 0; j < 3; j++)
                br.readLine();
            taskNum = Integer.parseInt(br.readLine()); // 读取任务数

            br.readLine();
            dataNum = Integer.parseInt(br.readLine()); // 读取原始数据数

            JobInfo jobInfo = new JobInfo(taskNum, dataNum); // 保存业务信息

            // 读取原始数据信息，默认按照序号升序排列
            br.readLine();
            for (int j = 0; j < dataNum; j++) {
                str = br.readLine();

                // 端口号根据配置为5位
                Pattern pattern = Pattern.compile("\\[\\d{5},");
                Matcher m = pattern.matcher(str);
                if (m.find()) {
                    jobInfo.dataPort[j] = str.substring(m.start() + 1, m.end() - 1);
                } else {
                    System.out.println("原始数据信息读取错误！");
                    bw.write("原始数据信息读取错误！");
                    bw.close();
                    br.close();
                    return;
                }

                //任务数受限于N最多2位，该数字为对应任务下标+1
                pattern = Pattern.compile(",\\d{1,2},");
                m = pattern.matcher(str);
                if (m.find()) {
                    jobInfo.dataInfo[j][0] = Integer.parseInt(str.substring(m.start() + 1, m.end() - 1));
                } else {
                    System.out.println("原始数据信息读取错误！");
                    bw.write("原始数据信息读取错误！");
                    bw.close();
                    br.close();
                    return;
                }

                // 存储资源暂定<100
                pattern = Pattern.compile(",\\d{1,3}\\]");
                m = pattern.matcher(str);
                if (m.find()) {
                    jobInfo.dataInfo[j][1] = Integer.parseInt(str.substring(m.start() + 1, m.end() - 1));
                } else {
                    System.out.println("原始数据信息读取错误！");
                    bw.write("原始数据信息读取错误！");
                    bw.close();
                    br.close();
                    return;
                }

                // 判断存储资源是否溢出
                int dataIndex = portList.indexOf(jobInfo.dataPort[j]);
                storage[dataIndex] += jobInfo.dataInfo[j][1];
                if (storage[dataIndex] > MAX_STORAGE) {
                    System.out.println("存储资源溢出，分配失败！");
                    bw.write("存储资源溢出，分配失败！");
                    bw.close();
                    br.close();
                    return;
                }

            } // 读取原始数据信息，默认按照序号升序排列

            // 读取资源消耗信息，默认按照序号升序排列
            br.readLine();
            for (int j = 0; j < taskNum; j++) {
                str = br.readLine();
                Pattern pattern = Pattern.compile("\\[\\d{1,3},"); // 计算资源暂定<100
                Matcher m = pattern.matcher(str);
                if (m.find()) {
                    jobInfo.resource[j][0] = Integer.parseInt(str.substring(m.start() + 1, m.end() - 1));
                    if (jobInfo.resource[j][0] > MAX_CPU) {
                        System.out.println("计算资源溢出，分配失败！");
                        bw.write("计算资源溢出，分配失败！");
                        bw.close();
                        br.close();
                        return;
                    }
                }

                pattern = Pattern.compile(",\\d{1,3}\\]"); // 传输资源暂定<100
                m = pattern.matcher(str);
                if (m.find()) {
                    jobInfo.resource[j][1] = Integer.parseInt(str.substring(m.start() + 1, m.end() - 1));
                    if (jobInfo.resource[j][1] > MAX_SLICE) {
                        System.out.println("传输资源溢出，分配失败！");
                        bw.write("传输资源溢出，分配失败！");
                        bw.close();
                        br.close();
                        return;
                    }
                }
            } // 读取资源消耗信息，默认按照序号升序排列

            // 读取任务连接关系，默认按照序号升序排列
            br.readLine();
            for (int j = 0; j < taskNum; j++) {
                str = br.readLine();
                if (str.length() < 4) {
                    jobInfo.link[j] = null;
                } else {
                    jobInfo.link[j] = str.substring(3, str.length());
                }
            } // 读取任务连接关系，默认按照序号升序排列

            List<Integer> preList[] = new ArrayList[taskNum]; // 保存每一个任务的所有前驱任务
            List<Integer> preListTemp[] = new ArrayList[taskNum]; // 用于去重的中间list数组
            for (int j = 0; j < taskNum; j++) // list数组使用前的初始化
            {
                preList[j] = new ArrayList<>();
                preListTemp[j] = new ArrayList<>();
            }
            // 将任务按顺序加入队列，并得到每个任务的前驱任务
            Queue<Integer> queue = new LinkedList<>();
            Queue<Integer> queueTemp = new LinkedList<>();
            queue.offer(0);
            queueUp(jobInfo, queueTemp, queue, 0, preListTemp);

            // list去重
            int routeNum = 0; // 存储路由总数
            for (int j = 1; j < taskNum; j++) {
                LinkedHashSet<Integer> hashSet = new LinkedHashSet<>(preListTemp[j]);
                preList[j] = new ArrayList<>(hashSet);
                routeNum += preList[j].size();
            }

            System.out.println("业务" + (i + 1));
            System.out.println("共" + (dataNum + routeNum) + "条路由");

            bw.write("{\"tasks_routing\":[");

            // 从task[0]开始确定节点和路由
            int[] taskNode = new int[taskNum]; // 保存每个任务对应的节点号
            Status[] taskStatus = new Status[taskNum]; // 保存每个任务是否已被分配，处理队列中的重复任务
            for (int j = 0; j < taskNum; j++) {
                taskStatus[j] = Status.UNDISCOVERD;
            }

            while (queue.peek() != null) {
                int taskIndex = queue.poll();
                if (taskStatus[taskIndex] == Status.UNDISCOVERD) {
                    if (taskIndex == 0) {
                        // 选取内环上的点分配task0
                        taskNode[taskIndex] = random.nextInt(10);
                        occupation[taskNode[taskIndex]] = Occupation.OCCUPIED;
                        jobInfo.delay[taskIndex] = COMDELAY;
                    } else {
                        List<Integer> nodesInPath = new ArrayList<Integer>();
                        int weight = DijkstraPath(taskNode[preList[taskIndex].get(0)], taskNode, taskIndex, nodesInPath, jobInfo.resource[preList[taskIndex].get(0)][1], jobInfo.resource[taskIndex][0]);
                        occupation[taskNode[taskIndex]] = Occupation.OCCUPIED;

                        System.out.print("task" + (preList[taskIndex].get(0) + 1) + "->task" + (taskIndex + 1) + ": " + port[taskNode[preList[taskIndex].get(0)]] + "-");
                        bw.write("\"task" + (preList[taskIndex].get(0) + 1) + "->task" + (taskIndex + 1) + ": " + port[taskNode[preList[taskIndex].get(0)]] + "-");
                        for (int j : nodesInPath) {
                            System.out.print(port[j] + "-");
                            bw.write(port[j] + "-");
                        }
                        System.out.println(port[taskNode[taskIndex]]);
                        bw.write(port[taskNode[taskIndex]] + "\",");
                        bw.newLine();

                        int maxDelay = jobInfo.delay[preList[taskIndex].get(0)] + weight * TRANSDELAY + COMDELAY;

                        // 计算其它前驱到当前task的路由
                        for (int j = 1; j < preList[taskIndex].size(); j++) {
                            List<Integer> nodesList = new ArrayList<Integer>();
                            weight = DijkstraPath(taskNode[preList[taskIndex].get(j)], taskNode[taskIndex], nodesList, jobInfo.resource[preList[taskIndex].get(j)][1]);
                            int delayTemp = jobInfo.delay[preList[taskIndex].get(j)] + weight * TRANSDELAY + COMDELAY;
                            if (delayTemp > maxDelay) {
                                maxDelay = delayTemp;
                            }

                            System.out.print("task" + (preList[taskIndex].get(j) + 1) + "->task" + (taskIndex + 1) + ": " + port[taskNode[preList[taskIndex].get(j)]] + "-");
                            bw.write("\"task" + (preList[taskIndex].get(j) + 1) + "->task" + (taskIndex + 1) + ": " + port[taskNode[preList[taskIndex].get(j)]] + "-");
                            for (int k : nodesList) {
                                System.out.print(port[k] + "-");
                                bw.write(port[j] + "-");
                            }
                            System.out.println(port[taskNode[taskIndex]]);
                            bw.write(port[taskNode[taskIndex]] + "\",");
                            bw.newLine();
                        }
                        jobInfo.delay[taskIndex] = maxDelay;
                    }
                    taskStatus[taskIndex] = Status.VISITED;
                }
            }

            // 计算每个原始数据到目标任务节点的路由
            int comTransDelay = 0;
            for (int j = 0; j < dataNum; j++) {
                if (jobInfo.dataPort[j].equals(port[taskNode[jobInfo.dataInfo[j][0] - 1]])) {
                    System.out.println("data" + (j + 1) + "->task" + jobInfo.dataInfo[j][0] + ": " + jobInfo.dataPort[j] + "-" + port[taskNode[jobInfo.dataInfo[j][0] - 1]]);
                    bw.write("\"data" + (j + 1) + "->task" + jobInfo.dataInfo[j][0] + ": " + jobInfo.dataPort[j] + "-" + port[taskNode[jobInfo.dataInfo[j][0] - 1]] + "\"");
                } else {
                    int dataIndex = portList.indexOf(jobInfo.dataPort[j]);
                    List<Integer> nodesList = new ArrayList<Integer>();
                    int weight = DijkstraPath(dataIndex, taskNode[jobInfo.dataInfo[j][0] - 1], nodesList);
                    int delayTemp = weight * TRANSDELAY;
                    if (delayTemp > comTransDelay) {
                        comTransDelay = delayTemp;
                    }

                    System.out.print("data" + (j + 1) + "->task" + jobInfo.dataInfo[j][0] + ": " + port[dataIndex] + "-");
                    bw.write("\"data" + (j + 1) + "->task" + jobInfo.dataInfo[j][0] + ": " + port[dataIndex] + "-");
                    for (int m : nodesList) {
                        System.out.print(port[m] + "-");
                        bw.write(port[m] + "-");
                    }
                    System.out.println(port[taskNode[jobInfo.dataInfo[j][0] - 1]]);
                    bw.write(port[taskNode[jobInfo.dataInfo[j][0] - 1]] + "\"");
                }
                if (j == dataNum - 1) {
                    bw.write("],");
                    bw.newLine();
                } else {
                    bw.write(",");
                    bw.newLine();
                }
            }

            int totalDelay = comTransDelay + jobInfo.delay[taskNum - 1];
            System.out.println("完成时长：" + totalDelay + "us");
            System.out.println();
            bw.write("\"finish_time\":\"" + totalDelay + "us\"}");
            if (i == jobNum - 1) {
                bw.write("]}");
            } else {
                bw.write(",");
                bw.newLine();
            }
        }

        bw.close();
        br.close();
    }

    /**
     * 将DAG中的各task按照先后顺序加入队列。有重复，使用时需要判断是否已计算过！
     *
     * @param jobInfo   业务信息
     * @param queueTemp 临时队列，用于辅助队列生成
     * @param queue     结果队列
     * @param task      寻找该task的后继任务，并加入队列
     * @param preList   保存每个节点的前驱节点
     * @return void
     */
    public void queueUp(JobInfo jobInfo, Queue<Integer> queueTemp, Queue<Integer> queue, int task, List<Integer> preList[]) {
        if (jobInfo.link[task] != null) {
            String[] str = jobInfo.link[task].split(",");
            for (String s : str) {
                int nextTask = Integer.parseInt(s) - 1;
                queue.offer(nextTask);
                queueTemp.offer(nextTask);
                preList[nextTask].add(task);
            }
        }
        if (queueTemp.peek() != null) {
            int next = queueTemp.poll();
            queueUp(jobInfo, queueTemp, queue, next, preList);
        }
    }

    /**
     * 初始化状态数组
     *
     * @return void
     */
    public void initStatuses() {
        for (int i = 0; i < N; i++) {
            statuses[i] = Status.UNDISCOVERD;
        }
    }

    /**
     * 初始化Occupation数组
     *
     * @return void
     */
    public void initOccupation() {
        for (int i = 0; i < N; i++) {
            occupation[i] = Occupation.UNOCCUPIED;
        }
    }

    /**
     * 检查是否全部被遍历(只要有一个是未被遍历返回false)
     *
     * @return boolean
     */
    private boolean isAllVisited() {
        for (Status status : statuses) {
            if (status == Status.UNDISCOVERD) {
                return false;
            }
        }
        return true;
    }

    /**
     * 找到undiscovered中最小weight的索引
     *
     * @param nums 整型数组, 存放weight
     * @return int
     */
    private int indexOfMin(int[] nums) {
        List<Integer> remain = new ArrayList<Integer>();
        for (int i = 0; i < N; i++) {
            if (statuses[i] == Status.UNDISCOVERD) {
                remain.add(i);
            }
        }
        if (remain.size() == 0) {
            return 0;  // 这里返回什么都行, 因为所有节点会在下一循环全部设置为已访问, 从而循环内无任何操作
        }
        int minIndex = remain.get(0);
        for (int j : remain) {
            if (nums[j] < nums[minIndex]) {
                minIndex = j;
            }
        }
        return minIndex;
    }

    /**
     * 找到数组中距离index最近、未被分配task、且计算和传输资源均满足需求的节点索引
     * 并记录下所消耗的相应资源
     *
     * @param prevs 每个节点的前驱节点
     * @param nums  每个节点的weight
     * @param index 起点
     *              //     * @param trans 所需传输资源
     * @param comp  所需计算资源
     * @return int
     */
    private int getMinIndex(int[] prevs, int[] nums, int index, int comp) {
        int minIndex = -1;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < N; i++) {
            if (nums[i] < min && occupation[i] == Occupation.UNOCCUPIED && cpu[i] + comp <= MAX_CPU) {
                minIndex = i;
                min = nums[i];
            }
        }
        return minIndex;
    }

    /**
     * 最短路径-Dijkstra算法(找出某个点到其他所有点的最短路径)
     *
     * @param index       起点的节点序号
     * @param taskNodes   用于存放计算得到的下一个task对应的节点序号
     * @param taskIndex   下一个task序号
     * @param nodesInPath 用于存放最短路径，不包含起点和终点
     * @param trans       前驱任务所需传输资源
     * @param comp        当前任务所需计算资源
     * @return int 路径权值总和
     */
    public int DijkstraPath(int index, int[] taskNodes, int taskIndex, List<Integer> nodesInPath, int trans, int comp) {
        // 每一轮选出的路径权值最小的节点, 则不可能再找出另外的路径权值更小
        // 比如从A到D是2, 则这一轮取出D节点, 假如有A能通过另外的节点到达D并且更短,
        // 比如A-1-E-1-D, 则上一轮取出的节点将是E而不是D
        initStatuses();
        // 数组存放该点到各个点的路径权值
        int[] weights = new int[N];
        // 将每个默认权值设置为整型最大值
        for (int i = 0; i < N; i++) {
            weights[i] = Integer.MAX_VALUE;
        }
        // 数组记录指定节点到每个节点的最短路径中, 终点节点的前驱节点
        // 动态规划: 找到到达某个节点的最短路径, 先找到到达他的上一个节点的最短路径
        int[] prevs = new int[N];
        prevs[index] = -1;  // 负数表示该点没有前驱
        // 循环所用的辅助索引
        int from = index;
        // 只要不是全部被遍历
        while (!isAllVisited()) {
            // 将这个节点设置为已访问
            statuses[from] = Status.VISITED;
            // 查看邻接矩阵中与指定节点邻接的节点
            for (int i = 0; i < N; i++) {
                // 可能的新路径权值: 从最开始的指定起点到本轮起点到该节点的路径权值总和
                int newWeight;
                if (weights[from] == Integer.MAX_VALUE) {
                    newWeight = matrix[from][i];
                } else {
                    newWeight = weights[from] + matrix[from][i];
                }

                // 如果节点未访问, 且是邻接节点, 且小于weights中记录的该节点原来的路径权值
                if (statuses[i] == Status.UNDISCOVERD && matrix[from][i] > 0 && newWeight < weights[i] && slice[from][i] + trans <= MAX_SLICE) {
                    // 则更新该节点的最小路径值, 更新该节点的前驱为本轮起点
                    weights[i] = newWeight;
                    prevs[i] = from;
                }
            }
            // 下轮起点from设置为: weights数组中数值最小的并且未访问的节点
            from = indexOfMin(weights);
        }

        // 得到结果
        int nextPort = getMinIndex(prevs, weights, index, comp);
        occupation[nextPort] = Occupation.OCCUPIED;
        allPrevs(prevs, nextPort, nodesInPath);
        taskNodes[taskIndex] = nextPort;

        // 更新已消耗的计算和传输资源
        cpu[nextPort] += comp;
        int prev = nextPort;
        while (prevs[prev] != -1) {
            slice[prevs[prev]][prev] += trans;
            slice[prev][prevs[prev]] += trans;
            prev = prevs[prev];
        }

        return weights[nextPort];
    }

    /**
     * 某确定节点的task至其剩余前驱的路由计算
     *
     * @param index       前驱节点序号
     * @param taskNode    当前任务所在节点序号
     * @param nodesInPath 存放路由的链表
     * @param trans       前驱任务所需传输资源
     * @return int 路径权值总和
     */
    public int DijkstraPath(int index, int taskNode, List<Integer> nodesInPath, int trans) {
        // 每一轮选出的路径权值最小的节点, 则不可能再找出另外的路径权值更小
        // 比如从A到D是2, 则这一轮取出D节点, 假如有A能通过另外的节点到达D并且更短,
        // 比如A-1-E-1-D, 则上一轮取出的节点将是E而不是D
        initStatuses();
        // 数组存放该点到各个点的路径权值
        int[] weights = new int[N];
        // 将每个默认权值设置为整型最大值
        for (int i = 0; i < N; i++) {
            weights[i] = Integer.MAX_VALUE;
        }
        // 数组记录指定节点到每个节点的最短路径中, 终点节点的前驱节点
        // 动态规划: 找到到达某个节点的最短路径, 先找到到达他的上一个节点的最短路径
        int[] prevs = new int[N];
        prevs[index] = -1;  // 负数表示该点没有前驱
        // 循环所用的辅助索引
        int from = index;
        // 只要不是全部被遍历
        while (!isAllVisited()) {
            // 将这个节点设置为已访问
            statuses[from] = Status.VISITED;
            // 查看邻接矩阵中与指定节点邻接的节点
            for (int i = 0; i < N; i++) {
                // 可能的新路径权值: 从最开始的指定起点到本轮起点到该节点的路径权值总和
                int newWeight;
                if (weights[from] == Integer.MAX_VALUE) {
                    newWeight = matrix[from][i];
                } else {
                    newWeight = weights[from] + matrix[from][i];
                }
                // 如果节点未访问, 且是邻接节点
                if (statuses[i] == Status.UNDISCOVERD && matrix[from][i] > 0 && newWeight < weights[i] && slice[from][i] + trans <= MAX_SLICE) {
                    // 则更新该节点的最小路径值, 更新该节点的前驱为本轮起点
                    weights[i] = newWeight;
                    prevs[i] = from;
                }
            }
            // 下轮起点from设置为: weights数组中数值最小的并且未访问的节点
            from = indexOfMin(weights);
        }

        // 得到结果
        allPrevs(prevs, taskNode, nodesInPath);

        // 更新已消耗的传输资源
        int prev = taskNode;
        while (prevs[prev] != -1) {
            slice[prevs[prev]][prev] += trans;
            slice[prev][prevs[prev]] += trans;
            prev = prevs[prev];
        }
        return weights[taskNode];
    }

    /**
     * 原始数据的路由计算
     *
     * @param index       原始数据所在节点序号
     * @param taskNode    对应任务所在节点序号
     * @param nodesInPath 存放路由的链表
     * @return int 路径权值总和
     */
    public int DijkstraPath(int index, int taskNode, List<Integer> nodesInPath) {
        // 每一轮选出的路径权值最小的节点, 则不可能再找出另外的路径权值更小
        // 比如从A到D是2, 则这一轮取出D节点, 假如有A能通过另外的节点到达D并且更短,
        // 比如A-1-E-1-D, 则上一轮取出的节点将是E而不是D
        initStatuses();
        // 数组存放该点到各个点的路径权值
        int[] weights = new int[N];
        // 将每个默认权值设置为整型最大值
        for (int i = 0; i < N; i++) {
            weights[i] = Integer.MAX_VALUE;
        }
        // 数组记录指定节点到每个节点的最短路径中, 终点节点的前驱节点
        // 动态规划: 找到到达某个节点的最短路径, 先找到到达他的上一个节点的最短路径
        int[] prevs = new int[N];
        prevs[index] = -1;  // 负数表示该点没有前驱
        // 循环所用的辅助索引
        int from = index;
        // 只要不是全部被遍历
        while (!isAllVisited()) {
            // 将这个节点设置为已访问
            statuses[from] = Status.VISITED;
            // 查看邻接矩阵中与指定节点邻接的节点
            for (int i = 0; i < N; i++) {
                // 可能的新路径权值: 从最开始的指定起点到本轮起点到该节点的路径权值总和
                int newWeight;
                if (weights[from] == Integer.MAX_VALUE) {
                    newWeight = matrix[from][i];
                } else {
                    newWeight = weights[from] + matrix[from][i];
                }
                // 如果节点未访问, 且是邻接节点
                if (statuses[i] == Status.UNDISCOVERD && matrix[from][i] > 0
                        // 并且如果小于weights中记录的该节点原来的路径权值
                        && newWeight < weights[i]) {
                    // 则更新该节点的最小路径值, 更新该节点的前驱为本轮起点
                    weights[i] = newWeight;
                    prevs[i] = from;
                }
            }
            // 下轮起点from设置为: weights数组中数值最小的并且未访问的节点
            from = indexOfMin(weights);
        }

        // 输出结果
        allPrevs(prevs, taskNode, nodesInPath);
        return weights[taskNode];
    }

    /**
     * 指定节点, 按路径顺序返回该节点的所有前驱节点
     * 不包含起点
     *
     * @param prevs  记录前驱节点的数组
     * @param index  指定节点
     * @param result 存放结果
     */
    private void allPrevs(int[] prevs, int index, List<Integer> result) {
        // 记录指定节点到达指定起点的最短路径沿途的节点
        Stack<Integer> prevStack = new Stack<Integer>();
        int prev = prevs[index];
        // 前面设置的算法最开始指定的起点的前驱索引为-1在这里起作用
        // 只要前驱的前驱索引不为最开始指定的起点
        while (prevs[prev] != -1) {
            // 把前驱索引加入栈
            prevStack.add(prev);
            // 下次循环要检查此次循环前驱节点的前驱节点, 所以更新变量
            prev = prevs[prev];
        }

        // 方便遍历, 倒序输出
        while (!prevStack.isEmpty()) {
            result.add(prevStack.pop());
        }
    }

    /**
     * 本方法用于初始化端口号信息，端口号与port数组索引号一一对应
     *
     //* @param path 存储设备信息的json文件地址
     * @param port 存储端口信息的String数组
     * @throws IOException
     */
    //public void readPort(String path, String[] port) throws IOException {
    public void readPort(String[] port) {
        //File file = new File(path);
        //String jsonString = new String(Files.readAllBytes(Paths.get(file.getPath())));
        //String jsonString = new String(Files.readAllBytes(Path.of(path)));
        String jsonString = Device.device;
        Pattern pattern = Pattern.compile("\"port\" : \"\\d{5}\""); //字符串匹配，寻找端口号
        Matcher m = pattern.matcher(jsonString);

        int start_index = 0;
        int i = 0;
        while (m.find()) {
            start_index = m.start();
            port[i] = jsonString.substring(start_index + 10, start_index + 15);
            i++;
        }
    }

    /**
     * 本方法通过读取json文件配置邻接关系矩阵，matrix的行/列索引与port数组索引对应的端口号一致
     * 当前默认每个连接的权值为1
     *
     //* @param path   存储连接信息的json文件地址
     * @param port   存储端口信息的String数组
     * @param matrix 邻接关系矩阵
     * @throws IOException
     */
    //public void readLink(String path, String[] port, int[][] matrix) throws IOException {
    public void readLink(String[] port, int[][] matrix) {
        ArrayList<String> portList = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            portList.add(port[i]);
        }
        //File file = new File(path);
        //String jsonString = new String(Files.readAllBytes(Paths.get(file.getPath())));
        //String jsonString = new String(Files.readAllBytes(Path.of(path)));
        String jsonString = Link.link;
        Pattern pattern = Pattern.compile(IP + ":\\d{5}"); //字符串匹配，寻找端口号
        Matcher m = pattern.matcher(jsonString);

        int start_index = 0;
        int flag = 0;
        int p1 = -1;
        int p2 = -1;
        while (m.find()) {
            start_index = m.start();

            String temp = jsonString.substring(start_index + IP_LENGTH + 1, start_index + IP_LENGTH + 6);
            if (flag % 4 == 0) {
                p1 = portList.indexOf(temp);
            } else if (flag % 4 == 3) {
                p2 = portList.indexOf(temp);
                matrix[p1][p2] = 1;
                matrix[p2][p1] = 1;
                p1 = -1;
                p2 = -1;
            }
            flag++;
        }
    }
}
