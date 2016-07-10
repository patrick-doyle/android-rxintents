Android RxJava wrapper for startActivityForResult

RxIntent 

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


Usage
```java
    Observable<RxIntentResult> observable = RxIntent.forResult(activity, intent);
```

The Observable will emit one event when the result activity returns and then 
will finish with an onCompleteEvent. 

Rotation is not handled as the RxChain will be destroyed