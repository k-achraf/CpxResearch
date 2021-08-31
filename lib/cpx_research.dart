
import 'dart:async';

import 'package:flutter/services.dart';

typedef void OnSurveyDidOpen();
typedef void OnSurveyDidClose();
typedef void OnSurveyUpdated();

class CpxResearch {

  static OnSurveyDidOpen? _onSurveyDidOpen;
  static OnSurveyDidClose? _onSurveyDidClose;
  static OnSurveyUpdated? _onSurveyUpdated;

  static CpxResearch get instance => _instance;
  final MethodChannel _channel;

  static final CpxResearch _instance = CpxResearch.private(
    const MethodChannel('cpx_research'),
  );

  CpxResearch.private(MethodChannel channel) : _channel = channel {
    _channel.setMethodCallHandler(_platformCallHandler);
  }

  Future<void> init({required appId, required secureHash, required userId, Position position = Position.SideRightSmall, CpxStyle? style}){
    if(style == null){
      style = CpxStyle();
    }
    return _channel.invokeMethod("init", <String, dynamic>{
      "appId" : appId,
      "secureHash" : secureHash,
      "userId" : userId,
      "position" : position.index,
      "text" : style.text,
      "textColor" : style.textColor,
      "textSize" : style.textSize,
      "bgColor" : style.bgColor,
      "roundedCorners" : style.roundedCorners
    });
  }

  Future<void> show() async{
    return _channel.invokeMethod("show");
  }
  
  Future<void> open(){
    return _channel.invokeMethod("open");
  }
  
  Future<void> hide(){
    return _channel.invokeMethod("hide");
  }
  
  Future<void> openSurvey({required String surveyId}){
    return _channel.invokeMethod("specific", <String, String>{
      "surveyId" : surveyId
    });
  }

  Future<void> setLogMode(bool log){
    return _channel.invokeMethod("log", <String, bool>{
      "log" : log
    });
  }

  Future _platformCallHandler(MethodCall call) async {
    if(call.method == "surveyDidOpen"){
      _onSurveyDidOpen?.call();
    }
    if(call.method == "surveyDidClose"){
      _onSurveyDidClose?.call();
    }
    if(call.method == "surveyUpdated"){
      _onSurveyUpdated?.call();
    }
  }

  void setSurveyDidOpen(OnSurveyDidOpen surveyDidOpen) => _onSurveyDidOpen = surveyDidOpen;
  void setSurveyDidClose(OnSurveyDidClose surveyDidClose) => _onSurveyDidClose = surveyDidClose;
  void setSurveyUpdated(OnSurveyUpdated surveyUpdated) => _onSurveyUpdated = surveyUpdated;

}

class CpxStyle {
  final String text;
  final int textSize;
  final String textColor;
  final String bgColor;
  final bool roundedCorners;

  CpxStyle({this.text = "Cpx Research", this.textSize = 20, this.textColor = "#ffffff", this.bgColor = "#ffaf20", this.roundedCorners = true});
}

enum Position{
  SideLeftNormal,
  SideLeftSmall,
  SideRightNormal,
  SideRightSmall,
  CornerTopLeft,
  CornerTopRight,
  CornerBottomRight,
  CornerBottomLeft,
  ScreenCenterTop,
  ScreenCenterBottom
}
