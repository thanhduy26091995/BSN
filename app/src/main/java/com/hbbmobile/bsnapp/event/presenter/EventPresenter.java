package com.hbbmobile.bsnapp.event.presenter;

import android.app.ProgressDialog;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.hbbmobile.bsnapp.event.CustomEventAdapter;
import com.hbbmobile.bsnapp.event.OnLoadMoreListener;
import com.hbbmobile.bsnapp.event.model.Event;
import com.hbbmobile.bsnapp.event.model.EventResponse;
import com.hbbmobile.bsnapp.event.view.EventFragment;
import com.hbbmobile.bsnapp.rest.ApiClient;
import com.hbbmobile.bsnapp.rest.ApiInterface;
import com.hbbmobile.bsnapp.utils.ShowAlertDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by buivu on 21/12/2016.
 */
public class EventPresenter {
    private EventFragment view;
    private CustomEventAdapter customAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private List<Event> listEventTemp;
    private ProgressDialog progressDialog;

    public EventPresenter(EventFragment view, RecyclerView mRecycler) {
        this.view = view;
        this.mRecycler = mRecycler;
    }

    public void fetchData() {
        showProgessDialog();
        Log.d(view.TAG, "onFetchData");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<EventResponse> call = apiService.getAllEvent();
        call.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                Log.d(view.TAG, "onResponse");
                final List<Event> listEvent = response.body().getListEvent();
                listEventTemp = new ArrayList<Event>();
                int sizeList = 0;
                if (listEvent.size() >= 20) {
                    sizeList = 20;
                } else {
                    sizeList = listEvent.size();
                }
                for (int i = 0; i < sizeList; i++) {
                    Event event = listEvent.get(i);
                    listEventTemp.add(event);
                }
                mRecycler.setLayoutManager(new LinearLayoutManager(view.getActivity()));
                customAdapter = new CustomEventAdapter(view.getActivity(), listEventTemp, mRecycler);

                mRecycler.setAdapter(customAdapter);
                hideProgressDialog();

                customAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {
                        mRecycler.post(new Runnable() {
                            @Override
                            public void run() {
                                listEventTemp.add(null);
                                customAdapter.notifyItemInserted(listEventTemp.size() - 1);
                            }
                        });

                        //load more data for recyclerview
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                //remove loading item
                                listEventTemp.remove(listEventTemp.size() - 1);
                                customAdapter.notifyItemRemoved(listEventTemp.size() - 1);
                                //load data
                                int indexRemain;
                                if (listEvent.size() - listEventTemp.size() >= 20) {
                                    indexRemain = listEventTemp.size() + 20;
                                } else {
                                    indexRemain = listEventTemp.size() + (listEvent.size() - listEventTemp.size());
                                }
                                for (int i = listEventTemp.size(); i < indexRemain; i++) {
                                    Event event = listEvent.get(i);
                                    listEventTemp.add(event);
                                }
                                customAdapter.notifyDataSetChanged();
                                customAdapter.setLoaded();
                            }
                        }, 3000);


                    }
                });
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                //Log.d(TAG, t.getMessage());
                ShowAlertDialog.showAlert("Cannot load data event! Please try it again", view.getActivity());
                hideProgressDialog();
            }
        });
    }

    public ProgressDialog mProgressDialog;


    public void showProgessDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(view.getActivity());
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setCancelable(false);
        }
        try {
            mProgressDialog.show();
        } catch (Exception e) {
        }

    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            try {
                mProgressDialog.dismiss();
            } catch (Exception e) {
            }

        }
    }
}
