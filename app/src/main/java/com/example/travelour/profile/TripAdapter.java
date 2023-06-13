package com.example.travelour.profile;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelour.R;

import java.util.ArrayList;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewAdapter> {

    private Context context;
    private ArrayList<TripData> list;

    public TripAdapter(Context context, ArrayList<TripData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public TripViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.trip_cretaed, parent, false);
        return new TripViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewAdapter holder, int position) {
        TripData currItem = list.get(position);

        holder.tripname.setText("Trip name: "+currItem.getTripName());
        holder.tripstart.setText("Trip start: "+currItem.getTripStart());
        holder.tripend.setText("Destination: "+currItem.getTripEnd());
        holder.tripdate.setText("Trip date: "+currItem.getDate());
        holder.tripmembers.setText("Member's count: "+currItem.getMemberscount());


        holder.updateTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,update_your_trip.class);
                intent.putExtra("tripName",currItem.getTripName());
                intent.putExtra("tripStart",currItem.getTripStart());
                intent.putExtra("tripEnd",currItem.getTripEnd());
                intent.putExtra("memberscount",currItem.getMemberscount());
                intent.putExtra("date",currItem.getDate());
                intent.putExtra("key",currItem.getKey());

                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public class TripViewAdapter extends RecyclerView.ViewHolder {


        private TextView tripname,tripstart,tripend,tripdate,tripmembers;
        private Button updateTrip;

        public TripViewAdapter(@NonNull View itemView) {
            super(itemView);
            tripname = itemView.findViewById(R.id.triptitle);
            tripstart = itemView.findViewById(R.id.start);
            tripend = itemView.findViewById(R.id.end);
            tripdate = itemView.findViewById(R.id.tripDate);
            tripmembers=itemView.findViewById(R.id.count);
            updateTrip=itemView.findViewById(R.id.update);
        }
    }
}
