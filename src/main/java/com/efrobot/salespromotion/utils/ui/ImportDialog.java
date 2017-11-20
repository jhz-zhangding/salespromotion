package com.efrobot.salespromotion.utils.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.efrobot.salespromotion.R;

/**
 * @项目名称:餐饮Diy
 * @类名称：ImportDialog
 * @类描述：导入dialog
 * @创建人：luyuqin
 * @创建时间：2017/5/2613:33
 * @修改时间：2017/5/2613:33
 * @备注：
 */
public class ImportDialog extends BaseDialog implements View.OnClickListener {
    private Button mCancelBtn;

    public ImportDialog(Context context, int layoutId) {
        super(context, layoutId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCancelBtn = (Button) findViewById(R.id.cancel_import_btn);
        mCancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel_import_btn:
                dismiss();
                break;
        }
    }
}
