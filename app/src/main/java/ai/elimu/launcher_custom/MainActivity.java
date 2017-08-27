package ai.elimu.launcher_custom;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ai.elimu.launcher_custom.R.layout.activity_main);

        Button button = (Button) findViewById(ai.elimu.launcher_custom.R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AppsListActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = new Intent(getApplicationContext(), HomeScreensActivity.class);
        startActivity(intent);

        finish();
    }

    @Override
    public void onBackPressed() {
        //do nothing
    }
}
