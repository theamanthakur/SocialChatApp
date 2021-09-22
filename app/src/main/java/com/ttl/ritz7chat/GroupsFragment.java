package com.ttl.ritz7chat;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupsFragment extends Fragment {

    private View groupFragmentView;
    RecyclerView recyclerView;
    FirebaseAuth mAuth;
    FirebaseUser user;
    private AdapterGroupList adapterGroupList;
    private ArrayList<modelGroupsList> groupList;
    DatabaseReference groupRef;
    String currentUser="";

    public GroupsFragment() {
        // Required empty public constructor
    }
    public static GroupsFragment newInstance(String param1, String param2) {
        GroupsFragment fragment = new GroupsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        groupFragmentView = inflater.inflate(R.layout.fragment_groups, container, false);
        groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");
        recyclerView = groupFragmentView.findViewById(R.id.rvGroupList);
//       currentUser = user.getUid();
        mAuth = FirebaseAuth.getInstance();

        loadGroupList();
        return groupFragmentView;
    }

    private void loadGroupList() {
        groupList = new ArrayList<>();
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupList.size();
                groupList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    if (ds.child("Participants").child(mAuth.getCurrentUser().getUid()).exists()){
                        modelGroupsList model = ds.getValue(modelGroupsList.class);
                        groupList.add(model);
                    }
                }
                adapterGroupList = new AdapterGroupList(getActivity(),groupList);
                recyclerView.setAdapter(adapterGroupList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}