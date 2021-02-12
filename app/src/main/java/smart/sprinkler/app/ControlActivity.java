package smart.sprinkler.app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

public class ControlActivity extends FragmentActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.water_control, new WaterControlFragment()).commit();
    }

    public void onNotificationViewClick(View v) {
        if (v.getId() == R.id.weatherNotification) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this,
                    android.R.style.Theme_Holo_Light_Dialog);

            // create custom dialog view
            View weatherDialogView = getLayoutInflater().inflate(R.layout.dialog, null);
            alertDialogBuilder.setView(weatherDialogView);

            // set message text
            TextView message = (TextView) weatherDialogView.findViewById(R.id.dialogMessage);
            message.setText(R.string.weather_notification_text);

            // show dialog
            final AlertDialog dialog = alertDialogBuilder.show();

            // add click listener to the  the dialog button
            // click on the button should close the dialog
            Button okButton = (Button) dialog.findViewById(R.id.dialogButtonOk);
            okButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }
}
