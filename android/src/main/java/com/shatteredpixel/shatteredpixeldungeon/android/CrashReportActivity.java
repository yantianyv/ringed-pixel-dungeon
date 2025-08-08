package com.shatteredpixel.shatteredpixeldungeon.android;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CrashReportActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_report);

        String crashLog = getIntent().getStringExtra("crash_log");
        TextView logView = findViewById(R.id.crash_log);
        logView.setText(crashLog);

        Button copyBtn = findViewById(R.id.copy_btn);
        copyBtn.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Crash Log", crashLog);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, R.string.crash_log_copied, Toast.LENGTH_SHORT).show();
        });
    }
}
