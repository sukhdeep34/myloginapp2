package com.app.app2.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.app.app2.LoginActivity;
import com.app.app2.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisterFragment extends Fragment {

    TextInputLayout create_name, create_gender, create_bod, create_city, create_email, create_password, create_confirmPassword;
    Button create_btn, login_btn, btn_calender;
    private FirebaseAuth mFirebaseAuth;

    public RegisterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);

        create_name = v.findViewById(R.id.create_name);
        create_gender = v.findViewById(R.id.create_gender);
        create_bod = v.findViewById(R.id.create_BOD);
        create_city = v.findViewById(R.id.create_city);
        create_email = v.findViewById(R.id.create_email);
        create_password = v.findViewById(R.id.create_password);
        create_confirmPassword = v.findViewById(R.id.create_confirmPassword);
        create_btn = v.findViewById(R.id.create_btn);
        login_btn = v.findViewById(R.id.login_btn);
        btn_calender = v.findViewById(R.id.calender);

        Calendar calender = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calender.clear();

        Long today = MaterialDatePicker.todayInUtcMilliseconds();

        calender.setTimeInMillis(today);

//        final CalendarConstraints.Builder constraint = new CalendarConstraints.Builder();
//        constraint.setValidator(DateValidatorPointForward.now());


        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();

        builder.setTitleText("SELECT A DATE");
//        builder.setCalendarConstraints(constraint.build());
        final MaterialDatePicker materialDatePicker = builder.build();
        builder.setSelection(today);


        btn_calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDatePicker.show(getFragmentManager(), "DATE_PICKER");
            }

        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                create_bod.getEditText().setText(materialDatePicker.getHeaderText());

            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), LoginActivity.class);
                startActivity(i);
            }
        });

        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseAuth = FirebaseAuth.getInstance();
                String Name = create_name.getEditText().getText().toString();
                String Gender = create_gender.getEditText().getText().toString();
                String BOD = create_bod.getEditText().getText().toString();
                String City = create_city.getEditText().getText().toString();
                String Email = create_email.getEditText().getText().toString();
                String Password = create_password.getEditText().getText().toString();
                String ConfirmPassword = create_confirmPassword.getEditText().getText().toString();

                if (Name.isEmpty() || Gender.isEmpty() || BOD.isEmpty() || City.isEmpty() || Email.isEmpty() || Password.isEmpty() || ConfirmPassword.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please Fill The Form", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Password.length() < 6) {

                    Toast.makeText(getActivity().getApplicationContext(), "Password should be 6 characters or long", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Password.equals(ConfirmPassword)) {
                    Toast.makeText(getActivity().getApplicationContext(), "Confirm password doesn't match with password!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isEmailValid(Email)) {
                    Toast.makeText(getActivity().getApplicationContext(), "Please enter valid email", Toast.LENGTH_SHORT).show();
                    return;
                }

                final Map<String, Object> usermap = new HashMap<>();
                usermap.put("Name", Name);
                usermap.put("Gender", Gender);
                usermap.put("BOD", BOD);
                usermap.put("City", City);
                usermap.put("Email", Email);

                mFirebaseAuth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseFirestore db = FirebaseFirestore.getInstance();
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            db.collection("User").document(mFirebaseAuth.getCurrentUser().getUid()).set(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity().getApplicationContext(), "Register Success!", Toast.LENGTH_LONG).show();
                                        Intent i = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                                        startActivity(i);
                                    }
                                }
                            });
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e) {
                                Toast.makeText(getActivity().getApplicationContext(), "Email id already Exist", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(getActivity().getApplicationContext(), "SignUp Unsuccessful, Please Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        return v;
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]+$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}