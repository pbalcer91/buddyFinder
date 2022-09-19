package pl.com.wfiis.android.buddyfinder.DBServices;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telecom.Call;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import pl.com.wfiis.android.buddyfinder.R;
import pl.com.wfiis.android.buddyfinder.models.Event;
import pl.com.wfiis.android.buddyfinder.models.Message;
import pl.com.wfiis.android.buddyfinder.models.User;
import pl.com.wfiis.android.buddyfinder.views.HomeFragment;
import pl.com.wfiis.android.buddyfinder.views.LoginDialog;
import pl.com.wfiis.android.buddyfinder.views.MainActivity;

public class DBServices implements Callback, CallbackEvents {
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore userRef;
    private FirebaseFirestore firebaseRef;
    


    public DBServices() {
        firebaseRef = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void logoutUser(Context context  ){
        firebaseAuth.signOut();

        MainActivity.sharedPreferences = context.getSharedPreferences("login", MODE_PRIVATE);
        MainActivity.sharedPreferencesEditor = MainActivity.sharedPreferences.edit();
        MainActivity.sharedPreferencesEditor.putString("email",  null);
        MainActivity.sharedPreferencesEditor.putString("password",  null);
        MainActivity.sharedPreferencesEditor.commit();
    }

    public void SignInUser(String email, String password,Context context){
        final int[] result = new int[1];

        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email,password).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "asdasf");
                     getUser(getUserId(), new Callback() {
                        @Override
                        public void onCallbackGetUser(User user) {
                            if (user == null)
                                return;

                            MainActivity.currentUser = user;
                            MainActivity.currentUser.setId(getUserId());

                            Toast.makeText(context, "Logged in Successfully", Toast.LENGTH_SHORT).show();

                            MainActivity.sharedPreferences = context.getSharedPreferences("login", MODE_PRIVATE);
                            MainActivity.sharedPreferencesEditor = MainActivity.sharedPreferences.edit();
                            MainActivity.sharedPreferencesEditor.putString("email",  MainActivity.currentUser.getEmail());
                            MainActivity.sharedPreferencesEditor.putString("password",  MainActivity.currentUser.getPassword());
                            MainActivity.sharedPreferencesEditor.commit();
                        }
                    });

                    //TODO maybe delete later
                    getEventsCreatedByUser(new CallbackCreatedEvents() {
                        @Override
                        public void onCallbackGetCreatedEvents(ArrayList<Event> list) {
                            if (!list.isEmpty() && MainActivity.currentUser != null)
                                MainActivity.currentUser.setCreatedEvents(list);
                        }
                    });

                    getEventsJoinedByUser(new CallbackJoinedEvents() {
                        @Override
                        public void onCallbackGetJoinedEvents(ArrayList<Event> list) {
                            if (!list.isEmpty() && MainActivity.currentUser != null)
                                MainActivity.currentUser.setJoinedEvents(list);
                        }
                    });

                    if (MainActivity.bottomSheetDialog != null)
                        MainActivity.bottomSheetDialog.dismiss();

                    if (MainActivity.homeFragment != null)
                        MainActivity.showHomeViewSignIn();

                } else{
                    Toast.makeText(context, "Incorrect email or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public String getUserId(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        return user.getUid();
    }

    public void registerUser(String email, String username, String password, Context context) {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isComplete()) {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    MainActivity.successfulRegister(context);
                                Map<String, Object> map = new HashMap<>();
                                map.put("uid", firebaseUser.getUid());
                                map.put("username", username);
                                map.put("email", email);
                                map.put("password", password);

                                firebaseRef.collection("Users").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                Log.d(TAG, "DocumentSnapshot written  " );
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
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("TAG", "onFailure: Email not sent " + e.getMessage());
                            }
                        });
                            }

                    }
            });
        }

    public void updateUserData(String valueName ,String value) {
         firebaseRef.collection("Users").whereEqualTo("uid",getUserId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<QuerySnapshot> task) {
                         if(task.isSuccessful()) {
                             for(QueryDocumentSnapshot document: task.getResult()){
                                 firebaseRef.collection("Users").document(document.getId()).update(valueName,value);
                             }
                             if (valueName.equals("email")) {
                                 firebaseAuth.getCurrentUser().updateEmail(value).addOnCompleteListener(new OnCompleteListener<Void>() {
                                     @Override
                                     public void onComplete(@NonNull Task<Void> task) {
                                         if (task.isSuccessful()) {
                                             Log.d(TAG, "User email address updated.");
                                         }
                                     }
                                 });
                             } else if (valueName.equals("password")) {
                                 firebaseAuth.getCurrentUser().updatePassword(value).addOnCompleteListener(new OnCompleteListener<Void>() {
                                     @Override
                                     public void onComplete(@NonNull Task<Void> task) {
                                         if (task.isSuccessful()) {
                                             Log.d(TAG, "User password updated.");
                                         }
                                     }
                                 });
                             }
                         }
                 }
    });
    }

    public void updatePassword(String newPassword, String uid){
        //TODO BASE64 encoding/decoding
        DocumentReference userRefe = firebaseRef.collection("Users").document(uid);
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
        firebaseRef.collection("Users").document(uid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
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

    public void getUser(String uid, Callback callback) {
        final User[] user = new User[1];
        firebaseRef.collection("Users").whereEqualTo("uid",uid).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                for(QueryDocumentSnapshot snapshot : task.getResult()) {
                    if (snapshot.exists()) {
                        Log.d(TAG, snapshot.getId() + " => " + snapshot.getData());
                        user[0] = snapshot.toObject(User.class);
                        user[0].setUserName(snapshot.get("username").toString());
                    }
            }
                callback.onCallbackGetUser(user[0]);
        }});


//                .addOnCompleteListener(new ){
//            @Override
//            public void onC(QuerySnapshot queryDocumentSnapshots) {
//                if(!queryDocumentSnapshots.isEmpty()){
//                    for(DocumentSnapshot snapshot : queryDocumentSnapshots) {
//                        if (snapshot.exists()) {
//                            Log.d(TAG, snapshot.getId() + " => " + snapshot.getData());
//                            user[0] = snapshot.toObject(User.class);
//                        }
//                        else {
//                            Log.d(TAG, "xcbvbbb");
//                        }
//                    }
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        System.out.println("err");
//                    }
//                });
 //      return user[0];


     //   return user[0];
    }

    public void getEventsCreatedByUser(CallbackCreatedEvents callback){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ArrayList <Event> list = new ArrayList<>();
        Task<QuerySnapshot> query = firebaseRef.collection("Events").whereEqualTo("author.id",user.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                        for(DocumentSnapshot snapshot : task.getResult()) {
                            if(snapshot.exists()){
                            list.add(snapshot.toObject(Event.class));
                        }
                    }
                    callback.onCallbackGetCreatedEvents(list);
                }
            }
        });
    }


    public void getEventsJoinedByUser(CallbackJoinedEvents callback){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ArrayList <Event> list = new ArrayList<>();
        Task<QuerySnapshot> query = firebaseRef.collection("Events").whereArrayContains("members",user.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    for(DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        list.add(snapshot.toObject(Event.class));
                    }
                }
                callback.onCallbackGetJoinedEvents(list);
            }
    });
    }


    public void createEvent(Event event){
        Map<String, Object> map = new HashMap<>();
        map.put("title", event.getTitle());
        map.put("description", event.getDescription());
        map.put("location", event.getLocation());
        map.put("author", event.getAuthor());
        map.put("title", event.getTitle());
        map.put("members",event.getMembers());

        firebaseRef.collection("Events").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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
        DocumentReference eventRef = firebaseRef.collection("Events").document(eventId);
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
        DocumentReference eventRef = firebaseRef.collection("Events").document(eventId);
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

        DocumentReference userRef = firebaseRef.collection("Users").document(uid);
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
        DocumentReference eventRef = firebaseRef.collection("Events").document(eventId);
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

        DocumentReference userRef = firebaseRef.collection("Users").document(uid);
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
        firebaseRef.collection("Events").document(eventId).delete()
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

    public List<Message> getEventMessages(String id){
        List<Message> list = new ArrayList<>();
        Task<QuerySnapshot> query = firebaseRef.collection("GroupChat").whereArrayContains("groupId", id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    for(DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        list.add(snapshot.toObject(Message.class));
                    }
                }
            }
        });
        return list;
    }

    public void addMessage(Message message){
        Map<String, Object> map = new HashMap<>();
        map.put("eventId", message.getEventId());
        map.put("senderId", message.getSenderId());
        map.put("message", message.getMessage());
        map.put("date", message.getDate());

        firebaseRef.collection("GroupChat").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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

    public void getAllEvents(CallbackEvents callback){
        ArrayList<Event> list = new ArrayList<>();
        Task<QuerySnapshot> query = firebaseRef.collection("Events").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    for(DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        list.add(snapshot.toObject(Event.class));
                    }
                    callback.onCallbackGetAllEvents(list);
                }
            }
        });

    }

    public void isEmailInDB(String email, CallbackIsEmailInDB callbackIsEmailInDB) {
        firebaseRef.collection("Users").whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    callbackIsEmailInDB.onCallbackIsEmailInDB(!task.getResult().isEmpty());
                }
            }
        });
    }
          
    @Override
    public void onCallbackGetUser(User user) {
    }
    @Override
    public void onCallbackGetAllEvents(ArrayList<Event> list) {
    }
    public interface CallbackJoinedEvents{
         void onCallbackGetJoinedEvents(ArrayList<Event>list);
    }
    public interface CallbackCreatedEvents{
        void onCallbackGetCreatedEvents(ArrayList<Event>list);
    }
    public interface CallbackIsEmailInDB{
        void onCallbackIsEmailInDB(boolean result);
    }
}
