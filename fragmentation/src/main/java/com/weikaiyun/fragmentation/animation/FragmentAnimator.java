package com.weikaiyun.fragmentation.animation;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.AnimRes;

/**
 * Fragment动画实体类
 */
public class FragmentAnimator implements Parcelable {
    @AnimRes
    protected int targetFragmentEnter;
    @AnimRes
    protected int currentFragmentPopExit;
    @AnimRes
    protected int currentFragmentPopEnter;
    @AnimRes
    protected int targetFragmentExit;

    public FragmentAnimator() {
    }

    public FragmentAnimator(int targetFragmentEnter, int currentFragmentPopExit, int currentFragmentPopEnter, int targetFragmentExit) {
        this.targetFragmentEnter = targetFragmentEnter;
        this.currentFragmentPopExit = currentFragmentPopExit;
        this.currentFragmentPopEnter = currentFragmentPopEnter;
        this.targetFragmentExit = targetFragmentExit;
    }

    protected FragmentAnimator(Parcel in) {
        targetFragmentEnter = in.readInt();
        currentFragmentPopExit = in.readInt();
        currentFragmentPopEnter = in.readInt();
        targetFragmentExit = in.readInt();
    }

    public static final Creator<FragmentAnimator> CREATOR = new Creator<FragmentAnimator>() {
        @Override
        public FragmentAnimator createFromParcel(Parcel in) {
            return new FragmentAnimator(in);
        }

        @Override
        public FragmentAnimator[] newArray(int size) {
            return new FragmentAnimator[size];
        }
    };

    public int getTargetFragmentEnter() {
        return targetFragmentEnter;
    }

    public int getCurrentFragmentPopExit() {
        return currentFragmentPopExit;
    }

    public int getCurrentFragmentPopEnter() {
        return currentFragmentPopEnter;
    }

    public int getTargetFragmentExit() {
        return targetFragmentExit;
    }

    public FragmentAnimator setTargetFragmentEnter(int targetFragmentEnter) {
        this.targetFragmentEnter = targetFragmentEnter;
        return this;
    }

    public FragmentAnimator setCurrentFragmentPopExit(int currentFragmentPopExit) {
        this.currentFragmentPopExit = currentFragmentPopExit;
        return this;
    }

    public FragmentAnimator setCurrentFragmentPopEnter(int currentFragmentPopEnter) {
        this.currentFragmentPopEnter = currentFragmentPopEnter;
        return this;
    }

    public FragmentAnimator setTargetFragmentExit(int targetFragmentExit) {
        this.targetFragmentExit = targetFragmentExit;
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(targetFragmentEnter);
        dest.writeInt(currentFragmentPopExit);
        dest.writeInt(currentFragmentPopEnter);
        dest.writeInt(targetFragmentExit);
    }
}
