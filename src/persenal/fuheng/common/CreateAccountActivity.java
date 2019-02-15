package com.ericsson.geely.ui.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.ericsson.geely.R;
import com.ericsson.geely.R2;
import com.ericsson.geely.adapter.AccountCompleteAdapter;
import com.ericsson.geely.data.dao.BindVehicleInfoDao;
import com.ericsson.geely.data.dao.LoginRecordDao;
import com.ericsson.geely.data.entity.LoginRecord;
import com.ericsson.geely.ui.BaseActivity;
import com.ericsson.geely.ui.language.LanguageActivity;
import com.ericsson.geely.ui.main.BeforeMainActivity;
import com.ericsson.geely.ui.setting.IpAddrActivity;
import com.ericsson.geely.ui.web.WebviewActivity;
import com.ericsson.lib.utils.CommonUtils;
import com.ericsson.lib.utils.StatusBarUtil;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;

public class CreateAccountActivity extends BaseActivity {

    @BindView(R.id.edit_phone)
    EditText mEditAccount;
    @BindView(R2.id.edit_psw)
    EditText mEditUserPsw;
    @BindView(R2.id.btn_sure)
    Button mBtnSure;
    @BindView(R2.id.btn_cancel)
    TextView mBtnCancel;
    @BindView(R.id.btn_get_verifi_code)
    Button mBtnGetVerfiCode;
    @BindView(R.id.txt_hint)
    TextView mTxtHint;
    private final static int REQUEST_REGISTER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setActionBarToolbarTitle(null);
        getActionBarToolbar().setBackgroundColor(Color.TRANSPARENT);
        getActionBarToolbar().setOverflowIcon(CommonUtils.tintDrawable(
                getActionBarToolbar().getOverflowIcon(), Color.WHITE
        ));

        StatusBarUtil.setStatusBarLightMode(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActionBarToolbar().setElevation(0);
        }

        initView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mEditAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEditUserPsw.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                login();
                return false;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_REGISTER:
                if (resultCode == RESULT_OK && data != null) {
                    String resultData = data.getStringExtra(BUNDLE_RESULT);
                    try {
                        Map<String, String> map = JSON.parseObject(resultData, Map.class);
                        mEditAccount.setText(map.get("userName"));
                        mEditUserPsw.setText(map.get("password"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_setting:
                toActivity(LanguageActivity.class);
                break;
            case R.id.item_ip:
                toActivity(IpAddrActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @OnClick({R.id.btn_login, R.id.btn_forget, R.id.btn_to_register})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
//                login();
                break;
            case R.id.btn_forget:
                WebviewActivity.start(this, "/login/forgetPasswordStepOne.html", mEditAccount.getText().toString(), REQUEST_REGISTER);
                break;
            case R.id.btn_to_register:
                WebviewActivity.start(this, "/login/createAccountStepOne.html", REQUEST_REGISTER);
                break;
            default:
                break;
        }
    }


    private boolean isPhoneNumber(String phone) {
        Pattern p = Pattern.compile("^1(3|4|5|7|8)[0-9]\\\\d{8}$");
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    private boolean isMatchPassword(String psw) {
        Pattern p = Pattern.compile("^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]{8,12}$");
        Matcher m = p.matcher(psw);
        return m.matches();
    }

}
