package com.app.app2.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.app.app2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;


public class ProfileFragment extends Fragment {

    TextView text_name, text_gender, text_bod, text_city, text_email;
    private FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        text_name = view.findViewById(R.id.text_name);
        text_gender = view.findViewById(R.id.text_gender);
        text_bod = view.findViewById(R.id.text_bod);
        text_city = view.findViewById(R.id.text_city);
        text_email = view.findViewById(R.id.text_email);

        getUserData();

        return view;
    }

    private void getUserData() {
        auth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = auth.getCurrentUser();
        final String id = firebaseUser.getUid();
        Log.v("tagvv", " " + id);
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("User").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data2 = document.getData();
                        String Name = data2.get("Name").toString();
                        String Email = data2.get("Email").toString();
                        String Gender = data2.get("Gender").toString();
                        String BOD = data2.get("BOD").toString();
                        String City = data2.get("City").toString();
                        Log.d("tagvv", "DocumentSnapshot data: " + data2);
                        text_name.setText(Name);
                        text_email.setText(Email);
                        text_gender.setText(Gender);
                        text_bod.setText(BOD);
                        text_city.setText(City);


                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

    }


}