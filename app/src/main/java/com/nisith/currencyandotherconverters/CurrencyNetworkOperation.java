package com.nisith.currencyandotherconverters;


import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CurrencyNetworkOperation {
    private CurrencyActivity currencyActivity;
    private ArrayList<CurrencyInfoHolder> allCurrencyInfoArrayList;
    private OnFinishServerOperationIListener onFinishServerOperationIListener;

    public interface OnFinishServerOperationIListener{
        //This callback method is Called when OkHttpClient finish it's server Operation
        void onFinishServerOperation(ArrayList<CurrencyInfoHolder> allcurrencyInfoArrayList, String resultStatus);
    }

    public CurrencyNetworkOperation(CurrencyActivity currencyActivity) {
        this.currencyActivity = currencyActivity;
        this.onFinishServerOperationIListener = currencyActivity;
    }

    public void performCurrencyNetworkOperation() {
        //This Method perform background network operation to fetch data for currency Activity from Server
        //This will Communicate with currencyscoop rapid api
        OkHttpClient okHttpClient = new OkHttpClient();
        //URL for Api
        final String url = "https://currencyscoop.p.rapidapi.com/latest";
        //Preparing Request Body
        final Request request = new Request.Builder()
                .url(url)
                .get()
               .addHeader("x-rapidapi-host", "currencyscoop.p.rapidapi.com")
               .addHeader("x-rapidapi-key", "4cb9c3992cmsh8959d21ec207e07p1940fdjsnb047353be7d3")
               .build();
        //Send Request To Server and Get Response.
        //When response is Available to user, then Callback method is called like onResponse() or onFailure according to server Result Status
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //This Method mainly called any internet connection Problem
                //If OnFailure() is Called, then onFinishServerOperation() Callback method is Called with passing Following Parameters
                onFinishServerOperationIListener.onFinishServerOperation(null,"failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    //All Ok
                    String responseString = response.body().string();
                    try {
                        //This is root JSON Object
                        JSONObject rootJSONObject = new JSONObject(responseString);
                        //This is "response" key. Which is Another JSON Object.Inside which "rates" key is Present ,which is Another JSON Object
                        //For more information see "currencyscoop" rapid api and see Result which is a JSon file, Then You get All the Information
                        JSONObject response1 = rootJSONObject.getJSONObject("response");
                        JSONObject rates = response1.getJSONObject("rates");
                        //This Array contains All Available Currencies name supported by this "currencyscoop" API
                        JSONArray allCurrencyNameJSONArray = rates.names();
                        if (rates != null && allCurrencyNameJSONArray!=null) {
                            //This allCurrencyInfoArrayList Contains all currencies Information i.e. one Object contains two fields,
                            // first currencyName and second currencyRate. This all rate value is with respect to USD
                            allCurrencyInfoArrayList = putAllCurrencyInfoInArrayList(rates, allCurrencyNameJSONArray);
                            //If OnFailure() is Called, then onFinishServerOperation() Callback method is Called with passing Following Parameters
                            onFinishServerOperationIListener.onFinishServerOperation(allCurrencyInfoArrayList,"ok");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        //If OnFailure() is Called, then onFinishServerOperation() Callback method is Called with passing Following Parameters
                        onFinishServerOperationIListener.onFinishServerOperation(null,"not successful");
                    }
                } else {
                    //If OnFailure() is Called, then onFinishServerOperation() Callback method is Called with passing Following Parameters
                    onFinishServerOperationIListener.onFinishServerOperation(null,"not successful");
                }
            }
        });
    }

    private ArrayList<CurrencyInfoHolder> putAllCurrencyInfoInArrayList(JSONObject rates,JSONArray allCurrencyNameJSONArray) throws JSONException {
        //This Method Fill allCurrencyInfoArrayList whit currencyName and currencyRate
        int index;
        String currencyName,currencyRate;
        ArrayList<CurrencyInfoHolder> allCurrencyInfoArrayList = new ArrayList<>();
        for (index = 0;index <allCurrencyNameJSONArray.length();index++){
            //Get Name of the Currency
            currencyName = allCurrencyNameJSONArray.getString(index);
            //Get rate of That currency
            currencyRate = rates.getString(currencyName);
            allCurrencyInfoArrayList.add(new CurrencyInfoHolder(currencyName,currencyRate));
        }
        return allCurrencyInfoArrayList;
    }


}


















