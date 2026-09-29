# Firebase Firestore সেটআপ ও পরীক্ষা

এই Android অ্যাপ Firestore-এর বিদ্যমান ওয়েব-সামঞ্জস্যপূর্ণ `imran_store/backup` ডকুমেন্ট ব্যবহার করে। UI প্রথমে Room লোকাল ক্যাশ পড়ে; নেটওয়ার্ক থাকলে WorkManager পরিবর্তন sync করে। Firestore snapshot listener দূরের পরিবর্তন শনাক্ত করে sync worker চালায়। Android Firestore SDK-তে offline persistence ডিফল্টভাবে চালু; Room cache-ও offline-first আচরণ ধরে রাখে।

## Firebase configuration যুক্ত করা হয়েছে

আপনার দেওয়া `google-services.json` ফাইলটি `app/google-services.json`-এ যোগ করা হয়েছে এবং project ID, Android package ও Android App ID যাচাই করা হয়েছে। App module-এ Google Services Gradle plugin সক্রিয়। Firebase Console থেকে JSON আবার ডাউনলোড করে বসাতে হবে না।

Gradle-এর `applicationId` Firebase-এ দেওয়া Android package name `com.clothingstore.myandroidapp`-এর সঙ্গে মেলানো হয়েছে। JSON-এও একই package থাকতে হবে। যাচাই করুন:

```bash
python3 - <<'PY'
import json
p = json.load(open('app/google-services.json', encoding='utf-8'))
print('project_id:', p['project_info']['project_id'])
for client in p['client']:
    print('package_name:', client['client_info']['android_client_info']['package_name'])
    print('mobilesdk_app_id:', client['client_info']['mobilesdk_app_id'])
PY
```

প্রত্যাশিত মান: `project_id=dokane-aa207`, `package_name=com.clothingstore.myandroidapp`, Android App ID `1:394082302202:android:90286952d31ad6fbcff143`।

> **অ্যাপ পরিচয়ের পরিবর্তন:** আগের build যদি `com.imran.clothstore` application ID-তে install করা হয়ে থাকে, নতুন `com.clothingstore.myandroidapp` build সেটির update নয়—Android-এ আলাদা app হিসেবে install হবে। পুরোনো app-এর Room-এ থাকা local data লাগলে আগে backup/migration করে নিন। Source namespace `com.imran.clothstore` অপরিবর্তিত; namespace ও application ID আলাদা হতে পারে।

## Firebase Console setup সম্পন্ন

`dokane-aa207`-এর Android app configuration যাচাই করা হয়েছে, default Firestore database আগে থেকেই আছে (`asia-south1`), Anonymous Authentication provider enable করা হয়েছে, এবং repository-র `firestore.rules` policy প্রকাশ করা হয়েছে। বিদ্যমান `imran_store/backup` ডকুমেন্ট ও data মুছে বা overwrite করা হয়নি—নতুন database তৈরি করবেন না।

> **Rules-এর পরিসর:** published rule অনুযায়ী যেকোনো authenticated account—anonymous account-সহ—Firestore-এর সব document read/write করতে পারে; unauthenticated request প্রত্যাখ্যাত হয়। একই project-এর `hisab` web app sign-in না করলে তার Firestore access বন্ধ হতে পারে। নির্দিষ্ট user/store-এ সীমাবদ্ধ করতে user-scoped rules ও data model দরকার; বর্তমান shared backup schema বদলানো হয়নি।

Anonymous sign-in-এর জন্য SHA-1 প্রয়োজন নেই। ভবিষ্যতে Google Sign-In, Phone Auth বা অন্য certificate-sensitive provider যোগ করলে সেই provider-এর নির্দেশনা অনুযায়ী SHA-1/SHA-256 লাগতে পারে; এই পরিবর্তনে নতুন provider যোগ করা হয়নি।

## Build ও অ্যাপ পরীক্ষা

1. Project root থেকে build চালান:

   ```bash
   ./gradlew assembleDebug
   ```

2. Debug APK install করে internet চালু রেখে app খুলুন। Logcat-এ `ClothStoreApplication`-এর `signInAnonymously: success` দেখুন এবং `BackupSyncWorker`-এর error পরীক্ষা করুন।
3. App-এ একটি test entry যোগ বা পরিবর্তন করুন। Firebase Console → **Firestore Database → Data**-তে `imran_store` collection-এর `backup` document তৈরি/আপডেট হয়েছে কি না দেখুন। বিদ্যমান UI দিয়ে update ও delete-ও পরীক্ষা করুন।
4. একই Firebase project-এর web app বা দ্বিতীয় test device থেকে backup বদলান; Android app-এর Room/WorkManager sync-এ পরিবর্তনটি এসেছে কি না দেখুন। Background scheduling-এ কিছু সময় লাগতে পারে।
5. Internet বন্ধ করে test entry যোগ করুন; local change app-এ দেখা উচিত। Internet ফিরলে pending change sync হবে।
6. Rules Playground-এ unauthenticated read/write reject এবং signed-in read/write allow হয় কি না পরীক্ষা করুন।

## সাধারণ সমস্যা সমাধান

- **`google-services.json` missing / Google Services task failed:** ফাইলটি `app/google-services.json`-এ আছে, সঠিক নাম আছে, এবং app package মেলে কি না দেখুন।
- **`No matching client found for package name`:** JSON-এর `client[].client_info.android_client_info.package_name` অবশ্যই `com.clothingstore.myandroidapp` হতে হবে। Firebase Console থেকে সঠিক Android app-এর JSON আবার ডাউনলোড করুন।
- **`PERMISSION_DENIED`:** Firestore Rules publish হয়েছে, Anonymous provider enabled, এবং app sign-in সফল হয়েছে কি না যাচাই করুন।
- **Anonymous sign-in failure:** `dokane-aa207` project-এ Anonymous provider, internet connection ও app-এর Firebase project selection পরীক্ষা করুন।
- **Cloud document আপডেট হচ্ছে না:** internet, sign-in/logcat ও WorkManager constraints দেখুন। Offline পরিবর্তন Room-এ থাকে; network ফিরলে sync worker retry করবে।
- **পুরোনো install-এর data দেখা যাচ্ছে না:** application ID বদলেছে বলে নতুন app পুরোনো `com.imran.clothstore` installation-এর local Room data স্বয়ংক্রিয়ভাবে পাবে না।
