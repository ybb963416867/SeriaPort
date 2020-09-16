package com.example.seriaport;

import top.keepempty.sph.library.DataConversion;
import top.keepempty.sph.library.SerialPortConfig;
import top.keepempty.sph.library.SerialPortHelper;
import top.keepempty.sph.library.SphCmdEntity;
import top.keepempty.sph.library.SphResultCallback;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static String TAG = "MainActivity2";

    private SerialPortHelper serialPortHelper;
    private boolean isOpen;
    private TextView viewById;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewById = findViewById(R.id.tv1);

        openSerialPort();

        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] bytes = new byte[4];
                bytes[0] = 0x07;
                bytes[1] = 0x07;
                bytes[2] = (byte) 0xaa;
                bytes[3] = 0x55;
                serialPortHelper.addCommands(bytes);
            }
        });
    }


    /**
     * 打开串口
     */
    private void openSerialPort() {

        /**
         * 串口参数
         */
        SerialPortConfig serialPortConfig = new SerialPortConfig();
        serialPortConfig.mode = 0;
        serialPortConfig.path = "/dev/ttyXRUSB1";
        serialPortConfig.baudRate = 9600;
//        serialPortConfig.dataBits = 8;
//        serialPortConfig.parity   = ;
//        serialPortConfig.stopBits = stopBits;


        // 初始化串口
        serialPortHelper = new SerialPortHelper(16);
        // 设置串口参数
        serialPortHelper.setConfigInfo(serialPortConfig);
        // 开启串口
        isOpen = serialPortHelper.openDevice();
        if (!isOpen) {
            Toast.makeText(this, "串口打开失败！", Toast.LENGTH_LONG).show();
            return;
        } else {
            Toast.makeText(this, "串口打开成功！", Toast.LENGTH_LONG).show();
            isOpen = true;
        }
        serialPortHelper.setSphResultCallback(new SphResultCallback() {
            @Override
            public void onSendData(SphCmdEntity sendCom) {
                Log.d(TAG, "发送命令：" + sendCom.commandsHex);
            }

            @Override
            public void onReceiveData(SphCmdEntity data) {

                Log.d(TAG, "收到命令：" + data.commandsHex);
                byte[] bytes = data.commands;
//                for (byte aByte : bytes) {
//                    Log.e(TAG+"整型",String.valueOf(aByte)+"");
//                }

                if (bytes[1] == 00) {
                    Toast.makeText(MainActivity.this, "成功", Toast.LENGTH_SHORT).show();
                }


                byte[] bytes2 = new byte[2];
                bytes2[0] = bytes[3];
                bytes2[1] = bytes[2];
                int i = DataConversion.bytesToDec(bytes2);

                StringBuffer stringBuffer = new StringBuffer(i+"");
                stringBuffer.insert(2, ".");
                stringBuffer.append("℃");
                Log.e(TAG, "stringBuffervalue:" + stringBuffer.toString());


                byte[] bytes1 = new byte[2];
                bytes1[0] = bytes[5];
                bytes1[1] = bytes[4];

                int i1 = DataConversion.bytesToDec(bytes1);

                StringBuffer stringBuffer1 = new StringBuffer(i1+"");
                stringBuffer1.insert(2, ".");
                stringBuffer1.append("℃");
                Log.e(TAG, "stringBuffervalue1:" + stringBuffer.toString());


                viewById.setText("人体温度为：" + stringBuffer.toString() + "\n环境温度" + stringBuffer1.toString()+"");


            }

            @Override
            public void onComplete() {
                Log.d(TAG, "完成");
            }
        });
    }
}