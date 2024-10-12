package com.example.gameplan;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditEvents extends AppCompatActivity implements EventAdapter.OnDeleteClickListener, EventAdapter.OnModifyClickListener {
    private RecyclerView recyclerViewEvents;
    private List<Event> eventList;
    private EventAdapter eventAdapter;
    private static final int IMAGE_PICKER_REQUEST_CODE = 100;
    private Event currentEvent;
    private ImageView imageViewEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_events);

        recyclerViewEvents = findViewById(R.id.recyclerViewEvents);
        recyclerViewEvents.setLayoutManager(new LinearLayoutManager(this));

        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList, this, this);
        recyclerViewEvents.setAdapter(eventAdapter);

        fetchEventsFromDatabase();

        Button back = findViewById(R.id.Back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditEvents.this, Admin_Dashboard.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void fetchEventsFromDatabase() {
        FirebaseDatabase.getInstance().getReference("Events")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        eventList.clear();
                        for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                            Event event = eventSnapshot.getValue(Event.class);
                            if (event != null) {
                                eventList.add(event);
                            }
                        }
                        // Notify adapter after adding all events
                        eventAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle error
                    }
                });
    }

    @Override
    public void onDeleteClick(Event event) {
        // Handle delete click event
        showDeleteConfirmationDialog(event);
    }

    @Override
    public void onModifyClick(Event event) {
        // Handle modify click event
        currentEvent = event;
        showModifyEventDialog(event);
    }

    private void showDeleteConfirmationDialog(final Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete this event?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Delete the event
                deleteEvent(event);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void deleteEvent(Event event) {
        FirebaseDatabase.getInstance().getReference("Events").child(event.getEventId())
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Event deleted successfully
                        Toast.makeText(EditEvents.this, "Event deleted", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditEvents.this,Admin_Dashboard.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to delete event
                        Toast.makeText(EditEvents.this, "Failed to delete event", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showModifyEventDialog(final Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_edit_event, null);
        builder.setView(dialogView);

        final EditText editTextTitle = dialogView.findViewById(R.id.editTextTitle);
        final EditText editTextDescription = dialogView.findViewById(R.id.editTextDescription);
        final EditText editTextTime = dialogView.findViewById(R.id.editTextTime);
        final EditText editTextDate = dialogView.findViewById(R.id.editTextDate);
        final EditText editTextLocation = dialogView.findViewById(R.id.editTextLocation);
        Button buttonSelectImage = dialogView.findViewById(R.id.buttonSelectImage);

        editTextTitle.setText(event.getTitle());
        editTextDescription.setText(event.getDescription());
        editTextLocation.setText(event.getLocation());

        // Set current date to the EditText field
        editTextDate.setText(event.getDate());

        // Set up Calendar for the DatePickerDialog
        final Calendar calendar = Calendar.getInstance();

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditEvents.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, monthOfYear);
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                editTextDate.setText(sdf.format(calendar.getTime()));
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        // Set up Calendar for the TimePickerDialog
        final Calendar timeCalendar = Calendar.getInstance();

        editTextTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = timeCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = timeCalendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(EditEvents.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                timeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                timeCalendar.set(Calendar.MINUTE, minute);
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                editTextTime.setText(sdf.format(timeCalendar.getTime()));
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_PICKER_REQUEST_CODE);
            }
        });

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String updatedTitle = editTextTitle.getText().toString();
                String updatedDescription = editTextDescription.getText().toString();
                String updatedTime = editTextTime.getText().toString();
                String updatedDate = editTextDate.getText().toString();
                String updatedLocation = editTextLocation.getText().toString();

                event.setTitle(updatedTitle);
                event.setDescription(updatedDescription);
                event.setTime(updatedTime);
                event.setDate(updatedDate);
                event.setLocation(updatedLocation);

                FirebaseDatabase.getInstance().getReference("Events").child(event.getEventId())
                        .setValue(event)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditEvents.this, "Event details updated", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditEvents.this, "Failed to update event details", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            uploadImageToFirebaseStorage(selectedImageUri);
        }
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + System.currentTimeMillis() + ".jpg");

        storageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imageUrl = uri.toString();
                                currentEvent.setImageUrl(imageUrl);
                                Glide.with(EditEvents.this).load(imageUrl).into(imageViewEvent);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditEvents.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
