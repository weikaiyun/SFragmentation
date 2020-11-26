package com.weikaiyun.fragmentation.debug;

import java.util.List;

/**
 * 为了调试时 查看栈视图
 */
public class DebugFragmentRecord {
    public CharSequence fragmentName;
    public List<DebugFragmentRecord> childFragmentRecord;

    public DebugFragmentRecord(CharSequence fragmentName, List<DebugFragmentRecord> childFragmentRecord) {
        this.fragmentName = fragmentName;
        this.childFragmentRecord = childFragmentRecord;
    }
}
