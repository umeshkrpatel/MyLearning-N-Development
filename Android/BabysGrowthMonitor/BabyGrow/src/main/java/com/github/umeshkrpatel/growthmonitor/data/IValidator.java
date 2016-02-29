package com.github.umeshkrpatel.growthmonitor.data;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.github.umeshkrpatel.growthmonitor.Utility;

/**
 * Created by weumeshweta on 25-Feb-2016.
 */
public class IValidator implements TextWatcher {
    public static final int MODE_WEIGHT = 0;
    public static final int MODE_HEIGHT = 1;
    public static final int MODE_HEADSIZE = 2;
    public static final int MODE_DATE = 3;
    public static final int COLOR_VALID = 0xFF067406;
    public static final int COLOR_WARNING = 0xFFCB6600;
    public static final int COLOR_ERROR = 0xFFC70101;
    private final EditText mEditText;
    private final int mMode;
    public IValidator(EditText editText, int mode) {
        mEditText = editText;
        mMode = mode;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() <= 0)
            return;

        IBabyInfo info = IBabyInfo.currentBabyInfo();
        boolean result = true;
        switch (mMode) {
            case MODE_DATE: {
                Long date = Utility.getDateTime();
                if (date < info.getBirthDate() || date > System.currentTimeMillis()) {
                    result = false;
                }
            }
            break;
            case MODE_WEIGHT: {
                Double weight = Double.parseDouble(s.toString());
                result = IGrowthInfo.validateWeight(weight);
            }
            break;
            case MODE_HEIGHT: {
                Double height = Double.parseDouble(s.toString());
                result = IGrowthInfo.validateHeight(height);
            }
            break;
            case MODE_HEADSIZE: {
                Double headSize = Double.parseDouble(s.toString());
                result = IGrowthInfo.validateHeadSize(headSize);
            }
            break;
        }

        if (result)
            mEditText.setTextColor(COLOR_VALID);
        else
            mEditText.setTextColor(COLOR_WARNING);
    }
}
