package in.codersclub.todolistftfirebase;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import in.codersclub.todolistftfirebase.Model.ToDoModel;
import in.codersclub.todolistftfirebase.ToDoAdapter.ToDoAdapter;

public class MainActivity extends AppCompatActivity implements OnDialogCloseListener{
    private RecyclerView mRecyclerView;
    private FloatingActionButton fab;
    private List<ToDoModel> mlist;
    private ToDoAdapter adapter;
    private FirebaseFirestore firestore;
    private Query query;
    private ListenerRegistration listenerRegistration;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recycleView);
         fab= findViewById(R.id.Add);
        firestore= FirebaseFirestore.getInstance();

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager() , AddNewTask.TAG);
            }
        });


        mRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager() , AddNewTask.TAG);
            }
        });
         mlist = new ArrayList<>();
        adapter = new ToDoAdapter(MainActivity.this , mlist);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        showData();
        mRecyclerView.setAdapter(adapter);
    }
    private void showData(){
        query = firestore.collection("task").orderBy("time" , Query.Direction.DESCENDING);

        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange documentChange : value.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED){
                        String id = documentChange.getDocument().getId();
                        ToDoModel toDoModel = documentChange.getDocument().toObject(ToDoModel.class).withId(id);
                        mlist.add(toDoModel);
                        adapter.notifyDataSetChanged();
                    }
                }
                listenerRegistration.remove();
            }
        });
    }


    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        mlist.clear();
        showData();
        adapter.notifyDataSetChanged();
    }
}