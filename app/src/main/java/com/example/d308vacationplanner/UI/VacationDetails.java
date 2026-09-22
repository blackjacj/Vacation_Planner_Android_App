package com.example.d308vacationplanner.UI;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.d308vacationplanner.R;
import com.example.d308vacationplanner.database.Repository;
import com.example.d308vacationplanner.entities.Excursion;
import com.example.d308vacationplanner.entities.Vacation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class VacationDetails extends AppCompatActivity {

    Repository repository;
    int vacationID;
    String name;
    String hotel;
    String startDate;
    String endDate;
    EditText editName;
    EditText editHotel;
    Button startDateButton;
    Button endDateButton;
    DatePickerDialog.OnDateSetListener startSetListener;
    DatePickerDialog.OnDateSetListener endSetListener;
    final Calendar myCalendarStart = Calendar.getInstance();
    final Calendar myCalendarEnd = Calendar.getInstance();

    String dateFormat = "MM/dd/yy";
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_vacation_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FloatingActionButton fab=findViewById(R.id.floatingActionButton2);

        editName = findViewById(R.id.vacationName);
        editHotel = findViewById(R.id.hotelName);
        startDateButton = findViewById(R.id.startDate);
        endDateButton = findViewById(R.id.endDate);

        vacationID = getIntent().getIntExtra("vacationID", -1);
        name = getIntent().getStringExtra("name");
        hotel = getIntent().getStringExtra("hotel");
        startDate = getIntent().getStringExtra("startDate");
        endDate = getIntent().getStringExtra("endDate");

        editName.setText(name);
        editHotel.setText(hotel);
        String currentDate = sdf.format(new Date());

        startDateButton.setText(Objects.requireNonNullElse(startDate, currentDate));

        endDateButton.setText(Objects.requireNonNullElse(endDate, currentDate));


        startSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendarStart.set(Calendar.YEAR, year);
                myCalendarStart.set(Calendar.MONTH, month);
                myCalendarStart.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateStartButtonLabel();
            }
        };

        endSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendarEnd.set(Calendar.YEAR, year);
                myCalendarEnd.set(Calendar.MONTH, month);
                myCalendarEnd.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateEndButtonLabel();
            }
        };

        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date;
                String info = startDateButton.getText().toString();
                try {
                    myCalendarStart.setTime(Objects.requireNonNull(sdf.parse(info)));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                new DatePickerDialog(VacationDetails.this, startSetListener, myCalendarStart.get(Calendar.YEAR),
                        myCalendarStart.get(Calendar.MONTH), myCalendarStart.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date;
                String info = endDateButton.getText().toString();
                try {
                    myCalendarEnd.setTime(Objects.requireNonNull(sdf.parse(info)));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                new DatePickerDialog(VacationDetails.this, endSetListener, myCalendarEnd.get(Calendar.YEAR),
                        myCalendarEnd.get(Calendar.MONTH), myCalendarEnd.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(VacationDetails.this, ExcursionDetails.class);

                try {
                    // 1. If it's a brand new vacation, generate a fresh ID just like your menu save button does
                    if (vacationID == -1) {
                        if (repository.getAllVacations().isEmpty()) {
                            vacationID = 1;
                        } else {
                            vacationID = repository.getAllVacations().get(repository.getAllVacations().size() - 1).getVacationID() + 1;
                        }

                        // Construct and INSERT the brand new vacation
                        Vacation vacation = new Vacation(vacationID, editName.getText().toString(), editHotel.getText().toString(), startDate, endDate);
                        repository.insert(vacation);
                    } else {
                        // If it already exists in the database, UPDATE it so edits aren't lost
                        Vacation vacation = new Vacation(vacationID, editName.getText().toString(), editHotel.getText().toString(), startDate, endDate);
                        repository.update(vacation); // Make sure your Repository has an update method!
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                intent.putExtra("vacationID", vacationID);
                intent.putExtra("vacationNameForward", editName.getText().toString());
                intent.putExtra("hotelForward", editHotel.getText().toString());
                startActivity(intent);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.excursionRecyclerView);
        repository = new Repository(getApplication());
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Excursion> filteredExcursions = new ArrayList<>();
        try {
            for (Excursion e : repository.getAllExcursions()) {
                if (e.getVacationID() == vacationID) filteredExcursions.add(e);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        excursionAdapter.setExcursions(filteredExcursions);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Excursion> associatedExcursions;
        try {
            associatedExcursions = repository.getAssociatedExcursions(vacationID);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        RecyclerView recyclerView = findViewById(R.id.excursionRecyclerView);
        final ExcursionAdapter excursionAdapter = new ExcursionAdapter(this);
        recyclerView.setAdapter(excursionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        excursionAdapter.setExcursions(associatedExcursions);
    }

    private void updateStartButtonLabel() {
        String dateFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

        startDateButton.setText(sdf.format(myCalendarStart.getTime()));
        startDate = sdf.format(myCalendarStart.getTime());
    }

    private void updateEndButtonLabel() {
        String dateFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

        endDateButton.setText(sdf.format(myCalendarEnd.getTime()));
        endDate = sdf.format(myCalendarEnd.getTime());
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_vacationdetails, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish(); // Closes this activity and returns to the previous one
            return true;
        }
        if (item.getItemId() == R.id.vacationsave) {
            Vacation vacation;

            if (vacationID == -1) {
                try {
                    if (repository.getAllVacations().isEmpty()) vacationID = 1;
                    else vacationID = repository.getAllVacations().get(repository.getAllVacations().size() - 1).getVacationID() + 1;
                    vacation = new Vacation(vacationID,
                            editName.getText().toString(),
                            editHotel.getText().toString(),
                            startDateButton.getText().toString(),
                            endDateButton.getText().toString());

                    // CHECK IF START DATE IS BEFORE END DATE

                    Date vacayStart = sdf.parse(startDateButton.getText().toString());
                    Date vacayEnd = sdf.parse(endDateButton.getText().toString());

                    if (vacayEnd.before(vacayStart)) {
                        Toast.makeText(VacationDetails.this, "Vacation end date must be after the vacation start date!", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    else {
                        repository.insert(vacation);
                        this.finish();
                    }
                } catch (InterruptedException | ParseException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                try {
                    vacation = new Vacation(vacationID,
                            editName.getText().toString(),
                            editHotel.getText().toString(),
                            startDateButton.getText().toString(),
                            endDateButton.getText().toString());

                    // CHECK IF START DATE IS BEFORE END DATE
                    Date vacayStart = sdf.parse(vacation.getStartDate());
                    Date vacayEnd = sdf.parse(vacation.getEndDate());

                    if (vacayEnd.before(vacayStart)) {
                        Toast.makeText(VacationDetails.this, "Vacation end date must be after the vacation start date!", Toast.LENGTH_LONG).show();
                        return false;
                    }
                    else {
                        try {
                            repository.update(vacation);
                            this.finish();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
            return true;
        }
        if (item.getItemId() == R.id.vacationdelete) {
            if (vacationID == -1) {
                this.finish();
            }
            else {
                Vacation vacation;
                try {
                    if (repository.getAssociatedExcursions(vacationID).isEmpty()) {
                        vacation = new Vacation(vacationID,
                                editName.getText().toString(),
                                editHotel.getText().toString(),
                                startDateButton.getText().toString(),
                                endDateButton.getText().toString());
                        repository.delete(vacation);
                        this.finish();
                    }
                    else {
                        Toast.makeText(VacationDetails.this, "This vacation has excursions associated with it!", Toast.LENGTH_LONG).show();
                        return false;
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (item.getItemId() == R.id.vacationshare) {
            StringBuilder s1;
            try {
                if (repository.getAssociatedExcursions(vacationID).isEmpty()) {
                    s1 = new StringBuilder("Hotel: " + editHotel.getText().toString() + "\n" +
                            "Start Date: " + startDate + "\n" +
                            "End Date: " + endDate + "\n" +
                            "Excursions: None");
                }
                else {
                    s1 = new StringBuilder("Hotel: " + editHotel.getText().toString() + "\n" +
                            "Start Date: " + startDate + "\n" +
                            "End Date: " + endDate + "\n" +
                            "Excursions: ");
                    for (Excursion e: repository.getAssociatedExcursions(vacationID)) {
                        s1.append(e.getExcursionTitle()).append("\n");
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TITLE, editName.getText().toString());
            sendIntent.putExtra(Intent.EXTRA_TEXT, s1.toString());
            sendIntent.setType("text/plain");
            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
            return true;
        }
        if (item.getItemId() == R.id.vacationnotify) {
            String startDateFromScreen = startDateButton.getText().toString();

            Date myStartDate = null;


            try {
                myStartDate = sdf.parse(startDateFromScreen);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            try {
                // ALARM MANAGER
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                // START ALERT
                assert myStartDate != null;
                Long startTrigger = myStartDate.getTime();
                String vacayNameStart = name + " is starting!";
                Intent intentStart = new Intent(VacationDetails.this, MyReceiver.class);
                intentStart.putExtra("key", vacayNameStart);
                PendingIntent senderStart = PendingIntent.getBroadcast(VacationDetails.this, 101, intentStart, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, startTrigger, senderStart);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // END ALERT
            String endDateFromScreen = endDateButton.getText().toString();
            Date myEndDate = null;

            try {
                myEndDate = sdf.parse(endDateFromScreen);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            try {
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                assert myEndDate != null;
                Long endTrigger = myEndDate.getTime();
                String vacayNameEnd = name + " is ending!";
                Intent intentEnd = new Intent(VacationDetails.this, MyReceiver.class);
                intentEnd.putExtra("key", vacayNameEnd);
                PendingIntent senderEnd = PendingIntent.getBroadcast(VacationDetails.this, 102, intentEnd, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, endTrigger, senderEnd);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return true;
    }


}