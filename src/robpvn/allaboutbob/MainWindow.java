package robpvn.allaboutbob;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.time.DurationFormatUtils;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainWindow extends Activity implements OnClickListener {
	
	private String[] factList;
	private TextView factBox;
	private Random numGen;
	private int currentFact;
	private Timer countdownTimer;
	
	private Button nextFactoidButton;
	
	private Date eventDate;
	private TextView timerText;
	
    /** Called when the activity is first created, sets up everything. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        numGen = new Random();
        factList = getResources().getStringArray(R.array.fun_facts);
        factBox = (TextView) findViewById(R.id.funfactTextView);
        displayRandomFact();
        
        nextFactoidButton = (Button) findViewById(R.id.nextFactoidButton);
        nextFactoidButton.setOnClickListener(this);
        
        //It may be deprecated, but I don't see equivalent functionality in the replacement.
        eventDate = new Date(getResources().getString(R.string.countdown_date));
        timerText = (TextView) findViewById(R.id.timerTextView);
        this.runOnUiThread(upDateCountdown);
        //Setting up the timer to update once a minute
        GregorianCalendar when = new GregorianCalendar();
        when.set(GregorianCalendar.MINUTE, when.get(GregorianCalendar.MINUTE) + 1);
        when.set(GregorianCalendar.SECOND, 0);
        countdownTimer = new Timer();
        countdownTimer.schedule(new TimerTask() {
        	@Override
        	public void run() {
        		onTick();
        	}

        }, when.getTime(), 60000);
        
    }
    
    /** Called when the device is rotated to reinitialize the UI 
     * (Background data and timers are still running from onCreate)*/
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
      setContentView(R.layout.main);
      
      factBox = (TextView) findViewById(R.id.funfactTextView);
      factBox.setText(factList[currentFact]);
      
      nextFactoidButton = (Button) findViewById(R.id.nextFactoidButton);
      nextFactoidButton.setOnClickListener(this);
      
      timerText = (TextView) findViewById(R.id.timerTextView);
      this.runOnUiThread(upDateCountdown);
      
    }
        
    /**
     * Shows a new random fact in the view box.
     * (Actually pseudorandom because it will not display the same fact twice.)
     */
	private void displayRandomFact() {

		int random = numGen.nextInt(factList.length);
		if (random != currentFact) {
			factBox.setText(factList[random]);
			currentFact = random;
		} else {
			displayRandomFact();
		}
	}

    /**
     * Updates the countdown/up timer.
     */
	private Runnable upDateCountdown = new Runnable() {
		public void run() {	
			Date now = new Date();

			//String format = getResources().getString(R.string.countdown_format);
			String format = "y \'years,\' M \'months,\' d \'days, \'H \'hours and \' m \'minutes\'";
			//TODO: Needs to be made a string resource.

			if (now.before(eventDate)) {	//In the future
				String elapsed = DurationFormatUtils.formatPeriod(now.getTime(), eventDate.getTime(), 
				format);
				
				timerText.setText(getResources().getString(R.string.countdown_prepend)
						+ " " + elapsed + " " 
						+ getResources().getString(R.string.countdown_beforetext));
			} else {						//In the past
				String elapsed = DurationFormatUtils.formatPeriod(eventDate.getTime(), now.getTime(), 
						format);
				
				timerText.setText(getResources().getString(R.string.countdown_prepend)
						+ " " + elapsed + " " 
						+ getResources().getString(R.string.countdown_aftertext));
			}


		}
	};
	
	/** Reacts to the user requesting a new fact.*/
    public void onClick(View clicked) {
    	
    	if (clicked == nextFactoidButton) {
    		displayRandomFact();
		} 
    	
    }
    
    /** Handles ticks from the timer once a minute. */
    protected void onTick()
	{
		this.runOnUiThread(upDateCountdown);
	}
	
}