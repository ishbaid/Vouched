package info.androidhive.slidingmenu;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import co.pipevine.android.R;
import co.pipevine.core.Leaderboards;

public class CommunityFragment extends Fragment implements View.OnClickListener{
	
	Button total, prof, integ, comm, innovation, prod, adapt, lead, team;
	String [] traitScores = {

			"professionalismScore",
			"productivityScore",
			"integrityScore",
			"adaptabilityScore",
			"communicationScore",
			"leadershipScore",
			"innovationScore",
			"teamworkScore",
			"totalVouchScore"

	};
	
	Button[]allButtons;
	public CommunityFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_community, container, false);
        
        total = (Button) rootView.findViewById(R.id.button1);
        prof = (Button) rootView.findViewById(R.id.button2);
        integ = (Button) rootView.findViewById(R.id.button3);
        comm = (Button) rootView.findViewById(R.id.button4);
        innovation = (Button) rootView.findViewById(R.id.button5);
        prod = (Button) rootView.findViewById(R.id.button6);
        adapt = (Button) rootView.findViewById(R.id.button7);
        lead = (Button) rootView.findViewById(R.id.button8);
        team = (Button) rootView.findViewById(R.id.button9);
        
        allButtons = new Button[9];
        allButtons[0] = prof;
        allButtons[1] = prod;
        allButtons[2] = integ;
        allButtons[3] = adapt;
        allButtons[4] = comm;
        allButtons[5] = lead;
        allButtons[6] = innovation;
        allButtons[7] = team;
        allButtons[8] = total;
        
        for(int i = 0; i < allButtons.length; i ++){
        	
        	allButtons[i].setOnClickListener(this);
        }
        
        return rootView;
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		int id = v.getId();
		for(int i = 0; i < allButtons.length; i ++){
			
			if(id == allButtons[i].getId()){
				
				Intent intent = new Intent(getActivity(), Leaderboards.class);
				intent.putExtra("Key", traitScores[i]);
				startActivity(intent);
				break;
			}
		}
	}
}
