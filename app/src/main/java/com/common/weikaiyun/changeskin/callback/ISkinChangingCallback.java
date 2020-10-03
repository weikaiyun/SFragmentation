package com.common.weikaiyun.changeskin.callback;

public interface ISkinChangingCallback {
    void onStart();

    void onError(Exception e);

    void onComplete();

    DefaultSkinChangingCallback DEFAULT_SKIN_CHANGING_CALLBACK = new DefaultSkinChangingCallback();

    class DefaultSkinChangingCallback implements ISkinChangingCallback {
        @Override
        public void onStart() {

        }

        @Override
        public void onError(Exception e) {

        }

        @Override
        public void onComplete() {

        }
    }
}
