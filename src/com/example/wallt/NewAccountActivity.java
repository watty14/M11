package com.example.wallt;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


public class NewAccountActivity extends Fragment {
	
    private View fragmentView;
    private Activity parentActivity;
    private ServerUtility instance;
    
    private EditText mBankNameField;
	private EditText mAccountNumberField;
	private EditText mBalanceField;
	private Button mCreateAccountButton;
	private ProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	fragmentView = inflater.inflate(R.layout.activity_new_account, container, false);
    	instance = ServerUtility.getInstance();
    	fragmentView.setOnTouchListener(new GestureListener() {
            public void onSwipeRight() {
                ((MainActivity) parentActivity).finishFragment();
            }
        });
    	parentActivity = getActivity();
    	mBankNameField = (EditText) fragmentView.findViewById(R.id.bankname_field);
		mAccountNumberField = (EditText) fragmentView.findViewById(R.id.accountnumber_field);
		mBalanceField = (EditText) fragmentView.findViewById(R.id.balance_field);
		mCreateAccountButton = (Button) fragmentView.findViewById(R.id.createaccount_button);
		mProgressBar = (ProgressBar) fragmentView.findViewById(R.id.progressBar1);
		mCreateAccountButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {	
				String bankName = mBankNameField.getText().toString();
				String accountNumber = mAccountNumberField.getText().toString();
				String balance = mBalanceField.getText().toString();
				if (bankName.equals("") || accountNumber.equals("") || balance.equals("")) {
					Toast.makeText(parentActivity, "Invalid Input", 
							Toast.LENGTH_SHORT).show();
				} else {
					new AsyncTaskCreateBankAccount().execute(bankName,
	                        accountNumber, balance);
	                    mProgressBar.setVisibility(View.VISIBLE);
	                    mCreateAccountButton.setVisibility(View.INVISIBLE);
				}

			}
		});
        return fragmentView;
    }
    
	
	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
	    super.onCreateOptionsMenu(menu, inflater);
	    parentActivity.getActionBar().setDisplayHomeAsUpEnabled(true);
	    parentActivity.setTitle(getString(R.string.NewAccount));
	    inflater.inflate(R.menu.new_account, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (getFragmentManager().getBackStackEntryCount() > 1) {
				((MainActivity) parentActivity).finishFragment();
	        }
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class AsyncTaskCreateBankAccount
    	extends AsyncTask<String, Void, Boolean> {

		@Override
		protected Boolean doInBackground(final String... params) {
		    final BankAccount account = new BankAccount(null, params[1],
		            Integer.parseInt(params[2]), params[0], null);
		    return instance.createNewBankAccount(account);
		}

		@Override
		protected void onPostExecute(final Boolean result) {
		    super.onPostExecute(result);
		    mProgressBar.setVisibility(View.INVISIBLE);
		    if (result) {
		        Toast.makeText(parentActivity, "Account created",
		                Toast.LENGTH_LONG).show();
		        final Thread thread = new Thread() {
		            @Override
		            public void run() {
		                try {
		                    int waited = 0;
		                    while (waited < 1000) {
		                        sleep(100);
		                        waited += 100;
		                    }
		                } catch (InterruptedException e) {
		                    //do nothing
		                } finally {
		                	((MainActivity) parentActivity).finishFragment();
		                }
		            }
		        };
		        thread.start();
		    } else {
		        mCreateAccountButton.setVisibility(View.VISIBLE);
		        Toast.makeText(parentActivity,
		                "Failed to create account! Try again.",
		                Toast.LENGTH_LONG).show();
		    }
		}

	}

}