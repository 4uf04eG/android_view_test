import 'package:flutter/foundation.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  final String channel = 'com.startActivity/testChannel';

  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: 2,
      child: Scaffold(
        appBar: AppBar(
          title: Text('Native view test'),
          bottom: TabBar(
            tabs: [Tab(text: 'Virtual display'), Tab(text: 'Hybrid')],
          ),
          actions: [
            Padding(
              padding: const EdgeInsets.only(right: 8.0),
              child: TextButton(
                onPressed: () {
                  MethodChannel(channel).invokeMethod('StartSecondActivity');
                },
                child: Text('Launch Android', style: TextStyle(color: Colors.white)),
              ),
            ),
          ],
        ),
        body: TabBarView(
          children: [
            _AndroidViewPage(),
            _HybridViewWidget(),
          ],
        ),
      ),
    );
  }
}

class _AndroidViewPage extends StatelessWidget {
  const _AndroidViewPage({Key key}) : super(key: key);

  final String nativeView = 'native-view';

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: ListView.builder(
        itemCount: 100,
        itemBuilder: (context, _) {
          return SizedBox(height: 100, child: AndroidView(viewType: nativeView));
        },
      ),
    );
  }
}

class _HybridViewWidget extends StatelessWidget {
  _HybridViewWidget({Key key}) : super(key: key);

  final String viewType = 'native-view';

  final Map<String, dynamic> creationParams = <String, dynamic>{};

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: ListView.builder(
          itemCount: 100,
          itemBuilder: (context, _) {
            return SizedBox(
              height: 100,
              child: PlatformViewLink(
                viewType: viewType,
                surfaceFactory: (BuildContext context, PlatformViewController controller) {
                  return AndroidViewSurface(
                    controller: controller,
                    gestureRecognizers: const <Factory<OneSequenceGestureRecognizer>>{},
                    hitTestBehavior: PlatformViewHitTestBehavior.opaque,
                  );
                },
                onCreatePlatformView: (PlatformViewCreationParams params) {
                  return PlatformViewsService.initSurfaceAndroidView(
                    id: params.id,
                    viewType: viewType,
                    layoutDirection: TextDirection.ltr,
                    creationParams: creationParams,
                    creationParamsCodec: StandardMessageCodec(),
                  )
                    ..addOnPlatformViewCreatedListener(params.onPlatformViewCreated)
                    ..create();
                },
              ),
            );
          }),
    );
  }
}
