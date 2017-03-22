package lv.emils.dev.simpleapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import lv.emils.dev.simpleapp.content.UserContent;
import lv.emils.dev.simpleapp.util.JsonReaderUtil;

/**
 * A fragment representing a single User detail screen.
 * This fragment is either contained in a {@link UserListActivity}
 * in two-pane mode (on tablets) or a {@link UserDetailActivity}
 * on handsets.
 */
public class UserDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The content this fragment is presenting.
     */
    private UserContent.User mItem;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UserDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            try {
                mItem = new UserJsonReaderTask().execute(getArguments().getString(ARG_ITEM_ID)).get(10000, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getUsername());
            }
            mSwipeRefreshLayout = (SwipeRefreshLayout) activity.findViewById(R.id.user_detail_refresh_layout);
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshContent();
                }
                private void refreshContent(){
                    mSwipeRefreshLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            mSwipeRefreshLayout.setRefreshing(true);
                            try {
                                mItem = new UserJsonReaderTask().execute(getArguments().getString(ARG_ITEM_ID)).get(10000, TimeUnit.MILLISECONDS);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Activity activity = UserDetailFragment.this.getActivity();
                            activity.findViewById(R.id.user_detail);
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            });

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_detail, container, false);

        // Show the content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.user_detail)).setText(mItem.toString());
        }

        return rootView;
    }

    private class UserJsonReaderTask extends AsyncTask<String, Void, UserContent.User> {

        private ProgressDialog progressDialog = new ProgressDialog(getActivity());
        JSONObject jsonUser;

        protected void onPreExecute() {
            progressDialog.setMessage("Getting user info");
            progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    UserJsonReaderTask.this.cancel(true);
                }
            });
        }

        protected UserContent.User doInBackground(String... id) {
            try {
                jsonUser = JsonReaderUtil.readJsonObjectFromUrl(UserContent.USERS_LINK + id[0]);
                mItem = JsonReaderUtil.getUser(jsonUser);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException je) {
                je.printStackTrace();
            }
            return mItem;
        }

        protected void onPostExecute(UserContent.User user) {
            progressDialog.setMessage("User info received");
            this.progressDialog.dismiss();
        }
    }
}