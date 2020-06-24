package com.common.weikaiyun.changeskin.attr;

import android.view.View;

import java.util.List;

public class SkinView {
    public View view ;
    public List<SkinAttr> attrs;

    public SkinView(View view, List<SkinAttr> skinAttrs) {
        this.view = view;
        this.attrs = skinAttrs;
    }

    public void apply() {
        if (view == null) return;

        for (SkinAttr attr : attrs) {
            attr.apply(view);
        }
    }
}
