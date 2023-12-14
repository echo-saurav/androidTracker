package com.ironman.tracker.viewHolder;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ironman.tracker.R;
import com.ironman.tracker.database.Firebase;
import com.ironman.tracker.utills.OnMyCompleteListener;
import com.ironman.tracker.valueHolder.RequestValueHolder;
import com.ironman.tracker.valueHolder.UserValueHolder;

import static com.ironman.tracker.database.Firebase.acceptRequest;
import static com.ironman.tracker.database.Firebase.cancelRequest;

public class RequestViewHolder extends RecyclerView.ViewHolder {

    public RequestViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setView(RequestValueHolder requestValueHolder){
        if(requestValueHolder!=null){
            ((TextView)itemView.findViewById(R.id.time)).setText(requestValueHolder.timestamp.toString());

            DocumentReference documentReference= FirebaseFirestore.getInstance()
                    .collection("users").document(requestValueHolder.uid);
            documentReference.get().addOnCompleteListener(task -> {
                if(task.isSuccessful() && task.getResult()!=null){
                    UserValueHolder userValueHolder=task.getResult().toObject(UserValueHolder.class);
                    if(userValueHolder!=null)
                        ((TextView)itemView.findViewById(R.id.title)).setText(String.format("You got a request from %s", userValueHolder.userName));
                }
            });

            itemView.findViewById(R.id.cancel_button).setOnClickListener(v -> cancelRequest(requestValueHolder.uid, new OnMyCompleteListener() {
                @Override
                public void success() {
                    Toast.makeText(itemView.getContext(),"Request canceled",Toast.LENGTH_LONG).show();
                }

                @Override
                public void error(String error) {
                    Toast.makeText(itemView.getContext(),"Error "+error,Toast.LENGTH_LONG).show();
                }
            }));

            itemView.findViewById(R.id.accept_button).setOnClickListener(v -> {
                Toast.makeText(itemView.getContext(),"Please wait your request is processing....",Toast.LENGTH_LONG).show();
                Firebase.acceptRequest(requestValueHolder.uid, new OnMyCompleteListener() {
                    @Override
                    public void success() {
                        Toast.makeText(itemView.getContext(),"This will be removed, after request send",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void error(String error) {
                        Toast.makeText(itemView.getContext(),"Error "+error,Toast.LENGTH_LONG).show();
                    }
                });
            });

        }
    }

}
