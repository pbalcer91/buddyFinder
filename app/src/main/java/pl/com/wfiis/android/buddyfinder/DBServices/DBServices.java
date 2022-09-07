package pl.com.wfiis.android.buddyfinder.DBServices;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.com.wfiis.android.buddyfinder.models.Event;
import pl.com.wfiis.android.buddyfinder.models.User;

public class DBServices {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore userRef;
   // private DatabaseReference userReference;
    //private DatabaseReference eventReference;
    private FirebaseFirestore eventReference;
    private DatabaseReference groupChatReference;


    public DBServices() {
       eventReference = FirebaseFirestore.getInstance();
    }

    public void registerUser(String email, String username, String password, Context context) {
        //TODO TALK ABOUT SEDING CONTEXT
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // TODO User is signed in
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password);
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(context, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("TAG", "onFailure: Email not sent " + e.getMessage());
                }
            });

            Map<String, Object> map = new HashMap<>();
            map.put("username", username);
            map.put("email", email);
            map.put("password", password);

            userRef.collection("Users").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
        }
    }

    public void updateUserData(String uid, String valueName ,Object value) {
        DocumentReference userRefe = userRef.collection("Users").document(uid);
        userRefe.update(valueName, value).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully updated!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("email", newEmail);
//            map.put("username",newUsername);
//            map.put("password",dbUser.getPassword());
//
//            userReference.child(user.getUid()).updateChildren(map);
//        } else {
//            //TODO USER IS NOT LOGGED
//        }
    }

    public void updatePassword(String newPassword, String uid){
        //TODO BASE64 encoding/decoding
        DocumentReference userRefe = userRef.collection("Users").document(uid);
        userRefe.update("password", newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    private void deleteUser(String uid) {
        userRef.collection("Users").document(uid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully deleted!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });

    }

    public User getUser(String uid) {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        final User[] modelUser = new User[1];
//        if (user != null) {
//            String userid = user.getUid();
//            userReference.child(userid).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if (snapshot.exists()) {
//                        String currentNickname = snapshot.child("username").toString();
//                        String currentEmail = snapshot.child("email").toString();
//                        String currentPassword = snapshot.child("password").toString();
//                        modelUser[0] = new User(currentNickname, currentEmail, currentPassword);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//                    //TODO onCancelled
//                }
//            });
//        }
       // return modelUser[0];


        final User[] user = new User[1];
        userRef.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                user[0] = task.getResult().toObject(User.class);
            }
        });
        return user[0];
    }

    public List<Event> getEventsCreatedByUser(String uid){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        List <Event> list = new ArrayList<>();
        Task<QuerySnapshot> query = eventReference.collection("Events").whereEqualTo("author",user.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                for(DocumentSnapshot snapshot : queryDocumentSnapshots) {
                    list.add(snapshot.toObject(Event.class));
                  }
                }
            }
        });
        return list;
    }


    public List<Event> getEventsJoinedByUser(String uid){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        List <Event> list = new ArrayList<>();
        Task<QuerySnapshot> query = eventReference.collection("Events").whereArrayContains("members",user.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    for(DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        list.add(snapshot.toObject(Event.class));
                    }
                }
            }
    });
        return list;
    }


    public void createEvent(Event event){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Map<String, Object> map = new HashMap<>();
        map.put("title", event.getTitle());
        map.put("description", event.getDescription());
        map.put("location", event.getLocation());
        map.put("author", user.getUid());
        map.put("title", event.getTitle());
        if(event.getMembers().isEmpty()){
            event.addMember(getUser(user.getUid()));
        }
        map.put("members",event.getMembers());

        eventReference.collection("Events").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void updateEvent(String eventId, String valueName ,Object value){
        DocumentReference eventRef = eventReference.collection("Events").document(eventId);
        eventRef.update(valueName, value).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
        });
    }

    public void addUserToEvent(String eventId, String uid){
        DocumentReference eventRef = eventReference.collection("Events").document(eventId);
        eventRef.update("members", FieldValue.arrayUnion(uid)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

        DocumentReference userRef = eventReference.collection("Users").document(uid);
        userRef.update("joinedEvents", FieldValue.arrayUnion(eventId)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }


    public void deleteUserFromEvent(String eventId, String uid){
        DocumentReference eventRef = eventReference.collection("Events").document(eventId);
        eventRef.update("members", FieldValue.arrayRemove(uid)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });

        DocumentReference userRef = eventReference.collection("Users").document(uid);
        userRef.update("joinedEvents", FieldValue.arrayRemove(eventId)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    public void deleteEvent(String eventId) {
        eventReference.collection("Events").document(eventId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

}
