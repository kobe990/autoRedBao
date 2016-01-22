package com.zxa.apptest.zxaapp;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * Created by zxa on 1/21/16.
 *
 * qq自动回复主要的逻辑代码
 */
public class AutoReplyService extends AccessibilityService {

    private boolean flag = false;

    /**
     * AccessibilityEvent的回调方法
     * <p/>
     * 当聊天窗体状态或内容变化时，根据当前阶段选择相应的入口
     *
     * @param event 事件
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        if (this.flag) return;

        this.flag = true;

        try {
            fetchChatText(event.getSource());
        } finally {
            this.flag = false;
        }

    }

    @Override
    public void onInterrupt() {

    }

    //qq chat test
    private void fetchChatText(AccessibilityNodeInfo nodeInfo) {
        /* QQ聊天会话窗口，遍历节点匹配“” */
       // List<AccessibilityNodeInfo> fetchNodes = nodeInfo.findAccessibilityNodeInfosByText("ok");

       // if (fetchNodes.isEmpty()) return;

//        for (AccessibilityNodeInfo cellNode : fetchNodes) {
////            String id = getHongbaoHash(cellNode);
////
////
////            if (id != null && !fetchedIdentifiers.contains(id)) {
////                nodesToFetch.add(cellNode);
////            }
            System.out.println("zxa9:ok is sended");
//            cellNode.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
//        }

        if(getRootInActiveWindow().getChildCount() == 7) {
            AccessibilityNodeInfo textNode = getRootInActiveWindow().getChild(1).getChild(2).getChild(0).getChild(1);
            textNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", "TEST DATA");
            clipboard.setPrimaryClip(clip);
            textNode.performAction(AccessibilityNodeInfo.ACTION_PASTE);
        }
        //System.out.println("zxa:" + getRootInActiveWindow().getChildCount());//.performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }
}
