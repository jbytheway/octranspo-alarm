package io.github.jbytheway.rideottawa.ui;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.commons.collections4.IteratorUtils;

import java.util.List;

import io.github.jbytheway.rideottawa.Favourite;
import io.github.jbytheway.rideottawa.OcTranspoDataAccess;
import io.github.jbytheway.rideottawa.RideOttawaApplication;
import io.github.jbytheway.rideottawa.utils.IndirectArrayAdapter;
import io.github.jbytheway.rideottawa.R;

public class ListFavouritesActivityFragment extends Fragment {
    @SuppressWarnings("unused")
    private static final String TAG = "ListFavouritesFragment";

    public ListFavouritesActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Don't destroy Fragment on reconfiguration
        setRetainInstance(true);

        // This Fragment adds options to the ActionBar
        setHasOptionsMenu(true);

        mOcTranspo = ((RideOttawaApplication) getActivity().getApplication()).getOcTranspo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_favourites, container, false);
        mFavouriteList = (ListView) view.findViewById(R.id.favourite_list_view);

        mAdapter = new IndirectArrayAdapter<>(
            getActivity(),
            R.layout.favourite_list_item,
            new IndirectArrayAdapter.ListGenerator<Favourite>() {
                @Override
                public List<Favourite> makeList() {
                    return IteratorUtils.toList(Favourite.findAll(Favourite.class));
                }
            },
            new IndirectArrayAdapter.ViewGenerator<Favourite>() {
                @Override
                public void applyView(View v, Favourite f) {
                    TextView name = (TextView) v.findViewById(R.id.name);
                    name.setText(f.Name);
                }
            }
        );
        mFavouriteList.setAdapter(mAdapter);

        mFavouriteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mOcTranspo.isDatabaseAvailable()) {
                    mActivity.notifyNoDatabase();
                    return;
                }
                Favourite item = mAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), ViewFavouriteActivity.class);
                intent.putExtra(ViewFavouriteActivity.FAVOURITE_ID, item.getId());

                startActivity(intent);
            }
        });

        mFavouriteList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (!mOcTranspo.isDatabaseAvailable()) {
                    mActivity.notifyNoDatabase();
                    return true;
                }
                Favourite item = mAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), EditFavouriteActivity.class);
                intent.putExtra(EditFavouriteActivity.NEW_FAVOURITE, false);
                intent.putExtra(EditFavouriteActivity.FAVOURITE_ID, item.getId());

                startActivity(intent);

                return true;
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (ListFavouritesActivity) getActivity();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list_favourites, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFavouriteList = null;
        super.onDestroyView();
    }

    private OcTranspoDataAccess mOcTranspo;
    private ListFavouritesActivity mActivity;
    private ListView mFavouriteList;
    private IndirectArrayAdapter<Favourite> mAdapter;
}
