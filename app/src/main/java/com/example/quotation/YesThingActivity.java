package com.example.quotation;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bigkoo.pickerview.TimePickerView;
import com.example.database.bean.AlreadyBean;
import com.example.database.dao.AlreadyDao;
import com.example.database.dao.UpcomingDao;
import com.example.tool.*;
import com.example.tool.ShowYes.InnerItemOnclickListener;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class YesThingActivity extends AppCompatActivity implements ShowYes.InnerItemOnclickListener,
        AdapterView.OnItemClickListener {

    private ListView yesThingShow;
    private EditText lookDate, sum;
    private static Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.yesthingtotal);
        yesThingShow = findViewById(R.id.YesThingShow);
        lookDate = findViewById(R.id.lookDate);
        sum = findViewById(R.id.sum);
        Button reset = findViewById(R.id.reset);
        final DateTool d = new DateTool();
        show(yesThingShow, d.getYearNow(), d.getMonthNow());
        sum.setText(new AlreadyDao().findSum(d.getYearNow(), d.getMonthNow()));

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(YesThingActivity.this,AddYesActivity.class));
                finish();
            }
        });

        lookDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    try {
                        show(yesThingShow, d.getYear(d.getDateGood1(s.toString())),
                                d.getMonth(d.getDateGood1(s.toString())));
                        sum.setText(new AlreadyDao().findSum(
                                d.getYear(d.getDateGood1(s.toString())),
                                d.getMonth(d.getDateGood1(s.toString()))));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // ????????????
        lookDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDate();
            }
        });
        lookDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showDate();
            }
        });

    }

    public void show(ListView tt, String year, String month) {
        AlreadyDao alreadyDao = new AlreadyDao();
        List<AlreadyBean> all = alreadyDao.findAll(year, month);
        ShowYes showYes = new ShowYes(this, all);
        showYes.setOnInnerItemOnClickListener(this);
        tt.setAdapter(showYes);
        tt.setOnItemClickListener(this);
    }

    /**
     * ????????????
     */
    private void showDate(){
        TimePickerView timePickerView = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                String start = null;
                try {
                    start = new DateTool().getDateOO1(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                lookDate.setText(start);
            }
        }).setType(new boolean[]{true, true, false, false, false, false}).build();
        //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        timePickerView.setDate(Calendar.getInstance());
        timePickerView.show();
    }

    @Override
    public void itemClick(View v) {
        String position;
        position =(String) v.getTag();
        switch (v.getId()) {
            case R.id.update:
                updateDialog(position);
                break;
            case R.id.del:
                deleteLog(position);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public static Context getContext() {
        return context;
    }


    /**
     * ???????????????
     */
    public void showReset(){
        final DateTool d = new DateTool();
        String s = lookDate.getText().toString();
        if (s.isEmpty()){
            show(yesThingShow, d.getYearNow(), d.getMonthNow());
            sum.setText(new AlreadyDao().findSum(d.getYearNow(), d.getMonthNow()));
        }else {
            try {
                show(yesThingShow, d.getYear(d.getDateGood1(s)),
                        d.getMonth(d.getDateGood1(s)));
                sum.setText(new AlreadyDao().findSum(
                        d.getYear(d.getDateGood1(s)),
                        d.getMonth(d.getDateGood1(s))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ????????????
     */
    public void updateDialog(final String id){
        final CustomizationDialog cdl = new CustomizationDialog(this);
        cdl.setOnOkOnclickListener(new CustomizationDialog.onOkOnclickListener() {
            @Override
            public String onOkClick() {
                return id;
            }
        });
        cdl.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                showReset();
            }
        });
        cdl.show();
        cdl.initData(new AlreadyDao().updateOne(id));
    }

    public void deleteLog(final String position){
        final DeleteDialog dialog = new DeleteDialog(this);
        dialog.setOnDeleteOkOnclickListener(new DeleteDialog.onDeleteOkOnclickListener() {
            @Override
            public void onDeleteOkClick() {
                new AlreadyDao().deleteOne(position);
                new ToastWei().showToast(YesThingActivity.this,"????????????");
                showReset();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
