package com.zxa.apptest.zxaapp;

/**
 * Created by zxa on 16/1/21.
 */

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

public class RedBaoService extends AccessibilityService {
    /**
     * 红包队列
     */
    private List<AccessibilityNodeInfo> redBaoList = new ArrayList<>();
    /**
     * 状态变量,控制窗口变化触发onAccessibilityEvent函数的次数,处理完一次变化再处理下一次,防止app挂死
     */
    private boolean flag = false;
    /**
     * 状态枚举值
     */
    private enum Action {
        getRedBao,
        openRedBao
    }
    private Action nowAction;
    /**
     * AccessibilityEvent的回调方法
     *
     * 当窗体状态或内容变化时，根据当前阶段选择相应的入口
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        if (this.flag) return;

        this.flag = true;

        try {
            handleWindowChange(event.getSource());
        }
        catch (Exception e) {
            // 最土逼的打印错误
            System.out.println(e.toString());
        }
        finally {
            // 处理完事件后释放锁
            this.flag = false;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void handleWindowChange(AccessibilityNodeInfo nodeInfo) throws Exception {
        if (this.nowAction == Action.getRedBao) {
            if (nodeInfo == null) return;

            if (redBaoList.size() > 0) {
                AccessibilityNodeInfo node = redBaoList.remove(redBaoList.size() - 1);
                if (node.getParent() != null) {
                    this.nowAction = Action.openRedBao;
                    node.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
                return;
            }

            /* 聊天会话窗口，遍历节点匹配“领取红包” */
            List<AccessibilityNodeInfo> nodesList = nodeInfo.findAccessibilityNodeInfosByText("领取红包");

            if (!nodesList.isEmpty()) {
                for (AccessibilityNodeInfo node : nodesList) {
            /*下个版本加入"排除"已判断过的红包的method*/
                    redBaoList.add(node);
                }
                if (redBaoList.size() > 0) {
                    AccessibilityNodeInfo node = redBaoList.remove(redBaoList.size() - 1);
                    if (node.getParent() != null) {
                        this.nowAction = Action.openRedBao;
                        node.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                    return;
                }
            }
        } else if (this.nowAction == Action.openRedBao) {
            /* 点开红包,判断红包是否有效 */
            List<AccessibilityNodeInfo> failureNodes = new ArrayList<>();
            failureNodes.addAll(nodeInfo.findAccessibilityNodeInfosByText("红包详情"));
            failureNodes.addAll(nodeInfo.findAccessibilityNodeInfosByText("手慢了"));
            failureNodes.addAll(nodeInfo.findAccessibilityNodeInfosByText("过期"));
            if (!failureNodes.isEmpty()) {
                this.nowAction = Action.getRedBao;
                performMyGlobalAction(GLOBAL_ACTION_BACK);
                return;
            }

        /* 点开红包，找到拆红包按钮并点击 */
            List<AccessibilityNodeInfo> successNodes = nodeInfo.findAccessibilityNodeInfosByText("拆红包");
            if (!successNodes.isEmpty()) {
                AccessibilityNodeInfo openNode = successNodes.get(successNodes.size() - 1);
                this.nowAction = Action.getRedBao;
                openNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                /* 这里需要加入一个延迟方案,因为打开红包有延迟 */
                performMyGlobalAction(GLOBAL_ACTION_BACK);// 退回聊天界面
                return;
            } else {
                this.nowAction = Action.getRedBao;
                return;
            }
        }
    }

    @Override
    public void onInterrupt() {
        // 中断函数,必须重写
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void performMyGlobalAction(int action) {
        this.flag = false;
        performGlobalAction(action);
    }
}

