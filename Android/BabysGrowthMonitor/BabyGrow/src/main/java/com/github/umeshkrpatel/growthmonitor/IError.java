package com.github.umeshkrpatel.growthmonitor;

/**
 * Created by weumeshweta on 25-Feb-2016.
 */
public interface IError {
    int ERROR_NONE      = 0;
    int UNDER_WEIGHT    = 1<<1;
    int OVER_WEIGHT     = 1<<2;
    int UNDER_HEIGHT    = 1<<3;
    int OVER_HEIGHT     = 1<<4;
    int UNDER_HEADSIZE  = 1<<5;
    int OVER_HEADSIZE   = 1<<6;
    int INVALID_DATE    = 1<<7;
}
