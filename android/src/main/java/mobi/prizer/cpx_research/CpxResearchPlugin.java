package mobi.prizer.cpx_research;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;

import com.makeopinion.cpxresearchlib.CPXResearch;
import com.makeopinion.cpxresearchlib.CPXResearchListener;
import com.makeopinion.cpxresearchlib.misc.CPXJsonValidator;
import com.makeopinion.cpxresearchlib.models.CPXConfiguration;
import com.makeopinion.cpxresearchlib.models.CPXConfigurationBuilder;
import com.makeopinion.cpxresearchlib.models.CPXStyleConfiguration;
import com.makeopinion.cpxresearchlib.models.SurveyItem;
import com.makeopinion.cpxresearchlib.models.SurveyPosition;
import com.makeopinion.cpxresearchlib.models.TransactionItem;
import com.makeopinion.cpxresearchlib.views.CPXBannerViewHandler;
import com.makeopinion.cpxresearchlib.views.CPXWebViewActivity;

import java.util.List;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** CpxResearchPlugin */
  public class CpxResearchPlugin implements FlutterPlugin, ActivityAware, MethodCallHandler {

  private MethodChannel channel;
  private FlutterPluginBinding binding = null;
  private Activity activity = null;
  private CPXResearch app;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
    channel = new MethodChannel(binding.getBinaryMessenger(), "cpx_research");
    channel.setMethodCallHandler(this);
    this.binding = binding;
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
    this.binding = null;
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

  }

  @Override
  public void onDetachedFromActivity() {
    activity = null;
  }

  private void initCpxResearch(
          final Activity activity,
          final String appId,
          final String secureHash,
          final String userId,
          final int position,
          final CPXStyleConfiguration style
  ){

    if(app != null){
      app.setSurveyVisibleIfAvailable(false, activity);
      app = null;
    }

    CPXConfiguration config = new CPXConfigurationBuilder(
            appId,
            userId,
            secureHash,
            style
    ).build();

    app = CPXResearch.Companion.init(activity, config);
    app.setLogMode(true);

    app.registerListener(new CPXResearchListener() {
      @Override
      public void onSurveysUpdated() {
        channel.invokeMethod("surveyUpdated", null);
      }

      @Override
      public void onTransactionsUpdated(List<TransactionItem> list) {
        System.out.println("Tr updated");
      }

      @Override
      public void onSurveysDidOpen() {
        channel.invokeMethod("surveyDidOpen", null);
      }

      @Override
      public void onSurveysDidClose() {
        channel.invokeMethod("surveyDidClose", null);
      }
    });
  }

  private void extractParams(Activity activity, MethodCall call, Result result){
    String appId = null;
    String secureHash = null;
    String userId = null;
    int position = 0;
    String text = "CPX Research";
    String textColor = "#ffffff";
    String bgColor = "#ffaf20";
    int textSize = 20;
    boolean roundedCorners = true;

    if(call.argument("appId") != null){
      appId = call.argument("appId");
    }
    if(call.argument("secureHash") != null){
      secureHash = call.argument("secureHash");
    }
    if(call.argument("userId") != null){
      userId = call.argument("userId");
    }
    if(call.argument("position") != null){
      position = call.argument("position");
    }
    if(call.argument("text") != null){
      text = call.argument("text");
    }
    if(call.argument("textSize") != null){
      textSize = call.argument("textSize");
    }
    if(call.argument("textColor") != null){
      textColor = call.argument("textColor");
    }
    if(call.argument("bgColor") != null){
      bgColor = call.argument("bgColor");
    }
    if(call.argument("roundedCorners") != null){
      roundedCorners = call.argument("roundedCorners");
    }

    if(appId == null){
      result.error("no_app_id", "no app id provided", null);
      return;
    }
    if(secureHash == null){
      result.error("no_secure_hash", "no secure hash provided", null);
      return;
    }
    if(userId == null){
      result.error("no_user_id", "no user id provided", null);
      return;
    }

    CPXStyleConfiguration style = new CPXStyleConfiguration(
            SurveyPosition.values()[position],
            text,
            textSize,
            textColor,
            bgColor,
            roundedCorners
    );

    if(binding != null){
      initCpxResearch(
              activity,
              appId,
              secureHash,
              userId,
              position,
              style
      );
    }
  }

  private void showSpecificSurvey(Activity activity, MethodCall call, Result result){
    String surveyId = null;

    if(call.argument("surveyId") != null){
      surveyId = call.argument("surveyId");
    }

    if(surveyId == null){
      result.error("no_survey_id", "no survey id", null);
      return;
    }

    app.openSurvey(activity, surveyId);
  }
  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if(call.method.equals("init")){
      if(activity != null){
        extractParams(activity, call, result);
      }
    }
    if(call.method.equals("show")){
      app.setSurveyVisibleIfAvailable(true, activity);
    }
    if(call.method.equals("specific")){
      showSpecificSurvey(activity, call, result);
    }
    if(call.method.equalsIgnoreCase("hide")){
      app.setSurveyVisibleIfAvailable(false, activity);
    }
    if(call.method.equals("open")){
      app.openSurveyList(activity);
    }
    if(call.method.equals("log")){
      boolean log = call.argument("log");
      app.setLogMode(log);
    }
  }
}
