package cn.fython.weathercard;

import com.fima.cardsui.views.CardUI;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	private static CardUI mCardUI; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mCardUI = (CardUI) findViewById(R.id.cardUI1);
	}

}
