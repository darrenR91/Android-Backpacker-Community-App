package com.k00140908.darren.the88days.ManageFriends;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.k00140908.darren.the88days.Model.Message;
import com.k00140908.darren.the88days.Model.User;
import com.k00140908.darren.the88days.PagerAdapters.CustomItemClickListener;
import com.k00140908.darren.the88days.R;
import com.k00140908.darren.the88days.RecycledListViewAdapters.AdapterChatMessages;
import com.k00140908.darren.the88days.RetrofitInterfaces.BackpackerAPI;
import com.k00140908.darren.the88days.UserLocalStore;
import com.k00140908.darren.the88days.db.MessengerDB;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private ArrayList<Message> mListChatMessages = new ArrayList<>();

    private AdapterChatMessages mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    FloatingActionButton fab;

    private RecyclerView mRecyclerChats;
    int defaultRadius=100;
    private View mProgressView;
    private View mMessagesListView;

    private LinearLayoutManager mLayoutManager;
    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;
    SharedPreferences prefs;

    private String mSenderUserId="";
    private String mSenderUsername="";
    private OnFragmentInteractionListener mListener;

    public TextView mMsgView;
    public Button mSenMsgButton;
    MessengerDB messengerDB;
    UserLocalStore userLocalStore;
    final static String ENDPOINT ="https://the88days.azurewebsites.net/";
    User user = null;

    boolean sendingMsg = false;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_chat, container, false);

        Bundle b = getArguments();
        if(b!=null) {
            mSenderUserId = b.getString("userId");
            mSenderUsername = b.getString("username");
        }
        mListener.SetupToolbar(1, mSenderUsername);

        mMsgView = (EditText) view.findViewById(R.id.msg_edit);
        mSenMsgButton = (Button) view.findViewById(R.id.send_btn);
        mSenMsgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sentMsg();
            }
        });
        mSenMsgButton.setEnabled(false);
        messengerDB = new MessengerDB(getContext());

        userLocalStore = new UserLocalStore(getContext());

        user = userLocalStore.getLoggedInUser();

        //listener on changed sort order preference:
        prefs = getActivity().getSharedPreferences("userDetails", 0);

        SharedPreferences.Editor userLocalDatabaseEditor = prefs.edit();
        userLocalDatabaseEditor.putString("NewChatId", mSenderUserId);
        userLocalDatabaseEditor.commit();
        //Toast.makeText(getActivity(), "Enter Chat window", Toast.LENGTH_SHORT).show();



        userLocalDatabaseEditor.putBoolean("MessageListOpen", false);
        userLocalDatabaseEditor.commit();
        //Toast.makeText(getActivity(), "Exit Message List to chat window", Toast.LENGTH_SHORT).show();


        mMessagesListView = view.findViewById(R.id.listMessages);
        //mProgressView = view.findViewById(R.id.messages__progress);
        //showProgress(true);

        //mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeMessagesList);
        //mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerChats = (RecyclerView) view.findViewById(R.id.listMessages);
        //set the layout manager before trying to display data
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mRecyclerChats.setLayoutManager(mLayoutManager);


        if (mAdapter == null) {
            mAdapter = new AdapterChatMessages(getActivity(), new CustomItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    //Toast.makeText(getActivity(), "Clicked Item: " + position, Toast.LENGTH_SHORT).show();
//                    //mListener.ViewBackpackerProfilebyList(position);
//                    User a = mListChatMessages.get(position);
//                    //selectOptions(a.getUserName());
//                    mListener.LoadChatFragment(a);
                }
            });

        }
        mRecyclerChats.setAdapter(mAdapter);

        getMessages();


        prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences SharedPrefs, String key) {
                //Toast.makeText(getActivity(), key, Toast.LENGTH_SHORT).show();
                boolean NewMessageStatus = prefs.getBoolean("NewChatWindowsMessage", false);
                if (NewMessageStatus)
                {
                    //Toast.makeText(getActivity(), "Update Chat a new message has arrived!!", Toast.LENGTH_SHORT).show();

                    SharedPreferences.Editor userLocalDatabaseEditor = prefs.edit();
                    userLocalDatabaseEditor.putBoolean("NewChatWindowsMessage", false);
                    userLocalDatabaseEditor.commit();

                    getMessages();
                }


            }
        };
        prefs.registerOnSharedPreferenceChangeListener(prefListener);


        mMsgView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(mMsgView.getText().toString().equals(""))
                {
                    mSenMsgButton.setEnabled(false);
                }
                else if(!mMsgView.getText().toString().equals("") & !sendingMsg )
                {
                    mSenMsgButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;


    }

    private void sentMsg() {
        sendingMsg = true;
        mSenMsgButton.setEnabled(false);
        RestAdapter.Builder adapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Authorization", "Bearer " + user.getAccess_token());
                    }
                });

        BackpackerAPI api =  adapter.build().create(BackpackerAPI.class);

        api.SendMessage(mMsgView.getText().toString(), mSenderUserId, new Callback<User>() {
            @Override
            public void success(User userResponse, Response response) {
                messengerDB.insertMessage(user.getUserId(), mMsgView.getText().toString(), mSenderUserId);
                sendingMsg = false;
                mMsgView.setText("");
                //mSenMsgButton.setEnabled(true);
                getMessages();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                //mSenMsgButton.setEnabled(true);
                Toast.makeText(getActivity(), "Failed to send message please try again.", Toast.LENGTH_SHORT).show();
                sendingMsg = false;
            }
        });
    }

    public void getMessages()
    {
        mListChatMessages = messengerDB.getMessage(mSenderUserId);
        mAdapter.setFriendsList(mListChatMessages,user);
        messengerDB.resetProfleCount(mSenderUserId);
        //mAdapter.
        //mSwipeRefreshLayout.setRefreshing(false);
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        SharedPreferences SharedPrefs = getActivity().getSharedPreferences("userDetails", 0);
        SharedPreferences.Editor userLocalDatabaseEditor = SharedPrefs.edit();
        userLocalDatabaseEditor.putString("NewChatId", "");
        userLocalDatabaseEditor.commit();
        //Toast.makeText(getActivity(), "Exit Chat window", Toast.LENGTH_SHORT).show();
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void SetupToolbar(int i, String s);

        boolean checkMessageListVisible();
    }
}
