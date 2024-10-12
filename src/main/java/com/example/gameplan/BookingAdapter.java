package com.example.gameplan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    private List<Booking> bookingList;
    private Context context;
    private DatabaseReference bookingsRef; // Add this reference
    public BookingAdapter(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }
    public BookingAdapter(List<Booking> bookingList, DatabaseReference bookingsRef, Context context) {
        this.bookingList = bookingList;
        this.context = context;
        this.bookingsRef = bookingsRef; // Initialize in constructor
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.textViewCourtId.setText(booking.getCourtId());
        holder.textViewDate.setText(booking.getDate());
        holder.textViewTimeSlot.setText(booking.getTimeSlot());
        holder.textViewSportName.setText(booking.getsportName());

        // Show cancel button only if bookings are present
        if (booking != null) {
            holder.buttonCancelBooking.setVisibility(View.VISIBLE);
            holder.buttonCancelBooking.setOnClickListener(view -> cancelBooking(booking.getBookingId(), position));
        } else {
            holder.buttonCancelBooking.setVisibility(View.GONE);
        }
    }

    // Helper method to cancel a booking
    private void cancelBooking(String bookingId, int position) {
        Log.d("BookingAdapter", "Attempting to cancel booking: " + bookingId);
        bookingsRef.child(bookingId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("BookingAdapter", "Booking cancelled: " + bookingId);
                bookingList.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "Booking cancelled successfully.", Toast.LENGTH_SHORT).show();
            } else {
                Log.e("BookingAdapter", "Failed to cancel booking: " + bookingId);
                Toast.makeText(context, "Failed to cancel booking.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewCourtId;
        TextView textViewDate;
        TextView textViewTimeSlot;
        TextView textViewSportName;
        Button buttonCancelBooking;

        @SuppressLint("WrongViewCast")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCourtId = itemView.findViewById(R.id.textViewCourtId);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewTimeSlot = itemView.findViewById(R.id.textViewTimeSlot);
            textViewSportName = itemView.findViewById(R.id.textViewSportName);
            buttonCancelBooking = itemView.findViewById(R.id.buttonCancelBooking);
        }
    }
}
