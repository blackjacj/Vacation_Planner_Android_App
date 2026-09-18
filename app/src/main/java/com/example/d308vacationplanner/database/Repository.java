package com.example.d308vacationplanner.database;

import android.app.Application;

import com.example.d308vacationplanner.dao.ExcursionDAO;
import com.example.d308vacationplanner.dao.VacationDAO;
import com.example.d308vacationplanner.entities.Excursion;
import com.example.d308vacationplanner.entities.Vacation;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {

    private ExcursionDAO mExcursionDAO;
    private VacationDAO mVacationDAO;

    private List<Vacation> mAllVacations;
    private List<Excursion> mAllExcursions;

    private static int NUMBER_OF_THREADS=4;
    static final ExecutorService databaseExecutor= Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public Repository(Application application){
        vacationDatabaseBuilder db=vacationDatabaseBuilder.getDatabase(application);
        mExcursionDAO = db.excursionDAO();
        mVacationDAO = db.vacationDAO();
    }

    // VACATIONS
    public List<Vacation>getAllVacations() throws InterruptedException {
        databaseExecutor.execute(()->{
            mAllVacations=mVacationDAO.getAllVacations();
        });

        Thread.sleep(1000);

        return mAllVacations;
    }

    public void insert(Vacation vacation) throws InterruptedException {
        databaseExecutor.execute(()->{
            mVacationDAO.insert(vacation);
        });

        Thread.sleep(1000);
    }

    public void update(Vacation vacation) throws InterruptedException {
        databaseExecutor.execute(()->{
            mVacationDAO.update(vacation);
        });

        Thread.sleep(1000);
    }

    public void delete(Vacation vacation) throws InterruptedException {
        databaseExecutor.execute(()->{
            mVacationDAO.delete(vacation);
        });

        Thread.sleep(1000);
    }

    // EXCURSIONS

    public List<Excursion>getAllExcursions() throws InterruptedException {
        databaseExecutor.execute(()->{
            mAllExcursions=mExcursionDAO.getAllExcursions();
        });

        Thread.sleep(1000);

        return mAllExcursions;
    }

    public List<Excursion>getAssociatedExcursions(int VacationID) throws InterruptedException {
        databaseExecutor.execute(()->{
            mAllExcursions=mExcursionDAO.getAssociatedExcursions(VacationID);
        });

        Thread.sleep(1000);

        return mAllExcursions;
    }

    public void insert(Excursion excursion) throws InterruptedException {
        databaseExecutor.execute(()->{
            mExcursionDAO.insert(excursion);
        });

        Thread.sleep(1000);
    }

    public void update(Excursion excursion) throws InterruptedException {
        databaseExecutor.execute(()->{
            mExcursionDAO.update(excursion);
        });

        Thread.sleep(1000);
    }

    public void delete(Excursion excursion) throws InterruptedException {
        databaseExecutor.execute(()->{
            mExcursionDAO.delete(excursion);
        });

        Thread.sleep(1000);
    }

}
