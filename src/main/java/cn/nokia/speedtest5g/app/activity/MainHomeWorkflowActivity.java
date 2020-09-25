package cn.nokia.speedtest5g.app.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;

import com.android.volley.util.JsonHandler;
import com.android.volley.util.MarqueesTextView;
import com.android.volley.util.SharedPreHandler;

import cn.nokia.speedtest5g.R;
import cn.nokia.speedtest5g.app.SpeedTest5g;
import cn.nokia.speedtest5g.app.TypeKey;
import cn.nokia.speedtest5g.app.adapter.FunctionAdapter;
import cn.nokia.speedtest5g.app.enums.EnumRequest;
import cn.nokia.speedtest5g.app.enums.ModeEnum;
import cn.nokia.speedtest5g.app.enums.MyEvents;
import cn.nokia.speedtest5g.app.gridview.util.Item;
import cn.nokia.speedtest5g.app.request.BaseRequest;
import cn.nokia.speedtest5g.app.thread.MyLoginExitThread;
import cn.nokia.speedtest5g.app.uitl.ModeClickHandler;
import cn.nokia.speedtest5g.app.uitl.MyToSpile;
import cn.nokia.speedtest5g.app.uitl.NetWorkUtilNow;
import cn.nokia.speedtest5g.app.uitl.WaterMarkUtil;
import cn.nokia.speedtest5g.notify.response.ResponseSystemUnreadMsg;
import cn.nokia.speedtest5g.notify.ui.NotifyDetailActivity;
import cn.nokia.speedtest5g.view.MyScrollyGridView;

/**
 * 主页-作业流程（首页）
 * @author zwq
 *
 */
public class MainHomeWorkflowActivity extends BaseActionBarHandlerActivity implements OnClickListener {

    private View mViewScroll = null;
    //优化工具-便民工具 控件适配器
    private FunctionAdapter mAdapter_1;//基础服务
    //优化工具-专用工具 控件适配器
    private FunctionAdapter mAdapter_2;//业务测试
    //优化工具-三方应用 控件适配器
    private FunctionAdapter mAdapter_3;//wifi工具
    //优化工具-三方应用 控件适配器
    private FunctionAdapter mAdapter_4;//专业工具

    private LinearLayout mLl_1 = null; //基础服务
    private LinearLayout mLl_2 = null; //业务测试
    private LinearLayout mLl_3 = null; //wifi工具
    private LinearLayout mLl_4 = null; //专业工具

    //无功能模块数据时提示语
    private View mViewNotModeData;
    //当前操作的模块对象
    private Item mClickItem;
    // 未读消息
    private MarqueesTextView tvUnreadMsg;
    private ResponseSystemUnreadMsg systemUnreadMsg;
    private int typeInitFirst = EnumRequest.OTHER_MAINHOME_TYPE_WORKFLOW.toInt();
    private UpdateConfigReceiver mReceiver = new UpdateConfigReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainhome_workflow);
        ModeClickHandler.getInstances(this);

        registerReceiver(workflowCast, new IntentFilter(TypeKey.getInstance().ACTION_MAINHOME_WORKFLOW));
        registerReceiver(mReceiver, new IntentFilter(TypeKey.getInstance().ACTION_MAINHOME_SUPER));

        new Thread(new Runnable() {

            @Override
            public void run() {
                ModeClickHandler.getInstances(MainHomeWorkflowActivity.this).initModularAllData();
                sendMessage(new MyEvents(ModeEnum.TASK,EnumRequest.OTHER_INIT.toInt()));
            }
        }).start();
    }

    /**
     * 获取状态栏高度
     * @return
     */
    public int getStatusBarHeightOne() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int height = getResources().getDimensionPixelSize(resourceId);
        if (height == 0) {
            return (int) Math.ceil(20 * getResources().getDisplayMetrics().density);
        }
        return height;
    }

    @Override
    public void init(Object titleId, boolean isBack) {
        super.init(titleId, isBack);
        //		3.1.4版开始屏蔽
        //		actionBar.addAction(new MyActionBar.MenuAction(R.drawable.icon_search, EnumRequest.MENU_SELECT_ONE.toInt(), ""));

        //		mGridViewYhgj 		 = (GridView) findViewById(R.id.mainhome_workflow_gridview_yhgj);
        mViewScroll  = findViewById(R.id.mainhome_workflow_scrollView);
        mViewNotModeData     = findViewById(R.id.mainhome_workflow_tv_notModeData);

        mLl_1 = (LinearLayout)findViewById(R.id.mainhome_workflow_ll_1);
        mLl_2 = (LinearLayout)findViewById(R.id.mainhome_workflow_ll_2);
        mLl_3 = (LinearLayout)findViewById(R.id.mainhome_workflow_ll_3);
        mLl_4 = (LinearLayout)findViewById(R.id.mainhome_workflow_ll_4);

        MyScrollyGridView mGridView_1  = (MyScrollyGridView) findViewById(R.id.mainhome_workflow_gridview_1);
        MyScrollyGridView mGridView_2   = (MyScrollyGridView) findViewById(R.id.mainhome_workflow_gridview_2);
        MyScrollyGridView mGridView_3   = (MyScrollyGridView) findViewById(R.id.mainhome_workflow_gridview_3);
        MyScrollyGridView mGridView_4   = (MyScrollyGridView) findViewById(R.id.mainhome_workflow_gridview_4);
        mAdapter_1 		 = new FunctionAdapter(this);
        mAdapter_2		 = new FunctionAdapter(this);
        mAdapter_3 		 = new FunctionAdapter(this);
        mAdapter_4         = new FunctionAdapter(this);
        mGridView_1.setAdapter(mAdapter_1);
        mGridView_2.setAdapter(mAdapter_2);
        mGridView_3.setAdapter(mAdapter_3);
        mGridView_4.setAdapter(mAdapter_4);

        mAdapter_1.setData(ModeClickHandler.getInstances(MainHomeWorkflowActivity.this).initGridView(4));
//        mAdapter_2.setData(ModeClickHandler.getInstances(MainHomeWorkflowActivity.this).initGridView(5));
//        mAdapter_3.setData(ModeClickHandler.getInstances(MainHomeWorkflowActivity.this).initGridView(6));
        mAdapter_4.setData(ModeClickHandler.getInstances(MainHomeWorkflowActivity.this).initGridView(5));

        //设置如果无数据的话不显示控件
        //基础服务
        if (mAdapter_1.getCount() > 0) {
            mGridView_1.setOnItemClickListener(gridViewItemClickListener);
        }else {
            findViewById(R.id.mainhome_workflow_ll_title_1).setVisibility(View.GONE);
            mLl_1.setVisibility(View.GONE);
        }
        //业务测试
        if (mAdapter_2.getCount() > 0) {
            mGridView_2.setOnItemClickListener(gridViewItemClickListener);
        }else {
            findViewById(R.id.mainhome_workflow_ll_title_2).setVisibility(View.GONE);
            mLl_2.setVisibility(View.GONE);
        }
        //wifi工具
        if (mAdapter_3.getCount() > 0) {
            mGridView_3.setOnItemClickListener(gridViewItemClickListener);
        }else {
            findViewById(R.id.mainhome_workflow_ll_title_3).setVisibility(View.GONE);
            mLl_3.setVisibility(View.GONE);
        }
        //专业工具
        if (mAdapter_4.getCount() > 0) {
            mGridView_4.setOnItemClickListener(gridViewItemClickListenerForZygj);
        }else {
            findViewById(R.id.mainhome_workflow_ll_title_4).setVisibility(View.GONE);
            mLl_4.setVisibility(View.GONE);
        }

        // 未读消息
        tvUnreadMsg = (MarqueesTextView) findViewById(R.id.mainhome_workflow_unread_msg);
        tvUnreadMsg.setOnClickListener(this);
        //		TODO 3.1.4版开始屏蔽
        //		getNotifyUnreadMsg();

        setVisibilityType(typeInitFirst);
    }

    @Override
    protected void onResume() {
        MyLoginExitThread.setNotTatExit(false);
        SharedPreHandler.getShared(SpeedTest5g.getContext()).setBooleanShared(TypeKey.getInstance().IS_RECTIFY, false);
        super.onResume();
    }

    /**
     * 获取系统公告未读消息
     */
    @SuppressWarnings("deprecation")
    private void getNotifyUnreadMsg() {
        if (MyToSpile.getInstances().isAuthorityMenu(getString(R.string.permissionsNotify))) {
            String requestUrl = NetWorkUtilNow.getInstances().getToIp() + getResources().getString(R.string.URL_NOTIFY_GET_UNREAD_MSG);
            NetWorkUtilNow.getInstances().readNetworkPostDes(requestUrl, JsonHandler.getHandler().toJson(new BaseRequest()), EnumRequest.NET_NOTIFY_GET_UNREAD_MSG.toInt(), this);
        }
    }

    @Override
    public void onListener(int type, Object object, boolean isTrue) {
        super.onListener(type, object, isTrue);
        //快速搜索模块
        if (type == EnumRequest.MENU_SELECT_ONE.toInt()) {
//			getParent().startActivityForResult(new Intent(this,SearchAppActivity.class), EnumRequest.OTHER_MAINHOME_UPDATE_VIEW.toInt());
        } else if (type == EnumRequest.NET_NOTIFY_GET_UNREAD_MSG.toInt()) {// 未读系统消息返回
            if (isTrue) {
                systemUnreadMsg = JsonHandler.getHandler().getTarget(object.toString(), ResponseSystemUnreadMsg.class);
                if (systemUnreadMsg != null && systemUnreadMsg.isRs()) {
                    // 未读公告公告展示
                    if (systemUnreadMsg.getDatas().isShow()) {
                        tvUnreadMsg.setVisibility(View.VISIBLE);
                        tvUnreadMsg.setText(systemUnreadMsg.getDatas().getNoticeInfo().getNOTICE_TITLE());

                    } else {
                        tvUnreadMsg.setVisibility(View.GONE);
                    }
                    markRedForFunctionItem(mAdapter_4, systemUnreadMsg.getDatas().isRed());
                }

            } else {
                showCommon(object.toString());
            }
        }
    }

    /**
     * 功能入口标记红点
     */
    private void markRedForFunctionItem(FunctionAdapter functionAdapter,boolean isMarkRed) {
        if (functionAdapter != null && functionAdapter.getCount() > 0) {
            for (int i = 0; i < functionAdapter.getCount(); i++) {
                Item item = (Item) functionAdapter.getItem(i);
                if (item.getName().equals(getResources().getString(R.string.notify))) {
                    item.setMarkRed(isMarkRed);
                    functionAdapter.updateItem(item);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mainhome_workflow_unread_msg) {// 打开公告详情页
            Intent intent = new Intent(this, NotifyDetailActivity.class);
            intent.putExtra("notifyInfo", systemUnreadMsg.getDatas().getNoticeInfo());
            getParent().startActivityForResult(intent, EnumRequest.OTHER_MAINHOME_UPDATE_NOTIFY_VIEW.toInt());
        }
    }

    /**
     * GridView控件点击事件
     * 集中规划，集中入网，集中工参管理，集中分析优化
     */
    private OnItemClickListener gridViewItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mClickItem = (Item) parent.getAdapter().getItem(position);
            ModeClickHandler.getInstances(MainHomeWorkflowActivity.this).clickMode(mClickItem, true);
        }
    };

    //专业工具 点击区分游客与手机号登陆
    private OnItemClickListener gridViewItemClickListenerForZygj = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mClickItem = (Item) parent.getAdapter().getItem(position);
            //游客登录  专业工具不可点
            String loginType = SharedPreHandler.getShared(SpeedTest5g.getContext()).getStringShared("QgLoginType", "");
            if("Tourist".equals(loginType)){
                //提示登录
                Intent intent = new Intent(TypeKey.getInstance().ACTION_MAINHOME_SUPER);
                intent.putExtra("type", EnumRequest.OTHER_MAINHOME_TYPE_USER_TOURIST.toInt());
                MainHomeWorkflowActivity.this.sendBroadcast(intent);
            }else if("Phone".equals(loginType)){
                ModeClickHandler.getInstances(MainHomeWorkflowActivity.this).clickMode(mClickItem, true);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mReceiver);
            unregisterReceiver(workflowCast);
        } catch (Exception e) {
        }
    }

    /**
     * 设置显示类型
     * @param type
     */
    private void setVisibilityType(int type){
        if (type == EnumRequest.OTHER_MAINHOME_TYPE_OPTIMIZER.toInt()) {
            mViewScroll.setVisibility(View.VISIBLE);
            actionBar.setTitle("工具");
            if (mAdapter_1.getCount() <= 0 && mAdapter_2.getCount() <= 0 && mAdapter_3.getCount() <= 0 ||
                    mAdapter_4.getCount() <= 0) {
                mViewNotModeData.setVisibility(View.VISIBLE);
            }else {
                mViewNotModeData.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 登陆登出更新广播
     * @author JQJ
     *
     */
    private class UpdateConfigReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if(arg1.getAction().equals(TypeKey.getInstance().ACTION_MAINHOME_SUPER)){
                int type = arg1.getIntExtra("type", -1);
                if(type == -1000){
                    //刷新水印
                    WaterMarkUtil.showWatermarkView(MainHomeWorkflowActivity.this, getUser());
                }
            }
        }
    }

    /**
     * 作业流程页面广播
     */
    private BroadcastReceiver workflowCast = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                int type = intent.getIntExtra("type", EnumRequest.OTHER_MAINHOME_TYPE_WORKFLOW.toInt());
                if (mViewScroll == null) {
                    typeInitFirst = type;
                    return;
                }
                //作业流程、优化工具
                if (type == EnumRequest.OTHER_MAINHOME_TYPE_WORKFLOW.toInt() || type == EnumRequest.OTHER_MAINHOME_TYPE_OPTIMIZER.toInt()) {
                    setVisibilityType(type);
                    // 刷新公告视图
                } else if (type == EnumRequest.OTHER_MAINHOME_UPDATE_NOTIFY_VIEW.toInt()) {
                    updateNotifyView();
                    // 快速搜索页面返回首页刷新处理
                } else if (type == EnumRequest.OTHER_MAINHOME_UPDATE_VIEW.toInt()) {
                    int requestCode = (int) intent.getIntExtra("requestCode", 0);
                    if (requestCode == EnumRequest.OTHER_MAINHOME_UPDATE_NOTIFY_VIEW.toInt()) {
                        updateNotifyView();
                    }
                }
            }
        }
    };

    /**
     * 刷新公告视图
     */
    private void updateNotifyView() {
        if (tvUnreadMsg.getVisibility() == View.VISIBLE) {
            tvUnreadMsg.setVisibility(View.INVISIBLE);
        }
        markRedForFunctionItem(mAdapter_4, false);
        //		getNotifyUnreadMsg(); 全国版注释
    }

    @Override
    public void onHandleMessage(MyEvents events) {
        switch (events.getMode()) {
            case TASK:
                if (events.getType() == EnumRequest.OTHER_INIT.toInt()) {
                    mBgTopColor = R.color.bg_color;
                    mBgParentColor = R.color.bg_color;
                    mTitleTextColor = R.color.gray_c0c0c3;

                    init(R.string.workflow, false);
                }
                break;
            default:
                break;
        }

    }
}
