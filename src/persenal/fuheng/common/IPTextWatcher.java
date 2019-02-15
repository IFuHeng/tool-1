package personal.fuheng.common;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

public class IPTextWatcher implements TextWatcher {

    private int mCount;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.d("TEST", s + "  : [start = " + start + ", before = " + before + ", count = " + count);
        mCount = count;
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mCount == 0)
            return;

        if (mCount == 1) {//手动输入的情况
            String temp = s.toString();
            if (temp.indexOf('.') == -1) {//when no '.' exist
                if (isValueGt255(temp))
                    s.insert(s.length() - 1, ".");

                if (temp.length() == 1 && temp.charAt(0) == '0')
                    s.append('.');
                return;
            }

            if (s.charAt(s.length() - 1) != '.') {
                temp = s.subSequence(temp.lastIndexOf('.') + 1, s.length()).toString();

                if (getCharCount(s, '.') < 3) {
                    if (isValueGt255(temp)) {
                        s.insert(s.length() - 1, ".");
                    }
                    if (temp.length() == 1 && temp.charAt(0) == '0')
                        s.append('.');
                } else if (!isFormatCorrect(temp))
                    deleteNewInputWord(s, mCount);
                return;
            } else if (getCharCount(s, '.') > 3) {
                deleteNewInputWord(s, mCount);
            } else if (s.length() == 1 || s.charAt(s.length() - 2) == '.') {// "." ||  "*.."的情况
                s.insert(s.length() - 1, "0");
            }
            return;
        }

        //复制粘贴的情况
        if (!isFormatCorrect(s)) {
            deleteNewInputWord(s, mCount);
        }


    }

    private boolean isFormatCorrect(Editable s) {
        if (getCharCount(s, '.') > 3)
            return false;

        int end = s.length();
        for (int i = s.length() - 1; i >= 0; --i) {
            if (s.charAt(i) == '.' || i == 0) {
                if (!isFormatCorrect(s.subSequence(i + 1, end).toString())) {
                    return false;
                }
                end = i;
            }
        }

        return true;

    }

    private int getCharCount(Editable s, char c) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c)
                count++;
        }
        return count;
    }

    /**
     * @param part 无'.'的片段
     * @return 是否满足是十进制且小于255
     */
    private boolean isFormatCorrect(String part) {
        return !isValueGt255(part) && !isValueStartWith0(part);
    }

    private boolean isValueGt255(String part) {
        if (part.length() > 2) {
            int value = Integer.parseInt(part);
            if (value > 255)
                return true;
        }
        return false;
    }

    private boolean isValueStartWith0(String part) {
        return part.length() == 2 && part.charAt(0) == '0';
    }

    private void deleteNewInputWord(Editable s, int count) {
        s.delete(s.length() - count, s.length());
    }

}


