package co.pipevine.core;

import java.util.List;
import java.util.Vector;

import org.brickred.socialauth.Contact;

import co.pipevine.android.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;


public class ViewConnectionProfileActivity extends FragmentActivity {
	
	private PagerAdapter mPagerAdapter;
	public static Contact connection;


	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.viewpager_layout);
		this.initialisePaging();

		

		
		Intent intent = getIntent();
		String reverseName = intent.getStringExtra("reverseName");
		if(reverseName != null){
			
			connection = ContactDataListener.getOrdererdContacts().get(reverseName);

		}
		
	}
    /**
     * Initialise the fragments to be paged
     */
    private void initialisePaging() {
 
        List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this, ViewProfileFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, ViewProfileFragment2.class.getName()));
        
        
        this.mPagerAdapter  = new PagerAdapter(super.getSupportFragmentManager(), fragments);
        //
        ViewPager pager = (ViewPager)super.findViewById(R.id.viewpager);
        pager.setAdapter(this.mPagerAdapter);
    }

}
