package com.example.quotation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.bigkoo.pickerview.TimePickerView;
import com.example.database.bean.AlreadyBean;
import com.example.database.dao.AlreadyDao;
import com.example.tool.DateTool;
import com.example.tool.ExcelUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author zhc
 */
public class OutExcelActivity extends AppCompatActivity {

    private EditText outExcelDate;
    private Button outExcel,open,toPdf;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outdata);

        outExcelDate = findViewById(R.id.outExcelDate);
        outExcel = findViewById(R.id.outExcel);
        open = findViewById(R.id.open);

        outExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    saveExcel();
                } catch (ParseException | IOException e) {
                    e.printStackTrace();
                }
            }
        });

        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager packageManager = getPackageManager();
                Intent intent= packageManager.getLaunchIntentForPackage("com.android.fileexplorer");
                startActivity(intent);
                finish();
            }
        });


        // ??????
        outExcelDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                showDate();
            }
        });
        outExcelDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showDate();
            }
        });
    }


    /**
     * ????????????
     */
    public void saveExcel() throws ParseException, IOException {
        String year = "";
        String month = "";
        DateTool d = new DateTool();
        if (!outExcelDate.getText().toString().isEmpty()){
            year = d.getYear(d.getDateGood1(outExcelDate.getText().toString()));
            month = d.getMonth(d.getDateGood1(outExcelDate.getText().toString()));
        }else {
            year = d.getYearNow();
            month = d.getMonthNow();
        }


        String fineName = "/??????-" + year + "-" + month + ".xls";
        String fineNameOne = "??????-" + year + "-" + month + "-???????????????????????????????????????????????????";
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/???????????????";

        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            String[] SdCardPermission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (ContextCompat.checkSelfPermission(this, SdCardPermission[0]) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, SdCardPermission, 100);
            }
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
        }

        String[] title = {"??????","????????????","?????????","????????????","??????(???)","??????",
                "??????","????????????","?????????","????????????","?????????","????????????","??????"};
        String[] sheetName = {"???","??????"};

        List<AlreadyBean> allOne = new AlreadyDao().findAll(year, month, sheetName[0]);
        String allSum1 = new AlreadyDao().findAllSum(year, month, sheetName[0]);
        List<AlreadyBean> allTwo = new AlreadyDao().findAll(year, month, sheetName[1]);
        String allSum2 = new AlreadyDao().findAllSum(year, month, sheetName[1]);
        filePath = filePath + fineName;

        ExcelUtils.initExcel(filePath,fineNameOne,sheetName,title);
        ExcelUtils.writeObjListToExcel(allOne,filePath,OutExcelActivity.this,0,allSum1);
        ExcelUtils.writeObjListToExcel(allTwo,filePath,OutExcelActivity.this,1,allSum2);

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
                    start = new DateTool().getDateOO(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                outExcelDate.setText(start);
            }
        }).setType(new boolean[]{true, true, false, false, false, false}).build();
        //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        timePickerView.setDate(Calendar.getInstance());
        timePickerView.show();
    }
}
