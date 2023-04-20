package org.onosproject.roadm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InputUtils {
//        public String input = "业务数：\n" +
//                "2\n" +
//                "\n" +
//                "业务1\n" +
//                "任务数：\n" +
//                "7\n" +
//                "原始数据数：\n" +
//                "1\n" +
//                "原始数据信息：\n" +
//                "1: [11002,2,1]\n" +
//                "资源消耗：\n" +
//                "1: [1,1]\n" +
//                "2: [1,1]\n" +
//                "3: [1,1]\n" +
//                "4: [1,1]\n" +
//                "5: [1,1]\n" +
//                "6: [1,1]\n" +
//                "7: [1,1]\n" +
//                "连接关系：\n" +
//                "1: 2,3\n" +
//                "2: 4,5\n" +
//                "3: 5,6\n" +
//                "4: 7\n" +
//                "5: 7\n" +
//                "6: 7\n" +
//                "7:\n" +
//                "\n" +
//                "业务2\n" +
//                "任务数：\n" +
//                "4\n" +
//                "原始数据数：\n" +
//                "2\n" +
//                "原始数据信息：\n" +
//                "1: [11001,1,1]\n" +
//                "2: [11091,3,1]\n" +
//                "资源消耗：\n" +
//                "1: [1,1]\n" +
//                "2: [1,1]\n" +
//                "3: [1,1]\n" +
//                "4: [1,1]\n" +
//                "连接关系：\n" +
//                "1: 2,3\n" +
//                "2: 4\n" +
//                "3: 4\n" +
//                "4:";
        private final Logger log = LoggerFactory.getLogger(getClass());
        public void writeToFile (String input) {

                try {
                        FileWriter fileWriter = new FileWriter("input.txt");
                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                        bufferedWriter.write(input);
                        bufferedWriter.close();
                        //System.out.println("Content has been written to the file.");
                } catch (IOException e) {
                        //System.out.println("An error occurred while writing to the file.");
                        e.printStackTrace();
                }
                log.info("写入input.txt文件成功");
        }

}

