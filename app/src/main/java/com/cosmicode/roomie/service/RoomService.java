package com.cosmicode.roomie.service;

import android.content.Context;
import android.util.Log;

import com.cosmicode.roomie.BaseActivity;
import com.cosmicode.roomie.domain.Address;
import com.cosmicode.roomie.domain.Room;
import com.cosmicode.roomie.domain.RoomCreate;
import com.cosmicode.roomie.domain.RoomExpense;
import com.cosmicode.roomie.domain.SearchFilter;
import com.cosmicode.roomie.util.network.ApiServiceGenerator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoomService {
    private final static String TAG = "RoomService";
    private Context context;
    private String authToken;
    private RoomServiceListener listener;

    public RoomService(Context context, RoomServiceListener listener) {
        this.context = context;
        this.listener = listener;
        this.authToken = ((BaseActivity) this.context).getJhiUsers().getAuthToken();
    }

    public void createRoom(RoomCreate room, Address address, RoomExpense roomExpense){

        AddressApiEndpointInterface AddressApiService = ApiServiceGenerator.createService(AddressApiEndpointInterface.class, authToken);
        Call<Address> callA = AddressApiService .createAddress(address);

        RoomExpenseApiEndpointInterface ExpenseApiService = ApiServiceGenerator.createService(RoomExpenseApiEndpointInterface.class, authToken);
        Call<RoomExpense> callE = ExpenseApiService.createExpense(roomExpense);

        RoomApiEndpointInterface RoomApiService = ApiServiceGenerator.createService(RoomApiEndpointInterface.class, authToken);
        Call<Room> callR = RoomApiService.createRoom(room);

        callA.enqueue(new Callback<Address>() {
            @Override
            public void onResponse(Call<Address> call, Response<Address> response) {
                room.setAddressId(response.body().getId());
                callE.enqueue(new Callback<RoomExpense>() {
                    @Override
                    public void onResponse(Call<RoomExpense> call, Response<RoomExpense> response) {
                        room.setPriceId(response.body().getId());
                        callR.enqueue(new Callback<Room>() {
                            @Override
                            public void onResponse(Call<Room> call, Response<Room> response) {
                                if (response.code() == 201) { // OK
                                    listener.OnCreateSuccess(response.body());

                                } else {
                                    Log.e(TAG, response.toString());
                                    listener.OnGetRoomsError(response.errorBody().toString());
                                }
                            }

                            @Override
                            public void onFailure(Call<Room> call, Throwable t) {
                                Log.e(TAG, t.toString());
                                listener.OnGetRoomsError(t.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<RoomExpense> call, Throwable t) {
                        Log.e(TAG, t.toString());
                        listener.OnGetRoomsError(t.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(Call<Address> call, Throwable t) {
                Log.e(TAG, t.toString());
                listener.OnGetRoomsError(t.getMessage());
            }
        });

    }

    public List<Room> getAllRooms(){

        RoomApiEndpointInterface apiService = ApiServiceGenerator.createService(RoomApiEndpointInterface.class, authToken);

        Call<List<Room>> call = apiService.getRooms();

        call.enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if (response.code() == 200) { // OK
                    listener.OnGetRoomsSuccess(response.body());

                } else {
                    Log.e(TAG, response.toString());
                    listener.OnGetRoomsError(Integer.toString(response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Log.e(TAG, t.toString());
                listener.OnGetRoomsError(t.getMessage());
            }
        });
        return null;
    }

    public void updateRoom (Room room){

        RoomApiEndpointInterface apiService = ApiServiceGenerator.createService(RoomApiEndpointInterface.class, authToken);

        Call<Room> call = apiService.updateRoom(room);

        call.enqueue(new Callback<Room>() {
            @Override
            public void onResponse(Call<Room> call, Response<Room> response) {
                if (response.code() == 200) { // OK
                    listener.OnUpdateSuccess(response.body());

                } else {
                    Log.e(TAG, response.toString());
                    listener.OnGetRoomsError(Integer.toString(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Room> call, Throwable t) {
                Log.e(TAG, t.toString());
                listener.OnGetRoomsError(t.getMessage());
            }
        });
    }

    public void serachRooms(String query){
        RoomApiEndpointInterface apiService = ApiServiceGenerator.createService(RoomApiEndpointInterface.class, authToken);

        Call<List<Room>> call = apiService.serachRooms(query);

        call.enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if (response.code() == 200) { // OK
                    listener.OnGetRoomsSuccess(response.body());
                } else {
                    Log.e(TAG, response.toString());
                    listener.OnGetRoomsError(Integer.toString(response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Log.e(TAG, t.toString());
                listener.OnGetRoomsError(t.getMessage());
            }
        });

    }

    public void searchRoomsAdvanced(SearchFilter searchFilter) {
        RoomApiEndpointInterface apiService = ApiServiceGenerator.createService(RoomApiEndpointInterface.class, authToken);

        Call<List<Room>> call = apiService.searchRoomsAdvanced(searchFilter);

        call.enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                if (response.code() == 200) { // OK
                    listener.OnGetRoomsSuccess(response.body());
                } else {
                    Log.e(TAG, response.toString());
                    listener.OnGetRoomsError(Integer.toString(response.code()));
                }
            }
            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Log.e(TAG, t.toString());
                listener.OnGetRoomsError(t.getMessage());
            }
        });
    }

    public interface RoomServiceListener {
        void OnCreateSuccess(Room room);
        void OnGetRoomsSuccess(List<Room> rooms);
        void OnGetRoomsError(String error);
        void OnUpdateSuccess(Room room);
    }

}
