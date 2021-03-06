package com.daiict.enterprizecomputing.reviewdekho.SystemDashboard;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.daiict.enterprizecomputing.reviewdekho.Classes.SharedPrefManager;
import com.daiict.enterprizecomputing.reviewdekho.Classes.UserDataClass;
import com.daiict.enterprizecomputing.reviewdekho.Classes.UserReviewClass;
import com.daiict.enterprizecomputing.reviewdekho.Comments.CommentsView;
import com.daiict.enterprizecomputing.reviewdekho.DashboardLogin.DashboardLogin;
import com.daiict.enterprizecomputing.reviewdekho.DatabaseConnection.API;
import com.daiict.enterprizecomputing.reviewdekho.R;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class FragmentFeed extends Fragment {
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<UserReviewClass> userDataClasses = new ArrayList<UserReviewClass>();
    RecyclerView recyclerView;
    AdapterUserReviewDisplay adapterUserReviewDisplay;
    ImageView imageView;

    //Size data for ArrayList if data updated then load data in the
    int size=0;
    boolean valueFirst = false;

    SharedPrefManager sharedPrefManager ;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sharedPrefManager  = new SharedPrefManager(Objects.requireNonNull(getContext()));
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        imageView = view.findViewById(R.id.feed_frag_add);

        //Swiper To swipe load the new data;
        swipeRefreshLayout = view.findViewById(R.id.feed_swiper);



        recyclerView = view.findViewById(R.id.feed_recyclerview);


        adapterUserReviewDisplay = new AdapterUserReviewDisplay(getActivity(),userDataClasses);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapterUserReviewDisplay);

        if(sharedPrefManager.getRolePreference()==5)
        {
            imageView.setImageResource(R.drawable.image_login);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), DashboardLogin.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //intent.putExtra("Fragment", "profilefragment");
                    startActivity(intent);
                }
            });
        }
        else if(sharedPrefManager.getRolePreference()==1)
        {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), AddReview.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    //intent.putExtra("Fragment", "profilefragment");
                    startActivity(intent);
                }
            });


        }
        else
        {
            imageView.setVisibility(View.GONE);
        }

        databaseConnection();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataReload();
                swipeRefreshLayout.setRefreshing(false);
            }
        });



        return view;
    }


    public void databaseConnection()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(sharedPrefManager.getBaseURL())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        API instanceofapi = retrofit.create(API.class);
        Call<ArrayList<UserReviewClass>>  call = instanceofapi.getReviews();
        call.enqueue(new Callback<ArrayList<UserReviewClass>>() {
            @Override
            public void onResponse(Call<ArrayList<UserReviewClass>> call, Response<ArrayList<UserReviewClass>> response) {
                if(response.isSuccessful())
                {
                    userDataClasses = response.body();
                    Log.e("Feed Fragment : ","Data Got : "+userDataClasses.size());

                    if(!valueFirst)
                    {
                        size= userDataClasses.size();
                        valueFirst = true;
                    }

                    dataChangeRView();
//                    Toast.makeText(getContext(), userDataClasses.size(), Toast.LENGTH_SHORT).show();
                }
               // Log.e("Feed Fragment : ","Data Got : "+userDataClasses.size());
            }

            @Override
            public void onFailure(Call<ArrayList<UserReviewClass>> call, Throwable t) {
                Log.e("Feed Fragment : ","Error : "+t.getMessage());

            }
        });
    }
    private void dataChangeRView()
    {
        adapterUserReviewDisplay  = new AdapterUserReviewDisplay(getActivity(),userDataClasses);
        recyclerView.setAdapter(adapterUserReviewDisplay);
    }

    private void dataReload()
    {
        databaseConnection();
        if(size==userDataClasses.size())
        {
            Log.e("Feed Reload", "No change Same data");
        }
        else
        {
            dataChangeRView();
        }
    }
}