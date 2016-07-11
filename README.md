Android RxJava wrapper for startActivityForResult

## RxIntent 

#### Setup
Add to root build.gradle

```groovy
    allprojects {
        repositories {
            jcenter()
            mavenCentral()
        }
    }
```
In your module build.gradle file add to the dependencies block

```groovy
    dependencies {
        compile "com.twistedequations.rx:rx-intent:1.0.0"
    }
```
If its giving an error about not finding a jar file use `com.twistedequations.rx:rx-intent:1.0.0@aar` instead

## Usage

To listen for onActivityForResultEvents
```java
    Observable<RxIntentResult> observable = RxIntent.observeActivityForResult(activity, requestCode);
```

The Observable will emit one event when the result activity returns and then 
will finish with an onCompleteEvent. 

To start the activity for result use the `RxIntent.startActivityForResult(activity, intent, options, requestCode)` method.
this will start the activity and any `RxIntent.observeActivityForResult(activity, requestCode)` observables will emit the result of the callback as log as the 
request codes are the same.

Use `RxIntent.startAndObserveActivityForResult(activity, intent, options, requestCode)` method to start the activity 

To Handle activities being recreated while in the next activity is visible you can merge the results of `observeActivityForResult()` with a `startAndObserveActivityForResult()`
call into a single stream.

```java
    final Observable<RxIntentResult> rxIntentObservable = RxIntent.observeActivityForResult(RxIntentActivity.this, REQUEST_CODE);
    
    final Observable<RxIntentResult> result = RxView.clicks(view)
            .flatMap(new Func1<Void, Observable<RxIntentResult>>() {
                @Override
                public Observable<RxIntentResult> call(Void aVoid) {
                    return RxIntent.startAndObserveActivityForResult(RxIntentActivity.this, intent, REQUEST_CODE);
                }
            });

    final Observable<RxIntentResult> rxIntentResultObservable = Observable.merge(rxIntentObservable, result);
    //handle the events from the rxIntentResultObservable
```
