package mobi.prizer.cpx_research;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;

import com.makeopinion.cpxresearchlib.CPXResearch;
import com.makeopinion.cpxresearchlib.models.CPXConfiguration;
import com.makeopinion.cpxresearchlib.models.CPXConfigurationBuilder;
import com.makeopinion.cpxresearchlib.models.CPXStyleConfiguration;
import com.makeopinion.cpxresearchlib.models.SurveyPosition;

public class CPXApplication extends Application {
    private CPXResearch cpxResearch;

    @Override
    public void onCreate() {
        super.onCreate();
        initCPX();
    }

    @NonNull
    public CPXResearch getCpxResearch() {
        return cpxResearch;
    }

    private void initCPX() {
        CPXStyleConfiguration style = new CPXStyleConfiguration(SurveyPosition.SideRightNormal,
                "Earn up to 3 Coins in<br> 4 minutes with surveys",
                20,
                "#ffffff",
                "#ffaf20",
                true);

        CPXConfiguration config = new CPXConfigurationBuilder("<Your app id>",
                "<Your external user id>",
                "<Your secure hash>",
                style)
                .build();

        cpxResearch = CPXResearch.Companion.init(this, config);
    }
}
