package com.example.d308vacationplanner.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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

import java.util.ArrayList;
import java.util.List;

public class VacationDetails extends AppCompatActivity {

    Repository repository;
    int vacationID;
    String name;
    String hotel;
    String startDate;
    String endDate;
    EditText editName;
    EditText editHotel;
    EditText editStartDate;
    EditText editEndDate;

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
        editStartDate = findViewById(R.id.startDate);
        editEndDate = findViewById(R.id.endDate);

        vacationID = getIntent().getIntExtra("vacationID", -1);
        name = getIntent().getStringExtra("name");
        hotel = getIntent().getStringExtra("hotel");
        startDate = getIntent().getStringExtra("startDate");
        endDate = getIntent().getStringExtra("endDate");

        editName.setText(name);
        editHotel.setText(hotel);
        editStartDate.setText(startDate);
        editEndDate.setText(endDate);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(VacationDetails.this, ExcursionDetails.class);
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
                            editStartDate.getText().toString(),
                            editEndDate.getText().toString());
                    repository.insert(vacation);
                    this.finish();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                vacation = new Vacation(vacationID,
                        editName.getText().toString(),
                        editHotel.getText().toString(),
                        editStartDate.getText().toString(),
                        editEndDate.getText().toString());
                try {
                    repository.update(vacation);
                    this.finish();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
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
                                editStartDate.getText().toString(),
                                editEndDate.getText().toString());
                        repository.delete(vacation);
                        this.finish();
                    }
                    else {
                        this.finish();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return true;
    }


}