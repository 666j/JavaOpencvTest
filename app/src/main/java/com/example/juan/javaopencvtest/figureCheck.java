package com.example.juan.javaopencvtest;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
        import android.os.Handler;
        import android.os.Message;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.WindowManager;
        import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

        import org.opencv.android.CameraBridgeViewBase;
        import org.opencv.android.OpenCVLoader;
        import org.opencv.core.Core;
        import org.opencv.core.CvType;
        import org.opencv.core.Mat;
        import org.opencv.core.MatOfInt;
        import org.opencv.core.MatOfInt4;
        import org.opencv.core.MatOfPoint;
        import org.opencv.core.Point;
        import org.opencv.core.Size;
        import org.opencv.imgproc.Imgproc;
import org.w3c.dom.Text;

import java.util.ArrayList;
        import java.util.List;
        import java.util.Vector;


public class figureCheck extends Activity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnClickListener {

    private static final String TAG = "OpenCameraActivity";
    private LinearLayout show_figure_btn;

    static {
        OpenCVLoader.initDebug();
    }
    private int num=0;
    private int mylastshownum=0;
    private Mat mRgba;
    private Mat mFlipRgba;//
    private Mat mTransposeRgba;//转换之后的RGBA
    private Handler handler;
    private EditText figure_num;
    private Button return_edit;
    private LinearLayout show_hcitest3;
    private CameraBridgeViewBase mOpenCvCameraView;

    public figureCheck() {

        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_figure_check);
        show_figure_btn= (LinearLayout) findViewById(R.id.show_figure_btn);
        figure_num = (EditText)findViewById(R.id.figure_num);

        return_edit = (Button)findViewById(R.id.return_edit);

        show_hcitest3 = (LinearLayout)findViewById(R.id.show_hcitest3);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.enableView();//
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_BACK);//前置摄像头 CAMERA_ID_BACK为后置摄像头
        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                Log.d("testcont",num+"");

                return false;
            }
        });
        initListener();

    }

    private void initListener(){
        show_hcitest3.setOnClickListener(this);
        return_edit.setOnClickListener(this);
        show_figure_btn.setOnClickListener(this);
    }
    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onDestroy() {
        super.onDestroy();
        mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat();
        mFlipRgba = new Mat();
        mTransposeRgba = new Mat();

    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();//为当前照相机原图
        ArrayList<MatOfPoint> contours=new ArrayList<MatOfPoint>();
        //检测的轮廓数组，每一个轮廓用一个MatOfPoint类型的List表示
        Core.flip(mRgba, mFlipRgba,1);//

        Core.flip(mFlipRgba, mFlipRgba, 1);
        Core.flip(mFlipRgba, mFlipRgba, 0);

        Core.transpose(mFlipRgba, mTransposeRgba);
        Imgproc.resize(mTransposeRgba,mFlipRgba,mFlipRgba.size() , 0.0D, 0.0D, 0);
        Imgproc.cvtColor(mFlipRgba, mRgba, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(mRgba, mRgba, 100, 255, Imgproc.THRESH_BINARY);
        //阈值处理，用于肤色检测
        Imgproc.cvtColor(mRgba, mFlipRgba, Imgproc.COLOR_GRAY2RGBA, 4);

        Mat hierarchy = new Mat();
        hierarchy.convertTo(hierarchy,CvType.CV_32SC1);
        //定义轮廓抽取模式
        int mode = Imgproc.RETR_EXTERNAL;
        //定义轮廓识别方法
        int method = Imgproc.CHAIN_APPROX_NONE;//存储所有的轮廓点，相邻的两个点的像素位置差不超过1

        Imgproc.findContours(mRgba, contours,hierarchy,mode,method);
        num=contours.size();
        double x=0;
        double y=0;
        for (int k=0; k < contours.size(); k++){
            Point[] ap=contours.get(k).toArray();
            x=x+ap[0].x;
            y=y+ap[0].y;
        }
        Point cenPoint=new Point(x/contours.size(),y/contours.size());

        ArrayList<MatOfInt> hull=new ArrayList<MatOfInt>();
        ArrayList<MatOfInt4> dis=new ArrayList<MatOfInt4>();
        mylastshownum=0;
        for (int k=0; k < contours.size(); k++){
            MatOfInt matint=new MatOfInt();
            Imgproc.convexHull(contours.get(k), matint,true);//找到所有的凸包
            hull.add(matint);
        }
        for (int k=0; k < contours.size(); k++){
            try {
                MatOfInt4 matint4=new MatOfInt4();
                //用于存储，Vec4i存储了起始点（startPoint），
                // 结束点(endPoint)，距离convexity hull最远点(farPoint)以及
                // 最远点到convexity hull的距离(depth)
                Imgproc.convexityDefects(contours.get(k), hull.get(k),matint4);
                //输入检测到的轮廓，凸包
                List<Integer> cdList = matint4.toList();
                //转换到一个整数列表中
                for (int i=0;i<cdList.size();i++){
                    if (i%4==3&&cdList.get(i)>40500){
                        //每四个连续的元素有一个是depth，检测depth，
                        mylastshownum++;
                    }
                }
            }catch (Exception e){

            }
        }
        num=dis.size()+1;
        handler.sendEmptyMessage(mylastshownum);
        return mFlipRgba;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.show_figure_btn:
                if (mylastshownum==0){
                    mylastshownum++;
                }
                figure_num.setText(""+mylastshownum);
                break;
            case R.id.return_edit:
                Intent intent = new Intent(figureCheck.this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.show_hcitest3:
                Intent intent1 = new Intent(figureCheck.this, HciTest3Activity.class);
                startActivity(intent1);
                break;
        }
    }
}
