import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:cpx_research/cpx_research.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();

    //Define your style
    CpxStyle cpxStyle = CpxStyle(
      text: "Complete survey",
      textColor: "#ffffff",
      textSize: 15,
      roundedCorners: true,
      bgColor: "#26547C"
    );

    //init CPXResearch
    CpxResearch.instance.init(
      appId: "<Your app id>",
      secureHash: "<Your secure hash>",
      userId: "<Your external user id>",
      position: Position.CornerBottomLeft, //you can change it using Position.
      style: cpxStyle
    );

    //If you want to set log mode
    CpxResearch.instance.setLogMode(true);

    //Show Cpx Research button
    CpxResearch.instance.show();

    //When open survey
    void surveyDidOpen(){
      print("Open");
    }

    //When close survey
    void surveyDidClose(){
      print("Close");
    }

    //When surveys updated
    void surveyUpdated(){
      print("Updated");
    }

    //Define listeners
    CpxResearch.instance.setSurveyDidOpen(surveyDidOpen);
    CpxResearch.instance.setSurveyDidClose(surveyDidClose);
    CpxResearch.instance.setSurveyUpdated(surveyUpdated);
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('CPX Research'),
        ),
        body: Center(
          child: ElevatedButton(
            onPressed: (){
              CpxResearch.instance.open();
            },
            child: Text("Open survey wall"),
          ),
        ),
      ),
    );
  }
}
