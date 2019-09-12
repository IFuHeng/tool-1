package personal.fuheng.common;

import android.net.ethernet.EthernetManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.changhong.jxsettings.R;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

public class IpV6UiConctral implements TextWatcher {
    private final EditText mEtGateway;
    private final EditText mEtDns;
    private final EditText mEtIp;
    private final EditText mEtMarkLength;

    private EthernetManager mEthernetManager;

    public IpV6UiConctral(EthernetManager ethmanager, View root, View.OnFocusChangeListener onFocusChangeListener) {

        mEthernetManager = ethmanager;

        mEtIp = (EditText) root.findViewById(R.id.editText_v6_ip);
        mEtIp.addTextChangedListener(this);
        mEtGateway = (EditText) root.findViewById(R.id.editText_v6_gateway);
        mEtGateway.addTextChangedListener(this);
        mEtDns = (EditText) root.findViewById(R.id.editText_v6_dns);
        mEtDns.addTextChangedListener(this);
        mEtMarkLength = (EditText) root.findViewById(R.id.editText_v6_mark_length);
        mEtMarkLength.addTextChangedListener(new TextWatcher() {
            private int mCount;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCount = count;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0)
                    return;

                while (s.charAt(0) == '0' && s.length() > 1) {//清除0开头的数字
                    s.delete(0, 1);
                }

                if ((s.length() == 3 && Integer.parseInt(s.toString()) > 126) || s.length() > 3) {
                    s.clear();
                    s.append('1').append('2').append('6');
                    mEtMarkLength.setSelection(0, 3);
                }
            }
        });
        if (onFocusChangeListener != null) {
            mEtIp.setOnFocusChangeListener(onFocusChangeListener);
            mEtGateway.setOnFocusChangeListener(onFocusChangeListener);
            mEtDns.setOnFocusChangeListener(onFocusChangeListener);
            mEtMarkLength.setOnFocusChangeListener(onFocusChangeListener);
        }
        Log.d(getClass().getSimpleName(), "====~getIpv6DatabaseAddress = " + ethmanager.getIpv6DatabaseAddress());
        refreshStatus();

    }

    /**
     * 判断IPV6是否有效
     *
     * @param IP
     * @return
     */
    public boolean isIpV6(String IP) {// 判断是否是一个IP
        return IP.matches("^\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*$");
    }

    public boolean checkInputCorrect() {
        return isIpV6(mEtIp.getText().toString()) && isIpV6(mEtGateway.getText().toString()) && isIpV6(mEtDns.getText().toString()) && !TextUtils.isEmpty(mEtMarkLength.getText().toString());
    }

    private int mCount;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mCount = count;
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 0)
            return;

        if (!isIpV6Input(s.toString())) {
            s.delete(s.length() - mCount, s.length());
        } else {
            Log.d(getClass().getSimpleName(), "====~ isIpv6 = " + isIpV6(s.toString()));
        }
    }


    private boolean isIpV6Input(String s) {

        if (s == null || s.isEmpty())
            return true;

        int colonNum = getCharCount(s, ':');

        if (colonNum == 0 && s.length() > 4) {
            return false;
        }

        int endsymbolNum = getCharCount(s, '.');
        if (endsymbolNum > 0) {
            if (colonNum > 0 && s.contains(":."))
                return false;
            if (endsymbolNum > 3)
                return false;
            if (s.contains(".."))
                return false;
        }

        if (colonNum > 2 && s.contains(":::"))
            return false;

        int doubleColonNum = getStringCount(s, "::");
        if (doubleColonNum > 1)// :: only allowed 1
            return false;

        if (s.length() > 1 && s.charAt(0) == ':' && s.charAt(1) != ':') { // 当 :开头，不允许直接接数字
            return false;
        }

        if (colonNum > 7 || (doubleColonNum > 0 && colonNum > 6)) { // 最多8段
            return false;
        }

        if (endsymbolNum > 0) {
            if (colonNum > 6)
                return false;
            if (doubleColonNum > 0 && colonNum > 5)
                return false;

            int end = s.lastIndexOf('.');
            if (s.indexOf(':', end) != -1)
                return false;
        }

        String[] list = s.split(":");
//            Log.d(getClass().getSimpleName(), "====~ list = " + Arrays.toString(list));
        for (int i = 0; i < list.length; i++) {
            String s1 = list[i];
//                Log.d(getClass().getSimpleName(), "====~ s1 = " + s1);
            if (endsymbolNum > 0 && s1.indexOf('.') != -1) {// 当前段 含有 ‘.’ 的时候，代表是ipv4，必须在最后32位，如果前面少于96位，那么必须含有"::"
                if (i != list.length - 1)
                    return false;
                else
                    return isIpV4Input(s1);
            }
            if (s1.length() > 4) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断是否是输入中的ipv4
     *
     * @param s
     * @return
     */
    private boolean isIpV4Input(String s) {
        final char[] HEX_CHARS = {'a', 'b', 'c', 'd', 'e', 'f', 'A', 'B', 'C', 'D', 'E', 'F'};
        for (char hex_char : HEX_CHARS) {
            if (s.indexOf(hex_char) != -1)
                return false;
        }

        int endsymbolNum = getCharCount(s, '.');
        if (endsymbolNum == 0) {
            if (s.length() > 3)
                return false;
            if (s.length() == 3 && Integer.parseInt(s) > 255)
                return false;
        } else {
            if (endsymbolNum > 1 && s.contains("..")) {
                return false;
            }

            int start = 0;
            int end = s.indexOf('.');
            do {
                String s1 = s.substring(start, end);
//                    Log.d(getClass().getSimpleName(), "====~ ipv4 :" + s1);
                if (s1.length() > 3)
                    return false;
                if (s1.length() == 3 && Integer.parseInt(s1) > 255)
                    return false;

                start = s.indexOf('.', end);
                if (start == s.length() - 1 || start == -1)
                    break;
                start += 1;

                end = s.indexOf('.', start);
                if (end == -1)
                    end = s.length();

            } while (true);
        }
        return true;
    }

    private int getCharCount(String str, char c) {
        int result = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c)
                result++;
        }
        return result;
    }

    private int getStringCount(String str, String s) {
        int result = 0;
        int start = 0;
        do {
            start = str.indexOf(s, start);
            if (start == -1)
                break;
            start += s.length();
            result++;
        } while (true);

        return result;
    }

    public byte[] turnIpv6ToByte(String ipv6str) {
        try {
            InetAddress inetaddress = InetAddress.getByName(ipv6str);
            Log.d(getClass().getSimpleName(), "====~ turnIpv6ToByte :" + Arrays.toString(inetaddress.getAddress()));
            Log.d(getClass().getSimpleName(), "====~ turnIpv6ToByte :" + inetaddress.getHostAddress());
//            Log.d(getClass().getSimpleName(), "====~ turnIpv6ToByte :" + inetaddress.getHostName());
            return inetaddress.getAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String turnByte2Ipv6(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toHexString(bytes[0]));
            if (i % 2 == 1 && i < bytes.length - 2) {
                sb.append(':');
            }
        }
        return sb.toString();
    }

    public void setIpv6Static() {
        if (!checkInputCorrect()) {
            return;
        }

        String IP = mEtIp.getText().toString();
        if (TextUtils.isEmpty(IP) || !isIpV6(IP)) {
            mEtIp.requestFocus();
            return;
        }

        if (mEtMarkLength.getText().length() == 0) {
            mEtMarkLength.requestFocus();
            return;
        }
        int prefixLength = Integer.parseInt(mEtMarkLength.getText().toString());
        if (prefixLength > 126) {
            mEtMarkLength.requestFocus();
            return;
        }
        String gw = mEtGateway.getText().toString();
        if (TextUtils.isEmpty(gw) || !isIpV6(gw)) {
            mEtGateway.requestFocus();
            return;
        }
        String dns1 = mEtDns.getText().toString();
        if (TextUtils.isEmpty(dns1) || !isIpV6(dns1)) {
            mEtDns.requestFocus();
            return;
        }

        if (!checkHasChanged(IP, prefixLength, gw, dns1, null)) {
            Toast.makeText(mEtIp.getContext(), R.string.not_modify, Toast.LENGTH_SHORT).show();
            return;
        }

        mEthernetManager.setIpv6DatabaseInfo(IP, prefixLength, gw, dns1, null);
        mEthernetManager.setEthernetEnabled(false);
        mEthernetManager.enableIpv6(true);
        mEthernetManager.setEthernetMode6(EthernetManager.ETHERNET_CONNECT_MODE_MANUAL);
        mEthernetManager.setEthernetEnabled(true);

        Toast.makeText(mEtIp.getContext(), mEtDns.getContext().getString(R.string.change_to_lan) + "(IPV6)", Toast.LENGTH_SHORT).show();
    }

    private boolean checkHasChanged(String IP, int prefixLength, String gw, String dns1, String dns2) {
        if (mEthernetManager.getIpv6PersistedState() != EthernetManager.IPV6_STATE_ENABLED) {
            Log.d(getClass().getSimpleName(),"====~ current not ipv6");
            return true;
        }

        String oldIp = mEthernetManager.getIpv6DatabaseAddress();
Log.d(getClass().getSimpleName(),"====~ compare IP :" + oldIp + " ==  " + IP);
        if (oldIp == null) {
            if (IP != null)
                return true;
        } else if (!oldIp.equalsIgnoreCase(IP))
            return true;

        int oldLen = mEthernetManager.getIpv6DatabasePrefixlength();
Log.d(getClass().getSimpleName(),"====~ compare length :" + oldLen + " ==  " + prefixLength);
        if (oldLen != prefixLength)
            return true;

        String oldGw = mEthernetManager.getIpv6DatabaseGateway();
Log.d(getClass().getSimpleName(),"====~ compare Gateway :" + oldGw + " ==  " + gw);
        if (oldGw == null) {
            if (gw != null)
                return true;
        } else if (!oldGw.equalsIgnoreCase(gw))
            return true;

        String oldDns1 = mEthernetManager.getIpv6DatabaseDns1();
Log.d(getClass().getSimpleName(),"====~ compare dns1 :" + oldDns1 + " ==  " + dns1);
        if (oldDns1 == null) {
            if (dns1 != null)
                return true;
        } else if (!oldDns1.equalsIgnoreCase(dns1))
            return true;

        String oldDns2 = mEthernetManager.getIpv6DatabaseDns2();
Log.d(getClass().getSimpleName(),"====~ compare dns2 :" + oldDns2 + " ==  " + dns2);
        if (oldDns2 == null) {
            if (dns2 != null)
                return true;
        } else if (!oldDns2.equalsIgnoreCase(dns2))
            return true;

        return false;
    }

    public void refreshStatus() {
        if (mEthernetManager.getIpv6DatabaseAddress().equals("::")) {

//        } else if (ethmanager.getIpv6DatabaseAddress().contains("::")) {
//            try {
//                mEtIp.setText(turnByte2Ipv6(InetAddress.getByName(ethmanager.getIpv6DatabaseAddress()).getAddress()));
//                mEtGateway.setText(turnByte2Ipv6(InetAddress.getByName(ethmanager.getIpv6DatabaseGateway()).getAddress()));
//                mEtDns.setText(turnByte2Ipv6(InetAddress.getByName(ethmanager.getIpv6DatabaseDns1()).getAddress()));
//                mEtMarkLength.setText(Integer.toString(ethmanager.getStatelessIpv6Prefixlength()));
//            } catch (UnknownHostException e) {
//                e.printStackTrace();
//                mEtIp.setText(ethmanager.getIpv6DatabaseAddress());
//                mEtGateway.setText(ethmanager.getIpv6DatabaseGateway());
//                mEtDns.setText(ethmanager.getIpv6DatabaseDns1());
//                mEtMarkLength.setText(Integer.toString(ethmanager.getStatelessIpv6Prefixlength()));
//            }
        } else {
            mEtIp.setText(mEthernetManager.getIpv6DatabaseAddress());
            mEtGateway.setText(mEthernetManager.getIpv6DatabaseGateway());
            mEtDns.setText(mEthernetManager.getIpv6DatabaseDns1());
            mEtMarkLength.setText(String.valueOf(mEthernetManager.getIpv6DatabasePrefixlength()));
        }
    }
}
