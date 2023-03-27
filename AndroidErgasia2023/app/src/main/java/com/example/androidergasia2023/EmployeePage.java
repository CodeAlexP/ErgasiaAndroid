package com.example.androidergasia2023;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class EmployeePage extends AppCompatActivity {
    ArrayList<ArrayList<String>> CompleteData;
    ArrayList<ArrayList<String>> GeneralData;
    ArrayList<ArrayList<String>> generaldata1;
    ArrayList<ArrayList<String>> generaldata2;
    ArrayList<ArrayList<String>> generaldata3;
    ArrayList<ArrayList<String>> generaldata4;
    ArrayList<ArrayList<String>> generaldata5;
    RecyclerView recyclerView;
    ArrayList<Data> list;
    ArrayList<String> listKeys;
    ArrayList<ArrayList<String>> fire;
    ArrayList<ArrayList<ArrayList<String>>> locFire;
    ArrayList<ArrayList<ArrayList<String>>> completeFire;
    ArrayList<ArrayList<String>> floods;
    ArrayList<ArrayList<ArrayList<String>>> locFloods;
    ArrayList<ArrayList<ArrayList<String>>> completeFloods;
    ArrayList<ArrayList<String>> earthquake;
    ArrayList<ArrayList<ArrayList<String>>> locEarthquake;
    ArrayList<ArrayList<ArrayList<String>>> completeEarthquake;
    ArrayList<ArrayList<String>> tornado;
    ArrayList<ArrayList<ArrayList<String>>> locTornado;
    ArrayList<ArrayList<ArrayList<String>>> completeTornado;
    ArrayList<ArrayList<String>> othernatdis;
    ArrayList<ArrayList<ArrayList<String>>> locOthernatdis;
    ArrayList<ArrayList<ArrayList<String>>> completeOthernatdis;
    public int counter=0;

    ArrayList<ArrayList<Float>> firedistances = new ArrayList<>();
    ArrayList<ArrayList<Double>> fireavg=new ArrayList<>();

    ArrayList<ArrayList<Float>> flooddistances = new ArrayList<>();
    ArrayList<ArrayList<Double>> floodavg=new ArrayList<>();

    ArrayList<ArrayList<Float>> earthdistances = new ArrayList<>();
    ArrayList<ArrayList<Double>> earthavg=new ArrayList<>();

    ArrayList<ArrayList<Float>> Tordistances = new ArrayList<>();
    ArrayList<ArrayList<Double>> Toravg=new ArrayList<>();

    ArrayList<ArrayList<Float>> Otherdistances = new ArrayList<>();
    ArrayList<ArrayList<Double>> Otheravg=new ArrayList<>();




    DatabaseReference databaseReference;
    MyAdapter adapter;
    Button buttonlogout ,info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employeepage);
        FirebaseAuth auth=FirebaseAuth.getInstance();
        FirebaseUser currentuser=auth.getCurrentUser();
        if(currentuser==null){
            Intent intent=new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        buttonlogout=findViewById(R.id.Logoutempl);

        buttonlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogoutUser();
            }
        });

        recyclerView=findViewById(R.id.recycleview);
        databaseReference= FirebaseDatabase.getInstance().getReference("data").child("events");
        list=new ArrayList<>();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listKeys = new ArrayList<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Data data=dataSnapshot.getValue(Data.class);
                    list.add(data);
                    listKeys.add(dataSnapshot.getKey());
                    counter=counter+1;
                }
                criteria();

                completeFire.addAll(completeFloods);
                completeFire.addAll(completeEarthquake);
                completeFire.addAll(completeTornado);
                completeFire.addAll(completeOthernatdis);
                adapter=new MyAdapter(getApplicationContext(),completeFire, GeneralData);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private void LogoutUser(){
        FirebaseAuth.getInstance().signOut();
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void criteria(){
        fire = new ArrayList<>();
        floods = new ArrayList<>();
        earthquake = new ArrayList<>();
        tornado = new ArrayList<>();
        othernatdis = new ArrayList<>();
        try {
            for (int i = 0; i < counter; i++) {
                if (list.get(i).spinvalue.equals("Fire")) {
                    ArrayList<String> addList = new ArrayList<>(Arrays.asList(list.get(i).timeStamp, list.get(i).longi, list.get(i).lati, list.get(i).comments, list.get(i).encoded, listKeys.get(i)));
                    fire.add(addList);
                } else if (list.get(i).spinvalue.equals("Floods")) {
                    ArrayList<String> addList = new ArrayList<>(Arrays.asList(list.get(i).timeStamp, list.get(i).longi, list.get(i).lati, list.get(i).comments, list.get(i).encoded, listKeys.get(i)));
                    floods.add(addList);
                } else if (list.get(i).spinvalue.equals("Earthquake")) {
                    ArrayList<String> addList = new ArrayList<>(Arrays.asList(list.get(i).timeStamp, list.get(i).longi, list.get(i).lati, list.get(i).comments, list.get(i).encoded, listKeys.get(i)));
                    earthquake.add(addList);
                } else if (list.get(i).spinvalue.equals("Tornado")) {
                    ArrayList<String> addList = new ArrayList<>(Arrays.asList(list.get(i).timeStamp, list.get(i).longi, list.get(i).lati, list.get(i).comments, list.get(i).encoded, listKeys.get(i)));
                    tornado.add(addList);
                } else {
                    ArrayList<String> addList = new ArrayList<>(Arrays.asList(list.get(i).timeStamp, list.get(i).longi, list.get(i).lati, list.get(i).comments, list.get(i).encoded, listKeys.get(i)));
                    othernatdis.add(addList);
                }
            }
            makeCluster(fire, "Fire");
            makeCluster(floods, "Floods");
            makeCluster(earthquake, "Earthquake");
            makeCluster(tornado, "Tornado");
            makeCluster(othernatdis, "Other Natural Disaster");

            //Fire

            ArrayList<ArrayList<ArrayList<Double>>> coordfire=new ArrayList<>();



            for(int i=0;i<completeFire.size();i++){

                double avgtempx1=0;
                double avgtempx2=0;
                coordfire.add(new ArrayList<>());
                fireavg.add(new ArrayList<>());


                for(int g=0;g<completeFire.get(i).size();g++){
                    coordfire.get(i).add(new ArrayList<>());

                    Double x1= Double.valueOf(completeFire.get(i).get(g).get(1));
                    Double x2= Double.valueOf(completeFire.get(i).get(g).get(2));


                    coordfire.get(i).get(g).add(x1);
                    coordfire.get(i).get(g).add(x2);


                    avgtempx1=avgtempx1+x1;
                    avgtempx2=avgtempx2+x2;



                }
                fireavg.get(i).add(avgtempx1/completeFire.get(i).size());
                fireavg.get(i).add(avgtempx2/completeFire.get(i).size());


            }

            Location loc1=new Location("");
            Location loc2=new Location("");

            for(int i=0;i<coordfire.size();i++){

                if(coordfire.get(i).size()<1){
                    firedistances.get(i).add(0.0f);
                }else{
                    float maxfire=0.0f;
                    firedistances.add(new ArrayList<>());
                    int l=0;

                    do{

                        int j=l;

                        do{
                            loc1.setLongitude(coordfire.get(i).get(l).get(0));
                            loc1.setLatitude(coordfire.get(i).get(l).get(1));
                            loc2.setLongitude(coordfire.get(i).get(j).get(0));
                            loc2.setLatitude(coordfire.get(i).get(j).get(1));

                            float distanceInMeters = loc1.distanceTo(loc2);

                            if(maxfire<=distanceInMeters){

                                maxfire=distanceInMeters;
                            }


                            j=j+1;
                        }while(j < coordfire.get(i).size());
                        l=l+1;
                        firedistances.get(i).add(maxfire);

                    }while(l<coordfire.get(i).size());

                }
            }


            // Floods


            ArrayList<ArrayList<ArrayList<Double>>> coordfloods=new ArrayList<>();



            for(int i=0;i<completeFloods.size();i++){

                double avgtempx1=0;
                double avgtempx2=0;
                coordfloods.add(new ArrayList<>());
                floodavg.add(new ArrayList<>());


                for(int g=0;g<completeFloods.get(i).size();g++){
                    coordfloods.get(i).add(new ArrayList<>());

                    Double x1= Double.valueOf(completeFloods.get(i).get(g).get(1));
                    Double x2= Double.valueOf(completeFloods.get(i).get(g).get(2));


                    coordfloods.get(i).get(g).add(x1);
                    coordfloods.get(i).get(g).add(x2);


                    avgtempx1=avgtempx1+x1;
                    avgtempx2=avgtempx2+x2;

                }
                floodavg.get(i).add(avgtempx1/completeFloods.get(i).size());
                floodavg.get(i).add(avgtempx2/completeFloods.get(i).size());

            }
            Location locflood1=new Location("");
            Location locflood2=new Location("");


            for(int i=0;i<coordfloods.size();i++){

                if(coordfloods.get(i).size()<1){
                    flooddistances.get(i).add(0.0f);
                }else{
                    float maxflood=0.0f;
                    flooddistances.add(new ArrayList<>());
                    int l=0;

                    do{

                        int j=l;

                        do{
                            locflood1.setLongitude(coordfloods.get(i).get(l).get(0));
                            locflood1.setLatitude(coordfloods.get(i).get(l).get(1));
                            locflood2.setLongitude(coordfloods.get(i).get(j).get(0));
                            locflood2.setLatitude(coordfloods.get(i).get(j).get(1));

                            float distanceInMeters = loc1.distanceTo(loc2);

                            if(maxflood<=distanceInMeters){

                                maxflood=distanceInMeters;
                            }


                            j=j+1;
                        }while(j < coordfloods.get(i).size());
                        l=l+1;
                        flooddistances.get(i).add(maxflood);

                    }while(l<coordfloods.get(i).size());

                }
            }
            //Earthquake
            ArrayList<ArrayList<ArrayList<Double>>> coordearth=new ArrayList<>();

            for(int i=0;i<completeEarthquake.size();i++){

                double avgtempx1=0;
                double avgtempx2=0;
                coordearth.add(new ArrayList<>());
                earthavg.add(new ArrayList<>());


                for(int g=0;g<completeEarthquake.get(i).size();g++){
                    coordearth.get(i).add(new ArrayList<>());

                    Double x1= Double.valueOf(completeEarthquake.get(i).get(g).get(1));
                    Double x2= Double.valueOf(completeEarthquake.get(i).get(g).get(2));


                    coordearth.get(i).get(g).add(x1);
                    coordearth.get(i).get(g).add(x2);


                    avgtempx1=avgtempx1+x1;
                    avgtempx2=avgtempx2+x2;



                }
                earthavg.get(i).add(avgtempx1/completeEarthquake.get(i).size());
                earthavg.get(i).add(avgtempx2/completeEarthquake.get(i).size());




            }




            Location locearth1=new Location("");
            Location locearth2=new Location("");


            for(int i=0;i<coordearth.size();i++){

                if(coordearth.get(i).size()<1){
                    earthdistances.get(i).add(0.0f);
                }else{
                    float maxearth=0.0f;
                    earthdistances.add(new ArrayList<>());
                    int l=0;

                    do{

                        int j=l;

                        do{
                            locearth1.setLongitude(coordearth.get(i).get(l).get(0));
                            locearth1.setLatitude(coordearth.get(i).get(l).get(1));
                            locearth2.setLongitude(coordearth.get(i).get(j).get(0));
                            locearth2.setLatitude(coordearth.get(i).get(j).get(1));

                            float distanceInMeters = loc1.distanceTo(loc2);

                            if(maxearth<=distanceInMeters){

                                maxearth=distanceInMeters;
                            }


                            j=j+1;
                        }while(j < coordearth.get(i).size());
                        l=l+1;
                        earthdistances.get(i).add(maxearth);

                    }while(l<coordearth.get(i).size());

                }
            }

            //Tornado


            ArrayList<ArrayList<ArrayList<Double>>> coorTor=new ArrayList<>();



            for(int i=0;i<completeTornado.size();i++){

                double avgtempx1=0;
                double avgtempx2=0;
                coorTor.add(new ArrayList<>());
                Toravg.add(new ArrayList<>());


                for(int g=0;g<completeTornado.get(i).size();g++){
                    coorTor.get(i).add(new ArrayList<>());

                    Double x1= Double.valueOf(completeTornado.get(i).get(g).get(1));
                    Double x2= Double.valueOf(completeTornado.get(i).get(g).get(2));


                    coorTor.get(i).get(g).add(x1);
                    coorTor.get(i).get(g).add(x2);


                    avgtempx1=avgtempx1+x1;
                    avgtempx2=avgtempx2+x2;



                }
                Toravg.get(i).add(avgtempx1/completeTornado.get(i).size());
                Toravg.get(i).add(avgtempx2/completeTornado.get(i).size());




            }




            Location locTor1=new Location("");
            Location locTor2=new Location("");


            for(int i=0;i<coorTor.size();i++){

                if(coorTor.get(i).size()<1){
                    Tordistances.get(i).add(0.0f);
                }else{
                    float maxTor=0.0f;
                    Tordistances.add(new ArrayList<>());
                    int l=0;

                    do{

                        int j=l;

                        do{
                            locTor1.setLongitude(coorTor.get(i).get(l).get(0));
                            locTor1.setLatitude(coorTor.get(i).get(l).get(1));
                            locTor2.setLongitude(coorTor.get(i).get(j).get(0));
                            locTor2.setLatitude(coorTor.get(i).get(j).get(1));

                            float distanceInMeters = loc1.distanceTo(loc2);

                            if(maxTor<=distanceInMeters){

                                maxTor=distanceInMeters;
                            }


                            j=j+1;
                        }while(j < coorTor.get(i).size());
                        l=l+1;
                        Tordistances.get(i).add(maxTor);

                    }while(l<coorTor.get(i).size());

                }
            }


            //Other Natural Disasters


            ArrayList<ArrayList<ArrayList<Double>>> coorOther=new ArrayList<>();



            for(int i=0;i<completeOthernatdis.size();i++){

                double avgtempx1=0;
                double avgtempx2=0;
                coorOther.add(new ArrayList<>());
                Otheravg.add(new ArrayList<>());


                for(int g=0;g<completeOthernatdis.get(i).size();g++){
                    coorOther.get(i).add(new ArrayList<>());

                    Double x1= Double.valueOf(completeOthernatdis.get(i).get(g).get(1));
                    Double x2= Double.valueOf(completeOthernatdis.get(i).get(g).get(2));


                    coorOther.get(i).get(g).add(x1);
                    coorOther.get(i).get(g).add(x2);


                    avgtempx1=avgtempx1+x1;
                    avgtempx2=avgtempx2+x2;



                }
                Otheravg.get(i).add(avgtempx1/completeOthernatdis.get(i).size());
                Otheravg.get(i).add(avgtempx2/completeOthernatdis.get(i).size());




            }




            Location locOther1=new Location("");
            Location locOther2=new Location("");


            for(int i=0;i<coorOther.size();i++){

                if(coorOther.get(i).size()<1){
                    Otherdistances.get(i).add(0.0f);
                }else{
                    float maxOther=0.0f;
                    Otherdistances.add(new ArrayList<>());
                    int l=0;

                    do{

                        int j=l;

                        do{
                            locOther1.setLongitude(coorOther.get(i).get(l).get(0));
                            locOther1.setLatitude(coorOther.get(i).get(l).get(1));
                            locOther2.setLongitude(coorOther.get(i).get(j).get(0));
                            locOther2.setLatitude(coorOther.get(i).get(j).get(1));

                            float distanceInMeters = loc1.distanceTo(loc2);

                            if(maxOther<=distanceInMeters){

                                maxOther=distanceInMeters;
                            }


                            j=j+1;
                        }while(j < coorOther.get(i).size());
                        l=l+1;
                        Otherdistances.get(i).add(maxOther);

                    }while(l<coorOther.get(i).size());

                }
            }
            CompleteData=new ArrayList<>();
            GeneralData=new ArrayList<>();
            generaldata1=new ArrayList<>();
            generaldata2=new ArrayList<>();
            generaldata3=new ArrayList<>();
            generaldata4=new ArrayList<>();
            generaldata5=new ArrayList<>();


            for(int i=0;i<completeFire.size();i++){
                generaldata1.add(new ArrayList<>());
                generaldata1.get(i).add("Fire");
                generaldata1.get(i).add(String.valueOf( fireavg.get(i).get(0)));
                generaldata1.get(i).add(String.valueOf( fireavg.get(i).get(1)));
                //weight people reported *1,2+ event distance *2.5
                generaldata1.get(i).add( String.valueOf(completeFire.get(i).size()*1.2+2.5*firedistances.get(i).get(0)));

            }

            for(int i=0;i<completeFloods.size();i++){
                generaldata2.add(new ArrayList<>());
                generaldata2.get(i).add("Flood");
                generaldata2.get(i).add(String.valueOf( floodavg.get(i).get(0)));
                generaldata2.get(i).add(String.valueOf( floodavg.get(i).get(1)));
                //weight people reported *1,2+ event distance *2.5
                generaldata2.get(i).add( String.valueOf(completeFloods.get(i).size()*1.2+2.5*flooddistances.get(i).get(0)));

            }
            for(int i=0;i<completeEarthquake.size();i++){
                generaldata3.add(new ArrayList<>());
                generaldata3.get(i).add("Earthquake");
                generaldata3.get(i).add(String.valueOf( earthavg.get(i).get(0)));
                generaldata3.get(i).add(String.valueOf( earthavg.get(i).get(1)));
                //weight people reported *1,2+ event distance *2.5
                generaldata3.get(i).add( String.valueOf(completeEarthquake.get(i).size()*1.2+2.5*earthdistances.get(i).get(0)));

            }
            for(int i=0;i<completeTornado.size();i++){
                generaldata4.add(new ArrayList<>());
                generaldata4.get(i).add("Tornado");
                generaldata4.get(i).add(String.valueOf( Toravg.get(i).get(0)));
                generaldata4.get(i).add(String.valueOf( Toravg.get(i).get(1)));
                //weight people reported *1,2+ event distance *2.5
                generaldata4.get(i).add( String.valueOf(completeTornado.get(i).size()*1.2+2.5*Tordistances.get(i).get(0)));

            }
            for(int i=0;i<completeOthernatdis.size();i++){
                generaldata5.add(new ArrayList<>());
                generaldata5.get(i).add("Other Natural Disasters");
                generaldata5.get(i).add(String.valueOf( Otheravg.get(i).get(0)));
                generaldata5.get(i).add(String.valueOf( Otheravg.get(i).get(1)));
                //weight people reported *1,2+ event distance *2.5
                generaldata5.get(i).add( String.valueOf(completeOthernatdis.get(i).size()*1.2+2.5*Otherdistances.get(i).get(0)));

            }


            GeneralData.addAll(generaldata1);
            GeneralData.addAll(generaldata2);
            GeneralData.addAll(generaldata3);
            GeneralData.addAll(generaldata4);
            GeneralData.addAll(generaldata5);






        }catch (Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void makeCluster(ArrayList<ArrayList<String>> category, String categoryName) throws ParseException {
        locFire = new ArrayList<>();
        locFloods = new ArrayList<>();
        locTornado = new ArrayList<>();
        locEarthquake = new ArrayList<>();
        locOthernatdis = new ArrayList<>();
        int indexCounter = -1;
        for (ArrayList<String> event : category) {
            indexCounter++;
            if (categoryName.equals("Fire")) {
                locFire.add(new ArrayList<>());
            } else if (categoryName.equals("Floods")) {
                locFloods.add(new ArrayList<>());
            } else if (categoryName.equals("Earthquake")) {
                locEarthquake.add(new ArrayList<>());
            } else if (categoryName.equals("Tornado")) {
                locTornado.add(new ArrayList<>());
            } else {
                locOthernatdis.add(new ArrayList<>());
            }
            for (ArrayList<String> compareEvent : category) {
                if (categoryName.equals("Fire")) {
                    if (areLocationsWithinRange(Double.valueOf(event.get(2)), Double.valueOf(event.get(1)), Double.valueOf(compareEvent.get(2)), Double.valueOf(compareEvent.get(1)), 10.0f)) {
                        locFire.get(indexCounter).add(compareEvent);
                    }
                } else if (categoryName.equals("Floods")) {
                    if (areLocationsWithinRange(Double.valueOf(event.get(2)), Double.valueOf(event.get(1)), Double.valueOf(compareEvent.get(2)), Double.valueOf(compareEvent.get(1)), 15.0f)) {
                        locFloods.get(indexCounter).add(compareEvent);
                    }
                } else if (categoryName.equals("Earthquake")) {
                    if (areLocationsWithinRange(Double.valueOf(event.get(2)), Double.valueOf(event.get(1)), Double.valueOf(compareEvent.get(2)), Double.valueOf(compareEvent.get(1)), 30.0f)) {
                        locEarthquake.get(indexCounter).add(compareEvent);
                    }
                } else if (categoryName.equals("Tornado")) {
                    if (areLocationsWithinRange(Double.valueOf(event.get(2)), Double.valueOf(event.get(1)), Double.valueOf(compareEvent.get(2)), Double.valueOf(compareEvent.get(1)), 5.0f)) {
                        locTornado.get(indexCounter).add(compareEvent);
                    }
                } else {
                    if (areLocationsWithinRange(Double.valueOf(event.get(2)), Double.valueOf(event.get(1)), Double.valueOf(compareEvent.get(2)), Double.valueOf(compareEvent.get(1)), 10.0f)) {
                        locOthernatdis.get(indexCounter).add(compareEvent);
                    }
                }
            }
        }
        boolean donelocfire;
        boolean donelocfloods;
        boolean donelocearth;
        boolean doneloctor;
        boolean donelocother;
        if (categoryName.equals("Fire")) {
            int end = locFire.size();
            int i = 0;
            while (i < end-1) {
                donelocfire=false;
                for (int j = 0; j < locFire.get(i).size(); j++) {
                    if (locFire.get(i+1).contains(locFire.get(i).get(j))) {
                        locFire.get(i+1).addAll(locFire.get(i));
                        HashSet<ArrayList> set = new HashSet<>(locFire.get(i+1));
                        locFire.set(i+1, new ArrayList(set));
                        locFire.remove(i);
                        end--;
                        donelocfire=true;
                        break;
                    }
                }
                if(!donelocfire) {
                    i++;
                }
            }

            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            ArrayList<String> temp;
            for (ArrayList<ArrayList<String>> cluster : locFire) {
                for (int j = 0; j < cluster.size()-1; j++) {
                    for (int k = 0; k < cluster.size()-j; j++) {
                        Date date1 = format.parse(cluster.get(k).get(0));
                        Date date2 = format.parse(cluster.get(k+1).get(0));
                        if (date1.after(date2)) {
                            temp = cluster.get(j);
                            cluster.set(k, cluster.get(k+1));
                            cluster.set(k+1, temp);
                        }
                    }
                }
            }

            completeFire = new ArrayList<>();
            int index = -1;
            for (ArrayList<ArrayList<String>> cluster : locFire) {
                completeFire.add(new ArrayList<>());
                index++;
                if (cluster.size() > 1) {
                    Date firstDate = format.parse(cluster.get(0).get(0));
                    for (int j = 0; j < cluster.size(); j++) {
                        if (TimeUnit.DAYS.convert(Math.abs(firstDate.getTime() - format.parse(cluster.get(j).get(0)).getTime()), TimeUnit.MILLISECONDS) <= 3) {
                            completeFire.get(index).add(cluster.get(j));
                        }
                        else {
                            firstDate = format.parse(cluster.get(j).get(0));
                            completeFire.add(new ArrayList<>());
                            index++;
                            completeFire.get(index).add(cluster.get(j));
                        }
                    }
                }
                else {
                    completeFire.get(index).add(cluster.get(0));
                }
            }

        } else if (categoryName.equals("Floods")) {


            int end = locFloods.size();
            int i = 0;
            while (i < end-1) {
                donelocfloods=false;
                for (int j = 0; j < locFloods.get(i).size(); j++) {
                    if (locFloods.get(i+1).contains(locFloods.get(i).get(j))) {
                        locFloods.get(i+1).addAll(locFloods.get(i));
                        HashSet<ArrayList> set = new HashSet<>(locFloods.get(i+1));
                        locFloods.set(i+1, new ArrayList(set));
                        locFloods.remove(i);
                        end--;
                        donelocfloods=true;
                        break;
                    }
                }
                if(!donelocfloods) {
                    i++;
                }
            }

            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            ArrayList<String> temp;
            for (ArrayList<ArrayList<String>> cluster : locFloods) {
                for (int j = 0; j < cluster.size()-1; j++) {
                    for (int k = 0; k < cluster.size()-j; j++) {
                        Date date1 = format.parse(cluster.get(k).get(0));
                        Date date2 = format.parse(cluster.get(k+1).get(0));
                        if (date1.after(date2)) {
                            temp = cluster.get(j);
                            cluster.set(k, cluster.get(k+1));
                            cluster.set(k+1, temp);
                        }
                    }
                }
            }

            completeFloods = new ArrayList<>();
            int index = -1;
            for (ArrayList<ArrayList<String>> cluster : locFloods) {
                completeFloods.add(new ArrayList<>());
                index++;
                if (cluster.size() > 1) {
                    Date firstDate = format.parse(cluster.get(0).get(0));
                    for (int j = 0; j < cluster.size(); j++) {
                        if (TimeUnit.DAYS.convert(Math.abs(firstDate.getTime() - format.parse(cluster.get(j).get(0)).getTime()), TimeUnit.MILLISECONDS) <= 1) {
                            completeFloods.get(index).add(cluster.get(j));
                        }
                        else {
                            firstDate = format.parse(cluster.get(j).get(0));
                            completeFloods.add(new ArrayList<>());
                            index++;
                            completeFloods.get(index).add(cluster.get(j));
                        }
                    }
                }
                else {
                    completeFloods.get(index).add(cluster.get(0));
                }
            }
        } else if (categoryName.equals("Earthquake")) {
            int end = locEarthquake.size();
            int i = 0;
            while (i < end-1) {
                donelocearth=false;
                for (int j = 0; j < locEarthquake.get(i).size(); j++) {
                    if (locEarthquake.get(i+1).contains(locEarthquake.get(i).get(j))) {
                        locEarthquake.get(i+1).addAll(locEarthquake.get(i));
                        HashSet<ArrayList> set = new HashSet<>(locEarthquake.get(i+1));
                        locEarthquake.set(i+1, new ArrayList(set));
                        locEarthquake.remove(i);
                        end--;
                        donelocearth=true;
                        break;
                    }
                }
                if(!donelocearth) {
                    i++;
                }
            }

            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            ArrayList<String> temp;
            for (ArrayList<ArrayList<String>> cluster : locEarthquake) {
                for (int j = 0; j < cluster.size()-1; j++) {
                    for (int k = 0; k < cluster.size()-j; j++) {
                        Date date1 = format.parse(cluster.get(k).get(0));
                        Date date2 = format.parse(cluster.get(k+1).get(0));
                        if (date1.after(date2)) {
                            temp = cluster.get(j);
                            cluster.set(k, cluster.get(k+1));
                            cluster.set(k+1, temp);
                        }
                    }
                }
            }

            completeEarthquake = new ArrayList<>();
            int index = -1;
            for (ArrayList<ArrayList<String>> cluster : locEarthquake) {
                completeEarthquake.add(new ArrayList<>());
                index++;
                if (cluster.size() > 1) {
                    Date firstDate = format.parse(cluster.get(0).get(0));
                    for (int j = 0; j < cluster.size(); j++) {
                        if (TimeUnit.DAYS.convert(Math.abs(firstDate.getTime() - format.parse(cluster.get(j).get(0)).getTime()), TimeUnit.MILLISECONDS) <= 2) {
                            completeEarthquake.get(index).add(cluster.get(j));
                        }
                        else {
                            firstDate = format.parse(cluster.get(j).get(0));
                            completeEarthquake.add(new ArrayList<>());
                            index++;
                            completeEarthquake.get(index).add(cluster.get(j));
                        }
                    }
                }
                else {
                    completeEarthquake.get(index).add(cluster.get(0));
                }
            }
        } else if (categoryName.equals("Tornado")) {
            int end = locTornado.size();
            int i = 0;
            while (i < end-1) {
                doneloctor=false;
                for (int j = 0; j < locTornado.get(i).size(); j++) {
                    if (locTornado.get(i+1).contains(locTornado.get(i).get(j))) {
                        locTornado.get(i+1).addAll(locTornado.get(i));
                        HashSet<ArrayList> set = new HashSet<>(locTornado.get(i+1));
                        locTornado.set(i+1, new ArrayList(set));
                        locTornado.remove(i);
                        end--;
                        doneloctor=true;
                        break;
                    }
                }
                if(!doneloctor) {
                    i++;
                }
            }

            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            ArrayList<String> temp;
            for (ArrayList<ArrayList<String>> cluster : locTornado) {
                for (int j = 0; j < cluster.size()-1; j++) {
                    for (int k = 0; k < cluster.size()-j; j++) {
                        Date date1 = format.parse(cluster.get(k).get(0));
                        Date date2 = format.parse(cluster.get(k+1).get(0));
                        if (date1.after(date2)) {
                            temp = cluster.get(j);
                            cluster.set(k, cluster.get(k+1));
                            cluster.set(k+1, temp);
                        }
                    }
                }
            }

            completeTornado = new ArrayList<>();
            int index = -1;
            for (ArrayList<ArrayList<String>> cluster : locTornado) {
                completeTornado.add(new ArrayList<>());
                index++;
                if (cluster.size() > 1) {
                    Date firstDate = format.parse(cluster.get(0).get(0));
                    for (int j = 0; j < cluster.size(); j++) {
                        if (TimeUnit.DAYS.convert(Math.abs(firstDate.getTime() - format.parse(cluster.get(j).get(0)).getTime()), TimeUnit.MILLISECONDS) <= 1) {
                            completeTornado.get(index).add(cluster.get(j));
                        }
                        else {
                            firstDate = format.parse(cluster.get(j).get(0));
                            completeTornado.add(new ArrayList<>());
                            index++;
                            completeTornado.get(index).add(cluster.get(j));
                        }
                    }
                }
                else {
                    completeTornado.get(index).add(cluster.get(0));
                }
            }
        } else {
            int end = locOthernatdis.size();
            int i = 0;
            while (i < end-1) {
                donelocother=false;
                for (int j = 0; j < locOthernatdis.get(i).size(); j++) {
                    if (locOthernatdis.get(i+1).contains(locOthernatdis.get(i).get(j))) {
                        locOthernatdis.get(i+1).addAll(locOthernatdis.get(i));
                        HashSet<ArrayList> set = new HashSet<>(locOthernatdis.get(i+1));
                        locOthernatdis.set(i+1, new ArrayList(set));
                        locOthernatdis.remove(i);
                        end--;
                        donelocother=true;
                        break;
                    }
                }
                if(!donelocother) {
                    i++;
                }
            }

            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            ArrayList<String> temp;
            for (ArrayList<ArrayList<String>> cluster : locOthernatdis) {
                for (int j = 0; j < cluster.size()-1; j++) {
                    for (int k = 0; k < cluster.size()-j; j++) {
                        Date date1 = format.parse(cluster.get(k).get(0));
                        Date date2 = format.parse(cluster.get(k+1).get(0));
                        if (date1.after(date2)) {
                            temp = cluster.get(j);
                            cluster.set(k, cluster.get(k+1));
                            cluster.set(k+1, temp);
                        }
                    }
                }
            }

            completeOthernatdis = new ArrayList<>();
            int index = -1;
            for (ArrayList<ArrayList<String>> cluster : locOthernatdis) {
                completeOthernatdis.add(new ArrayList<>());
                index++;
                if (cluster.size() > 1) {
                    Date firstDate = format.parse(cluster.get(0).get(0));
                    for (int j = 0; j < cluster.size(); j++) {
                        if (TimeUnit.DAYS.convert(Math.abs(firstDate.getTime() - format.parse(cluster.get(j).get(0)).getTime()), TimeUnit.MILLISECONDS) <= 5) {
                            completeOthernatdis.get(index).add(cluster.get(j));
                        }
                        else {
                            firstDate = format.parse(cluster.get(j).get(0));
                            completeOthernatdis.add(new ArrayList<>());
                            index++;
                            completeOthernatdis.get(index).add(cluster.get(j));
                        }
                    }
                }
                else {
                    completeOthernatdis.get(index).add(cluster.get(0));
                }
            }
        }
    }

    public static final int EARTH_RADIUS_KM = 6371;

    public static double degreesToRadians(double degrees) {
        return degrees * Math.PI / 180;
    }

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = degreesToRadians(lat2 - lat1);
        double dLon = degreesToRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(degreesToRadians(lat1)) * Math.cos(degreesToRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = EARTH_RADIUS_KM * c;
        return distance;
    }

    public static boolean areLocationsWithinRange(double lat1, double lon1, double lat2, double lon2, float difference) {
        double distance = calculateDistance(lat1, lon1, lat2, lon2);
        return distance <= difference;
    }
}