package com.ttl.ritz7chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    CardView profile_card, card_request, card_logout, card_about;
    FirebaseAuth mAuth;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialise();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);
        profile_card = (CardView) view.findViewById(R.id.profile_card);
        card_request = (CardView) view.findViewById(R.id.request_card);
        card_about = (CardView) view.findViewById(R.id.about_card);
        card_logout = (CardView) view.findViewById(R.id.logout_card);
        setListener();
        return view;
    }

    private void initialise() {

        mAuth = FirebaseAuth.getInstance();

    }

    private void setListener() {

        profile_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(),ProfileActivity.class));
            }
        });
        card_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(requireContext(),loginActivity.class));
            }
        });

    }

}