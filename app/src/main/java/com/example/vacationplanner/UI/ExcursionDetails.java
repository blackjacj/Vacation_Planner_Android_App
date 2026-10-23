package com.example.vacationplanner.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vacationplanner.R;
import com.example.vacationplanner.database.Repository;
import com.example.vacationplanner.entities.Excursion;
import com.example.vacationplanner.entities.Vacation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ExcursionDetails extends AppCompatActivity {
    String name;
    String date;
    String vacayStart;
    String vacayEnd;
    int excursionID;
    int vacationID;
    EditText editName;
    Button dateButton;
    Repository repository;
    DatePickerDialog.OnDateSetListener setListener;
    final Calendar myCalender = Calendar.getInstance();
    String dateFormat = "MM/dd/yy";
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_excursion_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        repository = new Repository(getApplication());
        name = getIntent().getStringExtra("name");
        date = getIntent().getStringExtra("date");
        vacayStart = getIntent().getStringExtra("startDate");
        vacayEnd = getIntent().getStringExtra("endDate");

        editName = findViewById(R.id.excursionName);
        dateButton = findViewById(R.id.excursionDate);

        String currentDate = sdf.format(new Date());
        editName.setText(name);
        dateButton.setText(Objects.requireNonNullElse(date, currentDate));

        excursionID = getIntent().getIntExtra("excursionID", -1);
        vacationID = getIntent().getIntExtra("vacationID", -1);

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalender.set(Calendar.YEAR, year);
                myCalender.set(Calendar.MONTH, month);
                myCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateButtonLabel();
            }
        };

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = dateButton.getText().toString();
                try {
                    myCalender.setTime(Objects.requireNonNull(sdf.parse(info)));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                new DatePickerDialog(ExcursionDetails.this, setListener, myCalender.get(Calendar.YEAR),
                        myCalender.get(Calendar.MONTH), myCalender.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void updateButtonLabel() {
        String dateFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

        dateButton.setText(sdf.format(myCalender.getTime()));
        date = sdf.format(myCalender.getTime());
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_excursiondetails, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        if (item.getItemId() == R.id.excursionsave) {
            Excursion excursion;

            if (excursionID == -1) {
                try {
                    if (repository.getAllExcursions().isEmpty()) excursionID = 1;
                    else excursionID = repository.getAllExcursions().get(repository.getAllExcursions().size() - 1).getExcursionID() + 1;
                    excursion = new Excursion(excursionID,
                            editName.getText().toString(),
                            dateButton.getText().toString(),
                            vacationID);

                    // CHECK IF EXCURSION DATE IS BETWEEN START AND END DATE OF ASSOCIATED VACATION
                    int index = vacationID - 1;
                    if (index < 0) {
                        index = 0;
                    }
                    Vacation associatedVacation = repository.getAllVacations().get(index);
                    Date exDate = sdf.parse(dateButton.getText().toString());
                    Date vacationStart = sdf.parse(associatedVacation.getStartDate());
                    Date vacationEnd = sdf.parse(associatedVacation.getEndDate());

                    if (exDate.before(vacationStart) || exDate.after(vacationEnd)) {
                        Toast.makeText(ExcursionDetails.this, "Excursion date must be within associated vacation date!", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    else {
                        repository.insert(excursion);
                        this.finish();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                excursion = new Excursion(excursionID,
                        editName.getText().toString(),
                        dateButton.getText().toString(),
                        vacationID);
                try {
                    // CHECK IF EXCURSION DATE IS BETWEEN START AND END DATE OF ASSOCIATED VACATION
                    int index = vacationID - 1;
                    if (index < 0) {
                        index = 0;
                    }
                    Vacation associatedVacation = repository.getAllVacations().get(index);
                    Date exDate = sdf.parse(dateButton.getText().toString());
                    Date vacationStart = sdf.parse(associatedVacation.getStartDate());
                    Date vacationEnd = sdf.parse(associatedVacation.getEndDate());

                    if (exDate.before(vacationStart) || exDate.after(vacationEnd)) {
                        Toast.makeText(ExcursionDetails.this, "Excursion date must be within associated vacation date!", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    else {
                        repository.update(excursion);
                        this.finish();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
            return true;
        }
        if (item.getItemId() == R.id.excursiondelete) {
            if (excursionID == -1) {
                this.finish();
            }
            else {
                Excursion excursion;
                excursion = new Excursion(excursionID,
                        editName.getText().toString(),
                        dateButton.getText().toString(),
                        vacationID);
                try {
                    repository.delete(excursion);
                    this.finish();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (item.getItemId() == R.id.excursionnotify) {
            String dateFromScreen = dateButton.getText().toString();

            Date myDate = null;

            try {
                myDate = sdf.parse(dateFromScreen);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            try {
                // ALARM MANAGER
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                // START ALERT
                assert myDate != null;
                Long trigger = myDate.getTime();
                String excursionNameStart = name + " is starting!";
                Intent intent = new Intent(ExcursionDetails.this, MyReceiver.class);
                intent.putExtra("key", excursionNameStart);
                PendingIntent sender = PendingIntent.getBroadcast(ExcursionDetails.this, 103, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, trigger, sender);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return true;

        }
        return true;
    }
}