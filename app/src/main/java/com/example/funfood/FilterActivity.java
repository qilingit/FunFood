package com.example.funfood;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class FilterActivity extends Activity {

    Spinner spinnerType;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        initSpinner();

    }

    private void initSpinner() {

        //Récupération des Spinners déclarés dans le fichier activity_filter.xml de res/layout
        spinnerType = findViewById(R.id.spinnerType);


		/*On passe à l'adapate le context, le tableau de string et un fichier de presentation par défaut*/

        /*On passe à l'adapate le context, le tableau de string et un fichier de presentation par défaut*/

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_array, android.R.layout.simple_spinner_item);

        /* On definit une présentation du spinner quand il est déroulé (android.R.layout.simple_spinner_dropdown_item) */
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //Enfin on passe l'adapter au Spinner
        spinnerType.setAdapter(adapter);

    }
}
