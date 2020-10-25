package jesse.me;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import jesse.me.view.LikeView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LikeView mLikeView = findViewById(R.id.lv);
        mLikeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLikeView.click();
            }
        });
    }


}